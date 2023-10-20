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
                    String str = "SMS from:" + phone + "\n" + msg;
                    String ddate = c.getString(indexDate);
                    String dateSent = c.getString(indexDateSent);

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date thisDate = new Date(Long.parseLong(ddate));
                    ddate = df.format(thisDate);
                    thisDate = new Date(Long.parseLong(dateSent));
                    dateSent = df.format(thisDate);

                    str += "\nDate: " + ddate;
                    str += "\nDateSent: " + dateSent;

                    arrayAdapter.add(str);

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