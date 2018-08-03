package pitt.ece1896.humblehome;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTManager {

    private static final String TAG = "MQTTManager";

    private MqttAndroidClient mqttAndroidClient;

    public static final String MQTT_TAG = "MQTT - ";
    public static final String serverUri = "tcp://ec2-54-243-18-99.compute-1.amazonaws.com:1883";
    private final String clientId = "android-app";
    private final String username = "humblehome";
    private final String password = "1896seniordesign";

    public static final String GetBreakerInfo = "GetBreakerInfo";
    public static final String PutBreakerInfo = "PutBreakerInfo";
    public static final String SetBreakerInfo = "SetBreakerInfo";
    public static final String GetBreakerState = "GetBreakerState";
    public static final String PutBreakerState = "PutBreakerState";
    public static final String SetBreakerState = "SetBreakerState";
    public static final String GetBreakerData = "GetBreakerData";
    public static final String SetBreakerData = "SetBreakerData";

    public MQTTManager(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
    }

    public boolean connected() {
        return this.mqttAndroidClient.isConnected();
    }

    public void connect() {
        if (!connected()) {
            try {
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(false);
                mqttConnectOptions.setUserName(username);
                mqttConnectOptions.setPassword(password.toCharArray());
                mqttConnectOptions.setConnectionTimeout(0);

                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, MQTT_TAG + "Failed to connect to: " + serverUri + "\nException: " + exception.toString());
                    }
                });

            } catch (MqttException ex) {
                Log.e(TAG, MQTT_TAG + ex.toString());
                ex.printStackTrace();
            }
        }
    }

    public void setCallback(MqttCallbackExtended mqttCallbackExtended) {
        mqttAndroidClient.setCallback(mqttCallbackExtended);
    }

    public void subscribeToTopic(String topic) {
        try {
            Log.d(TAG, MQTT_TAG + "Attempting to subscribe to topic: " + topic);
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, MQTT_TAG + "Subscribed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, MQTT_TAG + "Failed to subscribe");
                }
            });
        } catch (MqttException ex) {
            Log.e(TAG, MQTT_TAG + ex.toString());
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic, IMqttActionListener mqttActionListener) {
        try {
            Log.d(TAG, MQTT_TAG + "Attempting to subscribe to topic: " + topic);
            mqttAndroidClient.subscribe(topic, 0, null, mqttActionListener);
        } catch (MqttException ex) {
            Log.e(TAG, MQTT_TAG + ex.toString());
            ex.printStackTrace();
        }
    }

    public void publishToTopic(String topic, byte[] payload) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(payload);
            mqttAndroidClient.publish(topic, mqttMessage);
        } catch (MqttException ex) {
            Log.e(TAG, MQTT_TAG + ex.toString());
            ex.printStackTrace();
        }
    }
}
