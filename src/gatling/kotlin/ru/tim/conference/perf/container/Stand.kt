package ru.tim.conference.perf.container

import org.testcontainers.containers.PostgreSQLContainer

class Stand {

    val postgres = PostgreSQLContainer().apply { start() }

    // Когда симуляция будет создаваться, для нее создаться контейнер с приложением
    val application = JvmContainer(Config("application"), "ru.tim.conference/perf:1.0.0")
        .withEnv("SPRING.DATASOURCE.URL", postgres.jdbcUrl)
        .startContainer()

}
