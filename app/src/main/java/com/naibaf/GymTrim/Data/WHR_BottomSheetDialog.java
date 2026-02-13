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

package com.naibaf.GymTrim.Data;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.naibaf.GymTrim.R;

import java.text.DecimalFormat;

public class WHR_BottomSheetDialog extends BottomSheetDialogFragment {
    float whr = 0;
    float waist_girth;
    float hip_girth;
    String genus;
    TextView WHRTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.whr_buttom_sheet_layout,
                container, false);

        FloatingActionButton Apply = v.findViewById(R.id.floatingActionButton_Apply_WHR);
        RadioGroup Genus = v.findViewById(R.id.RadioGroup_genus_WHR);
        TextInputEditText WaistGirth = v.findViewById(R.id.TextInputEditText_waist_girth_WHR);
        TextInputEditText HipGirth = v.findViewById(R.id.TextInputEditText_hip_girth_WHR);
        WHRTextView = v.findViewById(R.id.TextView_WHRValue);
        TextView WHRInformation = v.findViewById(R.id.textView_Informations_WHR);

        Genus.clearCheck();

        Apply.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // Get genus get started radio button id
                int id = Genus.getCheckedRadioButtonId();
                if (id == R.id.radioButton_female_WHR) {
                    genus = "female";
                } else if (id == R.id.radioButton_male_WHR) {
                    genus = "male";
                } else {
                    genus = null;
                }

                // Calculate & display WHR
                if (!WaistGirth.getText().toString().trim().isEmpty() && !HipGirth.getText().toString().trim().isEmpty() && genus != null) {
                    // Check data, if everything is set apply it to the graph
                    waist_girth = Float.parseFloat(WaistGirth.getText().toString());
                    hip_girth = Float.parseFloat(HipGirth.getText().toString());

                    // Calculate WHR
                    whr = waist_girth / hip_girth;
                    if (genus.equals("female")) {
                        WHRInformation.setText(R.string.WHR_informations_female);
                    } else if (genus.equals("male")) {
                        WHRInformation.setText(R.string.WHR_informations_male);
                    }

                    String calculatedWHR = getString(R.string.calculated_WHR);
                    WHRTextView.setText(calculatedWHR + " " + new DecimalFormat("##.##").format(whr));

                } else {
                    Toast.makeText(getContext(), R.string.error_missing_data, Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

}

//https://www.geeksforgeeks.org/implement-radiobutton-with-custom-layout-in-android/
// Sources: https://www.bmi3d.com/whr-waisthipratio.html
