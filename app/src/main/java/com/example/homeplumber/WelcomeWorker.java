package com.example.homeplumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WelcomeWorker extends AppCompatActivity {

    SharedPreferences preferences;
    ListView lstView;
    ArrayList<WBookings> workBook;
    WorkerBookAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.worker_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_worker);
        lstView = findViewById(R.id.lstView);
        getSupportActionBar().setTitle("My Bookings");
        preferences = getSharedPreferences("loginPreference",0);
        new LoadWorkerOrder().execute();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id){
            case R.id.updateplace:
                intent = new Intent(WelcomeWorker.this,UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.changepassword:
                intent = new Intent(WelcomeWorker.this,ChangePassword.class);
                startActivity(intent);
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                intent = new Intent(WelcomeWorker.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class LoadWorkerOrder extends AsyncTask<String,String,String> {

        ProgressDialog progressDialog ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            workBook = new ArrayList<>();
            progressDialog  = new ProgressDialog(WelcomeWorker.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            WebServiceCall call = new WebServiceCall();
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("userid",preferences.getString("userid","0"));
            try {
                result = call.postData(GlobalURL.WORKER_ORDER_URL,WebServiceCall.GET,hashMap);
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
                JSONObject object = new JSONObject(s);
                JSONArray jsonArray = object.getJSONArray("msg");
                if(jsonArray.length() == 0){
                    Toast.makeText(WelcomeWorker.this, "You have No booking Order!!!", Toast.LENGTH_SHORT).show();
                }
                for(int i = 0;i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    WBookings order = new WBookings();
                    order.setBookingid(Integer.parseInt(jsonObject.getString("bookingid")));
                    order.setUserid(Integer.parseInt(jsonObject.getString("userid")));
                    order.setUsername(jsonObject.getString("username"));
                    order.setAddress(jsonObject.getString("address"));
                    order.setArea(jsonObject.getString("area"));
                    order.setCity(jsonObject.getString("city"));
                    order.setPincode(jsonObject.getString("pincode"));
                    order.setDate(jsonObject.getString("bookingdate"));
                    order.setPhone(jsonObject.getString("phone"));
                    order.setStatus(jsonObject.getString("status"));


                    workBook.add(order);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            adapter = new WorkerBookAdapter();
            lstView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }



    public class WorkerBookAdapter extends BaseAdapter {

        LayoutInflater inflater;
        public WorkerBookAdapter() {
            inflater = LayoutInflater.from(WelcomeWorker.this);

        }

        public class ViewHolder{
            TextView username;
            TextView phone;
            TextView address;
            TextView status;
            TextView date;
        }
        @Override
        public int getCount() {
            return workBook.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.worker_booking_row, null);
                viewHolder.username = convertView.findViewById(R.id.tvUsername);
                viewHolder.phone = convertView.findViewById(R.id.tvPhone);
                viewHolder.address = convertView.findViewById(R.id.tvAddress);
                viewHolder.date = convertView.findViewById(R.id.tvDate);
                viewHolder.status = convertView.findViewById(R.id.tvStatus);
//                viewHolder.city = convertView.findViewById(R.id.tvCity);
//                viewHolder.callNow = convertView.findViewById(R.id.btnCall);
//                viewHolder.bookWorker = convertView.findViewById(R.id.btnBook);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.username.setText("Name : " + workBook.get(position).getUsername());
            viewHolder.phone.setText("Phone : "+workBook.get(position).getPhone());
            viewHolder.address.setText("Address : "+workBook.get(position).getAddress()+","+workBook.get(position).getArea()+","+workBook.get(position).getCity()+"-"+workBook.get(position).getPincode());
            viewHolder.status.setText("Status : "+workBook.get(position).getStatus());
            viewHolder.date.setText("Date & Time : "+workBook.get(position).getDate());
            String s = workBook.get(position).getStatus();
            if(s.equals("0"))
                s = "Not Completed";
            else if(s.equals("1"))
                s = "Completed";
            else
                s = "Canceled By User";
            viewHolder.status.setText("Order Status : "+s);


            return convertView;
        }
    }
}