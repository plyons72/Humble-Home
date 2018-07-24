package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.json.JSONObject;

public class ManualControl extends Fragment {

    private static final String TAG = "ManualControl";

    public static List<BreakerView> breakers = new ArrayList<BreakerView>();

    public static ManualControl newInstance() {
        ManualControl fragment = new ManualControl();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View manualView = inflater.inflate(R.layout.manual_layout, container, false);
        final LinearLayout layout = (LinearLayout) manualView.findViewById(R.id.layout);

        if (MainActivity.mqttManager != null) {

            MainActivity.mqttManager.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        Log.d(TAG, MQTTManager.MQTT_TAG + "Reconnected to: " + MQTTManager.serverUri);

                    } else {
                        Log.d(TAG, MQTTManager.MQTT_TAG + "Connected to: " + MQTTManager.serverUri);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, MQTTManager.MQTT_TAG + "Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, MQTTManager.MQTT_TAG + "Message arrived\nTopic: " + topic + "\nPayload: " + message.getPayload());

                    if (topic.equals(MQTTManager.SetBreakerInfo)) {
                        BreakerView breakerView = new BreakerView(getContext());
                        breakerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BreakerView breakerView = (BreakerView)v;
                                Log.d(TAG, "breaker " + breakerView.getId() + " clicked");

                                DialogFragment breakerInfoDialog = new BreakerInfoDialog();
                                Bundle args = new Bundle();
                                args.putInt("breakerId", breakerView.getId());
                                args.putString("label", breakerView.getLabel());
                                args.putString("description", breakerView.getDescription());
                                breakerInfoDialog.setArguments(args);

                                breakerInfoDialog.show(getFragmentManager(), "breaker");
                            }
                        });

                        JSONObject jsonObject = new JSONObject(message.getPayload().toString());
                        if (jsonObject.has("Item")) {
                            JSONObject breakerJson = jsonObject.getJSONObject("Item");
                            if (breakerJson.has("breakerId")) {
                                breakerView.setId(breakerJson.getJSONObject("breakerId").getInt("N"));
                            }
                            if (breakerJson.has("label")) {
                                breakerView.setLabel(breakerJson.getJSONObject("label").getString("S"));
                            }
                            if (breakerJson.has("description")) {
                                breakerView.setDescription(breakerJson.getJSONObject("description").getString("S"));
                            }
                            if (breakerJson.has("breakerState")) {
                                breakerView.setBreakerState(BreakerView.BreakerState.values()[breakerJson.getJSONObject("breakerState").getInt("N")]);
                            }
                            layout.addView(breakerView);
                        }
                    }
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

            MainActivity.mqttManager.subscribeToTopic(MQTTManager.SetBreakerInfo);
            MainActivity.mqttManager.publishToTopic(MQTTManager.GetBreakerInfo, new String("1").getBytes());

        } else {
            Log.e(TAG, "mqttAndroidClient is null");
        }

        return manualView;
    }

    public static void updateBreaker(int breakerId, String label, String description) {
        if (breakers != null && breakers.size() > breakerId) {
            Log.d(TAG, "updating breaker id: " + breakerId);
            breakers.get(breakerId).setLabel(label);
            breakers.get(breakerId).setDescription(description);

            //TODO: send update to database
        }
    }
}
