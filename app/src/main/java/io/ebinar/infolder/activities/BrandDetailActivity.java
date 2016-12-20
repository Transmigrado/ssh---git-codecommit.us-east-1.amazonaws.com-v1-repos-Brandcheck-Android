package io.ebinar.infolder.activities;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//import android.support.v7.internal.widget.TintImageView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blueprint.blueprint.adapter.BPPagerAdapter;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.util.ImageUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.fragments.MediaListFragment;
import io.ebinar.infolder.section.SectionPagerAdapter;
import io.ebinar.infolder.utils.BPBitmapUtil;
import io.ebinar.infolder.utils.ColorArt;

public class BrandDetailActivity extends AppCompatActivity {
    private BPPagerAdapter adapter;
    private TabLayout tabLayout;
    //private SectionPagerAdapter mSectionsPagerAdapter;

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);
        Bundle bundle = getIntent().getExtras();
        BPObject data = bundle.getParcelable("data");

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);


        toolbar.setTitle(data.getString("name"));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), this);

        adapter = new BPPagerAdapter(getSupportFragmentManager());


        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(5);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //tabLayout.setTabTextColors(getResources().getColor(R.color.grey_square1), getResources().getColor(R.color.orange_first));


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.grey_square1), getResources().getColor(R.color.orange_first));


        //tabLayout.setTabTextColors(myList);

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);


        BPCollection collection = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Filter");
        query.setOrderBy("id");
        collection.dataFromSQLQuery(query);

        for(int i = 0 ; i < adapter.getCount();i++) {
            tabLayout.addTab(tabLayout.newTab().setText(collection.get(i).getString("name")));
        }

        SectionPagerAdapter mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), this);


        BPPagerAdapter adapter = new BPPagerAdapter(this.getSupportFragmentManager());


        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        //PNT
        int[] medios = {1,2,3,6,7,4,5,9};

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)));
            MediaListFragment f = new MediaListFragment();
            f.filter = 2;
            f.brand = data.getString("objectId");
            f.medio = medios[i];
            f.objectId = data.getString("objectId");
            adapter.addFragment(f);
        }

        pager = (ViewPager) findViewById(R.id.pager);

        pager.setOffscreenPageLimit(5);
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

    private static void setOverflowButtonColor(final AppCompatActivity activity, final PorterDuffColorFilter colorFilter) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

                if (outViews.isEmpty()) {
                    return;
                }


            }
        });
    }

    private static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
        else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
