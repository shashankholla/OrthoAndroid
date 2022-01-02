package com.sharcodes.ortho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>  implements Filterable {
    List<HashMap<String,LinkedHashMap<String,FormClass>>> data;
    List<HashMap<String,LinkedHashMap<String,FormClass>>> dataOriginal;
    Context context;
    private final OnItemClickListener listener;
    List<HashMap<String,LinkedHashMap<String,FormClass>>> list;
    public CustomAdapter( List<HashMap<String,LinkedHashMap<String,FormClass>>> data, Context context, OnItemClickListener listener){
        this.data = data;
        this.dataOriginal = data;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (List<HashMap<String,LinkedHashMap<String,FormClass>>>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<HashMap<String,LinkedHashMap<String,FormClass>>> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = dataOriginal;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            protected List<HashMap<String,LinkedHashMap<String,FormClass>>> getFilteredResults(String constraint) {
                List<HashMap<String,LinkedHashMap<String,FormClass>>> results = new ArrayList<>();

                for (HashMap<String,LinkedHashMap<String,FormClass>> item : dataOriginal) {
                    if (item.get("Biography").get("name").content.toLowerCase(Locale.ROOT).contains(constraint.toLowerCase(Locale.ROOT))) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(HashMap<String, LinkedHashMap<String,FormClass>> item);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public ImageView imageView;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            cardView = view.findViewById(R.id.cardView);
            patientName = (TextView) view.findViewById(R.id.patientName);
        }

        public TextView getTextView() {
            return patientName;
        }
    }


    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(data.get(holder.getAdapterPosition()+1));
            }
        });
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder viewHolder, int position) {
        viewHolder.patientName.setText(data.get(position).get("Biography").get("name").content);

//        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return data.size();

    }
}
