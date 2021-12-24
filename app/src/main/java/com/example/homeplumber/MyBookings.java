package com.example.homeplumber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyBookings extends AppCompatActivity {

    ListView listView;
    SharedPreferences preferences;
    ArrayList<Bookings> bookList;
    OrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("loginPreference",0);
        setContentView(R.layout.activity_my_bookings);
        listView = findViewById(R.id.lstView);
        new LoadMyOrder().execute();

    }



    public class LoadMyOrder extends AsyncTask<String,String,String> {

        ProgressDialog progressDialog ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSupportActionBar().setTitle("My Bookings");
            bookList = new ArrayList<>();
            progressDialog = new ProgressDialog(MyBookings.this);
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
                result = call.postData(GlobalURL.USER_ORDER_URL,WebServiceCall.GET,hashMap);
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
                for(int i = 0;i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Bookings order = new Bookings();
                    order.setOrderid(Integer.parseInt(jsonObject.getString("bookingid")));
                    order.setWorkerid(jsonObject.getString("wid"));
                    order.setWorkername(jsonObject.getString("wname"));
                    order.setDate(jsonObject.getString("bookingdate"));
                    order.setStatus(jsonObject.getString("status"));
                    order.setPhone(jsonObject.getString("phone"));
                    bookList.add(order);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(MyBookings.this, String.valueOf(bookList.size()), Toast.LENGTH_SHORT).show();
            adapter = new OrderAdapter();
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public class OrderAdapter extends BaseAdapter {

        LayoutInflater inflater;
        public OrderAdapter() {
            inflater = LayoutInflater.from(MyBookings.this);

        }

        public class ViewHolder{
            TextView username;
            TextView phone;
            TextView status;
            TextView date;
            Button btnCancel;
            Button btnComplete;
        }
        @Override
        public int getCount() {
            return bookList.size();
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
                convertView = inflater.inflate(R.layout.my_bookings_row, null);
                viewHolder.username = convertView.findViewById(R.id.tvUsername);
                viewHolder.phone = convertView.findViewById(R.id.tvPhone);
                viewHolder.date = convertView.findViewById(R.id.tvDate);
                viewHolder.status = convertView.findViewById(R.id.tvStatus);
                viewHolder.btnCancel = convertView.findViewById(R.id.btnCancel);
                viewHolder.btnComplete = convertView.findViewById(R.id.btnComplete);
//                viewHolder.city = convertView.findViewById(R.id.tvCity);
//                viewHolder.callNow = convertView.findViewById(R.id.btnCall);
//                viewHolder.bookWorker = convertView.findViewById(R.id.btnBook);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.username.setText("Name : " + bookList.get(position).getWorkername());
            viewHolder.phone.setText("Phone : "+bookList.get(position).getPhone());
            viewHolder.date.setText("Date & Time : "+bookList.get(position).getDate());
            String s = bookList.get(position).getStatus();
            if(s.equals("0")){
                viewHolder.btnCancel.setText("Cancel Booking");
                viewHolder.btnComplete.setText("Work Completed");

            }else if(s.equals("1")){
                viewHolder.btnComplete.setText("Completed");
                viewHolder.btnComplete.setEnabled(false);
                viewHolder.btnComplete.setTextColor(ContextCompat.getColor(MyBookings.this, R.color.white));
                viewHolder.btnComplete.setBackgroundColor(ContextCompat.getColor(MyBookings.this, R.color.orange));
                viewHolder.btnCancel.setVisibility(View.GONE);
            }else{
                viewHolder.btnCancel.setText("Canceled ");
                viewHolder.btnCancel.setEnabled(false);
                viewHolder.btnCancel.setTextColor(ContextCompat.getColor(MyBookings.this, R.color.white));
                viewHolder.btnCancel.setBackgroundColor(ContextCompat.getColor(MyBookings.this, R.color.teal_200));

                viewHolder.btnComplete.setVisibility(View.GONE);
            }
            viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CancelBooking(bookList.get(position).getOrderid()).execute();
                }
            });
            viewHolder.btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new WorkComplete(bookList.get(position).getOrderid()).execute();
                }
            });


            return convertView;
        }
    }
    public class CancelBooking extends AsyncTask<String,String,String>{
        ProgressDialog pd;
        int orderid;
        String res;
        CancelBooking(int orderid){
            this.orderid = orderid;
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MyBookings.this);
            pd.show();
            pd.setContentView(R.layout.progress_dialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HashMap<String,String> map = new HashMap<>();
                map.put("orderid",String.valueOf(orderid));
                WebServiceCall call = new WebServiceCall();
                res = call.postData(GlobalURL.CANCEL_BOOKING,WebServiceCall.POST,map);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try{
                JSONObject jsonObject = new JSONObject(s);
                String isValid = jsonObject.getString("msg");
                if(isValid.equals("Success")){
                    Toast.makeText(MyBookings.this,"Booking Canceled Success....",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MyBookings.this,MyBookings.class));
                }else{
                    Toast.makeText(MyBookings.this,"Booking Canceled Failed...",Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public class WorkComplete extends AsyncTask<String,String,String>{
        ProgressDialog pd;
        int orderid;
        String res;
        WorkComplete(int orderid){
            this.orderid = orderid;
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MyBookings.this);
            pd.show();
            pd.setContentView(R.layout.progress_dialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HashMap<String,String> map = new HashMap<>();
                map.put("orderid",String.valueOf(orderid));
                WebServiceCall call = new WebServiceCall();
                res = call.postData(GlobalURL.COMPLETE_BOOKING,WebServiceCall.POST,map);
            }catch (Exception e){
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try{
                JSONObject jsonObject = new JSONObject(s);
                String isValid = jsonObject.getString("msg");
                if(isValid.equals("Success")){
                    Toast.makeText(MyBookings.this,"Work Completed Updation Success....",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MyBookings.this,MyBookings.class));
                }else{
                    Toast.makeText(MyBookings.this,"Work Completed Updation Failed...",Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}