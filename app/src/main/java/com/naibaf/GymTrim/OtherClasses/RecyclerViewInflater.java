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

package com.naibaf.GymTrim.OtherClasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.RecyclerViewAdapters.PlansCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewInflater {
    static Bitmap image;
    public static List<ExerciseCustomRecyclerViewAdapter.CustomExerciseList> ExerciseArrayList;
    public static List<PlansCustomRecyclerViewAdapter.CustomList> PlanArrayList;
    public static Cursor data2;

    public static ExerciseCustomRecyclerViewAdapter buildExerciseRecyclerView(Context context, View view, RecyclerView list, ExerciseCustomRecyclerViewAdapter.ItemClickListener itemClickListener,
                                             ExerciseCustomRecyclerViewAdapter Adapter, Cursor data, Boolean userIsTraining, int columnIndexOfId){

        //Get data from database

        //Add Data to list => https://www.geeksforgeeks.org/how-to-dynamically-add-elements-to-a-listview-in-android/
        //!Add an own list with more information's, e.g.: images: https://abhiandroid.com/ui/listview
        list.setLayoutManager(new GridLayoutManager(context, 1));

        //Create a Arraylist from ExerciseCustomRecyclerViewAdapter
        ExerciseArrayList = new ArrayList<>();

        // Now create the instance of the ExerciseCustomRecyclerViewAdapter and pass the context and ArrayListOfExercises created above
        Adapter = new ExerciseCustomRecyclerViewAdapter(context, ExerciseArrayList);
        Adapter.setClickListener(itemClickListener);
        list.setAdapter(Adapter);
        //Add divider: https://www.repeato.app/how-to-add-dividers-and-spaces-between-items-in-recyclerview/
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);

        //Fill RecyclerView
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            //Get column index once
            int columnIndexOfName = data.getColumnIndex("NameOfExercise");
            int columnIndexOfImage = data.getColumnIndex("ImageOfExercise");
            int columnIndexOfNotes = data.getColumnIndex("NotesForExercise");
            String name;
            String notes;

            // Check if all columns off the database are correct
            if (columnIndexOfName >= 0 && columnIndexOfImage >= 0 && columnIndexOfNotes >= 0 && columnIndexOfId >= 0) {
                do {
                    image = CommonFunctions.getArrayAsBitmap(data.getBlob(columnIndexOfImage));
                    //Store exercises => https://www.geeksforgeeks.org/how-to-dynamically-add-elements-to-a-listview-in-android
                    name = data.getString(columnIndexOfName);
                    if (name == null){
                        name = "null";
                    }
                    notes = data.getString(columnIndexOfNotes);
                    if (notes == null){
                        notes = "null";
                    }

                    ExerciseArrayList.add(new ExerciseCustomRecyclerViewAdapter.CustomExerciseList(name, notes, image, false, userIsTraining, data.getInt(columnIndexOfId), context));

                } while (data.moveToNext());
                Adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, R.string.error_missing_column, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.notification_empty, Toast.LENGTH_SHORT).show();
        }
        return Adapter;
    }

    public static PlansCustomRecyclerViewAdapter buildPlansRecyclerView(Context context, View view, RecyclerView list, PlansCustomRecyclerViewAdapter.ItemClickListener itemClickListener,
                                                                        PlansCustomRecyclerViewAdapter Adapter, Cursor dataForRecyclerView){

        data2 = dataForRecyclerView;

        list.setLayoutManager(new GridLayoutManager(context, 1));

        PlanArrayList = new ArrayList<>();

        Adapter = new PlansCustomRecyclerViewAdapter(context, PlanArrayList);
        Adapter.setClickListener(itemClickListener);
        list.setAdapter(Adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);

        //Display plans from DB
        //Get ColumnIndexes
        int columnIndexOfName = data2.getColumnIndex("NameOfPlan");
        int columnIndexOfColor = data2.getColumnIndex("ColorOfPlan");
        int columnIndexOfId = data2.getColumnIndex("plansId");
        int columnIndexOfDate = data2.getColumnIndex("DateOfLastTraining");

        int countOfPlans = data2.getCount();

        if (countOfPlans > 0 && data2 != null) {
            data2.moveToFirst();
            for (int item = 0; item < countOfPlans; item++) {
                String name = data2.getString(columnIndexOfName);
                int color = data2.getInt(columnIndexOfColor);
                int id = data2.getInt(columnIndexOfId);
                String date = data2.getString(columnIndexOfDate);

                PlanArrayList.add(new PlansCustomRecyclerViewAdapter.CustomList(name, color, date, id));

                data2.moveToNext();
            }
        } else {
            Toast.makeText(context, R.string.notification_empty, Toast.LENGTH_SHORT).show();
        }
        return Adapter;
    }
}
