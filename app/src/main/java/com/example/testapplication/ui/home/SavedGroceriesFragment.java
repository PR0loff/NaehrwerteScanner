package com.example.testapplication.ui.home;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testapplication.R;
import com.example.testapplication.backend.dbHelper;

public class SavedGroceriesFragment extends Fragment {

    int selectetItemID;
    String selectedItemName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_savedgroceries,
                container, false);

        ListView lv = view.findViewById(R.id.list_view_lebensmittel);
        lv = fillListView(lv);

        ((ListView) view.findViewById(R.id.list_view_lebensmittel))
                .setAdapter(lv.getAdapter());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //ListView wird mit allen gespeicherten Lebensmitteln gefüllt
    private ListView fillListView(ListView _lv) {
        Cursor cursor = dbHelper.getLebensmittelVorlagen();

        String[] fromColumns = {"Name"};
        int[] toViews = {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(),
                android.R.layout.simple_list_item_1, cursor, fromColumns,
                toViews, 0);


        ListView listview = _lv;
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            cursor.moveToFirst();
            cursor.moveToPosition(i);
            selectetItemID = cursor.getInt(0);
            selectedItemName = cursor.getString(1);

            showDialog(selectetItemID, selectedItemName);
        });

        return listview;

    }

    //Öffnet einen Dialog in welchem die gespeicerten Daten angezeigt werden
    //und ggf angepasst werden können
    private void showDialog(int LebID, String _name) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setMessage("Diese Nährwerte sind unter dem Eintrag " + _name
                + " gespeichert:");
        alert.setTitle("Eingabe");

        //View wird mit Daten gefüllt
        Cursor cursor = dbHelper.getNutrients(LebID);
        cursor.moveToFirst();
        final View customLayout = getLayoutInflater().inflate(R.layout
                .alert_ui_inupt_nutrients, null);

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Brennwert))
                .setText(String.valueOf(cursor.getDouble(1)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Kohlenhydrate))
                .setText(String.valueOf(cursor.getDouble(2)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Eiweiß))
                .setText(String.valueOf(cursor.getDouble(3)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_davonZucker))
                .setText(String.valueOf(cursor.getDouble(4)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Fettsäuren))
                .setText(String.valueOf(cursor.getDouble(5)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Fett))
                .setText(String.valueOf(cursor.getDouble(6)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Salz))
                .setText(String.valueOf(cursor.getDouble(7)));

        ((EditText) customLayout.findViewById(R.id
                .editTextNumberDecimal_Name))
                .setText(cursor.getString(0));

        ((CheckBox) customLayout.findViewById(R.id.checkBox))
                .setVisibility(View.INVISIBLE);

        alert.setView(customLayout);

        //Eingaben werden gespeichert
        alert.setPositiveButton("Bestätigen", (dialogInterface, i) -> {

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
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Name);
            if(!et.getText().toString().isEmpty()) {
                name = et.getText().toString();
            }
            et = (EditText) customLayout.findViewById(R.id.
                    editTextNumberDecimal_Menge);
            if(!et.getText().toString().isEmpty()) {
                menge = Double.parseDouble(et.getText().toString());
            }

            if( name == "" || menge == 0.)
            {
                return;
            }

            dbHelper.addLebensmittel(name, brennwert, fett, fettsaeuren,
                    kohlenhydrate, zucker, eiweis, salz, false, menge);


        });

        //bei Abbruch wird der Dialog geschlossen
        alert.setNegativeButton("Abbrechen", (dialog, whichButton) -> {
        });

        alert.show();

    }
}