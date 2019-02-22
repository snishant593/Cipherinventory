package com.vishalcilpher.nishant.vishalcipher;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;
import com.cipherlab.barcode.decoder.BcReaderType;
import com.cipherlab.barcode.decoder.ClResult;
import com.cipherlab.barcode.decoder.Enable_State;
import com.cipherlab.barcode.decoder.KeyboardEmulationType;
import com.cipherlab.barcode.decoder.OutputEnterChar;
import com.cipherlab.barcode.decoder.OutputEnterWay;
import com.cipherlab.barcode.decoder.TriggerType;
import com.cipherlab.barcode.decoderparams.Code39;
import com.cipherlab.barcode.decoderparams.ReaderOutputConfiguration;
import com.cipherlab.barcode.decoderparams.UserPreference;
import com.cipherlab.barcodebase.ReaderCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScanActivity extends AppCompatActivity implements ReaderCallback {

    private Button b1 ;
    private EditText e1;


    EditText GandolaNo,Manual;
    AutoCompleteTextView Barcode;
    TextView Totalqty,Timeqty,Qty,totalcount,gandolatextview;
    Button Exit,Edit,Save,Enter,Manualbut;
    String Storecode,Stocktype,Areatype,Floortype,userTypedString,Ecode;

    DBController dbController;
    int count = 1;
    Boolean check;
    ArrayList<String> duplicatebarcode;
    ArrayList<String>quantity;

    ArrayList<String>duplicatebarcodedialog;
    ArrayList<String>received;
    android.support.v7.app.ActionBar actionBar;
    int countbarcode = 0;
    Bundle bundle = new Bundle();
    LinearLayout gandolaedit,gandolatext,scanlayout,scantextlayout,countlayout,manualbuttonlayout;
    private String STORECODE;
    ArrayList<String>  export = new ArrayList<>();


    String BarcodeCode;

    // Create an IntentFilter to get intents which we want
    private IntentFilter filter;

    // ReaderManager is using to communicate with Barcode Reader Service
    private ReaderManager mReaderManager;

    private Thread mMyThread2 = null;
    private boolean mIsRunning = false;
    private int mDecodeCount = 0;
    private ReaderCallback mReaderCallback = null;
    ArrayList<String> recieved = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        dbController = new DBController(this);
        totalcount = (TextView)findViewById(R.id.totalcount) ;

        GandolaNo = (EditText)findViewById(R.id.gandolano);
        Qty = (TextView)findViewById(R.id.qty);
        Barcode = (AutoCompleteTextView)findViewById(R.id.barcodeno);
        Exit = (Button)findViewById(R.id.exit);
        Edit = (Button)findViewById(R.id.edit);
        Save = (Button)findViewById(R.id.next);
        // Totalqty = (TextView)findViewById(R.id.totalqty);
        Timeqty = (TextView)findViewById(R.id.timetotal);
        gandolaedit = (LinearLayout)findViewById(R.id.gandolalayout) ;
        gandolatext = (LinearLayout)findViewById(R.id.gandolatext);
        Enter = (Button)findViewById(R.id.enter);
        gandolatextview = (TextView) findViewById(R.id.gandolanotext);
        scanlayout = (LinearLayout) findViewById(R.id.scanlayout);
        scantextlayout = (LinearLayout)findViewById(R.id.scantextlayout);
        countlayout = (LinearLayout)findViewById(R.id.countlayout);
        Manualbut = (Button)findViewById(R.id.manualeditbutton);
        manualbuttonlayout = (LinearLayout)findViewById(R.id.Manualeditbuttonlayout) ;




        //   ExeSampleCode();




        Bundle bundle1 = getIntent().getExtras();
        Storecode = bundle1.getString("Storecode");
        Stocktype = bundle1.getString("Stocktype");
        Areatype = bundle1.getString("Areatype");
        Floortype = bundle1.getString("Floortype");
        Ecode = bundle1.getString("Ecode");

        STORECODE =Storecode.toUpperCase();
        Log.e("Value",STORECODE +" " +Stocktype + " " + Areatype
                + " " + Floortype + "" + Ecode);



        mReaderManager = ReaderManager.InitInstance(this);
        mReaderCallback = this;

        // ***************************************************//
        // Register an IntentFilter
        // Add GeneralString.Intent_SOFTTRIGGER_DATA for software trigger
        // Add GeneralString.Intent_PASS_TO_APP for getting decoded data after disabling Keyboard Emulation
        // Add GeneralString.Intent_READERSERVICE_CONNECTED for knowing apk is connected with Barcode Reader Service
        // ***************************************************//
        filter = new IntentFilter();
        filter.addAction(GeneralString.Intent_SOFTTRIGGER_DATA);
        filter.addAction(GeneralString.Intent_PASS_TO_APP);
        filter.addAction(GeneralString.Intent_READERSERVICE_CONNECTED);
        registerReceiver(myDataReceiver, filter);


        Timeqty.setText(Time());
       // Barcode.setText("");



        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GandolaNo.getText().toString().matches(""))
                {
                    Toast.makeText(ScanActivity.this, "Please Enter Gandola No", Toast.LENGTH_SHORT).show();
                    return;
                }


                gandolatextview.setText(GandolaNo.getText().toString());
                gandolatext.setVisibility(View.VISIBLE);
                gandolaedit.setVisibility(View.GONE);
                Barcode.setText("");
                scanlayout.setVisibility(View.VISIBLE);
                scantextlayout.setVisibility(View.VISIBLE);
                countlayout.setVisibility(View.VISIBLE);
                manualbuttonlayout.setVisibility(View.VISIBLE);

            }
        });


        Manualbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (Barcode.getText().toString().matches(""))
                {
                    Toast.makeText(ScanActivity.this, "Please Add Hu", Toast.LENGTH_SHORT).show();
                    return;
                }
                SaveManualBarcodedialog();
            }
        });




        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recieved = dbController.getScanData();
