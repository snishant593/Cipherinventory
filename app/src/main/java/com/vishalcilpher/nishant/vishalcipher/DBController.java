package com.vishalcilpher.nishant.vishalcipher;

/**
 * Created by nishant on 18/5/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBController extends SQLiteOpenHelper {
 private static final String LOGCAT = null;

 public DBController(Context applicationcontext) {
     super(applicationcontext, "Vishal.db", null, 1);  // creating DATABASE
     Log.d(LOGCAT, "Created");
 }

 @Override
 public void onCreate(SQLiteDatabase database) {
     String query;
     query = "CREATE TABLE IF NOT EXISTS retail_physical_scanning (SITE_CODE TEXT,STOCK_TYPE TEXT,ECODE INTEGER,AREA_TYPE TEXT,FLOOR TEXT,GANDOLA_NO TEXT,BARCODE TEXT PRIMARY KEY,QTY TEXT,START_DATE TEXT ,START_TIME TEXT,END_DATE TEXT,END_TIME TEXT)";
     database.execSQL(query);
     query = "CREATE TABLE IF NOT EXISTS retail_physical_count (SITE_CODE TEXT,STOCK_TYPE TEXT,ECODE INTEGER,AREA_TYPE TEXT,FLOOR TEXT,GANDOLA_NO TEXT PRIMARY KEY,QTY TEXT,START_DATE TEXT ,START_TIME TEXT,END_DATE TEXT,END_TIME TEXT)";
     database.execSQL(query);
     Log.e("@@@@@@@@",query);
 }


 @Override
 public void onUpgrade(SQLiteDatabase database, int version_old,
                       int current_version) {
     String query;
     query = "DROP TABLE IF EXISTS retail_physical_scanning";
     database.execSQL(query);
     onCreate(database);
 }


    public void Insertgateentry(String Storename, String stock, String ecode, String area, String floor, String gandolano, String barcode,
                                String qty, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SITE_CODE", Storename);
        contentValues.put("STOCK_TYPE ",stock);
         contentValues.put("ECODE",ecode);
        contentValues.put("AREA_TYPE", area);
        contentValues.put("FLOOR", floor);
        contentValues.put("GANDOLA_NO", gandolano);
        contentValues.put("BARCODE", barcode);
        contentValues.put("QTY",qty);
        contentValues.put("START_DATE", date());
        contentValues.put("START_TIME", time);
        contentValues.put("END_DATE", date());
        contentValues.put("END_TIME", Time());

        db.insert("retail_physical_scanning", null, contentValues);

        Log.e("############","physicalentryinserted");

    }



    public void removeSingleContact(String title) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM retail_physical_scanning  WHERE BARCODE = '" + title + "'");

        //Close the database
        database.close();
    }


    public ArrayList<String> getScanData(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT BARCODE FROM retail_physical_scanning",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("BARCODE")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }


    public ArrayList<String> getQuantity(String user){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT Sum(QTY) AS QTY FROM retail_physical_scanning where GANDOLA_No= '" + user + "'",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("QTY")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }

    public ArrayList<String> getScangandolaData(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT GANDOLA_NO FROM retail_physical_count",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("GANDOLA_NO")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }




    //SITE_CODE TEXT,STOCK_TYPE TEXT,ECODE INTEGER,AREA_TYPE TEXT,FLOOR TEXT,GANDOLA_NO TEXT,BARCODE TEXT PRIMARY KEY,QTY TEXT,START_DATE TEXT ,START_TIME TEXT,END_DATE TEXT,END_TIME TEXT
public void Insertphysicalcountentry(String Storename, String stock, String ecode, String area, String floor, String gandolano,
                                     String qty, String time)
{
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("SITE_CODE", Storename);
    contentValues.put("STOCK_TYPE ",stock);
    contentValues.put("ECODE",ecode);
    contentValues.put("AREA_TYPE", area);
    contentValues.put("FLOOR", floor);
    contentValues.put("GANDOLA_NO", gandolano);
    contentValues.put("QTY",qty);
    contentValues.put("START_DATE", date());
    contentValues.put("START_TIME", time);
    contentValues.put("END_DATE", date());
    contentValues.put("END_TIME", Time());

    db.insert("retail_physical_count", null, contentValues);

    Log.e("############","physicalcountinserted");

}

    public Cursor getScanninggandola()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mInvoiceNo = db.rawQuery("SELECT SITE_CODE,STOCK_TYPE,ECODE,AREA_TYPE,FLOOR,GANDOLA_NO,BARCODE,QTY,START_DATE,START_TIME,END_DATE,END_TIME FROM retail_physical_scanning",null);
        return mInvoiceNo;
    }


    public Cursor getphysicalgandola()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mInvoiceNo = db.rawQuery("SELECT * FROM  retail_physical_count",null);
        return mInvoiceNo;
    }


    public boolean CheckIsDataAlreadyInDBorNot(String Barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        /*String[] params = new String[1];
        params[0] = Phone + "%";*/
        String getQty=null;
        String Query = ("select BARCODE from retail_physical_scanning where BARCODE ='"+Barcode+"'");
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            Log.e("InDatabase","InDatabase");
            return false;
        }
        cursor.close();
        Log.e("Notindatabase","Notindatabase");
        return true;
    }

    public void updateExistinghustatus(String HuNo, String qty)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("QTY",qty);
        Log.e("QTY",qty);
        Log.e("Barcode",HuNo);
        db.update(" retail_physical_scanning", contentValues, "BARCODE = ? ", new String[]{String.valueOf(HuNo)});

    }


    public ArrayList<String> gethualreadyscanned(String Barcode)
    {
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select QTY from retail_physical_scanning where BARCODE ='"+Barcode+"'",null);
        if (cur.moveToFirst()) {
            do {


                arraylist.add(cur.getString(cur.getColumnIndex("QTY")));


            } while (cur.moveToNext());

        }
        return arraylist;

    }




    public String date() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        Format formatter = new SimpleDateFormat("dd-MMM-yyyy");

        //  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = formatter.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }




    public String Time() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }


    public ArrayList<Gandolamodel> getalldatainvoice() {
        ArrayList<Gandolamodel> invoicenolist = new ArrayList<Gandolamodel>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select DISTINCT GANDOLA_NO,SITE_CODE,STOCK_TYPE,AREA_TYPE,ECODE,FLOOR from retail_physical_scanning", null);
            if (cursor.moveToFirst()) {
                do {
                    Gandolamodel gandolamodel=new Gandolamodel();
                    gandolamodel.setStoreName(cursor.getString(cursor.getColumnIndex("SITE_CODE")));
                    gandolamodel.setStocktype(cursor.getString(cursor.getColumnIndex("STOCK_TYPE")));
                    gandolamodel.setAreatype(cursor.getString(cursor.getColumnIndex("AREA_TYPE")));
                    gandolamodel.setEcode(cursor.getString(cursor.getColumnIndex("ECODE")));
                    gandolamodel.setFloor(cursor.getString(cursor.getColumnIndex("FLOOR")));
                    gandolamodel.setGandolano(cursor.getString(cursor.getColumnIndex("GANDOLA_NO")));

                    invoicenolist.add(gandolamodel);
                } while (cursor.moveToNext());

            }


        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        return invoicenolist;
    }

}
