package com.example.win10.try_chat_json_expiriment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ChatRoom extends AppCompatActivity {

    public static final String WELCOME = "Welcome to CHAT ROOM";
    private static final String LOADING = "Loading...";
    TextView lblWelcomeChatroom;
    Spinner spinner;
    ListView lstMessages;
    EditText txtMessage;
    Button btnSend;
    private String message, userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        lblWelcomeChatroom = findViewById(R.id.lblWelcomeChatroom);
        spinner = findViewById(R.id.spnrContact);
        lstMessages = findViewById(R.id.lstMessages);
        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);
        userName = getIntent().getStringExtra("username");
        Toast.makeText(this, "Welcome " + userName, Toast.LENGTH_SHORT).show();
    }

    public void enableUI(boolean enable) {
        if (enable) {
            lblWelcomeChatroom.setText(WELCOME);
            txtMessage.setText("");
        } else {
            lblWelcomeChatroom.setText(LOADING);
        }
        txtMessage.setEnabled(enable);
        btnSend.setEnabled(enable);
    }

    public void send(View view) {
        enableUI(false);
        this.message = txtMessage.getText().toString();
        if (message.isEmpty()) {
            enableUI(true);
            Toast.makeText(this, "must write somthing down", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<String, Void, String>(){
            String message = "", userName = "";

            @Override
            protected String doInBackground(String... strings) {
                message = strings[0];
                userName = strings[1];

                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("username", userName);
                    jsonMessage.put("message", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                URL url = null;
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    url = new URL(MainActivity.BASE_URL);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.connect();

                    outputStream = connection.getOutputStream();
                    outputStream.write(jsonMessage.toString().getBytes());
                    outputStream.close();
                    outputStream = null;

                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    StringBuilder sb = new StringBuilder();
                    int actuallyRead;

                    while ((actuallyRead = inputStream.read(buffer)) != -1)
                        sb.append(new String(buffer, 0, actuallyRead));

                    return sb.toString();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                            inputStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                        connection = null;
                    }
                    Log.d("Mike", jsonMessage.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(String message) {
                enableUI(true);
                Toast.makeText(ChatRoom.this, message, Toast.LENGTH_SHORT).show();

            }
        }.execute(this.message, userName);
    }
}
