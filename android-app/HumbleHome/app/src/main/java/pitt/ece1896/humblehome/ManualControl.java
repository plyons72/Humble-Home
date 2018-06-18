package pitt.ece1896.humblehome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.List;

public class ManualControl extends Fragment {

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

        LinearLayout layout = (LinearLayout) manualView.findViewById(R.id.layout);

        BreakerView[] breakers = new BreakerView[32];
        for (int i = 0; i < breakers.length; i++) {
            breakers[i] = new BreakerView(getContext());
            breakers[i].setId(i);
            breakers[i].setLabel("Breaker Label");
            breakers[i].setDescription("Description");
            breakers[i].setBreakerState(BreakerView.BreakerState.values()[i % 4]);

            layout.addView(breakers[i]);
        }

        return manualView;

    }

}
