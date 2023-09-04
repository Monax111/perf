package ru.tim.conference.perf.container

import mu.two.KLogging
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer

class PrometheusContainer : GenericContainer<PrometheusContainer>(
    "prom/prometheus"
) {
    companion object : KLogging()

    init {
        withNetwork(perfNetwork)
        withNetworkAliases("prometheus")
        withExposedPorts(9090)
        withClasspathResourceMapping(
            "prometheus.yml",
            "/etc/prometheus/prometheus.yml",
            BindMode.READ_ONLY
        )
    }

    fun startContainer(): PrometheusContainer {
        try {

            super.start()
        } catch (e: Exception) {
            println(e)
        }
        //followOutput(Slf4jLogConsumer(logger))
        return this
    }
}