package com.jbass.orbital.di

import com.jbass.orbital.data.simulation.SimulationEngine
import org.koin.dsl.module

val simulationEngineModule = module {
    single { SimulationEngine(get(),get()) }

}