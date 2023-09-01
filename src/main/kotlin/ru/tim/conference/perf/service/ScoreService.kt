package ru.tim.conference.perf.service

import mu.two.KLogging
import org.springframework.stereotype.Component
import ru.tim.conference.perf.repository.ResultEntity
import ru.tim.conference.perf.repository.ResultRepository

@Component
class ScoreService(
    val repository: ResultRepository
) {

    companion object : KLogging()

    fun score(clientId: String): String {
        val result = "Ok"
        val resultEntity = ResultEntity(
            clientId = clientId,
            result = result
        )

        val isDuplicate = repository.findByClientIdIsContaining(clientId.substring(0, 3)).any {
            it.clientId == clientId
        }
        if (!isDuplicate) {
            repository.save(resultEntity)
            logger.info("clientId = $clientId saved")
        }else{
            logger.info("clientId = $clientId is duplicated")
        }


        return result
    }
}