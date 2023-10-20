package com.example.transactionsms;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.transactionsms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> smsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        listView = findViewById(R.id.listView);
        smsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        listView.setAdapter(adapter);

        new FetchSmsTask().execute("https://solutionscode.000webhostapp.com/select_data.php");
    }

    private class FetchSmsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection connection = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();

                int statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    response = stringBuilder.toString();
                } else {
                    response = null;
                }
            } catch (IOException e) {
                Log.e("FetchSmsTask", "Error: " + e.getMessage());
                response = null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return response;
        }


        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    String[] rows = response.split("<br><br>");

                    for (String row : rows) {
                        String[] fields = row.split("<br>");
                        String phone = "";
                        String msg = "";
                        String ddate = "";
                        String dateSent = "";

                        for (String field : fields) {
                            String[] parts = field.split(": ");
                            if (parts.length == 2) {
                                String key = parts[0];
                                String value = parts[1];

                                if (key.equals("phone_num")) {
                                    phone = value;
                                } else if (key.equals("message")) {
                                    msg = value;
                                } else if (key.equals("record_stamp")) {
                                    ddate = value;
                                } else if (key.equals("date_sent")) {
                                    dateSent = value;
                                }
                            }
                        }

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date recordDate = df.parse(ddate);
                        ddate = df.format(recordDate);
                        Date sentDate = df.parse(dateSent);
                        dateSent = df.format(sentDate);

                        String str = "SMS from: " + phone + "\n" + msg;
                        str += "\nDate: " + ddate;
                        str += "\nDate Sent: " + dateSent;

                        smsList.add(str);
                    }

                    adapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    Log.e("FetchSmsTask", "Error parsing date: " + e.getMessage());
                    Toast.makeText(SmsActivity.this, "Error parsing date", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
