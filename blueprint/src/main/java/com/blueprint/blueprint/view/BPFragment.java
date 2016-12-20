package com.blueprint.blueprint.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blueprint.blueprint.R;
import com.blueprint.blueprint.adapter.BPPagerAdapter;
import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.builder.BPAppBuilder;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.event.BPEvent;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.util.BPForm;

import org.json.JSONObject;

import static com.blueprint.blueprint.http.BPApi.*;

public class BPFragment extends Fragment implements BPEvent.BPOnEvent {

    private BPObject data;
    private BPUIBinding binding;

    public static BPFragment newInstance(BPObject data) {
        BPFragment fragment = new BPFragment();
        fragment.setData(data);
        return fragment;
    }

    public BPFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(getLayout(), container, false);
        bindTabs(view);
        bindSidebar(view);
        bindComponents(view);

        binding = new BPUIBinding(view);
        BPObject object = new BPObject();
        BPObject viewBind = data.getBPObject("bind");


        if(viewBind!=null) {
            BPCollection viewBinding = viewBind.getBPCollection("binding");
            BPCollection values = viewBind.getBPCollection("values");

            for (int k = 0; k < values.size(); k++) {
                BPObject temp = values.get(k);
                object.setObject(temp.getString("field"), temp.getObject("value"));
            }

            for (int i = 0; i < viewBinding.size(); i++) {
                BPObject o = viewBinding.get(i);


                int id = BPAppBuilder.getUIID(o.getString("ui"));

                if(id==0) {
                    id = getActivity().getApplicationContext().getResources().getIdentifier(o.getString("ui"), "id", getActivity().getApplicationContext().getPackageName());
                }

                binding.add(id,o)
                        .addRule(o.getString("field"), BPBindType.valueOf(o.getString("type")));
            }
        }

        binding.setData(object);
        binding.setCallbackEvent(this);
        binding.draw();

        BPObject query = data.getBPObject("query");

        if(query!=null){
            executeQuery(query);
        }

        bindList(view);

