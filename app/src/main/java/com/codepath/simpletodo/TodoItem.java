package com.codepath.simpletodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by i_enpinghsieh on 2016/11/10.
 */

public class TodoItem implements Parcelable {

    private String title;
    private String content;
    private Date dueDate;
    private int priority;

    protected TodoItem(Parcel in) {
        title = in.readString();
        content = in.readString();
        dueDate = (Date) in.readSerializable();
        priority = in.readInt();
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeSerializable(dueDate);
        dest.writeInt(priority);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
