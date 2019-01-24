package edu.sjsu.hivo.ui.propertydetail;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.adapter.PropertyListAdapter;
import edu.sjsu.hivo.model.Property;

public class PropertyDetailAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Property property;
    private final Context context;
    String TAG = PropertyListAdapter.class.getSimpleName();

    enum PropertyViewType {
        IMAGE(0),
        TEXT(1),
        MAP(2);

        private final int viewType;
        PropertyViewType (int viewType) {
            this.viewType = viewType;
        }

        static PropertyViewType fromInt(int viewType) {
            if (viewType == 0) {
                return IMAGE;
            } else if (viewType == 1){
                return TEXT;
            } else {
                return MAP;
            }
        }
    }

    public PropertyDetailAdapter(Context context, Property property) {
        this.property = property;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        Log.i(TAG, "INTO ADATPER");
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PropertyViewType propertyViewType = PropertyViewType.fromInt(viewType);
        android.view.View view = null;
        switch (propertyViewType) {
            case IMAGE:
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.property_images, parent, false);
                return new ViewPagerViewHolder(context, view);
            case TEXT:
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.property_description_list_item, parent, false);
                return new KeyDetailsViewHolder(view);
            case MAP:
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.detail_page_mapview, parent, false);
                return new MapViewHolder(view, context);
        }
        return null;

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getItemCount() {
        return PropertyViewType.values().length;
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder viewHolder, int pos) {
        Log.i(TAG,"getting property object"+ property.getPrice());
        viewHolder.bindProperty(property);
    }
}