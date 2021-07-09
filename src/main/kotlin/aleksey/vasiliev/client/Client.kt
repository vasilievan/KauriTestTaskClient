package aleksey.vasiliev.client

import aleksey.vasiliev.helpers.Coder
import aleksey.vasiliev.helpers.SharedLogic.ip
import aleksey.vasiliev.helpers.SharedLogic.port
import aleksey.vasiliev.helpers.SharedLogic.protocol
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*

class Client {
    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    suspend fun postRequest(TLVInstance: Coder.TLVInstance) {
        client.post<Unit>("$protocol$ip:$port") {
            contentType(ContentType.Application.Json)
            body = TLVInstance
        }
    }

    fun closeConnection() {
        client.close()
    }
}