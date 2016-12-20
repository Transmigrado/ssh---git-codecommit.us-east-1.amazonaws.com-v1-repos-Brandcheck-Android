package io.ebinar.infolder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;

import io.ebinar.infolder.R;

public class SearchBrandActivity extends AppCompatActivity {
    private BPRecycleViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_brand);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Buscar marca");
        toolbar.setTitleTextColor(Color.WHITE);


        BPCollection collection = new BPCollection();
        BPSqlQuery query = new BPSqlQuery("Brand");
        query.setOrderBy("name");
        query.setGroupBy("name");
        collection.dataFromSQLQuery(query);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);

        adapter = new BPRecycleViewAdapter();
        adapter.addCollection(collection);
        adapter.addLayout(R.layout.item_brand_list);


        BPUIBinding binding = new BPUIBinding();

        binding.add(R.id.brand_name)
                .addRule("name", BPBindType.TEXT);

        binding.add(R.id.brand_image)
                .addRule("cover",BPBindType.IMAGE);

        adapter.setBinding(binding);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        LinearLayout filter_date = (LinearLayout) findViewById(R.id.date_picker);
        filter_date.setVisibility(View.GONE);

        EditText search_text = (EditText) findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                BPSqlQuery query = new BPSqlQuery("Brand");
                query.setGroupBy("objectId");
                query.where().likeTo("name","%" + s.toString().toUpperCase() + "%");
                adapter.getCollection().resetQuery(query);
                adapter.refresh();
            }
        });

        adapter.setOnItemClickListener(new BPRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BPObject viewModel) {
                Intent intent = new Intent(SearchBrandActivity.this, BrandDetailActivity.class);
                intent.putExtra("data", viewModel);

                startActivity(intent);
            }

            @Override
            public void renderView(View view) {

            }
        });


    }
}
