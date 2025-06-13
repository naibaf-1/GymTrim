package com.naibaf.GymTrim.Data;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.color.MaterialColors;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;

import java.util.ArrayList;

public class TrainingDataOfExerciseActivity extends AppCompatActivity {

    ExercisesDB DB;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training_data_of_exercise);

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

        //Start here
        //Receive necessary data
        DB = new ExercisesDB(TrainingDataOfExerciseActivity.this);
        Intent getValues = getIntent();
        int positionInRecyclerView = getValues.getIntExtra("position", 0);
        int idOfSelected = getValues.getIntExtra("id", 0);
        String nameOfSelected = getValues.getStringExtra("Selected");
        Cursor weights = DB.getAllWeightAverageForExercise(idOfSelected);
        weights.moveToFirst();
        Cursor times = DB.getAllTimeAverageForExercise(idOfSelected);
        times.moveToFirst();
        Cursor distances = DB.getAllDistanceAverageForExercise(idOfSelected);
        distances.moveToFirst();
        Cursor volumes = DB.getAllVolumesForExercise(idOfSelected);
        volumes.moveToFirst();

       ImageButton Close = findViewById(R.id.imageButton_close5);
       Close.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });

        TextView DisplayName = findViewById(R.id.textView_NameOfSelected);
        DisplayName.setText(nameOfSelected);

        //Check which graphs should be drawn & hide unnecessary
        Cursor wishedGraphs = DB.getExerciseData(idOfSelected);
        wishedGraphs.moveToFirst();
        int columnIndexOfWeightBoolean = wishedGraphs.getColumnIndex("RecordWeight");
        int columnIndexOfDistanceBoolean = wishedGraphs.getColumnIndex("RecordDistance");
        int columnIndexOfTimeBoolean = wishedGraphs.getColumnIndex("RecordTime");
        int columnIndexOfRepetitionsBoolean = wishedGraphs.getColumnIndex("RecordSentences");

        Boolean weightAverageShouldBeDrawn = CommonFunctions.getBooleanValueOfInteger(wishedGraphs.getInt(columnIndexOfWeightBoolean));
        Boolean distanceAverageShouldBeDrawn = CommonFunctions.getBooleanValueOfInteger(wishedGraphs.getInt(columnIndexOfDistanceBoolean));
        Boolean timeAverageShouldBeDrawn = CommonFunctions.getBooleanValueOfInteger(wishedGraphs.getInt(columnIndexOfTimeBoolean));
        Boolean volumeShouldBeDrawn = CommonFunctions.getBooleanValueOfInteger(wishedGraphs.getInt(columnIndexOfRepetitionsBoolean));

        //Draw graphs, if their data is documented
        //Draw graph for weight
        LineChart WeightChart = findViewById(R.id.lineChart_WeightAverage);
        TextView WeightHeader = findViewById(R.id.textView_WeightAverageHeader);
        if (weightAverageShouldBeDrawn){
            WeightChart.setVisibility(VISIBLE);
            buildChart(WeightChart, weights, getString(R.string.training_data_label_weight_average));
            WeightHeader.setVisibility(VISIBLE);
        } else {
            WeightChart.setVisibility(GONE);
            WeightHeader.setVisibility(GONE);
        }

        //Draw graph for distance
        LineChart DistanceChart = findViewById(R.id.lineChart_DistanceAverage);
        TextView DistanceHeader = findViewById(R.id.textView_DistanceAverageHeader);
        if (distanceAverageShouldBeDrawn) {
            DistanceChart.setVisibility(VISIBLE);
            buildChart(DistanceChart, distances, getString(R.string.training_data_label_distance_average));
            DistanceHeader.setVisibility(VISIBLE);
        } else {
            DistanceChart.setVisibility(GONE);
            DistanceHeader.setVisibility(GONE);
        }

        //Draw graph for times
        LineChart TimeChart = findViewById(R.id.lineChart_TimeAverage);
        TextView TimeHeader = findViewById(R.id.textView_TimeAverageHeader);
        if (timeAverageShouldBeDrawn) {
            TimeChart.setVisibility(VISIBLE);
            buildChart(TimeChart, times, getString(R.string.training_data_label_time_average));
            TimeHeader.setVisibility(VISIBLE);
        } else {
            TimeChart.setVisibility(GONE);
            TimeHeader.setVisibility(GONE);
        }

        //Draw graph for volumes
        LineChart VolumeChart = findViewById(R.id.lineChart_Volume);
        TextView VolumeHeader = findViewById(R.id.textView_VolumeHeader);
        if(volumeShouldBeDrawn){
            VolumeChart.setVisibility(VISIBLE);
            buildChart(VolumeChart, volumes, getString(R.string.training_data_label_volume));
            VolumeHeader.setVisibility(VISIBLE);
        } else {
            VolumeChart.setVisibility(GONE);
            VolumeHeader.setVisibility(GONE);
        }

        //Display date of last training
        int columnIndexOfLastTraining = wishedGraphs.getColumnIndex("LastlyTrained");
        TextView DateOfLastTraining = findViewById(R.id.textView_DateOfLastTrainingOfExercise);
        String lastTrained = getString(R.string.training_data_last_trained);
        DateOfLastTraining.setText(lastTrained + " " + wishedGraphs.getString(columnIndexOfLastTraining));
    }

    public void buildChart(LineChart chart, Cursor data, String label){
        //Fill data points from DB
        ArrayList<Entry> entries = new ArrayList<>();
        data.moveToFirst();
        int amountOfWeightData = data.getCount();
        for (int x = 0; x < amountOfWeightData; x++){
            int y = data.getInt(0);
            entries.add(new Entry(x, y));
            data.moveToNext();
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryInverse, Color.GREEN));
        dataSet.setValueTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.editTextColor, Color.GREEN));
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        //Change color of x-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.editTextColor, Color.GRAY));

        //Change color of y-axis
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.editTextColor, Color.GRAY));

        //Change Color of legend
        chart.getLegend().setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.editTextColor, Color.GRAY));

        //Activate auto-scale
        yAxis.setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getDescription().setEnabled(false);

        //Update
        chart.invalidate();
    }

}