        return view;

    }

    private void bindList(View view){
        RecyclerView list = (RecyclerView)view.findViewById(R.id.bp_list);
        if(list == null){
            return;
        }

        BPCollection collection = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Test");
        collection.dataFromSQLQuery(query);

        BPUIBinding binding = new BPUIBinding();
        BPCollection viewBinding =   data.getBPObject("item").getBPCollection("binding");

        for (int i = 0; i < viewBinding.size(); i++) {
            BPObject o = viewBinding.get(i);
            int id = getActivity().getApplicationContext().getResources().getIdentifier(o.getString("ui"), "id", getActivity().getApplicationContext().getPackageName());

            binding.add(id,o).addRule(o.getString("field"), BPBindType.valueOf(o.getString("type")));
        }

        BPRecycleViewAdapter adapter = new BPRecycleViewAdapter();
        adapter.addLayout(R.layout.list_item);
        adapter.addCollection(collection);
        adapter.setBinding(binding);

        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(adapter);
    }

    private void executeQuery(BPObject query){
        BPApi api = new BPApi();
        //api.query();
        api.setCallback(new BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {
                binding.draw();
            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {

            }

            @Override
            public void json(JSONObject jsonObject){

            }

            @Override
            public void error() {

            }
        });
    }


    public void setData(BPObject data) {
        this.data = data;
    }

    private int getLayout(){
        String type = data.getString("type");

        if(type.equalsIgnoreCase("pager")){
            return R.layout.pager;
        } else if(type.equalsIgnoreCase("sidebar")){
            return R.layout.sidebar;
        } else if(type.equalsIgnoreCase("tabs_toolbar")){
            return R.layout.tabs_toolbar;
        } else if(type.equalsIgnoreCase("tabs")){
            return R.layout.tabs;
        } else if(type.equalsIgnoreCase("list")){
            return R.layout.list;
        } else if(type.equalsIgnoreCase("form")){
            return R.layout.components;
        } else {
            return R.layout.blank;
        }
    }

    private void bindTabs(View view){

        if(!data.getString("type").equalsIgnoreCase("tabs") && !data.getString("type").equalsIgnoreCase("tabs_toolbar")) {
            return;
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.bp_tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.bp_pager);
        BPPagerAdapter adapter  = new BPPagerAdapter(getChildFragmentManager());

        tabLayout.setTabTextColors(Color.CYAN,Color.CYAN);

        BPCollection subviews = data.getBPCollection("subviews");

        for(int i = 0 ; i < subviews.size();i++){
            BPObject object = subviews.get(i);
            tabLayout.addTab(tabLayout.newTab().setText(object.getString("title")));
            adapter.addFragment(BPFragment.newInstance(BPAppBuilder.getView(object.getString("name"))));
        }

        pager.setAdapter(adapter);
    }

    private void bindComponents(View view){

        if(!data.getString("type").equalsIgnoreCase("form")){
            return;
        }

        BPCollection components = data.getBPCollection("components");

        LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0 ; i < components.size() ; i++) {
            BPObject o = components.get(i);

            String type = o.getString("type");

            View v = inflater.inflate(getComponentLayout(type), null);
            LinearLayout content = (LinearLayout) view.findViewById(R.id.bp_content_components);

            setAllViewId(v, o);

            content.addView(v);
        }


    }

    private void setAllViewId(View v, BPObject o){

        if(o.getString("type").equalsIgnoreCase("text_input")){

            int viewBackgroundId = View.generateViewId();
            int viewTextInputLayoutId = View.generateViewId();
            int viewEditTextId = View.generateViewId();

            View textinputlayout = v.findViewWithTag("textinputlayout");
            View edittext = v.findViewWithTag("edittext");

            v.setId(viewBackgroundId);
            textinputlayout.setId(viewTextInputLayoutId);
            edittext.setId(viewEditTextId);

            BPAppBuilder.getInstance().addUIID(o.getString("name") + "_background", viewBackgroundId);
            BPAppBuilder.getInstance().addUIID(o.getString("name")+"_textinputlayout",viewTextInputLayoutId);
            BPAppBuilder.getInstance().addUIID(o.getString("name")+"_edittext",viewEditTextId);

        } else if(o.getString("type").equalsIgnoreCase("one_button")){
            int viewBackgroundId = View.generateViewId();
            int viewButtonId = View.generateViewId();

            View button = v.findViewWithTag("button");

            v.setId(viewBackgroundId);
            button.setId(viewButtonId);

            BPAppBuilder.getInstance().addUIID(o.getString("name")+"_background",viewBackgroundId);
            BPAppBuilder.getInstance().addUIID(o.getString("name") + "_button", viewButtonId);
        }
    }

    private int getComponentLayout(String type){

        if(type.equalsIgnoreCase("image")){
           return R.layout.bp_comp_img_logo;
        } else  if(type.equalsIgnoreCase("check_input")){
            return R.layout.bp_comp_checkinput;
        } else  if(type.equalsIgnoreCase("date_input")){
            return R.layout.bp_comp_dateinput;
        }else  if(type.equalsIgnoreCase("text_input")){
            return R.layout.bp_comp_textinput;
        }else  if(type.equalsIgnoreCase("one_button")){
            return R.layout.bp_comp_button;
        }

        return 0;
    }

    private void bindSidebar(View view) {

        if (!data.getString("type").equalsIgnoreCase("sidebar")) {
            return;
        }

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.sidebar_container, BPFragment.newInstance(BPAppBuilder.getView(data.getString("embedView"))))
                .commit();
    }


    @Override
    public void onEvent(BPEvent event) {

        BPObject data = event.getData();
        final BPObject action = data.getBPObject("action");

        BPObject schema = this.data.getBPObject("api").getBPObject("schema");
        BPCollection fieldSchema = schema.getBPCollection(action.getString("name"));
        BPForm form = new BPForm(this.getActivity());
        form.add(fieldSchema, binding);


        if(action.getString("type").equalsIgnoreCase("api")){

            BPApi apiAction = new BPApi();
            //apiAction.queryAction(BPAppBuilder.getApiAction(action.getString("name")));
            apiAction.setCallback(new BPApiCallback() {
                @Override
                public void error(){}
                @Override
                public void queryDone(BPCollection collection) {

                }

                @Override
                public void actionDone(BPObject object, BPObject responseConfig) {


                    String field_type = responseConfig.getString("field_type");

                    if(field_type.equalsIgnoreCase("integer")){

                        //  integer
                        int field =  object.getInt(responseConfig.getString("field"));

                        /*
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        */

                    } else if(field_type.equalsIgnoreCase("string")){

                        //  string

                    }

                }

                @Override
                public void json(JSONObject jsonObject){

                }
            });
        }

        /*
        BPObject action = event.getData().getBPObject("action");
        String view = action.getString("view");

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.sidebar_container, BPFragment.newInstance(BPAppBuilder.getView(view)))
                .commit();
        */
    }
}
