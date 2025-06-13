/**  Copyright 2025 Fabian Roland (naibaf-1)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License. **/

package com.naibaf.GymTrim.RecyclerViewAdapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.naibaf.GymTrim.R;

import java.util.ArrayList;
import java.util.List;

public class ExerciseCustomRecyclerViewAdapter extends RecyclerView.Adapter<ExerciseCustomRecyclerViewAdapter.CustomExerciseViewHolder> implements Filterable {

    List<CustomExerciseList> mData;
    static Context contextForThis;
    private final LayoutInflater mInflater;
    private static ItemClickListener mClickListener;

    private final List<CustomExerciseList> getUserModelListFiltered;

    // data is passed into the constructor
    public ExerciseCustomRecyclerViewAdapter(Context context, List<CustomExerciseList> data) {
        this.mInflater = LayoutInflater.from(context);
        this.getUserModelListFiltered = data;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public CustomExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_exercise_recycler_view, parent, false);
        return new CustomExerciseViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomExerciseViewHolder holder, int position) {
        String name = mData.get(position).exercise_name;
        String note = mData.get(position).exercise_notes;
        Bitmap image = mData.get(position).exercise_image;
        holder.NameView.setText(name);
        holder.NotesView.setText(note);
        holder.ImageView.setImageBitmap(image);

        if (!mData.get(position).userIsTraining){
            holder.VolumeView.setVisibility(GONE);
            holder.VolumeView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } else {
            holder.VolumeView.setVisibility(VISIBLE);
            String textForVolume = contextForThis.getString(R.string.display_volume_of_exercise);
            holder.VolumeView.setText(textForVolume + " " + String.valueOf(mData.get(position).volume));
        }

        if(mData.get(position).exerciseIsDone && mData.get(position).userIsTraining){
            holder.ExerciseIsDone.setVisibility(VISIBLE);
            holder.ExerciseIsDone.setBackgroundColor(Color.RED);
        } else if(!mData.get(position).exerciseIsDone && mData.get(position).userIsTraining){
            holder.ExerciseIsDone.setVisibility(VISIBLE);
            holder.ExerciseIsDone.setBackgroundColor(Color.GRAY);
        } else {
            holder.ExerciseIsDone.setVisibility(GONE);
            holder.ExerciseIsDone.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

    }


    //Function to search in RecyclerView: https://stackoverflow.com/questions/28697562/search-through-recyclerview-using-searchview or
    //https://youtu.be/ujKDN_ZtGHQ?sub_confirmation=1
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

                    List<CustomExerciseList> resultData = new ArrayList<>();

                    for(CustomExerciseList userModel: getUserModelListFiltered){
                        if(userModel.exercise_name.toLowerCase().contains(searchChr)){
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

                mData = (List<CustomExerciseList>) filterResults.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }

    public static class CustomExerciseList {
        public String exercise_name;
        public String exercise_notes;
        public Bitmap exercise_image;
        public Boolean exerciseIsDone;
        public Boolean userIsTraining;
        public int idInDatabase;
        public int volume;

        public CustomExerciseList(String exerciseName, String exerciseNotes, Bitmap exerciseImage, Boolean exerciseIsFinished, Boolean userTrains, int idInDatabase, Context context) {
            exercise_name = exerciseName;
            exercise_notes = exerciseNotes;
            exercise_image = exerciseImage;
            exerciseIsDone = exerciseIsFinished;
            userIsTraining = userTrains;
            volume = 0;
            this.idInDatabase = idInDatabase;
            contextForThis = context;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class CustomExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView NameView;
        ImageView ImageView;
        TextView NotesView;
        TextView VolumeView;
        public static ImageButton ExerciseIsDone;

        CustomExerciseViewHolder(View itemView) {
            super(itemView);
            NameView = itemView.findViewById(R.id.textView_ExerciseName);
            ImageView = itemView.findViewById(R.id.imageView_ExerciseImage);
            NotesView = itemView.findViewById(R.id.textView_Notes);
            VolumeView = itemView.findViewById(R.id.textView_Volume);
            itemView.setOnClickListener(this);
            ExerciseIsDone = itemView.findViewById(R.id.imageButton_ExerciseDone);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    //Function to restore removed Items, but if they are deleted from database it doesn't work
    public void restoreItem(CustomExerciseList item, int position) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public List<CustomExerciseList> getData(){
        return mData;
    }
}