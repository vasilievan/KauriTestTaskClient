package aleksey.vasiliev.client

import aleksey.vasiliev.helpers.Coder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.ConnectException

/**
 * Тестовое приложение для Kauri. Представляет собой клиент-серверное приложение,
 * далее - клиентская часть. Попытка реализации X.690 на Kotlin с учетом языковых
 * абстракций. Так например, разные типы данных при передачи указываются пользователем
 * (при вводе вручную, как в данной реализации). При программной передаче тип выводится.
 * Можно передавать сообщения на сервер, соединение будет разорвано по команде пользователя.
 * @author <a href="mailto:enthusiastic.programmer@yandex.ru">Алексей Васильев</a>
 * @version 1.0
 */


fun main() = runBlocking {
    val client = Client()
    val coder = Coder()
    println("Client started.")
    while (true) {
        println("Print message to server.")
        val inputLine = readLine()
        println("Print message type.")
        val type = readLine()
        val encoded = coder.encode(inputLine, type)
        val job = launch(Dispatchers.Default) {
            try {
                client.postRequest(encoded)
            } catch (e: ConnectException) {
                println("Something went wrong :(")
            }
        }
        job.join()
        println("Continue? Y/N")
        val response = readLine()
        if (response == "N") {
            client.closeConnection()
            break
        }
    }
    println("Client stopped.")
}


