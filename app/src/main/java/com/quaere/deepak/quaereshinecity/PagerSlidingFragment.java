package com.quaere.deepak.quaereshinecity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.quaere.deepak.quaereshinecity.Adapter.SampleFragmentPagerAdapter;
import com.quaere.deepak.quaereshinecity.CheckNetwork;
import com.quaere.deepak.quaereshinecity.R;

/**
 * Created by deepak sachan on 9/8/2015.
 */
public class PagerSlidingFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager_sliding_fragment, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        if (!CheckNetwork.isInternetAvailable(getActivity())) {
           Toast toast= Toast.makeText(getActivity(), "Internet Not available", Toast.LENGTH_LONG);
            View view1 = toast.getView();
            view1.setBackgroundResource(R.drawable.toast_drawablecolor);
            toast.setGravity(Gravity.TOP,25,500);
            toast.show();
        } else {

            viewPager.setAdapter(new SampleFragmentPagerAdapter(getActivity().getSupportFragmentManager()));

            // Give the PagerSlidingTabStrip the ViewPager
            PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
            // Attachthe view pager to the tab strip
            tabsStrip.setViewPager(viewPager);


        }
        return view;
    }
}