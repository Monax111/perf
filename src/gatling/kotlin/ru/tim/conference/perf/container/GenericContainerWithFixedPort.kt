package ru.tim.conference.perf.container

import org.testcontainers.containers.GenericContainer

open class GenericContainerWithFixedPort<T : GenericContainer<T>?>(
    image: String
) : GenericContainer<T>(image) {

//    override fun withExposedPorts(vararg ports: Int?): T {
//        ports.forEach {
//            it?.let {
//                addFixedExposedPort(it, it)
//            }
//
//        }
//        return this as T
//    }

    override fun start() {
//        exposedPorts.forEach {
//            addFixedExposedPort(it, it)
//        }
        withCreateContainerCmdModifier {
            it.withHostConfig (
                    it.hostConfig!!
                    .withAutoRemove(true)
            )
        }

        super.start()
    }
}