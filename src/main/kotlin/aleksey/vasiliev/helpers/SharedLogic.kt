package aleksey.vasiliev.helpers

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Глобальные константы и разделяемая логика.
 */

object SharedLogic {
    val protocol: String
    val ip: String
    val port: Int
    val type2Value = mutableListOf<Pair<String, Byte>>()

    init {
        val config = File("config.json")
        val configContent = config.readText()
        val jsonObject = JSONObject(configContent)
        ip = jsonObject.getString("ip")
        port = jsonObject.getInt("port")
        protocol = jsonObject.getString("protocol")
        val properties = File("properties.json")
        val propertiesContent = properties.readText()
        val jsonArray = JSONArray(propertiesContent)
        var currentIndex = 0
        while (jsonArray.length() != currentIndex) {
            val jsonObjectProperties = jsonArray.getJSONObject(currentIndex)
            val type = jsonObjectProperties.getString("type")
            val value = jsonObjectProperties.getInt("value").toByte()
            type2Value.add(type to value)
            currentIndex++
        }
    }
}