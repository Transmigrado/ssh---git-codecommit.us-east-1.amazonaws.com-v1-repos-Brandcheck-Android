package io.ebinar.infolder.fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import io.ebinar.infolder.R;
import io.ebinar.infolder.activities.MediaDetailActivity;


public class HistorialFragment extends Fragment {

    private BPCollection collection;
    private RelativeLayout empty_content;
    private Button done_button;
    private TextView historial_message;
    private BPRecycleViewAdapter adapter;

    public HistorialFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        empty_content = (RelativeLayout) view.findViewById(R.id.empty_content);
        done_button = (Button) view.findViewById(R.id.done_button);
        historial_message = (TextView) view.findViewById(R.id.historial_message);
        final RecyclerView listView = (RecyclerView) view.findViewById(R.id.listView);

        collection = new BPCollection();

        BPSqlQuery query = new BPSqlQuery("Historial");
        collection.dataFromSQLQuery(query);

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historialMessageDone();
                validateEmptyContent();
            }
        });


       adapter = new BPRecycleViewAdapter();
        adapter.addLayout(R.layout.item_historial);
        adapter.addCollection(collection);


        BPUIBinding binding = new BPUIBinding();
        adapter.setBinding(binding);


        /*
        BPArrayResource arrayResourceIconsMediaType = new BPArrayResource(getActivity());
        arrayResourceIconsMediaType.put("video",R.drawable.ic_video);
        arrayResourceIconsMediaType.put("audio",R.drawable.ic_audio);
        arrayResourceIconsMediaType.put("image",R.drawable.ic_type_media_image);

        BPArrayResource arrayResourceIcons = new BPArrayResource(getActivity());
        arrayResourceIcons.put("ADD_FAV",R.drawable.ic_historial_add_fav);
        arrayResourceIcons.put("REMOVE_FAV",R.drawable.ic_historial_remove_fav);
        arrayResourceIcons.put("MEDIA_AUDIO_COMPLETE", R.drawable.ic_historial_ear);
        arrayResourceIcons.put("MEDIA_VIDEO_COMPLETE", R.drawable.ic_historial_eye);
        arrayResourceIcons.put("MEDIA_VIEW", R.drawable.ic_historial_eye);

        BPArrayResource arrayResourceStrings= new BPArrayResource(getActivity());
        arrayResourceStrings.put("ADD_FAV",R.string.historial_action_add_fav);
        arrayResourceStrings.put("REMOVE_FAV",R.string.historial_action_remove_fav);
        arrayResourceStrings.put("MEDIA_AUDIO_COMPLETE", R.string.historial_action_listened_content);
        arrayResourceStrings.put("MEDIA_VIDEO_COMPLETE", R.string.historial_action_view_content);
        arrayResourceStrings.put("MEDIA_VIEW", R.string.historial_action_view_content);
        */


        /*
        adapter.addBinding("action", R.id.historial_icon, BPUserInterface.IMAGE_FROM_ARRAY_RESOURCE, arrayResourceIcons, null);
        adapter.addBinding("action",R.id.historial_action_text,BPUserInterface.TEXT_FROM_ARRAY_RESOURCE,arrayResourceStrings,null);
        adapter.addBinding("thumb",R.id.feed_thumb,BPUserInterface.IMAGE);
        adapter.addBinding("type", R.id.play_icon, BPUserInterface.IMAGE_FROM_ARRAY_RESOURCE, arrayResourceIconsMediaType, null);
        adapter.addBinding("text",R.id.video_description,BPUserInterface.TEXT);
        adapter.addBinding(getResources().getString(R.string.historial_count),R.id.historial_count, BPUserInterface.TEXT);
           */

        //binding.addBinding("action_count",R.id.historial_count_content, BPUserInterface.SHOW_WHEN_GREAT,1,null);

        BPObject info = new BPObject();
        info.setInt("value",1);

        binding.add(R.id.historial_count_content,info)
                .addRule("action_count",BPBindType.SHOW_GREAT_THAN);

        binding.add(R.id.feed_thumb)
                .addRule("thumb",BPBindType.IMAGE);


        binding.add(R.id.historial_count)
                .addRule("action_count|%@ veces", BPBindType.TEXT);

        binding.add(R.id.video_description)
                .addRule("title",BPBindType.TEXT);



        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        validateEmptyContent();

        adapter.setOnItemClickListener(new BPRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BPObject viewModel) {
                Intent intent = new Intent(getActivity(), MediaDetailActivity.class);

                Log.d("brandcheck",">> h : " +viewModel.toString());
                intent.putExtra("media_id",viewModel.getString("video_id"));

                startActivity(intent);
            }

            @Override
            public void renderView(View view) {

            }
        });



        return view;
    }

    private void validateEmptyContent(){
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);

        boolean historial_message_done = settings.getBoolean("historial_message_done", false);

        if(historial_message_done){

            if(collection.size() > 0){
                empty_content.setVisibility(View.GONE);
            }else{
                empty_content.setVisibility(View.VISIBLE);
                done_button.setVisibility(View.GONE);
            }

            historial_message.setText(getResources().getString(R.string.historial_empty));

        }else{
            empty_content.setVisibility(View.VISIBLE);
            historial_message.setText(getResources().getString(R.string.historial_explain));
        }


    }

    private void historialMessageDone(){
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("historial_message_done", true);
        editor.commit();
    }



    public void refresh(){
        adapter.refresh();
        validateEmptyContent();
    }

}