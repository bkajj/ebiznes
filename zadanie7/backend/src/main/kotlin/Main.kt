import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
)

@Serializable
data class Customer(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
)

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int,
)

@Serializable
data class PaymentRequest(
    val customer: Customer,
    val cart: List<CartItem>,
    val total: String,
)

val products =
    listOf(
        Product(1, "Wino Czerwone", 49.99),
        Product(2, "Wino Białe", 39.99),
        Product(3, "Wino Różowe", 44.99),
    )

const val CONTENT_TYPE = "Content-Type"
const val ALLOW_ORIGIN = "Access-Control-Allow-Origin"

fun handleResponseHeaders(
    response: String,
    exchange: HttpExchange,
) {
    exchange.responseHeaders.add(CONTENT_TYPE, "application/json")
    exchange.responseHeaders.add(ALLOW_ORIGIN, "*")
    exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
    exchange.responseBody.use {
        it.write(response.toByteArray())
    }
}

fun main() {
    val server = HttpServer.create(InetSocketAddress(8080), 0)

    server.createContext("/products") { exchange ->
        if (exchange.requestMethod == "GET") {
            val response = Json.encodeToString(products)
            handleResponseHeaders(response, exchange)
        } else {
            exchange.sendResponseHeaders(405, -1)
        }
    }

    server.createContext("/payment") { exchange ->
        when (exchange.requestMethod) {
            "OPTIONS" -> {
                exchange.responseHeaders.add(ALLOW_ORIGIN, "*")
                exchange.responseHeaders.add("Access-Control-Allow-Methods", "POST, OPTIONS")
                exchange.responseHeaders.add("Access-Control-Allow-Headers", CONTENT_TYPE)
                exchange.sendResponseHeaders(204, -1) // No Content
            }

            "POST" -> {
                val body = exchange.requestBody.bufferedReader().readText()

                try {
                    val paymentRequest = Json.decodeFromString<PaymentRequest>(body)

                    println("Dane klienta: ${paymentRequest.customer.firstName} ${paymentRequest.customer.lastName}")
                    println("Koszyk: ${paymentRequest.cart.map { it.product.name }}")
                    println("Łączna kwota: ${paymentRequest.total}")

                    val response = Json.encodeToString(mapOf("status" to "sukces"))
                    handleResponseHeaders(response, exchange)
                } catch (e: Exception) {
                    println("Błąd podczas deserializacji: ${e.message}")
                    exchange.sendResponseHeaders(400, -1)
                    return@createContext
                }
            }

            else -> {
                exchange.sendResponseHeaders(405, -1)
            }
        }
    }

    println("Serwer działa na http://localhost:8080")
    server.start()
}
