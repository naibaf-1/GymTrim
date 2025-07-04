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

package com.naibaf.GymTrim.SQLiteDatabases;

import static com.naibaf.GymTrim.OtherClasses.CommonFunctions.getIntegerValueOfBoolean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExercisesDB extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "GymTrim-Exercises.db";

    // Database Version => increase after changes of tables!
    private static final int DATABASE_VERSION = 2;

    public ExercisesDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        try {
            // Enable foreign key support in SQLite
            DB.execSQL("PRAGMA foreign_keys = ON;");
            // Proceed to create tables with foreign key constraints
            DB.execSQL(" create Table ExerciseDetails (Id INTEGER primary key autoincrement, NameOfExercise TEXT, RecordWeight INTEGER, RecordDistance INTEGER, RecordRepetitions INTEGER, RecordTime INTEGER, ImageOfExercise BLOB, NotesForExercise TEXT, LastlyTrained TEXT) ");
            DB.execSQL(" create Table TrainingData (trainingdataMainId INTEGER primary key autoincrement, Volume INTEGER, AverageWeight FLOAT, AverageTime FLOAT, AverageDistance FLOAT, Date TEXT, ExerciseId INTEGER, FOREIGN KEY (ExerciseId) REFERENCES ExerciseDetails(Id) ON DELETE CASCADE) ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL(" drop Table if exists ExerciseDetails ");
        DB.execSQL(" drop Table if exists TrainingData ");
        DB.execSQL(" create Table ExerciseDetails (Id INTEGER primary key autoincrement, NameOfExercise TEXT, RecordWeight INTEGER, RecordDistance INTEGER, RecordRepetitions INTEGER, RecordTime INTEGER, ImageOfExercise BLOB, NotesForExercise TEXT, LastlyTrained TEXT) ");
        DB.execSQL(" create Table TrainingData (trainingdataMainId INTEGER primary key autoincrement, Volume INTEGER, AverageWeight FLOAT, AverageTime FLOAT, AverageDistance FLOAT, Date TEXT, ExerciseId INTEGER, FOREIGN KEY (ExerciseId) REFERENCES ExerciseDetails(Id) ON DELETE CASCADE) ");
    }

    public int createNewExercise(){
        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("NameOfExercise", "*empty*");

        long result = DB.insert("ExerciseDetails", null, contentvalues);
        DB.close();
        return (int) result;
    }

    //Functions for auto-save
    public void updateName(int Id, String newName){
        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("NameOfExercise", newName);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }
    public void updateNotes(int Id, String newNotes){
        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("NotesForExercise", newNotes);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }
    public void updateImage(int Id, byte[] newImage){
        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("ImageOfExercise", newImage);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }
    public void updateCheckWeight(int Id, boolean recordWeight){
        int weight = getIntegerValueOfBoolean(recordWeight);

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("RecordWeight", weight);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }
    public void updateCheckTime(int Id, boolean recordTime){
        int time = getIntegerValueOfBoolean(recordTime);

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("RecordTime", time);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }
    public void updateCheckRepetitions(int Id, boolean recordRepetitions){
        int repetitions = getIntegerValueOfBoolean(recordRepetitions);

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("RecordRepetitions", repetitions);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }
    public void updateCheckDistance(int Id, boolean recordDistance){
        int distance = getIntegerValueOfBoolean(recordDistance);

        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("RecordDistance", distance);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(Id)});
        DB.close();
    }

    public Boolean deleteExerciseData(int exerciseId) {
        SQLiteDatabase DB = this.getWritableDatabase();
        //Select the row
        Cursor cursor = DB.rawQuery(" Select * From ExerciseDetails Where Id = ? ", new String[]{String.valueOf(exerciseId)});
        long result = DB.delete("ExerciseDetails", "Id = ?", new String[]{String.valueOf(exerciseId)});
        if (cursor.getCount() > 0) {
            DB.close();
            //If Failed: false
            return result != -1;
        } else {
            DB.close();
            return false;
        }
    }

    //Get a specific exercise and its data
    public Cursor getExerciseData(int id){
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor exerciseData = DB.rawQuery(" Select * From ExerciseDetails Where Id = ? ", new String[]{String.valueOf(id)});
        return exerciseData;
    }

    //Get all exercises and their data
    public Cursor getAllExerciseData(){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor exerciseDataForAll = DB.rawQuery(" Select * From ExerciseDetails Order By NameOfExercise ", null);
        return exerciseDataForAll;
    }

    public void completelyRemoveEntireData(){
        SQLiteDatabase DB = this.getWritableDatabase();

        DB.delete("ExerciseDetails", null, null);
        DB.close();
    }

    public void editDateOfLastTraining(int exerciseId, String dateOfLastTraining){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("LastlyTrained", dateOfLastTraining);
        DB.update("ExerciseDetails", contentValues, "Id = ?", new String[]{String.valueOf(exerciseId)});
        DB.close();
    }

    //Insert calculated training data
    public void insertNewTrainingsData(int exerciseId, int volume, float averageOfWeight, float averageOfTime, float averageOfDistance, String date){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Volume", volume);
        contentValues.put("ExerciseId", exerciseId);
        contentValues.put("AverageWeight", averageOfWeight);
        contentValues.put("AverageTime", averageOfTime);
        contentValues.put("AverageDistance", averageOfDistance);
        contentValues.put("Date", date);

        DB.insert("TrainingData", null, contentValues);
        DB.close();
    }
    //Return all calculated training data => Display in graph
    public Cursor getAllVolumesForExercise(int exerciseId) {
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("SELECT Volume FROM ExerciseDetails e INNER JOIN TrainingData t ON e.Id = t.ExerciseId WHERE t.ExerciseId = ? ORDER BY t.Date", new String[]{String.valueOf(exerciseId)});
    }
    public Cursor getAllWeightAverageForExercise(int exerciseId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("SELECT AverageWeight FROM ExerciseDetails e INNER JOIN TrainingData t ON e.Id = t.ExerciseId WHERE t.ExerciseId = ? ORDER BY t.Date", new String[]{String.valueOf(exerciseId)});
    }
    public Cursor getAllTimeAverageForExercise(int exerciseId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("SELECT AverageTime FROM ExerciseDetails e INNER JOIN TrainingData t ON e.Id = t.ExerciseId WHERE t.ExerciseId = ? ORDER BY t.Date", new String[]{String.valueOf(exerciseId)});
    }
    public Cursor getAllDistanceAverageForExercise(int exerciseId){
        SQLiteDatabase DB = this.getReadableDatabase();
        return DB.rawQuery("SELECT AverageDistance FROM ExerciseDetails e INNER JOIN TrainingData t ON e.Id = t.ExerciseId WHERE t.ExerciseId = ? ORDER BY t.Date", new String[]{String.valueOf(exerciseId)});
    }

}
