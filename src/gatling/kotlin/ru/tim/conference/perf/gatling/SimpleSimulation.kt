package ru.tim.conference.perf.gatling

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl
import java.util.UUID
import ru.tim.conference.perf.Stand


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
        .exec(HttpDsl.http("score").get {
            stand.application.getUrl() + "/score/" + UUID.randomUUID()
        })

    init {
        // Конфигурируем нагрузку
        setUp(
            scenario.injectOpen(
                constantUsersPerSec(50.0).during(10),
                rampUsersPerSec(50.0).to(300.0).during(10),
                constantUsersPerSec(200.0).during(10),
                constantUsersPerSec(50.0).during(30),
            )
        )

    }
}