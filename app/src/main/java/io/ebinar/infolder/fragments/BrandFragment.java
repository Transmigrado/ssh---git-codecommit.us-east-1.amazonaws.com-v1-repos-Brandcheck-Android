package io.ebinar.infolder.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import io.ebinar.infolder.R;
import io.ebinar.infolder.activities.BrandDetailActivity;
import io.ebinar.infolder.activities.MediaActivity;
import io.ebinar.infolder.activities.SearchActivity;
import io.ebinar.infolder.activities.SearchBrandActivity;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.event.MediaEvent;


public class BrandFragment extends Fragment {


    private BPRecycleViewAdapter adapter;
    private BPCollection collection;
    private RelativeLayout loading;
    private RelativeLayout loading2;

    public boolean owner = false;

    public BrandFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_brand, container, false);


        loading = (RelativeLayout) view.findViewById(R.id.loading);
        loading2 = (RelativeLayout) view.findViewById(R.id.loading2);


        RecyclerView listView = (RecyclerView) view.findViewById(R.id.listView);
        collection = new BPCollection();


        BPUIBinding binding = new BPUIBinding();

        binding.add(R.id.brand_name)
                .addRule("name", BPBindType.TEXT);

        binding.add(R.id.brand_image)
                .addRule("logo",BPBindType.IMAGE);


        adapter = new BPRecycleViewAdapter(R.layout.item_brand_list);
        adapter.addCollection(collection);
        adapter.setBinding(binding);




        adapter.setOnItemClickListener(new BPRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BPObject viewModel) {

                Intent intent = new Intent(getActivity(), BrandDetailActivity.class);
                intent.putExtra("data", viewModel);

                startActivity(intent);
            }

            @Override
            public void renderView(View view) {

            }
        });



        listView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        listView.setLayoutManager(linearLayoutManager);

        getBrands();


        return view;
    }



    private void getBrands(){

        MTApi api = new MTApi();
        api.getBrands(owner, new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {


                BPCollection collection = object.getBPCollection("results");

                Collections.sort(collection, new Comparator<BPObject>() {
                    @Override
                    public int compare(BPObject o1, BPObject o2)
                    {

                        return  o1.getString("name").compareTo(o2.getString("name"));
                    }
                });

                if(collection!=null) {

                    adapter.addCollection(collection);
                    adapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                }


            }

            @Override
            public void json(JSONObject jsonObject){


            }

            @Override
            public void error(){
            }
        });
    }



    @Override
    public void onDetach() {
        super.onDetach();

    }



}
