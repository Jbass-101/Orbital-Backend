package com.jbass.orbital.plugins

import com.jbass.orbital.di.repositoryModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(
            repositoryModule
        )
        createEagerInstances()
    }
}
