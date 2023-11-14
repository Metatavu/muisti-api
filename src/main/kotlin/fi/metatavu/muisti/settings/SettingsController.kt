package fi.metatavu.muisti.settings

import org.slf4j.Logger
import java.time.Duration
import java.time.OffsetDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for application settings
 */
@ApplicationScoped
class SettingsController {

    @Inject
    private lateinit var logger: Logger

    /**
     * Returns MQTT server settings
     *
     * @return MQTT server settings
     */
    fun getMqttSettings(): MqttSettings {
        return MqttSettings(
            serverUrl = System.getenv("MQTT_SERVER_URL"),
            serverUrls = System.getenv("MQTT_SERVER_URLS")?.split(","),
            topic = System.getenv("MQTT_TOPIC"),
            username = System.getenv("MQTT_USERNAME"),
            password = System.getenv("MQTT_PASSWORD")
        )
    }

    /**
     * Returns time after created visitor sessions are valid
     *
     * @return time after created visitor sessions are valid
     */
    fun getVisitorSessionValidAfter(): OffsetDateTime {
        return OffsetDateTime.now().minus(getVisitorSessionTimeout())
    }

    /**
     * Returns visitor session timeout
     *
     * @return visitor session timeout
     */
    fun getVisitorSessionTimeout(): Duration {
        val envValue = System.getenv("VISITOR_SESSION_TIMEOUT")
        envValue ?: return DEFAULT_VISITOR_SESSION_TIMEOUT
        var result: Duration? = null

        try {
            result = Duration.parse(envValue)
        } catch (e: Exception) {
            logger.error("Failed to parse VISITOR_SESSION_TIMEOUT, using default value")
        }

        result ?: return DEFAULT_VISITOR_SESSION_TIMEOUT

        return result
    }

    companion object {
        val DEFAULT_VISITOR_SESSION_TIMEOUT = Duration.ofHours(6)
    }

}
