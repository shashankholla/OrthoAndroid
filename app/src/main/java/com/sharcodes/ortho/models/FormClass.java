package com.sharcodes.ortho.models;

import java.util.HashMap;
import java.util.UUID;

public class FormClass {
    public String id;
    public String group;

    public String uid;

    public String title;
    public String content;
    public HashMap<String, String> imagePath; //name,uri
    public HashMap<String, String> links; //name,uri

    FormClass(String id, String title, String group) {
        this.id = id;
        this.title = title;
        this.group = group;
        this.imagePath = new HashMap<>();
        this.links = new HashMap<>();
        this.uid = UUID.randomUUID().toString();
    }

    public FormClass(String id) {
        this.id = id;
    }

}
