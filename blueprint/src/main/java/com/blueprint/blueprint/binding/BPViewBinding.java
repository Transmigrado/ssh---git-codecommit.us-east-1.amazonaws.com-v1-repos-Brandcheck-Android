package com.blueprint.blueprint.binding;

    import android.app.Activity;
    import android.graphics.Bitmap;

    import android.support.design.widget.NavigationView;
    import android.support.design.widget.TextInputLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;

    import android.util.Log;
    import android.view.MenuItem;
    import android.view.View;

    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import com.blueprint.blueprint.event.BPEvent;
    import com.blueprint.blueprint.object.BPCollection;
    import com.blueprint.blueprint.object.BPIObject;
    import com.blueprint.blueprint.object.BPObject;
    import com.blueprint.blueprint.object.BPObjectUtil;
    import com.blueprint.blueprint.util.BPStyle;
    import com.blueprint.blueprint.util.ImageUtil;

    import com.nostra13.universalimageloader.core.assist.FailReason;
    import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

    import java.util.ArrayList;
    import java.util.Observable;
    import java.util.Observer;

    /**
     * Creado por Jorge Acosta Alvarado on 05-08-15.
     */

public class BPViewBinding implements Observer {

    private View view;
    private Object parent = null;
    private int objectId;
    private ArrayList<BPRulePair> bindingType;
    private BPObject data = new BPObject();
    private BPEvent.BPOnEvent callbackEvent;
    private BPObject info;

    public BPViewBinding(View view, BPObject info){
        this.view = view;

        bindingType = new ArrayList<>();
        this.info = info;
    }

    public BPViewBinding(int objectId, BPObject info){
        this.objectId = objectId;
        bindingType = new ArrayList<>();
        this.info = info;
    }

    public void setData(BPIObject data){
        this.data = (BPObject) data;
        ((BPObject) data).addObserver(this);
        if(view!=null) {
            view.setTag(data);
        }

    }

    private void createView(){

        if(parent == null) {
            return;
        }

        if(parent instanceof View){
            view = ((View) parent).findViewById(objectId);
        }else if(parent instanceof AppCompatActivity){
            view = ((AppCompatActivity) parent).findViewById(objectId);
        }else if(parent instanceof Activity){
            view = ((Activity) parent).findViewById(objectId);
        }

    }

    public void addParent(Object parent) {
            this.parent = parent;
            createView();
    }

    public void draw(){
        for(int i = 0 ; i < bindingType.size(); i++){
            draw(bindingType.get(i));
        }

        BPStyle.styleObject(view, info);
    }


    public BPViewBinding addRule(String field, BPBindType type){
        bindingType.add(bindingType.size(), new BPRulePair(field, type));
        return this;
    }



