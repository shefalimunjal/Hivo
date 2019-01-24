package edu.sjsu.hivo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.sjsu.hivo.R;

public class SortActivity extends AppCompatActivity {



        private String[] sortOptions = {"price","beds","baths","sqft"};
        int selectedItem = Integer.MIN_VALUE;
        String sortOrder="1";
        String sortOptionSelected ="";

    ListView lvCheckBox;

    @Override
        public void onCreate(Bundle savedInstanceState)
        {
            final TextView btnSortOrder;
            Button applySort;

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sort);

            btnSortOrder = findViewById(R.id.sortOrder);
            applySort = findViewById(R.id.apply_sort_button);

            lvCheckBox = findViewById(R.id.lvCheckBox);
            lvCheckBox.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvCheckBox.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_multiple_choice, sortOptions));



            btnSortOrder.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    if (btnSortOrder.getText().toString().equals("Low-High")) {
                        sortOrder = "-1";
                        btnSortOrder.setText("High-Low");
                    }
                    else{
                        sortOrder= "1";
                        btnSortOrder.setText("Low-High");
                    }
                }
            });

            lvCheckBox.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long arg3)
                {
                    if(selectedItem == position)
                    {
                        lvCheckBox.setItemChecked(position,false);
                        selectedItem = Integer.MIN_VALUE;

                    }
                    else
                    {
                        selectedItem = position;

                    }
                }
            });

            applySort.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                        if(selectedItem == Integer.MIN_VALUE)
                            Toast.makeText(SortActivity.this, "Please select an Option", Toast.LENGTH_SHORT).show();
                        else {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("SORT_OPTION", sortOptions[selectedItem]);
                            resultIntent.putExtra("SORT_ORDER",sortOrder);
                            setResult(FilterActivity.RESULT_OK, resultIntent);
                            finish();
                        }
                }
            });
        }
    }
