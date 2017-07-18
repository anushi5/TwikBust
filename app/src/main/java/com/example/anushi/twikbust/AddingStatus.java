package com.example.anushi.twikbust;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddingStatus extends AppCompatActivity {


    //ListView lvstatus;
    //ArrayList<LoadingImage> loadarray=new ArrayList<LoadingImage>();
    ///MycustomAdapter myadapter;
    ImageView bupost;
    ImageView buimage;
    EditText edpost;
    ImageView ivmypostimage;
    String text;
    String tweets;

    ImageView ivuserimage;
    TextView tvpostusername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_status);
        /*loadarray.add(new LoadingImage(1));
        myadapter=new MycustomAdapter(loadarray);
        lvstatus=(ListView) findViewById(R.id.lvstatus);
        lvstatus.setAdapter(myadapter);*/


        Bundle b=getIntent().getExtras();
        String user_name=b.getString("user_name");
        String dp_picture_path=b.getString("dp_picture_path");
        //// new code
        bupost=(ImageView) findViewById(R.id.iv_post);
        buimage=(ImageView) findViewById(R.id.iv_attach);
        edpost=(EditText)findViewById(R.id.etPost);
        ivmypostimage=(ImageView)findViewById(R.id.ivmypostimage) ;
        tvpostusername=(TextView) findViewById(R.id.tvpost);
        tvpostusername.setText(user_name);
        Toast.makeText(getApplicationContext(),user_name,Toast.LENGTH_SHORT).show();
        ivuserimage=(ImageView)findViewById(R.id.ivuserimage);
        Picasso.with(getApplicationContext()).load(dp_picture_path).into(ivuserimage);

        buimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tweets = java.net.URLEncoder.encode(  edpost.getText().toString() , "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                CheckUserPermsions();

            }
        });

        bupost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text=edpost.getText().toString();
                if(!text.matches(""))
                {
                     tweets=null;
                    try {
                        //for space with name
                        tweets = java.net.URLEncoder.encode(  edpost.getText().toString() , "UTF-8");
                        if(downloadUrl!=null)
                        {
                            downloadUrl= java.net.URLEncoder.encode(downloadUrl , "UTF-8");
                        }
                    } catch (UnsupportedEncodingException e) {
                        tweets=".";
                    }
                    Toast.makeText(getApplicationContext(),tweets,Toast.LENGTH_SHORT).show();
                    String url="https://tusharsk26.000webhostapp.com/TwikBust/AddTweets.php?user_id="+ SaveSettings.UserID +"&tweet_text="+ tweets +"&tweet_picture="+ downloadUrl;
                    new AddingStatus.MyAsyncTaskgetNews().execute(url);

                    edpost.setText("");


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"enter the tweet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /// checking permission

    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        //loadarray.add(new LoadingImage(2));
        //myadapter.notifyDataSetChanged();
        LoadImage();// init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImage();// init the contact list
                    //loadarray.add(new LoadingImage(2));
                    //myadapter.notifyDataSetChanged();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    int RESULT_LOAD_IMAGE=34;
    void LoadImage(){
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
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmap=BitmapFactory.decodeFile(picturePath);
            ivmypostimage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            uploadimage( BitmapFactory.decodeFile(picturePath));

            //ivUserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

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

    Bitmap bitmap=null;
    String downloadUrl=null;
    // ImageView postImage = new ImageView(this);
    public void uploadimage(Bitmap bitmap ) {
        showProgressDialog();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://twikbust2.appspot.com");
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmmss");
        Date dateobj = new Date();
        // System.out.println(df.format(dateobj));
// Create a reference to "mountains.jpg"
        String mydownloadUrl=SaveSettings.UserID+ "_"+ df.format(dateobj) +".jpg";
        StorageReference mountainsRef = storageRef.child("images/"+ mydownloadUrl);
        // postImage.setDrawingCacheEnabled(true);
        // postImage.buildDrawingCache();
        // Bitmap bitmap = imageView.getDrawingCache();
        // BitmapDrawable drawable=(BitmapDrawable)postImage.getDrawable();
        //  Bitmap bitmap =drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") String downloadUr = taskSnapshot.getDownloadUrl().toString();
                downloadUrl=downloadUr;
                hideProgressDialog();
            }
        });
    }










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

                if (json.getString("msg").equalsIgnoreCase("tweet is added")) {
                   Toast.makeText(getApplicationContext(),"tweet is added",Toast.LENGTH_SHORT).show();
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
