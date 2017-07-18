package com.example.anushi.twikbust;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUp extends AppCompatActivity {

    EditText username,email,password;
    ImageView imageView;
    Button bfemale,bmale,blogin,bsignup;
    String downloadUrl=null;
    String s_username,s_email,s_password,s_gender="2",picturePath=null;
    int isImagePresent=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username=(EditText)findViewById(R.id.username);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        imageView=(ImageView)findViewById(R.id.ivsignupprofile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUserPermissions();
            }
        });
        bsignup=(Button)findViewById(R.id.signup) ;
        bfemale=(Button)findViewById(R.id.female);
        bmale=(Button)findViewById(R.id.male);
        blogin=(Button)findViewById(R.id.login);
        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bsignup=(Button)findViewById(R.id.signup);
    }


    public void boolfemale(View view) {
        s_gender="1";

        Toast.makeText(getApplicationContext(),"female",Toast.LENGTH_SHORT).show();
    }

    public void boolmale(View view) {
        s_gender="0";
        Toast.makeText(getApplicationContext(),"male",Toast.LENGTH_SHORT).show();
    }

    void CheckUserPermissions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        LoadImage();

    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImage();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText( this,"Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    int RESULT_LOAD_IMAGE=346;
    void LoadImage(){
        isImagePresent=1;
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //uploadimage(BitmapFactory.decodeFile(picturePath));

        }
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading.....");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }




    public void uploadimage(Bitmap bitmap) {

        FirebaseStorage storage=FirebaseStorage.getInstance();

        // Creating storage reference
          showProgressDialog();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://twikbust2.appspot.com");
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmmss");
        Date dateobj = new Date();

        String ImagePath=SaveSettings.UserID+ "_"+ df.format(dateobj) +".jpg";
        StorageReference mountainsRef = storageRef.child("images/"+ImagePath);
        // imageView.setDrawingCacheEnabled(true);
       // imageView.buildDrawingCache();


        //BitmapDrawable drawable=(BitmapDrawable)imageView.getDrawable();
        //bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                String errorMessage = exception.getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") String downloadUr = taskSnapshot.getDownloadUrl().toString();
                downloadUrl=downloadUr;
                isImagePresent=0;
                if(downloadUrl!=null)
                {
                    try {
                        downloadUrl= java.net.URLEncoder.encode(downloadUrl , "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                String url="https://tusharsk26.000webhostapp.com/TwikBust/Register.php?user_name="+s_username+"&email="+s_email+"&password="+s_password+"&gender="+s_gender+"&picture_path="+downloadUrl;
                new SignUp.MyAsyncTaskgetNews().execute(url);
                //Toast.makeText(getApplicationContext(),downloadUrl+"999",Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });


    }




    public void insertData(View view) {


        s_username=username.getText().toString();
        s_email=email.getText().toString();
        s_password=password.getText().toString();

        if(!s_gender.matches("2")&&!s_username.matches("")&&!s_password.matches("")&&!s_email.matches("")&&picturePath==null)
        {
            //Toast.makeText(getApplicationContext()," here now ",Toast.LENGTH_SHORT).show();
            showProgressDialog();
            if(downloadUrl!=null)
            {
                try {
                    downloadUrl= java.net.URLEncoder.encode(downloadUrl , "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String url="https://tusharsk26.000webhostapp.com/TwikBust/Register.php?user_name="+s_username+"&email="+s_email+"&password="+s_password+"&gender="+s_gender+"&picture_path="+downloadUrl;
            new SignUp.MyAsyncTaskgetNews().execute(url);
            bsignup.setEnabled(false);
         //   new SignUp.MyAsyncTask().execute();

        }
        else if(picturePath!=null)
        {
            //showProgressDialog();
            bsignup.setEnabled(false);

            uploadimage(BitmapFactory.decodeFile(picturePath));
        }
        else
        {
            Toast.makeText(getApplicationContext(),"ENTER ALL DETAILS",Toast.LENGTH_SHORT).show();
        }

    }

/*
    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }



        @Override
        protected void onPreExecute() {



        }
        @Override
        protected void onPostExecute(Void result) {

            //hideProgressDialog();
            Toast.makeText(getApplicationContext()," here now "+downloadUrl,Toast.LENGTH_SHORT).show();
            //String url="https://tusharsk26.000webhostapp.com/TwikBust/Register.php?user_name="+s_username+"&email="+s_email+"&password="+s_password+"&gender="+s_gender+"&picture_path="+downloadUrl;
            // new SignUp.MyAsyncTaskgetNews().execute(url);
        }
    }*/







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

                else if (json.getString("msg").equalsIgnoreCase("user is added")) {
                    hideProgressDialog();
                    //Toast.makeText(getApplicationContext(),"THANKS FOR SIGNING IN ",Toast.LENGTH_SHORT).show();

                    Intent i= new Intent(SignUp.this,Login.class);
                    startActivity(i);
                    finish();
                }

            } catch (Exception ex) {

            }
            //downloadUrl=null;
        }

        protected void onPostExecute(String  result2){


        }




    }

}