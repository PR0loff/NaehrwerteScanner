package com.example.testapplication;

import static java.lang.Math.min;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.testapplication.backend.TextRecocgnition;
import com.example.testapplication.backend.dbHelper;
import com.example.testapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.mlkit.vision.text.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String currentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //Liste mit Wörtern nach welchen gesucht wird im gesamten Text
    private ArrayList<String> strings = new ArrayList<>(Arrays.asList(
            "Brennwert",
            "Kohlenhydrate",
            "Eiweiß",
            "Zucker",
            "Fettsäuren",
            "Fettsauren",
            "Fettsaeuren",
            "Fett",
            "Salz",
            "pro"));

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteDatabase db = openOrCreateDatabase("NaehrwerteTreacker",
                MODE_PRIVATE, null);
        dbHelper.initialize(db);
        //dbHelper.dropTables();
        dbHelper.createTables();


        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_savedgroceries,
                R.id.navigation_recipe, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController,
                appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    //OnclickMethode für den Fotobutton
    public void onAddButtonClick(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Soll die Eingabe per Foto oder manuell geschehen");
        alert.setTitle("Eingabe");
        //Öffnet einen Eingabe Dialog
        alert.setPositiveButton("Manuell", (dialogInterface, i) -> {
            showInputDioalog();
        });

        //Startet die Kamera Task
        alert.setNegativeButton("Foto", (dialog, whichButton) -> {
            dispatchTakePictureIntent();
        });

        alert.show();
    }

    public void showInputDioalog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Geben sie die Nährwerte pro 100g ein");
        alert.setTitle("Eingabe");
        final View customLayout = getLayoutInflater().inflate(R.layout
                .alert_ui_inupt_nutrients, null);
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

            if(name == "" || menge == 0.)
            {
                return;
            }

            dbHelper.addLebensmittel(name, brennwert, fett, fettsaeuren,
                    kohlenhydrate, zucker, eiweis, salz, cb.isChecked(), menge);

        });

        //bei Abbruch wird der Dialog geschlossen
        alert.setNegativeButton("Abbrechen", (dialog, whichButton) -> {
        });

        alert.show();
    }

    //Erstellt eine Kameraaktivität um eine Foto aufzunehmen und zu Speichern
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.testapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,".jpg", storageDirectory);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Startet die Tewxterkennung sobal das Bild erstellt wurde
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.println(Log.DEBUG, "TESTER", "SUCCESS");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = BitmapFactory.decodeFile(currentPhotoPath);

            photo = rotateBitmap(photo);

            TextRecocgnition.runTextRecognition(photo, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    handleCallback();
                    return null;
                }
            });
        }
    }

    //Sobald deie Texterkennung fertig ist, wird der Text analysiert
    private void handleCallback() {
        List<Text.TextBlock> TextBlocks = null;
        TextBlocks = TextRecocgnition.TextBlocks.getTextBlocks();
        int length = TextBlocks.size();
        Log.println(Log.DEBUG, "TESTER", String.valueOf(length));
        extractRelevantText(TextBlocks);
    }

    //aus dem gesamt erkannten Text wird der relevante herausgefiltert
    private void extractRelevantText(List<Text.TextBlock> TextBlocks) {
        ArrayList<String> relevantTextMengen = new ArrayList<String>();
        ArrayList<String> relevantTextNutrient = new ArrayList<String>();
        for (int i = 0; i < TextBlocks.size(); i++) {
            List<Text.Line> lines = TextBlocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                String line = "";
                String prev = "";
                for (int k = 0; k < elements.size(); k++) {

                    String t = elements.get(k).getText();


                    //Code zum Herausfiltern der Gramm- und Kalorienzahlen
                    Pattern p = Pattern.compile("(\\d+[\\.,]*\\d*)([gG9]*)");
                    Matcher m = p.matcher(t);
                    if (m.matches()) {
                        if (m.group(2).equals("g") || m.group(2).equals("G")
                                || m.group(2).equals("9")) {
                            relevantTextMengen.add(t);
                        } else {
                            prev = t;
                        }
                    } else if (prev != "") {
                        switch (t) {
                            //manchmal wird ein kleines g als 9 erkannt
                            case "9":
                            case "g":
                            case "kcal":
                            case "ml":
                                if (relevantTextMengen.contains("100g")
                                        && t.contains("100"))
                                    break;
                                relevantTextMengen.add(prev + t);
                            default:
                                prev = "";
                                break;
                        }
                    }

                    for (String s : strings) {
                        if (t.contains(s)) {
                            relevantTextNutrient.add(s);
                            break;
                        }
                    }
                    line += " " + elements.get(k).getText();
                }
            }
        }
        createCameraInputDialog(relevantTextNutrient, relevantTextMengen);
    }


    //Öffnet den Eingabe Dialog mit den erkannten Werten, damit diese überprüft
    //und geändert werden können
    private void createCameraInputDialog(ArrayList<String> nutrients,
                                         ArrayList<String> mengen) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Geben sie die Nährwerte pro 100g ein");
        alert.setTitle("Eingabe");
        final View customLayout = getLayoutInflater().inflate(R.layout
                .alert_ui_inupt_nutrients, null);

        for (int i = 0; i < min(nutrients.size(), mengen.size()); i++) {
            String m = mengen.get(i);
            String n = nutrients.get(i);

            m = m.replace(",", ".");
            m = m.replace("g", "");
            if (m.contains("kcal")) {
                ((EditText) customLayout.findViewById(R.id
                        .editTextNumberDecimal_Brennwert))
                        .setText(m.replace("kcal", ""));
            }

            switch (n) {
                case "Brennwert":
                    break;
                case "Kohlenhydrate":
                    ((EditText) customLayout.findViewById(R.id
                            .editTextNumberDecimal_Kohlenhydrate)).setText(m);
                    break;
                case "Eiweiß":
                    ((EditText) customLayout.findViewById(R.id
                            .editTextNumberDecimal_Eiweiß)).setText(m);
                    break;
                case "Zucker":
                    ((EditText) customLayout.findViewById(R.id
                            .editTextNumberDecimal_davonZucker)).setText(m);
                    break;
                case "Fettsäuren":
                case "Fettsauren":
                case "Fettsaeuren":
                    ((EditText) customLayout.findViewById(R.id
                            .editTextNumberDecimal_Fettsäuren)).setText(m);
                    break;
                case "Fett":
                    ((EditText) customLayout.findViewById(R.id
                            .editTextNumberDecimal_Fett)).setText(m);
                    break;
                case "Salz":
                    ((EditText) customLayout.findViewById(R.id
                            .editTextNumberDecimal_Salz)).setText(m);
                    break;
                default:
                    break;
            }
        }
        Log.println(Log.DEBUG, "Debug", "LAyout befüllt");
        alert.setView(customLayout);

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

            if(name == "" || menge == 0.)
            {
                return;
            }

            dbHelper.addLebensmittel(name, brennwert, fett, fettsaeuren,
                    kohlenhydrate, zucker, eiweis, salz, cb.isChecked(), menge);


        });

        //bei Abbruch wird der Dialog geschlossen
        alert.setNegativeButton("Abbrechen", (dialog, whichButton) -> {
        });

        alert.show();
    }

    //Rotiert eine Bitmap um 90 grad
    private Bitmap rotateBitmap(Bitmap input) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(input, input.getWidth(),
                input.getHeight(), true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                matrix, true);
        return rotatedBitmap;
    }

}