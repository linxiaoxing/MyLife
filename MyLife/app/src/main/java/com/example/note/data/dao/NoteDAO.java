package com.example.note.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.note.data.entity.Note;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("select * from tb_note order by `order` desc")
    public List<Note> getAll();

    @Query("select * from tb_note where id=:nid")
    public Note get(int nid);

    @Query("select * from tb_note where title LIKE :s")
    public List<Note> get(String s);

    @Update
    public void update(Note note);

    @Update
    public void update(List<Note> note);

    @Insert
    public void insert(Note note);

    @Delete
    public void delete(Note note);

    @Query("select max(id) from tb_note")
    public int getmaxid();

}