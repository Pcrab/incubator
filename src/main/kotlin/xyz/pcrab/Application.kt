package xyz.pcrab

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import xyz.pcrab.models.RedisStorage
import xyz.pcrab.models.UserSession
import xyz.pcrab.plugins.*
import xyz.pcrab.routes.badRequest

fun main() {

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            host("127.0.0.1")
            host("127.0.0.1:8000")
            allowCredentials = true
            header(HttpHeaders.ContentType)
        }
        install(Sessions) {
            cookie<UserSession>("user_session", storage = RedisStorage()) {
                cookie.path = "/"
            }
        }
        install(Authentication) {
            session<UserSession>("auth-session") {
                validate { session ->
                    session
                }
                challenge {
                    badRequest(call, "cookie not found")
                }
            }
        }
        install(XForwardedHeaderSupport)
        configureRouting()
        configureMonitoring()

    }.start(wait = true)
}


/**
 * ssh-keygen -t rsa -b 4096 -m PKCS8 -f jwtRS256.key
 * openssl rsa -in jwtRS256.key -pubout -out jwtRS256.key.pub
 * then use online converter
 * https://8gwifi.org/jwkconvertfunctions.jsp
 */