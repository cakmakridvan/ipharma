package com.parse.webservis;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidhive.pushnotifications.MainActivity;
import com.androidhive.pushnotifications.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class ShowFile extends Activity {
	
	String get_name = ""; // Name
	String get_sure = ""; //Sure
	HttpGet httpget;
	String jsn = "";
	

	
	TextView txt_image;
	
	ImageView img_show;
	
	Button btnt;
	
	Timer timer;
	Timer timer2;
	Timer timer3;

	int userInteractionTimeout;
	int userInteractionTimeout2;
	int userInteractionTimeout3;
	
	int sure ;
	
	SharedPreferences prefs;
	
	File SDCardRootCheck;
	
	String device_id;
	
	String get_result;
	

	
	// Progress Dialog
	public static final int progress_bar_type = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showfile);
		
		timer = new Timer();
		timer2 = new Timer();
		timer3 = new Timer();
		
		//SharedPrefencesss
		prefs = getSharedPreferences("ex1",MODE_PRIVATE); 
		get_name = prefs.getString("get_message_name","Url alınamadı");
		get_sure = prefs.getString("get_link_name","Name alınamadı");
		
		
		onPause();
		onUserInteraction();
		
		//Get Device IMEI number
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		device_id = telephonyManager.getDeviceId();
		Log.i("IMEI NUMBER", device_id);
		Log.i("IMEI NUMBER", get_name);
		
/*		try {
		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		    r.play();
		} catch (Exception e) {
		    e.printStackTrace();
		}*/
		
		
		if(get_name.contains("mp4")){
			
			VideoView vw = new VideoView(this);
			
			
			LinearLayout ll = (LinearLayout)findViewById(R.id.show_file);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ll.addView(vw, lp);
			
			/*
			 * Send Show message to service and send ping message to service
			 */
			
			timer3 = new Timer();
			timer3.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (userInteractionTimeout3 == 1) {

						Connect_send_data_video();
						Connect_send_data_work();
						
//						AsyncCallBanner_sendata_video dene_video = new AsyncCallBanner_sendata_video();
//						dene_video.execute();
						

					}
					userInteractionTimeout3++;
				}
			}, 0, 1000);
			
			userInteractionTimeout3 = 0;
			
//			Connect_send_data_video();
//			Connect_send_data_work();
			
			vw.setVideoPath("/mnt/sdcard/videos/" + get_name );
			vw.start();

			

			
			// video finish listener
			vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) { // not playVideo
					// playVideo();

//					mp.start();
					
//					AsyncCallBanner_sendata_video dene_video = new AsyncCallBanner_sendata_video();
//					dene_video.execute();
					

					
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					

				}
			});
		}
		
		else{
			
/*			ArrayList<String> list = new ArrayList<String>();
			list.add(get_name);
			
			SharedPreferences prefs=this.getSharedPreferences("yourPrefsKey",Context.MODE_PRIVATE);
			Editor edit=prefs.edit();

			Set<String> setm = new HashSet<String>();
			setm.addAll(list);
			edit.putStringSet("yourKey", setm);
			edit.commit();*/
			

			
		
		Log.i("get_url_Show_File", get_sure);
		
	 	//CONVERTİNg PROCESS STRİNG TO İNT
	 try {
		sure = NumberFormat.getInstance().parse(get_sure).intValue();
		
//		Connect_send_data();
//		Connect_send_data_work();
		
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
		timer2 = new Timer();
		timer2.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (userInteractionTimeout2 == 1) {

					Connect_send_data();
					Connect_send_data_work();
					

				}
				userInteractionTimeout2++;
			}
		}, 0, 1000);

		ImageView img_show = new ImageView(this);
		userInteractionTimeout2 = 0;

       img_show.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


		LinearLayout ll = (LinearLayout)findViewById(R.id.show_file);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll.addView(img_show, lp);
		
//		img_show = (ImageView) findViewById(R.id.img_photo_show);

		
			
