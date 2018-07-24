package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static MQTTManager mqttManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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

        mqttManager = new MQTTManager(getApplicationContext());
        mqttManager.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d(TAG, MQTTManager.MQTT_TAG + "Reconnected to: " + MQTTManager.serverUri);
                } else {
                    Log.d(TAG, MQTTManager.MQTT_TAG + "Connected to: " + MQTTManager.serverUri);
                }

                /*mqttManager.subscribeToTopic(MQTTManager.GetBreakerInfo);
                mqttManager.subscribeToTopic(MQTTManager.GetBreakerState);

                // Manually display the first fragment when app first opens
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, BoardControl.newInstance());
                transaction.commit();*/

                mqttManager.subscribeToTopic(MQTTManager.SetBreakerInfo, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, MQTTManager.MQTT_TAG + "Subscribed to " + MQTTManager.SetBreakerInfo);

                        // Manually display the first fragment when app first opens
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, BoardControl.newInstance());
                        transaction.commit();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e(TAG, MQTTManager.MQTT_TAG + "Failed to subscribe to " + MQTTManager.GetBreakerInfo);
                    }
                });
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, MQTTManager.MQTT_TAG + "Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, MQTTManager.MQTT_TAG + "Message arrived\nTopic: " + topic + "\nPayload: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Log.d(TAG, MQTTManager.MQTT_TAG + "Delivery complete\nMessage: " + new String(token.getMessage().getPayload()));
                } catch (MqttException ex) {
                    Log.e(TAG, MQTTManager.MQTT_TAG + ex.toString());
                    ex.printStackTrace();
                }
            }
        });
        mqttManager.connect();
    }
}
