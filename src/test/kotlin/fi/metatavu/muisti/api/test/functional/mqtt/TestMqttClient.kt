package fi.metatavu.muisti.api.test.functional.mqtt

import fi.metatavu.muisti.api.test.functional.settings.MqttTestSettings
import fi.metatavu.muisti.realtime.mqtt.MqttConnection
import fi.metatavu.muisti.settings.MqttSettings
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * MQTT client for functional tests
 */
class TestMqttClient(private val settings: MqttTestSettings): MqttCallback, AutoCloseable {

    private val subscriptions = mutableMapOf<String, MutableList<TestMqttSubscription<*>>>()

    /**
     * Constructor. Connects to MQTT server
     */
    init {
        MqttConnection.connect(MqttSettings(
            serverUrl = settings.mqttServerUrl,
            topic = settings.mqttTopic,
            username = null,
            password = null,
            serverUrls = null
        ))

        MqttConnection.setSubscriber(this)
    }

    /**
     * Subscribes to a topic
     *
     * @param targetClass message class
     * @param subtopic subtopic to subscribe
     */
    fun <T> subscribe(targetClass: Class<T>, subtopic: String): TestMqttSubscription<T> {
        val topic = "${settings.mqttTopic}/$subtopic"
        var topicSubscriptions = subscriptions.get(topic)
        if (topicSubscriptions == null) {
            topicSubscriptions = mutableListOf()
            subscriptions[topic] = topicSubscriptions
        }

        val subscription = TestMqttSubscription(targetClass)
        topicSubscriptions.add(subscription)

        return subscription
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        message ?: return

        val messageBytes = message.payload
        subscriptions[topic]?.forEach{ it.addMessageBytes(messageBytes) }
    }

    override fun connectionLost(cause: Throwable?) {
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }

    override fun close() {
        MqttConnection.disconnect()
    }

}