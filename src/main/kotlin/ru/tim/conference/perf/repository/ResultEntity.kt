package ru.tim.conference.perf.repository

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
class ResultEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val id: Long? = null,

    @Column
    val clientId: String,
    @Column
    val result: String,
)