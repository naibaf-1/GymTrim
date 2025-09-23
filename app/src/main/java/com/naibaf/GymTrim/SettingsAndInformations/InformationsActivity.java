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

package com.naibaf.GymTrim.SettingsAndInformations;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.MaterialColors;
import com.naibaf.GymTrim.R;

public class InformationsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informations);

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
        ImageButton Close = findViewById(R.id.imageButton_close4);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Update & Display current version
        TextView Version = findViewById(R.id.textView_Version);
        try {
            String version = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            String versionTitle = getString(R.string.informations_app_version);
            Version.setText(versionTitle + version);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        //Links
        //Link to the repository/https://github.com/naibaf-1/GymTrim
        TextView RepositoryLink = findViewById(R.id.textView_LinkToSourceCode);
        RepositoryLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim");
            }
        });
        ImageView LinkToRepository = findViewById(R.id.imageView_OpenLinkSource);
        LinkToRepository.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim");
            }
        });

        //Link to  report a bug/https://github.com/naibaf-1/GymTrim/issues/new?template=bug_report.md
        TextView BugReportLink = findViewById(R.id.textView_ReportABug);
        BugReportLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/issues/new?template=bug-report-for-gymtrim.md");
            }
        });
        ImageView LinkForBugReport = findViewById(R.id.imageView_OpenLinkBugReport);
        LinkForBugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/issues/new?template=bug-report-for-gymtrim.md");
            }
        });

        //Link to feature request/https://github.com/naibaf-1/GymTrim/issues/new?template=feature_request.md
        TextView FeatureRequestLink = findViewById(R.id.textView_FeatureRequest);
        FeatureRequestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/issues/new?template=feature-request-for-gymtrim.md");
            }
        });
        ImageView LinkForFeatureRequest = findViewById(R.id.imageView_OpenLinkFeatureRequest);
        LinkForFeatureRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/issues/new?template=feature-request-for-gymtrim.md");
            }
        });

        //Link to license
        TextView LicenseLink = findViewById(R.id.textView_License);
        LicenseLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim?tab=License-1-ov-file");
            }
        });
        ImageView LinkToLicense = findViewById(R.id.imageView_OpenLinkLicense);
        LinkToLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim?tab=License-1-ov-file");
            }
        });

        //Link to the latest release
        TextView LatestRelease = findViewById(R.id.textView_LinkToLatestRelease);
        LatestRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/releases/latest");
            }
        });
        ImageView LatestReleaseOnGithub = findViewById(R.id.imageView_OpenLinkToLatestRelease);
        LatestReleaseOnGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/releases/latest");
            }
        });

        //Link to the release Page
        TextView ReleasePage = findViewById(R.id.textView_LinkToReleasePage);
        ReleasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/releases");
            }
        });
        ImageView LinkToReleasePage = findViewById(R.id.imageView_OpenLinkToReleasePage);
        LinkToReleasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser("https://github.com/naibaf-1/GymTrim/releases");
            }
        });

    }

    //Function to open a Link
    public void openLinkInBrowser(String url){
        Intent openLink = new Intent(Intent.ACTION_VIEW);
        openLink.setData(Uri.parse(url));
        startActivity(openLink);
    }

    //Todo: Upload to F-Droid
    //For searching exercise: Make one global function & use this instead
    //Add Link to latest release
    //One function for opening links
    //UI improvements
    //Make TextView in data_fragment clickable too
    //Fix the issue of the plans not being sorted as expected
}