package com.example.testapplication.ui.dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testapplication.R;
import com.example.testapplication.backend.dbHelper;
import com.example.testapplication.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    int selectedItemId;
    String selectedItemDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Spinner spinner = view.findViewById(R.id.spinner);

        spinner=fillSpinner(spinner);

        ((Spinner) view.findViewById(R.id.spinner)).setAdapter(spinner.getAdapter());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private Spinner fillSpinner(Spinner _spin)
    {
        //TODO: Alle Lebensmittel mit Vorlage Tag in die ListView hinzufügen

        Cursor cursor = dbHelper.getDates();

        String[] fromColumns = {"Datum"};
        int[] toViews = {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(), android.R.layout.simple_list_item_1, cursor, fromColumns, toViews, 0);



        Spinner spinner = _spin;
        spinner.setAdapter(adapter);

        Log.println(Log.DEBUG, "LISTEITEM", "erstellen");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Cursor curser = adapter.getCursor();
                cursor.moveToFirst();
                cursor.moveToPosition(i);
                Log.println(Log.DEBUG, "Liste", "i: "+i);
                selectedItemId = cursor.getInt(0);
                selectedItemDate = cursor.getString(1);
                Log.println(Log.DEBUG, "Liste", "item: "+selectedItemId);
                Log.println(Log.DEBUG, "Liste", "item: "+selectedItemDate);

                //TODO: Nährwerte vom gewählten tag aus db holen und anzeigen



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }

        });

        return spinner;

    }


}