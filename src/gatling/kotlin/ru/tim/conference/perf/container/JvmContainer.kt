package ru.tim.conference.perf.container

import java.time.Duration
import mu.two.KotlinLogging
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait

val perfNetwork: Network = Network.newNetwork()

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

    fun withMilliCpuLimit(milliCpu: Long): JvmContainer {
        super.withCreateContainerCmdModifier {
            it.hostConfig!!.apply {
                withCpuPeriod(100_000)
                withCpuQuota(milliCpu * 100)
            }
        }
        return this
    }

    fun withMemoryLimit(megaByte: Long): JvmContainer {
        super.withCreateContainerCmdModifier {
            it.hostConfig!!.apply {
                withMemory(megaByte * 1000 * 1000)
            }
        }
        return this
    }

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
                .withStartupTimeout(Duration.ofSeconds(30))

        )
        return this
    }

    fun startContainer(): JvmContainer {

        withNetwork(perfNetwork)
        withNetworkAliases(service.networkAlias)
        withJvmOptions{}
        withMemoryLimit(1000)
        withMilliCpuLimit(1000)
        withHealthAwait()
        withExposedPorts(service.apiPort, service.managementPort)

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
    var xmx = 700
    var compressedClassSpaceSize = 100
    var maxMetaspaceSize = 110
    var reservedCodeCacheSize = 85

    fun toJvmArgs(): String = "-Xms${xmx}m -Xmx${xmx}m " +
        "-XX:CompressedClassSpaceSize=${compressedClassSpaceSize}M " +
        "-XX:MaxMetaspaceSize=${maxMetaspaceSize}M " +
        "-XX:ReservedCodeCacheSize=${reservedCodeCacheSize}M "
}
