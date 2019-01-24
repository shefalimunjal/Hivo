package edu.sjsu.hivo.ui.propertydetail;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.adapter.CustomPagerAdapter;
import edu.sjsu.hivo.events.DetailActivityData;

public class PropertyImages extends AppCompatActivity{
    private static final String POSITION_KEY = "POSITION";
    ViewPager viewPager;
    CustomPagerAdapter adapter;
    ArrayList<Integer> houseImages;
    int position;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_detail_images);
        viewPager = (ViewPager)findViewById(R.id.detail_viewpager_only);
        houseImages = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        DetailActivityData data = EventBus.getDefault().getStickyEvent(DetailActivityData.class);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(POSITION_KEY, 0);
        } else {
            position = extras.getInt(POSITION_KEY, 0);
        }

        adapter = new CustomPagerAdapter(this, data.getProperty().getUrls());
        viewPager.setCurrentItem(position, false);


        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(POSITION_KEY, viewPager.getCurrentItem());
    }
}