package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;

public class ScheduleControl extends Fragment {

    public static EventView[] events;

    public static ScheduleControl newInstance() {
        ScheduleControl fragment = new ScheduleControl();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View scheduleView = inflater.inflate(R.layout.schedule_layout, container, false);

        LinearLayout layout = (LinearLayout) scheduleView.findViewById(R.id.layout);

        BreakerView testBreaker = new BreakerView(getContext());
        testBreaker.setId(2);
        testBreaker.setLabel("Test Breaker");

        events = new EventView[5];
        for (int i = 0; i < events.length; i++) {
            events[i] = new EventView(getContext());
            events[i].setId(i);
            events[i].setBreaker(testBreaker);
            events[i].setNewState(BreakerView.BreakerState.values()[i % 4]);
            events[i].setDateTime(new Date());
            layout.addView(events[i]);
        }

        return scheduleView;
    }

}
