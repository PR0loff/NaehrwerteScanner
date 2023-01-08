package com.example.testapplication.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import com.example.testapplication.R;
import com.example.testapplication.backend.dbHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsFragment extends Fragment {

    //Liste mit Texten für die verschiedenen Einstellungsoptionen
    private ArrayList<String> settings = new ArrayList<>(Arrays.asList(
            "Eigenes Nährwerteziel anpassen",
            "Gespeicherte Lebensmittel löschen",
            "Gesamten Verlauf löschen"));

    int selectetItemID;
    SharedPreferences sharedPref;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        ListView lv = view.findViewById(R.id.list_view_settings);
        lv = fillListView(lv);

        ((ListView) view.findViewById(R.id.list_view_settings))
                .setAdapter(lv.getAdapter());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    //ListView mit verschiedenen Einstellungen füllen
    private ListView fillListView(ListView _lv) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, settings);


        ListView listview = _lv;
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            selectetItemID = i;

            handleInput(selectetItemID);
        });

        return listview;
    }

    //Abhängig von der Eingabe die gewünschte funktion ausführen
    private void handleInput(int index)
    {
        switch(index){
            case 0:
                showDialog();
                break;
            case 1:
                dbHelper.dropLebensmittel();
                break;
            case 2:
                dbHelper.dropTables();
                dbHelper.createTables();
                break;
        }
    }


    //Dialog zum einstellen des Ziels zu üffnen
    private void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setMessage("Diese Werte sind aktuell gespeichert:");
        alert.setTitle("Eingabe");

        final View customLayout = getLayoutInflater().inflate(R.layout.
                alert_ui_inupt_nutrients, null);

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Brennwert))
                .setText(String.valueOf(sharedPref.getFloat("Brennwert", 2000)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Kohlenhydrate))
                .setText(String.valueOf(sharedPref.getFloat("Kohlenhydrate", 300)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Eiweiß))
                .setText(String.valueOf(sharedPref.getFloat("Eiweis", 60)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_davonZucker))
                .setText(String.valueOf(sharedPref.getFloat("Zucker", 50)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Fettsäuren))
                .setText(String.valueOf(sharedPref.getFloat("Fettsaeuren", 22)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Fett))
                .setText(String.valueOf(sharedPref.getFloat("Fett", 65)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Salz))
                .setText(String.valueOf(sharedPref.getFloat("Salz", 6)));


        customLayout.findViewById(R.id.textView_Menge)
                .setVisibility(View.INVISIBLE);
        customLayout.findViewById(R.id.textView_Name)
                .setVisibility(View.INVISIBLE);
        customLayout.findViewById(R.id.textView_Datenbank)
                .setVisibility(View.INVISIBLE);
        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Name))
                .setVisibility(View.INVISIBLE);
        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Menge))
                .setVisibility(View.INVISIBLE);

        ((CheckBox) customLayout.findViewById(R.id.checkBox))
                .setVisibility(View.INVISIBLE);

        alert.setView(customLayout);

        //Eingaben werden gespeichert
        alert.setPositiveButton("Speichern", (dialogInterface, i) -> {

            CheckBox cb = (CheckBox) customLayout.findViewById(R.id.checkBox);

            Double brennwert = 0., zucker = 0., kohlenhydrate = 0., fett = 0.;
            Double salz = 0., menge= 0.,  fettsaeuren = 0., eiweis = 0.;
            String name = "";

            EditText et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Brennwert);
            if(!et.getText().toString().isEmpty()) {
                brennwert = Double.parseDouble(et.getText().toString());
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_davonZucker);
            if(!et.getText().toString().isEmpty()) {
                zucker = Double.parseDouble(et.getText().toString());
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Kohlenhydrate);
            if(!et.getText().toString().isEmpty()) {
                kohlenhydrate = Double.parseDouble(et.getText().toString());
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Fett);
            if(!et.getText().toString().isEmpty()) {
                fett = Double.parseDouble(et.getText().toString());
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Fettsäuren);
            if(!et.getText().toString().isEmpty()) {
                fettsaeuren = Double.parseDouble(et.getText().toString());
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Eiweiß);
            if(!et.getText().toString().isEmpty()) {
                eiweis = Double.parseDouble(et.getText().toString());
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Salz);
            if(!et.getText().toString().isEmpty()) {
                salz = Double.parseDouble(et.getText().toString());
            }

            SharedPreferences.Editor edit = sharedPref.edit();

            edit.putFloat("Brennwert", brennwert.floatValue());
            edit.putFloat("Kohlenhydrate", kohlenhydrate.floatValue());
            edit.putFloat("Zucker", zucker.floatValue());
            edit.putFloat("Fett", fett.floatValue());
            edit.putFloat("Fettsaeuren", fettsaeuren.floatValue());
            edit.putFloat("Eiweis", eiweis.floatValue());
            edit.putFloat("Salz", salz.floatValue());

            edit.apply();

        });

        //bei Abbruch wird der Dialog geschlossen
        alert.setNegativeButton("Abbrechen", (dialog, whichButton) -> {
        });

        alert.show();

    }


}