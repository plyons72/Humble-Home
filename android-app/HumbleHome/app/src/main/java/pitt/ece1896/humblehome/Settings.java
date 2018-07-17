package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends Fragment {

    private static final String TAG = "Settings";

    public static Settings newInstance() {
        Settings fragment = new Settings();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Settings");

        return inflater.inflate(R.layout.settings_layout, container, false);
    }

}
