package io.ebinar.infolder.section;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private Context ctx;
    private BPCollection collection;

    public SectionPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        ctx = context;

        collection = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Filter");
        query.setOrderBy("id");
        collection.dataFromSQLQuery(query);
    }

    @Override
    public Fragment getItem(int position) {
        ListFragment feed = new ListFragment();
        return feed;
    }

    @Override
    public int getCount() {
        return collection.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        BPObject filter = collection.get(position);
        return filter.getString("name");
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

}
