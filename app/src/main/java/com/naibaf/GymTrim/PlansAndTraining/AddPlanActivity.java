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
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.OtherClasses.GlobalVariables;
import com.naibaf.GymTrim.OtherClasses.SwipeToDeleteCallback;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddPlanActivity extends AppCompatActivity implements  ExerciseCustomRecyclerViewAdapter.ItemClickListener {

    private int picked_Color;
    ExerciseSelector_BottomSheetDialog ExerciseSelectorBottomSheetDialog;
    List<ExerciseCustomRecyclerViewAdapter.CustomExerciseList> WorkoutArrayList;
    ExerciseCustomRecyclerViewAdapter WorkoutListAdapter;
    RecyclerView WorkoutList;
    TextView Header;
    PlansDB DB;
    int plansId;
    int countOfExercises;
    List<Integer> idsOfDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_plan);

        //Add EdgeToScreen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    systemBars.top, // Toolbar adjusts for the status bar
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );
            return insets;
        });

        int dynamicPrimaryColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryInverse, Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setStatusBarColor(dynamicPrimaryColor);
            getWindow().setNavigationBarColor(dynamicPrimaryColor);
        } else {
            getWindow().setStatusBarColor(dynamicPrimaryColor);
            getWindow().setNavigationBarColor(dynamicPrimaryColor);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(dynamicPrimaryColor);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        //Start Here
        idsOfDeleted = new ArrayList<>();

        //Insert data for plan
        DB = new PlansDB(AddPlanActivity.this);
        Header = findViewById(R.id.textView_Add_New_Plan);
        plansId = DB.insertNewPlan();

        //Close & return
        ImageButton Close = findViewById(R.id.imageButton_close2);
        Close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Select a color
        ImageButton SelectColor = findViewById(R.id.imageButton_selectColor);
        SelectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Vgl.: https://www.geeksforgeeks.org/how-to-create-a-basic-color-picker-tool-in-android/
                // the AmbilWarnaDialog callback needs 3 parameters
                // one is the context, second is default color,
                final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(AddPlanActivity.this, picked_Color,
                        new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // leave this function body as
                                // blank, as the dialog
                                // automatically closes when
                                // clicked on cancel button
                            }

                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // change the mDefaultColor to
                                // change the GFG text color as
                                // it is returned when the OK
                                // button is clicked from the
                                // color picker dialog
                                picked_Color = color;

                                //Save changes
                                DB.editPlanColor(picked_Color, plansId);

                                // now change the picked color
                                // preview box to mDefaultColor
                                Header.setTextColor(picked_Color);
                            }
                        });
                colorPickerDialogue.show();
            }
        });

        //Display all data
        EditText NameOfPlan = findViewById(R.id.editText_NameOfPlan);
        EditText NotesOfPlan = findViewById(R.id.editText_NotesForPlan);
        Header.setTextColor(picked_Color);

        //Notice data changes
        NameOfPlan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Save changes
                DB.editPlanName(NameOfPlan.getText().toString(), plansId);
            }
        });
        NotesOfPlan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Save changes
                DB.editPlanNotes(NotesOfPlan.getText().toString(), plansId);
            }
        });

        //Handle reminder TextView and its InputLayout
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        EditText VibratorTime = findViewById(R.id.editTextNumberDecimal_ReminderTime);
        TextInputLayout VibratorLayout = findViewById(R.id.textInputLayout_ReminderTime);
        VibratorLayout.setVisibility(GONE);
        DB.editPlanReminder(0, plansId);
        if (sharedPreferences.getBoolean("IsReminderEnabled", false)){
            VibratorLayout.setVisibility(VISIBLE);
            VibratorTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //Save changes
                    if (s.length() > 0 && !s.equals(" ")) {
                        DB.editPlanReminder(Float.parseFloat(VibratorTime.getText().toString()), plansId);
                    }
                }
            });
        }

        //Display RecyclerView of selected Exercises
        //Open Selector for new Exercise
        FloatingActionButton SelectToAdd = findViewById(R.id.floatingActionButton_SelectExerciseToAdd2);
        SelectToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.senderActivity = "AddPlanActivity";
                ExerciseSelectorBottomSheetDialog = new ExerciseSelector_BottomSheetDialog();
                ExerciseSelectorBottomSheetDialog.show(getSupportFragmentManager(), "ExerciseSelector_BottomSheet");
            }
        });

        WorkoutList = findViewById(R.id.RecyclerView_ExerciseOfPlan);
        WorkoutList.setLayoutManager(new GridLayoutManager(this, 1));

        WorkoutArrayList = new ArrayList<>();

        WorkoutListAdapter = new ExerciseCustomRecyclerViewAdapter(this, WorkoutArrayList);
        WorkoutList.setAdapter(WorkoutListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        WorkoutList.addItemDecoration(dividerItemDecoration);
        WorkoutListAdapter.setClickListener(AddPlanActivity.this);

        //Get Data & save them
        FloatingActionButton Add = findViewById(R.id.floatingActionButton_AddPlan2);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onDestroy() already handles this
                finish();
            }
        });

        //Delete Exercise if swiped, vgl.: https://stackoverflow.com/questions/40089542/add-swipe-right-to-delete-listview-item
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                CommonFunctions.applySwipeToDeleteForExercises(viewHolder, WorkoutListAdapter, AddPlanActivity.this, DB, plansId);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(WorkoutList);
    }

    //Notify Fragment to update its recyclerView
    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isItemAdded", true);
        // setResult(RESULT_OK);
        setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DB.closePlansDB();
        DB.close();
    }

    //Add selected Exercise to RecyclerView
    public void getSelectedInAdd(){
        ExerciseCustomRecyclerViewAdapter.CustomExerciseList selectedData = GlobalVariables.selectedData;
        if (selectedData != null) {
            String selectedName = selectedData.exercise_name;
            String selectedNote = selectedData.exercise_notes;
            Bitmap selectedImage = selectedData.exercise_image;

            int repetitions = GlobalVariables.getInstance().getRecordRepetitions();
            int weight = GlobalVariables.getInstance().getRecordWeight();
            int time = GlobalVariables.getInstance().getRecordTime();
            int distance = GlobalVariables.getInstance().getRecordDistance();
            int idInEDB = GlobalVariables.getInstance().getIdInEDB();

            countOfExercises = WorkoutArrayList.size() +1;

            Date dateFromCalender = Calendar.getInstance().getTime();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String currentDate = sdf.format(dateFromCalender);
            int id = DB.addSingleExercise(plansId, selectedNote, selectedName, selectedImage, repetitions, weight, time, distance, countOfExercises, currentDate, idInEDB);

            WorkoutArrayList.add(new ExerciseCustomRecyclerViewAdapter.CustomExerciseList(selectedName, selectedNote, selectedImage, false, false, id, this));
            WorkoutListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        //Get exerciseMainId => Pass it
        Cursor exerciseID = DB.getExerciseId(plansId);
        exerciseID.moveToPosition(position);
        //final ExerciseCustomRecyclerViewAdapter.CustomExerciseList item = WorkoutListAdapter.getData().get(position);
        int columnIndex = exerciseID.getColumnIndex("exerciseMainId");
        GlobalVariables.exerciseID = exerciseID.getInt(columnIndex);
        GlobalVariables.userIsTraining = false;

        DisplayExerciseValues_BottomSheetDialog DisplayExerciseValuesDialog = new DisplayExerciseValues_BottomSheetDialog();
        DisplayExerciseValuesDialog.show(getSupportFragmentManager(), "ExerciseValues_BottomSheet");
    }

    public void refreshItemClickListener(){
        WorkoutListAdapter.setClickListener(this);
    }

}
//Todo: Make items Sortable: https://medium.com/@Codeible/swipe-or-slide-and-drag-and-drop-items-in-recyclerview-6dbe4871f87