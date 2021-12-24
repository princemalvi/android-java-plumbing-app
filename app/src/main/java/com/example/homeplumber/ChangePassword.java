package com.example.homeplumber;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {

    EditText etOldPassword , etNewPassword ,etConformNewPassword ;
    ProgressDialog pd;
    Button btnChangePassword ;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConformNewPassword = findViewById(R.id.etConformNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        preferences = getSharedPreferences("loginPreference",0);


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etOldPassword.getText().toString().trim().equals(""))
                {
                    Toast.makeText(ChangePassword.this, "Please Enter old password", Toast.LENGTH_SHORT).show();
                    return;
                } else if(etNewPassword.getText().toString().trim().equals("")){
                    Toast.makeText(ChangePassword.this, "Please Enter New Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(etConformNewPassword.getText().toString().trim().equals("")){
                    Toast.makeText(ChangePassword.this, "Please Enter Conform New Password", Toast.LENGTH_SHORT).show();
                    return;
                }else if(etOldPassword.getText().length() < 8 || etNewPassword.getText().length() < 8){
                    Toast.makeText(ChangePassword.this, "Please Must be 8 Digit", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!etConformNewPassword.getText().toString().equals(etNewPassword.getText().toString()))
                {
                    Toast.makeText(ChangePassword.this,"New Password and Confrom pasword not match",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    CheckChangePassword cp = new CheckChangePassword();
                    cp.execute();
                }
            }
        });



    }


    class CheckChangePassword extends AsyncTask<String,String,String>
    {
        String oldPassword , newPassword ,result ;
        String type,userid;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            type = preferences.getString("type",null);
            userid = preferences.getString("userid",null);
            pd = new ProgressDialog(ChangePassword.this);
            oldPassword = etOldPassword.getText().toString() ;
            newPassword = etNewPassword.getText().toString();
            pd = new ProgressDialog(ChangePassword.this);
            pd.setMessage("Loading....");
            pd.show();
            pd.setContentView(R.layout.progress_dialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }


        @Override
        protected String doInBackground(String... strings) {
            WebServiceCall service = new WebServiceCall();
            HashMap<String,String> map = new HashMap<>();

            map.put("userid",userid);
            map.put("old",oldPassword );
            map.put("new",newPassword );
            try {
                if(type.equals("0")) {
                    result = service.postData(GlobalURL.CHANGE_WORKER_PASSWORD,WebServiceCall.POST,map);
                }else{
                    result = service.postData(GlobalURL.CHANGE_USER_PASSWORD,WebServiceCall.POST,map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                String str = jsonObject.getString("msg");
                if(str.equals("valid") && type.equals("1")) {
                    Toast.makeText(ChangePassword.this, "Password Change Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePassword.this,WelcomeUser.class);
                    startActivity(intent);

                }else if(str.equals("valid") && type.equals("0")){
                    Toast.makeText(ChangePassword.this, "Password Change Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePassword.this,WelcomeWorker.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(ChangePassword.this, str+"", Toast.LENGTH_SHORT).show();;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}