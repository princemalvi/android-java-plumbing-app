package com.example.homeplumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class WokerDetail extends AppCompatActivity {

    EditText etArea,etCity;
    Button btnRegister;
    String username,phone,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woker_detail);
        etArea = findViewById(R.id.etArea);
        etCity = findViewById(R.id.etCity);
        btnRegister = findViewById(R.id.btnRegister);
        Bundle b = getIntent().getExtras();
        username = b.getString("username");
        phone = b.getString("phone");
        password = b.getString("password");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etArea.getText().toString().trim().equals("")){
                    Toast.makeText(WokerDetail.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etCity.getText().toString().trim().equals("")){
                    Toast.makeText(WokerDetail.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                new WorkerRegister(username,phone,password).execute();
            }
        });
    }


    public class WorkerRegister extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog ;
        String username,password,phone,city,area;

        WorkerRegister(String username,String phone,String password){
            this.username = username;
            this.phone = phone;
            this.password = password;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            city = etCity.getText().toString();
            area = etArea.getText().toString();
            progressDialog  = new ProgressDialog(WokerDetail.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            WebServiceCall call = new WebServiceCall();
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("username",username);
            hashMap.put("password",password);
            hashMap.put("phone",phone);
            hashMap.put("city",city);
            hashMap.put("area",area);
            try {
                result = call.postData(GlobalURL.WORKER_REGISTER_URL,WebServiceCall.POST,hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Result",result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = new JSONObject(s);
                String isValid = (String) jsonObject.getString("msg");
                if(isValid.equals("Success"))
                {
                    Toast.makeText(WokerDetail.this, "Registration Success!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WokerDetail.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(WokerDetail.this, isValid, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}