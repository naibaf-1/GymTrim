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

package com.naibaf.GymTrim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.naibaf.GymTrim.Data.DataFragment;
import com.naibaf.GymTrim.Exercise.ExercisesFragment;
import com.naibaf.GymTrim.OtherClasses.PopUpMenuInflater;
import com.naibaf.GymTrim.PlansAndTraining.PlansFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        // Check for updates
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
                Name.setText(R.string.data_section_training_data);

            } else {
                Log.d("Debug", "Unknown item ID");
            }
            return true;
        });

        checkForUpdatesAtGithubReleasePage();

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

    public void checkForUpdatesAtGithubReleasePage() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/repos/naibaf-1/GymTrim/releases/latest")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UpdateCheck", "HTTP-Error" + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    String latestVersion = obj.getString("tag_name");

                    // Get current version
                    String currentVersion = getPackageManager()
                            .getPackageInfo(getPackageName(), 0).versionName;

                    if (isNewerVersion(latestVersion, currentVersion)) {
                        runOnUiThread(() ->
                                Toast.makeText(
                                        PlansExercisesDataActivity.this,
                                        R.string.update_available_notification,
                                        Toast.LENGTH_LONG
                                ).show()
                        );
                    }
                } catch (Exception e) {
                    Log.e("UpdateCheck", "Parsing-Error", e);
                }
            }
        });
    }

    // Function to compare the version on github and the installed one
    private boolean isNewerVersion(String latest, String current) {
        // Clean the version name: "v.1.0.0-halcyon" -> "1.0.0"
        String latestClean = latest.replaceFirst("^v\\.?","").split("-")[0];
        String currentClean = current.replaceFirst("^v\\.?","").split("-")[0];

        // Get the single numbers
        String[] latestParts = latestClean.split("\\.");
        String[] currentParts = currentClean.split("\\.");

        // Get length of the longer version name => Loop that long
        int length = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < length; i++) {
            // If possible get the next digit, if there is no further digit enter 0 instead.
            int latestNum = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int currentNum = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

            // Quit as early as possible, but if they are the same digits move forward
            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false;
    }

}

// Todo: Upload to F-Droid
// Todo: If user adds square image display it as is