package com.sharcodes.ortho.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sharcodes.ortho.models.FormClass;
import com.sharcodes.ortho.models.NoteClass;
import com.sharcodes.ortho.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


public class CustomNotesAdapter extends RecyclerView.Adapter<CustomNotesAdapter.ViewHolder> implements Filterable {
    private final OnItemClickListener listener;
    List<NoteClass> data;
    List<NoteClass> dataOriginal;
    Context context;
    List<HashMap<String, LinkedHashMap<String, FormClass>>> list;

    public CustomNotesAdapter(List<NoteClass> data, Context context, OnItemClickListener listener) {
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
                data = (List<NoteClass>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<NoteClass> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = dataOriginal;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            protected List<NoteClass> getFilteredResults(String constraint) {
                List<NoteClass> results = new ArrayList<>();

                for (NoteClass item : dataOriginal) {
                    String searchin = item.title + item.content;
                    if (searchin.toLowerCase(Locale.ROOT).contains(constraint.toLowerCase(Locale.ROOT))) {
                        results.add(item);
                    }
                }
                return results;
            }
        };
    }

    public CustomNotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notes_card, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(data.get(holder.getAbsoluteAdapterPosition()));
            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull CustomNotesAdapter.ViewHolder viewHolder, int position) {
        viewHolder.title.setText(data.get(position).title);
        viewHolder.content.setText(data.get(position).content);
        viewHolder.date.setText(data.get(position).date);
        viewHolder.serialNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return data.size();

    }

    public interface OnItemClickListener {
        void onItemClick(NoteClass item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView date;

        public CardView cardView;
        public TextView serialNumber;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            title = view.findViewById(R.id.title);
            content = view.findViewById(R.id.content);
            date = view.findViewById(R.id.date);
            serialNumber = view.findViewById(R.id.serialNumber);
            cardView = view.findViewById(R.id.cardView);
        }


    }
}
