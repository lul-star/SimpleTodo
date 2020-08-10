package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final  int EDIT_TEXT_CODE = 20;
    List<String> items;

    Button btnadd;
    EditText etitem;
    RecyclerView rvItems;
    itemsadapter itemsadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnadd = findViewById(R.id.btnadd);
        etitem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loaditems();

        itemsadapter.OnLongClickListener onLongClickListener = new itemsadapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemsadapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveitems();
            }
        };

        itemsadapter. OnClickListener onClickListener = new itemsadapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position" + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items .get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        itemsadapter = new itemsadapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsadapter);
        rvItems.setLayoutManager(new LinearLayoutManager( this));

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etitem.getText().toString();
                items.add(todoItem);
                itemsadapter.notifyItemInserted(items.size() - 1);
                etitem.setText((""));
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveitems();
            }
        });


    }

    private void loaditems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDatafile(), Charset.defaultCharset()));
        } catch (IOException e){
            Log.e("MainActivity", "Error reading item", e);
            items = new ArrayList<>();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE)
        {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position, itemText);
            itemsadapter.notifyItemChanged(position);
            saveitems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDatafile() {
        return new File(getFilesDir(), "data.txt");
    }

    private void saveitems(){
        try{
            FileUtils.writeLines(getDatafile(), items);
        } catch (IOException e){
            Log.e(  "MainActivity", "Error writing items", e);
        }

    }
}