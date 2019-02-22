package com.vishalcilpher.nishant.vishalcipher;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    Spinner Stocktype,Area,Floor;
    EditText Ecode,Storecode;
    String Stocktypearray[],Areaarray[],Floorarray[];
    ArrayAdapter adapterStocktypearray,adapterareaarray,adapterfloorarray;
    String StockSpinValue,AreaSpinValue,FloorSpinvalue,Scan,Physical;
    RadioButton Scanning,Physicalcount;
    RadioGroup radioGroup;
    Button Exit,Next,Olddata;
    DBController dbController;
    ActionBar actionBar;
    String salesarea,dcarea;
    int maxLength = 4;
    ArrayList<String> recieved = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stocktype = (Spinner)findViewById(R.id.stocktype);
        Area = (Spinner)findViewById(R.id.area);
        Exit = (Button)findViewById(R.id.exit);
        Next = (Button)findViewById(R.id.next);
        Floor = (Spinner)findViewById(R.id.floor);
        radioGroup=(RadioGroup)findViewById(R.id.radiogroup);
        Scanning = (RadioButton)findViewById(R.id.Scanning);
        Olddata = (Button)findViewById(R.id.olddata);

        Physicalcount = (RadioButton)findViewById(R.id.areascan);


        Storecode = (EditText) findViewById(R.id.storename);

        checkPermissions();
      //  Storecode.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)}|{new InputFilter.AllCaps});

     //   Storecode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        Ecode = (EditText)findViewById(R.id.ecode);
        checkPermissions();
        dbController = new DBController(this);

        actionBar = getSupportActionBar();
        //  actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
    //    actionBar.setIcon(R.drawable.ic_launchervishal);

        Stocktypearray = getResources().getStringArray(R.array.stocktype);
        adapterStocktypearray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,  Stocktypearray);
        adapterStocktypearray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Stocktype.setAdapter(adapterStocktypearray);
        Stocktype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StockSpinValue = Stocktype.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Areaarray = getResources().getStringArray(R.array.areatype);
        adapterareaarray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,  Areaarray);
        adapterareaarray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Area.setAdapter(adapterareaarray);
        Area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AreaSpinValue = Area.getSelectedItem().toString();
                if (AreaSpinValue.matches("SALES AREA"))
                {
                    salesarea = "SA";
                }
                else if (AreaSpinValue.matches("DC AREA"))
                {
                    salesarea = "DCA";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Floorarray = getResources().getStringArray(R.array.floortype);
        adapterfloorarray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,  Floorarray);
        adapterfloorarray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Floor.setAdapter(adapterfloorarray);
        Floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FloorSpinvalue = Floor.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Olddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recieved = dbController.getScanData();

                if (recieved.size() == 0) {

                    Toast.makeText(MainActivity.this, "No Old Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(),OldCsvData.class);
                startActivity(intent);
            }
        });



        Next.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (Storecode.getText().toString().matches("") || StockSpinValue.matches("Please Select")|| AreaSpinValue.matches("Please Select")|| FloorSpinvalue.matches("Please Select")|| Ecode.getText().toString().matches(""))
                {
                    Toast.makeText(MainActivity.this, "Please Fill Mandatory Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("Storecode",Storecode.getText().toString());
                bundle.putString("Stocktype",StockSpinValue);
                bundle.putString("Areatype",salesarea);
                bundle.putString("Floortype",FloorSpinvalue);
                bundle.putString("Ecode",Ecode.getText().toString());




                if (Scanning.isChecked())
                {

                    clearfield();

                    Intent intent = new Intent(getApplicationContext(),ScanActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                else if (Physicalcount.isChecked())
                {

                    Intent intent = new Intent(getApplicationContext(),PhysicalcountActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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





    public  void clearfield()
    {
        dbController =  new DBController(this);
        SQLiteDatabase db = dbController.getWritableDatabase();
        String tableName = "retail_physical_scanning";
        String tableName1 = "retail_store";

        db.execSQL("delete from " + tableName);
        db.execSQL("delete from " + tableName1);
        Log.e("Data","Deleted");
        //  db.execSQL("delete from " + tableName2);

    }





    public void Exitbuttondialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

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

                  finish();

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


    private void checkPermissions(){
        PermissionsUtil.askPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsUtil.PERMISSION_ALL: {

                if (grantResults.length > 0) {

                    List<Integer> indexesOfPermissionsNeededToShow = new ArrayList<>();

                    for(int i = 0; i < permissions.length; ++i) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            indexesOfPermissionsNeededToShow.add(i);
                        }
                    }

                    int size = indexesOfPermissionsNeededToShow.size();
                    if(size != 0) {
                        int i = 0;
                        boolean isPermissionGranted = true;

                        while(i < size && isPermissionGranted) {
                            isPermissionGranted = grantResults[indexesOfPermissionsNeededToShow.get(i)]
                                    == PackageManager.PERMISSION_GRANTED;
                            i++;
                        }

                        if(!isPermissionGranted) {

                            showDialogNotCancelable("Permissions mandatory",
                                    "All the permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            checkPermissions();
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void showDialogNotCancelable(String title, String message,
                                         DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setCancelable(false)
                .create()
                .show();
    }




}
