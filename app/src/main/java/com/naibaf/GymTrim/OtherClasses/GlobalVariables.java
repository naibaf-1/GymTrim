package com.naibaf.GymTrim.OtherClasses;

import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;

//How to make Global Variables => https://www.tutorialspoint.com/how-to-declare-global-variables-in-android
public class GlobalVariables {

    //Pass certain exerciseIDs to DisplayExerciseValues_BottomSheetDialog
    public static int exerciseID;


    //Identify class which launches the ExerciseSelectorBottomSheetDialog
    public static String senderActivity;


    //Pass the name of new added exercise from ExerciseSelector to the Activity which launched it
    public static ExerciseCustomRecyclerViewAdapter.CustomExerciseList selectedData;
    public static int recordRepetitions;
    public static int recordWeight;
    public static int recordTime;
    public static int recordDistance;
    public static int idInEDB;


    //Notify DisplayExerciseValues_BottomSheetDialog whether user trains or not
    public static Boolean userIsTraining;


    //Tell DisplayExerciseValue_BottomSheetDialog the position of the selected Exercise in the recyclerView
    public static int trainingSelectedExercise;


    //Tell DisplayExerciseValues_BottomSheetDialog the duration of the Vibrator (Directly from EditPlanActivity)
    public static float selectedPlanVibrator = 0;


    private static final GlobalVariables ourInstance = new GlobalVariables();
    public static GlobalVariables getInstance() {
        return ourInstance;
    }
    private GlobalVariables() {
    }

    //Pass the name of new added exercise from ExerciseSelector to the Activity which launched it
    public void setSelected(ExerciseCustomRecyclerViewAdapter.CustomExerciseList SelectedData, int repetitions, int weight, int distance, int time, int idInEDB){
        selectedData = SelectedData;
        recordRepetitions = repetitions;
        recordDistance = distance;
        recordTime = time;
        recordWeight = weight;
        GlobalVariables.idInEDB = idInEDB;
    }
    public ExerciseCustomRecyclerViewAdapter.CustomExerciseList getSelectedData(){
        return selectedData;
    }
    public int getRecordRepetitions(){
        return recordRepetitions;
    }
    public int getIdInEDB(){
        return idInEDB;
    }
    public int getRecordWeight(){
        return recordWeight;
    }
    public int getRecordTime(){
        return recordTime;
    }
    public int getRecordDistance(){
        return recordDistance;
    }

    public ExerciseCustomRecyclerViewAdapter.CustomExerciseList getSelected(){
        return selectedData;
    }

    //Tell DisplayExerciseValues_BottomSheetDialog the duration of the Vibrator
    public float getReminderDuration(){
        if (selectedPlanVibrator != 0){
            return selectedPlanVibrator;
        } else {
            return 0;
        }
    }
}

//Note: https://stackoverflow.com/questions/10020744/android-whats-the-better-practice-using-a-global-string-or-intents-with-extra => Intents won't be lost due calls, etc.