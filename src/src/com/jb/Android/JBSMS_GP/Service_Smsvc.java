package com.jb.Android.JBSMS_GP;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import com.google.android.maps.GeoPoint;




public class Service_Smsvc extends Service
{
    //簡訊接收者及參數
	private mSMSReceiver mReceiver01  = null;
	public static final String smsACTION = "android.provider.Telephony.SMS_RECEIVED";
	//Handler
	private Handler handler    = new Handler();
	private Handler handler_log    = new Handler();
	
	//電源管理
	private PowerManager.WakeLock wakeLock = null;
	
	//SQLite
	private MyDataBaseAdapter myadp = null;
	
	private String Log_sms = "";
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		//寫LogCat -------------------------------------------------
		Android_SMSvc.Svc_Sts_Desc = "啟用系統服務...";
		Android_SMSvc.updateServiceStatus();
	    
		//服務開始後，註冊一個mSMSReceiver --------------------------
	    IntentFilter mFilter01 = new IntentFilter(smsACTION);
	    mReceiver01 = new mSMSReceiver();
	    registerReceiver(mReceiver01, mFilter01);
	    
	    
	    //Disable Sleeping Mode
	    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Service_Smsvc.class.getName());		    
        wakeLock.acquire();
        
        Log.v("JB_TAG","簡訊自動發送服務已關閉睡眠模式!");
	    
	    
	    // 開啟已經存在的資料庫 --------------------------------------
		myadp=new MyDataBaseAdapter(this);
		myadp.open();
	    
	    //開Thread 檢查是否有簡訊事件---------------------------------
	    mTask.run();
	    
		super.onCreate();
 
	}
	
	//private int cnt=60;
    private Runnable mTask =new Runnable()
	{
		public void run() 
	    {
			//Log.v("JB_TAG",cnt+" 後檢查是否有簡訊事件...");
			//handler.postDelayed(this, 1000);
			handler.postDelayed(this, 60000);
			Get_SmsEvent();

			/*
			cnt--;
			if(cnt==0)
			{
				cnt=60;
				Log.v("JB_TAG","檢查簡訊事件中...");
				Get_SmsEvent();
				
			}
			*/
	    }
	};
	
	
	/************************************
	 * Function Name:Get_NowTime
	 * Input:
	 * Output:YYYYMMDDHHmmss
	 * Description: 取得目前系統時間
	 *************************************/
	public String Get_NowTime()
	{
		AlarmManager alarm = (AlarmManager)getSystemService(Android_SMSvc.ALARM_SERVICE);
		alarm.setTimeZone("Asia/Taipei");
		
		
		//取得目前系統 UTC (GMT) 時間--------------------------------
		long time=System.currentTimeMillis();
		Calendar mCalendar=Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		//取得年
		int mYear=mCalendar.get(Calendar.YEAR);
		//取得月
		int mMon=mCalendar.get(Calendar.MONTH); //0:一月, 1:二月, 2:三月 ...
		mMon++;
		//取得日
		int mDay=mCalendar.get(Calendar.DATE);
		//取得小時
		int mHour=mCalendar.get(Calendar.HOUR_OF_DAY); //HOUR_OF_DAY為24小時制
		//取得分鐘
		int mMin=mCalendar.get(Calendar.MINUTE);
		
		String NowTime=mYear+Common.PadLeft(Integer.toString(mMon),'0',2)+
		                     Common.PadLeft(Integer.toString(mDay),'0',2)+
		                     Common.PadLeft(Integer.toString(mHour),'0',2)+
		                     Common.PadLeft(Integer.toString(mMin),'0',2);
		
		return NowTime;
	}
	
	
	private int SendLogCnt=20;
    private Runnable logTask =new Runnable()
	{
		public void run()
	    {
			Log.v("JB_TAG",SendLogCnt+" 後回傳訊息Log...");
			handler_log.postDelayed(this, 1000);
			SendLogCnt--;
			if(SendLogCnt==0)
			{
				Send_Log_Back();
				SendLogCnt=20;
			}
	    }
	};
	
	/********************************************
	 * Function Name: CheckSetup_Send_GEO
	 * Input:
	 * Output: true/false
	 * Description: 檢查是否要傳送地理作標
	 *********************************************/
	private boolean CheckSetup_Send_GEO()
	{
	  try
	  {
		Cursor cur_Setup = myadp.fetch_USERSETUP_Data(Android_SMSvc.OPTION_SEND_GEO_STR);
			
		if (cur_Setup != null && cur_Setup.getCount() > 0)
	  	{
			cur_Setup.moveToFirst();
	  		int SendGeo_flg = cur_Setup.getInt(2);
	  		cur_Setup.close();
	  		
	  		switch(SendGeo_flg)
	  		{
	  		case 0:
	  			return false;
	  		case 1: 
	  			return true;
	  		default:
	  			return false;
	  		}
	  	}//end if
	  	else
	  	{
	  		return false;
	  	}
	  }
	  catch(Exception ex)
	  {
		  Log.e("JB_TAG","錯誤："+ex.getMessage());
		  return false;
	  }
		
	}
	/********************************************
	 * Function Name: CheckSetup_Send_Notify
	 * Input:
	 * Output: true/false
	 * Description: 檢查是否要發送通知
	 *********************************************/
	private boolean CheckSetup_Send_Notify()
	{
	  try
	  {
		Cursor cur_Setup = myadp.fetch_USERSETUP_Data(Android_SMSvc.OPTION_SEND_NOTIFICATION_STR);
			
		if (cur_Setup != null && cur_Setup.getCount() > 0)
	  	{
			cur_Setup.moveToFirst();
	  		int SendNotify_flg = cur_Setup.getInt(2);
	  		cur_Setup.close();
	  		
	  		switch(SendNotify_flg)
	  		{
	  		case 0:
	  			return false;
	  		case 1: 
	  			return true;
	  		default:
	  			return false;
	  		}
	  	}//end if
	  	else
	  	{
	  		return false;
	  	}
	  }
	  catch(Exception ex)
	  {
		  Log.e("JB_TAG","錯誤："+ex.getMessage());
		  return false;
	  }
		
	}
	/********************************************
	 * Function Name: Send_Log_Back
	 * Input:
	 * Output:
	 * Description: 傳送Log訊息
	 *********************************************/
	private void Send_Log_Back()
	{
	  Cursor cur_log = myadp.fetch_LOGCONTACT_AllData();
	

  	  if (cur_log != null && cur_log.getCount() > 0)
  	  {
  		cur_log.moveToFirst();
  		//String SendLog_name = cur_log.getString(0);
  		String SendLog_phone = cur_log.getString(1);
  		int SendLog_flg = Integer.parseInt(cur_log.getString(2));
	  
  		if(SendLog_flg==1) //再次確定使用者是否有 開啟自動發送紀錄簡訊
  		{
  			Send_SMS_To(SendLog_phone, Log_sms);
  		}
  		else
  		{
  			//Log.v("JB_TAG","沒有開啟自動發送紀錄簡訊...");
  		}
	  
  		cur_log.close();
  		//cur_log = null;
  	  		
  	  }//end if
  	  else
  	  {
  	  	Log.v("JB_TAG","該筆資料不用發送紀錄簡訊...");
  	  }
  	  	
  	  Log_sms = ""; 
  	  handler_log.removeCallbacks(logTask);
	}
	
	/********************************************
	 * Function Name: Send_SMS_To
	 * Input:
	 * Output:
	 * Description: 傳送簡訊
	 *********************************************/
	public boolean Send_SMS_To(String sPhoneNum, String sSendSMS)
	{
		try
		{
			//設定震動
			this.Set_Vibration(2000);
			
			//簡訊物件(需設定Manifest權限)
			SmsManager smsManager = SmsManager.getDefault();
			PendingIntent mPI=PendingIntent.getBroadcast(this, 0, new Intent(), 0);
	  
	  
	  
			//若同時傳送簡訊和LOG簡訊給自己，
			//會發生 smsManager.sendTextMessage 回傳null的例外情況 ... 20111026
			try 
			{
				if(sSendSMS.length()>160) //長度超過160字元，需分成多封簡訊傳送...
				{
					Log.v("JB_TAG","Sending Multipart SMS...");
					
					ArrayList<String> sSendSMS_array = smsManager.divideMessage(sSendSMS);
					ArrayList<PendingIntent> mPI_array = 
						new ArrayList<PendingIntent>();
					for(int i=0; i<sSendSMS_array.size(); i++)
					{
						mPI_array.add(mPI);
					}
					smsManager.sendMultipartTextMessage(sPhoneNum, null, sSendSMS_array, mPI_array, null);
					
					sSendSMS_array.clear();
					mPI_array.clear();
					sSendSMS_array=null;
					mPI_array=null;
					
				}
				else
				{
					Log.v("JB_TAG","Sending one SMS...");
					
					smsManager.sendTextMessage(sPhoneNum, null, sSendSMS, mPI, null);
					
				}
				
				return true;
			}
			catch(Exception ex)
			{
				Log.e("JB_TAG","錯誤:"+ex.getMessage());
				return false;
			}
			finally
			{
				mPI = null;
				smsManager = null;
			}
			  
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","傳送簡訊錯誤："+ex.getMessage());
			return false;
		}
	}
	/********************************************
	 * Function Name:Get_SmsEvent
	 * Input:
	 * Output:
	 * Description: 取得目前系統時間對應到的簡訊事件
	 *********************************************/
	public void Get_SmsEvent()
	{
		String time_now = Get_NowTime();
		
		String sYear=time_now.substring(0, 4);//取得年
		String sMon=time_now.substring(4, 6);//取得月
		String sDay=time_now.substring(6, 8);//取得日
		String sHour=time_now.substring(8, 10);//取得小時
		String sMin=time_now.substring(10, 12);//取得分鐘
		time_now = sYear+"/"+sMon+"/"+sDay+" "+sHour+":"+sMin;
		
		//取得資料庫查詢目前時間是否有簡訊事件的Cursor
		Cursor cur = myadp.fetch_MSGEVENT_Data(sYear, sMon, sDay, sHour, sMin);
		
		if (cur != null && cur.getCount() > 0)
		{
			//每一分鐘log出檢查訊息
			Log.v("JB_TAG","系統時間:"+sYear+"/"+sMon+"/"+sDay+" "+sHour+":"+sMin+" 有簡訊事件!");
			
			int numRows = cur.getCount();
			cur.moveToFirst();
			try
			{
			   for (int i = 0; i < numRows; ++i)
			   {
			      //傳送簡訊------------------------------------------------------------------
				  String Send2_sn = cur.getString(0);
				  String Send2_name = cur.getString(1);
			      String Send2_phonenum = cur.getString(2);
			      String Send2_msgid = cur.getString(3);
			      int Send2_send_geo_flg = cur.getInt(4);
			      int Send2_send_me_flg = cur.getInt(5);
			      
			      
			      
			      String Send2_sms = this.Get_SmsDesc(Send2_msgid); //簡訊代號對應簡訊內容
			      
			      
			      if(Send2_send_geo_flg==1 && CheckSetup_Send_GEO()==true)
			      {
			    	  GeoPoint gp = null;
				      
			    	  /* Test...
			    	  double geoLatitude = 25.0519*1E6;
				      double geoLongitude = 121.5456*1E6;
				      gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
				      */
			    	  gp = Get_Curr_GEO(); 

			    	  String sMyPos = "";
			    	  if(gp!=null)
			    	  {
			    	    sMyPos = getAddressByGeoPoint(Service_Smsvc.this,gp); //目前位置
			    	  }
			    	  else
			    	  {
			    		sMyPos = "GPS disabled";
			    	  }
			    	  
			    	  if(sMyPos.length()!=0)
			    	  {
			    		  Send2_sms+="("+sMyPos+")";
			    	  }
			      }
			      
			      //開始傳送簡訊
		  		  boolean sendRslt = Send_SMS_To(Send2_phonenum, Send2_sms);
		  		  
		  		  String sMsg = "";
		  		  if(sendRslt==false) //失敗
		  		  {
		  			if(Common.Get_User_SetLanguage(this.getApplicationContext()).equals("Chinese"))
		  			{
		  				sMsg = "簡訊無法發送給:"+Send2_name+"，手機號碼("+Send2_phonenum+")";
		  			}
		  			else
		  			{
		  				sMsg = "Fail to send SMS to "+Send2_name+" ("+Send2_phonenum+
			        	 ")";
		  			}
		  			
		  		    //Log
		  			Log.v("JB_TAG",sMsg);
		  		  }
		  		  else //成功
		  		  {
		  			if(Common.Get_User_SetLanguage(this.getApplicationContext()).equals("Chinese"))
		  			{
		  				sMsg = "簡訊已發送給:"+Send2_name+" ("+Send2_phonenum+")";
		  			}
		  			else
		  			{
		  				sMsg = "Send SMS successfully to "+Send2_name+" ("+Send2_phonenum+")";
		  			}
		  			
		  		    //Log
		  			Log.v("JB_TAG",sMsg);
		  			
		  		    //紀錄這次發送的時間---------------------------------------------------------
				    myadp.update_MSGEVENT_DateTime(Send2_sn, Send2_name, Send2_phonenum, time_now);
				    
				    
		  		  }
		  		  
		  		  //設定通知
		  		  if(CheckSetup_Send_Notify()==true)
		  		  {
		  			  this.Set_Notification("Smart SMS robot", sMsg);
		  		  }
		  		  
		  		  /*  此筆訊息需回送簡訊紀錄  ... 20120724 移除此功能*/ 
		  		  /*
		  		  if(Send2_send_me_flg==1)//此筆訊息需回送簡訊紀錄
			      {
		  			  
			    	  String short_sms="";
	  	  			  if(Send2_sms.length()>10)
	  	  			  {
	  	  				short_sms=Send2_sms.substring(0, 10)+"...";
	  	  			  }
	  	  			  else
	  	  			  {
	  	  				short_sms=Send2_sms;
	  	  			  }
	  	  			  
			    	  //傳送訊息紀錄---------------------------------------------------------------
			    	  try
			    	  {
			    		  handler_log.removeCallbacks(logTask);
				    	  SendLogCnt=10;
				    	  logTask.run();
			    	  }
			    	  catch(Exception ex)
			    	  {
			    		  Log.e("JB_TAG","設定回傳Log簡訊錯誤："+ex.getMessage());
			    	  }
			    	  
			      }
			      */

			      cur.moveToNext();

			   }
			}
			catch(Exception e) 
			{
				Log.v("JB_TAG","Error!"+e.getMessage().toString());
			}
		}
		else
		{
			//Log.v("JB_TAG","系統時間:"+sYear+"/"+sMon+"/"+sDay+" "+sHour+":"+sMin+" 沒有簡訊事件!");
		}
		cur.close();
	}
	
	/********************************************
	 * Function Name:Get_SmsEvent
	 * Input:
	 * Output:
	 * Description: 系統簡訊中文敘述
	 *********************************************/
	public String Get_SmsDesc(String MSG_ID)
	{
		String smg_desc="";
		
		//取得資料庫JB_SYSMSG的Cursor
		Cursor cur = myadp.fetch_SYSMSG_Data(MSG_ID);
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();
			try
			{
			   smg_desc=cur.getString(0);
			   cur.close();
			   
			}
			catch(Exception e) 
			{
				//
			}
			
		}
		return smg_desc;
	}
	
	/********************************************
	 * Function Name: getAddressByGeoPoint
	 * Input:
	 * Output:
	 * Description: 由經緯度取得目前地址
	 *********************************************/
	public String getAddressByGeoPoint(Context context, GeoPoint gp)
	{
	    String strAddress = "";
	    try
	    {
	      if (gp != null)
	      {
	        double geoLatitude = (int)gp.getLatitudeE6()/1E6;
	        double geoLongitude = (int)gp.getLongitudeE6()/1E6;
	        
	        //double dLat = location.getLatitude();
	        //double dLng = location.getLongitude();
	        
	        Geocoder gc = new Geocoder(context, Locale.getDefault());
	        try
	        {
	          //自經緯度取得地址(可能有多行地址)
	          List<Address> lstAddress = gc.getFromLocation(geoLatitude, geoLongitude, 1);
	          StringBuilder sb = new StringBuilder();
	          
	          //判斷地址是否為多行
	          if (lstAddress.size() > 0)
	          {
	            Address adsLocation = lstAddress.get(0);
	            for (int i = 0; i < adsLocation.getMaxAddressLineIndex(); i++)
	            {
	              //sb.append(adsLocation.getAddressLine(i)).append("\n");
	            }
	            //sb.append(adsLocation.getLocality()).append(" ");
	            sb.append(adsLocation.getAddressLine(0)).append(" ");
	            //sb.append(adsLocation.getPostalCode()).append(" ");
	            //sb.append(adsLocation.getCountryName());
	          }
	          strAddress = sb.toString();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	      }
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	    return strAddress;
	}
	 

	/********************************************
	 * Function Name: Get_Curr_GEO
	 * Input:
	 * Output:
	 * Description: 
	 *********************************************/
	public GeoPoint Get_Curr_GEO()
	{       
      GeoPoint gp = null;
	  GetGPS myGetGPS = new GetGPS(this);
	  
	  
	  if(myGetGPS.CheckNetWork()==false && myGetGPS.CheckGPS()==false)
	  {
		  Log.v("JB_TAG", "無網路&沒有開啟GPS");
		  myGetGPS = null;
		  return gp;
	  }
	  else if( myGetGPS.CheckGPS()==true )
	  {
		  Log.v("JB_TAG", "有開啟GPS");
	  }
		  
      try
	  {
		//取得GPS位置
    	double[] dGpsPosition = new double[2]; 
		dGpsPosition = myGetGPS.GetGPSCurPosition();
		
		double geoLongitude = dGpsPosition[0]; //經度
		double geoLatitude = dGpsPosition[1]; //緯度
	    
		Log.v("JB_TAG", "經度："+ geoLongitude);
		Log.v("JB_TAG", "緯度："+ geoLatitude);
		
		gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
		
		return gp;
	  }
      catch(Exception ex)
      {
    	Log.e("JB_TAG", "錯誤："+ex.getMessage());
    	return gp;
      }
      finally
      {
    	  myGetGPS = null;
      }
	}
	

	/********************************************
	 * Function Name: Set_Vibration
	 * Input: 震動多少毫秒
	 * Output:
	 * Description: 手機震動
	 *********************************************/
	public void Set_Vibration(int vib_msec)
	{
		try
		{
			//取得震動服務
			Vibrator vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
			//振動2秒
			vibrator.vibrate(vib_msec);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","震動功能錯誤，原因:"+ex.getMessage());
		}
	}
	/********************************************
	 * Function Name: Set_Notification
	 * Input: 標題,提醒內容
	 * Output:
	 * Description: 狀態列和狀態欄
	 *********************************************/
	public void Set_Notification(String sTitle_Text, String sNotify_Text)
	{
		try
		{
			//取得訊息提示服務的管理物件
			NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			
			//將訊息顯示在 "狀態列" 上
			Notification msg = new Notification(
			    R.drawable.main_title,      //訊息圖示 (會自動縮放)
			    "",                         //要顯示的訊息
			    System.currentTimeMillis()  //傳送的時間
			);

			//將訊息顯示在 "狀態欄" 上
			PendingIntent intent = PendingIntent.getActivity(
			    this,
			    0,
			    new Intent(this, Android_SMSvc.class),
			    PendingIntent.FLAG_UPDATE_CURRENT
			);
			 
			msg.setLatestEventInfo(this.getApplicationContext(), sTitle_Text, sNotify_Text, intent);
			nManager.notify(0, msg);

		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","震動功能錯誤，原因:"+ex.getMessage());
		}
	}
	/**********************
	* 簡訊Receiver
	***********************
	public class mSMSReceiver extends BroadcastReceiver
	{
	  @Override
	  public void onReceive(Context context, Intent intent)
	  {
	    // TODO Auto-generated method stub
	    if (intent.getAction().equals(smsACTION))
	    {
	      StringBuilder sb = new StringBuilder();
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
	          sb.append("收到來自:");
	          sb.append(currentMessage.getDisplayOriginatingAddress());
	          sb.append("傳來的簡訊");
	          sb.append(currentMessage.getDisplayMessageBody());
	        }
	        
	        try
	        {
	        	Intent NewIntent=new Intent(JB_BROATCAST_ID);
	        	NewIntent.putExtra("STR_PARAM", sb.toString());
	        	NewIntent.setClass(Service_Smsvc.this, Android_SMSvc.class); //設定從此activity轉頁到哪一個activity
	        	sendBroadcast(NewIntent);
	    		startActivity(NewIntent);
	    		
	    		
	        	//Intent i = new Intent(JB_BROATCAST_ID);
	    	    //i.putExtra("STR_PARAM", sb.toString());
	    	    //sendBroadcast(i);
	    	    
	    	    //Android_SMSvc.started = true;
	    		//Android_SMSvc.updateServiceStatus();
	        }
	        catch (Exception e)
	        {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	        }
	        
	        
	      }
	    }
	  }
	}
	*/
	

	@Override
	public void onDestroy() {
		//unregisterReceiver(mReceiver01);
		
		//結束簡訊預約事件檢查
		handler.removeCallbacks(mTask);
		
		//節數資料庫連線
		myadp.close();
		
		//結束wakeLock
        if (wakeLock != null) 
        { 
            wakeLock.release(); 
            wakeLock = null;    
        } 
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

	    super.onStart(intent, startId);
	}
	
}
