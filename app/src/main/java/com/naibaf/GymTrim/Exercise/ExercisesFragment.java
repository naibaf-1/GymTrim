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

package com.naibaf.GymTrim.Exercise;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.OtherClasses.RecyclerViewInflater;
import com.naibaf.GymTrim.OtherClasses.SwipeToDeleteCallback;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExercisesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExercisesFragment extends Fragment implements ExerciseCustomRecyclerViewAdapter.ItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Cursor exerciseData;
    ExerciseCustomRecyclerViewAdapter exerciseListAdapter;

    //Get Database => Fill ListView & Delete Data
    ExercisesDB DB;
    View v;
    Context context;
    int selectedFromList;
    RecyclerView exerciseList;
    List<ExerciseCustomRecyclerViewAdapter.CustomExerciseList> ArrayListOfExercises;
    int columnIndexOfId;

    public ExercisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExercisesFragment.
     */
    public static ExercisesFragment newInstance(String param1, String param2) {
        ExercisesFragment fragment = new ExercisesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exercises, container, false);

        context = getContext();

        //Start here adding things
        //Get database
        DB = new ExercisesDB(getContext());

        //Add a New Exercise
        ImageButton AddExercise = v.findViewById(R.id.floatingActionButton_AddExercise4);
        AddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addExercise = new Intent(context, AddExerciseActivity.class);
                startActivityForResult(addExercise, 1);
            }
        });

        //Add Data/Exercises to list => https://www.geeksforgeeks.org/how-to-dynamically-add-elements-to-a-listview-in-android/
        //!Add an own List with more Informations, e.g.: images: https://abhiandroid.com/ui/listview
        exerciseList  = v.findViewById(R.id.recyclerView_ExerciseList);

        //Create a Arraylist from ExerciseCustomRecyclerViewAdapter
        ArrayListOfExercises = new ArrayList<>();

        // Now create the instance of the ExerciseCustomRecyclerViewAdapter and pass the context and ArrayListOfExercises created above
        exerciseListAdapter = new ExerciseCustomRecyclerViewAdapter(context, ArrayListOfExercises);
        exerciseData = DB.getAllExerciseData();
        columnIndexOfId = exerciseData.getColumnIndex("Id");
        exerciseListAdapter = RecyclerViewInflater.buildExerciseRecyclerView(context, v, exerciseList, this, exerciseListAdapter, exerciseData, false, columnIndexOfId);
        ArrayListOfExercises = RecyclerViewInflater.ExerciseArrayList;

        //Search through exercises
        SearchView Search = v.findViewById(R.id.searchView_SearchForExercise);
        Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ( TextUtils.isEmpty ( newText ) ) {
                    exerciseListAdapter.getFilter().filter("");
                } else {
                    exerciseListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        //Delete Exercise if swiped: vgl.: https://stackoverflow.com/questions/40089542/add-swipe-right-to-delete-listview-item
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final ExerciseCustomRecyclerViewAdapter.CustomExerciseList Exercise;
                final int position = viewHolder.getAdapterPosition();
                try {
                    Exercise = exerciseListAdapter.getData().get(position);
                    selectedFromList = Exercise.idInDatabase;
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(context, "IndexOutOfBoundsException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                //Use a Alert Dialog to ask user if he is sure => https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
                // Create the object of AlertDialog Builder class
                MaterialAlertDialogBuilder ConfirmDialog = new MaterialAlertDialogBuilder(context);

                // Set the message show for the Alert time
                ConfirmDialog.setMessage(R.string.delete_item_callback);

                // Set Cancelable true for when the user clicks on the outside the Dialog Box disappears
                ConfirmDialog.setCancelable(true);
                ConfirmDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        exerciseListAdapter.notifyDataSetChanged();
                    }
                });

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                ConfirmDialog.setPositiveButton(R.string.button_apply, (dialog, which) -> {
                    exerciseListAdapter.removeItem(position);
                    DB.deleteExerciseData(selectedFromList);
                    exerciseListAdapter.notifyDataSetChanged();
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                ConfirmDialog.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                    // If user click cancel then dialog box is canceled.
                    exerciseListAdapter.notifyDataSetChanged();
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog confirmDialog = ConfirmDialog.create();
                // Show the Alert Dialog box
                confirmDialog.show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(exerciseList);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Boolean wasItemAdded = data.getBooleanExtra("isItemAdded", true);
                String name = data.getStringExtra("name");
                String notes = data.getStringExtra("notes");
                Bitmap image = CommonFunctions.getArrayAsBitmap(data.getByteArrayExtra("image"));
                int id = data.getIntExtra("id", -1);
                int position = data.getIntExtra("position", -1);

                if(wasItemAdded){
                    // Add a new item to the RecyclerView
                    ExerciseCustomRecyclerViewAdapter.CustomExerciseList newItem = new ExerciseCustomRecyclerViewAdapter.CustomExerciseList(name, notes, image, false, false, id, getContext());
                    ArrayListOfExercises.add(newItem);
                    exerciseListAdapter.notifyItemInserted(ArrayListOfExercises.size() - 1);
                } else {
                    // Change an item in the RecyclerView
                    ExerciseCustomRecyclerViewAdapter.CustomExerciseList updatedItem = new ExerciseCustomRecyclerViewAdapter.CustomExerciseList(name, notes, image, false, false, id, getContext());
                    ArrayListOfExercises.set(position, updatedItem);
                    exerciseListAdapter.notifyItemChanged(position);
                }
            }
//            Cursor newExercisesValues = DB.getAllExerciseData();
//            exerciseListAdapter = RecyclerViewInflater.buildExerciseRecyclerView(context, v, exerciseList, this, exerciseListAdapter, newExercisesValues, false, columnIndexOfId);
//            ArrayListOfExercises = RecyclerViewInflater.ExerciseArrayList;
//            exerciseListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exerciseData.close();
        DB.closeExerciseDB();
        DB.close();
    }

    @Override
    public void onItemClick(View view, int position) {
        final ExerciseCustomRecyclerViewAdapter.CustomExerciseList item = exerciseListAdapter.getData().get(position);
        String selectedFromList = item.exercise_name;
        int idOfSelected = item.idInDatabase;
        Intent EditExercise = new Intent(context, EditExerciseActivity.class);
        EditExercise.putExtra("Selected", selectedFromList);
        EditExercise.putExtra("position", position);
        EditExercise.putExtra("id", idOfSelected);
        EditExercise.putExtra("position", position);
        startActivityForResult(EditExercise, 1);
    }

}