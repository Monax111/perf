package ru.tim.conference.perf.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResultRepository : JpaRepository<ResultEntity, Long>
