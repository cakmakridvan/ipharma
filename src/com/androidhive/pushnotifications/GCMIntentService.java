package com.androidhive.pushnotifications;

import static com.androidhive.pushnotifications.CommonUtilities.SENDER_ID;
import static com.androidhive.pushnotifications.CommonUtilities.displayMessage;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	
	 SharedPreferences app_preferences;
	 

	 
	 SharedPreferences.Editor editor;

    public GCMIntentService() {
        super(SENDER_ID);
    }
    
    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	super.onCreate();
    	
		//SharedPrefencesss
		app_preferences = getSharedPreferences("ex1", MODE_PRIVATE);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", MainActivity.name);
        ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
    	
    	//"message" for gokcen pushmessage
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("Par1"); //Kampanya_adı 
        String link = intent.getExtras().getString("Par2"); //Url
        
        String image_size;
        if(intent.getExtras().getString("Par3").isEmpty()){
        	
        	image_size = "0";
        	
        	
        	
        }
        else{
        	
        	image_size = intent.getExtras().getString("Par3");
        }
        
        String get_message_type = intent.getExtras().getString("MSG");
        
       JSONObject returndata;
	


		
        //SharedPrefencesss
        editor = app_preferences.edit();
        editor.putString("get_message_name", message);
        editor.putString("get_link_name", link);
        
        if(!intent.getExtras().getString("Par3").isEmpty()) editor.putString("get_image_size", image_size);
        	
        	
        	
        
        
        
        
        editor.commit();
		
	
        SharedPreferences prefs= getSharedPreferences("app_pdownload", context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.putString("get_message_ad", message); 
        editor2.putString("get_message_link", link); 
        editor2.commit();


        
        displayMessage(context, get_message_type);
        // notifies user
        generateNotification(context, get_message_type);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }

}
