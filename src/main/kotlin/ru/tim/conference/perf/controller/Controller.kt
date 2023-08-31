package ru.tim.conference.perf.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.tim.conference.perf.service.ScoreService

@RestController
class Controller(
    val scoreService: ScoreService
) {

    // Должен быть post, но что бы можно было дергать из браузера сделал get
    @GetMapping("score/{clientId}")
    fun score(@PathVariable clientId: String): String {
        return scoreService.score(clientId)
    }
}