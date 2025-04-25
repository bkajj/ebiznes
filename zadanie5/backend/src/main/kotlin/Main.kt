import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.InetSocketAddress

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Double
)

@Serializable
data class Customer(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
)

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int
)

@Serializable
data class PaymentRequest(
    val customer: Customer,
    val cart: List<CartItem>,
    val total: String
)

val products = listOf(
    Product(1, "Wino Czerwone", 49.99),
    Product(2, "Wino Białe", 39.99),
    Product(3, "Wino Różowe", 44.99)
)

fun main() {
    val server = HttpServer.create(InetSocketAddress(8080), 0)

    server.createContext("/products") { exchange ->
        if (exchange.requestMethod == "GET") {
            val response = Json.encodeToString(products)
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use {
                it.write(response.toByteArray())
            }
        } else {
            exchange.sendResponseHeaders(405, -1)
        }
    }

    server.createContext("/payment") { exchange ->
        when (exchange.requestMethod) {
            "OPTIONS" -> {
                exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
                exchange.responseHeaders.add("Access-Control-Allow-Methods", "POST, OPTIONS")
                exchange.responseHeaders.add("Access-Control-Allow-Headers", "Content-Type")
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
                    exchange.responseHeaders.add("Content-Type", "application/json")
                    exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
                    exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
                    exchange.responseBody.use {
                        it.write(response.toByteArray())
                    }
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
