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
import java.util.Objects;

public class BMI_BottomSheetDialog extends BottomSheetDialogFragment {
    float bmi;
    float weight;
    float size;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bmi_bottom_sheet_layout,
                container, false);

        FloatingActionButton Apply = v.findViewById(R.id.floatingActionButton_Apply_BMI);
        TextInputEditText Size = v.findViewById(R.id.TextInputEditText_Size_BMI);
        TextInputEditText Weight = v.findViewById(R.id.TextInputEditText_BMI);

        TextView BMITextView = v.findViewById(R.id.TextView_BMIValue);
        BMITextView.setText(R.string.nothing_calculated_yet);

        Apply.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                //Check data, if everything is set apply it to the graph
                size = Float.parseFloat(Objects.requireNonNull(Size.getText()).toString());
                weight = Float.parseFloat(Objects.requireNonNull(Weight.getText()).toString());

                //Calculate & display BMI
                if(size >= 0 && weight >= 0){
                    bmi = weight /(size * size);
                    getResources();
                    String calculatedBMI = getString(R.string.calculated_BMI);
                    BMITextView.setText(calculatedBMI + " " + new DecimalFormat("##.##").format(bmi));
                } else {
                    Toast.makeText(getContext(), R.string.error_missing_data, Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

}

//Sources: https://www.bmi3d.com/formula.html and others
