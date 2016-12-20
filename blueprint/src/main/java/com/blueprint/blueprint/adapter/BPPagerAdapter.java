package com.blueprint.blueprint.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;

/**
 * Created by jorgeacostaalvarado on 07-07-15.
 */
public class BPPagerAdapter extends FragmentPagerAdapter {

    private SparseArrayCompat<Fragment> fragments = new SparseArrayCompat<>();
    public BPPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment){
        fragments.append(fragments.size(), fragment);
    }
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }


    public String getPageTitle(int position){
        return "";
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
