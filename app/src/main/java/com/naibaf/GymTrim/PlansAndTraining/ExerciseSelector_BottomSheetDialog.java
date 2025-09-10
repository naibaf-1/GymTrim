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

package com.naibaf.GymTrim.PlansAndTraining;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naibaf.GymTrim.Exercise.AddExerciseActivity;
import com.naibaf.GymTrim.OtherClasses.GlobalVariables;
import com.naibaf.GymTrim.OtherClasses.RecyclerViewInflater;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSelector_BottomSheetDialog extends BottomSheetDialogFragment implements  ExerciseCustomRecyclerViewAdapter.ItemClickListener {

    Cursor exerciseData;
    Bitmap exerciseImageBitmap;
    ExerciseCustomRecyclerViewAdapter selectableExerciseListAdapter;

    //Get Database => Fill ListView & Delete Data
    ExercisesDB DB;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.exercise_selector_bottom_sheet_dialog, container, false);

        //Add exercise if needed
        ImageButton Add = v.findViewById(R.id.imageButton_AddFromSelector);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddExercise = new Intent(getActivity(), AddExerciseActivity.class);
                startActivity(AddExercise);
            }
        });

        //Create RecyclerView
        //Create database
        DB = new ExercisesDB(getContext());

        //Add Data/Exercises to list => https://www.geeksforgeeks.org/how-to-dynamically-add-elements-to-a-listview-in-android/
        //!Add an own List with more Informations, e.g.: images: https://abhiandroid.com/ui/listview
        RecyclerView exerciseList = v.findViewById(R.id.RecyclerView_ListOfSelectableExercises);

        //Create a Arraylist from ExerciseCustomRecyclerViewAdapter
        List<ExerciseCustomRecyclerViewAdapter.CustomExerciseList> ArrayListOfExercises = new ArrayList<>();

        // Now create the instance of the ExerciseCustomRecyclerViewAdapter and pass the context and ArrayListOfExercises created above
        selectableExerciseListAdapter = new ExerciseCustomRecyclerViewAdapter(getContext(), ArrayListOfExercises);
        exerciseData = DB.getAllExerciseData();

        //Fill RecyclerView
        if (exerciseData.getCount() == 0) {
            Toast.makeText(getContext(), R.string.notification_empty, Toast.LENGTH_SHORT).show();
        } else if (exerciseData != null) {
            int columnIndexOfId = exerciseData.getColumnIndex("Id");
            selectableExerciseListAdapter = RecyclerViewInflater.buildExerciseRecyclerView(getContext(), v, exerciseList, this, selectableExerciseListAdapter, exerciseData, false, columnIndexOfId);
        }
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exerciseData.close();
        DB.closeExerciseDB();
        DB.close();

        //Update correct RecyclerView
        String Sender = GlobalVariables.senderActivity;
        if (getActivity() != null) {
            switch (Sender) {
                case "EditPlanActivity":
                    ((EditPlanActivity) getActivity()).refreshItemClickListener();
                    break;
                case "AddPlanActivity":
                    ((AddPlanActivity) getActivity()).refreshItemClickListener();
                    break;
                case "TrainingActivity":
                    ((TrainingActivity) getActivity()).refreshItemClickListener();
                    break;
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        final ExerciseCustomRecyclerViewAdapter.CustomExerciseList item = selectableExerciseListAdapter.getData().get(position);
        exerciseData.moveToPosition(position);

        int columnIndexOfRepetitions = exerciseData.getColumnIndex("RecordRepetitions");
        int columnIndexOfWeight = exerciseData.getColumnIndex("RecordWeight");
        int columnIndexOfTime = exerciseData.getColumnIndex("RecordTime");
        int columnIndexOfDistance = exerciseData.getColumnIndex("RecordDistance");
        int columnIndexOfId = exerciseData.getColumnIndex("Id");

        int recordRepetitions = exerciseData.getInt(columnIndexOfRepetitions);
        int recordWeight = exerciseData.getInt(columnIndexOfWeight);
        int recordTime = exerciseData.getInt(columnIndexOfTime);
        int recordDistance = exerciseData.getInt(columnIndexOfDistance);
        int id = exerciseData.getInt(columnIndexOfId);


        //Sent data to previous activity
        GlobalVariables.getInstance().setSelected(item, recordRepetitions, recordWeight, recordDistance, recordTime, id);

        //Update correct RecyclerView
        String Sender = GlobalVariables.senderActivity;
        if (getActivity() != null) {
            switch (Sender) {
                case "EditPlanActivity":
                    ((EditPlanActivity) getActivity()).getSelectedInEditor();
                    break;
                case "AddPlanActivity":
                    ((AddPlanActivity) getActivity()).getSelectedInAdd();
                    break;
                case "TrainingActivity":
                    ((TrainingActivity) getActivity()).getSelectedInTraining();
                    break;
            }

        }

    }

}
