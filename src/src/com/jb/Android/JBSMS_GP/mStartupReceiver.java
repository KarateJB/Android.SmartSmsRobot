package com.jb.Android.JBSMS_GP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class mStartupReceiver extends BroadcastReceiver
{
	/*####### 參數 #########*/
    private final static String OPTION_STARTUP_SERVICE_STR = "STARTUP_SERVICE";  //開機即執行服務的代表名稱
    
	/* SQLite */
	MyDataBaseAdapter myadp = null;
	

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		int iFlag = 0;
		
		/* *********************
		 * 開啟已經存在的資料庫
		 * ********************/
		myadp=new MyDataBaseAdapter(context);
		myadp.open();
		
		//從資料庫JB_USER_SETUP取得設定值(是否開機即執行?)
        Cursor cur = myadp.fetch_USERSETUP_Data(OPTION_STARTUP_SERVICE_STR);
		
		if (cur != null && cur.getCount() > 0)
		{
			cur.moveToFirst();
			try
			{
			   iFlag =  Integer.parseInt(cur.getString(2));
			   cur.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			}
		}
		
        		
		if(iFlag==1) /* 確定啟動服務 */
		{
			try
			{
				/* Start Service (自動送簡訊服務)*/
			    Intent i = new Intent( context, Service_Smsvc.class ); 
			    //i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			    context.startService(i);
			    
			    Log.e("JB_TAG","開機啟動服務(自動送簡訊服務)=On");
			    
			    
			    /* Register Receiver (收簡訊的監聽)*/
				IntentFilter mFilter01;
			    mFilter01 = new IntentFilter("NEW SMS");
			    mSMSReceiver mReceiver01 = new mSMSReceiver();
			    context.registerReceiver(mReceiver01, mFilter01);
			    
			    Log.e("JB_TAG","開機啟動監聽(收簡訊的監聽)=On");
			}
			catch(Exception ex)
			{
				Log.e("JB_TAG","錯誤："+ex.getMessage());
			}
		    
		}
		else /* 不啟動服務 */
		{
			Log.e("JB_TAG","開機啟動服務=Off");
		}
		
		
		
		myadp.close();
	}

	
}
