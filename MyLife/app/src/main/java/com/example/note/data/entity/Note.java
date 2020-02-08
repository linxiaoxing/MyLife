package com.example.note.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "tb_note")
public class Note implements Serializable{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "order")
    public int order;

    @ColumnInfo(name = "maintype")
    public int maintype = -1;

    @ColumnInfo(name = "subtype")
    public int subtype = -1;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "body")
    public String body;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    public Note() {

    }

    @Ignore
    public Note(int order, String title, String body, Date timestamp) {
        this.order = order;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }
}