//            gandolamodels = dbhelper.getalldatainvoice();
//            STORECODE = gandola.get(0).getStoreName();
//            Gandolacode = gandola.get(0).getGandolano();
//            Stocktype = gandola.get(0).getStocktype();
                if (recieved.size() == 0) {

                    Toast.makeText(ScanActivity.this, "No Barcode Data To Update", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("Gandolacode",GandolaNo.getText().toString());
                Intent intent  =  new Intent(getApplicationContext(),DialogActivity.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });




        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export = dbController.getScanData();
                if (export.size() == 0)
                {
                    Toast.makeText(ScanActivity.this, "No Data to Export", Toast.LENGTH_SHORT).show();
                    return;
                }

                Savebuttondialog();

            }
        });

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exitbuttondialog();
            }
        });



    }


    public void SaveManualBarcodedialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Save!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you Sure you want to Save Manual Barcode");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                  databaseoperation(Barcode.getText().toString());
                  Barcode.setText("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Barcode.setText("");

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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

                    clearfield();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    @Override
    public void onBackPressed() {
        Exitbuttondialog();
    }

    public void Savebuttondialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("You Can't Do the changes once the file is saved");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to save it");

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
                    GandolaNo.setText("");
                    totalcount.setText("");
                    gandolatextview.setText("");
                    Barcode.setText("");
                    gandolaedit.setVisibility(View.VISIBLE);
                    gandolatext.setVisibility(View.INVISIBLE);
                    countlayout.setVisibility(View.INVISIBLE);
                    scanlayout.setVisibility(View.INVISIBLE);
                    scantextlayout.setVisibility(View.INVISIBLE);
                    manualbuttonlayout.setVisibility(View.INVISIBLE);






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



    public class Exportgandolascanning extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(ScanActivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;
        String csvname = GandolaNo.getText().toString();

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Csv Files");
            this.dialog.show();
            dbhelper = new DBController(ScanActivity.this);
        }

        protected Boolean doInBackground(final String... args) {
            recieved = dbhelper.getScanData();
            if (recieved.size() == 0) {
                Log.e("recieved", "" + recieved);
                return false;
            } else {
                File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalPi/");
                Log.e("Nishant", exportDir.getAbsolutePath());
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, STORECODE.concat("_").concat("S").concat("_").concat(csvname).concat("_").concat(Stocktype).concat(datetime().concat(".csv")));
                try {

                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = dbhelper.getScanninggandola();
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
                Toast.makeText(ScanActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();

                clearfield();
            } else {
                Toast.makeText(ScanActivity.this, "No Gandola file Recieved", Toast.LENGTH_SHORT).show();
            }
        }
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

    public String datetime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        Format formatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss");

        //  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = formatter.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }





//
//    public void showChangeLangDialog() {
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final AutoCompleteTextView edt = (AutoCompleteTextView) dialogView.findViewById(R.id.barcodedialog);
//        final EditText edt1 = (EditText) dialogView.findViewById(R.id.Qtydialog);
//
//
//                try {
//
//
//
//                        edt.setText(BarcodeCode);
//
//
//
//                        duplicatebarcodedialog = dbController.gethualreadyscanned(BarcodeCode);
//                        if (duplicatebarcodedialog.size() >= 1) {
//
//
//                            edt1.setText(duplicatebarcodedialog.get(0));
//                        }
//
//
//
//                } catch (IndexOutOfBoundsException ex) {
//                    ex.printStackTrace();
//                }
//
//
//        dialogBuilder.setTitle("Update Or Delete Previous Barcode");
//        dialogBuilder.setMessage("Please Scan Barcode below");
//        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                dbController.updateExistinghustatus(BarcodeCode,edt1.getText().toString());
//                quantity =    dbController.getQuantity(GandolaNo.getText().toString());
//
//                totalcount.setText(quantity.get(0));
//
//                // String newqty = String.valueOf(Integer.parseInt(duplicatebarcodedialog.get(0)) + 1);
//                Toast.makeText(ScanActivity.this, " Quantity Updated", Toast.LENGTH_SHORT).show();
//
//
//            }
//        });
//        dialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//                dbController.removeSingleContact(BarcodeCode);
//                quantity =    dbController.getQuantity(GandolaNo.getText().toString());
//
//                totalcount.setText(quantity.get(0));
//
//                Toast.makeText(ScanActivity.this, "Barcode Deleted", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        AlertDialog b = dialogBuilder.create();
//        b.show();
//    }




    public  void  databaseoperation(String Barcodeno)
    {

        try
        {

        Qty.setText("1");
        duplicatebarcode = dbController.gethualreadyscanned(Barcodeno);
        if (duplicatebarcode.size() >= 1)
        {
            // Qty.setText(duplicatebarcode.get(0));
            String newqty = String.valueOf(Integer.parseInt(duplicatebarcode.get(0)) + 1);
            // String newcount = String.valueOf(Integer.parseInt(totalcount.getText().toString()) + 1);

            Toast.makeText(ScanActivity.this, newqty, Toast.LENGTH_SHORT).show();

            dbController.updateExistinghustatus(Barcodeno,newqty);
          //  Barcodeno.setText("");
            quantity =    dbController.getQuantity(GandolaNo.getText().toString());
            //   countbarcode++;
            totalcount.setText(quantity.get(0));

        } else {
            dbController.Insertgateentry(STORECODE, Stocktype, Ecode, Areatype,Floortype, GandolaNo.getText().toString(),Barcodeno, Qty.getText().toString(), Timeqty.getText().toString());
            dbController.Insertstorevalue(STORECODE, Stocktype, Ecode, Areatype,Floortype, GandolaNo.getText().toString());

            quantity =    dbController.getQuantity(GandolaNo.getText().toString());
            // String newcount = String.valueOf(Integer.parseInt(totalcount.getText().toString()) + 1);

           // Barcode.setText("");
            //  countbarcode++;
            totalcount.setText(quantity.get(0));
        }


        // Totalqty.setText("1");

} catch (IndexOutOfBoundsException ex) {
        ex.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
        // ***************************************************//
        // Unregister Broadcast Receiver before app close
        // ***************************************************//
        unregisterReceiver(myDataReceiver);

        // ***************************************************//
        // release(unbind) before app close
        // ***************************************************//
        if (mReaderManager != null)
        {
            mReaderManager.Release();
        }
    }


    /// Here is the all API examples
    private void ExeSampleCode()
    {



        // ***************************************************//
        // 5. get/set UserPreference
        //    For example, get/set scan duration time�Bsecurity level�Bredundancy level�Ketc
        // ***************************************************//
        {
            if (mReaderManager != null)
            {
                BcReaderType myReaderType =  mReaderManager.GetReaderType();

                // step1: new a class, the object is set to default value
                UserPreference settings = new UserPreference();

                // step2: this action does mean get current settings of UserPreference
                mReaderManager.Get_UserPreferences(settings);

                // step3: items are not supported exactly, so user can check...
                if (Enable_State.NotSupport == settings.displayMode)
                {
                    //1D does not support
                }

                //settings.addonSecurityLevel = 7;
                settings.laserOnTime = 3000;
                //settings.negativeBarcodes = InverseType.AutoDetect;
                //settings.scanAngle = ScanAngleType.Wide;
                //settings.securityLevel = SecurityLevel.Three;
                //settings.pickListMode = Enable_State.FALSE;
                //settings.timeoutBetweenSameSymbol = 2000;
                //settings.displayMode = Enable_State.FALSE;
                //settings.redundancyLevel = RedundancyLevel.Four;
                //settings.transmitCodeIdChar = TransmitCodeIDType.AimCodeId;
                //settings.triggerMode = TriggerType.ContinuousMode;
                //settings.triggerMode = TriggerType.AutoAimMode;
                //settings.triggerMode = TriggerType.LevelMode;

                // Change to Trigger Presentation Mode
                settings.triggerMode = TriggerType.PresentationMode;
                settings.timeoutPresentationMode = 10 * 60 * 1000; // ms
                settings.triggerPresentationMode = Enable_State.TRUE;

                // Change to Level Mode
                settings.triggerMode = TriggerType.LevelMode;
                settings.triggerPresentationMode = Enable_State.FALSE;

                //settings.interCharGapSize = InterCharacterGapSize.Normal;
                //settings.decodingAimingPattern = Enable_State.TRUE;
                //settings.decodingIllumination  = Enable_State.TRUE;
                //settings.decodingIlluminationPowerLevel = IlluminationPowerLevel.Zero;


                // step4
                // Set settings and check retrun value, if user get ClResult.S_ERR, it means failed,
                // if user get Err_InvalidParameter, it means user put wrong value into items
                // if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
                // if user get S_OK, it means set settings is successful.
                ClResult clRet = mReaderManager.Set_UserPreferences(settings);
                if (ClResult.S_ERR == clRet)
                    Toast.makeText(this, "Get_UserPreferences was failed", Toast.LENGTH_SHORT).show();
                else if (ClResult.Err_InvalidParameter == clRet)
                    Toast.makeText(this, "Get_UserPreferences was InvalidParameter",	Toast.LENGTH_SHORT).show();
                else if (ClResult.Err_NotSupport == clRet)
                    Toast.makeText(this, "Get_UserPreferences was NotSupport", Toast.LENGTH_SHORT).show();
                else if (ClResult.S_OK == clRet)
                    Toast.makeText(this, "Get_UserPreferences was successful", Toast.LENGTH_SHORT).show();
            }
        }


        // ***************************************************//
        //  7-6. get/set Code 39(same usage as above)
        // ***************************************************//
        {
            if (mReaderManager != null)
            {

                // step1: new a class, the object is set to default value
                Code39 settings = new Code39();

                // step2: to check does barcode scanner support this symbology
                if (ClResult.Err_NotSupport == mReaderManager.Get_Symbology(settings))
                {
                    // barcode scanner of device does not support this kind of symbology
                    return;
                }

                // step3: if barcode scanner support this symbology�Athen user can change attribute
                settings.enable = Enable_State.TRUE;
                settings.fullASCII = Enable_State.TRUE;
                settings.checkDigitVerification = Enable_State.FALSE;
                settings.transmitCheckDigit = Enable_State.FALSE;
                settings.convertToCode32 = Enable_State.FALSE;
                settings.convertToCode32Prefix = Enable_State.FALSE;

                // step4
                // Set settings and check retrun value, if user get ClResult.S_ERR, it means failed,
                // if user get Err_InvalidParameter, it means user put wrong value into items
                // if user get Err_NotSupport, it means the barcode reader does not support this kind of settings
                // if user get S_OK, it means set settings is successful.
                ClResult clRet = mReaderManager.Set_Symbology(settings);
                if (ClResult.S_ERR == clRet)
                    Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was failed", Toast.LENGTH_SHORT).show();
                else if (ClResult.Err_InvalidParameter == clRet)
                    Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was InvalidParameter",	Toast.LENGTH_SHORT).show();
                else if (ClResult.Err_NotSupport == clRet)
                    Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was NotSupport", Toast.LENGTH_SHORT).show();
                else if (ClResult.S_OK == clRet)
                    Toast.makeText(this, "Set_Symbology " + settings.getClass().getSimpleName() + " was successful", Toast.LENGTH_SHORT).show();
            }
        }




    }



    /// create a BroadcastReceiver for receiving intents from barcode reader service
    private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Software trigger must receive this intent message
            if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);

                // show decoded data
                Barcode.setText(BarcodeCode);
                databaseoperation(BarcodeCode);
            } else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)) {
                // If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);


                // show decoded data
                mDecodeCount++;
                Barcode.setText(BarcodeCode);
                databaseoperation(Barcode.getText().toString());
              //  e1.setText("[" + mDecodeCount + "]   " + BarcodeCode);

            } else if (intent.getAction().equals(GeneralString.Intent_READERSERVICE_CONNECTED)) {
                // Make sure this app bind to barcode reader service , then user can use APIs to get/set settings from barcode reader service

                BcReaderType myReaderType = mReaderManager.GetReaderType();
//                e1.setText(myReaderType.toString());
                Log.e("Done",myReaderType.toString());


                ReaderOutputConfiguration settings = new ReaderOutputConfiguration();
                mReaderManager.Get_ReaderOutputConfiguration(settings);
                settings.enableKeyboardEmulation = KeyboardEmulationType.None;
                settings.autoEnterWay = OutputEnterWay.Disable;
                settings.autoEnterChar = OutputEnterChar.None;
                settings.showCodeLen = Enable_State.FALSE;
                settings.showCodeType = Enable_State.FALSE;
              //  settings.szPrefixCode = "";
              //  settings.szSuffixCode = "";
               // settings.useDelim = ':';

                mReaderManager.Set_ReaderOutputConfiguration(settings);

                mReaderManager.SetActive(true);


            }

        }
    };


    public class SoftScanTriggerRunnable implements Runnable {
        @Override
        public void run() {
            int iSleepTime = 3000;

            while (mIsRunning) {
                try {
                    mReaderManager.SoftScanTrigger();

                    Thread.sleep(iSleepTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public IBinder asBinder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDecodeComplete(String arg0) throws RemoteException {
        // TODO Auto-generated method stub
        //e1.setText(arg0);
        Toast.makeText(this, "Decode Data " + arg0, Toast.LENGTH_SHORT).show();
    }



}
