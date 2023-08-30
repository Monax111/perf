package ru.tim.conference.perf.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    // Должен быть post, но что бы можно было дергать из браузера сделал get
    @GetMapping("score/{clientId}")
    fun score(@PathVariable clientId: String) = clientId
}