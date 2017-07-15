package com.example.anushi.twikbust;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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

import static com.example.anushi.twikbust.R.id.buFollow;

public class MainActivity extends AppCompatActivity {

    //adapter class
    ArrayList<AdapterItems> listnewsData = new ArrayList<AdapterItems>();
    int StartFrom = 0;
    int UserOperation = SearchType.MyFollowing; // 1 my followers post 2- specifc user post 3- search post
    String Searchquery;
    int totalItemCountVisible = 0; //totalItems visible
    LinearLayout ChannelInfo;
    TextView txtnamefollowers;
    int SelectedUserID = 0;
    Button buFollow;
    //MyCustomAdapter myadapter;

    DrawerLayout mdrawerlayout;
    ActionBarDrawerToggle mtoggle;
    Toolbar mtoolbar;
    int isFront=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);}}

       /* SaveSettings saveSettings = new SaveSettings(getApplicationContext());
        saveSettings.LoadData();
        setContentView(R.layout.activity_main);

        if(!saveSettings.UserPresent())
        {
            finish();
        }



        //mdrawerlayout=(DrawerLayout) findViewById(R.id.dl);
        //mtoggle = new ActionBarDrawerToggle(MainActivity.this, mdrawerlayout,R.string.open,R.string.close);

        //  mtoolbar = (Toolbar) findViewById (R.id.nav_action);
        // setSupportActionBar(mtoolbar);

        // mdrawerlayout.addDrawerListener(mtoggle);
        //mtoggle.syncState();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ChannelInfo = (LinearLayout) findViewById(R.id.ChannelInfo);
        ChannelInfo.setVisibility(View.GONE);
        txtnamefollowers = (TextView) findViewById(R.id.txtnamefollowers);
        buFollow = (Button) findViewById(R.id.buFollow);



        listnewsData.add(new AdapterItems(null,null,null,"add",null,saveSettings.user_name,saveSettings.dp_picture_path));

        myadapter = new MyCustomAdapter(this, listnewsData);
        ListView lsNews=(ListView)findViewById(R.id.LVNews);
        lsNews.setAdapter(myadapter);//intisal with data
        //LoadTweets(0,SearchType.MyFollowing);


    }
    public void addstatus(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AddingStatus.class);
        startActivity(intent);
    }


       /* public void buFollowers(View view) {
    //TODO: add code s=for subscribe and un subscribe


            int Operation; // 1- subsribe 2- unsubscribe
            String Follow=buFollow.getText().toString();
            if (Follow.equalsIgnoreCase("Follow")) {
                Operation = 1;
                buFollow.setText("Un Follow");
            }
            else {
                Operation = 2;
                buFollow.setText("Follow");
            }

            String url="https://tusharsk26.000webhostapp.com/TwikBust/UserFollow.php?op="+ Operation + "&user_id="+ SaveSettings.UserID +"&following_user_id="+SelectedUserID;
            new  MyAsyncTaskgetNews().execute(url);
        }
*/

    /*SearchView searchView;
    Menu myMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        myMenu = menu;
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchbar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //final Context co=this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast.makeText(co, query, Toast.LENGTH_LONG).show();
                Searchquery = null;
                try {
                    //for space with name
                    Searchquery = java.net.URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                //TODO: search in posts
                //LoadTweets(0,SearchType.SearchIn);// seearch
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //   searchView.setOnCloseListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (mtoggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.home:
                //TODO: main search
                //LoadTweets(0,SearchType.MyFollowing);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<AdapterItems> listnewsDataAdpater;
        Context context;

        public MyCustomAdapter(Context context, ArrayList<AdapterItems> listnewsDataAdpater) {
            this.listnewsDataAdpater = listnewsDataAdpater;
            this.context = context;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final AdapterItems s = listnewsDataAdpater.get(position);

            if (s.tweet_date.equals("add")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.addstatus, null);
                ImageView picture_path = (ImageView) myView.findViewById(R.id.ivmydp);
                Picasso.with(context).load(s.picture_path).into(picture_path);
                return myView;
            }
            else if (s.tweet_date.equals("loading")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_loading, null);
                return myView;
            }
            else if (s.tweet_date.equals("notweet")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_msg, null);
                return myView;
            }
            else {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_item, null);

                TextView txtUserName = (TextView) myView.findViewById(R.id.txtUserName);
                txtUserName.setText(s.first_name);
                txtUserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectedUserID = Integer.parseInt(s.user_id);
                        LoadTweets(0, SearchType.OnePerson);
                        txtnamefollowers.setText(s.first_name);

                        String url = "https://tusharsk26.000webhostapp.com/TwikBust/isFollowing.php?user_id=" + SaveSettings.UserID + "&following_user_id=" + SelectedUserID;
                        new MyAsyncTaskgetNews().execute(url);


                    }
                });

                txt_tweet.setText(s.tweet_text);

                TextView txt_tweet_date = (TextView) myView.findViewById(R.id.txt_tweet_date);
                txt_tweet_date.setText(s.tweet_date);

                ImageView tweet_picture = (ImageView) myView.findViewById(R.id.tweet_picture);
                Picasso.with(context).load(s.tweet_picture).into(tweet_picture);
                ImageView picture_path = (ImageView) myView.findViewById(R.id.picture_path);
                Picasso.with(context).load(s.picture_path).into(picture_path);
                return myView;
            }
        }


    } //load image
        //}

        //load image
        //pop

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

/*
    //save image
    int RESULT_LOAD_IMAGE=233;
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
            // postImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            uploadimage( BitmapFactory.decodeFile(picturePath));
        }
    }
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

                if (json.getString("msg").equalsIgnoreCase("tweet is added")) {
                    LoadTweets(0,UserOperation);
                }
                else if (json.getString("msg").equalsIgnoreCase("has tweet")) {
                    if(StartFrom==0) {
                        listnewsData.clear();
                        listnewsData.add(new AdapterItems(null, null, null,
                                "add", null, null, null));

                    }
                    else {
                        //remove we are loading now
                        listnewsData.remove(listnewsData.size()-1);
                    }
                   // Toast.makeText(getApplicationContext(),"getting tweet",Toast.LENGTH_SHORT).show();

                    JSONArray tweets=new JSONArray( json.getString("info"));

                    //Toast.makeText(getApplicationContext(),"size"+tweets.length(),Toast.LENGTH_SHORT).show();
                    for (int i = 0; i<tweets.length() ; i++) {
                        // try to add the resourcess
                        JSONObject js=tweets.getJSONObject(i);
                        //Toast.makeText(getApplicationContext(),"tweet "+i,Toast.LENGTH_SHORT).show();
                        //showProgressDialog();
                        //add data and view it
                        listnewsData.add(new AdapterItems(js.getString("tweet_id"),
                                js.getString("tweet_text"),js.getString("tweet_picture") ,
                                js.getString("tweet_date") ,js.getString("user_id") ,js.getString("user_name")
                                ,js.getString("picture_path") ));

                        //hideProgressDialog();
                       // Toast.makeText(getApplicationContext(),"tweetee",Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(getApplicationContext(),"got tweet",Toast.LENGTH_SHORT).show();
                    myadapter.notifyDataSetChanged();

                }

                else if (json.getString("msg").equalsIgnoreCase("no tweet")) {
                    Toast.makeText(getApplicationContext(),"no tweet",Toast.LENGTH_SHORT).show();
                    //remove we are loading now
                    if(StartFrom==0) {
                        listnewsData.clear();
                        listnewsData.add(new AdapterItems(null, null, null,
                                "add", null, null, null));
                    }
                    else {
                        //remove we are loading now
                        listnewsData.remove(listnewsData.size()-1);
                    }
                    // listnewsData.remove(listnewsData.size()-1);
                    listnewsData.add(new AdapterItems(null, null, null,
                            "notweet", null, null, null));
                }
                else if (json.getString("msg").equalsIgnoreCase("is subscriber")) {
                    buFollow.setText("Un Follow");
                }
                else if (json.getString("msg").equalsIgnoreCase("is not subscriber")) {
                    buFollow.setText("Follow");
                }

            } catch (Exception ex) {
                Log.d("er",  ex.getMessage());
                //first time
                listnewsData.clear();
                listnewsData.add(new AdapterItems(null, null, null,
                        "add", null, null, null));
            }

            myadapter.notifyDataSetChanged();
            //downloadUrl=null;
        }

        protected void onPostExecute(String  result2){

        }




    }

    void LoadTweets(int StartFrom,int UserOperation){
        this.StartFrom=StartFrom;
        this.UserOperation=UserOperation;
        //display loading
        if(StartFrom==0) // add loading at beggining
            listnewsData.add(0,new AdapterItems(null, null, null,
                    "loading", null, null, null));
        else // add loading at end
            listnewsData.add(new AdapterItems(null, null, null,
                    "loading", null, null, null));

        myadapter.notifyDataSetChanged();

        ///https://tusharsk26.000webhostapp.com/TwikBust/TweetsList.php


        if(isFront==1) {
            String url = "https://tusharsk26.000webhostapp.com/TwikBust/TweetsList.php?user_id=" + SaveSettings.UserID + "&StartFrom=" + StartFrom + "&op=" + UserOperation;
            //Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
            if (UserOperation == SearchType.SearchIn)
                url = "https://tusharsk26.000webhostapp.com/TwikBust/TweetsList.php?user_id=" + SaveSettings.UserID + "&StartFrom=" + StartFrom + "&op=" + UserOperation + "&query=" + Searchquery;
            if (UserOperation == SearchType.OnePerson)
                url = "https://tusharsk26.000webhostapp.com/TwikBust/TweetsList.php?user_id=" + SelectedUserID + "&StartFrom=" + StartFrom + "&op=" + UserOperation;

            new MyAsyncTaskgetNews().execute(url);

            if (UserOperation == SearchType.OnePerson)
                ChannelInfo.setVisibility(View.VISIBLE);
            else
                ChannelInfo.setVisibility(View.GONE);
        }


    }

    @Override
    public void onResume()
    {
        super.onResume();
        LoadTweets(0,SearchType.MyFollowing);
        isFront=1;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        isFront=0;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        isFront=1;
    }

}
*/