package io.ebinar.infolder.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blueprint.blueprint.adapter.BPPagerAdapter;
import com.blueprint.blueprint.adapter.BPRecycleViewAdapter;
import com.blueprint.blueprint.binding.BPBindType;
import com.blueprint.blueprint.binding.BPUIBinding;
import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.event.BPEvent;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.ebinar.infolder.R;
import io.ebinar.infolder.api.MTApi;
import io.ebinar.infolder.event.SearchEvent;
import io.ebinar.infolder.fragments.MediaListFragment;
import io.ebinar.infolder.section.SectionPagerAdapter;
import io.ebinar.infolder.utils.AlertManager;

public class SearchActivity extends AppCompatActivity implements BPEvent.BPOnEvent, CalendarDatePickerDialogFragment.OnDateSetListener {

    private BPRecycleViewAdapter adapter;
    private Date to = new Date();
    private Date from = new Date();
    private BPUIBinding mybinding;
    private EditText search_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);

        from = new Date(to.getYear(),to.getMonth() - 1,to.getDate());

        adapter = new BPRecycleViewAdapter();


        search_text = (EditText) findViewById(R.id.search_text);


        mybinding = new BPUIBinding(this);

        mybinding.setCallbackEvent(this);

        mybinding.add(R.id.from)
                .addRule("",BPBindType.EVENT);

        mybinding.add(R.id.to)
                .addRule("",BPBindType.EVENT);

        mybinding.draw();


        ((TextView)mybinding.get(R.id.from).getView()).setText("Ninguno");
        ((TextView)mybinding.get(R.id.to).getView()).setText("Hoy");

    }

    private void filter(String s){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);

        if(to.getTime()<from.getTime()){
            AlertManager.message(this,"Problema","La fecha \"desde\" debe ser menor que la fecha \"hasta\"");
           return;
        }


        String toISO = df.format(to);
        String fromISO = df.format(from);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        Intent intent = new Intent(this, SearchFilterActivity.class);
        intent.putExtra("from", fromISO);
        intent.putExtra("to", toISO);
        intent.putExtra("filter",s);
        startActivity(intent);
        finish();


    }

    @Override
    public void onEvent(BPEvent event) {

        Date now = new Date();


        MonthAdapter.CalendarDay calendarDay = new MonthAdapter.CalendarDay();
        calendarDay.setDay(now.getYear()+1900, 0, 1);

        if(event.getObjectId() == R.id.to){
            /**/
            CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setOnDateSetListener(this)
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setPreselectedDate(now.getYear() + 1900, now.getMonth(), now.getDate())
                    .setDateRange(calendarDay, null)
                    .setDoneText("Agregar")
                    .setCancelText("Cancelar");

            cdp.show(getSupportFragmentManager(), "PICKER_TO");

        } else if(event.getObjectId() == R.id.from){

            CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setOnDateSetListener(this)
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setPreselectedDate(now.getYear() + 1900, now.getMonth() - 1, now.getDate())
                    .setDateRange(calendarDay, null)
                    .setDoneText("Agregar")
                    .setCancelText("Cancelar");

            cdp.show(getSupportFragmentManager(), "PICKER_FROM");
        }

    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String date = String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear + 1)+"/"+String.valueOf(year);
        Date d;
        try{
            d = sdf.parse(date);
        }catch (Exception e){
            d = new Date();
        }


        if(dialog.getTag().equalsIgnoreCase("PICKER_FROM")){
            from = d;
            ((TextView)mybinding.get(R.id.from).getView()).setText(date);

        } else if(dialog.getTag().equalsIgnoreCase("PICKER_TO")){
            to = d;
            ((TextView)mybinding.get(R.id.to).getView()).setText(date);
        }


    }
    /**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_ok_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.action_search) {
            filter(search_text.getText().toString());

        }else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
