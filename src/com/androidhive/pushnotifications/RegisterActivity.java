package com.androidhive.pushnotifications;

import static com.androidhive.pushnotifications.CommonUtilities.SENDER_ID;
import static com.androidhive.pushnotifications.CommonUtilities.SERVER_URL;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Internet detector
	ConnectionDetector cd;
	
	// UI elements
	EditText txtName;
	EditText txtEmail;
	
	// Register button
	Button btnRegister;
	
	final String PREFS_NAME = "MyPrefsFile";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

if (settings.getBoolean("my_first_time", true)) {
    //the app is being launched for first time, do something        
    Log.d("Comments", "First time");

             // first time task
    
	setContentView(R.layout.activity_register);
	
	cd = new ConnectionDetector(getApplicationContext());

	// Check if Internet present
	if (!cd.isConnectingToInternet()) {
		// Internet Connection is not present
		alert.showAlertDialog(RegisterActivity.this,
				"Internet Connection Error",
				"Please connect to working Internet connection", false);
		// stop executing code by return
		return;
	}

	// Check if GCM configuration is set
	if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
			|| SENDER_ID.length() == 0) {
		// GCM sernder id / server url is missing
		alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
				"Please set your Server URL and GCM Sender ID", false);
		// stop executing code by return
		 return;
	}
	
	txtName = (EditText) findViewById(R.id.txtName);
	txtEmail = (EditText) findViewById(R.id.txtEmail);
	btnRegister = (Button) findViewById(R.id.btnRegister);
	
	/*
	 * Click event on Register button
	 * */
	btnRegister.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// Read EditText dat
			String name = txtName.getText().toString();
			String email = txtEmail.getText().toString();
			
			// Check if user filled the form
			if(name.trim().length() > 0 && email.trim().length() > 0){
				// Launch Main Activity
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				
				// Registering user on our server					
				// Sending registraiton details to MainActivity
				i.putExtra("name", name);
				i.putExtra("email", email);
				startActivity(i);
				finish();
			}else{
				// user doen't filled that data
				// ask him to fill the form
				alert.showAlertDialog(RegisterActivity.this, "Registration Error!", "Please enter your details", false);
			}
		}
	});
    

    // record the fact that the app has been started at least once
    settings.edit().putBoolean("my_first_time", false).commit(); 
}

else{
	
	Intent i = new Intent(getApplicationContext(), MainActivity.class);
	startActivity(i);
}
		

	}

}
