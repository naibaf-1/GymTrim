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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.io.IOException;

public class EditExerciseActivity extends AppCompatActivity {

    //Get the database => Write data to it
    ExercisesDB EDB;
    PlansDB PDB;

    Boolean nameIsEmpty = true;
    Boolean record_distance = false;
    Boolean record_weight = false;
    Boolean record_sentences = false;
    Boolean record_time = false;
    int id;
    byte[] imageOfExercise;
    Cursor exercise_data;
    CheckBox RecordWeight;
    CheckBox RecordDistance;
    CheckBox RecordSentences;
    CheckBox RecordTime;
    EditText Name;
    EditText Notes;
    String name;
    String notes;
    Boolean exerciseIsPartOfAPlan;
    int positionInRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_exercise);

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
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(dynamicPrimaryColor);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        //Start Here
        EDB = new ExercisesDB(this);
        PDB = new PlansDB(this);
        Intent getDataSentViaIntent = getIntent();
        id = getDataSentViaIntent.getIntExtra("id", -1);
        positionInRecyclerView = getDataSentViaIntent.getIntExtra("position", -1);
        exerciseIsPartOfAPlan = PDB.checkIfEditedExerciseExistsInPDB(id);

        //Get Checkboxes
        RecordWeight = findViewById(R.id.checkBox_NoteWeight);
        RecordWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Auto-save changes
                EDB.updateCheckWeight(id, isChecked);
                if (exerciseIsPartOfAPlan){
                    PDB.updateWeightAllowedOfEditedExercise(id, isChecked);
                }
            }
        });

        RecordDistance = findViewById(R.id.checkBox_NoteDistance);
        RecordDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EDB.updateCheckDistance(id, isChecked);
                if (exerciseIsPartOfAPlan) {
                    PDB.updateDistanceAllowedOfEditedExercise(id, isChecked);
                }
            }
        });

        RecordSentences = findViewById(R.id.checkBox_NoteRepetitions);
        RecordSentences.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EDB.updateCheckRepetitions(id, isChecked);
                if (exerciseIsPartOfAPlan) {
                    PDB.updateRepetitionsAllowedOfEditedExercise(id, isChecked);
                }
            }
        });

        RecordTime = findViewById(R.id.checkBox_NoteTime);
        RecordTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EDB.updateCheckTime(id, isChecked);
                if (exerciseIsPartOfAPlan) {
                    PDB.updateTimeAllowedOfEditedExercise(id, isChecked);
                }
            }
        });

        //Get the Name
        Name = findViewById(R.id.editText_Name);
        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = Name.getText().toString();
                EDB.updateName(id, name);
                if (exerciseIsPartOfAPlan) {
                    PDB.updateNameOfEditedExercise(id, name);
                }
                if (!name.isEmpty()){
                    nameIsEmpty = false;
                }
            }
        });
        //Get Notes
        Notes = findViewById(R.id.editText_Notes);
        Notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                notes = Notes.getText().toString();
                EDB.updateNotes(id, notes);
                if (exerciseIsPartOfAPlan) {
                    PDB.updateNotesOfEditedExercise(id, notes);
                }
            }
        });

        //Get ImageView
        ImageView ImageOfExercise = findViewById(R.id.imageView_EditImage);
        FloatingActionButton Save = findViewById(R.id.floatingActionButton_Save);

        //Close & return
        ImageButton Close = findViewById(R.id.imageButton_close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Get name of exercise clicked
        String selectedItem = getDataSentViaIntent.getStringExtra("Selected");

        //Get line where exercise is inserted in database
        if (selectedItem != null && id >= 0) {
            //Get data of selected exercise from database
            exercise_data = EDB.getExerciseData(id);

            //Display data
            int columnIndexOfName = exercise_data.getColumnIndex("NameOfExercise");
            int columnIndexOfRecordWeight = exercise_data.getColumnIndex("RecordWeight");
            int columnIndexOfRecordDistance = exercise_data.getColumnIndex("RecordDistance");
            int columnIndexOfRecordSentences = exercise_data.getColumnIndex("RecordRepetitions");
            int columnIndexOfRecordTime = exercise_data.getColumnIndex("RecordTime");
            int columnIndexOfImage = exercise_data.getColumnIndex("ImageOfExercise");
            int columnIndexOfNotes = exercise_data.getColumnIndex("NotesForExercise");

            if(columnIndexOfName >= 0 && columnIndexOfRecordWeight >= 0 && columnIndexOfRecordDistance >= 0 && columnIndexOfRecordSentences >= 0 && columnIndexOfRecordTime >= 0 && columnIndexOfImage >= 0 && columnIndexOfNotes >= 0) {
                exercise_data.moveToFirst();
                record_weight = CommonFunctions.getBooleanValueOfInteger(exercise_data.getInt(columnIndexOfRecordWeight));
                record_distance = CommonFunctions.getBooleanValueOfInteger(exercise_data.getInt(columnIndexOfRecordDistance));
                record_time = CommonFunctions.getBooleanValueOfInteger(exercise_data.getInt(columnIndexOfRecordTime));
                record_sentences = CommonFunctions.getBooleanValueOfInteger(exercise_data.getInt(columnIndexOfRecordSentences));
                if (record_weight) {
                    RecordWeight.setChecked(true);
                }
                if (record_distance) {
                    RecordDistance.setChecked(true);
                }
                if (record_time) {
                    RecordTime.setChecked(true);
                }
                if (record_sentences) {
                    RecordSentences.setChecked(true);
                }
                Name.setText(exercise_data.getString(columnIndexOfName));
                Notes.setText(exercise_data.getString(columnIndexOfNotes));
                Bitmap exerciseImage = CommonFunctions.getArrayAsBitmap(exercise_data.getBlob(columnIndexOfImage));
                ImageOfExercise.setImageBitmap(exerciseImage);
                imageOfExercise = exercise_data.getBlob(columnIndexOfImage);
            }

            //Get an other image => https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
            ActivityResultLauncher<Intent> launchImagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri), 500, 500, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ImageOfExercise.setImageBitmap(selectedImageBitmap);

                        //Get image as ByteArray => Store it in Database (vgl. https://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database)
                        if (selectedImageBitmap != null) {
                            imageOfExercise = CommonFunctions.getBitmapAsArray(selectedImageBitmap);
                            EDB.updateImage(id, imageOfExercise);
                            if (exerciseIsPartOfAPlan) {
                                PDB.updateImageOfEditedExercise(id, imageOfExercise);
                            }
                        }

                    }
                }
            });

            ImageOfExercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent imagePath = new Intent();
                    imagePath.setType("image/*");
                    imagePath.setAction(Intent.ACTION_GET_CONTENT);

                    launchImagePicker.launch(imagePath);
                }
            });

            Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //It will be saved by onDestroy(), so its not necessary to save it here too
                    finish();
                }
            });
        }else{
            Toast.makeText(this, R.string.error_exercise_not_found,  Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //Notify Fragment to update its recyclerView => vgl.: https://stackoverflow.com/questions/30502515/refresh-recyclerview-from-another-activity
    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isItemAdded", false);
        returnIntent.putExtra("name", name);
        returnIntent.putExtra("notes", notes);
        returnIntent.putExtra("image", imageOfExercise);
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("position", positionInRecyclerView);
        setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!nameIsEmpty) {
            finish();
        } else {
            Toast.makeText(EditExerciseActivity.this, R.string.error_missing_name, Toast.LENGTH_SHORT).show();
        }
        EDB.closeExerciseDB();
        PDB.closePlansDB();

        EDB.close();
        PDB.close();
        exercise_data.close();
    }
}