package ru.tim.conference.perf.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ResultRepository : JpaRepository<ResultEntity, Long> {

    // страшная функция которая должна тормозить
    fun findByClientIdIsContaining(prefix: String): Collection<ResultEntity>

}
