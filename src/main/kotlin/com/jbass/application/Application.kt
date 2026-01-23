package com.jbass.application

import com.jbass.plugins.configureMonitoring
import com.jbass.plugins.configureRouting
import com.jbass.plugins.configureSerialization
import com.jbass.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}