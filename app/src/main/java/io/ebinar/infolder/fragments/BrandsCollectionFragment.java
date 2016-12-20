package io.ebinar.infolder.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueprint.blueprint.adapter.BPPagerAdapter;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.json.JSONArray;
import org.json.JSONObject;

import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;

public class BrandsCollectionFragment extends Fragment {



    public BrandsCollectionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_brands_collection, container, false);

        AppCompatActivity activity = (AppCompatActivity) this.getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (toolbar != null) {

            activity.setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(getResources().getString(R.string.mybrand_title));
        }

        final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.grey_square1), getResources().getColor(R.color.orange_first));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.all_brand)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.my_brand)));


        BPPagerAdapter adapter = new BPPagerAdapter(getChildFragmentManager());

        BrandFragment brandFragment  = new BrandFragment();
        brandFragment.owner = true;
        adapter.addFragment(new BrandFragment());
        adapter.addFragment(brandFragment);

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


        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
