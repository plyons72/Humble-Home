package pitt.ece1896.humblehome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class DataAnalysis extends Fragment {

    private static final String TAG = "DataAnalysis";

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

        View dataView = (View)inflater.inflate(R.layout.data_layout, container, false);

        createRealTimeGraph(dataView);

        createBarGraph(dataView);

        return dataView;
    }

    private void createRealTimeGraph(View view) {
        // This implementation not real-time
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dataLayout);
        GraphView graph = (GraphView) layout.findViewById(R.id.realTimeGraph);
        graph.setTitle("");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0,1),
                new DataPoint(1,2),
                new DataPoint(2,3),
                new DataPoint(3,4),
                new DataPoint(4,5),
                new DataPoint(5,6),
                new DataPoint(6,7)
        });
        graph.addSeries(series);
    }

    private void createBarGraph(View view) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dataLayout);
        GraphView graph = (GraphView) layout.findViewById(R.id.barGraph);
        graph.setTitle("kWh/day");
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0,1),
                new DataPoint(1,2),
                new DataPoint(2,3),
                new DataPoint(3,4),
                new DataPoint(4,5),
                new DataPoint(5,6),
                new DataPoint(6,7)
        });
        graph.addSeries(series);

        /*series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int)data.getX() * 255 / 4, (int)Math.abs(data.getY() * 255 / 6), 100);
            }
        });*/

        series.setSpacing(50);
        series.setDrawValuesOnTop(false);
        series.setValuesOnTopColor(Color.BLACK);
    }

}
