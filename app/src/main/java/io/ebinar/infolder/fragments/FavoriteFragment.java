package io.ebinar.infolder.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;


import org.json.JSONObject;

import io.ebinar.infolder.R;
import io.ebinar.infolder.activities.MediaDetailActivity;
import io.ebinar.infolder.api.MTApi;


public class FavoriteFragment extends Fragment {

    private static int MESSAGE = 0;
    private static int SWIPE_MESSAGE = 1;
    private static int MESSAGE_OK = 2;

    private BPCollection collection;
    private RelativeLayout empty_content;
    private Button done_button;
    private TextView favorite_message;
    private ImageView favorite_message_icon;
    private int typeMessage = 0;
    private BPRecycleViewAdapter adapter;
    private  RecyclerView listView;
    private RelativeLayout loading;


    public FavoriteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        empty_content = (RelativeLayout) view.findViewById(R.id.empty_content);
        done_button = (Button) view.findViewById(R.id.done_button);
        favorite_message = (TextView) view.findViewById(R.id.favorite_message);
        favorite_message_icon = (ImageView) view.findViewById(R.id.favorite_message_icon);
        listView = (RecyclerView) view.findViewById(R.id.listView);
        loading = (RelativeLayout) view.findViewById(R.id.loading);

        collection = new BPCollection();



        if(collection.size() > 0){
            empty_content.setVisibility(View.GONE);
        }else{
            empty_content.setVisibility(View.VISIBLE);
        }

        BPUIBinding binding = new BPUIBinding();

        binding.add(R.id.feed_thumb)
                .addRule("cover_url", BPBindType.IMAGE);

        binding.add(R.id.video_description)
                .addRule("description",BPBindType.TEXT);

        binding.add(R.id.date)
                .addRule("updatedAt",BPBindType.TEXT);

        binding.add(R.id.brand_name)
                .addRule("brand",BPBindType.TEXT);


        adapter = new BPRecycleViewAdapter();
        adapter.addCollection(collection);
        adapter.addLayout(R.layout.item_favorite);
        adapter.setBinding(binding);



        /*
        BPArrayResource arrayResourceIconsMediaType = new BPArrayResource(getActivity());
        arrayResourceIconsMediaType.put("video",R.drawable.ic_video);
        arrayResourceIconsMediaType.put("audio",R.drawable.ic_audio);
        arrayResourceIconsMediaType.put("image",R.drawable.ic_type_media_image);

        adapter.addBinding("image", R.id.brand_image, BPUserInterface.IMAGE);
        adapter.addBinding("thumb",R.id.feed_thumb, BPUserInterface.IMAGE);

        adapter.addBinding("text",R.id.video_description,BPUserInterface.TEXT);
        adapter.addBinding("name",R.id.brand_name,BPUserInterface.TEXT);
        adapter.addBinding("type", R.id.play_icon, BPUserInterface.IMAGE_FROM_ARRAY_RESOURCE, arrayResourceIconsMediaType, null);
        */

        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(adapter);


        AppCompatActivity activity = (AppCompatActivity) this.getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (toolbar != null) {

            activity.setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
        }



        //((MainActivity) activity).instanteToolbar(getResources().getString(R.string.fav_title));




        validateEmptyContent();

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeMessage == MESSAGE) {
                    favoriteMessageDone("favorite_message_done");
                } else if (typeMessage == SWIPE_MESSAGE) {
                    favoriteMessageDone("favorite_swipe_message_done");
                }

                validateEmptyContent();
            }
        });

        adapter.setOnItemClickListener(new BPRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BPObject viewModel) {

                Intent intent = new Intent(getContext(), MediaDetailActivity.class);
                intent.putExtra("video",viewModel);
                startActivity(intent);
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

        setUpRecyclerView();

        MTApi api = new MTApi();

        api.getFavorites(new BPApi.BPApiCallback() {
            @Override
            public void queryDone(BPCollection collection) {

            }

            @Override
            public void actionDone(BPObject object, BPObject responseConfig) {
                BPCollection collection = object.getBPCollection("elements");

                adapter.addCollection(collection);
                adapter.notifyDataSetChanged();

                loading.setVisibility(View.GONE);

                if(collection.size() > 0){
                    empty_content.setVisibility(View.GONE);
                }else{
                    empty_content.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void json(JSONObject json) {

            }

            @Override
            public void error() {

            }
        });

        return view;
    }

    private void validateEmptyContent(){
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);


        boolean favorite_message_done = settings.getBoolean("favorite_message_done", false);
        boolean favorite_swipe_message_done = settings.getBoolean("favorite_swipe_message_done", false);


        if(!favorite_message_done){

            empty_content.setVisibility(View.VISIBLE);
            favorite_message_icon.setImageDrawable(getResources().getDrawable(R.mipmap.favorite_message));
            favorite_message.setText(getResources().getString(R.string.favorite_explain));

            typeMessage = MESSAGE;

        }else if(!favorite_swipe_message_done){

            empty_content.setVisibility(View.VISIBLE);
            favorite_message_icon.setImageDrawable(getResources().getDrawable(R.mipmap.favorite_swipe_message));
            favorite_message.setText(getResources().getString(R.string.favorite_swipe_explain));

            typeMessage = SWIPE_MESSAGE;

        } else{

            typeMessage = MESSAGE_OK;

            if(adapter.getCollection().size() > 0){
                empty_content.setVisibility(View.GONE);
            }else{
                empty_content.setVisibility(View.VISIBLE);
                done_button.setVisibility(View.GONE);
            }

            favorite_message_icon.setImageDrawable(getResources().getDrawable(R.mipmap.big_star_fav));
            favorite_message.setText(getResources().getString(R.string.fav_empty));
        }


    }


    private void favoriteMessageDone(String pref){
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(pref, true);
        editor.apply();
    }

    @Override
    public void onResume(){
        super.onResume();

        validateEmptyContent();
    }

    /**/
    private void setUpRecyclerView() {

        listView.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(getActivity(),R.mipmap.ic_delete);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = 20;
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();



                /*
                TestAdapter testAdapter = (TestAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }*/
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();

                BPObject o = adapter.getCollection().get(swipedPosition)      ;

                MTApi api = new MTApi();
                api.setFav(o.getString("objectId"), false, new BPApi.BPApiCallback() {
                    @Override
                    public void queryDone(BPCollection collection) {

                    }

                    @Override
                    public void actionDone(BPObject object, BPObject responseConfig) {

                    }

                    @Override
                    public void json(JSONObject json) {

                    }

                    @Override
                    public void error() {

                    }
                });

                adapter.getCollection().remove(swipedPosition);
                adapter.notifyDataSetChanged();
                validateEmptyContent();


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);


                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(listView);
    }

    private void setUpAnimationDecoratorHelper() {
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
}
