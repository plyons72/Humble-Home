package pitt.ece1896.humblehome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataAnalysis extends Fragment {

    private static final String TAG = "DataAnalysis";

    private View dataView;
    private GraphView lineGraph;
    private GraphView barGraph;

    public static DataAnalysis newInstance() {
        DataAnalysis fragment = new DataAnalysis();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Usage Data");

        dataView = (View)inflater.inflate(R.layout.data_layout, container, false);
        LinearLayout layout = (LinearLayout) dataView.findViewById(R.id.dataLayout);
        lineGraph = (GraphView) layout.findViewById(R.id.lineGraph);
        barGraph = (GraphView) layout.findViewById(R.id.barGraph);

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
                    Log.d(TAG, MQTTManager.MQTT_TAG + "Message arrived\nTopic: " + topic + "\nPayload: " + new String(message.getPayload()));

                    String payload = new String(message.getPayload());
                    parseBreakerData(payload);
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

            // Publish to GetBreakerData to request breaker data for the current day
            MainActivity.mqttManager.publishToTopic(MQTTManager.GetBreakerData, new String("0").getBytes());

            // Publish to GetBreakerData to request breaker data for the last week
            MainActivity.mqttManager.publishToTopic(MQTTManager.GetBreakerData, new String("7").getBytes());

        } else {
            Log.e(TAG, "mqttAndroidClient is null");
        }

        return dataView;
    }

    private void parseBreakerData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            int id = jsonObject.getInt("id");
            JSONArray jsonData = jsonObject.getJSONArray("data");

            DataPoint[] dataPoints = new DataPoint[jsonData.length()];
            if (id == 0) {
                //long lastTime = 0;
                for (int i = 0; i < jsonData.length(); i++) {
                    //long t = Long.parseLong(jsonData.getJSONObject(i).getJSONObject("timestamp").getString("S"));
                    Date d = new Date();
                    //if (lastTime == 0) d.setTime(lastTime);
                    //else d.setTime(d.getTime() + (t - lastTime));
                    //lastTime = t;
                    //Log.d(TAG, "Date: " + d.toString());
                    d.setTime(i * 15 * 60 * 1000);
                    double y = jsonData.getJSONObject(i).getJSONObject("power").getDouble("N");
                    dataPoints[i] = new DataPoint(d, y);
                }
                createLineGraph(dataPoints);
            } else if (id > 0) {
                for (int i = 0; i < jsonData.length(); i++) {
                    double y = jsonData.getDouble(i);
                    dataPoints[i] = new DataPoint(i, y);
                }
                createBarGraph(dataPoints);
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void createLineGraph(DataPoint[] dataPoints) {
        lineGraph.setTitle("Today's Usage (kWh)");
        lineGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), /*DateFormat.getTimeInstance()*/new SimpleDateFormat("HH:mm")));
        lineGraph.getViewport().setMinX(dataPoints[0].getX());
        lineGraph.getViewport().setMaxX(dataPoints[dataPoints.length - 1].getX());
        lineGraph.getViewport().setXAxisBoundsManual(true);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        lineGraph.addSeries(series);
        lineGraph.setVisibility(View.VISIBLE);
    }

    private void createBarGraph(DataPoint[] dataPoints) {
        barGraph.setTitle("Past Week's Usage (kWh)");
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
        barGraph.addSeries(series);

        /*series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int)data.getX() * 255 / 4, (int)Math.abs(data.getY() * 255 / 6), 100);
            }
        });*/

        series.setSpacing(50);
        series.setDrawValuesOnTop(false);
        series.setValuesOnTopColor(Color.BLACK);

        barGraph.setVisibility(View.VISIBLE);
    }

}
