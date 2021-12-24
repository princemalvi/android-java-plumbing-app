package com.example.homeplumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    EditText etUsername, etPassword,etPhone;
    Spinner spinner ;
    ProgressDialog pd  ;
    TextView tvLogin;
    Button btnNext;
    String type="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        etUsername = findViewById(R.id.etUsername);
        spinner = findViewById(R.id.type);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnNext = findViewById(R.id.btnNext);
        tvLogin = findViewById(R.id.tvLogin);

        ArrayList<String> list = new ArrayList<>();
        list.add("Worker");
        list.add("Customer");
        ArrayAdapter adapter = new ArrayAdapter(RegistrationActivity.this, android.R.layout.simple_expandable_list_item_1,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type= String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etUsername.getText().toString().trim().equals("")){
                    Toast.makeText(RegistrationActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etPhone.getText().toString().trim().equals("")){
                    Toast.makeText(RegistrationActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return ;
                }else if(etPhone.getText().toString().trim().length() != 10){
                    Toast.makeText(RegistrationActivity.this, "Phone Number Must be 10 Digit", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(etPassword.getText().toString().trim().equals("")){
                    Toast.makeText(RegistrationActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return ;
                }else if(etPassword.getText().toString().trim().length() < 8){
                    Toast.makeText(RegistrationActivity.this, "Password must be 8 character or greater ", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(type.equals("0")){
                    Intent intent = new Intent(RegistrationActivity.this,WokerDetail.class);
                    intent.putExtra("username",etUsername.getText().toString());
                    intent.putExtra("phone",etPhone.getText().toString());
                    intent.putExtra("password",etPassword.getText().toString());
                    startActivity(intent);
                }else{
                    new UserRegister().execute();
                }
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });
    }



    public class UserRegister extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog ;
        String username,password,phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = etUsername.getText().toString();
            phone = etPhone.getText().toString();
            password = etPassword.getText().toString();
            progressDialog = new ProgressDialog(RegistrationActivity.this);
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
            try {
                result = call.postData(GlobalURL.USER_REGISTER_URL,WebServiceCall.POST,hashMap);
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
                    Toast.makeText(RegistrationActivity.this, "Registration Success!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegistrationActivity.this, isValid, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}