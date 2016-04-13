package com.androidhive.pushnotifications;

import static com.androidhive.pushnotifications.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.androidhive.pushnotifications.CommonUtilities.EXTRA_MESSAGE;
import static com.androidhive.pushnotifications.CommonUtilities.SENDER_ID;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.webservis.DeleteFile;
import com.parse.webservis.DownloadImage;
import com.parse.webservis.DownloaderClassPicture;
import com.parse.webservis.ShowFile;
import com.show.picture.SetVolumeLevel;
import com.google.android.gcm.GCMRegistrar;
import com.show.picture.ShowPicture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MainActivity extends Activity implements OnClickListener {
	
	 Button btn;
	 
	SharedPreferences type_downlaod;
	SharedPreferences.Editor download_type;
	
	SharedPreferences app_preferences_name;
    SharedPreferences.Editor editor_name;
    
    SharedPreferences app_save_ping_time;
    SharedPreferences.Editor editorsave_ping_time;
	
	WakeLock wakeLock;
	
	Context context = this;
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0; 
	
	
	// label to display gcm messages
	TextView lblMessage;
	
	SharedPreferences prefs;
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Connection detector
	ConnectionDetector cd;
	
	public static String name;
	public static String email;
	
	String Icerik = ""; //Path
	String Icerik2 = ""; //name
	
	String get_url = "";
	String get_name = "";
	
	String get_name_main;
	
    String get_ping_time;
   
	
	 Button sound;
	
	 SharedPreferences app_preferences;
	 
	 SharedPreferences.Editor editor;
	 
	 File SDCardRootCheck_frontpage;
	 
	 File SDCardRootCheck;
	 
	 File SDCardRootCheck_video;
	 
	 File SDCardCheckMain_page;
	 
	 String get_result;
	 
     String device_id;
     
     String image_size;
     
	 int userInteractionTimeout;
	 
	 Timer timer_send;
	 
	 long img_size;
	 
	 long length_of_image;
	 
	 long length_of_imagem;
	 
	 boolean pressed = false;
	 
	 int convert_ping;
	 
	 Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		try {
			// REQUIRES ROOT

			Build.VERSION_CODES vc = new Build.VERSION_CODES();
			Build.VERSION vr = new Build.VERSION();
			String ProcID = "79"; // HONEYCOMB AND OLDER

			// v.RELEASE //4.0.3
			if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
				ProcID = "42"; // ICS AND NEWER
			}

			// REQUIRES ROOT
			Process proc = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							"service call activity " + ProcID
									+ " s16 com.android.systemui" }); // WAS
																		// 79
			proc.waitFor();

		} catch (Exception ex) {
			// Toast.LENGTH_LONG).show();
		}

		
		setContentView(R.layout.activity_main);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"Full Wake Lock");
		

		//Get Device IMEI number
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		device_id = telephonyManager.getDeviceId();
		Log.i("IMEI NUMBER", device_id);
		
		
		SDCardRootCheck_frontpage = new File("/mnt/sdcard/ipharmafrontpage/");
		if (!SDCardRootCheck_frontpage.exists()) {
			SDCardRootCheck_frontpage.mkdir();
		}
		
		
		cd = new ConnectionDetector(getApplicationContext());
		
		//SharedPrefencesss
		app_preferences_name = getSharedPreferences("ex_name", MODE_PRIVATE);
		
		//Shared Preferences get was saved main_name
		String take_name_main = app_preferences_name.getString("get_message_main_name","resim1.png");
		
	    // GET Ping Time
		app_save_ping_time = getSharedPreferences("ex_ping", MODE_PRIVATE);
		
		String take_ping_time = app_save_ping_time.getString("getpingtime", "1800");
		
		convert_ping = Integer.parseInt(take_ping_time);
		
		SDCardCheckMain_page = new File("/mnt/sdcard/" + take_name_main);
		
