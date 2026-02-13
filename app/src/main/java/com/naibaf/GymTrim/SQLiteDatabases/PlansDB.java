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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
public class PlansDB extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "GymTrim-Plans.db";

    // Database Version => increase after changes of tables!
    private static final int DATABASE_VERSION = 2;
    private final SQLiteDatabase DB;

    public PlansDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        try {
            DB.execSQL("PRAGMA foreign_keys = ON;");
            DB.execSQL("CREATE TABLE PlansDetails (" + "plansId INTEGER PRIMARY KEY AUTOINCREMENT, " + "NameOfPlan TEXT, " + "ColorOfPlan INTEGER, " + "NotesForPlan TEXT, " + "ReminderTime FLOAT, " + "DateOfLastTraining TEXT, " + "TimerDurationWhenTrainingLeft TEXT" + ");");
            DB.execSQL("CREATE TABLE Exercise (" + "exerciseMainId INTEGER PRIMARY KEY AUTOINCREMENT, " + "DateOfLastTraining TEXT, " + "NameOfExercise TEXT, " + "ImageOfExercise BLOB, " + "NotesForExercise TEXT, " + "RecordWeight INTEGER, " + "RecordTime INTEGER, " + "RecordDistance INTEGER, " + "RecordRepetitions INTEGER, " + "ExerciseID INTEGER, " + "ExerciseIdInEDB INTEGER, " + "ExerciseOrder INTEGER, " + "FOREIGN KEY (ExerciseID) REFERENCES PlansDetails(plansId) ON DELETE CASCADE" + ");");
            DB.execSQL("CREATE TABLE ExerciseTable (" + "tableMainId INTEGER PRIMARY KEY AUTOINCREMENT, " + "RecordWeight REAL, " + "RecordDistance REAL, " + "RecordSentences INTEGER, " + "RecordTime NUMERIC, " + "RowNumber INTEGER, " + "RowIsDone INTEGER, " + "TableId INTEGER, " + "FOREIGN KEY (TableId) REFERENCES Exercise(exerciseMainId) ON DELETE CASCADE" + ");");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL(" drop Table if exists PlansDetails ");
        DB.execSQL("drop Table if exists Exercise");
        DB.execSQL("drop Table if exists ExerciseTable ");

        DB.execSQL(" create Table PlansDetails (plansId INTEGER primary key autoincrement, NameOfPlan TEXT,  ColorOfPlan INTEGER, NotesForPlan TEXT, ReminderTime FLOAT, DateOfLastTraining TEXT, TimerDurationWhenTrainingLeft TEXT) ");
        DB.execSQL(" create Table Exercise (exerciseMainId INTEGER primary key autoincrement, DateOfLastTraining TEXT, NameOfExercise TEXT, ImageOfExercise BLOB, NotesForExercise TEXT, RecordWeight INTEGER, RecordTime INTEGER, RecordDistance INTEGER, RecordRepetitions INTEGER, ExerciseID INTEGER, ExerciseIdInEDB INTEGER, ExerciseOrder INTEGER, FOREIGN KEY (ExerciseID) REFERENCES PlansDetails(plansId) ON DELETE CASCADE) ");
        DB.execSQL(" create Table ExerciseTable (tableMainId INTEGER primary key autoincrement, RecordWeight REAL, RecordDistance REAL, RecordSentences INTEGER, RecordTime NUMERIC, RowNumber INTEGER, RowIsDone INTEGER, TableId INTEGER, FOREIGN KEY (TableId) REFERENCES Exercise(exerciseMainId) ON DELETE CASCADE ) ");
    }

    //Delete data
    public void deletePlanData(int plansId) {
        //Delete the plan itself
        Cursor cursor = DB.rawQuery(" Select * From PlansDetails Where plansId = ? ", new String[]{String.valueOf(plansId)});
        DB.delete("PlansDetails", "plansId = ?", new String[]{String.valueOf(plansId)});

        //rowId.close();
        cursor.close();
    }

    //Get all plans and their data from PlansDetails
    public Cursor getAllPlans(){
        // Convert dd-mm-yyyy to yyyy-mm-dd so that the plans will be sorted correctly
        Cursor planData = DB.rawQuery(" Select * From PlansDetails Order By substr(DateOfLastTraining, 7, 4) || '-' || substr(DateOfLastTraining, 4, 2) || '-' || substr(DateOfLastTraining, 1, 2) DESC", null);
        return planData;
    }

    //Delete exercise
    public Boolean deleteExerciseData(int exerciseId) {
        //Select the row
        Cursor cursor = DB.rawQuery(" Select * From Exercise Where exerciseMainId = ? ", new String[]{String.valueOf(exerciseId)});
        long result = DB.delete("Exercise", "exerciseMainId = ?", new String[]{String.valueOf(exerciseId)});
        if (cursor.getCount() > 0) {
            cursor.close();
            //If Failed: false
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    //Get data of a specific exercise
    public Cursor getBasicExerciseData(int exerciseId){
        Cursor exerciseData = DB.rawQuery(" Select * From PlansDetails p INNER JOIN Exercise e ON p.plansId = e.ExerciseID Where e.exerciseMainId = ? ", new String[]{String.valueOf(exerciseId)});
        return exerciseData;
    }
    public int addSingleExercise(int plansId, String notes, String name, Bitmap image, int repetitions, int weight, int time, int distance, int order, String date, int idInEDB){
        //Insert data correctly
        ContentValues contentValues = new ContentValues();
        contentValues.put("NameOfExercise", name);
        contentValues.put("ImageOfExercise", CommonFunctions.getBitmapAsArray(image));
        contentValues.put("NotesForExercise", notes);
        contentValues.put("ExerciseID", plansId);
        contentValues.put("ExerciseOrder", order);
        contentValues.put("RecordRepetitions", repetitions);
        contentValues.put("RecordDistance", distance);
        contentValues.put("RecordWeight", weight);
        contentValues.put("RecordTime", time);
        contentValues.put("DateOfLastTraining", date);
        contentValues.put("ExerciseIdInEDB", idInEDB);

        long check = DB.insert("Exercise", null, contentValues);
        return (int) check;
    }

    //Get exercise Id
    public Cursor getExerciseId(int plansId){
        Cursor data = DB.rawQuery(" Select exerciseMainId From Exercise  WHERE ExerciseID = ?  ORDER BY ExerciseOrder", new String[]{String.valueOf(plansId)});
        return data;
    }

    //Get id of certain rows
    public Cursor getRowId(int exerciseId){
        Cursor data = DB.rawQuery(" Select tableMainId From ExerciseTable  WHERE TableId = ?  ORDER BY RowNumber", new String[]{String.valueOf(exerciseId)});
        return data;
    }

    //Insert everything at once
    public int insertNewPlan(){
        ContentValues contentValue = new ContentValues();
        contentValue.put("NameOfPlan", "*empty*");
        long plansId = DB.insert("PlansDetails", null, contentValue);
        return (int) plansId;
    }

    //Get all data of an specific plan at once
    public Cursor getAllPlanExercises(int plansId){
        Cursor data = DB.rawQuery(" Select * From PlansDetails p INNER JOIN Exercise e ON p.plansId = e.ExerciseID WHERE p.plansId = ? Order By e.ExerciseOrder", new String[]{String.valueOf(plansId)});
        return data;
    }
    public Cursor getBasicPlanData(int plansId){
        Cursor data = DB.rawQuery(" Select * From PlansDetails WHERE plansId = ?", new String[]{String.valueOf(plansId)});
        return data;
    }
    public Boolean updateTrainingData(int planId, String planName, String planNotes, String date, String timerDuration){
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("NameOfPlan", planName);
        contentvalues.put("NotesForPlan", planNotes);
        contentvalues.put("DateOfLastTraining", date);
        contentvalues.put("TimerDurationWhenTrainingLeft", timerDuration);
        DB.update("PlansDetails", contentvalues, "plansId = ?", new String[]{String.valueOf(planId)});

        return true;
    }
    public int addExerciseTableRow(int exerciseId, float weight, int repetitions, float time, float distance, int order) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("RecordWeight", weight);
        contentValues.put("RecordDistance", distance);
        contentValues.put("RecordTime", time);
        contentValues.put("RecordSentences", repetitions);
        contentValues.put("TableId", exerciseId);
        contentValues.put("RowNumber", order);
        contentValues.put("RowIsDone", 0);
        long check = DB.insert("ExerciseTable", null, contentValues);
        return (int) check;
    }
    public Cursor getExerciseTableRows(int exerciseId){
        Cursor rows = DB.rawQuery(" SELECT * FROM Exercise e INNER JOIN ExerciseTable t ON e.exerciseMainId = t.TableId WHERE t.TableId = ? ORDER BY t.RowNumber", new String[]{String.valueOf(exerciseId)});
        return rows;
    }
    public void updateExerciseTableRows(int exerciseId, float weight, int repetitions, float time, float distance, int order){
        ContentValues contentValues = new ContentValues();
        contentValues.put("RecordWeight", weight);
        contentValues.put("RecordDistance", distance);
        contentValues.put("RecordTime", time);
        contentValues.put("RecordSentences", repetitions);
        contentValues.put("TableId", exerciseId);
        DB.execSQL("UPDATE ExerciseTable SET RecordWeight = " + weight + " , RecordDistance = " + distance + " , RecordTime = " + time+ ", RecordSentences = " + repetitions + " , TableId = " + exerciseId + " WHERE TableId = " + exerciseId + " AND RowNumber = " + order);
    }
    public void updateRowDone(int tableRowId, Boolean rowIsDone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("RowIsDone", CommonFunctions.getIntegerValueOfBoolean(rowIsDone));
        db.update("ExerciseTable", contentValues, "tableMainId = ?", new String[]{String.valueOf(tableRowId)});
    }

    //Note when a specific exercise was trained before
    public void setDateOfCurrentTrainingForExercise(int exerciseId, String date){
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("DateOfLastTraining", date);
        DB.update("Exercise", contentValues, "exerciseMainId = ?", new String[]{String.valueOf(exerciseId)});
    }

    //Get when a specific exercise was trained before
    public String getDateOfExercise(int exerciseID){
        Cursor date = DB.rawQuery("SELECT DateOfLastTraining FROM Exercise WHERE exerciseMainId = ?", new String[]{String.valueOf(exerciseID)});
        date.moveToFirst();
        int columnIndex = date.getColumnIndex("DateOfLastTraining");
        return date.getString(columnIndex);
    }

    //Delete row
    public void deleteExerciseTableRow(int rowId){
        //Select the row
        DB.rawQuery(" Select * From ExerciseTable Where tableMainId = ? ", new String[]{String.valueOf(rowId)});
        DB.delete("ExerciseTable", "tableMainId = ?", new String[]{String.valueOf(rowId)});
    }

    //Functions for fast auto-save
    public void editPlanName(String newName, int plansId){
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("NameOfPlan", newName);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
    }
    public void editPlanNotes(String newNotes, int plansId){
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("NotesForPlan", newNotes);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
    }
    public void editPlanColor(int planColor, int plansId){
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("ColorOfPlan", planColor);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
    }
    public void editPlanReminder(float duration, int plansId){
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("ReminderTime", duration);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
    }
    public void completelyRemoveEntireData() {
        DB.delete("PlansDetails", null, null);
        DB.delete("Exercise", null, null);
        DB.delete("ExerciseTable", null, null);
        DB.close();
    }
    public int getIdOfExerciseInEDB(int exerciseId){
        Cursor ids = DB.rawQuery("SELECT ExerciseIdInEDB FROM Exercise WHERE exerciseMainId = ?", new String[]{String.valueOf(exerciseId)});
        ids.moveToFirst();
        int columnIndexOfId = ids.getColumnIndex("ExerciseIdInEDB");
        return ids.getInt(columnIndexOfId);
    }

    //Update exercises too when in editor changed
    public Boolean checkIfEditedExerciseExistsInPDB(int idOfEditedInEDB){
        Cursor check = DB.rawQuery("Select ExerciseIdInEDB FROM Exercise WHERE ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        if (check.getCount() != 0){
            return true; //It exists
        } else {
            return false; //It doesn't exist
        }
    }
    public void updateNameOfEditedExercise(int idOfEditedInEDB, String newName){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("NameOfExercise", newName);
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }
    public void updateNotesOfEditedExercise(int idOfEditedInEDB, String newNotes){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("NotesForExercise", newNotes);
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }
    public void updateWeightAllowedOfEditedExercise(int idOfEditedInEDB, Boolean isWeightAllowed){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordWeight", CommonFunctions.getIntegerValueOfBoolean(isWeightAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }
    public void updateTimeAllowedOfEditedExercise(int idOfEditedInEDB, Boolean isTimeAllowed){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordTime", CommonFunctions.getIntegerValueOfBoolean(isTimeAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }
    public void updateDistanceAllowedOfEditedExercise(int idOfEditedInEDB, Boolean isDistanceAllowed){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordDistance", CommonFunctions.getIntegerValueOfBoolean(isDistanceAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }
    public void updateRepetitionsAllowedOfEditedExercise(int idOfEditedInEDB, Boolean areRepetitionsAllowed){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordRepetitions", CommonFunctions.getIntegerValueOfBoolean(areRepetitionsAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }
    public void updateImageOfEditedExercise(int idOfEditedInEDB, byte[] editedImage){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("ImageOfExercise", editedImage);
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
    }

    // Function called at the end of an activity to close the database once
    public void closePlansDB(){
        DB.close();
    }

}
