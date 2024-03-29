package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // a numeric code to identify the edit activity
    public final static int EDIT_REQUEST_CODE = 20;
    // keys used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";


    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        // arguments for ArrayAdapter constructor: activity, type of item it will wrap (using android.r class), list we just created named items
        lvItems = (ListView) findViewById(R.id.lvItems);
        // accepts integer id and reference via R.id.blank, also need to cast output of findViewById results to ListView
        // unqualified 'R' class generated at compile time!!
        lvItems.setAdapter(itemsAdapter);

        //sample data
        //items.add("Item One");
        //items.add("Item Two");

        setupListViewListener();
    }


    public void onAddItem(View v) {
        EditText etNewItem = findViewById(R.id.editText);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
        Toast.makeText(getApplicationContext(), "item added to list", Toast.LENGTH_SHORT).show();

    }

    private void setupListViewListener() {
        Log.i("MainActivity", "setting up listener on list view" );
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("MainActivity","item removed from list" + position);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });



        // set up item listener for edit (regular click not long as above)
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // create the new activity, referring to instance via .this and .class refers to type in Java (Intent will create unmade instance)
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                // pass the data being edited
                i.putExtra(ITEM_TEXT, items.get(position));
                i.putExtra(ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i, EDIT_REQUEST_CODE);

            }
        });
    }

    private File getDataFile(){
        return new File(getFilesDir(), "simpletodo.txt");
    }

    private void readItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "error reading file", e);
            items = new ArrayList<>();

        }
    }

    private void writeItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "error writing file", e);
        }
    }

    // handle results from edit activity


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity completed ok
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE){
            // extract updated item text from result intent extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            // extract original position of edited item
            int position = data.getExtras().getInt(ITEM_POSITION);
            // update the model with the new item text at the edited position
            items.set(position, updatedItem);
            // notify the adapter that the model changed
            itemsAdapter.notifyDataSetChanged();
            // persist the changed model
            writeItems();
            // notify the user the operation completed ok
            Toast.makeText(this, "item updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
