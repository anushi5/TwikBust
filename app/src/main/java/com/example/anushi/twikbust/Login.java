package com.example.anushi.twikbust;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;




public class Login extends AppCompatActivity {

    EditText etName;
    EditText etEmail;
    EditText etPassword;
    ImageView ivUserImage;

    String email;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        etEmail=(EditText)findViewById(R.id.et_email);
        etPassword=(EditText)findViewById(R.id.et_password);


    }




    // loading display

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void login(View view) {

        email=etEmail.getText().toString();
        password=etPassword.getText().toString();
        if(!email.matches("")&&!password.matches(""))
        {
            String url="https://tusharsk26.000webhostapp.com/TwikBust/login.php?email="+email+"&password="+password;
            new MyAsyncTaskgetNews().execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"PLEASE ENTER THE DETAILS",Toast.LENGTH_SHORT).show();
        }

    }

    public void register(View view) {
        Intent i= new Intent(Login.this,SignUp.class);
        startActivity(i);
        finish();
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();

    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();

    }




    // get news from server
    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String NewsData;
                //define the url we have to connect with
                URL url = new URL(params[0]);
                //make connect with url and send request
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //waiting for 7000ms for response
                urlConnection.setConnectTimeout(7000);//set timeout to 5 seconds

                try {
                    //getting the response data
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //convert the stream to string
                    Operations operations=new Operations(getApplicationContext());
                    NewsData = operations.ConvertInputToStringNoChange(in);
                    //send to display data
                    publishProgress(NewsData);
                } finally {
                    //end connection
                    urlConnection.disconnect();
                }

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {

            try {
                JSONObject json= new JSONObject(progress[0]);
                //display response data
                if (json.getString("msg")==null)
                    return;
                if (json.getString("msg").equalsIgnoreCase("Pass Login")) {
                    Toast.makeText(getApplicationContext(), json.getString("msg"), Toast.LENGTH_LONG).show();
                     //login

                    JSONArray UserInfo=new JSONArray( json.getString("info"));
                    JSONObject UserCreintal= UserInfo.getJSONObject(0);
                    //Toast.makeText(getApplicationContext(),UserCreintal.getString("user_id"),Toast.LENGTH_LONG).show();
                    hideProgressDialog();

                    /// adding data inside shared prefrences
                    SaveSettings saveSettings= new SaveSettings(getApplicationContext());
                    saveSettings.SaveData(UserCreintal.getString("user_id"),UserCreintal.getString("picture_path"),UserCreintal.getString("gender"),UserCreintal.getString("user_name"));
                    Intent i= new Intent(Login.this,NavigationBar.class);
                    startActivity(i);
                    finish();
                    //String url="https://tusharsk26.000webhostapp.com/TwikBust/login.php?email="+etEmail.getText().toString()+"&password="+etPassword.getText().toString() ;

                   // new MyAsyncTaskgetNews().execute(url);
                }

                if (json.getString("msg").equalsIgnoreCase("cannot login")) {



                    Toast.makeText(getApplicationContext(),"WRONG EMAIL OR PASSWORD",Toast.LENGTH_SHORT).show();
                    /*JSONArray UserInfo=new JSONArray( json.getString("info"));
                    JSONObject UserCreintal= UserInfo.getJSONObject(0);
                    //Toast.makeText(getApplicationContext(),UserCreintal.getString("user_id"),Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    SaveSettings saveSettings= new SaveSettings(getApplicationContext());
                    saveSettings.SaveData(UserCreintal.getString("user_id"));
                    finish(); //close this activity*/
                }

            } catch (Exception ex) {
                //Log.d("er",  ex.getMessage());
            }


        }

        protected void onPostExecute(String  result2){


        }




    }



}
