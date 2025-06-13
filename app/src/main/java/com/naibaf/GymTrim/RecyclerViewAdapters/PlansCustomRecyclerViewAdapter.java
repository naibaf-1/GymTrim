package com.naibaf.GymTrim.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naibaf.GymTrim.R;

import java.util.ArrayList;
import java.util.List;


public class PlansCustomRecyclerViewAdapter extends RecyclerView.Adapter<PlansCustomRecyclerViewAdapter.CustomViewHolder> implements Filterable {
     private List<CustomList> mData;
     private final LayoutInflater mInflater;
     private ItemClickListener mClickListener;

     private final List<CustomList> getUserModelListFiltered;

     // data is passed into the constructor
     public PlansCustomRecyclerViewAdapter(Context context, List<CustomList> data) {
         this.mInflater = LayoutInflater.from(context);
         this.getUserModelListFiltered = data;
         this.mData = data;
     }

    @NonNull
    @Override
    public PlansCustomRecyclerViewAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_plans_recycler_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.NameView.setText(mData.get(position).name);
        holder.ColorView.setBackgroundColor(mData.get(position).color);
        holder.DateView.setText(mData.get(position).date);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if(charSequence == null | charSequence.length() == 0){
                    filterResults.count = getUserModelListFiltered.size();
                    filterResults.values = getUserModelListFiltered;

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<CustomList> resultData = new ArrayList<>();

                    for(PlansCustomRecyclerViewAdapter.CustomList userModel: getUserModelListFiltered){
                        if(userModel.name.toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                mData = (List<PlansCustomRecyclerViewAdapter.CustomList>) filterResults.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }

    public static class CustomList {
        public String name;
        public int color;
        public int idOfItemInDatabase;
        public String date;

        public CustomList(String Name, int Color, String dateOfLastTraining, int idOfItemInDatabase) {
            name = Name;
            color = Color;
            this.idOfItemInDatabase = idOfItemInDatabase;
            date = dateOfLastTraining;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView NameView;
        View ColorView;
        TextView DateView;

        CustomViewHolder(View itemView) {
            super(itemView);
            NameView = itemView.findViewById(R.id.textView_Name);
            ColorView = itemView.findViewById(R.id.view_Color);
            DateView = itemView.findViewById(R.id.textView_DateOfLastTraining);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public List<CustomList> getData() {
        return mData;
    }
}