package com.vishalcilpher.nishant.vishalcipher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;
import com.cipherlab.barcode.decoder.BcReaderType;
import com.cipherlab.barcode.decoder.Enable_State;
import com.cipherlab.barcode.decoder.KeyboardEmulationType;
import com.cipherlab.barcode.decoder.OutputEnterChar;
import com.cipherlab.barcode.decoder.OutputEnterWay;
import com.cipherlab.barcode.decoderparams.ReaderOutputConfiguration;
import com.cipherlab.barcodebase.ReaderCallback;

import java.util.ArrayList;

public class DialogActivity extends Activity implements ReaderCallback {



    // Create an IntentFilter to get intents which we want
    private IntentFilter filter;

    // ReaderManager is using to communicate with Barcode Reader Service
    private ReaderManager mReaderManager;

    private Thread mMyThread2 = null;
    private boolean mIsRunning = false;
    private int mDecodeCount = 0;
    String BarcodeCode;
    private ReaderCallback mReaderCallback = null;
    ArrayList<String> duplicatebarcodedialog;
    DBController dbController;
    Button Exit,Update,Delete;
    ArrayList<String>quantity;




    EditText edt ;
    EditText edt1 ;
    String Gandola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        dbController = new DBController(this);


        edt = (EditText)findViewById(R.id.barcodedialog);
        edt1 = (EditText)findViewById(R.id.Qtydialog);
        Exit = (Button)findViewById(R.id.Exit);
        Update = (Button)findViewById(R.id.update);
        Delete = (Button)findViewById(R.id.Delete);

        Bundle bundle1 = getIntent().getExtras();
        Gandola = bundle1.getString("Gandolacode");

        Log.e("Gandola",Gandola);




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
        registerReceiver(DataReceiver, filter);

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (edt1.getText().toString().matches("")) {
                    Toast.makeText(DialogActivity.this, "Please Scan Barcode", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbController.removeSingleContact(BarcodeCode);
                quantity =    dbController.getQuantity(Gandola);

                Intent intent = new Intent(getApplicationContext(), ScanAfterDialog.class);
                startActivity(intent);



                Toast.makeText(DialogActivity.this, "Barcode Deleted", Toast.LENGTH_SHORT).show();


            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    if (edt1.getText().toString().matches("")) {
                        Toast.makeText(DialogActivity.this, "Please Scan Barcode", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbController.updateExistinghustatus(BarcodeCode, edt1.getText().toString());
                    quantity = dbController.getQuantity(Gandola);

                    Intent intent = new Intent(getApplicationContext(), ScanAfterDialog.class);
                    startActivity(intent);

                    //  totalcount.setText(quantity.get(0));

                    // String newqty = String.valueOf(Integer.parseInt(duplicatebarcodedialog.get(0)) + 1);
                    Toast.makeText(DialogActivity.this, " Quantity Updated", Toast.LENGTH_SHORT).show();


                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });


        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exitbuttondialog();
            }
        });


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


                    Intent intent = new Intent(getApplicationContext(),ScanAfterDialog.class);
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


    @Override
    public void onBackPressed() {

        Exitbuttondialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
        // ***************************************************//
        // Unregister Broadcast Receiver before app close
        // ***************************************************//
        unregisterReceiver(DataReceiver);

        // ***************************************************//
        // release(unbind) before app close
        // ***************************************************//
        if (mReaderManager != null)
        {
            mReaderManager.Release();
        }
    }


    public void Databaseoperation()
    {
        try {

            duplicatebarcodedialog = dbController.gethualreadyscanned(BarcodeCode);
            if (duplicatebarcodedialog.size() >= 1) {


                edt1.setText(duplicatebarcodedialog.get(0));
            }



        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }


    /// create a BroadcastReceiver for receiving intents from barcode reader service
    private final BroadcastReceiver DataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Software trigger must receive this intent message
            if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);

                // show decoded data
                edt.setText(BarcodeCode);
                Databaseoperation();
            } else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)) {
                // If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);


                // show decoded data
                mDecodeCount++;
                edt.setText(BarcodeCode);
                Databaseoperation();

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
