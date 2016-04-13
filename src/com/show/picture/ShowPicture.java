package com.show.picture;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidhive.pushnotifications.MainActivity;


import com.androidhive.pushnotifications.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ShowPicture extends Activity {
	
	Timer timer;

	int userInteractionTimeout;
	
	String device_id;
	
	 String get_result;
	 
	   // Progress Dialog
	    private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showpicture);
		
		//Get Device IMEI number
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		device_id = telephonyManager.getDeviceId();
		Log.i("IMEI NUMBER", device_id);
		
		AsyncCallBanner_sendata_work send = new AsyncCallBanner_sendata_work();
		send.execute();
		
		

	}
	
	
	private class AsyncCallBanner_sendata_work extends AsyncTask<Void, Void, String> {

//		ProgressDialog dialog;

		@Override
		protected String doInBackground(Void... params) {
			Log.i("TAG", "doInBackground");
			Connect_send_data_work();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("TAG", "onPostExecute");
			// txt1.setText(Icerik);
			
			dialog.dismiss();
			
			
				
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			    
			
			
			
/*			else{
				
				Toast.makeText(getApplicationContext(), "Gitmedi", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}*/
			

		}

		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
			dialog = new ProgressDialog(ShowPicture.this);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.setMessage("Lütfen bekleyiniz");
			dialog.setIndeterminate(true);

   		dialog.show();
		}

	}
	
	
	private void Connect_send_data_work() {
		// TODO Auto-generated method stub

		JSONObject returndata = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://78.186.62.169:9666/svc.asmx/SendMessage");
		httppost.setHeader("Content-type", "application/json");
		JSONObject jsonparameter = new JSONObject();
		


		
		try {
				
			jsonparameter.put("Imei", device_id);
			jsonparameter.put("MSG", "CLIENT_UP_DEVICE");
			jsonparameter.put("Par1", "Live");
			jsonparameter.put("Par2", "UpDevice");
			
			
			
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		try {

			// jsonparameter.put("AnketID", "3");

			httppost.setEntity(new StringEntity(jsonparameter.toString(),
					"UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);

			Log.i("@banner_responseString", "" + responseString);

			try {
//				returndata = new JSONObject(responseString);
//				String d = returndata.get("d").toString();
//				
//				Icerik = d;
//				

				returndata = new JSONObject(responseString);
				

				
     			 get_result = returndata.optString("d");
     			
     			Log.i("IMEI NUMBER", ""+get_result);
//				
			
//				
			

//                String al = get_result;
				

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
		
	}


