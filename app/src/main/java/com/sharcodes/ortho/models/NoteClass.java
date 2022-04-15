package com.sharcodes.ortho.models;

import java.util.Date;



public class NoteClass {
    public NoteClass(String date, String title, String content, String uuid) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.uuid = uuid;
    }
    public String uuid;
    public String date;
    public String title;
    public String content;
}