//					AsyncCallBanner banner = new AsyncCallBanner();
//					
//						banner.execute();
		

			
			
			
			//Show image 
			File imageFile = new File("/sdcard/" + get_name);
			Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			img_show.setImageBitmap(bitmap);
			
			/*
			 * Send Show Message to Service and send ping to service to device is Active
			 */

			

/*	        TranslateAnimation animation = new TranslateAnimation(-500.0f, 100.0f, 0.0f, 0.0f); //new TranslateAnimation(xFrom,xTo, yFrom,yTo)

		        animation.setDuration(2000);  // animation duration

		        animation.setRepeatCount(1);  // animation repeat count

		        animation.setRepeatMode(1);   // repeat animation (left to right, right to left )

		        animation.setFillAfter(true);     

		        img_show.startAnimation(animation);*/
			
			
			RotateAnimation rotate
			  = new RotateAnimation(0.0f, 1080.0f,
			                        Animation.RELATIVE_TO_SELF, 0.5f,
			                        Animation.RELATIVE_TO_SELF, 0.5f);
			
			
			ScaleAnimation scale
			  = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
			                       Animation.RELATIVE_TO_SELF, 0.5f,
			                       Animation.RELATIVE_TO_SELF, 0.5f);
			
			AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
			
			AnimationSet set = new AnimationSet(true);
			set.addAnimation(rotate);
			set.addAnimation(scale);
			set.addAnimation(alpha);
			set.setDuration(2000);
			
			
			img_show.startAnimation(set);
			


			
			
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (userInteractionTimeout == sure) {
						// Do your @Override
						
//						AsyncCallBanner_sendata dene = new AsyncCallBanner_sendata();
//						dene.execute();
						
//						Connect_send_data();
//						Connect_send_data_work();
						
						Intent intent = new Intent(getApplicationContext(),
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);
						
						timer.cancel();
						
						android.os.Process.killProcess(android.os.Process.myPid());

					}
					userInteractionTimeout++;
				}
			}, 0, 1000);

			
		}			

			


	}
	

	
	private Target target = new Target() {
		   
	    @Override
	   public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
	      new Thread(new Runnable() {
	         
	          @Override
	         public void run() {

	            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/"+ get_name);
	            try {
	               file.createNewFile();
	               FileOutputStream ostream = new FileOutputStream(file);
	               bitmap.compress(CompressFormat.JPEG, 80, ostream);
	               ostream.close();
	            } catch (Exception e) {
	               e.printStackTrace();
	            }

	         }
	      }).start();
	   }
	   




	@Override
	public void onBitmapFailed(Drawable arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onPrepareLoad(Drawable arg0) {
		// TODO Auto-generated method stub
		
	}
	};
	


	/**
	 * For Sending data to picture is showed
	 */
	
	
	private class AsyncCallBanner_sendata extends AsyncTask<Void, Void, String> {

//		ProgressDialog dialog;

		@Override
		protected String doInBackground(Void... params) {
			Log.i("TAG", "doInBackground");
			Connect_send_data();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("TAG", "onPostExecute");
			// txt1.setText(Icerik);
			Log.i("Last value of Icerik:", "" + get_name);
//			dialog.dismiss();
			
//			if(get_result.equals("True")){
//				
//				Toast.makeText(getApplicationContext(), "Gönderildi", 3000).show();
//				
//				Intent intent = new Intent(getApplicationContext(),
//						MainActivity.class);
//				intent.putExtra("EXIT", false);
////				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//				
//				
//			}
//			
//			else{
//				
//				Intent intent_ot = new Intent(getApplicationContext(),
//						MainActivity.class);
//				intent_ot.putExtra("EXIT", false);
//				intent_ot.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent_ot);
//				
//				Toast.makeText(getApplicationContext(), "Gönderim Başarısız", 3000).show();
//			}
		}

		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
//			dialog = new ProgressDialog(ShowFile.this);
//			dialog.setCanceledOnTouchOutside(false);
//			dialog.setCancelable(false);
//			dialog.setMessage("Lütfen bekleyiniz");
//
//			dialog.setIndeterminate(true);
//
//			dialog.show();
		}

	}
	
	
	private void Connect_send_data() {
		// TODO Auto-generated method stub

		JSONObject returndata = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://78.186.62.169:9666/svc.asmx/SendMessage");
		httppost.setHeader("Content-type", "application/json");
		JSONObject jsonparameter = new JSONObject();
		


		
		try {
				
			jsonparameter.put("Imei", device_id);
			jsonparameter.put("MSG", "CLIENT_SHOW_SOURCE");
			jsonparameter.put("Par1", get_name);
			jsonparameter.put("Par2", "Show");
			
			
			
			
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
 
			}

		} catch (Exception e) {

		}

	}
	
	
	/**
	 * For Sending data to video is showed
	 */
	
	
	/*
	 * For Send message device is up
	 */
	
	
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
     			

     			
//     			if(get_result.equals("True")){
     				
     				userInteractionTimeout = 0;
     				
