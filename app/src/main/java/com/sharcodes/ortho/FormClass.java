package com.sharcodes.ortho;

import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormClass {
    public String id;
    public String group;

    public String title;
    public String content;
    public HashMap<String, String> imagePath; //name,uri

    FormClass(String id, String title, String group){
        this.id = id;
        this.title = title;
        this.group = group;
        this.imagePath = new HashMap<>();
    }
    FormClass(String id) {
        this.id = id;
    }

}
