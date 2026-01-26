package com.jbass.orbital.application

import com.jbass.orbital.plugins.configureMonitoring
import com.jbass.orbital.plugins.configureRouting
import com.jbass.orbital.plugins.configureSerialization
import com.jbass.orbital.plugins.configureSockets
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