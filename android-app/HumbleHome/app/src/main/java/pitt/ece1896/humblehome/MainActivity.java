package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static DynamoDBMapper dynamoDBMapper;
    private static final String userId = "48756d626c65486f6d65";

    private MqttAndroidClient mqttAndroidClient;
    private final String MQTT_TAG = "MQTT - ";
    private final String serverUri = "ssl://b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1.mq.us-east-1.amazonaws.com:8883";
    private final String keystore_path = "";
    private final String clientId = "android-app";
    private final String username = "user";
    private final String password = "humblehome1896";

    private final String BreakerState = "BreakerState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d(TAG, "AWSMobileClient is instantiated and connection with AWS is successful!");
            }
        }).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        setContentView(R.layout.activity_main);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
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
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            //mqttConnectOptions.setSocketFactory(mqttAndroidClient.getSSLSocketFactory(this.getApplicationContext().getAssets().open(keystore_path), password));

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    subscribeToTopic(BreakerState);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, MQTT_TAG + "Failed to connect to: " + serverUri + "\nException: " + exception.toString());
                }
            });

        } catch (/*IOException | */MqttException ex) {
            Log.e(TAG, MQTT_TAG + ex.toString());
            ex.printStackTrace();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.control_item:
                        selectedFragment = BoardControl.newInstance();
                        break;
                    case R.id.data_item:
                        selectedFragment = DataAnalysis.newInstance();
                        break;
                    case R.id.settings_item:
                        selectedFragment = Settings.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        // Manually display the first fragment when app first opens
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, BoardControl.newInstance());
        transaction.commit();
    }

    public static DynamoDBMapper getDynamoDBMapper() {
        return dynamoDBMapper;
    }

    public static String getUserId() {
        return userId;
    }

    private void subscribeToTopic(String topic) {
        try {
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

    private void publishToTopic(String topic, byte[] payload) {
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
