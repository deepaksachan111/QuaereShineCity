package com.quaere.deepak.quaereshinecity.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.quaere.deepak.quaereshinecity.AccountFragment;
import com.quaere.deepak.quaereshinecity.RedeemFragment;
import com.quaere.deepak.quaereshinecity.StatementFragment;

/**
 * Created by deepak sachan on 8/26/2015.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{ "Redeem", "Statement", "Account"};
    //  private int tabimage[] = new int[]{R.drawable.contact,R.drawable.contact,R.drawable.contact};

    public SampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {


        if (position==0) {
            return new RedeemFragment();
        }if(position ==1) {

            return new StatementFragment();
        }
        return new AccountFragment();

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position] ;
    }


}