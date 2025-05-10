import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*

import routes.authRoutes
import routes.productRoutes

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Options)
        }
        routing {
            authRoutes()
            productRoutes()
        }
    }.start(wait = true)
}

fun Application.module() {
    configureJWT()
    routing {
        authenticate("auth-jwt") {
            get("/protected") {
                call.respondText("Dostęp do chronionej trasy przyznany!")
            }
        }
    }
}

fun Application.configureJWT() {
    val jwtIssuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "WineShop"
    val jwtAudience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "WineShopAudience"
    val jwtSecret = environment.config.propertyOrNull("jwt.secret")?.getString() ?: "supersecretkey"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor-sample-app"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString()
                        .isNotEmpty()
                ) JWTPrincipal(credential.payload) else null
            }
        }
    }
}