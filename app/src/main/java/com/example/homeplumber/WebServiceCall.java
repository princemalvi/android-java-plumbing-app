package com.example.homeplumber;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WebServiceCall {
    String URL;
    HashMap<String,String> hashMap;
    public static final int GET = 1;
    public static final int POST = 2;
    URL url;
    HttpURLConnection conn;
    StringBuilder result;
    StringBuilder buildParam ;

    public WebServiceCall(){

    }
    public String postData(String strURL,int type,HashMap<String,String> postDataParams) throws Exception{
        buildParam = new StringBuilder();
        buildParam = getPostDataString(postDataParams);

        if(type== GET){
            if (buildParam.length() != 0) {
                strURL += "?" + buildParam.toString();
            }
            try {
                url = new URL(strURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(false);//if true then body send via post method
                conn.setDoInput(true); // if true that means server response something
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            response = new StringBuilder();
//            url = new URL(strURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String strLine = null;
//                while ((strLine = input.readLine()) != null) {
//                    response.append(strLine);
//                }
//                input.close();
//            }
        }else if(type == POST){
//            String response ="";
            try {
                url = new URL(strURL);

                Log.d("service 1", strURL);
                Log.d("service 2", buildParam.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                Log.d("service 1", "3");
                conn.setRequestMethod("POST");
                Log.d("service 1", "4 ");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Log.d("service 1", " 5 ");

                //            OutputStream os = conn.getOutputStream();
                Log.d("service 1", "6");
                //            DataOutputStream writer = new DataOutputStream(os);
                //            writer.writeBytes(buildParam.toString());

                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
                BufferedWriter writer = new BufferedWriter(os);
                writer.write(buildParam.toString());
                writer.flush();
                writer.close();
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Result is = "+result.toString());
        return result.toString();
    }

    private static StringBuilder getPostDataString(HashMap<String,String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String,String> entry : params.entrySet()){
            if(first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }
        return result;
    }
}
