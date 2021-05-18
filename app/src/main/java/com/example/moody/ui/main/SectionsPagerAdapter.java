package com.example.moody.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.moody.Q1Fragment;
import com.example.moody.Q2Fragment;
import com.example.moody.Q3Fragment;
import com.example.moody.Q4Fragment;
import com.example.moody.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println(position);
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Q1Fragment();
                break;
            case 1:
                fragment = new Q2Fragment();
                break;
            case 2:
                fragment = new Q3Fragment();
                break;
            case 3:
                fragment = new Q4Fragment();
                break;

        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 4;
    }
}