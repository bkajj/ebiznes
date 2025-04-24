package bot

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.dotenv

class DiscordBot {
    private val dotenv = dotenv()
    private val token = dotenv["DC_TOKEN"] ?: throw IllegalStateException("Nieznany token")

    suspend fun start() {
        val client = Kord(token)

        client.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on
            MessageHandler.handle(message)
        }

        client.login() {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
}
