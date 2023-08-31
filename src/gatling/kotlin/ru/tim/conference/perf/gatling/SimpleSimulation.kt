package ru.tim.conference.perf.gatling

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.atOnceUsers
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import ru.tim.conference.perf.container.Config
import ru.tim.conference.perf.container.JvmContainer
import ru.tim.conference.perf.container.Stand


/**
 * This sample is based on our official tutorials:
 *
 * - [Gatling quickstart tutorial](https://gatling.io/docs/gatling/tutorials/quickstart)
 * - [Gatling advanced tutorial](https://gatling.io/docs/gatling/tutorials/advanced)
 */
class SimpleSimulation : Simulation() {

    // Когда симуляция будет создаваться, для нее создаться контейнер с приложением
    val stand = Stand()

    // Объясняем куда стучаться к нашему приложению
    val scenario: ScenarioBuilder = CoreDsl.scenario("Perf")
        .exec(HttpDsl.http("score").get(stand.application.getUrl()+"/score/123"))

    init {
        // Конфигурируем нагрузку
        setUp(
            scenario.injectOpen(
                atOnceUsers(10),
                rampUsersPerSec(10.0).to(300.0).during(10),
            )
        )

    }
}