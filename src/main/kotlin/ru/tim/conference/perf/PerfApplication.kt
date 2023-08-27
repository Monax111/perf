package ru.tim.conference.perf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PerfApplication

fun main(args: Array<String>) {

	runApplication<PerfApplication>(*args)
}
