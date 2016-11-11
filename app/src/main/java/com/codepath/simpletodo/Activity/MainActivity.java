package com.codepath.simpletodo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.simpletodo.Constant;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.TodoItem;
import com.codepath.simpletodo.model.TodoItemDAO;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<TodoItem> itemsAdapter;
    private ArrayList<TodoItem> items;
    private ListView lvItems;
    private TodoItemDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoItemDAO = new TodoItemDAO(getApplicationContext());
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<TodoItem>(this, android.R.layout.simple_list_item_1, items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position,convertView,parent);
                ((TextView) view.findViewById(android.R.id.text1)).setText(items.get(position).getTitle());
                return view;
            }
        };
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();

    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeItem(items.remove(position));
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditItemActivity.editItem(MainActivity.this,items.get(position),position);
            }
        });
    }



    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        if(TextUtils.isEmpty(etNewItem.getText())){
            return;
        }
        TodoItem item = new TodoItem(etNewItem.getText().toString());
        itemsAdapter.add(item);
        addTodoItem(item);
        etNewItem.setText("");
    }



    private void readItems(){
        items.addAll(todoItemDAO.getAll());
    }

    private void addTodoItem(TodoItem insertItem) {
        todoItemDAO.insert(insertItem);
    }

    private void removeItem(TodoItem removeItem) {
        todoItemDAO.delete(removeItem.getId());
    }

    private void updateItem(TodoItem item) {
        todoItemDAO.update(item);
    }

    @Override
    public void onDestroy(){
        todoItemDAO.close();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.ACTION_EDIT_ITEM){
            if(resultCode == RESULT_OK){
                int pos = data.getIntExtra(Constant.KEY_ITEM_POS,-1);
                TodoItem item = data.getParcelableExtra(Constant.KEY_ITEM);
                items.set(pos,item);
                itemsAdapter.notifyDataSetChanged();
                updateItem(item);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
