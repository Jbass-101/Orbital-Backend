package com.jbass.orbital.application

import com.jbass.orbital.data.simulation.SimulationEngine
import com.jbass.orbital.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    val engine = get<SimulationEngine>()
    engine.start()
    configureDiscovery()
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}