/*         			runOnUiThread(new Runnable(){
         				public void run() {
         				   
         					Toast.makeText(getApplicationContext(), "Gönderildi", Toast.LENGTH_SHORT).show();
         				}
         				});*/
//     			}
//     			else{
     				
/*         			runOnUiThread(new Runnable(){
         				public void run() {
         				   
         					Toast.makeText(getApplicationContext(), "Gönderilemedi!!!", Toast.LENGTH_SHORT).show();
         				}
         				});*/
     				
     				
//     			}
//				
			
//				
			

//                String al = get_result;
				

			} catch (JSONException e) {
 
			}

		} catch (Exception e) {

		}

	}
	
	
	private class AsyncCallBanner_sendata_video extends AsyncTask<Void, Void, String> {

//		ProgressDialog dialog;

		@Override
		protected String doInBackground(Void... params) {
			Log.i("TAG", "doInBackground");
			Connect_send_data_video();
			Connect_send_data_work();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("TAG", "onPostExecute");
			// txt1.setText(Icerik);
			Log.i("Last value of Icerik:", "" + get_name);
//			dialog.dismiss();
			
//			if(get_result.equals("True")){
//				
//				Toast.makeText(getApplicationContext(), "Gönderildi", 3000).show();
//				

				
//				
//				
//			}
//			
//			else{
//				
//				Intent intent_ot = new Intent(getApplicationContext(),
//						MainActivity.class);
//				intent_ot.putExtra("EXIT", false);
//				intent_ot.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent_ot);
//				
//				Toast.makeText(getApplicationContext(), "Gönderim Başarısız", 3000).show();
//			}
		}

		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
//			dialog = new ProgressDialog(ShowFile.this);
//			dialog.setCanceledOnTouchOutside(false);
//			dialog.setCancelable(false);
//			dialog.setMessage("Lütfen bekleyiniz");
//
//			dialog.setIndeterminate(true);
//
//			dialog.show();
		}

	}
	
	private void Connect_send_data_video() {
		// TODO Auto-generated method stub

		JSONObject returndata = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://78.186.62.169:9666/svc.asmx/SendMessage");
		httppost.setHeader("Content-type", "application/json");
		JSONObject jsonparameter = new JSONObject();
		


		
		try {
				
			jsonparameter.put("Imei", device_id);
			jsonparameter.put("MSG", "CLIENT_SHOW_SOURCE");
			jsonparameter.put("Par1", get_name);
			jsonparameter.put("Par2", "Show");
			
			
			
			
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
 
			}

		} catch (Exception e) {

		}

	}
	
	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		super.onUserInteraction();

		userInteractionTimeout = 0;

		// Log.d(LOG_TAG,"User Interaction : "+userInteractionTimeout);

	}
	
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        timer.cancel();
        
    }

	
}
