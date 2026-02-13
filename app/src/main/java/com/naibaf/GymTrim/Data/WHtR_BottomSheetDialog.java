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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.naibaf.GymTrim.R;

import java.text.DecimalFormat;

public class WHtR_BottomSheetDialog extends BottomSheetDialogFragment {
    float whtr = 0;
    float waist_girth;
    float height;

    int age;
    TextView WHtRTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.whtr_buttom_sheet_layout,
                container, false);

        FloatingActionButton Apply = v.findViewById(R.id.floatingActionButton_Apply_WHtR);
        TextInputEditText WaistGirth = v.findViewById(R.id.TextInputEditText_waist_girth_WHtR);
        TextInputEditText Height = v.findViewById(R.id.TextInputEditText_height_WHtR);
        TextInputEditText Age = v.findViewById(R.id.TextInputEditText_age_WHtR);
        WHtRTextView = v.findViewById(R.id.TextView_WHtRValue);

        //Get columns
        TextView Underweight = v.findViewById(R.id.textView_underweight);
        TextView NormalWeight = v.findViewById(R.id.textView_normal_weight);
        TextView Overweight = v.findViewById(R.id.textView_overweight);
        TextView Adiposity = v.findViewById(R.id.textView_adiposity);
        TextView SevereAdiposity = v.findViewById(R.id.textView_severe_adiposity);


        Apply.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                //Check data, if everything is set apply it to the graph
                if(!WaistGirth.getText().toString().trim().isEmpty() && !Height.getText().toString().trim().isEmpty() && !Age.getText().toString().trim().isEmpty()){
                    // Get the data
                    waist_girth = Float.parseFloat(WaistGirth.getText().toString());
                    height = Float.parseFloat(Height.getText().toString());
                    age = Integer.parseInt(Age.getText().toString());

                    //Calculate WHR
                    whtr = waist_girth/height;
                    //Fill table per age group
                    if (age <= 15){
                        Underweight.setText("... - 0.34");
                        NormalWeight.setText("0.35 - 0.45");
                        Overweight.setText("0.46 - 0.51");
                        Adiposity.setText("0.52 - 0.63");
                        SevereAdiposity.setText("0.64 - ...");
                    } else if (age <= 40) {
                        Underweight.setText("... - 0.40");
                        NormalWeight.setText("0.41 - 0.50");
                        Overweight.setText("0.51 - 0.56");
                        Adiposity.setText("0.57 - 0.68");
                        SevereAdiposity.setText("0.69 - ...");
                    } else if (age > 50){
                        Underweight.setText("... - 0.40");
                        NormalWeight.setText("0.41 - 0.60");
                        Overweight.setText("0.61 - 0.66");
                        Adiposity.setText("0.67 - 0.78");
                        SevereAdiposity.setText("0.79 - ...");
                    }

                    String calculatedWHtR = getString(R.string.calculated_WHtR);
                    WHtRTextView.setText(calculatedWHtR + " " + new DecimalFormat("##.##").format(whtr));

                } else {
                    Toast.makeText(getContext(), R.string.error_missing_data, Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

}

//Sources: https://www.bmi3d.com/whtr-waisttoheightratio.html
