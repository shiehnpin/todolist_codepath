package com.codepath.simpletodo.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class EditItemDialogFragment extends DialogFragment implements View.OnClickListener {

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

    public EditItemDialogFragment() {
    }


    public static EditItemDialogFragment getInstance(TodoItem item, int pos){
        EditItemDialogFragment fragment = new EditItemDialogFragment();
        Bundle data = new Bundle();
        data.putParcelable(Constant.KEY_ITEM,item);
        data.putInt(Constant.KEY_ITEM_POS,pos);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_edit_item, container, false);

        etItemTitle = (EditText) v.findViewById(R.id.etItemTitle);
        etItemContent = (EditText) v.findViewById(R.id.etContent);
        btnSaveItem = (Button) v.findViewById(R.id.btnSaveItem);
        dueDatePicker = (DatePicker) v.findViewById(R.id.dueDatePicker);
        seekBarPriority = (SeekBar) v.findViewById(R.id.prioritySeekBar);
        txPriorityHint = (TextView) v.findViewById(R.id.txPriorityHint);
        swDate = (Switch) v.findViewById(R.id.swDueDate);
        swPriority = (Switch) v.findViewById(R.id.swPriority);


        if(getArguments()!=null &&
                getArguments().containsKey(Constant.KEY_ITEM) &&
                getArguments().containsKey(Constant.KEY_ITEM_POS)){
            todoItem = getArguments().getParcelable(Constant.KEY_ITEM);
            etItemTitle.setTag(getArguments().getInt(Constant.KEY_ITEM_POS,-1));
            initWidget();
        }

        swDate.setOnClickListener(this);
        swPriority.setOnClickListener(this);
        btnSaveItem.setOnClickListener(this);

        return v;
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
                if(callback!=null){
                    callback.onSave(todoItem,(Integer) etItemTitle.getTag());
                }
                dismiss();
            }
        } else if(v.getId() == R.id.swDueDate){
            if(!swDate.isChecked()) {
                dueDatePicker.setVisibility(View.GONE);
            }else{
                dueDatePicker.setVisibility(View.VISIBLE);
            }
            initDueDatePicker();
        } else if(v.getId() == R.id.swPriority){
            if(!swPriority.isChecked()) {
                txPriorityHint.setVisibility(View.GONE);
                seekBarPriority.setVisibility(View.GONE);
            }else{
                txPriorityHint.setVisibility(View.VISIBLE);
                seekBarPriority.setVisibility(View.VISIBLE);
            }
            initPriorityWidget();
        }
    }

    public synchronized void setCallback(Callback callback){
        this.callback = callback;
    }
    private Callback callback;

    public interface Callback {
        void onSave(TodoItem todoItem, Integer tag);
    }
}