//		secret_btn = (Button) findViewById(R.id.btn_volume);
//		secret_btn.setOnClickListener(this);
//		secret_btn.setVisibility(View.VISIBLE);
//		secret_btn.setBackgroundColor(Color.TRANSPARENT);
		
		
		
		
		 if(take_name_main.contains("mp4")){
				
				
			VideoView vw = new VideoView(this);
			
			
			LinearLayout ll = (LinearLayout)findViewById(R.id.main_screen);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ll.addView(vw, lp);
			

            
            
            
			

			
			vw.setVideoPath("/mnt/sdcard/ipharmafrontpage/" + take_name_main );
			vw.start();

			// video finish listener
			vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) { // not playVideo
					// playVideo();

					mp.start();
					

				}
			});
		}
		 
		 else{
			
			ImageView img_showm = new ImageView(this);


		       img_showm.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


				   LinearLayout llm = (LinearLayout)findViewById(R.id.main_screen);
				   LayoutParams lpm = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				   llm.addView(img_showm, lpm);
				
					
					
					//Show image 
					File imageFilem = new File("/mnt/sdcard/ipharmafrontpage/" + take_name_main);
					Bitmap bitmapm = BitmapFactory.decodeFile(imageFilem.getAbsolutePath());
					img_showm.setImageBitmap(bitmapm);
			
			
		 }

		
/*		else{
			
			ImageView img_show = new ImageView(this);


		       img_show.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


				LinearLayout ll = (LinearLayout)findViewById(R.id.main_screen);
				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				ll.addView(img_show, lp);
				
					//Show image 
					File imageFile = new File("/sdcard/" + get_name_main);
					Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
					img_show.setImageBitmap(bitmap);
		}*/
	
		
//		sound = (Button) findViewById(R.id.btn_volume);
//		sound.setOnClickListener(this);
//		sound.setVisibility(View.VISIBLE);
//		sound.setBackgroundColor(Color.TRANSPARENT);

		// Check if Internet present
/*		if (!cd.isConnectingToInternet()) {
//			// Internet Connection is not present
//			alert.showAlertDialog(MainActivity.this,
//					"İnternet Bağlantı Hatası",
//					"Lütfen internet bağlantınızı kontrol ediniz", false);
//			// stop executing code by return
//			return;
			
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

				// set title
				alertDialogBuilder.setTitle("Hata!!");

				// set dialog message
				alertDialogBuilder
					.setMessage("iPharma uygulamasını kullanabilmeniz için internet bağlantınızın açık olması gerekmektedir.Lütfen internet ayarlarınızı kontrol ediniz...")
					.setCancelable(false)
					.setPositiveButton("Tamam",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					  });


					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
			new Thread(new Runnable() {
				  @Override
				  public void run() {
					  Toast.makeText(getApplicationContext(), "Lütfen internet ayarlarınızı kontrol ediniz...", Toast.LENGTH_SHORT);

				    // ...
				  }
				}).start();

			
	    	
			
			
		}*/
		

		/**
		 * Check device is working or not working send webservice
		 */
				timer_send = new Timer();
				timer_send.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if (userInteractionTimeout == convert_ping) {
							// Do your @Override
							
							//Go send to message of  Client Up 
							
/*							Intent send_ok = new Intent(getApplicationContext(),ShowPicture.class);
							send_ok.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(send_ok);*/
							
							Connect_send_data_work();
							
//							timer_send.cancel();
							
//							userInteractionTimeout = 0;
							
//							Toast.makeText(getApplicationContext(), "Gönderildi", Toast.LENGTH_SHORT).show();
							
						

						}
						userInteractionTimeout++;
					}
				}, 0, 1000);
				
				
	
		
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

//		lblMessage = (TextView) findViewById(R.id.lblMessage);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
//				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, name, email, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
		
		

		
//		VideoView mVideoView = (VideoView) findViewById(R.id.videoview);

/*		String uriPath = "android.resource://com.androidhive.pushnotifications/" + R.raw.ipharma_video;
		Uri uri = Uri.parse(uriPath);
		mVideoView.setVideoURI(uri);
		mVideoView.requestFocus();

		mVideoView.start();

		// video finish listener
		mVideoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// not playVideo
						// playVideo();

						mp.start();
					}
				});*/

	
	}		
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wakeLock.acquire();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		wakeLock.release();
	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			
