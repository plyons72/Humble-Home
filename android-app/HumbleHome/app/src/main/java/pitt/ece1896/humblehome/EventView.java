package pitt.ece1896.humblehome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import pitt.ece1896.humblehome.BreakerView.BreakerState;

public class EventView extends TableLayout {

    private static final String TAG = "EventView";

    private LinearLayout layout;

    private TextView switchState;
    private TextView switchLabel;
    private TextView switchId;
    private TextView eventDate;
    private TextView eventTime;

    private int id;
    private BreakerView breaker;
    private BreakerState newState;
    private Date dateTime;

    public EventView(Context context) {
        super(context);
        init(context);
    }

    public EventView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(service);
        layout = (TableLayout) layoutInflater.inflate(R.layout.event_view, this, true);

        switchState = (TextView) layout.findViewById(R.id.switchState);
        switchLabel = (TextView) layout.findViewById(R.id.switchLabel);
        switchId = (TextView) layout.findViewById(R.id.switchId);
        eventDate = (TextView) layout.findViewById(R.id.eventDate);
        eventTime = (TextView) layout.findViewById(R.id.eventTime);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BreakerView getBreaker() {
        return this.breaker;
    }

    public void setBreaker(BreakerView breaker) {
        this.breaker = breaker;
        this.switchLabel.setText(breaker.getLabel());
        this.switchId.setText("Breaker ID: " + breaker.getId());
        invalidate();
        requestLayout();
    }

    public BreakerState getNewState() {
        return this.newState;
    }

    public void setNewState(BreakerState newState) {
        this.newState = newState;
        this.switchState.setText(newState.toString());
        invalidate();
        requestLayout();
    }

    public Date getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        this.eventDate.setText(dateFormat.format(dateTime));
        this.eventTime.setText(timeFormat.format(dateTime));
        invalidate();
        requestLayout();
    }

}