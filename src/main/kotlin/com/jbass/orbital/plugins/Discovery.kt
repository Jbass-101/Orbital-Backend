package com.jbass.orbital.plugins

import io.ktor.server.application.*
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

fun Application.configureDiscovery() {
    // Run in a background thread so we don't block server startup
    val thread = Thread {
        try {
            val jmdns = JmDNS.create(InetAddress.getLocalHost())

            // Define the service: Type, Name, Port, Description
            val serviceInfo = ServiceInfo.create(
                "_orbital._tcp.local.",
                "OrbitalSmartHome",
                58080,
                "Orbital Backend Service"
            )

            jmdns.registerService(serviceInfo)
            log.info("mDNS: Service registered as _orbital._tcp.local. on port 58080")

            // Unregister on shutdown
            Runtime.getRuntime().addShutdownHook(Thread {
                jmdns.unregisterAllServices()
            })
        } catch (e: Exception) {
            log.error("mDNS: Failed to register service", e)
        }
    }
    thread.start()
}