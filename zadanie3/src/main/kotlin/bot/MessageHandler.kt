package bot

import dev.kord.core.entity.Message
import model.Category
import model.Product

object MessageHandler {

    private val categories = listOf(
        Category("Elektronika"),
        Category("Meble"),
        Category("Książki")
    )

    private val products = listOf(
        Product("Laptop Asus Vivobook 15", "Elektronika"),
        Product("IPhone 16 Max Pro", "Elektronika"),
        Product("Samsung Galaxy S24", "Elektronika"),
        Product("Telewizor Samsung 50 Cali OLED", "Elektronika"),
        Product("Sofa narożna", "Meble"),
        Product("Stół kuchenny", "Meble"),
        Product("Fotel biurowy", "Meble"),
        Product("Clean Code", "Książki"),
        Product("Pragmatyczny Programista", "Książki"),
        Product("English For IT", "Książki")
    )

    suspend fun handle(message: Message) {
        val content = message.content.lowercase()
        println("content: $content")

        when {
            content.startsWith("!kategorie") -> {
                val response = categories.joinToString("\n") { "- ${it.name}" }
                message.channel.createMessage("Dostępne kategorie:\n$response")
            }

            content.startsWith("!produkty") -> {
                val category = content.removePrefix("!produkty").trim()
                val filtered = products.filter { it.category.equals(category, ignoreCase = true) }

                val response = if (filtered.isEmpty()) {
                    "Brak produktów w kategorii: $category"
                } else {
                    filtered.joinToString("\n") { "- ${it.name}" }
                }

                message.channel.createMessage(response)
            }
        }
    }
}
