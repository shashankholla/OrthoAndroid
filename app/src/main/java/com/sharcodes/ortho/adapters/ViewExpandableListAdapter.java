package com.sharcodes.ortho.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.helper.DBHelper;
import com.sharcodes.ortho.models.FormClass;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class ViewExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableListTitle;
    public LinkedHashMap<String, LinkedHashMap<String, FormClass>> expandableListDetail;
    public boolean online;

    public ViewExpandableListAdapter(Context context, List<String> expandableListTitle,
                                     LinkedHashMap<String, LinkedHashMap<String, FormClass>> expandableListDetail, boolean online) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.online = online;

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

        TextView title = convertView
                .findViewById(R.id.titleTv);
        title.setText(titleText);

        TextView contentTv = convertView
                .findViewById(R.id.contentTv);
        contentTv.setText(contentText);

        RelativeLayout rv = convertView.findViewById(R.id.fullView);

        ListView filesList = convertView.findViewById(R.id.filesList);

        filesList.setNestedScrollingEnabled(false);
        List<String> fileName = new ArrayList<>();
        List<String> filePath = new ArrayList<>();

        for (String c : child.imagePath.keySet()) {
            fileName.add(c);
            filePath.add(child.imagePath.get(c));
        }

        List<String> links = new ArrayList<>();

        for (String c : child.links.keySet()) {
            links.add(c);
        }


        filesList.setAdapter(new ArrayAdapter<String>(context, R.layout.activity_view_imagelist, fileName));


        ListView linksList = convertView.findViewById(R.id.linksList);

        linksList.setAdapter(new ArrayAdapter<String>(context, R.layout.activity_view_imagelist, links));

        filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//                boolean offline = sharedPref.getBoolean("offline", false);

                if (!online) {
                    DBHelper dbHelper = new DBHelper(context);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();


                    Uri[] uriList = new Uri[filePath.size()];

                    for(int i = 0; i < filePath.size(); i++) {

                        String imageUUID = filePath.get(i);

                        String selection = "UUID=?";
                        String[] selectionArgs = {imageUUID};
                        Cursor cursor = db.query("IMAGES", null, selection, selectionArgs, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
//                            byte[] blob = cursor.getBlob(cursor.getColumnIndex("DATA"));
                                String imageUri = cursor.getString(cursor.getColumnIndex("DATA"));
                                uriList[i] = Uri.parse(imageUri);
//                            Bitmap theImage = DbBitmapUtility.getImage(blob);


                                cursor.moveToNext();
                            }
                        }
                    }



                    new StfalconImageViewer.Builder<Uri>(context, uriList, new ImageLoader<Uri>() {
                        @Override
                        public void loadImage(ImageView imageView, Uri image) {

                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
                            circularProgressDrawable.setStrokeWidth(5f);
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.start();

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(circularProgressDrawable);

//                            requestOptions.skipMemoryCache(true);
                            requestOptions.fitCenter();


                            Glide.with(context)
//                                            .asBitmap()
                                    .load(image)
                                    .apply(requestOptions)
                                    .into(imageView);
                        }
                    })
                            .withStartPosition(position)
                            .withHiddenStatusBar(true)
                            .show();


                } else {
//                    Intent i = new Intent(context, ImageViwer.class);
//                    i.putExtra("image", filePath.get(position));
                    Uri[] uriList = new Uri[filePath.size()];


                    for(int i = 0; i < filePath.size(); i++) {

                            uriList[i] = Uri.parse(filePath.get(i));


                    }





                    new StfalconImageViewer.Builder<Uri>(context, uriList, new ImageLoader<Uri>() {
                        @Override
                        public void loadImage(ImageView imageView, Uri image) {

                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
                            circularProgressDrawable.setStrokeWidth(5f);
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.start();

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(circularProgressDrawable);

//                            requestOptions.skipMemoryCache(true);
                            requestOptions.fitCenter();


                            Glide.with(context)
                                    .load(image)
                                    .apply(requestOptions)
                                    .placeholder(circularProgressDrawable)
                                    .into(imageView);
                        }
                    })
                            .withStartPosition(position)
                            .withHiddenStatusBar(true)
                            .show();


//                    context.startActivity(i);
                }

            }
        });

        setListViewHeightBasedOnChildren(filesList);
        setListViewHeightBasedOnChildren(linksList);

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
}
