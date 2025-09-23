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
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.ExerciseCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.io.ByteArrayOutputStream;

public class CommonFunctions {
    //Functions to convert the exercise images (Vgl. https://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database)
    //byte[] => Bitmap
    public static Bitmap getArrayAsBitmap(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    //Bitmap => byte[]
    public static byte[] getBitmapAsArray(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    //Convert a Boolean into an Integer
    public static int getIntegerValueOfBoolean(Boolean booleanToChange){
        //1 means true, 0 means false
        int changedBooleanAsInt = 0;
        if (booleanToChange){
            changedBooleanAsInt = 1;
        } else {
            changedBooleanAsInt = 0;
        }
        return  changedBooleanAsInt;
    }

    //Convert an Integer into a Boolean
    public static Boolean getBooleanValueOfInteger(int integerToChange){
        Boolean changedIntegerAsBoolean = false;
        if(integerToChange == 1){
            changedIntegerAsBoolean = true;
        } else {
            changedIntegerAsBoolean = false;
        }
        return changedIntegerAsBoolean;
    }

    //Apply SwipeToDelete to all CustomExerciseRecyclerViews
    public static void applySwipeToDeleteForExercises(RecyclerView.ViewHolder viewHolder, ExerciseCustomRecyclerViewAdapter adapter, Context context, PlansDB DB, int plansId){
        ExerciseCustomRecyclerViewAdapter.CustomExerciseList exercise;
        final int position = viewHolder.getAdapterPosition();
        try {
            exercise = adapter.getData().get(position);

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
                    adapter.notifyDataSetChanged();
                }
            });

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            ConfirmDialog.setPositiveButton(R.string.button_apply, (dialog, which) -> {
                adapter.removeItem(position);
                Cursor possibleIds = DB.getExerciseId(plansId);
                possibleIds.moveToPosition(position);
                int ColumnIndexOfId = possibleIds.getColumnIndex("exerciseMainId");
                int id = possibleIds.getInt(ColumnIndexOfId);

                //Delete from DB
                DB.deleteExerciseData(id);

                adapter.notifyDataSetChanged();
            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            ConfirmDialog.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                // If user click cancel then dialog box is canceled.
                adapter.notifyDataSetChanged();
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog confirmDialog = ConfirmDialog.create();
            // Show the Alert Dialog box
            confirmDialog.show();

        }catch (IndexOutOfBoundsException e){
            Toast.makeText(context, "IndexOutOfBoundsException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Search for exercises matching to the text, which is entered into a search box
    public static void searchExerciseInExerciseRecyclerView(String text, ExerciseCustomRecyclerViewAdapter Adapter){
        if (TextUtils.isEmpty(text)){
            Adapter.getFilter().filter("");
        } else {
            Adapter.getFilter().filter(text);
        }
    }
}
