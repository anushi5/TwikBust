package com.example.anushi.twikbust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by anushi on 9/7/17.
 */

public class SaveSettings {
    public  static String UserID="";
    public  static String dp_picture_path="";
    public  static String user_name="";
    public  static String user_gender="";
    Context context;
    SharedPreferences ShredRef;
    public  SaveSettings(Context context){
        this.context=context;
        ShredRef=context.getSharedPreferences("myRef",Context.MODE_PRIVATE);
    }

    void SaveData(String UserID,String profilepath,String user_gender,String user_name){

        SharedPreferences.Editor editor=ShredRef.edit();
        editor.putString("UserID",UserID);
        editor.putString("dp_picture_path",profilepath);
        editor.putString("user_gender",user_gender);
        editor.putString("user_name",user_name);
        editor.commit();
        LoadData();
    }

    void LoadData(){
        UserID= ShredRef.getString("UserID","0");
        dp_picture_path=ShredRef.getString("dp_picture_path",null);
        user_gender=ShredRef.getString("user_gender",null);
        user_name=ShredRef.getString("user_name",null);
        if (UserID.equals("0")){

            Intent intent=new Intent(context, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }
    boolean UserPresent()
    {
        UserID= ShredRef.getString("UserID","0");
        if(UserID.equals("0"))
            return false;
        else
            return true;
    }
}