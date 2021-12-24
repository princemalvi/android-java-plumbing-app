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

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BookingActivity extends AppCompatActivity {

    String userid,workerid;
    SharedPreferences preferences ;
    EditText address,city,area,pincode;
    Button btnBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        preferences  = getSharedPreferences("loginPreference",0);
        userid = preferences.getString("userid","0");
        workerid = getIntent().getExtras().getString("workerid");

        address = findViewById(R.id.etAddress);
        area = findViewById(R.id.etArea);
        city = findViewById(R.id.etCity);
        pincode = findViewById(R.id.etPincode);
        btnBook = findViewById(R.id.btnBook);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(address.getText().toString().trim().equals("")){
                    Toast.makeText(BookingActivity.this, "Please Enter Addresss", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(area.getText().toString().trim().equals("")){
                    Toast.makeText(BookingActivity.this, "Please Enter Area", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(city.getText().toString().trim().equals("")){
                    Toast.makeText(BookingActivity.this, "Please Enter City", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pincode.getText().toString().trim().equals("")){
                    Toast.makeText(BookingActivity.this, "Please Enter Pincode", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(pincode.getText().toString().length() != 6){
                        Toast.makeText(BookingActivity.this, "Pincode Must be 6 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                new BookWorker(workerid,userid).execute();

            }
        });
    }


    public class BookWorker extends AsyncTask<String,String,String> {
        String workerid;
        String userid;
        String add,ct,ara,pin ;
        ProgressDialog progressDialog;
        String result;
        BookWorker(String workerid,String userid){
            this.workerid = workerid;
            this.userid = userid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            add = address.getText().toString();
            ct = city.getText().toString();
            ara = area.getText().toString();
            pin = pincode.getText().toString();

            progressDialog = new ProgressDialog(BookingActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> map = new HashMap();
            map.put("userid",userid);
            map.put("workerid",workerid);
            map.put("address",add);
            map.put("area",ct);
            map.put("city",ara);
            map.put("pincode",pin);
            try {
                WebServiceCall call = new WebServiceCall();
                result = call.postData(GlobalURL.BOOKING_WORKER_URL, WebServiceCall.POST, map);
            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                String isSuccess = object.getString("msg");
                if(isSuccess.equals("Success")){

                    Toast.makeText(BookingActivity.this, "Booking Success...", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(BookingActivity.this,WelcomeUser.class);
                    startActivity(i);
                }else{
                    Toast.makeText(BookingActivity.this, "Booking Failed Retry...", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }

}