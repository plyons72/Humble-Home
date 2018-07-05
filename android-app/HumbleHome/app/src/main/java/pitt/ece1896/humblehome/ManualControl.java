package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ManualControl extends Fragment {

    public static BreakerView[] breakers;

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

        breakers = new BreakerView[32];
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
