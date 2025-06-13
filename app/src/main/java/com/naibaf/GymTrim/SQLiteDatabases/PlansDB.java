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
    private static final int DATABASE_VERSION = 1;

    public PlansDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        try {
            DB.execSQL("PRAGMA foreign_keys = ON;");
            DB.execSQL("CREATE TABLE PlansDetails (" + "plansId INTEGER PRIMARY KEY AUTOINCREMENT, " + "NameOfPlan TEXT, " + "ColorOfPlan INTEGER, " + "NotesForPlan TEXT, " + "VibratorTime FLOAT, " + "DateOfLastTraining TEXT, " + "TimerDurationWhenTrainingLeft TEXT" + ");");
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

        DB.execSQL(" create Table PlansDetails (plansId INTEGER primary key autoincrement, NameOfPlan TEXT,  ColorOfPlan INTEGER, NotesForPlan TEXT, VibratorTime FLOAT, DateOfLastTraining TEXT, TimerDurationWhenTrainingLeft TEXT) ");
        DB.execSQL(" create Table Exercise (exerciseMainId INTEGER primary key autoincrement, DateOfLastTraining TEXT, NameOfExercise TEXT, ImageOfExercise BLOB, NotesForExercise TEXT, RecordWeight INTEGER, RecordTime INTEGER, RecordDistance INTEGER, RecordRepetitions INTEGER, ExerciseID INTEGER, ExerciseIdInEDB INTEGER, ExerciseOrder INTEGER, FOREIGN KEY (ExerciseID) REFERENCES PlansDetails(plansId) ON DELETE CASCADE) ");
        DB.execSQL(" create Table ExerciseTable (tableMainId INTEGER primary key autoincrement, RecordWeight REAL, RecordDistance REAL, RecordSentences INTEGER, RecordTime NUMERIC, RowNumber INTEGER, RowIsDone INTEGER, TableId INTEGER, FOREIGN KEY (TableId) REFERENCES Exercise(exerciseMainId) ON DELETE CASCADE ) ");
    }

    //Delete data
    public void deletePlanData(int plansId) {
        SQLiteDatabase DB = this.getWritableDatabase();

        //Select the row => delete it
//        Cursor rowId = DB.rawQuery("Select exerciseMainId From Exercise Where ExerciseID = ?", new String[]{String.valueOf(plansId)});
//        int ColumnIndexOfRowId = rowId.getColumnIndex("exerciseMainId");
//        int idForRow = rowId.getInt(ColumnIndexOfRowId);
//        DB.delete("ExerciseTable", "TableId = ?", new String[]{String.valueOf(idForRow)});
//        //Delete the exercise
//        DB.delete("Exercise", "ExerciseID = ?", new String[]{String.valueOf(plansId)});

        //Lastly delete the plan itself
        Cursor cursor = DB.rawQuery(" Select * From PlansDetails Where plansId = ? ", new String[]{String.valueOf(plansId)});
        DB.delete("PlansDetails", "plansId = ?", new String[]{String.valueOf(plansId)});

        //rowId.close();
        cursor.close();
        DB.close();
    }

    //Get all plans and their data from PlansDetails
    public Cursor getAllPlans(){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor planData = DB.rawQuery(" Select * From PlansDetails Order By DateOfLastTraining DESC", null);
        return planData;
    }

    //Delete exercise
    public Boolean deleteExerciseData(int exerciseId) {
        SQLiteDatabase DB = this.getWritableDatabase();
        //Select the row
        Cursor cursor = DB.rawQuery(" Select * From Exercise Where exerciseMainId = ? ", new String[]{String.valueOf(exerciseId)});
        long result = DB.delete("Exercise", "exerciseMainId = ?", new String[]{String.valueOf(exerciseId)});
        if (cursor.getCount() > 0) {
            cursor.close();
            DB.close();
            //If Failed: false
            return result != -1;
        } else {
            cursor.close();
            DB.close();
            return false;
        }
    }

    //Get data of a specific exercise
    public Cursor getBasicExerciseData(int exerciseId){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor exerciseData = DB.rawQuery(" Select * From PlansDetails p INNER JOIN Exercise e ON p.plansId = e.ExerciseID Where e.exerciseMainId = ? ", new String[]{String.valueOf(exerciseId)});
        return exerciseData;
    }
    public int addSingleExercise(int plansId, String notes, String name, Bitmap image, int repetitions, int weight, int time, int distance, int order, String date, int idInEDB){
        SQLiteDatabase DB = getWritableDatabase();

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
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor data = DB.rawQuery(" Select exerciseMainId From Exercise  WHERE ExerciseID = ?  ORDER BY ExerciseOrder", new String[]{String.valueOf(plansId)});
        return data;
    }

    //Get id of certain rows
    public Cursor getRowId(int exerciseId){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor data = DB.rawQuery(" Select tableMainId From ExerciseTable  WHERE TableId = ?  ORDER BY RowNumber", new String[]{String.valueOf(exerciseId)});
        return data;
    }

    //Insert everything at once
    public int insertNewPlan(){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put("NameOfPlan", "*empty*");
        long plansId = DB.insert("PlansDetails", null, contentValue);
        DB.close();
        return (int) plansId;
    }

    //Get all data of an specific plan at once
    public Cursor getAllPlanExercises(int plansId){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor data = DB.rawQuery(" Select * From PlansDetails p INNER JOIN Exercise e ON p.plansId = e.ExerciseID WHERE p.plansId = ? Order By e.ExerciseOrder", new String[]{String.valueOf(plansId)});
        return data;
    }
    public Cursor getBasicPlanData(int plansId){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor data = DB.rawQuery(" Select * From PlansDetails WHERE plansId = ?", new String[]{String.valueOf(plansId)});
        return data;
    }
    public Boolean updateTrainingData(int planId, String planName, String planNotes, String date, String timerDuration){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("NameOfPlan", planName);
        contentvalues.put("NotesForPlan", planNotes);
        contentvalues.put("DateOfLastTraining", date);
        contentvalues.put("TimerDurationWhenTrainingLeft", timerDuration);
        DB.update("PlansDetails", contentvalues, "plansId = ?", new String[]{String.valueOf(planId)});

        DB.close();
        return true;
    }
    public int addExerciseTableRow(int exerciseId, float weight, int repetitions, float time, float distance, int order) {
        SQLiteDatabase DB = this.getWritableDatabase();
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
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor rows = DB.rawQuery(" SELECT * FROM Exercise e INNER JOIN ExerciseTable t ON e.exerciseMainId = t.TableId WHERE t.TableId = ? ORDER BY t.RowNumber", new String[]{String.valueOf(exerciseId)});
        return rows;
    }
    public void updateExerciseTableRows(int exerciseId, float weight, int repetitions, float time, float distance, int order){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("RecordWeight", weight);
        contentValues.put("RecordDistance", distance);
        contentValues.put("RecordTime", time);
        contentValues.put("RecordSentences", repetitions);
        contentValues.put("TableId", exerciseId);
        DB.execSQL("UPDATE ExerciseTable SET RecordWeight = " + weight + " , RecordDistance = " + distance + " , RecordTime = " + time+ ", RecordSentences = " + repetitions + " , TableId = " + exerciseId + " WHERE TableId = " + exerciseId + " AND RowNumber = " + order);
        DB.close();
    }
    public void updateRowDone(int tableRowId, Boolean rowIsDone){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("RowIsDone", CommonFunctions.getIntegerValueOfBoolean(rowIsDone));
        DB.update("ExerciseTable", contentValues, "tableMainId = ?", new String[]{String.valueOf(tableRowId)});
        DB.close();
    }

    //Note when a specific exercise was trained before
    public void setDateOfCurrentTrainingForExercise(int exerciseId, String date){
        SQLiteDatabase DB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues(1);
        contentValues.put("DateOfLastTraining", date);
        DB.update("Exercise", contentValues, "exerciseMainId = ?", new String[]{String.valueOf(exerciseId)});
        DB.close();
    }

    //Get when a specific exercise was trained before
    public String getDateOfExercise(int exerciseID){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor date = DB.rawQuery("SELECT DateOfLastTraining FROM Exercise WHERE exerciseMainId = ?", new String[]{String.valueOf(exerciseID)});
        date.moveToFirst();
        int columnIndex = date.getColumnIndex("DateOfLastTraining");
        DB.close();
        return date.getString(columnIndex);
    }

    //Delete row
    public void deleteExerciseTableRow(int rowId){
        SQLiteDatabase DB = this.getWritableDatabase();
        //Select the row
        DB.rawQuery(" Select * From ExerciseTable Where tableMainId = ? ", new String[]{String.valueOf(rowId)});
        DB.delete("ExerciseTable", "tableMainId = ?", new String[]{String.valueOf(rowId)});
        DB.close();
    }

    //Functions for fast auto-save
    public void editPlanName(String newName, int plansId){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("NameOfPlan", newName);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
        DB.close();
    }
    public void editPlanNotes(String newNotes, int plansId){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("NotesForPlan", newNotes);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
        DB.close();
    }
    public void editPlanColor(int planColor, int plansId){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("ColorOfPlan", planColor);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
        DB.close();
    }
    public void editPlanReminder(float duration, int plansId){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("VibratorTime", duration);
        DB.update("PlansDetails", contentValues, "plansId = ?", new String[]{String.valueOf(plansId)});
        DB.close();
    }
    public void completelyRemoveEntireData() {
        SQLiteDatabase DB = this.getWritableDatabase();

        DB.delete("PlansDetails", null, null);
        DB.delete("Exercise", null, null);
        DB.delete("ExerciseTable", null, null);
        DB.close();
    }
    public int getIdOfExerciseInEDB(int exerciseId){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor ids = DB.rawQuery("SELECT ExerciseIdInEDB FROM Exercise WHERE exerciseMainId = ?", new String[]{String.valueOf(exerciseId)});
        ids.moveToFirst();
        int columnIndexOfId = ids.getColumnIndex("ExerciseIdInEDB");
        return ids.getInt(columnIndexOfId);
    }

    //Update exercises too when in editor changed
    public Boolean checkIfEditedExerciseExistsInPDB(int idOfEditedInEDB){
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor check = DB.rawQuery("Select ExerciseIdInEDB FROM Exercise WHERE ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        if (check.getCount() != 0){
            return true; //It exists
        } else {
            return false; //It doesn't exist
        }
    }
    public void updateNameOfEditedExercise(int idOfEditedInEDB, String newName){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("NameOfExercise", newName);
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }
    public void updateNotesOfEditedExercise(int idOfEditedInEDB, String newNotes){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("NotesForExercise", newNotes);
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }
    public void updateWeightAllowedOfEditedExercise(int idOfEditedInEDB, Boolean isWeightAllowed){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordWeight", CommonFunctions.getIntegerValueOfBoolean(isWeightAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }
    public void updateTimeAllowedOfEditedExercise(int idOfEditedInEDB, Boolean isTimeAllowed){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordTime", CommonFunctions.getIntegerValueOfBoolean(isTimeAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }
    public void updateDistanceAllowedOfEditedExercise(int idOfEditedInEDB, Boolean isDistanceAllowed){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordDistance", CommonFunctions.getIntegerValueOfBoolean(isDistanceAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }
    public void updateRepetitionsAllowedOfEditedExercise(int idOfEditedInEDB, Boolean areRepetitionsAllowed){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("RecordRepetitions", CommonFunctions.getIntegerValueOfBoolean(areRepetitionsAllowed));
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }
    public void updateImageOfEditedExercise(int idOfEditedInEDB, byte[] editedImage){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("ImageOfExercise", editedImage);
        DB.update("Exercise", contentValues, "ExerciseIdInEDB = ?", new String[]{String.valueOf(idOfEditedInEDB)});
        DB.close();
    }

}
