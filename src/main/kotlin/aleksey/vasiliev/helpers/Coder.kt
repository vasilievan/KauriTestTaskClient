package aleksey.vasiliev.helpers

import aleksey.vasiliev.helpers.SharedLogic.type2Value
import java.io.*
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.pow
import java.io.Serializable

/** Класс для (де)кодирования данных для передачи по Сети.
 */

class Coder {
    private val nullValue: Byte = 5
    private val objectIdentifierValue: Byte = 6
    private val maximumLength = 2.0.pow(1008.0)

    /** Data-класс, передаваемый через TCP
     */

    data class TLVInstance(val identifier: Byte, val length: List<Byte>, val value: Serializable?)

    /** Функция для получения размера сериализуемого объекта в байтах.
     * К сожалению, в JVM нет прямого аналога sizeOf из C,
     * поэтому объект сериализуется, загружается в ObjectOutputStream,
     * который базируется на ByteArrayOutputStream (BAOS). Далее BAOS кастуется к массиву байт,
     * размер полученного массива - искомое число байт при передачи по сети.
     */

    private fun sizeOf(obj: Any?): Int {
        if (obj !is Serializable) {
            throw IllegalArgumentException("Object must implement Serializable interface.")
        }
        val byteOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteOutputStream)
        with(objectOutputStream) {
            writeObject(obj)
            flush()
            close()
        }
        val size = byteOutputStream.toByteArray().size
        byteOutputStream.close()
        return size
    }

    /** Функция для кодирования идентификатора из тэга.
     */

    private fun encodeDataIdentifier(data: Serializable?): Byte {
        if (data == null) {
            return nullValue
        }
        val type = data.javaClass.name
        return type2Value.firstOrNull { it.first == type }?.second ?: return objectIdentifierValue
    }

    /** Функция для кодирования длины сообщения согласно стандрату.
     */

    private fun encodeDataLength(data: Serializable?): List<Byte> {
        var length = sizeOf(data)
        if (length < 128) {
            return listOf(length.toByte())
        }
        if (length < maximumLength) {
            val result = mutableListOf<Byte>()
            var bytesAmount = ceil(log(length.toDouble(), 256.0)).toInt()
            val firstByte = (bytesAmount + 128).toByte()
            result.add(firstByte)
            while (bytesAmount > 0) {
                val mod = ceil(256.0.pow(bytesAmount - 1)).toInt()
                val nextByte = length / mod
                length -= mod * nextByte
                result.add(nextByte.toByte())
                bytesAmount--
            }
            return result
        }
        throw IllegalArgumentException("Data is too long.")
    }

    /** Функция для кодирования сообщения после ввода пользователем.
     */

    fun encode(data: String?, type: String?): TLVInstance {
        if (data == null) {
            return TLVInstance(encodeDataIdentifier(null), encodeDataLength(data), null)
        }
        when (type) {
            "boolean" -> return TLVInstance(encodeDataIdentifier(data.toBoolean()), encodeDataLength(data), data.toBoolean())
            "int" -> return TLVInstance(encodeDataIdentifier(data.toInt()), encodeDataLength(data), data.toInt())
            "double" -> return TLVInstance(encodeDataIdentifier(data.toDouble()), encodeDataLength(data), data.toDouble())
            "float" -> return TLVInstance(encodeDataIdentifier(data.toFloat()), encodeDataLength(data), data.toFloat())
        }
        return TLVInstance(encodeDataIdentifier(data), encodeDataLength(data), data)
    }
}