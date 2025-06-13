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

package com.naibaf.GymTrim.PlansAndTraining;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.naibaf.GymTrim.OtherClasses.RecyclerViewInflater;
import com.naibaf.GymTrim.OtherClasses.SwipeToDeleteCallback;
import com.naibaf.GymTrim.R;
import com.naibaf.GymTrim.RecyclerViewAdapters.PlansCustomRecyclerViewAdapter;
import com.naibaf.GymTrim.SQLiteDatabases.PlansDB;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlansFragment extends Fragment implements PlansCustomRecyclerViewAdapter.ItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView PlanList;

    PlansDB DB;
    public PlansCustomRecyclerViewAdapter PlanListAdapter;
    Cursor planValues;
    Context context;
    View v;
    List<PlansCustomRecyclerViewAdapter.CustomList> PlanArrayList;

    public PlansFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlansFragment.
     */
    public static PlansFragment newInstance(String param1, String param2) {
        PlansFragment fragment = new PlansFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_plans, container, false);

        //Start here

        //Get Basic data
        context = getContext();
        DB = new PlansDB(context);
        planValues = DB.getAllPlans();

        //Add a New Plan
        ImageButton AddPlan = v.findViewById(R.id.floatingActionButton_AddPlan4);
        AddPlan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addExercise = new Intent(context, AddPlanActivity.class);
                startActivityForResult(addExercise, 1);
            }
        });

        //Add Data/Exercises to list => https://www.geeksforgeeks.org/how-to-dynamically-add-elements-to-a-listview-in-android/
        //!Add an own List with more Information's, e.g.: images: https://abhiandroid.com/ui/listview
        PlanList = v.findViewById(R.id.recyclerView_PlansList);

        PlanArrayList = new ArrayList<>();

        PlanListAdapter = new PlansCustomRecyclerViewAdapter(context, PlanArrayList);
        PlanListAdapter = RecyclerViewInflater.buildPlansRecyclerView(context, v, PlanList, this, PlanListAdapter, planValues);
        PlanArrayList = RecyclerViewInflater.PlanArrayList;

        //Delete Plan if user swipes
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final PlansCustomRecyclerViewAdapter.CustomList Plan;
                final int position = viewHolder.getAdapterPosition();
                try {
                    Plan = PlanListAdapter.getData().get(position);
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(context, "IndexOutOfBoundsException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                //Use a Alert Dialog to ask user if he is sure => https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
                // Create the object of AlertDialog Builder class
                MaterialAlertDialogBuilder ConfirmToRemoveDialog = new MaterialAlertDialogBuilder(context);

                final PlansCustomRecyclerViewAdapter.CustomList swipedItem = PlanListAdapter.getData().get(position);
                String itemName = swipedItem.name;

                // Set the message show for the Alert time
                ConfirmToRemoveDialog.setMessage(R.string.delete_item_callback);

                // Set Cancelable true for when the user clicks on the outside the Dialog Box disappears
                ConfirmToRemoveDialog.setCancelable(true);
                ConfirmToRemoveDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        PlanListAdapter.notifyDataSetChanged();
                    }
                });

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                ConfirmToRemoveDialog.setPositiveButton(R.string.button_apply, (dialog, which) -> {
                    DB.deletePlanData(PlanArrayList.get(position).idOfItemInDatabase);
                    PlanListAdapter.removeItem(position);
                    PlanListAdapter.notifyDataSetChanged();
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                ConfirmToRemoveDialog.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                    // If user click cancel then dialog box is canceled.
                    dialog.cancel();
                    PlanListAdapter.notifyDataSetChanged();
                });

                // Create the Alert dialog
                AlertDialog confirmDialog = ConfirmToRemoveDialog.create();
                // Show the Alert Dialog box
                confirmDialog.show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(PlanList);

        //Search through plans
        SearchView Search = v.findViewById(R.id.searchView_SearchForPlan);
        Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ( TextUtils.isEmpty ( newText ) ) {
                    PlanListAdapter.getFilter().filter("");
                } else {
                    PlanListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Boolean wasItemAdded = (Boolean) data.getExtras().get("isItemAdded");
            }
            Cursor newPlansValues = DB.getAllPlans();
            PlanListAdapter = RecyclerViewInflater.buildPlansRecyclerView(context, v, PlanList, this, PlanListAdapter, newPlansValues);
            PlanArrayList = RecyclerViewInflater.PlanArrayList;
            PlanListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DB.close();
        planValues.close();
    }

    @Override
    public void onItemClick(View view, int position) {
        final PlansCustomRecyclerViewAdapter.CustomList item = PlanListAdapter.getData().get(position);
        String itemName = item.name;
        int itemColor = item.color;
        int id = item.idOfItemInDatabase;

        Intent openEditor = new Intent(context, EditPlanActivity.class);
        //Values to identify selected Plan
        openEditor.putExtra("name", itemName);
        openEditor.putExtra("color", itemColor);
        openEditor.putExtra("id", id);
        startActivityForResult(openEditor, 1);
    }

}

//Todo: Implement wipe gestures using ViewPager (If possible with SwipeToDelete)