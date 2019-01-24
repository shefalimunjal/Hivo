package edu.sjsu.hivo.ui.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sjsu.hivo.ui.SortActivity;

public class SortUtility {

    private static final int PICK_SORT_REQUEST = 2;  // The request code


    private Activity listOrMapActivity;

    public SortUtility(Activity activity) {
        listOrMapActivity = activity;
    }

    public void setSortListener(ImageView sortImg,
                                TextView sortText) {
        sortImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent sortIntent = new Intent(context, SortActivity.class);
                listOrMapActivity.startActivityForResult(sortIntent, PICK_SORT_REQUEST);
            }
        });

        sortText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent filterIntent = new Intent(context, SortActivity.class);
                listOrMapActivity.startActivityForResult(filterIntent, PICK_SORT_REQUEST);
            }
        });
    }


    public static String applySortData(Intent data, String extension) {
        if (data == null || TextUtils.isEmpty(extension)) {
            return extension;
        }
        extension += "&sortv=" + data.getStringExtra("SORT_OPTION") + "&sort_by=" + data.getStringExtra("SORT_ORDER");;
        return extension;

    }
}
