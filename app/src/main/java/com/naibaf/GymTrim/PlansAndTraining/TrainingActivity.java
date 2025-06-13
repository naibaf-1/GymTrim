package com.naibaf.GymTrim.PlansAndTraining;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.OtherClasses.GlobalVariables;
import com.naibaf.GymTrim.OtherClasses.RecyclerViewInflater;
import com.naibaf.GymTrim.OtherClasses.SwipeToDeleteCallback;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity implements ExerciseCustomRecyclerViewAdapter.ItemClickListener {
    //Added stop watch => https://de.acervolima.com/so-erstellen-sie-eine-stoppuhr-app-mit-android-studio/
    //Resets with restart
    private int seconds = 0;
    // Is the stopwatch running?
    private boolean running;
    private boolean wasRunning;
    ExerciseSelector_BottomSheetDialog ExerciseSelectorBottomSheetDialog;
    List<ExerciseCustomRecyclerViewAdapter.CustomExerciseList> WorkoutArrayList;
    ExerciseCustomRecyclerViewAdapter WorkoutListAdapter;
    RecyclerView WorkoutList;
    TextView Name;
    String nameOfSelected;
    String notesOfSelected;
    PlansDB PDB;
    TextView Notes;
    Cursor specificPlanData;
    float vibratorOfSelected;
    int plansId;
    int countOfExercises;
    View v;
    int planColor;
    Cursor exerciseId;
    String timer;
    String timerInLastTraining;
    String dateOfLastTraining;
    String currentDate;
    int hours;
    int minutes;
    int secs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training);

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
        Intent getPlan = getIntent();
        nameOfSelected = getPlan.getStringExtra("name");
        plansId = getPlan.getIntExtra("plansId", -1);
        notesOfSelected = getPlan.getStringExtra("notes");
        vibratorOfSelected = getPlan.getIntExtra("vibrator", 0);
        planColor = getPlan.getIntExtra("color", 0);
        timerInLastTraining = getPlan.getStringExtra("lastTrainingDurationOfTimer");
        dateOfLastTraining = getPlan.getStringExtra("lastTrainingDate");

        //Get date of this training
        Date dateFromCalender = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(dateFromCalender);

        if (currentDate.equals(dateOfLastTraining)  && !timerInLastTraining.equals("firstTimeTrained")){
            //Split timerInLastTraining into hours, minutes & seconds
            String[] timeParts = timerInLastTraining.split(":");
            //Extract hours, minutes & seconds => Parse them to Integers
            hours = Integer.parseInt(timeParts[0]);
            minutes = Integer.parseInt(timeParts[1]);
            secs = Integer.parseInt(timeParts[2]);

            seconds = hours * 3600 + minutes * 60 + secs;
        }

        //Leave Button
        ImageButton Leave = findViewById(R.id.imageButton_Leave);
        Leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPrevious();
            }
        });
        
        //Open Selector for new Exercise
        FloatingActionButton SelectToAdd = findViewById(R.id.floatingActionButton_AddExerciseForTraining);
        SelectToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.senderActivity = "TrainingActivity";
                ExerciseSelectorBottomSheetDialog = new ExerciseSelector_BottomSheetDialog();
                ExerciseSelectorBottomSheetDialog.show(getSupportFragmentManager(), "ExerciseSelector_BottomSheet");
            }
        });

        //Fill everything with data from PlansDB & write it into history
        PDB = new PlansDB(com.naibaf.GymTrim.PlansAndTraining.TrainingActivity.this);

        //Get exerciseMainId => Pass it
        exerciseId = PDB.getExerciseId(plansId);
        exerciseId.moveToFirst();

        if (plansId >= 0){
            specificPlanData = PDB.getBasicPlanData(plansId);
            //Get ColumnIndexes

            //Get missing values of the plan
            specificPlanData.moveToFirst();

            //Display necessary data
            Name = findViewById(R.id.textView_toolbarTitle);
            Name.setText(nameOfSelected);
            Name.setTextColor(planColor);
            Notes = findViewById(R.id.textView_TrainingsNotes);
            Notes.setText(notesOfSelected);

            //Display all exercises using a recyclerView
            WorkoutList = findViewById(R.id.recyclerView_Training);

            WorkoutArrayList = new ArrayList<>();
            specificPlanData = PDB.getAllPlanExercises(plansId);
            WorkoutListAdapter = new ExerciseCustomRecyclerViewAdapter(this, WorkoutArrayList);

            int columnIndexOfId = specificPlanData.getColumnIndex("exerciseMainId");

            WorkoutListAdapter = RecyclerViewInflater.buildExerciseRecyclerView(this, v, WorkoutList, this, WorkoutListAdapter, specificPlanData, true, columnIndexOfId);
            WorkoutArrayList = RecyclerViewInflater.ExerciseArrayList;
            specificPlanData = RecyclerViewInflater.data2;
            specificPlanData.moveToFirst();

        } else {
            Toast.makeText(TrainingActivity.this, R.string.error_missing_plan, Toast.LENGTH_SHORT);
        }

        //Delete Exercise if swiped: vgl.: https://stackoverflow.com/questions/40089542/add-swipe-right-to-delete-listview-item
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                CommonFunctions.applySwipeToDeleteForExercises(viewHolder, WorkoutListAdapter, TrainingActivity.this, PDB, plansId);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(WorkoutList);

        //Timer:
        //Start running the timer if layout is launched
        running = true;

        // Use seconds, running and wasRunning respectively
        // to record the number of seconds passed,
        // whether the stopwatch is running and
        // whether the stopwatch was running
        // before the activity was paused.

        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        FloatingActionButton Finish = findViewById(R.id.floatingActionButton_FinishTraining);
        Finish.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //confirmToLeave();
                returnToPrevious();
            }
        });
        runTimer();
    }

    @Override
    public void onBackPressed() {
        returnToPrevious();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String name = Name.getText().toString();
        String notes = Notes.getText().toString();

        specificPlanData.moveToFirst();

        //Update Data
        Boolean checkUpdate = false;
        if (!name.isEmpty()){
            checkUpdate = PDB.updateTrainingData(plansId, name, notes, currentDate, timer);
        }

        if (checkUpdate){
            Toast.makeText(com.naibaf.GymTrim.PlansAndTraining.TrainingActivity.this, R.string.notification_succesful_inserted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(com.naibaf.GymTrim.PlansAndTraining.TrainingActivity.this, R.string.error_notification_not_inserted, Toast.LENGTH_SHORT).show();
        }

        PDB.close();
        specificPlanData.close();
    }

    // Save the state of the stopwatch if it's about to be destroyed.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

//        // If the activity is paused,
//        // stop the stopwatch.
//        @Override
//        protected void onPause()
//        {
//            super.onPause();
//            wasRunning = running;
//            running = false;
//        }
//
//        // If the activity is resumed,
//        // start the stopwatch
//        // again if it was running previously.
//        @Override
//        protected void onResume()
//        {
//            super.onResume();
//            if (wasRunning) {
//                running = true;
//            }
//        }

        // Sets the Number of seconds on the timer.
        // The runTimer() method uses a Handler
        // to increment the seconds and
        // update the text view.
    private void runTimer() {

        // Get the text view.
        final TextView timeView = findViewById(R.id.textView_TrainingsTime);

        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run() {
                hours = seconds / 3600;
                minutes = (seconds % 3600) / 60;
                secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                timer = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);

                // Set the text view text.
                timeView.setText(timer);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }
    @Override
    public void onItemClick(View view, int position) {
        //Get exerciseMainId => Pass it
        exerciseId = PDB.getExerciseId(plansId);
        exerciseId.moveToPosition(position);

        int columnIndex = exerciseId.getColumnIndex("exerciseMainId");
        GlobalVariables.exerciseID = exerciseId.getInt(columnIndex);
        GlobalVariables.userIsTraining = true;
        //GlobalVariables.selectedPlanVibrator = vibratorOfSelected;
        GlobalVariables.trainingSelectedExercise = position;
        DisplayExerciseValues_BottomSheetDialog DisplayExerciseValuesDialog = new DisplayExerciseValues_BottomSheetDialog();
        DisplayExerciseValuesDialog.show(getSupportFragmentManager(), "ExerciseValues_BottomSheet");
    }

    //Add selected Exercise to RecyclerView
    public void getSelectedInTraining(){
        ExerciseCustomRecyclerViewAdapter.CustomExerciseList selectedData = GlobalVariables.selectedData;
        if (selectedData != null) {
            Bitmap selectedImage = selectedData.exercise_image;
            String name = selectedData.exercise_name;
            String notes = selectedData.exercise_notes;

            int repetitions = GlobalVariables.getInstance().getRecordRepetitions();
            int weight = GlobalVariables.getInstance().getRecordWeight();
            int time = GlobalVariables.getInstance().getRecordTime();
            int distance = GlobalVariables.getInstance().getRecordDistance();
            int idInEDB = GlobalVariables.getInstance().getIdInEDB();

            countOfExercises = WorkoutArrayList.size() +1;

            Date dateFromCalender = Calendar.getInstance().getTime();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String currentDate = sdf.format(dateFromCalender);
            int id = PDB.addSingleExercise(plansId, name, notes, selectedImage, repetitions, weight, time, distance, countOfExercises, currentDate, idInEDB);

            WorkoutArrayList.add(new ExerciseCustomRecyclerViewAdapter.CustomExerciseList(name, notes, selectedImage, false, true, id, this));
            WorkoutListAdapter.notifyDataSetChanged();
        }
    }

    //If user has completed an exercise mark its item in the recyclerView as done, otherwise as undone
    public void markExercise(int positionInRecyclerView, Boolean exerciseCompleted, int volume){
        WorkoutListAdapter.getData().get(positionInRecyclerView).exerciseIsDone = exerciseCompleted;
        WorkoutListAdapter.getData().get(positionInRecyclerView).volume = volume;
        WorkoutArrayList = WorkoutListAdapter.getData();
        WorkoutListAdapter = new ExerciseCustomRecyclerViewAdapter(this, WorkoutArrayList);
        WorkoutList.setAdapter(WorkoutListAdapter);
    }
    public void refreshItemClickListener(){
        WorkoutListAdapter.setClickListener(this);
    }

    //Go to previous Activity
    private void returnToPrevious(){
        Intent Leave = new Intent(TrainingActivity.this, EditPlanActivity.class);
        Leave.putExtra("name", nameOfSelected);
        Leave.putExtra("color", planColor);
        Leave.putExtra("id", plansId);
        startActivity(Leave);
        finish();
    }

}
//Todo: Add heartbeat monitor