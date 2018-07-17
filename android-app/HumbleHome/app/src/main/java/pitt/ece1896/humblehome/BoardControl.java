package pitt.ece1896.humblehome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BoardControl extends Fragment {

    private static final String TAG = "BoardControl";

    // Number of pages to show
    private static final int NUM_PAGES = /*2*/1;

    // The pager widget (handles animation and allows swiping horizontally)
    private ViewPager pager;

    // The pager adapter (provides the pages to the view pager widget)
    private PagerAdapter pagerAdapter;

    public static BoardControl newInstance() {
        BoardControl fragment = new BoardControl();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Breaker Board Control");

        View controlView = inflater.inflate(R.layout.control_layout, container, false);

        pager = (ViewPager) controlView.findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);

        return controlView;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int index) {
            /*if (index == 0)*/ return new ManualControl();
            //else return new ScheduleControl();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

}
