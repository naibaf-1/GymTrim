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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;


public class AddExerciseActivity extends AppCompatActivity {

    //Get the database => Write data to it
    ExercisesDB DB;
    byte[] imageOfExercise;
    CheckBox RecordWeight;
    CheckBox RecordDistance;
    CheckBox RecordRepetitions;
    CheckBox RecordTime;
    EditText Name;
    EditText Notes;
    String name;
    String notes;
    int id;
    Boolean nameIsEmpty = true;
    Boolean userAddedImage = false;

    @Override
    protected void onStart() {
        super.onStart();

        //Start here
        DB = new ExercisesDB(this);
        id  =  DB.createNewExercise();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exercise);

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
        //Get ImageView
        ImageView ImageOfExercise = findViewById(R.id.imageView_AddImage);

        //Get Checkboxes
        RecordWeight = findViewById(R.id.checkBox_NoteWeight);
        RecordWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Auto-save changes
                DB.updateCheckWeight(id, isChecked);
            }
        });

        RecordDistance = findViewById(R.id.checkBox_NoteDistance);
        RecordDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DB.updateCheckDistance(id, isChecked);
            }
        });

        RecordTime = findViewById(R.id.checkBox_NoteTime);
        RecordTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DB.updateCheckTime(id, isChecked);
            }
        });

        RecordRepetitions = findViewById(R.id.checkBox_NoteRepetitions);
        RecordRepetitions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DB.updateCheckRepetitions(id, isChecked);
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
                DB.updateName(id, name);
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
                DB.updateNotes(id, notes);
            }
        });

        //Close & return
        ImageButton Close = findViewById(R.id.imageButton_close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Get a image => https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
        ActivityResultLauncher<Intent> launchImagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri), 195, 195, false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ImageOfExercise.setImageBitmap(selectedImageBitmap);

                        //Get image as ByteArray => Store it in Database (vgl. https://stackoverflow.com/questions/9357668/how-to-store-image-in-sqlite-database)
                        if (selectedImageBitmap != null) {
                            imageOfExercise = CommonFunctions.getBitmapAsArray(selectedImageBitmap);
                            DB.updateImage(id, imageOfExercise);
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
                    userAddedImage = true;
                }
            });

        FloatingActionButton Add = findViewById(R.id.floatingActionButton_Add);
        Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
    }

    //Notify Fragment to update its recyclerView => vgl.: https://stackoverflow.com/questions/30502515/refresh-recyclerview-from-another-activity
    @Override
    public void finish() {
        // Added default image if user didn't select one
        if(!userAddedImage){
            imageOfExercise = CommonFunctions.getBitmapAsArray(BitmapFactory.decodeResource(getResources(), R.drawable.new_icon_no_background_gray));
            DB.updateImage(id, imageOfExercise);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("isItemAdded", true);
        returnIntent.putExtra("name", name);
        returnIntent.putExtra("notes", notes);
        returnIntent.putExtra("image", imageOfExercise);
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("position", 0);
        setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Sent Data to ExercisesActivity => https://www.geeksforgeeks.org/how-to-send-data-from-one-activity-to-second-activity-in-android/
        //If string isn't empty
        if (!nameIsEmpty) {
            finish();
        } else {
            Toast.makeText(AddExerciseActivity.this, R.string.error_missing_name, Toast.LENGTH_SHORT).show();
        }
        DB.closeExerciseDB();
        DB.close();
    }
}