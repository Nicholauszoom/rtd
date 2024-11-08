package com.example.transactionsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity2 extends AppCompatActivity {

    EditText etPhone, etMsg;
    Button btnSendSMS, btnReadSMS;

    ListView lvSMS;
    ArrayList<String> smsData = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    private final int REQ_CODE_PERMISSION_SEND_SMS= 121;
    private final int REQ_CODE_PERMISSION_READ_SMS= 122;
    private final int REQ_CODE_PERMISSION_RECEIVE_SMS= 123;

    private final String SERVER ="https://solutionscode.000webhostapp.com/save_sms0.php";

    private String extractReceivedAmount(String msg) {
        // Regular expression to find "umepokea" or "Umepokea" followed by an amount (e.g., Tsh17,000.00)
        Pattern pattern = Pattern.compile("(umepokea|Umepokea)\\s*Tsh\\s*([\\d,]+\\.\\d{2})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            // Get the matched amount (in the second group)
            return matcher.group(2).replace(",", ""); // Remove commas for numeric representation
        }
        return null; // Return null if no match found
    }

    //capture transaction from name
    private static String extractTransactionFromName(String msg) {
        // Regex to match "kutoka" or "kutoka kwa Wakala" and capture everything up to "Salio" or "salio"
        Pattern pattern = Pattern.compile("(kutoka(?: kwa Wakala)?)(.*?)(?=Salio|salio)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            return matcher.group(2).trim(); // Capture everything between "kutoka" and "Salio", trimming any extra whitespace
        }
        return null; // Return null if no match is found
    }


    private static String extractTransationToName(String msg) {

        Pattern pattern = Pattern.compile("(kwenda(?: Kwenda)?)(.*?)(?=Makato|makato)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null; // Return null if no match is found
    }

    private static String extractTransactionId(String msg) {
        // Regex to match "Muamala No", "Muamala No ", or "TID" and capture everything until the end of the message
        Pattern pattern = Pattern.compile("(?:Muamala No:?\\s*|TID:?\\s*)(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            // Get the matched transaction ID (in the first capturing group)
            return matcher.group(1).trim(); // Trim any extra whitespace
        }
        return null; // Return null if no match found
    }

    private static String extractTransactionType(String msg) {
        if (msg.toLowerCase().contains("umepokea")) {
            return "DEPOSIT";
        } else if (msg.toLowerCase().contains("umelipa")) {
            return "WITHDRAW";
        }
        return "UNKNOWN"; // Return "UNKNOWN" if neither keyword is found
    }



    // Method to extract remaining balance after "Salio"
    // Method to extract remaining balance after "Salio" with optional spaces around value and "Tsh"
    private String extractRemainingBalance(String msg) {
        // Regular expression to match "Salio" with optional spaces around the amount and currency
        Pattern pattern = Pattern.compile("(salio|Salio|Salio jipya)\\s*(Tsh)?\\s*([\\d,]+\\.\\d{2})\\s*(Tsh)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            // Retrieve and clean the balance amount (removing commas)
            return matcher.group(3).replace(",", "");
        }
        return null; // Return null if no match found
    }

    private String extractWithdrawAmount(String msg) {
        // Regular expression to match "Umelipa" with optional spaces around the amount and currency
        Pattern pattern = Pattern.compile("(Umelipa|umelipa)\\s*(Tsh)?\\s*([\\d,]+\\.\\d{2})\\s*(Tsh)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            // Retrieve and clean the balance amount (removing commas)
            return matcher.group(3).replace(",", "");
        }
        return null; // Return null if no match found
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etPhone= findViewById(R.id.etPhoneNum);
        etMsg =findViewById(R.id.etMsg);

        btnReadSMS =findViewById(R.id.btnRead);
        btnSendSMS= findViewById(R.id.btnSend);

        lvSMS =findViewById(R.id.lvSMS);
        arrayAdapter =new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,smsData);
        lvSMS.setAdapter(arrayAdapter);

        btnReadSMS.setEnabled(false);
        btnSendSMS.setEnabled(false);

        /** CHECKING PERMISSION SEND SMS **/
        if (checkPermission(android.Manifest.permission.SEND_SMS)){
            btnSendSMS.setEnabled(true);
        }else {
            ActivityCompat.requestPermissions(MainActivity2.this,new String[]{android.Manifest.permission.SEND_SMS},
                    REQ_CODE_PERMISSION_SEND_SMS);
        }

        /** CHECKING PERMISSION READ SMS **/
        if (checkPermission(android.Manifest.permission.READ_SMS)){
            btnReadSMS.setEnabled(true);
        }else {
            ActivityCompat.requestPermissions(MainActivity2.this,new String[]{android.Manifest.permission.READ_SMS},
                    REQ_CODE_PERMISSION_READ_SMS);
        }

        /** CHECKING PERMISSION RECEIVE SMS **/
        if (checkPermission(android.Manifest.permission.RECEIVE_SMS)){

            ActivityCompat.requestPermissions(MainActivity2.this,new String[]{Manifest.permission.RECEIVE_SMS},
                    REQ_CODE_PERMISSION_RECEIVE_SMS);
        }

        /** CHECKING PERMISSION END **/



        btnReadSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver cr =getContentResolver();
                Cursor c = cr.query(Uri.parse("content://sms/inbox"), null, null, null,null);
                /**  get all column names **/

                StringBuffer info = new StringBuffer();

                for(int i=0; i<c.getColumnCount(); i++){
                    info.append("COLUMN_NAME: " + c.getColumnName(i) + "\n");
                }
                Toast.makeText(MainActivity2.this, info.toString(), Toast.LENGTH_LONG).show();

                int indexBody =c.getColumnIndex("body");
                int indexPhone = c.getColumnIndex("address");
                int indexDate =c.getColumnIndex("date");
                int indexDateSent =c.getColumnIndex("date_sent");

                if (indexBody < 0 || !c.moveToFirst()) return;
                arrayAdapter.clear();
                do {
                    String phone = c.getString(indexPhone);
                    String msg = c.getString(indexBody);
//                    String str = "SMS from:" + phone + "\n" + msg;
                    String str = "SMS from:" + phone;
                    String ddate = c.getString(indexDate);
                    String dateSent = c.getString(indexDateSent);

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    if ("AirtelMoney".equals(phone)) {
                        try {
                    Date thisDate = new Date(Long.parseLong(ddate));
                    ddate = df.format(thisDate);
                    thisDate = new Date(Long.parseLong(dateSent));
                    dateSent = df.format(thisDate);


                    String transactionType = extractTransactionType(msg);
                            if (transactionType != null) {
                                str += "\nTransaction Type: " + transactionType;
                            }

                    String transactionId = extractTransactionId(msg);
                            if (transactionId != null) {
                                str += "\nTransaction ID(TID): " + transactionId;
                            }

                    String transactionFromName = extractTransactionFromName(msg);
                            if (transactionFromName != null) {
                                str += "\nTransaction From: " + transactionFromName;
                            }

                    String transationSentName = extractTransationToName(msg);
                            if (transationSentName != null) {
                                str += "\nTransaction To: " + transationSentName;
                            }

                     String withdrawAmount = extractWithdrawAmount(msg);
                            if (withdrawAmount != null) {
                                str += "\nWithdraw Amount: " + withdrawAmount;
                            }

                    //add Received amount to the MainActivity2
                    String receivedAmount = extractReceivedAmount(msg);
                            if (receivedAmount != null) {
                                str += "\nDeposited Amount: " + receivedAmount;
                            }

                            // Extract and display Remain Balance
                            // Inside btnReadSMS click handler:
                    String remainingBalance = extractRemainingBalance(msg);
                            if (remainingBalance != null) {
                                str += "\nRemain Balance: " + remainingBalance;
                            }

                    str += "\nDate: " + ddate;

                    str += "\nDateSent: " + dateSent;

                            arrayAdapter.add(str);

                        } catch (NumberFormatException e) {
                            Log.e("ReadSMS", "Error parsing date: " + e.getMessage());
                        }
                    }
                    //TODO: Send sms to database
                }while(c.moveToNext());

            }
        });
    }

    private boolean checkPermission(String permisssion){
        int permissionCode = ContextCompat.checkSelfPermission(this,permisssion);
        return permissionCode == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION_READ_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnReadSMS.setEnabled(true);
                    btnSendSMS.setEnabled(true);
                }
                break;
        }
    }


}