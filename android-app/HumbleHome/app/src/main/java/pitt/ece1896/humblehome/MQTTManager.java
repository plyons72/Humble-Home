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
    public static final String serverUri = "ssl://b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1.mq.us-east-1.amazonaws.com:8883";
    private final String clientId = "android-app";
    private final String username = "user";
    private final String password = "humblehome1896";

    public static final String GetBreakerInfo = "GetBreakerInfo";
    public static final String SetBreakerInfo = "SetBreakerInfo";
    public static final String GetBreakerState = "GetBreakerState";
    public static final String SetBreakerState = "SetBreakerState";

    public MQTTManager(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

        /*mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d(TAG, MQTT_TAG + "Reconnected to: " + serverUri);

                } else {
                    Log.d(TAG, MQTT_TAG + "Connected to: " + serverUri);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, MQTT_TAG + "Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, MQTT_TAG + "Message arrived\nTopic: " + topic + "\nPayload: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Log.d(TAG, MQTT_TAG + "Delivery complete\nMessage: " + new String(token.getMessage().getPayload()));
                } catch (MqttException ex) {
                    Log.e(TAG, MQTT_TAG + ex.toString());
                    ex.printStackTrace();
                }
            }
        });*/
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
            })/*.waitForCompletion()*/;
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
