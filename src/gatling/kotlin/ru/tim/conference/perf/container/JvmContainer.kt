package ru.tim.conference.perf.container

import java.time.Duration
import mu.two.KotlinLogging
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.InternetProtocol
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait


// docker network create -d bridge perf
val perfNetwork: Network = NetworkImpl("perf")

class NetworkImpl(val ids: String) : Network {
    override fun close() = Unit
    override fun apply(base: Statement, description: Description): Statement {
        return base
    }
    override fun getId() = ids
}

private val logger = KotlinLogging.logger {}

class Config(
    val networkAlias: String,
    val apiPort: Int = 8080,
    val managementPort: Int = 8080,
    val readinessPath: String = "/actuator/health/readiness",
    val metricPath: String = ""
)


open class JvmContainer(open val service: Config, tag: String = "latest") :
    GenericContainer<JvmContainer>(tag) {

    override fun getLivenessCheckPortNumbers(): MutableSet<Int> = mutableSetOf(service.managementPort)



    fun withSpringProfile(profile: String): JvmContainer {
        withEnv("SPRING_PROFILES_ACTIVE", profile)
        return this
    }
    fun withJvmOptions(function: JvmOption.() -> Unit): JvmContainer {
        val option = JvmOption().apply(function)
        withEnv("JAVA_TOOL_OPTIONS", option.toJvmArgs())
        return this
    }

    fun withHealthAwait(): JvmContainer {
        // по умолчанию на локалке не торчат readiness пробы
        withEnv("MANAGEMENT.ENDPOINT.HEALTH.PROBES.ENABLED", "true")

        waitingFor(
            Wait
                .forHttp(service.readinessPath)
                .forPort(service.managementPort)
                .withReadTimeout(Duration.ofSeconds(1))
                .withStartupTimeout(Duration.ofSeconds(40))

        )
        return this
    }

    fun withFixedExposedPort(hostPort: Int, containerPort: Int): JvmContainer {
        super.addFixedExposedPort(hostPort, containerPort, InternetProtocol.TCP)
        return this
    }

    fun startContainer(): JvmContainer {

        withNetwork(perfNetwork)
        withNetworkAliases(service.networkAlias)
        withJvmOptions{}
        withMemoryLimit(300)
        withMilliCpuLimit(500)
        withHealthAwait()
        withFixedExposedPort(service.apiPort, service.apiPort)

        super.start()
        // followOutput(Slf4jLogConsumer(logger))

        val springUrl = getUrl()
        logger.warn("${service.networkAlias} Started on host $springUrl")
        return this
    }

    fun getUrl() = "http://" + host + ":" + getMappedPort(service.apiPort)
    fun getHealthUrl() =
        "http://" + host + ":" + getMappedPort(service.managementPort) + "/" + service.readinessPath


}

class JvmOption {
    var xmx = 200
    var compressedClassSpaceSize = 100
    var maxMetaspaceSize = 110
    var reservedCodeCacheSize = 85

    fun toJvmArgs(): String = "-Xms${xmx}m -Xmx${xmx}m " +
        "-XX:CompressedClassSpaceSize=${compressedClassSpaceSize}M " +
        "-XX:MaxMetaspaceSize=${maxMetaspaceSize}M " +
        "-XX:ReservedCodeCacheSize=${reservedCodeCacheSize}M "
}


inline fun <reified T : GenericContainer<T>> GenericContainer<T>.withMilliCpuLimit(milliCpu: Long): GenericContainer<T> {
    withCreateContainerCmdModifier {
        it.hostConfig!!.apply {
            withCpuPeriod(100_000)
            withCpuQuota(milliCpu * 100)
        }
    }
    return this
}

inline fun <reified T : GenericContainer<T>> GenericContainer<T>.withMemoryLimit(megaByte: Long): GenericContainer<T> {
    withCreateContainerCmdModifier {
        it.hostConfig!!.apply {
            withMemory(megaByte * 1000 * 1000)
        }
    }
    return this
}