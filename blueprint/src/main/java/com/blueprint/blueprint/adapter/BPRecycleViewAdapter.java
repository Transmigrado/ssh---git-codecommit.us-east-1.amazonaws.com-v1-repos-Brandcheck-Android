package com.blueprint.blueprint.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

/**
 * Creado por Jorge Acosta Alvarado on 04-08-15.
 */
public class BPRecycleViewAdapter extends RecyclerView.Adapter<BPViewholder> implements View.OnClickListener {

    private SparseArray<Integer> layouts;
    private SparseArray<BPCollection> collections;
    private BPUIBinding binding;
    private OnItemClickListener onItemClickListener;
    public int delay = 200;

    public BPRecycleViewAdapter(int layout){
        layouts = new SparseArray<>();
        collections = new SparseArray<>();
        layouts.put(0,layout);
    }

    public BPRecycleViewAdapter(){
        layouts = new SparseArray<>();
        collections = new SparseArray<>();
    }

    public void addLayout(int layout, int type){
        layouts.put(type, layout);
    }

    public void addLayout(int layout){
        layouts.put(0,layout);
    }

    public void addCollection(BPCollection collection, int type){
        collections.append(type, collection);
    }
    public void addCollection(BPCollection collection) {
        addCollection(collection, 0);
    }

    @Override
    public BPViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layouts.get(viewType), parent, false);
        return new BPViewholder(v,viewType);
    }

    @Override
    public void onBindViewHolder(BPViewholder bpViewholder, int position) {

        BPCollection collection = collections.get(bpViewholder.getViewType());
        int bpIndex = position - bpViewholder.getViewType();

        BPObject o =  collection.get(bpIndex);
        o.setInt("bpIndex",bpIndex);

        bpViewholder.itemView.setTag(o);

        binding.setParent(bpViewholder.itemView);
        binding.setData(o);
        binding.draw();

        bpViewholder.itemView.setOnClickListener(this);

        if(onItemClickListener != null){
            onItemClickListener.renderView(bpViewholder.itemView);
        }

    }

    public void add(BPObject object, int position, int type){
        collections.get(type).add(position, object);
        notifyItemInserted(position);
    }

    public void refresh(){

        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            collections.get(key).refresh();
        }

        notifyDataSetChanged();
    }

    public int getSizeCollection(){
        if(collections.size()>0){
            return collections.get(0).size();
        }

        return 0;
    }

    public BPCollection getCollection(){
        return  collections.get(0);
    }


    public void refreshQuery(BPSqlQuery query){
        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            collections.get(key).refreshQuery(query);
        }

        notifyDataSetChanged();
    }


    public void refreshAndApendQuery(String field){

        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            collections.get(key).refreshAndApendQuery();
            collections.get(key).sortBy(field);
        }

        notifyDataSetChanged();
    }

    public boolean appendFromLastObject(String field){

        boolean haveNew = false;

        int oldcount = 0;
        int newcount = 0;

        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            oldcount += collections.get(key).size();
        }

        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            collections.get(key).appendFromLastObject(field);
            newcount += collections.get(key).size();
        }

        if(newcount>oldcount){
            haveNew = true;
            notifyDataSetChanged();
        }



        return haveNew;
    }

    public void add(BPObject object, int position){
        add(object, position, 0);
    }

    public void setBinding(BPUIBinding binding) {
        this.binding = binding;
    }

    @Override
    public int getItemViewType(int position) {

        int count = 0;
        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            count += collections.get(key).size();

            if(position < count){
                return i;
            }
        }

        return 0;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for(int i = 0; i < collections.size(); i++) {
            int key = collections.keyAt(i);
            count += collections.get(key).size();
        }

        return count;
    }

    @Override
    public void onClick(final View v) {
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    onItemClickListener.onItemClick(v,(BPObject) v.getTag());
                }
            }, delay);
        }
    }

    public Object getItem(int position, int collection) {
        return  collections.get(collection).get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, BPObject viewModel);
        void renderView(View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
