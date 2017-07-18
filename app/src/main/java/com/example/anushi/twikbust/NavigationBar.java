package com.example.anushi.twikbust;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NavigationBar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<AdapterItems> listnewsData = new ArrayList<AdapterItems>();
    int StartFrom = 0;
    int UserOperation = SearchType.MyFollowing; // 1 my followers post 2- specifc user post 3- search post
    String Searchquery;
    int totalItemCountVisible = 0; //totalItems visible
    LinearLayout ChannelInfo;
    TextView txtnamefollowers;
    int SelectedUserID = 0;
    Button buFollow;
    MyAdapterRecylerview mAdapter;

    DrawerLayout mdrawerlayout;
    ActionBarDrawerToggle mtoggle;
    Toolbar mtoolbar;
    int isFront=0;
    RecyclerView recyclerView;

    SaveSettings saveSettings;
    ImageView ivdrawerdp;

    TextView tvusernamenav;
    TextView tvemailnav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        saveSettings= new SaveSettings(getApplicationContext());
        saveSettings.LoadData();
        if(!saveSettings.UserPresent())
        {
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader = navigationView.getHeaderView(0);
        ivdrawerdp=(ImageView) navHeader.findViewById(R.id.ivdrawerdp);
        tvusernamenav=(TextView) navHeader.findViewById(R.id.tvusernamenav);
        tvusernamenav.setText(saveSettings.user_name);
        Picasso.with(getApplicationContext()).load(saveSettings.dp_picture_path).into(ivdrawerdp);




        // my code





        ChannelInfo = (LinearLayout) findViewById(R.id.ChannelInfo);
        ChannelInfo.setVisibility(View.GONE);
        txtnamefollowers = (TextView) findViewById(R.id.txtnamefollowers);
        buFollow = (Button) findViewById(R.id.buFollow);


        recyclerView = (RecyclerView) findViewById(R.id.rv);
        mAdapter = new MyAdapterRecylerview(this,listnewsData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent=new Intent(getApplicationContext(),AddingStatus.class);
            String name=saveSettings.user_name;
            String dp_picture_path=saveSettings.dp_picture_path;
            intent.putExtra("user_name",name);
            intent.putExtra("dp_picture_path",dp_picture_path);
            startActivity(intent);


        } else if (id == R.id.nav_slideshow) {

            listnewsData.clear();
            LoadTweets(0,SearchType.MyFollowing);
        } else if (id == R.id.nav_manage) {

            saveSettings.DeleteData();
            Intent intent=new Intent(getApplicationContext(),Login.class);

            startActivity(intent);
            finish();


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    // my code
    public void addstatus(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AddingStatus.class);
        startActivity(intent);
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
                        //listnewsData.clear();
                        //listnewsData.add(new AdapterItems(null, null, null,"add", null, null, null));

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
                    mAdapter.notifyDataSetChanged();

                }

                else if (json.getString("msg").equalsIgnoreCase("no tweet")) {
                    Toast.makeText(getApplicationContext(),"no tweet",Toast.LENGTH_SHORT).show();
                    //remove we are loading now
                    if(StartFrom==0) {
                        //listnewsData.clear();
                        //listnewsData.add(new AdapterItems(null, null, null,
                                //"add", null, null, null));
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

            mAdapter.notifyDataSetChanged();
            //downloadUrl=null;
        }

        protected void onPostExecute(String  result2){

        }




    }
    void LoadTweets(int StartFrom,int UserOperation){
        this.StartFrom=StartFrom;
        this.UserOperation=UserOperation;
        //display loading
       // if(StartFrom==0) // add loading at beggining
            ////listnewsData.add(0,new AdapterItems(null, null, null,
                //    "loading", null, null, null));
        //else // add loading at end
           // listnewsData.add(new AdapterItems(null, null, null,
             //       "loading", null, null, null));

        mAdapter.notifyDataSetChanged();

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
        listnewsData.clear();
        isFront=1;
    }



    public class  MyAdapterRecylerview extends RecyclerView.Adapter<MyAdapterRecylerview.MyViewHolder>
    {
        private ArrayList<AdapterItems> listnewsDataAdpater;
        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtUserName,txt_tweet,txt_tweet_date;
            public ImageView tweet_picture,picture_path;

            public MyViewHolder(View myView) {
                super(myView);
                txtUserName = (TextView) myView.findViewById(R.id.txtUserName);
                txt_tweet = (TextView) myView.findViewById(R.id.txt_tweet);
                txt_tweet_date = (TextView) myView.findViewById(R.id.txt_tweet_date);
                tweet_picture = (ImageView) myView.findViewById(R.id.tweet_picture);
                picture_path = (ImageView) myView.findViewById(R.id.picture_path);


            }
        }

        public MyAdapterRecylerview(Context context,ArrayList<AdapterItems> listnewsDataAdpater)
        {
            this.listnewsDataAdpater=listnewsDataAdpater;
            this.context=context;
        }

        @Override
        public MyAdapterRecylerview.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tweet_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyAdapterRecylerview.MyViewHolder holder, int position) {
            AdapterItems s=listnewsDataAdpater.get(position);
            holder.txtUserName.setText(s.first_name);
            holder.txt_tweet.setText(s.tweet_text);
            holder.txt_tweet_date.setText(s.tweet_date);
            //holder.picture_path.setImageBitmap(s.picture_path);
            Picasso.with(context).load(s.picture_path).into(holder.picture_path);
            Picasso.with(context).load(s.tweet_picture).into(holder.tweet_picture);
            //Toast.makeText(getApplicationContext(),s.picture_path,Toast.LENGTH_SHORT).show();

        }

        @Override
        public int getItemCount() {
            return listnewsDataAdpater.size();
        }


    }

}
