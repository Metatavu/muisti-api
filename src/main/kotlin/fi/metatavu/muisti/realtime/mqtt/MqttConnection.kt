package fi.metatavu.muisti.realtime.mqtt

import fi.metatavu.muisti.settings.MqttSettings
import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.util.*

/**
 * MQTT client connection
 *
 * @author Antti Leppä
 */
class MqttConnection {

    companion object {

        private val PUBLISHER_ID = UUID.randomUUID().toString()
        private var CLIENT: IMqttClient? = null
        private var SETTINGS: MqttSettings? = null

        /**
         * Connects to a MQTT server
         *
         * @throws MqttException thrown when MQTT connection fails
         */
        @Throws(MqttException::class)
        fun connect(settings: MqttSettings) {
            try {
                synchronized (this) {
                    val serverUrls = getServerUrls(settings = settings)
                    val client = MqttClient(serverUrls.first(), PUBLISHER_ID)
                    val options = MqttConnectOptions()
                    val username = settings.username
                    val password = settings.password

                    options.serverURIs = serverUrls
                    options.isAutomaticReconnect = true
                    options.isCleanSession = true
                    options.connectionTimeout = 10

                    if (username != null) {
                        options.userName = username
                    }

                    if (password != null) {
                        options.password = password.toCharArray()
                    }

                    client.connect(options)
                    client.subscribe(String.format("%s/#", settings.topic))
                    CLIENT = client
                    SETTINGS = settings
                }
            } catch (e: Exception) {
                throw MqttException(e)
            }
        }

        /**
         * Connects to a MQTT server
         *
         * @throws MqttException thrown when MQTT connection fails
         */
        @Throws(MqttException::class)
        fun disconnect() {
            try {
                synchronized (this) {
                    val client = CLIENT
                    if (client != null) {
                        client.disconnect()
                        CLIENT = null
                    }
                }
            } catch (e: Exception) {
                throw MqttException(e)
            }
        }

        /**
         * Publishes message
         *
         * @param message message
         * @throws MqttException thrown when MQTT message publish fails on any reason
         */
        @Throws(MqttException::class)
        fun publish(message: MqttMessage) {
            publish(message.subtopic, message.data, 1, false)
        }

        /**
         * Sets client subscriber
         */
        fun setSubscriber(callback: MqttCallback) {
            try {
                val client = CLIENT
                val settings = SETTINGS

                if (client == null || settings == null) {
                    throw MqttException("Client not configured")
                }

                client.subscribe("${settings.topic}/#", 1)
                client.setCallback(callback)
            } catch (e: Exception) {
                throw MqttException(e)
            }
        }

        /**
         * Publishes message into given MQTT topic
         *
         * @param subtopic subtopic to deliver the message to.
         * @param payload the byte array to use as the payload
         * @param qos the Quality of Service to deliver the message at. Valid values are 0, 1 or 2.
         * @param retained whether or not this message should be retained by the server.
         * @throws MqttException thrown when MQTT message publish fails on any reason
         */
        @Throws(MqttException::class)
        private fun publish(subtopic: String, payload: ByteArray, qos: Int, retained: Boolean) {
            try {
                val client = CLIENT
                val settings = SETTINGS

                if (client == null || settings == null) {
                    throw MqttException("Client not configured")
                }

                val topic = "${settings.topic}/$subtopic"
                client.publish(topic, payload, qos, retained)
            } catch (e: Exception) {
                throw MqttException(e)
            }
        }

        private fun getServerUrls(settings: MqttSettings): Array<String> {
            val serverUrl = settings.serverUrl
            val serverUrls = settings.serverUrls
            return serverUrls?.toTypedArray() ?: arrayOf("tcp://${serverUrl}")
        }


    }

}