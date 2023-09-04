package ru.tim.conference.perf

import ru.tim.conference.perf.container.GrafanaContainer
import ru.tim.conference.perf.container.PrometheusContainer

fun main() {
    PrometheusContainer().startContainer()
    GrafanaContainer().apply {
        startContainer()
        val datasourceId = addPrometheusDataSource()
        addDashboard(datasourceId)
    }

    Thread.sleep(1000 * 60 * 60 * 3) //3 часа
}