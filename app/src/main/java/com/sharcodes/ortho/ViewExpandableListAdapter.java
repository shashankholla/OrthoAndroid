package com.sharcodes.ortho;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class ViewExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    public LinkedHashMap<String,LinkedHashMap<String,FormClass>> expandableListDetail;

    public ViewExpandableListAdapter(Context context, List<String> expandableListTitle,
                                     LinkedHashMap<String,LinkedHashMap<String,FormClass>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

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
        final String titleText = child.title;
        final String contentText = child.content;


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_view_list_item, null);
        }

        TextView title = (TextView) convertView
                .findViewById(R.id.titleTv);
        title.setText(titleText);

        TextView contentTv = (TextView) convertView
                .findViewById(R.id.contentTv);
        contentTv.setText(contentText);

        RelativeLayout rv = convertView.findViewById(R.id.fullView);

        ListView filesList = convertView.findViewById(R.id.filesList);
        filesList.setNestedScrollingEnabled(false);
        List<String> fileName = new ArrayList<>();
        List<String> filePath = new ArrayList<>();
        for(String c: child.imagePath.keySet()) {
            fileName.add(c);
            filePath.add(child.imagePath.get(c));
        }


        filesList.setAdapter(new ArrayAdapter<String>(context, R.layout.activity_view_imagelist, fileName));
        filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(context, ImageViwer.class);
                i.putExtra("image", filePath.get(position));
                context.startActivity(i);


            }
        });

        setListViewHeightBasedOnChildren(filesList);

//        ImageButton btn = (ImageButton) convertView.findViewById(R.id.imgbtn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((AddPatientActivity)context).hello(expandableListTitle.get(listPosition),child.id);
//            }
//        });


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
        TextView listTitleTextView = (TextView) convertView
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}
