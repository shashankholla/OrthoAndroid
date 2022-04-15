package com.sharcodes.ortho.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sharcodes.ortho.models.FormClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ListModel {
    List<FormClass> data;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LinkedHashMap<String, LinkedHashMap<String, FormClass>> getData() {
        LinkedHashMap<String, LinkedHashMap<String, FormClass>> expandableListDetail = new LinkedHashMap<>();

        List<String> orderOfTitles = new ArrayList<>();
        orderOfTitles.add("Biography");
        orderOfTitles.add("PreOp");
        orderOfTitles.add("patientDetails");
        orderOfTitles.add("Post Op");
        //Add orderOfTitles.add("Whatever Group Name");

        data = new ArrayList<>();
        //This is the group name
        data.add(new FormClass("name", "Name", "Biography"));
        data.add(new FormClass("diagnosis", "Diagnosis", "Biography"));
        data.add(new FormClass("rounds", "Rounds", "Biography"));
        data.add(new FormClass("phnumber", "Number", "Biography"));
        data.add(new FormClass("history", "History", "Biography"));


        data.add(new FormClass("fitness", "Fitness", "PreOp"));
        data.add(new FormClass("afitness", "Anaesthesia Fitness", "PreOp"));
        data.add(new FormClass("poOthers", "Others", "PreOp"));
        data.add(new FormClass("implants", "Implants", "PreOp"));
        data.add(new FormClass("details", "Details", "PreOp"));
        data.add(new FormClass("recent", "Recent", "PreOp"));
        data.add(new FormClass("guide", "Guide", "PreOp"));
        data.add(new FormClass("bills", "Bills", "PreOp"));
        data.add(new FormClass("preopWorkout", "Preop Workout", "PreOp"));
        data.add(new FormClass("postOpPrep", "Postop Preparation", "PreOp"));
        data.add(new FormClass("xrayCTMRI", "Preop Xra CT MRI", "PreOp"));
        data.add(new FormClass("bloodArrang", "Blood Arrangement", "PreOp"));
        data.add(new FormClass("preopXray", "Pre OP Xray", "PreOp"));
        data.add(new FormClass("preopCT", "Pre OP CT", "PreOp"));
        data.add(new FormClass("preopMRI", "Pre OP MRI", "PreOp"));
        data.add(new FormClass("preopClinic", "Pre OP Clinical Status", "PreOp"));
        data.add(new FormClass("good", "Good", "PreOp"));
        data.add(new FormClass("find", "Find", "PreOp"));


        data.add(new FormClass("inv", "Inv", "patientDetails"));
        data.add(new FormClass("scrubbing", "Scrubbing", "patientDetails"));
        data.add(new FormClass("approach", "Approach", "patientDetails"));
        data.add(new FormClass("findings", "Findings", "patientDetails"));
        data.add(new FormClass("newthings", "Newthings", "patientDetails"));
        data.add(new FormClass("consultant", "Consultant", "patientDetails"));
        data.add(new FormClass("inventions", "Inventions", "patientDetails"));
        data.add(new FormClass("skills", "Skills", "patientDetails"));
        data.add(new FormClass("reminder", "Reminder", "patientDetails"));
        data.add(new FormClass("duration", "Duration", "patientDetails"));
        data.add(new FormClass("procedure", "Procedure", "patientDetails"));
        data.add(new FormClass("iimplants", "Iimplants", "patientDetails"));
        data.add(new FormClass("dressing", "Dressing", "patientDetails"));

        data.add(new FormClass("postoporder", "Postoporder", "Post Op"));
        data.add(new FormClass("postoprounds", "Postoprounds", "Post Op"));
        data.add(new FormClass("complaints", "Complaints", "Post Op"));

        data.add(new FormClass("diagnosispost", "Diagnosispost", "patientDetails"));
        data.add(new FormClass("status", "Status", "patientDetails"));
        data.add(new FormClass("movements", "Movements", "patientDetails"));
        data.add(new FormClass("rehab", "Rehab", "patientDetails"));
        data.add(new FormClass("rest", "Rest", "patientDetails"));
        data.add(new FormClass("weight", "Weight", "patientDetails"));
        data.add(new FormClass("pdressing", "Pdressing", "patientDetails"));
        data.add(new FormClass("physio", "Physio", "patientDetails"));
        data.add(new FormClass("pxray", "Pxray", "patientDetails"));
        data.add(new FormClass("discharge", "Discharge", "patientDetails"));
        data.add(new FormClass("followup", "Followup", "patientDetails"));
        data.add(new FormClass("fxray", "Fxray", "patientDetails"));
        data.add(new FormClass("fphysio", "Fphysio", "patientDetails"));
        data.add(new FormClass("prognosis", "Prognosis", "patientDetails"));
        data.add(new FormClass("cconc", "Cconc", "patientDetails"));
        data.add(new FormClass("cdiagnosis", "Cdiagnosis", "patientDetails"));
        data.add(new FormClass("class", "Class", "patientDetails"));
        data.add(new FormClass("cpreop", "Cpreop", "patientDetails"));
        data.add(new FormClass("treatment", "Treatment", "patientDetails"));
        data.add(new FormClass("cpost", "Cpost", "patientDetails"));
        data.add(new FormClass("cprognosis", "Cprognosis", "patientDetails"));
        data.add(new FormClass("exam", "Exam", "patientDetails2"));


        for (String s : orderOfTitles) {
            expandableListDetail.put(s, new LinkedHashMap<>());
        }

        for (FormClass d : data) {
            LinkedHashMap<String, FormClass> x = expandableListDetail.getOrDefault(d.group, new LinkedHashMap<>());
            x.put(d.id, d);
            expandableListDetail.put(d.group, x);
        }

        //expandableListDetail.put("Patient Details", patientDetails);

        return expandableListDetail;

    }
}
