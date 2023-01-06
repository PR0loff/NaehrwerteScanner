package com.example.testapplication.backend;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class dbHelper {
    private static SQLiteDatabase db;

    public dbHelper()
    {

    }

    public static void initialize(SQLiteDatabase _db)
    {
        db= _db;
    }

    public static void addLebensmittel(String _name, double _brennwert, double _fett, double _fettsaeuren, double _kohlenhydrate, double _zucker, double _eiweis, double _salz, boolean _istVorlage, double _menge)
    {
        db.execSQL("INSERT INTO Lebensmittel (Name, Brennwert, Fett, Fettsaeure, Kohlenhydrate, Zucker, Eiweis, Salz, istVorlage)" +
                "VALUES ('"+_name+ "', "+_brennwert+", "+ _fett+", "+_fettsaeuren+", "+_kohlenhydrate+", "+_zucker+", "+_eiweis+", "+_salz+", "+_istVorlage+" )");

        //TODO LebensmittelID herausfinden und zur Tabelle hinzufügen

        String query = "SELECT MAX(ID) FROM Lebensmittel";
        Cursor resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();
        int LebID = resultSet.getInt(0);
        resultSet.close();

        LocalDate today = LocalDate.now();
        System.out.println();
        int TagID = createDay(today);

        NährwertZumTagHinzufügen(_menge,TagID,LebID);

    }

    public static void createTables()
    {
        db.execSQL("CREATE TABLE if not exists Lebensmittel (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "Name varchar(256) ," +
                "Brennwert double(4,2)," +
                "Fett double(4,2)," +
                "Fettsaeure double(4,2)," +
                "Kohlenhydrate double(4,2)," +
                "Zucker double(4,2)," +
                "Eiweis double(4,2)," +
                "Salz double(4,2)," +
                "istVorlage boolean)");

        db.execSQL("CREATE TABLE if not exists Tagesverzehr (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Datum varchar(256))");

        db.execSQL("CREATE TABLE if not exists LebTag (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LebID int," +
                "TagID int," +
                "Menge double(4,2))");

        db.execSQL("CREATE TABLE if not exists LebRez (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LebID int," +
                "RezeptID int," +
                "Menge double(4,2))");

        db.execSQL("CREATE TABLE if not exists Rezept (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "Name varchar(256) ," +
                "Brennwert double(4,2)," +
                "Fett double(4,2)," +
                "Fettsaeure double(4,2)," +
                "Kohlenhydrate double(4,2)," +
                "Zucker double(4,2)," +
                "Eiweis double(4,2)," +
                "Salz double(4,2))");
    }

    public static void dropTables()
    {
        db.execSQL("DROP TABLE Tagesverzehr");
        db.execSQL("DROP TABLE Lebensmittel");
        db.execSQL("DROP TABLE LebTag");
        db.execSQL("DROP TABLE LebRez");
        db.execSQL("DROP TABLE Rezept");
    }

    public static int createDay(LocalDate _date)
    {
        //TODO quarry in Tagesverzehr ob der Tag angelegt ist, gibt Tages ID zurück, sonst wird der Tag neu angelegt

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        String date = dtf.format(_date);
        String query = "SELECT ID FROM Tagesverzehr WHERE Datum = '"+date+"'";
        Cursor resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();

        if(resultSet.getCount() == 0)
        {
            db.execSQL("INSERT INTO Tagesverzehr (Datum)" +
                    "VALUES ('"+date+"')");
        }
        resultSet.close();

        query = "SELECT ID FROM Tagesverzehr WHERE Datum = '"+date+"'";
        resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();

        int TagID = resultSet.getInt(0);
        resultSet.close();

        return TagID;
    }

    private static void NährwertZumTagHinzufügen(double menge, int tagID, int lebID)
    {
        db.execSQL("INSERT INTO LebTag (LebID, TagID, Menge)" +
                "VALUES ("+lebID+ ", "+tagID+", "+ menge+")");
    }

    public static Cursor getNährwerteproTag(LocalDate _date)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        String date = dtf.format(_date);
        String query = "SELECT Name, Brennwert, Fett, Fettsaeure, Kohlenhydrate, Zucker, Eiweis, Salz " +
                "FROM Lebensmittel, Tagesverzehr, LebTag " +
                "WHERE Tagesverzehr.Datum = '"+date+"' " +
                "AND Tagesverzehr.ID = LebTag.TagID " +
                "AND Lebensmittel.ID = LebTag.LebID";
        Cursor resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();

        return resultSet;
    }

    public static Cursor getNutrients(int id)
    {
        String query = "SELECT Name, Brennwert, Fett, Fettsaeure, Kohlenhydrate, Zucker, Eiweis, Salz  "+
                "FROM Lebensmittel " +
                "WHERE ID = "+id;
        Cursor resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();

        return resultSet;
    }

    public static Cursor getDates()
    {
        String query = "SELECT ID AS _id , Datum " +
                "FROM Tagesverzehr ";
        Cursor resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();

        return resultSet;
    }

    public static Cursor getLebensmittelVorlagen()
    {
        String query = "SELECT ID AS _id , Name " +
                "FROM Lebensmittel " +
                "WHERE istVorlage = 1";
        Cursor resultSet = db.rawQuery(query, null);
        resultSet.moveToFirst();

        return resultSet;
    }
}
