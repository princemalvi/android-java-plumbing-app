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

import org.json.JSONObject;

import java.util.HashMap;

public class UpdateProfile extends AppCompatActivity {
    EditText etArea,etCity;
    Button btnUpdate;
    SharedPreferences preferences ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        etArea = findViewById(R.id.etArea);
        etCity = findViewById(R.id.etCity);
        btnUpdate = findViewById(R.id.btnUpdate);

        preferences = getSharedPreferences("loginPreference",0);
        etArea.setText(preferences.getString("area",""));
        etCity.setText(preferences.getString("city",""));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etArea.getText().toString().trim().equals("")){
                    Toast.makeText(UpdateProfile.this,"Please Enter Area",Toast.LENGTH_SHORT).show();
                    return;
                }else if(etCity.getText().toString().trim().equals("")) {
                    Toast.makeText(UpdateProfile.this, "Please Enter City", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    new UpdatePlaces().execute();
                }
            }
        });
    }


    class UpdatePlaces extends AsyncTask<String,String,String>
    {
        String area , city ,result ,userid;
        ProgressDialog pd ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            editor = preferences.edit();
            pd = new ProgressDialog(UpdateProfile.this);
            userid = preferences.getString("userid",null);
            pd = new ProgressDialog(UpdateProfile.this);
            area = etArea.getText().toString() ;
            city = etCity.getText().toString();
            pd = new ProgressDialog(UpdateProfile.this);
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
            map.put("area",area );
            map.put("city",city );
            try {
                result = service.postData(GlobalURL.UPDATE_WORKER_PROFILE,WebServiceCall.POST,map);
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
                if(str.equals("success")){
                    editor.putString("city",city);
                    editor.putString("area",area);
                    editor.commit();
                    Toast.makeText(UpdateProfile.this, "Place Changed Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateProfile.this,WelcomeWorker.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(UpdateProfile.this, "Place Updation Failed!!", Toast.LENGTH_SHORT).show();;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}