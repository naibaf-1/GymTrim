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
