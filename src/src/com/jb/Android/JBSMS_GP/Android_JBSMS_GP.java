package com.jb.Android.JBSMS_GP;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.jb.Android.JBSMS_GP.R.drawable;

public class Android_JBSMS_GP extends Activity
{
	/*處理自動轉換頁面的Handler*/
	private Handler handler    = new Handler();
	
	/* 系統內建訊息 */
    private int Sel_pos = -1; //User選取的訊息index
    
	/*初始化*/
	private LinearLayout Layout_Main02    = null;
	private LinearLayout Layout_Language  = null;
	private ScrollView ScrollView_Main01  = null;
	private ImageView lv_Main             = null;
	
	private ImageButton imb_main_option01 =null;
	private ImageButton imb_main_option02 =null;
	private ImageButton imb_main_option03 =null;
	private ImageButton imb_main_option99 =null;
	
	private Button bt_SetLanguage    = null;
	
	//電源管理
	//PowerManager.WakeLock wakeLock = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Layout_Main02 = (LinearLayout)this.findViewById(R.id.Layout_Main02);
        Layout_Language = (LinearLayout)this.findViewById(R.id.Layout_Language);
        ScrollView_Main01 = (ScrollView)this.findViewById(R.id.ScrollView_Main01);
        lv_Main = (ImageView)this.findViewById(R.id.iv_Main);
        imb_main_option01 = (ImageButton)this.findViewById(R.id.imb_main_option01);
        imb_main_option02 = (ImageButton)this.findViewById(R.id.imb_main_option02);
        imb_main_option03 = (ImageButton)this.findViewById(R.id.imb_main_option03);
        imb_main_option99 = (ImageButton)this.findViewById(R.id.imb_main_option99);
        bt_SetLanguage = (Button)this.findViewById(R.id.bt_SetLanguage);
        
        Layout_Main02.setVisibility(8); //visible: gone
        Layout_Language.setVisibility(8); //visible: gone
        //ScrollView_Main01.setVisibility(8); //visible: gone
        lv_Main.setImageResource(drawable.starwars); 
        
        
        //取得使用者上次設定的語言 
        String sUserLan = Common.Get_User_SetLanguage(Android_JBSMS_GP.this);
        if(sUserLan.equals("English") )
        {
        	Sel_pos = 0;
        }
        else if(sUserLan.equals("Chinese"))
        {
        	Sel_pos = 1;
        }
        //根據語言設定顯示
        Adjust_Language_Layout();
        
        Log.v("JB_TAG","系統語言："+Common.My_Language);
        
