package com.example.homeplumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WelcomeUser extends AppCompatActivity {
    SearchView searchView;
    MenuItem searchText;
    ListView listView;
    WorkerAdapter adapter;
    ArrayList<Worker> workers;
    SharedPreferences preferences;
    private Toolbar toolbar;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu,menu);

        searchText = menu.findItem(R.id.search);;
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search By area or City");



        searchText.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                new LoadWorker().execute();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new LoadUserBySearch(query).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_user);

        toolbar =  findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setTitle("Home");


        //        searchView = findViewById(R.id.search);
        listView = findViewById(R.id.lstView);
        preferences = getSharedPreferences("loginPreference",0);
        new LoadWorker().execute();


    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id){
            case R.id.mybooking:
                intent = new Intent(WelcomeUser.this,MyBookings.class);
                startActivity(intent);
                break;
            case R.id.changepassword:
                intent = new Intent(WelcomeUser.this,ChangePassword.class);
                startActivity(intent);
                break;
            case R.id.logout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                intent = new Intent(WelcomeUser.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoadWorker extends AsyncTask<String,String,String> {

        ProgressDialog progressDialog ;
        //        ArrayList<Blog> arrayList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            workers = new ArrayList<>();
            progressDialog  = new ProgressDialog(WelcomeUser.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            WebServiceCall call = new WebServiceCall();
            HashMap<String,String> hashMap = new HashMap<>();
            try {
                result = call.postData(GlobalURL.GETWORKER_URL,WebServiceCall.GET,hashMap);
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
                    Worker w = new Worker();
                    w.setWid(Integer.parseInt(jsonObject.getString("userid")));
                    w.setWname(jsonObject.getString("username"));
                    w.setPhone(jsonObject.getString("phone"));
                    w.setArea(jsonObject.getString("area"));
                    w.setCity(jsonObject.getString("city"));
                    workers.add(w);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(WelcomeUser.this, String.valueOf(workers.size()), Toast.LENGTH_SHORT).show();
            adapter = new WorkerAdapter(workers);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }



    public class WorkerAdapter extends BaseAdapter {

        //        List<Blog> blogList;
        ArrayList<Worker> workerList ;
        //        Context workerListxt;
        ProgressDialog progressDialog;
        LayoutInflater inflater;
        public WorkerAdapter(ArrayList<Worker> userList) {
            this.workerList = userList;
            inflater = LayoutInflater.from(WelcomeUser.this);

        }

        public class ViewHolder{
            TextView username;
            TextView phone;
            TextView city;
            TextView area;
            Button callNow;
            Button bookWorker;
        }
        @Override
        public int getCount() {
            return workerList.size();
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
                convertView = inflater.inflate(R.layout.worker_row_item, null);
                viewHolder.username = convertView.findViewById(R.id.tvUsername);
                viewHolder.phone = convertView.findViewById(R.id.tvPhone);
                viewHolder.area = convertView.findViewById(R.id.tvArea);
                viewHolder.city = convertView.findViewById(R.id.tvCity);
//                viewHolder.city = convertView.findViewById(R.id.tvCity);
                viewHolder.callNow = convertView.findViewById(R.id.btnCall);
                viewHolder.bookWorker = convertView.findViewById(R.id.btnBook);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.username.setText("Name : " + workerList.get(position).getWname());
            viewHolder.phone.setText("Phone : "+workerList.get(position).getPhone());
            viewHolder.area.setText("Area : "+workerList.get(position).getArea());
            viewHolder.city.setText("City : "+workerList.get(position).getCity());



            viewHolder.callNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+viewHolder.phone.getText().toString()));

                    if(ContextCompat.checkSelfPermission(WelcomeUser.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WelcomeUser.this,new String[]{Manifest.permission.CALL_PHONE,},1);
                    }
                    else{
                        try {
                            startActivity(i);
                        }catch(SecurityException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            viewHolder.bookWorker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String workerid = String.valueOf(workerList.get(position).getWid());
                    Intent intent = new Intent(WelcomeUser.this,BookingActivity.class);
                    intent.putExtra("workerid",workerid);

                    startActivity(intent);
                }
            });
            return convertView;
        }
    }


    class LoadUserBySearch extends AsyncTask<String,String,String>{
        ProgressDialog progressDialog ;
        String query;
        LoadUserBySearch(String query){
            this.query = query;
        }
        //        ArrayList<Blog> arrayList;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            workers = new ArrayList<>();
            progressDialog = new ProgressDialog(WelcomeUser.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        }



        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            WebServiceCall call = new WebServiceCall();
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("area",query);
            try {
                result = call.postData(GlobalURL.WORKER_SEARCH_URL,WebServiceCall.GET,hashMap);
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
                    Toast.makeText(WelcomeUser.this, "No Worker Found Try Nearby Area!!", Toast.LENGTH_SHORT).show();
                }
                for(int i = 0;i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Worker w = new Worker();
                    w.setWid(Integer.parseInt(jsonObject.getString("userid")));
                    w.setWname(jsonObject.getString("username"));
                    w.setPhone(jsonObject.getString("phone"));
                    w.setArea(jsonObject.getString("area"));
                    w.setCity(jsonObject.getString("city"));

                    workers.add(w);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(WelcomeUser.this, String.valueOf(workers.size()), Toast.LENGTH_SHORT).show();
            adapter = new WorkerAdapter(workers);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


    }



}