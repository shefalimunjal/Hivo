package edu.sjsu.hivo.ui.propertydetail ;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.adapter.CustomPagerAdapter;
import edu.sjsu.hivo.model.Property;

public class ViewPagerViewHolder extends PropertyViewHolder {

    ViewPager viewPager;
    Context context;
    CustomPagerAdapter adapter;
    View view;
    public ViewPagerViewHolder(Context context, View view) {
        super(view);
        this.view = view;
        this.context = context;
        viewPager = view.findViewById(R.id.detail_viewpager);
        adapter = new CustomPagerAdapter(context);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void bindProperty(Property property) {
        if (!adapter.hasImages()) {
            adapter.setImages(property.getUrls());
            adapter.notifyDataSetChanged();
        }
    }
}