//			newMessage = newMessage.toUpperCase();
			
			WakeLocker.acquire(getApplicationContext());
			
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			if(newMessage.equals("ali")){
			
			Intent send_push = new Intent(MainActivity.this, ShowPicture.class)
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
	startActivity(send_push);
	
			}
			
			else if(newMessage.equals("DOWNLOAD")){
				
				
//				Intent go_download_pic = new Intent(getApplicationContext(),DownloaderClassPicture.class);
//				startActivity(go_download_pic);
				
				//SharedPrefencesss
				prefs = getSharedPreferences("ex1",MODE_PRIVATE); 
				get_name = prefs.getString("get_message_name","Url alınamadı");
				get_url = prefs.getString("get_link_name","Name alınamadı");
				image_size = prefs.getString("get_image_size", "Size alınmadı");
				
				
		        //Save type of download and send AndroidDownloadFileByProgressBar
		        type_downlaod = getSharedPreferences("download_typem", context.MODE_WORLD_READABLE);
		        download_type = type_downlaod.edit();
		        download_type.putString("get_message_download", "backendpage"); 
		        download_type.commit();
				
				
				img_size = Long.parseLong(image_size);
				 
				 //get image size in Byte 
				 File file = new File("/mnt/sdcard/" + "/" + get_name);
				 length_of_image = file.length();
				 double d = (double)length_of_image;
				 
//				  retrunsKiloByte	from Byte	 
//                  double length = d/1024;
//
//
//                 double x = Math.ceil(length * 100) / 100;



			
				
				
				if(get_name.contains("mp4")){
					
					SDCardRootCheck_video = new File("/mnt/sdcard/videos/" + get_name);
					if (!SDCardRootCheck_video.exists()) {
						
					
							Log.i("getUrl_forDownload", get_url);
							
//							new DownloadFileFromURL().execute(get_url);
		
							Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.androidhive");
/*							launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//							launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//									| Intent.FLAG_ACTIVITY_NEW_TASK);
							launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/
							
							launchIntent
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
									);
							launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							
							timer_send.cancel();
							userInteractionTimeout = 0;
							startActivity(launchIntent);
							
						
						
						
//						Intent go_download_pic = new Intent(getApplicationContext(),DownloaderClassPicture.class);
//						go_download_pic.putExtra("EXIT", false);
//					    go_download_pic.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//						startActivity(go_download_pic);
						
					}else if(SDCardRootCheck_video.exists()){
						
						Toast.makeText(getBaseContext(), "Video Yüklü", Toast.LENGTH_SHORT).show();
					}
				}
				else{
				
				SDCardRootCheck = new File("/mnt/sdcard/" + "/" + get_name);
				if (!SDCardRootCheck.exists()) {
					
				
						Log.i("getUrl_forDownload", get_url);
						
//						new DownloadFileFromURL().execute(get_url);
	
						Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.androidhive");
						
//						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						launchIntent
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
								);

						
						
						timer_send.cancel();
						userInteractionTimeout = 0;
							
					
						startActivity(launchIntent);
						
//						finish();
					
					
//					Intent go_download_pic = new Intent(getApplicationContext(),AndroidDownloadFileByProgressBarActivity.class);
//
//					startActivity(go_download_pic);
					
				}else if(SDCardRootCheck.exists()){
					
					if(img_size == length_of_image && img_size != 0){
					
					       Toast.makeText(getBaseContext(), "Resim Yüklü", Toast.LENGTH_SHORT).show();
					       
					}
					else{
						
						Log.i("getUrl_forDownload", get_url);
						
//						new DownloadFileFromURL().execute(get_url);
	
						Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.androidhive");
						
//						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						
						
						launchIntent
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
								);
						
						
						timer_send.cancel();
						userInteractionTimeout = 0;
					
						startActivity(launchIntent);
					}
				}

     			

				}

				
				
//				Intent send_push = new Intent(MainActivity.this, DownloadImage.class)
//				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//						| Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(send_push);
				
//			AsyncCallBanner_Download banner = new AsyncCallBanner_Download();
//			
//			banner.execute();
				
//				Toast.makeText(getApplicationContext(), "BAşarılı", Toast.LENGTH_LONG).show();
				
			}
			
			
			
			else if(newMessage.equals("SHOW")){
				
				//SharedPrefencesss
				prefs = getSharedPreferences("ex1",MODE_PRIVATE); 
				get_name = prefs.getString("get_message_name","Url alınamadı");
				get_url = prefs.getString("get_link_name","Name alınamadı");
				
				
				
				
				if(get_name.contains("mp4")){
					
					SDCardRootCheck_video = new File("/mnt/sdcard/videos/" + get_name);
					if (!SDCardRootCheck_video.exists()) {
						
						Toast.makeText(getBaseContext(), "Video Yüklü Değil", Toast.LENGTH_SHORT).show();
					

						
						
//						Intent go_download_pic = new Intent(getApplicationContext(),DownloaderClassPicture.class);
//						go_download_pic.putExtra("EXIT", false);
//					    go_download_pic.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//						startActivity(go_download_pic);
						
					}else if(SDCardRootCheck_video.exists()){
						
						Log.i("getUrl_forDownload", get_url);
						
//						new DownloadFileFromURL().execute(get_url);
						
//						Connect_send_data();
						

	
						Intent send_show = new Intent(MainActivity.this, ShowFile.class);
						send_show.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						
/*						send_show
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
								);*/
						
						send_show.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						timer_send.cancel();
				        userInteractionTimeout = 0;
				        startActivity(send_show);
				        
				        
					}
					
				}
				
				else{
				

				
				
				
				SDCardRootCheck = new File("/mnt/sdcard/" + "/" + get_name);
				if (!SDCardRootCheck.exists()) {
					
					Toast.makeText(getBaseContext(), "Resim Yüklü Değil", Toast.LENGTH_SHORT).show();
					
				}else if(SDCardRootCheck.exists()){
					

				
				Intent send_show = new Intent(MainActivity.this, ShowFile.class);
				
				send_show.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
/*				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
						);*/
				timer_send.cancel();
				userInteractionTimeout = 0;
		startActivity(send_show);

//		finish();
				}
			  }
			}
			
			else if(newMessage.equals("DELETE")){
				
				
				//SharedPrefencesss
				prefs = getSharedPreferences("ex1",MODE_PRIVATE); 
				get_name = prefs.getString("get_message_name","Url alınamadı");
				
				if(get_name.contains("mp4")){
					
					SDCardRootCheck_video = new File("/mnt/sdcard/videos/" + get_name);
					if (!SDCardRootCheck_video.exists()) {
						
						Toast.makeText(getBaseContext(), "Video Bulunamadı Silinemez", Toast.LENGTH_SHORT).show();
					


						
					}else if(SDCardRootCheck_video.exists()){
						
						boolean deleted = SDCardRootCheck_video.delete();
						
						if(deleted == true){
							
							Toast.makeText(getApplicationContext(), "Video Dosyası silindi", Toast.LENGTH_SHORT).show();
						}
					}
					
				}
				
				else{
				
				SDCardRootCheck = new File("/mnt/sdcard/" + "/" + get_name);
				if (!SDCardRootCheck.exists()) {
					
					Toast.makeText(getBaseContext(), "Resim Bulunamadı Silinemez", Toast.LENGTH_SHORT).show();
					
				}else if(SDCardRootCheck.exists()){
				
					boolean deleted = SDCardRootCheck.delete();
				
				if(deleted == true){
					
					Toast.makeText(getApplicationContext(), "Resim Dosyası silindi", Toast.LENGTH_SHORT).show();
				}
				
				}
				
				  }
				
//				Intent send_delete = new Intent(MainActivity.this, DeleteFile.class)
//				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//						| Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(send_delete);
				
			}
			
			else if(newMessage.equals("APK")){
				
				
//				Intent go_download_pic = new Intent(getApplicationContext(),DownloaderClassPicture.class);
//				startActivity(go_download_pic);
				

				

//				Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.downloadapk");
//				
//				launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				
//				timer_send.cancel();
//					
//			
//				startActivity(launchIntent);
				
				Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.downloadapk");
				

				launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				timer_send.cancel();
				userInteractionTimeout = 0;
				startActivity(launchIntent);
//				android.os.Process.killProcess(android.os.Process.myPid());
				
				
			}
			
			else if(newMessage.equals("DOWNLOADMAIN")){
				
				//SharedPrefencesss
				prefs = getSharedPreferences("ex1",MODE_PRIVATE); 
				get_name_main = prefs.getString("get_message_name","Url alınamadı");
				get_url = prefs.getString("get_link_name","Name alınamadı");
				image_size = prefs.getString("get_image_size", "Size alınmadı");
				

				long img_sizem = Long.parseLong(image_size);
				
		        //SharedPrefencesss save to get_name_main
		        editor_name = app_preferences_name.edit();
		        editor_name.putString("get_message_main_name", get_name_main);
                editor_name.commit();
		        
		        
		        //Save type of download and send AndroidDownloadFileByProgressBar
		        type_downlaod = getSharedPreferences("download_typem", context.MODE_WORLD_READABLE);
		        download_type = type_downlaod.edit();
		        download_type.putString("get_message_download", "frontpage"); 
		        download_type.commit();
		        
		        
				
				
/*				img_size = Long.parseLong(image_size);
				 
				 //get image size in Byte 
				 File file = new File("/mnt/sdcard/" + "/" + get_name_main);
				 length_of_image = file.length();*/
				
				//  retrunsKiloByte	from Byte	 
                //  length = length/1024;
			

				 //get image size in Byte 
				 File file = new File("/mnt/sdcard/ipharmafrontpage/" + get_name_main);
				 length_of_imagem = file.length();
				 double d = (double)length_of_imagem;
				
				
				if(get_name_main.contains("mp4")){
					
					SDCardRootCheck_frontpage = new File("/mnt/sdcard/ipharmafrontpage/" + get_name_main);
					if (!SDCardRootCheck_frontpage.exists()) {
						
					
							Log.i("getUrl_forDownload", get_url);
							
//							new DownloadFileFromURL().execute(get_url);
		
							Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.androidhive");
							launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//							launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//									| Intent.FLAG_ACTIVITY_NEW_TASK);
							launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							timer_send.cancel();
							userInteractionTimeout = 0;
							startActivity(launchIntent);
							android.os.Process.killProcess(android.os.Process.myPid());
						
						
						
//						Intent go_download_pic = new Intent(getApplicationContext(),DownloaderClassPicture.class);
//						go_download_pic.putExtra("EXIT", false);
//					    go_download_pic.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//						startActivity(go_download_pic);
						
					}else if(SDCardRootCheck_frontpage.exists()){
						
						Toast.makeText(getBaseContext(), "Video Yüklü", Toast.LENGTH_SHORT).show();
					}
				}
				else{
				
				SDCardRootCheck_frontpage = new File("/mnt/sdcard/ipharmafrontpage/" + get_name_main);
				if (!SDCardRootCheck_frontpage.exists()) {
					
				
						Log.i("getUrl_forDownload", get_url);
						
//						new DownloadFileFromURL().execute(get_url);
	
						Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.androidhive");
						
						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						
						
						timer_send.cancel();
						userInteractionTimeout = 0;
					
						startActivity(launchIntent);
						android.os.Process.killProcess(android.os.Process.myPid());
						
//						finish();
					
					
//					Intent go_download_pic = new Intent(getApplicationContext(),AndroidDownloadFileByProgressBarActivity.class);
//
//					startActivity(go_download_pic);
					
				}else if(SDCardRootCheck_frontpage.exists()){
					
					if(img_sizem == length_of_imagem && img_sizem != 0){
					
					       Toast.makeText(getBaseContext(), "Resim Yüklü", Toast.LENGTH_SHORT).show();
					       
/*						     // Refresh main activity upon close of dialog box
					        Intent refresh = new Intent(MainActivity.this,MainActivity.class);
							refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							
							timer_send.cancel();
							userInteractionTimeout = 0;
					        startActivity(refresh);*/

					}
					else{
						
						Log.i("getUrl_forDownload", get_url);
						
//						new DownloadFileFromURL().execute(get_url);
	
						Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.androidhive");
						
						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						
						timer_send.cancel();
						userInteractionTimeout = 0;
							
					
						startActivity(launchIntent);
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}

     			

				}
				
			}
			
			
			
			else if(newMessage.equals("TERMINAL_SET_PING")){
				
				//SharedPrefencesss
				prefs = getSharedPreferences("ex1",MODE_PRIVATE); 
				get_ping_time = prefs.getString("get_message_name","Url alınamadı");

		        //SharedPrefencesss save to get_name_main
		        editorsave_ping_time = app_save_ping_time.edit();
		        editorsave_ping_time.putString("getpingtime", get_ping_time);

		        editorsave_ping_time.commit();

		        
		     // Refresh main activity upon close of dialog box
		        Intent refresh = new Intent(MainActivity.this,MainActivity.class);
				refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				
				timer_send.cancel();
				userInteractionTimeout = 0;
		        startActivity(refresh);
		        
		        Connect_send_ping_message_ok();
				
/*				Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.downloadapk");
				

				launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				timer_send.cancel();
				userInteractionTimeout = 0;
				startActivity(launchIntent);*/
//				android.os.Process.killProcess(android.os.Process.myPid());
				
				
			}
			

			
			// Showing received message
//			lblMessage.append(newMessage + "\n");			
//			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			// Releasing wake lock
			WakeLocker.release();
		}


	};
	
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

	
	
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

