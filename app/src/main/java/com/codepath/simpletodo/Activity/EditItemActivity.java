package com.codepath.simpletodo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.simpletodo.Constant;
import com.codepath.simpletodo.R;
import com.codepath.simpletodo.TodoItem;

import java.util.Calendar;
import java.util.Date;


public class EditItemActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etItemTitle;
    private Button btnSaveItem;
    private TodoItem todoItem;
    private EditText etItemContent;
    private DatePicker dueDatePicker;
    private SeekBar seekBarPriority;
    private TextView txPriorityHint;
    private Switch swDate;
    private Switch swPriority;
    private Calendar calendar;


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

        etItemTitle = (EditText) findViewById(R.id.etItemTitle);
        etItemContent = (EditText) findViewById(R.id.etContent);
        btnSaveItem = (Button) findViewById(R.id.btnSaveItem);
        dueDatePicker = (DatePicker) findViewById(R.id.dueDatePicker);
        seekBarPriority = (SeekBar) findViewById(R.id.prioritySeekBar);
        txPriorityHint = (TextView) findViewById(R.id.txPriorityHint);
        swDate = (Switch) findViewById(R.id.swDueDate);
        swPriority = (Switch) findViewById(R.id.swPriority);


        if(getIntent()!=null &&
                getIntent().hasExtra(Constant.KEY_ITEM) &&
                getIntent().hasExtra(Constant.KEY_ITEM_POS)){
            todoItem = getIntent().getParcelableExtra(Constant.KEY_ITEM);
            etItemTitle.setTag(getIntent().getIntExtra(Constant.KEY_ITEM_POS,-1));
            initWidget();
        }
        btnSaveItem.setOnClickListener(this);

    }

    private void initWidget() {
        etItemTitle.setText(todoItem.getTitle());
        etItemContent.setText(todoItem.getContent());

        if(todoItem.getPriority()==Constant.UNSET){
            txPriorityHint.setVisibility(View.GONE);
            seekBarPriority.setVisibility(View.GONE);
            swPriority.setChecked(false);
        }else{
            txPriorityHint.setVisibility(View.VISIBLE);
            seekBarPriority.setVisibility(View.VISIBLE);
            swPriority.setChecked(true);

        }
        initPriorityWidget();

        if(todoItem.getDueDate().getTime()==Constant.UNSET){
            dueDatePicker.setVisibility(View.GONE);
            swDate.setChecked(false);
        }else{
            dueDatePicker.setVisibility(View.VISIBLE);
            swDate.setChecked(true);

        }
        initDueDatePicker();

    }

    private void initPriorityWidget() {
        if(todoItem.getPriority()!= Constant.UNSET) {
            seekBarPriority.setProgress(todoItem.getPriority() - 1);
            setPriorityHintText(todoItem.getPriority()-1);

        }

        seekBarPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPriorityHintText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setPriorityHintText(int progress) {
        switch (progress){
            case 0:
                txPriorityHint.setText("Low");break;
            case 1:
                txPriorityHint.setText("Med");break;
            case 2:
                txPriorityHint.setText("High");break;
        }
    }

    private void initDueDatePicker() {
        calendar = Calendar.getInstance();

        if(todoItem.getDueDate().getTime()!= Constant.UNSET) {
            calendar.setTime(todoItem.getDueDate());
        }else{
            calendar.setTime(new Date());
        }
        dueDatePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                });
    }

    public void switchPrioritySeekBar(View view) {
        if(!((Switch)view).isChecked()) {
            txPriorityHint.setVisibility(View.GONE);
            seekBarPriority.setVisibility(View.GONE);
        }else{
            txPriorityHint.setVisibility(View.VISIBLE);
            seekBarPriority.setVisibility(View.VISIBLE);
        }
        initPriorityWidget();
    }

    public void switchDatePicker(View view) {
        if(!((Switch)view).isChecked()) {
            dueDatePicker.setVisibility(View.GONE);
        }else{
            dueDatePicker.setVisibility(View.VISIBLE);
        }
        initDueDatePicker();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSaveItem) {
            if (etItemTitle.getText().length() != 0 && ((Integer) etItemTitle.getTag()) != -1 && todoItem != null) {
                todoItem.setTitle(etItemTitle.getText().toString());
                todoItem.setContent(etItemContent.getText().toString());
                if(!swDate.isChecked()) {
                    todoItem.setDueDate(new Date(0));
                }else{
                    todoItem.setDueDate(calendar.getTime());
                }
                if(!swPriority.isChecked()){
                    todoItem.setPriority((int) Constant.UNSET);
                }else{
                    todoItem.setPriority(seekBarPriority.getProgress()+1);
                }

                Intent resIntent = new Intent();
                resIntent.putExtra(Constant.KEY_ITEM, todoItem);
                resIntent.putExtra(Constant.KEY_ITEM_POS, (Integer) etItemTitle.getTag());
                setResult(RESULT_OK, resIntent);
                finish();
            }
        }
    }
}
