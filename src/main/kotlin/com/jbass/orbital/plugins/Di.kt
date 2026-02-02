package com.jbass.orbital.plugins

import com.jbass.orbital.di.repositoryModule
import com.jbass.orbital.di.simulationEngineModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            repositoryModule,
            simulationEngineModule,
        )
        createEagerInstances()
    }
}
