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
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.naibaf.GymTrim.SettingsAndInformations.InformationsActivity;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.SettingsAndInformations.SettingsActivity;

public class PopUpMenuInflater {

    public static void inflateSettingsAndInformationsMenu(Context context, ImageButton inflateMenu){
        PopupMenu popupMenu = new PopupMenu(context, inflateMenu);
        popupMenu.getMenuInflater().inflate(R.menu.settings_and_informations_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int idOfPressed = item.getItemId();

                if (idOfPressed == R.id.menu_item_settings) {
                    Intent goToSettings = new Intent(context, SettingsActivity.class);
                    context.startActivity(goToSettings);
                    return true;
                } else if (idOfPressed == R.id.menu_item_informations) {
                    Intent goToInformations = new Intent(context, InformationsActivity.class);
                    context.startActivity(goToInformations);
                    return true;
                } else {
                    return true;
                }
            }

        });
        popupMenu.show();
    }

}
