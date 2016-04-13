package com.parse.webservis;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


























import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




























import com.androidhive.pushnotifications.MainActivity;
import com.androidhive.pushnotifications.R;








import com.squareup.picasso.Picasso;








import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadImage extends Activity {
	
	String Icerik = ""; // Url
	String Icerik2 = ""; //Name
	HttpGet httpget;
	String jsn = "";
	
	File SDCardRootCheck;
	
	TextView txt_image;
	
	ImageView img;
	
	Button btnt;
	
	String imgURL="http://4.bp.blogspot.com/-GDG0jraKnFI/TzZHQ5At0zI/AAAAAAAAAEY/TPl4U6vm5lw/s1600/android-example-code-demo-program.jpg";
	
	// Progress Dialog
	public static final int progress_bar_type = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloadd);
		
		
	

		
		txt_image = (TextView) findViewById(R.id.txt_url_image);
		img = (ImageView) findViewById(R.id.img_resim);
		btnt = (Button) findViewById(R.id.btn);
		
			
					AsyncCallBanner banner = new AsyncCallBanner();
					banner.execute();
					

			
			


	}
	
	private class AsyncCallBanner extends AsyncTask<Void, Void, String> {
		
		

		ProgressDialog dialog;

		@Override
		protected String doInBackground(Void... params) {
			Log.i("TAG", "doInBackground");
			Connect();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("TAG", "onPostExecute");
//			 txt1.setText(Icerik);
			dialog.dismiss();
			txt_image.setText(Icerik);
			
//			Picasso.with(getApplicationContext())
//            .load("http://i.imgur.com/DvpvklR.png")
//            .into(img);

			
			Log.i("@Log", Icerik);
			
			
			
			
			//Save Image
			Picasso.with(getApplicationContext())
			.load(Icerik)
			.into(target);
			

			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.putExtra("EXIT", false);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
			
			
			//Show image 
//			File imageFile = new File("/sdcard/" + Icerik2 + ".jpg");
//			Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//			img.setImageBitmap(bitmap);
			
//			DownloadAndReadImage dImage= new DownloadAndReadImage(imgURL,1);
//			
//
//	        img.setImageBitmap(dImage.getBitmapImage());
			
//			
//			    File direct = new File(Environment.getExternalStorageDirectory()
//			            + "/anketapk");
//
//			    if (!direct.exists()) {
//			        direct.mkdirs();
//			    }
//
//			    DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
//
//			    Uri downloadUri = Uri.parse(Icerik);
//			    DownloadManager.Request request = new DownloadManager.Request(
//			            downloadUri);
//
//			    request.setAllowedNetworkTypes(
//			            DownloadManager.Request.NETWORK_WIFI
//			                    | DownloadManager.Request.NETWORK_MOBILE)
//			            .setAllowedOverRoaming(false).setTitle("Demo")
//			            .setDescription("Something useful. No, really.")
//			            .setDestinationInExternalPublicDir("/AnhsirkDasarpFiles", "fileName.jpg");
//
//			    mgr.enqueue(request);

			
			
			
		}

		@Override
		protected void onPreExecute() {
			Log.i("TAG", "onPreExecute");
			dialog = new ProgressDialog(DownloadImage.this);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.setMessage("Lütfen bekleyiniz");

			dialog.setIndeterminate(true);

			dialog.show();
		}

	}
	
	private Target target = new Target() {
		   
	    @Override
	   public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
	      new Thread(new Runnable() {
	         
	          @Override
	         public void run() {

	            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/"+ Icerik2 + ".jpg");
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
	




	private void Connect() {
		// TODO Auto-generated method stub

		JSONObject returndata = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://78.186.62.169:9666/svc.asmx/getImage");
		httppost.setHeader("Content-type", "application/json");
		JSONObject jsonparameter = new JSONObject();
		
		try {
			jsonparameter.put("EventID", "4");
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
				JSONObject get_d = returndata.getJSONObject("d");
				
				String get_path = get_d.getString("Path");
				
				String get_name = get_d.getString("Name");

                Icerik = get_path;
                Icerik2 = get_name;
				

			} catch (JSONException e) {
 
			}

		} catch (Exception e) {

		}

	}


}
