package com.jb.Android.JBSMS_GP;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


/***********************
* 簡訊Receiver
***********************/
public class mSMSReceiver extends BroadcastReceiver
{
  //private static final int RECEIVE_NEW_SMS = 1;          //取得新簡訊(mSMSReceiver)的Activity參數 
  public static final String BROATCAST_ACTION_ID = "NEW_SMS"; 
  public static final String smsACTION = "android.provider.Telephony.SMS_RECEIVED";
	 
  @Override
  public void onReceive(Context context, Intent intent)
  {
    try
    {
	    if (intent.getAction().equals(smsACTION))
	    {
	      //StringBuilder sFromPNum = new StringBuilder();
	      String sFromPNum = "";
	      StringBuilder sMySms = new StringBuilder();
	      Bundle bundle = intent.getExtras();
	      
	      if (bundle != null)
	      {
	        Object[] myOBJpdus = (Object[]) bundle.get("pdus");
	        SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
	        for (int i = 0; i<myOBJpdus.length; i++)
	        {
	          messages[i] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[i]);
	        }
	        
	        
	        for (SmsMessage currentMessage : messages)
	        {
	        	//取得寄送者電話號碼
	        	//sFromPNum.append(currentMessage.getDisplayOriginatingAddress());
	        	sFromPNum = currentMessage.getDisplayOriginatingAddress();
	        	//取得所有簡訊內容
	        	sMySms.append(currentMessage.getDisplayMessageBody());
	        }
	        
	        try
	        {
	        	/*For Debug*/
	        	Android_SMSvc.Svc_Sts_Desc = "接收到簡訊...";
	    		Android_SMSvc.updateServiceStatus();
	  	      
	    		
	    		Log.v("JB_TAG","收到簡訊內容:"+sFromPNum + sMySms);
	    		
	    		/* **********************************************
	    		 * 收到簡訊，夾參數再叫起 Android_SMSbs 這支Package
	    		 * **********************************************/
	    		Intent mBootIntent = new Intent(context, Android_SMSbs.class); 
	    		mBootIntent.setAction(BROATCAST_ACTION_ID);
	    		mBootIntent.putExtra("STR_PARAM01", sFromPNum.toString());
	    		mBootIntent.putExtra("STR_PARAM02", sMySms.toString());
	    	    // Set the Launch-Flag to the Intent. 
	    	    mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	    // Send the Intent to the OS.  
	    	    context.startActivity(mBootIntent);
	        }
	        catch (Exception e)
	        {
	        	Log.e("JB_TAG","錯誤:"+e.getMessage());
	        }
	        
	        
	      }
	    } //end if
    }
    catch(Exception ex)
    {
    	Log.e("JB_TAG", "錯誤:"+ex.getMessage());
    }
    
  }
}