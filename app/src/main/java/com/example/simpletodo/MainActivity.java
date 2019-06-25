package com.example.simpletodo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();

    }

    private void setupListViewListener() {
        Log.i("MainActivity", "Setting up listener on list view" );
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("MainActivity","Item removed from list" + position);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
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
            Log.e("MainActivity", "Error reading file", e);
            items = new ArrayList<>();

        }
    }

    private void writeItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing file", e);
        }
    }

}
