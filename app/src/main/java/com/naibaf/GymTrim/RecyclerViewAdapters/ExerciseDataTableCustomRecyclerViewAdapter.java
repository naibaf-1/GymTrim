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

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naibaf.GymTrim.OtherClasses.AudioServiceForBackgroundProcess;
import com.naibaf.GymTrim.PlansAndTraining.TrainingActivity;
import com.naibaf.GymTrim.R;

import java.util.List;

public class ExerciseDataTableCustomRecyclerViewAdapter extends RecyclerView.Adapter<ExerciseDataTableCustomRecyclerViewAdapter.CustomViewHolder> {
        public static List<ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList> mData;
        private final LayoutInflater mInflater;
        public static String ButtonColor = "gray";
        ClassLoader cl;
        Context context;

        // data is passed into the constructor
        public ExerciseDataTableCustomRecyclerViewAdapter(Context context, List<ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        @NonNull
        @Override
        public ExerciseDataTableCustomRecyclerViewAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.custom_training_table_recycler_view, parent, false);
            return new ExerciseDataTableCustomRecyclerViewAdapter.CustomViewHolder(view);
        }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cl = TrainingActivity.class.getClassLoader();
        String Repetitions = String.valueOf(mData.get(position).repetitions);
        String Weight = String.valueOf(mData.get(position).weight);
        String Time = String.valueOf(mData.get(position).time);
        String Distance = String.valueOf(mData.get(position).distance);

        //Show just necessary columns & Buttons
        if (mData.get(position).RecordRepetitions) {
            holder.Repetition.setVisibility(View.VISIBLE);
            holder.Repetition.setText(Repetitions);
            holder.Repetition.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.equals(" ")) {
                        mData.get(position).repetitions = Integer.parseInt(holder.Repetition.getText().toString());
                    }
                }
            });
        } else {
            holder.Repetition.setVisibility(View.GONE);
            holder.Repetition.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        if (mData.get(position).RecordWeight) {
            holder.Weight.setVisibility(View.VISIBLE);
            holder.Weight.setText(Weight);
            holder.Weight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.equals(" ")) {
                        mData.get(position).weight = Float.parseFloat(holder.Weight.getText().toString());
                    }
                }
            });
        } else {
            holder.Weight.setVisibility(View.GONE);
            holder.Weight.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }
        if (mData.get(position).RecordTime) {
            holder.Time.setVisibility(View.VISIBLE);
            holder.Time.setText(Time);
            holder.Time.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.equals(" ")) {
                        mData.get(position).time = Float.parseFloat(holder.Time.getText().toString());
                    }
                }
            });
        } else {
            holder.Time.setVisibility(View.GONE);
            holder.Time.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        if (mData.get(position).RecordDistance) {
            holder.Distance.setVisibility(View.VISIBLE);
            holder.Distance.setText(Distance);
            holder.Distance.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0 && !s.equals(" ")) {
                        mData.get(position).distance = Float.parseFloat(holder.Distance.getText().toString());
                    }
                }
            });
        } else {
            holder.Distance.setVisibility(View.GONE);
            holder.Distance.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        if (!mData.get(position).userIsTraining){
            holder.CheckSentence.setVisibility(View.GONE);
            holder.CheckSentence.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        } else {
            holder.CheckSentence.setVisibility(View.VISIBLE);
            if (mData.get(position).coloredButton){
                holder.CheckSentence.setBackgroundColor(Color.RED);
                ButtonColor = "red";
            } else {
                holder.CheckSentence.setBackgroundColor(Color.GRAY);
                ButtonColor = "gray";
            }
            //Change color of the Button
            holder.CheckSentence.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    context = holder.CheckSentence.getContext();
                    //Toggle Color each time its clicked
                    if (ButtonColor.equals("gray")) {
                        holder.CheckSentence.setBackgroundColor(Color.RED);
                        ButtonColor = "red";
                        mData.get(position).coloredButton = true;

                        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                        Boolean reminderEnabled = sharedPreferences.getBoolean("IsReminderEnabled", false);
                        if (reminderEnabled){
                            notifyByDuration(mData.get(position).duration);
                        }
                    } else {
                        holder.CheckSentence.setBackgroundColor(Color.GRAY);
                        ButtonColor = "gray";
                        mData.get(position).coloredButton = false;
                    }

                }
            });
        }
    }

        public static class CustomChildList {

            //Values to put into the child RecyclerView
            public int repetitions;
            public float weight;
            public float time;
            public float distance;
            Boolean RecordWeight;
            Boolean RecordTime;
            Boolean RecordDistance;
            Boolean RecordRepetitions;
            Boolean userIsTraining;
            public Boolean coloredButton;
            double duration;
            public int idInDatabase;

            public CustomChildList(int Repetitions, float Weight, float Time, float Distance, Boolean repetitions, Boolean weight, Boolean time, Boolean distance,
                                   Boolean isTraining, double durationOnClick, Boolean bottomIsChecked, int idInDatabase) {
                this.repetitions = Repetitions;
                this.weight = Weight;
                this.time = Time;
                this.distance = Distance;
                RecordDistance = distance;
                RecordWeight = weight;
                RecordTime = time;
                RecordRepetitions = repetitions;
                userIsTraining = isTraining;
                duration = durationOnClick;
                coloredButton = bottomIsChecked;
                this.idInDatabase = idInDatabase;
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            EditText Repetition;
            EditText Weight;
            EditText Time;
            EditText Distance;
            ImageButton CheckSentence;

            CustomViewHolder(View itemView) {
                super(itemView);
                Repetition = itemView.findViewById(R.id.editText_Repetition);
                Weight = itemView.findViewById(R.id.editText_Weight);
                Time = itemView.findViewById(R.id.editText_Time);
                Distance = itemView.findViewById(R.id.editText_Distance);
                CheckSentence = itemView.findViewById(R.id.imageButton_Tick);
            }

        }
        public void removeItem(int position) {
            mData.remove(position);
            notifyItemRemoved(position);
        }

        public List<ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList> getTableData(){
            return mData;
        }

    public List<ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList> getData() {
        return mData;
    }

    private void notifyByDuration(double duration){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, AudioServiceForBackgroundProcess.class));
                } else {
                    context.startService(new Intent(context, AudioServiceForBackgroundProcess.class));
                }
            }
        }, (long) (duration * 1000 * 60)); // 5000 milliseconds = 5 seconds => Convert into minutes
    }

}