        /*點選首頁圖事件*/
        lv_Main.setOnClickListener(new ImageView.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				//停止自動轉換頁面的Handler
				handler.removeCallbacks(mTask);
				
				/* 設定UI新寬高 */
				DisplayMetrics dm = getResources().getDisplayMetrics();  
				int ScreenWidth = dm.widthPixels;  
				int ScreenHeight = dm.heightPixels;
				
				int iMain_Pixel=0; //首頁圖新寬高
				int iOption_WPixel=0; //選項寬
				int iOption_HPixel=0; //選項高
				int iButton_WPixel=0; //按鈕寬
				int iButton_HPixel=0; //按鈕高
				
				
				Layout_Main02.setVisibility(0); //visible: enabled
				Layout_Language.setVisibility(0); //visible: enabled
				//ScrollView_Main01.setVisibility(0); //visible: enabled
				
				if(ScreenWidth<ScreenHeight) //直立手機
				{
					iMain_Pixel=(int)(ScreenWidth*0.6);
					iOption_WPixel=(int)(ScreenWidth*0.9);
					iOption_HPixel=(int)(iOption_WPixel*0.2);
					iButton_WPixel=(int)(ScreenWidth/3);
					iButton_HPixel=(int)(iButton_WPixel/3);
				}
				else  //橫放
				{
					iMain_Pixel=(int)(ScreenHeight*0.0);
					iOption_WPixel=(int)(ScreenHeight*0.9);
					iOption_HPixel=(int)(iOption_WPixel*0.2);
					iButton_WPixel=(int)(ScreenHeight/3);
					iButton_HPixel=(int)(iButton_WPixel/3);
				}
				lv_Main.getLayoutParams().width=iMain_Pixel;
				lv_Main.getLayoutParams().height=iMain_Pixel;
				
				
				imb_main_option01.getLayoutParams().width = iOption_WPixel;
				imb_main_option01.getLayoutParams().height = iOption_HPixel;
				imb_main_option02.getLayoutParams().width = iOption_WPixel;
				imb_main_option02.getLayoutParams().height = iOption_HPixel;
				imb_main_option03.getLayoutParams().width = iOption_WPixel;
				imb_main_option03.getLayoutParams().height = iOption_HPixel;
				imb_main_option99.getLayoutParams().width = iOption_WPixel;
				imb_main_option99.getLayoutParams().height = iOption_HPixel;
				//bt_SetLanguage.getLayoutParams().width = iButton_WPixel;
				//bt_SetLanguage.getLayoutParams().height = iButton_HPixel;
				
				
			}
		});
        
        /*點選[SMS Event]事件*/
        imb_main_option01.setOnClickListener(new ImageButton.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				ChgActivity01();
			}
		});
        
        /*點選[SMS Browser]事件*/
        imb_main_option02.setOnClickListener(new ImageButton.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				ChgActivity02();
			}
		});
        
        /*點選[Backup & Recovery]事件*/
        imb_main_option03.setOnClickListener(new ImageButton.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				ChgActivity03();
			}
		});
        
        /*點選[Guide]事件*/
        imb_main_option99.setOnClickListener(new ImageButton.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				if(Common.My_Language.equals("Chinese"))
				{
					Uri uri = Uri.parse( "http://karatejb.blogspot.com/2011/11/android_28.html" );
					startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
				}
				else
				{
					Uri uri = Uri.parse( "http://karatejb.blogspot.com/2011/11/android-smart-sms-robot-guide-english.html" );
					startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
				}
			}
		});
        
        /*點選[語言]事件*/
        bt_SetLanguage.setOnClickListener(new Button.OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder ad_Language = new AlertDialog.Builder(Android_JBSMS_GP.this);
				ad_Language.setTitle(R.string.tm_SetLanguage_title);
				
				//建立選擇的事件
				DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   Sel_pos = which;
				   }
				};
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   if(Sel_pos >=0 )
					   {
						   String sMyLan="";
						   switch(Sel_pos)
						   {
						   case 0:
							   sMyLan = "English";
							   break;
						   case 1:
							   sMyLan = "Chinese";
							   break;
						   default:
							   sMyLan = "English";
							   break;
						   }
						   
						   Common.Set_User_SetLanguage(Android_JBSMS_GP.this, sMyLan); //設定
						   Adjust_Language_Layout(); //調整UI
					   }
				   }
				};
				//建立按下[取消]的事件
				DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   Sel_pos=-1;
					   dialog.cancel();
				   }
				};
				
				//設定內容
				String [] sLanList=new String[2];
				sLanList[0] = "English";
				sLanList[1] = "Chinese(中文繁體)";
				
				ad_Language.setSingleChoiceItems(sLanList, Sel_pos, ListClick);
				sLanList=null;
				
				if(Common.My_Language.equals("Chinese"))
				{
					ad_Language.setPositiveButton(R.string.tm_OP_yes_ch,OkClick); 
					ad_Language.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
				}
				else
				{
					ad_Language.setPositiveButton(R.string.tm_OP_yes,OkClick); 
					ad_Language.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
				}
				
				ad_Language.show();
			}
		});
    }


    private int cnt=10;
    private Runnable mTask =new Runnable()
	{
		public void run()
	    {
			Log.v("JB_TAG",cnt+" 秒後到首頁...");
			handler.postDelayed(this, 1000);
			cnt--;
			if(cnt==0)
			{
				ChgActivity01();
			}
	    }
	};
	
	@Override
	protected void onDestroy() {
		handler.removeCallbacks(mTask);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		handler.removeCallbacks(mTask);
		super.onPause();
	}



	@Override
	protected void onResume()
	{
		super.onResume();
		mTask.run();
		
		//啟動簡訊自動發送服務
		Start_SMS_Send_Service();
	}

	public static void MainClose() 
	{

	}
	
	/************************************
	 * Function Name: Start_SMS_Send_Service
	 * Input: 
	 * Output:
	 * Description: 啟動簡訊自動發送服務
	 *************************************/
	public void Start_SMS_Send_Service()
	{
		try
		{
			//Service
			Intent i = new Intent( Android_JBSMS_GP.this, Service_Smsvc.class ); 
		    i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		    startService(i);
		    Log.v("JB_TAG","簡訊自動發送服務已啟動!");

		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","簡訊自動發送服務無法啟動!"+ex.getMessage());
		}
	}
	/************************************
	 * Function Name: Adjust_Language_Layout
	 * Input:
	 * Output:
	 * Description: 調整UI顯示語言
	 *************************************/
	private void Adjust_Language_Layout()
	{
		String sMyLan = Common.My_Language;
		if( sMyLan.equals("English") )
		{
			imb_main_option01.setBackgroundResource(R.drawable.bt_main_option01);
			imb_main_option02.setBackgroundResource(R.drawable.bt_main_option02);
			imb_main_option03.setBackgroundResource(R.drawable.bt_main_option03);
			imb_main_option99.setBackgroundResource(R.drawable.bt_main_option99);
		}
		else if( sMyLan.equals("Chinese") )
		{
			imb_main_option01.setBackgroundResource(R.drawable.bt_main_option01_ch);
			imb_main_option02.setBackgroundResource(R.drawable.bt_main_option02_ch);
			imb_main_option03.setBackgroundResource(R.drawable.bt_main_option03_ch);
			imb_main_option99.setBackgroundResource(R.drawable.bt_main_option99_ch);
		}
	}
	
	/************************************
	 * Function Name: ChgActivity01
	 * Input:
	 * Output:
	 * Description: 
	 *************************************/
	private void ChgActivity01()
	{
		Intent intent=new Intent();
		intent.setClass(Android_JBSMS_GP.this, Android_SMSvc.class); //設定從此activity轉頁到哪一個activity
		/*
		Bundle bund=new Bundle();
		bund.putString("UserID", et_Id.getText().toString());
		bund.putString("UserPWD", et_Pwd.getText().toString());
		intent.putExtras(bund);
		*/
		startActivityForResult(intent, 0);
		this.finish();
	}
	
	/************************************
	 * Function Name: ChgActivity02
	 * Input:
	 * Output:
	 * Description: 
	 *************************************/
	private void ChgActivity02()
	{
		Intent intent=new Intent();
		intent.setClass(Android_JBSMS_GP.this, Android_SMSbs.class); //設定從此activity轉頁到哪一個activity
		/*
		Bundle bund=new Bundle();
		bund.putString("UserID", et_Id.getText().toString());
		bund.putString("UserPWD", et_Pwd.getText().toString());
		intent.putExtras(bund);
		*/
		startActivityForResult(intent, 0);
		this.finish();
	}
	
	/************************************
	 * Function Name: ChgActivity03
	 * Input:
	 * Output:
	 * Description: 
	 *************************************/
	private void ChgActivity03()
	{
		Intent intent=new Intent();
		intent.setClass(Android_JBSMS_GP.this, Android_SMSbk.class); //設定從此activity轉頁到哪一個activity
		/*
		Bundle bund=new Bundle();
		bund.putString("UserID", et_Id.getText().toString());
		bund.putString("UserPWD", et_Pwd.getText().toString());
		intent.putExtras(bund);
		*/
		startActivityForResult(intent, 0);
		this.finish();
	}
	
	
}