/*		switch (v.getId()) {
		case R.id.btn_volume:

			Intent pas = new Intent(getApplicationContext(),
					SetVolumeLevel.class);
			startActivity(pas);

			break;

		default:
			break;
		}*/
		
		


	}
	
    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case progress_bar_type: // we set this to 0
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
            return pDialog;
        default:
            return null;
        }
    }
	
	
	//////////////for DownloadFile
	
	

	class DownloadFileFromURL extends AsyncTask<String, String, String> {
		 
	    /**
	     * Before starting background thread
	     * Show Progress Bar Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        showDialog(progress_bar_type);
	    }
	 
	    /**
	     * Downloading file in background thread
	     * */
	    @Override
	    protected String doInBackground(String... f_url) {
	        int count;
	        try {
	            URL url = new URL(f_url[0]);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	            int lenghtOfFile = conection.getContentLength();
	 
	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	 
	            // Output stream to write file
	            OutputStream output = new FileOutputStream("/sdcard/" + get_name); 
	 
	            byte data[] = new byte[1024];
	 
	            long total = 0;
	 
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
	                publishProgress(""+(int)((total*100)/lenghtOfFile));
	 
	                // writing data to file
	                output.write(data, 0, count);
	            }
	 
	            // flushing output
	            output.flush();
	 
	            // closing streams
	            output.close();
	            input.close();
	 
	        } catch (Exception e) {
	            Log.e("Error: ", e.getMessage());
	        }
	 
	        return null;
	    }
	 
	    /**
	     * Updating progress bar
	     * */
	    protected void onProgressUpdate(String... progress) {
	        // setting progress percentage
	        pDialog.setProgress(Integer.parseInt(progress[0]));
	   }
	 
	    /**
	     * After completing background task
	     * Dismiss the progress dialog
	     * **/
	    @Override
	    protected void onPostExecute(String file_url) {
	        // dismiss the dialog after the file was downloaded
	        dismissDialog(progress_bar_type);
	 
	        // Displaying downloaded image into image view
	        // Reading image path from sdcard
//	        String imagePath = Environment.getExternalStorageDirectory().toString() + "/"+ get_name + ".jpg";
	        // setting downloaded into image view
//	        my_image.setImageDrawable(Drawable.createFromPath(imagePath));
	    }
	 
	}

	
	
	//////////////////////
	
	/**
	 * For Sending data to device is working now
	 */
	
	
	private class AsyncCallBanner_sendata_work extends AsyncTask<Void, Void, String> {

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
			Log.i("Last value of Icerik:", "" + Icerik);
//			dialog.dismiss();
			
			if(get_result.equals("True")){
				
				userInteractionTimeout = 0;
			    
			
			}
			

		}

		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
