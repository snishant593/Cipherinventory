package com.vishalcilpher.nishant.vishalcipher;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PhysicalcountActivity extends AppCompatActivity {

    EditText GandolaNo, Qty;
    Button Exportcsv,Exit;
    DBController dbController;
    TextView Timeqty;

    String Storecode, Stocktype, Areatype, Floortype, userTypedString, Ecode,Storecodeup;

    private String STORECODE;


    android.support.v7.app.ActionBar actionBar;
    ArrayList<String>recieved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physicalcount);

        dbController = new DBController(this);


        GandolaNo = (EditText) findViewById(R.id.gandolano);
        Qty = (EditText) findViewById(R.id.physqty);
        Exportcsv = (Button)findViewById(R.id.next);
        Timeqty = (TextView) findViewById(R.id.timetotal);
        Exit = (Button)findViewById(R.id.exit) ;

        Bundle bundle1 = getIntent().getExtras();

        Storecode = bundle1.getString("Storecode");
        Stocktype = bundle1.getString("Stocktype");
        Areatype = bundle1.getString("Areatype");
        Floortype = bundle1.getString("Floortype");
        Ecode = bundle1.getString("Ecode");
        STORECODE =Storecode.toUpperCase();

        Log.e("Value", Storecode + " " + Stocktype + " " + Areatype
                + " " + Floortype + "" + Ecode + " " + STORECODE);

        Timeqty.setText(Time());

        actionBar = getSupportActionBar();
        //  actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
    //    actionBar.setIcon(R.drawable.ic_launchervishal);



        Exportcsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GandolaNo.getText().toString().matches("") || Qty.getText().toString().matches(""))
                {
                    Toast.makeText(PhysicalcountActivity.this, "No Data To Save", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbController.Insertphysicalcountentry(STORECODE, Stocktype, Ecode, Areatype, Floortype, GandolaNo.getText().toString(), Qty.getText().toString(), Timeqty.getText().toString());


                Toast.makeText(PhysicalcountActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                Exportcsvbuttondialog();
            }
        });

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Exitbuttondialog();
            }
        });


    }

    @Override
    public void onBackPressed() {
     Exitbuttondialog();
    }


    public void Exitbuttondialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Exit!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you Sure you want to Exit");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {


                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public void Exportcsvbuttondialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Export Csv");

        // Setting Dialog Message
        alertDialog.setMessage("Are you Sure you want to Export Csv");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {


                        new Exportgandolascanning().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {

                        new Exportgandolascanning().execute();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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
     public  void cleartext()
     {
         GandolaNo.setText("");
         Qty.setText("");

     }



    public class Exportgandolascanning extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(PhysicalcountActivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;
        String csvfile = GandolaNo.getText().toString();

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Csv Files");
            this.dialog.show();
            dbhelper = new DBController(PhysicalcountActivity.this);
        }

        protected Boolean doInBackground(final String... args) {
            recieved = dbhelper.getScangandolaData();
            if (recieved.size() == 0) {
                Log.e("recieved", "" + recieved);
                return false;
            } else {
                File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalPi/");
                Log.e("Nishant", exportDir.getAbsolutePath());
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, STORECODE.concat("_").concat("G").concat("_").concat(csvfile).concat("_").concat("MAIN").concat(datetime().concat(".csv")));
                try {

                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = dbhelper.getphysicalgandola();
                    csvWrite.writeNext(curCSV.getColumnNames());
                    while (curCSV.moveToNext()) {
                        String arrStr[] = null;
                        String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                        for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                            mySecondStringArray[i] = curCSV.getString(i);
                        }
                        csvWrite.writeNext(mySecondStringArray);
                    }
                    csvWrite.close();
                    curCSV.close();
                    MediaScannerConnection.scanFile (getApplicationContext(), new String[] {file.toString()}, null, null);

                    return true;
                } catch (IOException e) {
                    return false;
                }

            }

        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(PhysicalcountActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
                clearfield();
                cleartext();
            } else {
                Toast.makeText(PhysicalcountActivity.this, "No Gandola file Recieved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public  void clearfield()
    {
        dbController =  new DBController(this);
        SQLiteDatabase db = dbController.getWritableDatabase();
        String tableName = "retail_physical_count";
//        String tableName2 = "retail_hu_excess";
//        String tableName3 = "retail_gate";
        db.execSQL("delete from " + tableName);
        Log.e("Data","Deleted");
        //  db.execSQL("delete from " + tableName2);

    }

    public String datetime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }






}

