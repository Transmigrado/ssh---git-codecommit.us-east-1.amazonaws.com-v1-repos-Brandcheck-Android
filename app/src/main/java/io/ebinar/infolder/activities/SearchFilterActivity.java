package io.ebinar.infolder.activities;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.blueprint.blueprint.adapter.BPPagerAdapter;

import io.ebinar.infolder.R;
import io.ebinar.infolder.fragments.MediaListFragment;
import io.ebinar.infolder.section.SectionPagerAdapter;

public class SearchFilterActivity extends AppCompatActivity {

    int[] medios = {1,2,3,6,7,4,5,9};
    public int filter = 0 ;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        String filterKey = getIntent().getExtras().getString("filter");
        String from = getIntent().getExtras().getString("from");
        String to = getIntent().getExtras().getString("to");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(filterKey);
        toolbar.setTitleTextColor(Color.WHITE);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.grey_square1), getResources().getColor(R.color.orange_first));

        SectionPagerAdapter mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), this);

        BPPagerAdapter adapter = new BPPagerAdapter(getSupportFragmentManager());

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);


        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)));
            MediaListFragment f = new MediaListFragment();
            f.medio = medios[i];
            f.filter = filter;
            f.filterKey = filterKey;
            f.from = from;
            f.to = to;
            f.isSearch = true;
            adapter.addFragment(f);
        }

        pager = (ViewPager) findViewById(R.id.pager);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
