package com.example.homeplumber;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etPhone, etPassword;
    Button btnLogin ;
    TextView tvRegister;
    Spinner spinner;
    String type = "0";
    SharedPreferences sharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        spinner = findViewById(R.id.type);
        tvRegister = findViewById(R.id.tvRegister);
        sharedPreferences = getSharedPreferences("loginPreference",0);

        ArrayList<String> list;
        list = new ArrayList<>();
        list.add("Worker");
        list.add("Customer");
        ArrayAdapter adapter = new ArrayAdapter(LoginActivity.this, android.R.layout.simple_expandable_list_item_1,list);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = String.valueOf(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPhone.getText().toString().trim().equals("")){
                    Toast.makeText(LoginActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return ;
                }else if(etPhone.getText().toString().trim().length() != 10){
                    Toast.makeText(LoginActivity.this, "Phone Number Must be 10 Digit", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(etPassword.getText().toString().trim().equals("")){
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return ;
                }else if(etPassword.getText().toString().trim().length() < 8){
                    Toast.makeText(LoginActivity.this, "Password must be 8 character or greater ", Toast.LENGTH_SHORT).show();
                    return ;
                }

                CheckLogin login = new CheckLogin(type);
                login.execute();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

    }

    public class CheckLogin extends AsyncTask<String,String,String> {
        String phone,password;
        ProgressDialog progressDialog ;
        String type;
        CheckLogin(String type){
            this.type = type;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phone = etPhone.getText().toString();
            password = etPassword.getText().toString();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            Log.d("do","1");
            WebServiceCall call = new WebServiceCall();
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("phone",phone);
            hashMap.put("password",password);
            try {
                if(type.equals("0"))
                    result = call.postData(GlobalURL.WORKER_LOGIN_URL,WebServiceCall.POST,hashMap);
                else
                    result = call.postData(GlobalURL.USER_LOGIN_URL,WebServiceCall.POST,hashMap);
                Log.d("do","3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("Result",result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = new JSONObject(s);
                String isValid = jsonObject.getString("msg");
                if(isValid.equals("Valid") && type.equals("0")) {

                    String userid = jsonObject.getString("userid");
                    String username = jsonObject.getString("username");
                    String phone = jsonObject.getString("phone");
                    String city = jsonObject.getString("city");
                    String area = jsonObject.getString("area");
                    String date = jsonObject.getString("date");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userid);
                    editor.putString("username", username);
                    editor.putString("phone", phone);
                    editor.putString("city", city);
                    editor.putString("area", area);
                    editor.putString("type", "0");
                    editor.commit();
                    Toast.makeText(LoginActivity.this, "Login as Worker",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,WelcomeWorker.class));
                    finish();

                }
                else if(isValid.equals("Valid") && type.equals("1")){
                    String userid = jsonObject.getString("userid");
                    String username = jsonObject.getString("username");
                    String phone = jsonObject.getString("phone");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userid);
                    editor.putString("username", username);
                    editor.putString("phone", phone);
                    editor.putString("type","1");

                    editor.commit();
                    Toast.makeText(LoginActivity.this, "Login as Customer",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,WelcomeUser.class));
                    finish();

                }else{
                    Toast.makeText(LoginActivity.this, "Username and Password Incorrect!!",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}