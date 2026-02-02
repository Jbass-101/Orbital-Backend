package com.jbass.orbital.application

import com.jbass.orbital.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureDiscovery()
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}