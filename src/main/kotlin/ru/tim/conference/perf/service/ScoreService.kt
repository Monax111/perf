package ru.tim.conference.perf.service

import org.springframework.stereotype.Component
import ru.tim.conference.perf.repository.ResultEntity
import ru.tim.conference.perf.repository.ResultRepository

@Component
class ScoreService(
    val repository: ResultRepository
) {

    fun score(clientId: String): String {
        val result = "Ok"
        val resultEntity = ResultEntity(
            clientId = clientId,
            result = result
        )
        repository.save(resultEntity)
        return result
    }
}