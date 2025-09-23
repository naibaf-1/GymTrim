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

package com.naibaf.GymTrim.Data;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.OtherClasses.RecyclerViewInflater;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment implements ExerciseCustomRecyclerViewAdapter.ItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ExerciseCustomRecyclerViewAdapter trainingListAdapter;
    ExercisesDB DB;

    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataFragment.
     */
    public static DataFragment newInstance(String param1, String param2) {
        DataFragment fragment = new DataFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_data, container, false);

        //Add Functions of data fragment
        //Calculate BMI
        FloatingActionButton CalculateBMI = v.findViewById(R.id.floatingActionButton_CalculateBMI);
        //MyLineGraph BMILineGraph = findViewById(R.id.MyLineGraph_BMI);
        CalculateBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open BMI Overlay
                openBMICalculator();
            }
        });
        TextView BMICalculator = v.findViewById(R.id.textView_CalculateBMI);
        BMICalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open BMI Overlay
                openBMICalculator();
            }
        });

        //Calculate WHR
        FloatingActionButton CalculateWHR = v.findViewById(R.id.floatingActionButton_CalculateWHR);
        CalculateWHR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open WHR Overlay
                openWHRCalculator();
            }
        });
        TextView WHRCalculator = v.findViewById(R.id.textView_CalculateWHR);
        WHRCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open WHR Overlay
                openWHRCalculator();
            }
        });

        //Calculate WHtR
        FloatingActionButton CalculateWHtR = v.findViewById(R.id.floatingActionButton_CalculateWHtR);
        CalculateWHtR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open WHtR Overlay
                openWHtRCalculator();
            }
        });
        TextView WHtRCalculator = v.findViewById(R.id.textView_CalculateWHtR);
        WHtRCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open WHtR Overlay
                openWHtRCalculator();
            }
        });

        //Add Data/Exercises to list => https://www.geeksforgeeks.org/how-to-dynamically-add-elements-to-a-listview-in-android/
        //!Add an own List with more Informations, e.g.: images: https://abhiandroid.com/ui/listview
        RecyclerView TrainingDataList  = v.findViewById(R.id.recyclerView_TrainingdataForExercises);

        //Create a Arraylist from ExerciseCustomRecyclerViewAdapter
        ArrayList ArrayListOfTrainingData = new ArrayList<>();

        // Now create the instance of the ExerciseCustomRecyclerViewAdapter and pass the context and ArrayListOfExercises created above
        trainingListAdapter = new ExerciseCustomRecyclerViewAdapter(getContext(), ArrayListOfTrainingData);
        DB = new ExercisesDB(getContext());
        Cursor trainingData = DB.getAllExerciseData();
        int columnIndexOfId = trainingData.getColumnIndex("Id");
        trainingListAdapter = RecyclerViewInflater.buildExerciseRecyclerView(getContext(), v, TrainingDataList, this, trainingListAdapter, trainingData, false, columnIndexOfId);

        //Search through exercises
        SearchView Search = v.findViewById(R.id.searchView_SearchForExercise);
        Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CommonFunctions.searchExerciseInExerciseRecyclerView(newText, trainingListAdapter);
                return true;
            }
        });
        return v;
    }

    @Override
    public void onItemClick(View view, int position) {
        final ExerciseCustomRecyclerViewAdapter.CustomExerciseList item = trainingListAdapter.getData().get(position);
        String selectedFromListName = item.exercise_name;
        int idOfSelected = item.idInDatabase;
        Intent ShowTrainingData = new Intent(getContext(), TrainingDataOfExerciseActivity.class);
        ShowTrainingData.putExtra("Selected", selectedFromListName);
        ShowTrainingData.putExtra("position", position);
        ShowTrainingData.putExtra("id", idOfSelected);
        startActivity(ShowTrainingData);
    }

    private void openWHtRCalculator(){
        WHtR_BottomSheetDialog WHtR_BottomSheetDialog = new WHtR_BottomSheetDialog();
        WHtR_BottomSheetDialog.show(getActivity().getSupportFragmentManager(), "WHtRBottomSheet");
    }

    private void openWHRCalculator(){
        WHR_BottomSheetDialog WHR_BottomSheetDialog = new WHR_BottomSheetDialog();
        WHR_BottomSheetDialog.show(getActivity().getSupportFragmentManager(), "WHRBottomSheet");
    }

    private void openBMICalculator(){
        BMI_BottomSheetDialog BMI_BottomSheetDialog = new BMI_BottomSheetDialog();
        BMI_BottomSheetDialog.show(getActivity().getSupportFragmentManager(), "BMIBottomSheet");
    }

}

//Draw Diagrams of the Using Data

//Add Graph: - https://www.infoworld.com/article/2256745/graphlib-an-open-source-android-library-for-graphs.html
//                 - https://medium.com/@mayurjajoomj/custom-graphs-custom-view-android-862e16813cc
//                 - https://www.geeksforgeeks.org/line-graph-view-in-android-with-example/
//                 - https://www.scichart.com/example/android-chart/android-line-chart-example/

//Implement Calculators: - https://www.bmi3d.de/kind.html
//                             - https://www.bmi3d.de/alternativen.html
//                             - https://www.aok.de/pk/magazin/ernaehrung/abnehmen/bmi-rechner-body-mass-index-berechnen/

//https://www.geeksforgeeks.org/implement-radiobutton-with-custom-layout-in-android/
//https://www.geeksforgeeks.org/modal-bottom-sheet-in-android-with-examples