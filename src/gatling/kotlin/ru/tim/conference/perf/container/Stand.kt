package ru.tim.conference.perf.container

import org.testcontainers.containers.PostgreSQLContainer

class Stand {

    val postgresAliases = "postgres"
    val postgres = PostgreSQLContainer()
        .withNetwork(perfNetwork)
        .withNetworkAliases(postgresAliases)
        .withMilliCpuLimit(500)
        .apply { start() }

    // Когда симуляция будет создаваться, для нее создаться контейнер с приложением
    val application = JvmContainer(Config("application"), "ru.tim.conference/perf:1.0.0")
        .withEnv("SPRING.DATASOURCE.URL", "jdbc:postgresql://$postgresAliases:5432/test")
        .startContainer()

}
