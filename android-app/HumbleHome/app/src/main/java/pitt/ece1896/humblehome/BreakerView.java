package pitt.ece1896.humblehome;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

public class BreakerView extends TableLayout {

    private static final String TAG = "BreakerView";

    private LinearLayout layout;

    private TextView labelView;
    private TextView idView;
    private TextView descriptionView;
    private Switch breakerSwitch;

    private int id;
    private String label;
    private String description;
    private BreakerState breakerState;

    public BreakerView(Context context) {
        super(context);
        init(context);
    }

    public BreakerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(final Context context) {
        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(service);
        layout = (TableLayout) layoutInflater.inflate(R.layout.breaker_view, this, true);

        idView = (TextView) layout.findViewById(R.id.idView);
        labelView = (TextView) layout.findViewById(R.id.labelView);
        descriptionView = (TextView) layout.findViewById(R.id.descriptionView);
        breakerSwitch = (Switch) layout.findViewById(R.id.breakerSwitch);
        breakerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "switch: " + id + " isChecked: " + isChecked);
            }
        });
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
        this.idView.setText("Breaker ID: " + id);
        invalidate();
        requestLayout();
    }

    public String getLabel() {
        return this.label == null ? "" : this.label;
    }

    public void setLabel(String label) {
        Log.d(TAG, "new label: " + label);
        this.label = label;
        this.labelView.setText(label);
        //invalidate();
        //requestLayout();
    }

    public String getDescription() {
        return this.description == null ? "" : this.description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionView.setText(description);
        invalidate();
        requestLayout();
    }

    public BreakerState getBreakerState() {
        return this.breakerState;
    }

    public void setBreakerState(BreakerState breakerState) {
        this.breakerState = breakerState;
        if (breakerState == BreakerState.ON || breakerState == BreakerState.ALWAYS_ON) {
            this.breakerSwitch.setChecked(true);
        } else {
            this.breakerSwitch.setChecked(false);
        }
        invalidate();
        requestLayout();
    }

    public String toString() {
        return "breakerId: " + this.id + "\n" +
               "label: " + this.label + "\n" +
               "description: " + this.description + "\n" +
               "breakerState: " + this.breakerState;
    }

    public enum BreakerState { UNKNOWN, ON, OFF, ALWAYS_ON };

}
