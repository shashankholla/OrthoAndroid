package com.sharcodes.ortho.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharcodes.ortho.activities.AddPatientActivity;
import com.sharcodes.ortho.models.FormClass;
import com.sharcodes.ortho.R;

import java.util.List;

public class ImagesAndLinkCustomAdapter extends RecyclerView.Adapter<ImagesAndLinkCustomAdapter.ViewHolder> {


    List<String> lista;
    Context context;
    FormClass child;
    String adapterType;

    public ImagesAndLinkCustomAdapter(Context context, List<String> lista, FormClass child, String adapterType) {
        this.context = context;
        this.lista = lista;
        this.child = child;
        this.adapterType = adapterType;
    }

//    @Override
//    public int getCount() {
//        // TODO Auto-generated method stub
//        return lista.size();
//    }
//
//    @Override
//    public Object getItem(int arg0) {
//        // TODO Auto-generated method stub
//        return lista.get(arg0);
//    }

    @NonNull
    @Override
    public ImagesAndLinkCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_add_imagelist, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAndLinkCustomAdapter.ViewHolder holder, final int position) {
        holder.text.setText(lista.get(position));
        holder.b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                int pos = (int)arg0.getTag();

                lista.remove(position);

                if (adapterType.equals("images")) {
                    ((AddPatientActivity) context).changeImages(child.id, child.group, lista);
                } else {
                    ((AddPatientActivity) context).changeLinks(child.id, child.group, lista);
                }
                ImagesAndLinkCustomAdapter.this.notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button b2;
        TextView text;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            b2 = view.findViewById(R.id.delBtn);
            text = view.findViewById(R.id.text1);


        }
    }

//    @Override
//    public View getView(int arg0, View arg1, ViewGroup arg2) {
//        // TODO Auto-generated method stub
//
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = inflater.inflate(R.layout.activity_add_imagelist, arg2, false);
//
//        Button b2 = (Button) row.findViewById(R.id.delBtn);
//        b2.setTag(arg0);
//        b2.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                int pos = (int)arg0.getTag();
//
//                lista.remove(pos);
//
//                ((AddPatientActivity)context).changeImages(child.id, child.group, lista);
//                ImageListCustomAdapter.this.notifyDataSetChanged();            }
//        });
//        TextView titlu = (TextView) row.findViewById(R.id.text1);
//        titlu.setText(lista.get(arg0));
//
//
//
//
//        return row;
//    }
}