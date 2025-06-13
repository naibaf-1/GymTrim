package com.naibaf.GymTrim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.naibaf.GymTrim.Data.DataFragment;
import com.naibaf.GymTrim.Exercise.ExercisesFragment;
import com.naibaf.GymTrim.OtherClasses.PopUpMenuInflater;
import com.naibaf.GymTrim.PlansAndTraining.PlansFragment;
import com.naibaf.GymTrim.SQLiteDatabases.ExercisesDB;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.util.Locale;

public class PlansExercisesDataActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    static PlansExercisesDataActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //Set correct day/night mode
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final String mode = sharedPreferences.getString("getMode", "system");

        //Check whether the app is launched for the first time
        boolean isFirstStart = sharedPreferences.getBoolean("firstStart", true);
        sharedPreferences.edit().remove("isDarkModeOn");

        if (isFirstStart) {
            //Force system mode by first start
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            //Mark as already started before
            sharedPreferences.edit().putBoolean("firstStart", false).apply();
        } else {
            // When user reopens the app after applying dark/light mode
            if (mode.equals("system")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (mode.equals("dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }


        // Load the language based on user preferences
        String language = sharedPreferences.getString("SelectedLanguage", Locale.getDefault().getLanguage());
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        DynamicColors.applyToActivityIfAvailable(PlansExercisesDataActivity.this);
        setContentView(R.layout.activity_plans_exercises_data);

        //Apply EdgeToScreen
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView_PlansExercisesData), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    systemBars.bottom // BottomNavigationView adjusts for navigation bar
            );
            return insets;
        });
        int dynamicPrimaryColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryInverse, Color.BLACK);
        int dynamicSurfaceColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorControlHighlight, Color.BLACK);

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_PlansExercisesData);
        bottomNavigationView.setBackgroundColor(dynamicSurfaceColor);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);


        //Start Here
        instance = this;

        //Go to Settings
        ImageButton inflateMenu = findViewById(R.id.imageButton_openMenu);
        inflateMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpMenuInflater.inflateSettingsAndInformationsMenu(PlansExercisesDataActivity.this, inflateMenu);
            }
        });

        //Manage Fragments
        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_PlansExercisesData);
        Fragment PlansFragment = new PlansFragment();
        Fragment ExercisesFragment = new ExercisesFragment();
        Fragment DataFragment = new DataFragment();

        setCurrentFragment(PlansFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.NavigationBarItem_Plans) {
                setCurrentFragment(PlansFragment);

                //Set correct Name in Toolbar
                TextView Name = findViewById(R.id.textViewNameOfCurrentFragment);
                Name.setText(R.string.fragment_plans_header);

            } else if (itemId == R.id.NavigationBarItem_Exercises) {
                setCurrentFragment(ExercisesFragment);

                //Set correct Name in Toolbar
                TextView Name = findViewById(R.id.textViewNameOfCurrentFragment);
                Name.setText(R.string.fragment_exercises_header);

            } else if (itemId == R.id.NavigationBarItem_Data) {
                setCurrentFragment(DataFragment);

                //Set correct Name in Toolbar
                TextView Name = findViewById(R.id.textViewNameOfCurrentFragment);
                Name.setText(R.string.fragment_data_header);

            } else {
                Log.d("Debug", "Unknown item ID");
            }
            return true;
        });

    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrameLayout_PlansExercisesData, fragment)
                .commit();
    }

    public void refreshActivity(){
        Intent refresh = new Intent(PlansExercisesDataActivity.this, PlansExercisesDataActivity.class);
        startActivity(refresh);
    }

    public static PlansExercisesDataActivity getInstanceOfPEDActivity(){
        return instance;
    }

}