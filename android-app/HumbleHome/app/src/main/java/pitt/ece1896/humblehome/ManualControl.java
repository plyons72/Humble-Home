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

    //public static BreakerView[] breakers;
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

        /*breakers = new BreakerView[32];
        for (int i = 0; i < breakers.length; i++) {
            breakers[i] = new BreakerView(getContext());
            breakers[i].setOnClickListener(new View.OnClickListener() {
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
            breakers[i].setId(i);
            breakers[i].setLabel("Breaker Label");
            breakers[i].setDescription("Description");
            breakers[i].setBreakerState(BreakerView.BreakerState.values()[i % 4]);
            layout.addView(breakers[i]);
        }*/

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
                    breaker.setOnClickListener(new View.OnClickListener() {
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
                    breaker.setId((int)Math.round(result.getBreakerId()));
                    breaker.setLabel(result.getLabel());
                    breaker.setDescription(result.getDescription());
                    breaker.setBreakerState(BreakerState.values()[(int)Math.round(result.getState())]);
                    breakers.add(breaker);
                    //layout.addView(breaker);
                    Log.d(TAG, breaker.toString());
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();

        return manualView;
    }

    /*public static void updateBreaker(int breakerId, String label, String description) {
        if (breakers != null && breakers.length > breakerId) {
            Log.d(TAG, "updating breaker id: " + breakerId);
            breakers[breakerId].setLabel(label);
            breakers[breakerId].setDescription(description);
        }
    }*/

    public static void updateBreaker(int breakerId, String label, String description) {
        if (breakers != null && breakers.size() > breakerId) {
            Log.d(TAG, "updating breaker id: " + breakerId);
            breakers.get(breakerId).setLabel(label);
            breakers.get(breakerId).setDescription(description);

            //TODO: send update to database
        }
    }
}
