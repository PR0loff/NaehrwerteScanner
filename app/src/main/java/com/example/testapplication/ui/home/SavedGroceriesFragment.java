package com.example.testapplication.ui.home;

import static java.lang.Math.min;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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


    private static  Bitmap _image;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_savedgroceries, container, false);

        ListView lv = view.findViewById(R.id.list_view_lebensmittel);
        lv = fillListView(lv);

        ((ListView) view.findViewById(R.id.list_view_lebensmittel)).setAdapter(lv.getAdapter());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private ListView fillListView(ListView _lv)
    {
        //TODO: Alle Lebensmittel mit Vorlage Tag in die ListView hinzufügen

        Cursor cursor = dbHelper.getLebensmittelVorlagen();

        String[] fromColumns = {"Name"};
        int[] toViews = {android.R.id.text1};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(), android.R.layout.simple_list_item_1, cursor, fromColumns, toViews, 0);



        ListView listview = _lv;
        listview.setAdapter(adapter);

        Log.println(Log.DEBUG, "LISTEITEM", "erstellen");
        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Cursor curser = adapter.getCursor();
            cursor.moveToFirst();
            cursor.moveToPosition(i);
            Log.println(Log.DEBUG, "Liste", "i: "+i);
            selectetItemID = cursor.getInt(0);
            selectedItemName = cursor.getString(1);
            Log.println(Log.DEBUG, "Liste", "item: "+selectetItemID);
            Log.println(Log.DEBUG, "Liste", "item: "+selectedItemName);

            showDialog(selectetItemID, selectedItemName);
        });

        return listview;

    }

    private void showDialog(int LebID, String _name)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setMessage("Diese Nährwerte sind unter dem Eintrag "+ _name+" gespeichert:" );
        alert.setTitle("Eingabe");

        Cursor cursor = dbHelper.getNutrients(LebID);
        cursor.moveToFirst();
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_ui_inupt_nutrients, null);

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Brennwert)).setText(String.valueOf(cursor.getInt(1)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Kohlenhydrate)).setText(String.valueOf(cursor.getInt(2)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Eiweiß)).setText(String.valueOf(cursor.getInt(3)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_davonZucker)).setText(String.valueOf(cursor.getInt(4)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Fettsäuren)).setText(String.valueOf(cursor.getInt(5)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Fett)).setText(String.valueOf(cursor.getInt(6)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Salz)).setText(String.valueOf(cursor.getInt(7)));

        ((EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Name)).setText(cursor.getString(0));

        ((CheckBox) customLayout.findViewById(R.id.checkBox)).setVisibility(View.INVISIBLE);

        Log.println(Log.DEBUG, "Debug", "Layout befüllt");
        alert.setView(customLayout);

        //Eingaben werden gespeichert,, optional auch als Datenbankeintrag für Später
        alert.setPositiveButton("Bestätigen", (dialogInterface, i) -> {

            CheckBox cb = (CheckBox) customLayout.findViewById(R.id.checkBox);

            EditText et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Brennwert);
            double brennwert = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_davonZucker);
            double zucker = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Kohlenhydrate);
            double kohlenhydrate = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Fett);
            double fett = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Fettsäuren);
            double fettsaeuren = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Eiweiß);
            double eiweis = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Salz);
            double salz = Double.parseDouble(et.getText().toString());
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Name);
            String name = et.getText().toString();
            et = (EditText) customLayout.findViewById(R.id.editTextNumberDecimal_Menge);
            double menge = Double.parseDouble(et.getText().toString());

            dbHelper.addLebensmittel(name, brennwert, fett, fettsaeuren, kohlenhydrate, zucker, eiweis, salz, false, menge);


        });

        //bei Abbruch wird der Dialog geschlossen
        alert.setNegativeButton("Abbrechen", (dialog, whichButton) -> {
        });

        alert.show();

    }

    public static void setImage(Bitmap image)
    {
        _image = image;
    }
}