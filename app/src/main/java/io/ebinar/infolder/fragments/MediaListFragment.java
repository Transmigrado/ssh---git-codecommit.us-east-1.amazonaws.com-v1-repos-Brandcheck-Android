package io.ebinar.infolder.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.event.BPEvent;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.ebinar.infolder.MTApp;
import io.ebinar.infolder.R;
import io.ebinar.infolder.activities.AudioDetailActivity;
import io.ebinar.infolder.activities.MediaDetailActivity;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.event.MediaEvent;
import io.ebinar.infolder.event.SearchEvent;
import io.ebinar.infolder.utils.AlertManager;


public class MediaListFragment extends Fragment {

    private RelativeLayout loading;
    private RelativeLayout not_content_component;
    private BPRecycleViewAdapter adapter_list;
    public int medio = 0;
    public String objectId = "";
    public String brand = "0";
    private RecyclerView list;
    private EventBus bus = EventBus.getDefault();
    public int filter=0;
    private boolean charge = false;
    private  String tag;
    private BPCollection collection;
    public boolean isSearch = false;
    public String filterKey = "";
    public String from = "";
    public String to = "";

    public MediaListFragment() {

    }


    public static MediaListFragment newInstance(String param1, String param2) {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);


        AppCompatActivity activity = (AppCompatActivity) this.getActivity();

        if (toolbar != null) {

            activity.setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(getResources().getString(R.string.recent_activity));
        }

        loading = (RelativeLayout) view.findViewById(R.id.loading);
        not_content_component = (RelativeLayout) view.findViewById(R.id.not_content_component);

        list = (RecyclerView) view.findViewById(R.id.list);


        tag = "24hours";

        if(filter == 0){
            tag = "24hours";
        } else if(filter == 1){
            tag = "week";
        } else if(filter == 2){
            tag = "month";
        }


        collection = new BPCollection();

        BPUIBinding binding = new BPUIBinding();
        binding.add(R.id.feed_thumb)
                .addRule("thumbnail_url", BPBindType.IMAGE);

        binding.add(R.id.video_description)
                .addRule("description",BPBindType.TEXT);

        binding.add(R.id.date)
                .addRule("updatedAt",BPBindType.TEXT);

        binding.add(R.id.brand_name)
                .addRule("brand",BPBindType.TEXT);


        adapter_list = new BPRecycleViewAdapter();
        adapter_list.setBinding(binding);
        adapter_list.addCollection(collection);
        adapter_list.addLayout(R.layout.feed_item_layout);

        list.setAdapter(adapter_list);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter_list.setOnItemClickListener(new BPRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BPObject viewModel) {

                if(MTApp.jsonObject!=null){
                    boolean validate = false;
                    try {
                        JSONArray array = MTApp.jsonObject.getJSONArray("categories");
                        for(int i = 0 ; i < array.length();i++){
                            String t = array.getString(i);
                            if(t.equalsIgnoreCase(viewModel.getString("category"))){
                                validate = true;
                            }
                        }
                    }catch (Exception e){

                    }
                    if(validate){
                        OpenDetail(viewModel);
                    }else{

                        AlertManager.messageWithCancel(getContext(),"Brandcheck","No tienes acceso a esta marca, ¿deseas contratarla?\"");
                    }

                }else{
                    OpenDetail(viewModel);
                }



            }

            @Override
            public void renderView(View view) {
                TextView t = (TextView) view.findViewById(R.id.date);
                TextView brand = (TextView) view.findViewById(R.id.brand_name);

                String finalString_date = t.getText().toString().replace("T"," ");
                String finalString = brand.getText().toString();

                if(finalString.length()>12) {
                    finalString = finalString.substring(0,12) + "...";
                }

                t.setText(finalString_date);
                brand.setText(finalString);
            }
        });



        /*

           loading.setVisibility(View.VISIBLE);
        not_content_component.setVisibility(View.GONE);

        if(isSearch){
            search();
        }else{
            callContent();
        }
        */


        return view;
    }

    private void OpenDetail(BPObject o){
        Intent intent = new Intent(getContext(), MediaDetailActivity.class);
        intent.putExtra("video",o);
        startActivity(intent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        loading.setVisibility(View.VISIBLE);
        not_content_component.setVisibility(View.GONE);

        if(isSearch){
            search();
        }else{
            callContent();
        }

        //adapter_list.refresh();
    }

    private void search() {
        loading.setVisibility(View.VISIBLE);
        not_content_component.setVisibility(View.GONE);

        MTApi api = new MTApi();

        try {
            filterKey = URLEncoder.encode(filterKey, "utf-8");
        }catch (Exception e){}

        api.getSearch(filterKey, from, to, medio, new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {

                collection = object.getBPCollection("elements");
                adapter_list.addCollection(collection);


                if(adapter_list.getCollection().size() >0) {
                    loading.setVisibility(View.GONE);
                    not_content_component.setVisibility(View.GONE);
                }else{
                    loading.setVisibility(View.GONE);
                    not_content_component.setVisibility(View.VISIBLE);
                }

                adapter_list.notifyDataSetChanged();

            }

            @Override
            public void json(JSONObject json) {

            }

            @Override
            public void error() {


            }
        });
    }

    public void onFragmentInteraction(Uri uri) {

    }
    /**/

    private void callContent(){

        MTApi api = new MTApi();

        tag = "24hours";

        if(filter == 0){
            tag = "24hours";
        } else if(filter == 1){
            tag = "week";
        } else if(filter == 2){
            tag = "month";
        }

        api.getMedia(brand, tag, medio, new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {

                collection = object.getBPCollection("results");
                adapter_list.addCollection(collection);
                adapter_list.notifyDataSetChanged();

                if(adapter_list.getCollection().size() >0) {
                    loading.setVisibility(View.GONE);
                    not_content_component.setVisibility(View.GONE);
                }else{
                    loading.setVisibility(View.GONE);
                    not_content_component.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void json(JSONObject jsonObject){

            }

            @Override
            public void error() {

            }
        });

    }



}
