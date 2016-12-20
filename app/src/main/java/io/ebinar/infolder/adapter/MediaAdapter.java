package io.ebinar.infolder.adapter;

import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Parcel;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import io.ebinar.infolder.R;

/**
 * Created by jorgeacostaalvarado on 30-07-15.
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> implements View.OnClickListener {


    private BPCollection datasource;
    private OnItemClickListener onItemClickListener;

    public double total = 0.0;

    public MediaAdapter(BPCollection datasource, BPSqlQuery query, BPObject o) {
        this.datasource = datasource;
        this.datasource.dataFromSQLQuery(query);
        this.datasource.add(0,o);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_detail_header, parent, false);

            return new ViewHolder(v);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_historial_item, parent, false);
            v.setOnClickListener(this);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BPObject item = null;


        if(position == 0){
            item = datasource.get(position);
            TextView title = (TextView)  holder.itemView.findViewById(R.id.title);
            title.setText(item.getString("description"));
        }else{

            if(position < this.getItemCount() - 1) {
                item = datasource.get(position);
                TextView title = (TextView) holder.itemView.findViewById(R.id.support);
                TextView hour = (TextView) holder.itemView.findViewById(R.id.hour);
                TextView price = (TextView) holder.itemView.findViewById(R.id.price);
                TextView date = (TextView) holder.itemView.findViewById(R.id.date);
                View view = holder.itemView.findViewById(R.id.hour_content);

                String DateAppear = item.getString("DateAppear");

                title.setText(item.getString("Support"));
                date.setText(item.getString("Schedule"));
                price.setText(item.getString("Value"));
                hour.setText(DateAppear);

                title.setVisibility(View.VISIBLE);
                hour.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);

                if (DateAppear.equalsIgnoreCase("00:00:00")) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }

            } else {

                TextView title = (TextView) holder.itemView.findViewById(R.id.support);
                TextView hour = (TextView) holder.itemView.findViewById(R.id.hour);
                TextView date = (TextView) holder.itemView.findViewById(R.id.date);
                TextView price = (TextView) holder.itemView.findViewById(R.id.price);

                title.setVisibility(View.GONE);
                hour.setVisibility(View.GONE);
                date.setVisibility(View.GONE);

                //DecimalFormat dFormat = new DecimalFormat("####,###,###");

                //String totalString = "$" + dFormat.format(total);

                //price.setText(totalString);

                String finalTotalString = "";
                String totalString = String.valueOf(((int) total));


                String[] components = new StringBuilder(totalString).reverse().toString().split("");

                //06578811;


                for(int i = 0 ; i < components.length  ;i++){


                    finalTotalString += components[i];
                    if((i)%3 == 0 && i!=0){
                        finalTotalString +=".";
                    }

                }

                finalTotalString = new StringBuilder(finalTotalString).reverse().toString();
                finalTotalString = "$" + finalTotalString;
                finalTotalString = finalTotalString.replace("$.","$");
                price.setText(finalTotalString);
                price.setTextSize(12);

            }

        }

        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return datasource.size() + 1;
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    //onItemClickListener.onItemClick(v, 0l);
                }
            }, 200);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 0;
        }
        return 1;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }

    public void refresh(){
        BPObject o = datasource.get(0);
        datasource.refresh();
        this.datasource.add(0,o);
        notifyDataSetChanged();
    }



    public interface OnItemClickListener {

        void onItemClick(View view, BPObject viewModel);

    }
}