    private void draw(BPRulePair pair){

        switch (pair.type){
            case NULL:
            break;
            case TEXT:
                if(view instanceof TextView){

                    String value = data.getString(pair.field);

                    if(!((TextView) view).getText().toString().equalsIgnoreCase(value)){
                        ((TextView) view).setText(value);

                    }

                } else if(view instanceof Toolbar){
                    ((Toolbar) view).setTitle( data.getString(pair.field));
                }
            break;
            case TIME_AGO:

                if(view instanceof TextView){

                    String value = data.getStringTimeAgo(pair.field);

                    if(!((TextView) view).getText().toString().equalsIgnoreCase(value)){
                        ((TextView) view).setText(value);

                    }

                } else if(view instanceof Toolbar){
                    ((Toolbar) view).setTitle( data.getString(pair.field));
                }
                break;
            case HINT:
                if(view instanceof TextView){
                    ((TextView) view).setHint(data.getString(pair.field));
                } else if(view instanceof TextInputLayout){
                    ((TextInputLayout) view).setHint(data.getString(pair.field));
                }
                break;
            case PROGRESS:
                ((ProgressBar) view).setProgress(data.getInt(pair.field));
            break;
            case TOGGLE:
                if(view instanceof CheckBox){
                    ((CheckBox) view).setChecked(data.getBoolean(pair.field));
                }
            break;
            case TEXT_COLOR:
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(BPObjectUtil.getColorFromString(data.getString(pair.field)));
                }
            break;
            case HINT_COLOR:
                if (view instanceof TextView) {
                    ((TextView) view).setHintTextColor(BPObjectUtil.getColorFromString(data.getString(pair.field)));
                }
            break;
            case BACKGROUND_COLOR:
                view.setBackgroundColor(BPObjectUtil.getColorFromString(data.getString(pair.field)));
            break;

            case IMAGE:

                ImageUtil.displayImage((ImageView) view, data.getString(pair.field), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            break;
            case SHOW_GREAT_THAN:
                int value = info.getInt("value");
                int value2 = data.getInt(pair.field);

                if(value>value2){
                    view.setVisibility(View.VISIBLE);
                }else{
                    view.setVisibility(View.GONE);
                }

            break;
            case HIDE_EQUAL_BOOL:
                boolean value_bool2 = data.getBoolean(pair.field);

                if(value_bool2){
                    view.setVisibility(View.GONE);
                }else{
                    view.setVisibility(View.VISIBLE);
                }

                break;
            case SHOW_EQUAL_BOOL:
                boolean value_bool = data.getBoolean(pair.field);

                if(value_bool){
                    view.setVisibility(View.VISIBLE);
                }else{
                    view.setVisibility(View.GONE);
                }

                break;
            case SHOW_EQUAL:

                String v = info.getString("value");
                String[] values = v.split("/");
                boolean showIt = false;

                for (int i = 0; i < values.length; i++) {

                    if (values[i].equalsIgnoreCase(data.getString(pair.field))) {
                        showIt = true;
                        break;
                    }
                }
                if(showIt){
                    view.setVisibility(View.VISIBLE);
                }else{
                    view.setVisibility(View.GONE);
                }

                break;
            case HIDE_STRICT:
                view.setVisibility(View.GONE);
                break;
            case HIDE_EQUAL:
                String v2 = info.getString("value");
                String[] values2 = v2.split("/");
                boolean showIt2 = false;

                for (int i = 0; i < values2.length; i++) {
                    if (values2[i].equalsIgnoreCase(data.getString(pair.field))) {
                        showIt2 = true;
                        break;
                    }
                }

                if(showIt2){
                    view.setVisibility(View.GONE);
                }else{
                    view.setVisibility(View.VISIBLE);
                }

                break;
            case IMAGE_FROM_RESOURCE:
                int drawable =    this.view.getContext().getResources().getIdentifier( data.getString(pair.field), "mipmap",    this.view.getContext().getApplicationContext().getPackageName());
                if(view instanceof  ImageView) {
                    ((ImageView) view).setImageResource(drawable);
                }
                break;
            case LIST:
                if(view instanceof NavigationView){
                    BPCollection menu = data.getBPCollection(pair.field);
                    for(int i = 0 ; i < menu.size() ; i++){
                        final BPObject o = menu.get(i);
                        MenuItem item = ((NavigationView) view).getMenu().add(o.getString("title"));

                        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (callbackEvent != null) {
                                    BPEvent event = new BPEvent(BPEvent.Type.ON_ITEM_CLICK);
                                    event.setData(o);
                                    callbackEvent.onEvent(event);
                                }
                                return false;
                            }
                        });
                    }
                }else if(view instanceof Toolbar){
                    BPCollection menu = data.getBPCollection(pair.field);
                    for(int i = 0 ; i < menu.size() ; i++){
                        final BPObject o = menu.get(i);
                        MenuItem item = ((Toolbar) view).getMenu().add(o.getString("title"));

                        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (callbackEvent != null) {
                                    BPEvent event = new BPEvent(BPEvent.Type.ON_ITEM_CLICK);
                                    event.setData(o);
                                    callbackEvent.onEvent(event);
                                }
                                return false;
                            }
                        });
                    }
                } else if(view instanceof RecyclerView){

                }
            break;
            case EVENT:
                if(view instanceof Button || view instanceof ImageButton){

                    data.setObject("view",view);
                    data.setObject("parent",parent);
                    view.setTag(data);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BPEvent event = new BPEvent(BPEvent.Type.ON_CLICK);
                            event.setData((BPObject) v.getTag());
                            event.setObjectId(objectId);

                            event.setParent(parent);
                            callbackEvent.onEvent(event);
                        }
                    });
                }
                break;

        }

    }

    public Object getValue(){


        if(view instanceof TextView){
           return ((TextView) view).getText().toString();
        }else if(view instanceof EditText){
            return ((EditText) view).getText().toString();
        }

        return null;
    }

    public View getView(){
        return view;
    }

    @Override
    public void update(Observable observable, Object o) {
        //draw(this.in);
    }

    public void setCallbackEvent(BPEvent.BPOnEvent callbackEvent) {
        this.callbackEvent = callbackEvent;
    }


    private class BPRulePair {

        public String field;
        public BPBindType type;

        public BPRulePair(String field, BPBindType type){
            this.field = field;
            this.type = type;
        }
    }


}
