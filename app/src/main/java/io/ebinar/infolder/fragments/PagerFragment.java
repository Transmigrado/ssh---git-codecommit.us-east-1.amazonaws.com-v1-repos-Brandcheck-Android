package io.ebinar.infolder.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueprint.blueprint.adapter.BPPagerAdapter;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.api.SaveDataManager;
import io.ebinar.infolder.event.MediaEvent;
import io.ebinar.infolder.section.SectionPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagerFragment extends Fragment {


    private ViewPager pager;
    private int current = 0;
    int[] medios = {1,2,3,6,7,4,5,9};

    BPCollection collection;

    public int filter = 0 ;
    String tag;

    public PagerFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.grey_square1), getResources().getColor(R.color.orange_first));

        SectionPagerAdapter mSectionsPagerAdapter = new SectionPagerAdapter(getChildFragmentManager(), getActivity());

        BPPagerAdapter adapter = new BPPagerAdapter(this.getChildFragmentManager());

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);


        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)));
            MediaListFragment f = new MediaListFragment();
            f.medio = medios[i];
            f.filter = filter;
            adapter.addFragment(f);
        }

        pager = (ViewPager) view.findViewById(R.id.pager);

        pager.setOffscreenPageLimit(7);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /* llamamos al medio*/


        return view;
    }





}
