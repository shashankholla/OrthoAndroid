package com.sharcodes.ortho.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.sharcodes.ortho.activities.AddPatientActivity;
import com.sharcodes.ortho.models.FormClass;
import com.sharcodes.ortho.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class EditExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableListTitle;
    private final LinkedHashMap<String, LinkedHashMap<String, FormClass>> expandableListDetail;
    private String m_Text = "";
    private ImagesAndLinkCustomAdapter filesListAdapter;
    private ImagesAndLinkCustomAdapter linkListAdapter;

    public EditExpandableListAdapter(Context context, List<String> expandableListTitle,
                                     LinkedHashMap<String, LinkedHashMap<String, FormClass>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        LinkedHashMap<String, FormClass> k = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
        List<FormClass> l = new ArrayList<FormClass>(k.values());
        return l.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        FormClass child = (FormClass) getChild(listPosition, expandedListPosition);
        String titleText = child.title;

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_add_list_item, null);
            holder = new ViewHolder(convertView, new MyCustomEditTextListener(listPosition, expandedListPosition));
            holder.id = child.id;
            holder.group = child.group;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.myCustomEditTextListener.updatePosition(listPosition, expandedListPosition);


//        TextView title = (TextView) convertView
//                .findViewById(R.id.tv);
//        title.setText(titleText);

//        TextInputLayout et = (TextInputLayout) convertView
//                .findViewById(R.id.et);
        holder.et.setHint(titleText);

        if (child.content != null)
            holder.et.getEditText().setText(child.content);
        else
            holder.et.getEditText().setText("");


//        holder.et.getEditText().addTextChangedListener(new TextWatcher() {
//            boolean considerChange = false;
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                if (considerChange) {
//                    ((AddPatientActivity) context).changeData(child.id, child.group, editable.toString(), child.imagePath);
//                }
//                considerChange = !considerChange;
//            }
//        });


//        holder.et.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus)
//            {
//                if(!hasFocus)
//
//                    ((AddPatientActivity)context).changeData(holder.id, holder.group, holder.et.getEditText().getText().toString(), child.imagePath);
//// In java variable contains reference of Object so when you change childText object, it will be reflected in same HashMap you are getting data from.
//
//            }
//        });


//        RecyclerView filesList = convertView.findViewById(R.id.filesList);
        List<String> images = new ArrayList<>();
        for (String c : child.imagePath.keySet()) {
            images.add(c);
        }

        holder.filesList.setLayoutManager(new LinearLayoutManager(context));
        filesListAdapter = new ImagesAndLinkCustomAdapter(context, images, child, "images");
        holder.filesList.setAdapter(filesListAdapter);


        List<String> links = new ArrayList<>();
        for (String c : child.links.keySet()) {
            links.add(c);
        }

        holder.linksList.setLayoutManager(new LinearLayoutManager(context));
        linkListAdapter = new ImagesAndLinkCustomAdapter(context, links, child, "links");
        holder.linksList.setAdapter(linkListAdapter);


        holder.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddPatientActivity) context).addImage2(expandableListTitle.get(listPosition), child.id);
            }
        });


        holder.linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add a link");

// Set up the input
                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        ((AddPatientActivity) context).addLink(expandableListTitle.get(listPosition), child.id, m_Text);
                        dialog.dismiss();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        //setListViewHeightBasedOnChildren(filesList);
        return convertView;
    }


    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_add_list_group, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    class ViewHolder {
        TextInputLayout et;
        RecyclerView filesList;
        RecyclerView linksList;
        ImageButton imageBtn;
        ImageButton linkBtn;
        String id;
        String group;
        MyCustomEditTextListener myCustomEditTextListener;

        public ViewHolder(View v, MyCustomEditTextListener myCustomEditTextListener) {
            this.et = v.findViewById(R.id.et);
            this.filesList = v.findViewById(R.id.filesList);
            this.linksList = v.findViewById(R.id.linksList);
            this.imageBtn = v.findViewById(R.id.imgbtn);
            this.linkBtn = v.findViewById(R.id.linkbtn);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.et.getEditText().addTextChangedListener(this.myCustomEditTextListener);
            this.et.getEditText().setSelection(this.et.getEditText().getText().length());
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int listPosition;
        private int expandedListPosition;


        public MyCustomEditTextListener(int listPosition, int expandedListPosition) {
            this.listPosition = listPosition;
            this.expandedListPosition = expandedListPosition;
        }

        public void updatePosition(int listPosition, int expandedListPosition) {
            this.listPosition = listPosition;
            this.expandedListPosition = expandedListPosition;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i("INFO", editable.toString() + ":" + listPosition);
            FormClass child = (FormClass) getChild(listPosition, expandedListPosition);
            ((AddPatientActivity) context).changeData(child.id, child.group, editable.toString(), child.imagePath);
        }
    }

}
