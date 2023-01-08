package com.example.testapplication.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testapplication.R;
import com.example.testapplication.backend.dbHelper;

import java.text.DecimalFormat;

public class DashboardFragment extends Fragment {

    int selectedItemId;
    String selectedItemDate;
    SharedPreferences sharedPref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container,
                false);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        Spinner spinner = view.findViewById(R.id.spinner);

        spinner = fillSpinner(spinner);

        ((Spinner) view.findViewById(R.id.spinner))
                .setAdapter(spinner.getAdapter());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    //Methode um einen Adapter zum Spinner hinzuzufügen welcher alle Daten enthällt
    private Spinner fillSpinner(Spinner _spin) {
        Cursor cursor = dbHelper.getDates();

        String[] fromColumns = {"Datum"};
        int[] toViews = {android.R.id.text1};
        //Adapter mit dem Cursor füllen
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(),
                android.R.layout.simple_list_item_1, cursor, fromColumns,
                toViews, 0);

        Spinner spinner = _spin;
        spinner.setAdapter(adapter);

        //Dem spinner ein Listener hinzufügen um beim auswählen eines Elementes
        //fortfahren zu kännen
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int i, long l) {

                Cursor curser = adapter.getCursor();
                cursor.moveToFirst();
                cursor.moveToPosition(i);
                selectedItemId = cursor.getInt(0);
                selectedItemDate = cursor.getString(1);
                setWerte();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        return spinner;

    }

    //Methode um die Nährstoffdaten anzuzeigen
    private void setWerte() {
        Cursor cursor = dbHelper.getNährwerteproTag(selectedItemDate);

        double Fett = 0, Fettsaeuren = 0, Kohlenhydrate = 0, Zucker = 0;
        double Eiweis = 0, Salz = 0;
        int Brennwert = 0;


        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Brennwert += (cursor.getInt(1) * cursor.getDouble(8) / 100);
            Fett += cursor.getDouble(2) * cursor.getDouble(8) / 100;
            Fettsaeuren += cursor.getDouble(3) * cursor.getDouble(8) / 100;
            Kohlenhydrate += cursor.getDouble(4) * cursor.getDouble(8) / 100;
            Zucker += cursor.getDouble(5) * cursor.getDouble(8) / 100;
            Eiweis += cursor.getDouble(6) * cursor.getDouble(8) / 100;
            Salz += cursor.getDouble(7) * cursor.getDouble(8) / 100;

            cursor.moveToNext();
        }

        double sollBrennwert = sharedPref.getFloat("Brennwert",2000);
        double sollFett = sharedPref.getFloat("Fett",65);
        double sollFettsaeuren = sharedPref.getFloat("Fettsaeuren",22);
        double sollKohlenhydrate = sharedPref.getFloat("Kohlenhydrate",300);
        double sollZucker = sharedPref.getFloat("Zucker",50);
        double sollEiweis = sharedPref.getFloat("Eiweis",60);
        double sollSalz = sharedPref.getFloat("Salz",6);

        DecimalFormat df = new DecimalFormat("#.##");

        ((TextView) getActivity().findViewById(R.id.textViewProgress_Kalorien))
                .setText(df.format(Brennwert) +"\n/"+
                        String.valueOf(sollBrennwert));
        ((TextView) getActivity().findViewById(R.id.textViewProgress_Fett))
                .setText(df.format(Fett)+"\n/"+ String.valueOf(sollFett));
        ((TextView) getActivity().findViewById(R.id.textViewProgress_Fettsaeuren))
                .setText(df.format(Fettsaeuren)+"\n/"+
                        String.valueOf(sollFettsaeuren));
        ((TextView) getActivity().findViewById(R.id.textViewProgress_Kohlenhydrate))
                .setText(df.format(Kohlenhydrate)+"\n/"+
                        String.valueOf(sollKohlenhydrate));
        ((TextView) getActivity().findViewById(R.id.textViewProgress_Zucker))
                .setText(df.format(Zucker)+"\n/"+ String.valueOf(sollZucker));
        ((TextView) getActivity().findViewById(R.id.textViewProgress_Eiweis))
                .setText(df.format(Eiweis)+"\n/"+ String.valueOf(sollEiweis));
        ((TextView) getActivity().findViewById(R.id.textViewProgress_Salz))
                .setText(df.format(Salz)+"\n/"+ String.valueOf(sollSalz));


        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Kalorien))
                .setMax((int) sollBrennwert);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Kalorien))
                .setProgress((int) Brennwert);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Fett))
                .setMax((int) sollFett);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Fett))
                .setProgress((int) Fett);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Fettsaeuren))
                .setMax((int) sollFettsaeuren);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Fettsaeuren))
                .setProgress((int) Fettsaeuren);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Kohlenhydrate))
                .setMax((int) sollKohlenhydrate);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Kohlenhydrate))
                .setProgress((int) Kohlenhydrate);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Zucker))
                .setMax((int) sollZucker);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Zucker))
                .setProgress((int) Zucker);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Eiweis))
                .setMax((int) sollEiweis);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Eiweis))
                .setProgress((int) Eiweis);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Salz))
                .setMax((int) sollSalz);
        ((ProgressBar)getActivity().findViewById(R.id.progressBar_Salz))
                .setProgress((int) Salz);


    }

}