//			dialog = new ProgressDialog(MainActivity.this);
//			dialog.setCanceledOnTouchOutside(false);
//			dialog.setCancelable(false);
//			dialog.setMessage("Lütfen bekleyiniz");
//
//			dialog.setIndeterminate(true);
//
//			dialog.show();
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
	
	
	private void Connect_send_ping_message_ok() {
		// TODO Auto-generated method stub

		JSONObject returndata = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://78.186.62.169:9666/svc.asmx/SendMessage");
		httppost.setHeader("Content-type", "application/json");
		JSONObject jsonparameter = new JSONObject();
		


		
		try {
				
			jsonparameter.put("Imei", device_id);
			jsonparameter.put("MSG", "TERMINAL_SET_PING_OK");
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

	
	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		super.onUserInteraction();

//		userInteractionTimeout = 0;

		// Log.d(LOG_TAG,"User Interaction : "+userInteractionTimeout);

	}


    SimpleTwoFingerDoubleTapDetector multiTouchListener = new SimpleTwoFingerDoubleTapDetector() {
        @Override
        public void onTwoFingerDoubleTap() {
            // Do what you want here, I used a Toast for demonstration
//            Toast.makeText(MainActivity.this, "Two Finger Double Tap", Toast.LENGTH_SHORT).show();
        	
			Intent pas = new Intent(getApplicationContext(),
					SetVolumeLevel.class);
			pas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			timer_send.cancel();
			userInteractionTimeout = 0;
			startActivity(pas);
        }
    };

    // Override onCreate() and anything else you want

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(multiTouchListener.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }
}
	




