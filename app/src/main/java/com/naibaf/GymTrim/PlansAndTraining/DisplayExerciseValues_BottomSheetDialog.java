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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.OtherClasses.GlobalVariables;
import com.naibaf.GymTrim.OtherClasses.SwipeToDeleteCallback;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseDataTableCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DisplayExerciseValues_BottomSheetDialog extends BottomSheetDialogFragment {
    int exerciseId;
    PlansDB PDB;
    ExercisesDB EDB;
    int repetitions = 0;
    float weight = 0;
    float distance = 0;
    float time = 0;
    int rowId;
    Boolean rowIsDone;
    Cursor rowData;
    int rowCount;
    List<ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList> ExerciseDataArraylist;
    ExerciseDataTableCustomRecyclerViewAdapter ExerciseDataAdapter;
    Boolean userIsTraining;
    float reminderDuration;
    int ColumnIndexOfTime;
    int ColumnIndexOfRepetitions;
    int ColumnIndexOfDistance;
    int ColumnIndexOfWeight;
    int ColumnIndexOfId;
    int ColumnIndexOfDone;
    String currentDate;
    String dateOfLastTraining;
    Boolean newTraining;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {View v = inflater.inflate(R.layout.display_exercise_values_bottom_sheet_dialog,
                container, false);

        //Get data of pressed item
        PDB = new PlansDB(getContext());
        EDB = new ExercisesDB(getContext());
        exerciseId = GlobalVariables.exerciseID;
        userIsTraining = GlobalVariables.userIsTraining;
        reminderDuration = GlobalVariables.getInstance().getReminderDuration();

        Cursor data = PDB.getBasicExerciseData(exerciseId);
        rowData = PDB.getExerciseTableRows(exerciseId);
        data.moveToFirst();
        rowData.moveToFirst();

        //Get date => Store calculated Volume correct & apply doneRowsCorrect
        Date dateFromCalender = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(dateFromCalender);
        dateOfLastTraining = PDB.getDateOfExercise(exerciseId);
        if (!currentDate.equals(dateOfLastTraining)){
            PDB.setDateOfCurrentTrainingForExercise(exerciseId, currentDate);
            int columnIndexOfIdInEDB = data.getColumnIndex("ExerciseIdInEDB");
            int exerciseIdInEDB = data.getInt(columnIndexOfIdInEDB);
            EDB.editDateOfLastTraining(exerciseIdInEDB, currentDate);
            newTraining = true;
        } else {
            newTraining = false;
        }

        //Show only necessary columns
        int indexOfRepetitions = data.getColumnIndex("RecordRepetitions");
        int indexOfWeight = data.getColumnIndex("RecordWeight");
        int indexOfDistance = data.getColumnIndex("RecordDistance");
        int indexOfTime = data.getColumnIndex("RecordTime");

        Boolean recordRepetitions = CommonFunctions.getBooleanValueOfInteger(data.getInt(indexOfRepetitions));
        Boolean recordWeight = CommonFunctions.getBooleanValueOfInteger(data.getInt(indexOfWeight));
        Boolean recordTime = CommonFunctions.getBooleanValueOfInteger(data.getInt(indexOfTime));
        Boolean recordDistance = CommonFunctions.getBooleanValueOfInteger(data.getInt(indexOfDistance));

        //Hide if necessary unused columns
        TextView ColumnRepetitions = v.findViewById(R.id.textView_column_repetitions);
        TextView ColumnWeight = v.findViewById(R.id.textView_column_weight);
        TextView ColumnTime = v.findViewById(R.id.textView_column_time);
        TextView ColumnDistance = v.findViewById(R.id.textView_column_distance);
        Space PlaceHolder = v.findViewById(R.id.Space_placeholder_for_button);
        if (!recordRepetitions){
            ColumnRepetitions.setVisibility(GONE);
        } else {
            ColumnRepetitions.setVisibility(VISIBLE);
        }
        if (!recordWeight){
            ColumnWeight.setVisibility(GONE);
        } else {
            ColumnWeight.setVisibility(VISIBLE);
        }
        if (!recordTime){
            ColumnTime.setVisibility(GONE);
        } else {
            ColumnTime.setVisibility(VISIBLE);
        }
        if (!recordDistance){
            ColumnDistance.setVisibility(GONE);
        } else {
            ColumnDistance.setVisibility(VISIBLE);
        }

        //Get missing values from DB
        int ColumnIndexOfName = data.getColumnIndex("NameOfExercise");
        int ColumnIndexOfNotes = data.getColumnIndex("NotesForExercise");
        int ColumnIndexOfImage = data.getColumnIndex("ImageOfExercise");

        String name = data.getString(ColumnIndexOfName);
        String notes = data.getString(ColumnIndexOfNotes);
        Bitmap image = CommonFunctions.getArrayAsBitmap(data.getBlob(ColumnIndexOfImage));

        //Display exercise name
        TextView ExerciseNameAndHeader = v.findViewById(R.id.textView_DisplayExerciseValuesHeader);
        ExerciseNameAndHeader.setText(name);

        //Display exercise notes
        TextView ExerciseNotes = v.findViewById(R.id.textView_DisplayExerciseValuesNotes);
        ExerciseNotes.setText(notes);

        //Display exerciseImage
        ImageView ExerciseImage = v.findViewById(R.id.imageView_DisplayExerciseValuesImage);
        ExerciseImage.setImageBitmap(image);

        RecyclerView ExerciseData = v.findViewById(R.id.recyclerView_DisplayExerciseValues);
        ExerciseData.setLayoutManager(new GridLayoutManager(getContext(), 1));

        ExerciseDataArraylist = new ArrayList<>();
        ExerciseDataAdapter = new ExerciseDataTableCustomRecyclerViewAdapter(getContext(), ExerciseDataArraylist);

        ExerciseData.setAdapter(ExerciseDataAdapter);

        //Display all rows in the recyclerView
        rowCount = rowData.getCount();

        ColumnIndexOfWeight = rowData.getColumnIndex("RecordWeight");
        ColumnIndexOfDistance = rowData.getColumnIndex("RecordDistance");
        ColumnIndexOfRepetitions = rowData.getColumnIndex("RecordSentences");
        ColumnIndexOfTime = rowData.getColumnIndex("RecordTime");
        ColumnIndexOfId = rowData.getColumnIndex("tableMainId");
        ColumnIndexOfDone = rowData.getColumnIndex("RowIsDone");

        if (newTraining){
            for (int row = 0; row < rowCount; row++){
                rowData.moveToPosition(row);

                weight = Float.parseFloat(rowData.getString(ColumnIndexOfWeight));
                distance = Float.parseFloat(rowData.getString(ColumnIndexOfDistance));
                repetitions = Integer.parseInt(rowData.getString(ColumnIndexOfRepetitions));
                time = Float.parseFloat(rowData.getString(ColumnIndexOfTime));
                rowId = rowData.getInt(ColumnIndexOfId);
                ExerciseDataArraylist.add(new ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList(repetitions, weight, time, distance, recordRepetitions, recordWeight, recordTime, recordDistance, userIsTraining, reminderDuration, false, rowId));
                PDB.updateRowDone(rowId, false);
            }
        } else {
            for (int row = 0; row < rowCount; row++){
                rowData.moveToPosition(row);

                weight = Float.parseFloat(rowData.getString(ColumnIndexOfWeight));
                distance = Float.parseFloat(rowData.getString(ColumnIndexOfDistance));
                repetitions = Integer.parseInt(rowData.getString(ColumnIndexOfRepetitions));
                time = Float.parseFloat(rowData.getString(ColumnIndexOfTime));
                rowId = rowData.getInt(ColumnIndexOfId);
                rowIsDone = CommonFunctions.getBooleanValueOfInteger(rowData.getInt(ColumnIndexOfDone));
                ExerciseDataArraylist.add(new ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList(repetitions, weight, time, distance, recordRepetitions, recordWeight, recordTime, recordDistance, userIsTraining, reminderDuration, rowIsDone, rowId));
            }
        }
        ExerciseDataAdapter.notifyDataSetChanged();

        //Add repetition
        Button AddRepetition = v.findViewById(R.id.button_addRepetition);

        AddRepetition.setVisibility(VISIBLE);
        AddRepetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int order = ExerciseDataArraylist.size() +1;
                int rowId = PDB.addExerciseTableRow(exerciseId, weight, repetitions, time, distance, order);
                ExerciseDataArraylist.add(new ExerciseDataTableCustomRecyclerViewAdapter.CustomChildList(repetitions, weight, time, distance, recordRepetitions, recordWeight, recordTime, recordDistance, userIsTraining, reminderDuration, false, rowId));
                ExerciseDataAdapter.notifyDataSetChanged();
                rowCount++;
            }
        });

        //Delete Row if swiped: vgl.: https://stackoverflow.com/questions/40089542/add-swipe-right-to-delete-listview-item
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();

                ExerciseDataAdapter.removeItem(position);

                Cursor possibleIds = PDB.getRowId(exerciseId);
                possibleIds.moveToPosition(position);
                int ColumnIndexOfId = possibleIds.getColumnIndex("tableMainId");

                int id = possibleIds.getInt(ColumnIndexOfId);
                PDB.deleteExerciseTableRow(id);
                rowCount--;

                ExerciseDataAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(ExerciseData);

        return v;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        //Update data row per row
        Cursor rowIds = null;
        int countOfCompletedRows = 0;
        int countOfRows = ExerciseDataArraylist.size();
        int sumOfRepetitions = 0;
        float sumOfWeight = 0;
        float sumOfDistance = 0;
        float sumOfTime = 0;
        for(int i = 0; i < countOfRows; i++){
            weight = ExerciseDataArraylist.get(i).weight;
            repetitions = ExerciseDataArraylist.get(i).repetitions;
            time = ExerciseDataArraylist.get(i).time;
            distance = ExerciseDataArraylist.get(i).distance;
            rowId = ExerciseDataArraylist.get(i).idInDatabase;

            //Mark done rows in DB as done too
            if (ExerciseDataAdapter.getData().get(i).coloredButton){
                PDB.updateRowDone(rowId, true);
                countOfCompletedRows++;
            } else {
                PDB.updateRowDone(rowId, false);
            }

            PDB.updateExerciseTableRows(exerciseId, weight, repetitions, time, distance, i + 1);
            sumOfRepetitions = sumOfRepetitions + repetitions;
            sumOfWeight = sumOfWeight + weight;
            sumOfDistance = sumOfDistance + distance;
            sumOfTime = sumOfTime + time;
        }

        //If user completed all reps, mark current exercise as done & display calculated volume
        if (userIsTraining){
            //Calculate volume
            int position = GlobalVariables.trainingSelectedExercise;
            int volume = (countOfRows) * sumOfRepetitions;
            if (countOfRows == countOfCompletedRows && countOfRows > 0){
                ((TrainingActivity) getActivity()).markExercise(position, true, volume);
            } else {
                ((TrainingActivity) getActivity()).markExercise(position, false, volume);
            }
            if (newTraining){
                //Calculate average of weight, time & distance
                float averageOfWeight = sumOfWeight/(countOfRows);
                float averageOfDistance = sumOfDistance/(countOfRows);
                float averageOfTime = sumOfTime/(countOfRows);

                EDB.insertNewTrainingsData(PDB.getIdOfExerciseInEDB(exerciseId), volume, averageOfWeight, averageOfTime, averageOfDistance, currentDate);
            }
        }

        rowData.close();
        if (rowIds != null) {
            rowIds.close();
        }
        PDB.closePlansDB();
        PDB.close();

    }
}