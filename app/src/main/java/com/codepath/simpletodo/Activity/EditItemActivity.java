package com.codepath.simpletodo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.simpletodo.Constant;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.TodoItem;

public class EditItemActivity extends AppCompatActivity {

    private EditText etItemText;
    private Button btnSaveItem;
    private TodoItem todoItem;


    public static void editItem(Activity activity, TodoItem item, int pos) {
        Intent editIntent = new Intent(activity,EditItemActivity.class);
        editIntent.putExtra(Constant.KEY_ITEM,item);
        editIntent.putExtra(Constant.KEY_ITEM_POS,pos);
        activity.startActivityForResult(editIntent,Constant.ACTION_EDIT_ITEM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etItemText = (EditText) findViewById(R.id.etItemText);
        btnSaveItem = (Button) findViewById(R.id.btnSaveItem);

        if(getIntent()!=null &&
                getIntent().hasExtra(Constant.KEY_ITEM) &&
                getIntent().hasExtra(Constant.KEY_ITEM_POS)){
            todoItem = getIntent().getParcelableExtra(Constant.KEY_ITEM);
            etItemText.setText(todoItem.getTitle());
            etItemText.setTag(getIntent().getIntExtra(Constant.KEY_ITEM_POS,-1));
        }

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etItemText.getText().length()!=0 && ((Integer)etItemText.getTag())!= -1 && todoItem!=null){
                    todoItem.setTitle(etItemText.getText().toString());
                    Intent resIntent = new Intent();
                    resIntent.putExtra(Constant.KEY_ITEM,todoItem);
                    resIntent.putExtra(Constant.KEY_ITEM_POS, (Integer) etItemText.getTag());
                    setResult(RESULT_OK,resIntent);
                    finish();
                }
            }
        });

    }
}
