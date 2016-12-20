package io.ebinar.infolder.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.ebinar.infolder.MTApp;
import io.ebinar.infolder.MainActivity;
import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.api.MTUser;
import io.ebinar.infolder.event.MediaEvent;
import io.ebinar.infolder.fragments.BrandFragment;
import io.ebinar.infolder.fragments.BrandsCollectionFragment;
import io.ebinar.infolder.fragments.FavoriteFragment;
import io.ebinar.infolder.fragments.HistorialFragment;
import io.ebinar.infolder.fragments.PagerFragment;
import io.ebinar.infolder.utils.AlertManager;

public class MediaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private enum Section {
        LAST_24_HOURS,
        WEEK,
        MONTH,
        FAVORITES,
        HISTORIAL,
        BRANDS
    }

    private Section section;
    private PagerFragment pager_day;
    private PagerFragment pager_week;
    private PagerFragment pager_month;
    private HistorialFragment historialFragment;
    private FavoriteFragment fragment;
    private BrandsCollectionFragment brandFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        section = Section.LAST_24_HOURS;

        pager_day = new PagerFragment();
        pager_week = new PagerFragment();
        pager_month = new PagerFragment();

        pager_day.filter = 0;
        pager_week.filter = 1;
        pager_month.filter = 2;

         historialFragment = new HistorialFragment();
         fragment = new FavoriteFragment();
        brandFragment =  new BrandsCollectionFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.recent_activity));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        BPObject user = MTUser.getCurrenUser(this);



        TextView header_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name);
        TextView header_company = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_company);


        header_name.setText(user.getString("firstName"));
        header_company.setText(user.getString("company"));

        setSection();

        MTApi api = new MTApi();
        api.getMe(new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {



            }

            @Override
            public void json(JSONObject json) {
                MTApp.jsonObject = json;
            }

            @Override
            public void error() {

            }
        });

    }

    private void setSection(){

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(section){
            case LAST_24_HOURS:


                fragmentManager.beginTransaction()
                        .replace(R.id.container, pager_day)
                        .commit();

                EventBus.getDefault().post(new MediaEvent(MediaEvent.Type.REFRESH_MEDIA,0));
                break;


            case WEEK:


                fragmentManager.beginTransaction()
                        .replace(R.id.container, pager_week)
                        .commit();

                EventBus.getDefault().post(new MediaEvent(MediaEvent.Type.REFRESH_MEDIA,1));
                break;
            case MONTH:


                fragmentManager.beginTransaction()
                        .replace(R.id.container, pager_month)
                        .commit();

                EventBus.getDefault().post(new MediaEvent(MediaEvent.Type.REFRESH_MEDIA, 2));
                break;
            case HISTORIAL:

                fragmentManager.beginTransaction()
                        .replace(R.id.container, historialFragment)
                        .commit();
            break;
            case FAVORITES:




                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();

                break;
            case BRANDS:



                fragmentManager.beginTransaction()
                        .replace(R.id.container, brandFragment)
                        .commit();

                break;

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(section == Section.HISTORIAL) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.historial_menu, menu);
        }else if(section == Section.LAST_24_HOURS || section == Section.MONTH || section == Section.WEEK){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.search_menu, menu);
        }else if(section == Section.BRANDS){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.search_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(section == Section.HISTORIAL) {
            // borramos
            new AlertDialog.Builder(this)
                    .setTitle("Borrar historial")
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }
                    ).setPositiveButton("si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    BPCollection collection = new BPCollection();
                    BPSqlQuery query = new BPSqlQuery("Historial");

                    collection.dataFromSQLQuery(query);

                    for(int i = 0 ; i < collection.size();i++){
                        collection.get(i).remove();
                    }

                    historialFragment.refresh();


                }
            }).create().show();
        } else if(section == Section.LAST_24_HOURS || section == Section.MONTH || section == Section.WEEK){
            Intent intent = new Intent(MediaActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if(section == Section.BRANDS){
            Intent intent = new Intent(MediaActivity.this, SearchActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            // Handle the camera action
            section = Section.FAVORITES;
            setSection();
            getSupportActionBar().setTitle(item.getTitle());
        }else if(id==R.id.action_historial){
            section = Section.HISTORIAL;
            setSection();
            getSupportActionBar().setTitle(item.getTitle());
        }else if(id == R.id.action_recent_activity){
            section = Section.LAST_24_HOURS;
            setSection();
            getSupportActionBar().setTitle(item.getTitle());
        }else if(id == R.id.action_week_activity){
            section = Section.WEEK;
            setSection();
            getSupportActionBar().setTitle(item.getTitle());
        }else if(id == R.id.action_month_activity){
            section = Section.MONTH;
            setSection();
            getSupportActionBar().setTitle(item.getTitle());
        } else if(id == R.id.action_brands){
            section = Section.BRANDS;
            setSection();
            getSupportActionBar().setTitle(item.getTitle());
        } else if(id== R.id.action_config){
            Intent intent = new Intent(this,ConfigActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        invalidateOptionsMenu();

        return true;
    }
}
