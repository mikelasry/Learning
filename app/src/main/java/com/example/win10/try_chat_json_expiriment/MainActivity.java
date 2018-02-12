package com.example.win10.try_chat_json_expiriment;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String LOADING = "Loading..";
    public static final String WELCOME = "Welcome";
    public static final String BASE_URL = "http://10.0.2.2:8080/MainServlet";
    TextView lblWelcome;
    EditText txtUsername, txtPassword;
    Button btnLogin, btnSignup;
    public String username, password, action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblWelcome = findViewById(R.id.lblWelcomeScreen);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
    }

    public void enableUI(boolean enable){
        if (enable){
            lblWelcome.setText(WELCOME);
            txtUsername.setText("");
            txtPassword.setText("");
        } else {
            lblWelcome.setText(LOADING);
        }
        txtUsername.setEnabled(enable);
        txtPassword.setEnabled(enable);
        btnLogin.setEnabled(enable);
        btnSignup.setEnabled(enable);
    }

    public void start(View view) {
        enableUI(false);
        action = Integer.valueOf(view.getTag().toString()).equals(1) ? "login" : "signup" ;
        username = txtUsername.getText().toString().trim(); //toLowerCase
        password = txtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()){
            enableUI(true);
            Toast.makeText(this, "must right somthing down ", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {

                URL url = null;
                HttpURLConnection connection = null;
                InputStream inputStream = null;

                try {
                    url = new URL(BASE_URL + "?action=" + action +
                                                  "&username=" + username +
                                                  "&password=" + password );

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET"); //GET by default;
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.connect();

                    inputStream = connection.getInputStream();
                    byte[] buffer = new byte[64];
                    int actuallyRead = inputStream.read(buffer);

                    String response =  new String(buffer, 0, actuallyRead);
                    if (response.equals("ok")){
                        Intent intentChatRoom =
                                new Intent(MainActivity.this, ChatRoom.class);
                        intentChatRoom.putExtra("username", username);
                        intentChatRoom.putExtra("password", password);
                        startActivity(intentChatRoom);
                        return "success";
                    }else{
                        return response;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (connection != null)
                        connection.disconnect();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                enableUI(true);
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }.execute();


    }
}
