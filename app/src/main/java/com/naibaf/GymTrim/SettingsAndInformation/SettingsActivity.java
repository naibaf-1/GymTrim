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

package com.naibaf.GymTrim.SettingsAndInformation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.naibaf.GymTrim.OtherClasses.AudioServiceForBackgroundProcess;
import com.naibaf.GymTrim.OtherClasses.CommonFunctions;
import com.naibaf.GymTrim.PlansExercisesDataActivity;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    ExercisesDB EDB;
    PlansDB PDB;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    Spinner ChooseReminderSound;
    Spinner LanguageSelector;
    Boolean exportImportPlans = false;
    Boolean exportImportExercises = false;
    private ActivityResultLauncher<Intent> exportDirectoryPickerLauncher;
    private ActivityResultLauncher<Intent> importDirectoryPickerLauncher;
    boolean isFirstSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        //Add EdgeToScreen
        //Apply to toolbar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    systemBars.top,// Toolbar adjusts for the status bar
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            ); return insets; });
        //Apply to the rest of the layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding( view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    systemBars.bottom // Ensure elements are pushed up above navigation bar
                    ); return insets;
        });
        int dynamicPrimaryColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryInverse, Color.BLACK);

        // Ensure the status bar matches the toolbar
        getWindow().setStatusBarColor(dynamicPrimaryColor);

        // Saving state of our app using SharedPreferences
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //Delete All Data => Delete Database content
        EDB = new ExercisesDB(this);
        PDB = new PlansDB(this);

        //Close Settings
        ImageButton Close = findViewById(R.id.imageButton_close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();;
            }
        });

        //Surface & appearance section
        //Toggle between light and dark mode
        MaterialSwitch ToggleLightDarkMode = findViewById(R.id.switch_ToggleLightDarkMode);
        // Display correct value when launched
        String mode = sharedPreferences.getString("getMode", "system");
        if (mode.equals("system")){
            ToggleLightDarkMode.setChecked(false);
            //Todo: Try to set the toggle depending on system light or dark
        } else if (mode.equals("dark")) {
            ToggleLightDarkMode.setChecked(true);
        } else {
            ToggleLightDarkMode.setChecked(false);
        }
        ToggleLightDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putString("getMode", "dark");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putString("getMode", "light");
                }
                editor.apply();
            }
        });

        // Select preferred Language
        LanguageSelector = findViewById(R.id.spinner_SelectLanguage);
        String[] possibleLanguages = getResources().getStringArray(R.array.selectableLanguagesFullName);
        // Create the adapter
        ArrayAdapter<String> AdapterForChoosingLanguage = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, possibleLanguages);
        AdapterForChoosingLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the Spinner
        LanguageSelector.setAdapter(AdapterForChoosingLanguage);
        LanguageSelector.setSelection(sharedPreferences.getInt("PositionOfLanguageSelector", 0));

        LanguageSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] valuesForLanguages = getResources().getStringArray(R.array.selectableLanguagesShortNames);
                String newLanguage = valuesForLanguages[position];
                String currentLanguage = sharedPreferences.getString("SelectedLanguage", "sys");

                if (!newLanguage.equals(currentLanguage) && !newLanguage.equals("sys")) { //Just in case it was actually changed
                    Locale locale = new Locale(newLanguage);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.setLocale(locale);
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                    editor.putString("SelectedLanguage", newLanguage);
                    editor.apply();
                    Intent restart = getIntent();
                    finish();
                    startActivity(restart);
                    PlansExercisesDataActivity activity = PlansExercisesDataActivity.getInstanceOfPEDActivity();
                    if (activity != null) {
                        activity.refreshActivity();
                    }
                } else if (!newLanguage.equals(currentLanguage)){
                    Locale locale = new Locale(Locale.getDefault().getLanguage());
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.setLocale(locale);
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                    editor.remove("SelectedLanguage");
                    editor.apply();
                    Intent restart = getIntent();
                    finish();
                    startActivity(restart);
                    PlansExercisesDataActivity activity = PlansExercisesDataActivity.getInstanceOfPEDActivity();
                    if (activity != null) {
                        activity.refreshActivity();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Experience section
        //Option to disable the reminder
        MaterialSwitch ToggleReminderOnOff = findViewById(R.id.switch_ToggleReminderOnOff);
        boolean reminderIsEnabled = sharedPreferences.getBoolean("IsReminderEnabled", false);
        if (reminderIsEnabled){
            ToggleReminderOnOff.setChecked(true);
        }

        // Switch to toggle the vibrator on or off
        MaterialSwitch ToggleVibratorOnOff = findViewById(R.id.switch_ToggleVibratorOnOff);
        ToggleVibratorOnOff.setEnabled(reminderIsEnabled);

        // Activate the vibrator
        ToggleVibratorOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Save changes
                editor.putBoolean("isVibratorEnabled", isChecked);
                editor.apply();
                // If it's toggled on vibrate 3 times
                if (isChecked) {
                    CommonFunctions.reminderVibrate((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
                }
            }
        });

        //Spinner for selecting the sound of the reminder
        ChooseReminderSound = findViewById(R.id.spinner_ChooseReminderSound);
        ChooseReminderSound.setEnabled(reminderIsEnabled);

        //Get arrays of options from arrays.xml
        String[] choosableSoundsForReminder = getResources().getStringArray(R.array.selectableSoundsForReminder);
        //Create the Adapter for the spinner
        ArrayAdapter<String> AdapterForChoosingSound = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, choosableSoundsForReminder);
        AdapterForChoosingSound.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isFirstSelection = true;
        //Apply the Adapter to the Spinner
        ChooseReminderSound.setAdapter(AdapterForChoosingSound);
        ChooseReminderSound.setSelection(sharedPreferences.getInt("PositionOfReminderSelector", 0));
        ChooseReminderSound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Prevent automatic ringing when activity is launched
                if (isFirstSelection){
                    isFirstSelection = false;
                    return;
                } else {
                    //Get values
                    String[] valuesForReminder = getResources().getStringArray(R.array.selectableSoundsForReminderValues);
                    int idOfChosenFile;
                    // If silent is selected don't try to make a sound
                    if (valuesForReminder[position].equals("silent")){
                        // -1 stands for silent
                        idOfChosenFile = -1;
                    } else {
                        idOfChosenFile = getResources().getIdentifier(valuesForReminder[position], "raw", getPackageName());
                    }

                    //Save selected value
                    editor.putInt("SoundForReminder", idOfChosenFile);
                    editor.apply();

                    // Play a sound
                    if (!isFirstSelection) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            SettingsActivity.this.startForegroundService(new Intent(SettingsActivity.this, AudioServiceForBackgroundProcess.class));
                        } else {
                            SettingsActivity.this.startService(new Intent(SettingsActivity.this, AudioServiceForBackgroundProcess.class));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ToggleReminderOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Apply the configuration
                editor.putBoolean("isReminderEnabled", isChecked);
                editor.apply();
                // Enable or disable Spinner and Switch
                ChooseReminderSound.setEnabled(isChecked);
                ToggleVibratorOnOff.setEnabled(isChecked);
            }
        });

        //Data section
        //Export data
        //Register directory picker for export
        exportDirectoryPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri treeUri = result.getData().getData();
                if (treeUri != null){
                    getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (exportImportPlans) {
                        exportDB(treeUri, "GymTrim-Plans.db");
                    }
                    if (exportImportExercises){
                        exportDB(treeUri, "GymTrim-Exercises.db");
                    }
                }
            }
        });
        //Export data
        TextView ExportTrainingData = findViewById(R.id.textView_ExportData);
        ExportTrainingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open custom AlertDialog => Ask what to export
                // Create an alert builder
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
                builder.setTitle(R.string.alertdialog_export_header);

                // Set the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View customLayout = inflater.inflate(R.layout.custom_alert_dialog_export_data, null);
                builder.setView(customLayout);

                // Add a button
                builder.setPositiveButton("OK", (dialog, which) -> {
                    // Send data from the AlertDialog to the Activity
                    CheckBox ExportPlans = customLayout.findViewById(R.id.checkBox_ExportPlans);
                    CheckBox ExportExercises = customLayout.findViewById(R.id.checkBox_ExportExercises);
                    exportImportPlans = ExportPlans.isChecked();
                    exportImportExercises = ExportExercises.isChecked();

                    //Get directory => Export data
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    exportDirectoryPickerLauncher.launch(intent);
                });

                builder.setCancelable(true);

                builder.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                    dialog.cancel();
                });

                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Import data
        //Register directory picker for export
        importDirectoryPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri fileUri = result.getData().getData();
                if (fileUri != null) {
                    if (exportImportPlans) {
                        importDB(fileUri, "GymTrim-Plans.db");
                    }
                    if (exportImportExercises){
                        importDB(fileUri, "GymTrim-Exercises.db");
                    }
                }
            }
        });
        //Import data
        TextView ImportTrainingData = findViewById(R.id.textView_ImportData);
        ImportTrainingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask what to import => import this correctly
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(SettingsActivity.this);
                alertDialogBuilder.setTitle(R.string.alertdialog_import_header);

                // Set the custom layout
                LayoutInflater inflater = getLayoutInflater();
                View customLayout = inflater.inflate(R.layout.custom_alert_dialog_import_data, null);
                alertDialogBuilder.setView(customLayout);

                alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                    RadioButton ImportPlans = customLayout.findViewById(R.id.radioButton_ImportPlans);
                    RadioButton ImportExercises = customLayout.findViewById(R.id.radioButton_ImportExercises);
                    exportImportPlans = ImportPlans.isChecked();
                    exportImportExercises = ImportExercises.isChecked();

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/.db*"); //Allow only .db files
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/octet-stream", "application/x-sqlite3"});
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    importDirectoryPickerLauncher.launch(intent);
                });

                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                    dialog.cancel();
                });

                // Create and show the alert dialog
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
    });

        //Delete all training data
        TextView DeleteTrainingData = findViewById(R.id.textView_DeleteTrainingData);
        DeleteTrainingData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Create Dialog
                //Configure his activity
                MaterialAlertDialogBuilder ConfirmDeleteAllDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                ConfirmDeleteAllDialog.setMessage(R.string.delete_all_data_callback);

                ConfirmDeleteAllDialog.setCancelable(true);

                ConfirmDeleteAllDialog.setPositiveButton(R.string.button_apply, (dialog, which) -> {
                    EDB.completelyRemoveEntireData();
                    PDB.completelyRemoveEntireData();

                    Intent restart = getIntent();
                    finish();
                    startActivity(restart);
                    PlansExercisesDataActivity activity = PlansExercisesDataActivity.getInstanceOfPEDActivity();
                    if (activity != null) {
                        activity.refreshActivity();
                    }
                });

                ConfirmDeleteAllDialog.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                    dialog.cancel();
                });

                //Show Dialog
                AlertDialog confirmDeleteAllDialog = ConfirmDeleteAllDialog.create();
                confirmDeleteAllDialog.show();
            }
        });

        //General
        ImageButton ResetAll = findViewById(R.id.imageButton_ResetAllSettings);
        ResetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Dialog
                //Configure his activity
                MaterialAlertDialogBuilder ConfirmResetAllDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                ConfirmResetAllDialog.setMessage(R.string.reset_all_settings_callback);

                ConfirmResetAllDialog.setCancelable(true);

                ConfirmResetAllDialog.setPositiveButton(R.string.button_apply, (dialog, which) -> {
                    //Reset Light/dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    //Reset Language
                    editor.remove("SelectedLanguage");
                    //Reset reminder
                    editor.remove("IsReminderEnabled");
                    editor.remove("SoundForReminder");
                    editor.remove("PositionOfLanguageSelector");
                    editor.remove("PositionOfReminderSelector");
                    editor.putString("getMode", "system");
                    editor.apply();
                    PlansExercisesDataActivity activity = PlansExercisesDataActivity.getInstanceOfPEDActivity();
                    if (activity != null) {
                        activity.refreshActivity();
                    }
                });

                ConfirmResetAllDialog.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                    dialog.cancel();
                });

                //Show Dialog
                AlertDialog confirmDeleteAllDialog = ConfirmResetAllDialog.create();
                confirmDeleteAllDialog.show();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EDB.closeExerciseDB();
        PDB.closePlansDB();

        EDB.close();
        PDB.close();

        //Save position of the spinners
        editor.putInt("PositionOfLanguageSelector", LanguageSelector.getSelectedItemPosition());
        editor.putInt("PositionOfReminderSelector", ChooseReminderSound.getSelectedItemPosition());
        editor.apply();
    }

    public void exportDB(Uri treeUri, String nameOfDB){
        File dbFile = getDatabasePath(nameOfDB); //Path to SQLite database
        try {
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            if (pickedDir == null || !pickedDir.canWrite()) {
                Toast.makeText(this, R.string.error_no_write_permission, Toast.LENGTH_LONG).show();
                return;
            }

            DocumentFile newFile = pickedDir.createFile("application/octet-stream", dbFile.getName());
            if (newFile == null) {
                return;
            }

            OutputStream outStream = getContentResolver().openOutputStream(newFile.getUri());
            InputStream inStream = new FileInputStream(dbFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inStream.close();
            outStream.close();

            Toast.makeText(this, R.string.settings_exporting_successful, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_exporting_failed, Toast.LENGTH_LONG).show();
        }
    }

    public void importDB(Uri fileUri, String targetDBName){
        //Get finish path
        File targetDbFile = getDatabasePath(targetDBName);

        try {
            InputStream inStream = getContentResolver().openInputStream(fileUri);
            OutputStream outStream = new FileOutputStream(targetDbFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inStream.close();
            outStream.close();

            Toast.makeText(this, R.string.settings_importing_successful, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_importing_failed, Toast.LENGTH_LONG).show();
        }
    }

    //FixMe: Fix Bug when often toggling mode after resetting

}