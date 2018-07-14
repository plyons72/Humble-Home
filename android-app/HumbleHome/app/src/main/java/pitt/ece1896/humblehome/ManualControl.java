package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.models.nosql.BreakersDO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import pitt.ece1896.humblehome.BreakerView.BreakerState;

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

        return inflater.inflate(R.layout.manual_layout, container, false);

        /*LinearLayout layout = (LinearLayout) manualView.findViewById(R.id.layout);

        Runnable runnable = new Runnable() {
            public void run() {
                BreakersDO template = new BreakersDO();
                template.setUserId(MainActivity.getUserId());

                DynamoDBQueryExpression<BreakersDO> queryExpression = new DynamoDBQueryExpression<BreakersDO>()
                        .withHashKeyValues(template);

                List<BreakersDO> results = MainActivity.getDynamoDBMapper().query(BreakersDO.class, queryExpression);
                for (BreakersDO result : results) {
                    Log.d(TAG, result.toString());
                    BreakerView breaker = new BreakerView(getContext());
                    breaker.setId((int)Math.round(result.getBreakerId()));
                    breaker.setLabel(result.getLabel());
                    breaker.setDescription(result.getDescription());
                    breaker.setBreakerState(BreakerState.values()[(int)Math.round(result.getState())]);
                    breakers.add(breaker);
                    layout.addView(breaker);
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();*/
    }
}
