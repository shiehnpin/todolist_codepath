package com.codepath.simpletodo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<TodoItem> itemsAdapter;
    private ArrayList<TodoItem> items;
    private ListView lvItems;
    private TodoItemDAO todoItemDAO;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoItemDAO = new TodoItemDAO(getApplicationContext());
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        readItems();
        itemsAdapter = new CustomArrayAdapter(this,R.layout.item_todo_item,items);
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


    private class CustomArrayAdapter extends ArrayAdapter<TodoItem> {


        public CustomArrayAdapter(Context context, int resource, List<TodoItem> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TodoItem item = getItem(position);
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_todo_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.txItemTitle);
                viewHolder.dueDate = (TextView) convertView.findViewById(R.id.txItemDueDate);
                viewHolder.priority = (TextView) convertView.findViewById(R.id.txItemPriority);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(item.getTitle());
            if(item.getDueDate()!=null && item.getDueDate().getTime() != Constant.UNSET) {
                viewHolder.dueDate.setText(sdf.format(item.getDueDate()));
            }else{
                viewHolder.dueDate.setText("");
            }
            if(item.getPriority()!=Constant.UNSET) {
                viewHolder.priority.setText(new String[]{"Low","Med","High"}[item.getPriority()-1]);
            }else{
                viewHolder.priority.setText("");

            }
            return convertView;

        }

        private class ViewHolder {
            TextView title;
            TextView dueDate;
            TextView priority;
        }

    }
}
