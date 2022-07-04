package com.jb.Android.JBSMS_GP;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.jb.Android.JBSMS_GP.R.drawable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class Android_SMSvc extends Activity
{
	private ImageView iv_Title               = null;
    private ImageButton imb_contact          = null;
    private ImageButton imb_time             = null;
    private ImageButton imb_sms              = null;
    private ImageButton imb_eventlog         = null;
    private ImageButton imb_setup            = null;
    private ImageButton imb_save             = null;
    private ImageButton imb_open_contact     = null;
    private ImageButton imb_clr_contact      = null;
    private ImageButton imb_SaveSmsEvent     = null;
    private ImageButton imb_UpdateSmsEvent   = null;
    private ImageButton imb_SaveSetup        = null;
    
    private LinearLayout Layout_Contact  = null;
    private LinearLayout Layout_DateTime = null;
    private LinearLayout Layout_Sms      = null;
    private LinearLayout Layout_SmsEvent = null;
    private LinearLayout Layout_Save     = null;
    private LinearLayout Layout_Setup    = null;
    private LinearLayout Layout_admob    = null;
    
    private TextView tv_CheckList_SN_CP          = null;
    private TextView tv_CheckList_NAME_CP        = null;
    private TextView tv_CheckList_DATETIME_CP    = null;
    private TextView tv_CheckList_SMS_DESC_CP    = null;
    private TextView tv_SendLog2Me               = null;
    
    private TextView tv_NewSms     = null;
    private TextView tv_MySms      = null;

    private EditText et_NewSms          = null;
    private EditText et_MySms           = null;
    private ListView lv_Contact         = null;
    private Button  bt_SetYear          = null;
    private Button  bt_SetMon           = null;
    private Button  bt_SetDay           = null;
    private Button   bt_SetHour         = null;
    private Button   bt_SetMin          = null;
    private Button  bt_SysSms           = null;
    private Button  bt_DelSms           = null;
    private Button  bt_ClrSms           = null;
    private Button bt_SaveSms           = null;
    private Button bt_SendLog2Me        = null;
    
    private CheckBox cb_Send2Me         = null; //每一筆訊息事件中的自動回傳紀錄
    private CheckBox cb_Send2GEO        = null; //每一筆訊息事件中的自動回傳GEO
    
    private CheckBox cb_StartupService  = null; //設定中的開機即啟動
    private CheckBox cb_SendNotify      = null; //發送通知
    private CheckBox cb_SendLog2Me      = null; //設定中的自動回傳紀錄
    private CheckBox cb_SendGEO         = null; //設定中的回傳GEO地理位置
    
    
    
    private TextView tv_CheckList_SN          = null;
    private TextView tv_CheckList_NAME        = null;
    private TextView tv_CheckList_DATETIME    = null;
    private TextView tv_CheckList_SMS_DESC    = null;
    private TextView tv_CheckList_SENDGEO     = null;
    private TextView tv_CheckList_SEBD2ME     = null;
    
    
    
    //Admob AdView
	private AdView adView;
	

    /*####### 參數 #########*/
    //private final static int RECEIVE_NEW_SMS = 1;          //取得新簡訊(mSMSReceiver)的Activity參數 
    public final static int PICK_CONTACT_SUBACTIVITY = 2; //選擇收件者的Activity參數 
    public final static int PICK_CONTACT_SENDLOG2ME = 3;  //選擇要收簡訊紀錄收件者的Activity參數
    public final static String OPTION_STARTUP_SERVICE_STR = "STARTUP_SERVICE";  //開機即執行服務的代表名稱
    public final static String OPTION_SEND_NOTIFICATION_STR = "SEND_NOTIFY";  //開機即執行服務的代表名稱
    public final static String OPTION_SEND_LOG_STR = "SEND_LOG";  //回傳LOG訊息的代表名稱
    public final static String OPTION_SEND_GEO_STR = "SEND_GEO";  //傳送GPS的代表名稱
    
    /* 日期參數 */
    private List<String> mYearList = new ArrayList<String>();
    private List<String> mMonList = new ArrayList<String>();
    private List<String> mDayList = new ArrayList<String>();
    
    /* 通訊錄 */
    //private ContactsAdapter myContactAdp;
    
    /* 簡訊 */
    //private mSMSReceiver mReceiver01;

    /* SQLite */
	MyDataBaseAdapter myadp = null;
	
	/* 系統內建訊息 */
	private List<String> mSysMsgList = new ArrayList<String>();
    private int Sel_pos=-1; //User選取的訊息index
    
    /* 判斷目前為新增(0)或修改(1)事件中 */
    private int SmsEvent_STS=0;
    
	/* 簡訊事件列表  */
    private ArrayList<HashMap<String, String>> Sms_Event_List = new ArrayList<HashMap<String, String>>();
	private ListView lv_SmsEvent;
	private String SmsEvent_SN;
	
	private int Sel_SmsEvent_index=-1; //點選的訊息事件index
	private int Sel_SmsEvent_func=-1; //User選取的訊息事件處理(修改或刪除)

    /* USER選擇的通訊錄名字和電話號碼 */
    private List<String> Sel_UserName = new ArrayList<String>();
    private List<String> Sel_UserPhonenum = new ArrayList<String>();
    
    /* 服務狀態顯示文字 */
    public static String Svc_Sts_Desc = "";
    
    
    
    /* 是否傳送訊息紀錄給自己 */
    //public static int SendLog2Me_flg=0; //0:不傳送,1:傳送 
    public static int SendLog2Me_singleflg=0; //要更新的flg  0:不傳送,1:傳送 
    
    /* 是否傳送GEO地理位置(單筆) */
    public static int SendGEO_singleflg=0;  //要更新的flg  0:不傳送,1:傳送 


    
    public static String[] PEOPLE_PROJECTION = new String[]
    {
    	ContactsContract.Contacts._ID,
    	ContactsContract.CommonDataKinds.Phone.NUMBER,
    	ContactsContract.Contacts.DISPLAY_NAME
    };

  
    /************************************
	* Function Name: onCreateOptionsMenu
	* Input: 
	* Output: 
	* Description: 功能鍵選單
	*************************************/
    @Override
	public boolean onCreateOptionsMenu(Menu menu) 
    {
    	MenuInflater inflater = getMenuInflater();
		//設定menu界面為res/menu/menu.xml
    	if(Common.My_Language.equals("Chinese"))
    	{
    		inflater.inflate(R.menu.menu_ch, menu);
    	}
    	else 
    	{
    		inflater.inflate(R.menu.menu, menu);
    	}
		return true;
	}
    
    /************************************
	* Function Name: onCreate
	* Input: 
	* Output: 
	* Description: 
	*************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.sms);
		
		
		tv_CheckList_SN_CP = (TextView)this.findViewById(R.id.tv_CheckList_SN_CP);
	    tv_CheckList_NAME_CP = (TextView)this.findViewById(R.id.tv_CheckList_NAME_CP);
	    tv_CheckList_DATETIME_CP = (TextView)this.findViewById(R.id.tv_CheckList_DATETIME_CP);
	    tv_CheckList_SMS_DESC_CP = (TextView)this.findViewById(R.id.tv_CheckList_SMS_DESC_CP);
	    tv_SendLog2Me = (TextView)this.findViewById(R.id.tv_SendLog2Me);
	    
	    tv_NewSms = (TextView)this.findViewById(R.id.tv_NewSms);
	    tv_MySms = (TextView)this.findViewById(R.id.tv_MySms);
		
	    iv_Title = (ImageView)this.findViewById(R.id.iv_Title);
		imb_contact = (ImageButton)this.findViewById(R.id.imb_contact);
	    imb_time = (ImageButton)this.findViewById(R.id.imb_time);
	    imb_sms = (ImageButton)this.findViewById(R.id.imb_sms);
	    imb_eventlog = (ImageButton)this.findViewById(R.id.imb_eventlog);
	    imb_setup = (ImageButton)this.findViewById(R.id.imb_setup);
	    imb_save = (ImageButton)this.findViewById(R.id.imb_save);
	    imb_open_contact = (ImageButton)this.findViewById(R.id.imb_open_contact);
	    imb_clr_contact = (ImageButton)this.findViewById(R.id.imb_clr_contact);
	    imb_SaveSmsEvent=(ImageButton)this.findViewById(R.id.imb_SaveSmsEvent);
	    imb_UpdateSmsEvent=(ImageButton)this.findViewById(R.id.imb_UpdateSmsEvent);
	    imb_SaveSetup=(ImageButton)this.findViewById(R.id.imb_SaveSetup);
	    
	    Layout_Contact=(LinearLayout)this.findViewById(R.id.Layout_Contact);
	    Layout_DateTime=(LinearLayout)this.findViewById(R.id.Layout_DateTime);
	    Layout_Sms=(LinearLayout)this.findViewById(R.id.Layout_Sms);
	    Layout_SmsEvent=(LinearLayout)this.findViewById(R.id.Layout_SmsEvent);
	    Layout_Save=(LinearLayout)this.findViewById(R.id.Layout_Save);
	    Layout_Setup=(LinearLayout)this.findViewById(R.id.Layout_Setup);
	    Layout_admob=(LinearLayout)this.findViewById(R.id.Layout_admob);
	    
	    et_NewSms = (EditText)this.findViewById(R.id.et_NewSms);
	    et_MySms = (EditText)this.findViewById(R.id.et_MySms);	    
		
		bt_SetYear = (Button)this.findViewById(R.id.bt_SetYear);
		bt_SetMon = (Button)this.findViewById(R.id.bt_SetMon);
		bt_SetDay = (Button)this.findViewById(R.id.bt_SetDay);
		bt_SetHour = (Button)this.findViewById(R.id.bt_SetHour);
		bt_SetMin = (Button)this.findViewById(R.id.bt_SetMin);
		
		bt_SaveSms = (Button)this.findViewById(R.id.bt_SaveSms);
		bt_SysSms = (Button)this.findViewById(R.id.bt_SysSms);
		bt_DelSms = (Button)this.findViewById(R.id.bt_DelSms);
		bt_ClrSms = (Button)this.findViewById(R.id.bt_ClrSms);
		bt_SendLog2Me = (Button)this.findViewById(R.id.bt_SendLog2Me);
		
		cb_StartupService = (CheckBox)this.findViewById(R.id.cb_StartupService);
		cb_SendNotify = (CheckBox)this.findViewById(R.id.cb_SendNotify); 
		cb_Send2GEO = (CheckBox)this.findViewById(R.id.cb_Send2GEO);
		cb_Send2Me = (CheckBox)this.findViewById(R.id.cb_Send2Me);
		
		cb_SendLog2Me = (CheckBox)this.findViewById(R.id.cb_SendLog2Me);
		cb_SendGEO = (CheckBox)this.findViewById(R.id.cb_SendGEO); 
		lv_Contact = (ListView)this.findViewById(R.id.lv_Contact);
		lv_SmsEvent = (ListView)this.findViewById(R.id.lv_SmsEvent);
		
		tv_CheckList_SN = (TextView)this.findViewById(R.id.tv_CheckList_SN);
	    tv_CheckList_NAME = (TextView)this.findViewById(R.id.tv_CheckList_NAME);
	    tv_CheckList_DATETIME = (TextView)this.findViewById(R.id.tv_CheckList_DATETIME);
	    tv_CheckList_SMS_DESC = (TextView)this.findViewById(R.id.tv_CheckList_SMS_DESC);
	    tv_CheckList_SENDGEO = (TextView)this.findViewById(R.id.tv_CheckList_SENDGEO);
	    tv_CheckList_SEBD2ME = (TextView)this.findViewById(R.id.tv_CheckList_SEBD2ME);
	    
	    
	    
		//先隱藏每個圖層
		Set_Layout_Visible(0);
		
		//開啟廣告
		Show_Admob_Banner();
		
		//設定lv_Contact為多選
		lv_Contact.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
		
		
		
		switch(SmsEvent_STS)
		{
		case 0:
			imb_open_contact.setVisibility(0);
			imb_clr_contact.setVisibility(0);
			imb_SaveSmsEvent.setVisibility(0);
			imb_UpdateSmsEvent.setVisibility(8);
			break;
		case 1:
			imb_open_contact.setVisibility(8);
			imb_clr_contact.setVisibility(8);
			imb_SaveSmsEvent.setVisibility(8);
			imb_UpdateSmsEvent.setVisibility(0);
			break;
		default:
			break;
		}
		
		
		/* 開啟電話簿圖層  */
		imb_contact.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				
				Set_Layout_Visible(1);
				
				switch(SmsEvent_STS)
				{
				case 0:
					imb_open_contact.setVisibility(0);
					imb_clr_contact.setVisibility(0);
					imb_SaveSmsEvent.setVisibility(0);
					imb_UpdateSmsEvent.setVisibility(8);
					break;
				case 1:
					imb_open_contact.setVisibility(8);
					imb_clr_contact.setVisibility(8);
					imb_SaveSmsEvent.setVisibility(8);
					imb_UpdateSmsEvent.setVisibility(0);
					break;
				default:
					break;
				}
		}});
		/* 開啟日期時間圖層  */
		imb_time.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {


				Set_Layout_Visible(2);
				
		}});
		/* 開啟簡訊內容圖層  */
		imb_sms.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Set_Layout_Visible(3);
				
		}});
		/* 開啟紀錄圖層  */
		imb_eventlog.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Set_Layout_Visible(4);
				List_SmsEvent();
		}});
		/* 開啟儲存事件圖層  */
		imb_save.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {

				Set_Layout_Visible(5);
				Show_CheckList();
		}});
		/* 開啟設定圖層  */
		imb_setup.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {

				Set_Layout_Visible(6);
		}});
		
		/* 選擇連絡人  */
		imb_open_contact.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				/*
				Uri uri = Uri.parse("content://contacts/people");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                startActivityForResult(intent, PICK_CONTACT_SUBACTIVITY);
                */
				Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			    startActivityForResult(intent, PICK_CONTACT_SUBACTIVITY);
				
		}});
		/* 刪除選擇的連絡人  */
		imb_clr_contact.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				Clear_Contact();
		}});
		
		/* 清除目前使用者輸入的字串  */
		bt_ClrSms.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				et_NewSms.setText("");
				
		}});
		
		et_MySms.setOnClickListener(new EditText.OnClickListener(){
			@Override
			public void onClick(View v) {
				Show_SysMsgList();
		}});

		/* 顯示系統訊息清單  */
		bt_SysSms.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Show_SysMsgList();  
			}
			
		}
		);
		
		/* 儲存訊息到系統訊息清單  */
		bt_SaveSms.setOnClickListener(new Button.OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				String New_SmsDesc = et_NewSms.getText().toString();
				
				if(New_SmsDesc.equalsIgnoreCase("")) //未輸入訊息字串
				{
					//顯示警告訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
					ad_Action.setIcon(R.drawable.alert);
					
					ad_Action.setMessage(R.string.tm_EVT_sms_empty);
					
					DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						   dialog.cancel();
					   }
					};
					
					if(Common.My_Language.equals("Chinese")) //中文
					{
						ad_Action.setTitle(R.string.tm_OP_alert_ch);
						ad_Action.setNegativeButton("關閉",CancelClick);
					}
					else
					{
						ad_Action.setTitle(R.string.tm_OP_alert);
						ad_Action.setNegativeButton("Close",CancelClick);
					}
					ad_Action.show();
					
				}
				else if(New_SmsDesc.length()>160) //字串長度超過限制
				{
					//Toast.makeText(Android_SMSvc.this,R.string.tm_EVT_sms_toolong, Toast.LENGTH_SHORT).show();
					//顯示警告訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
					ad_Action.setIcon(R.drawable.alert);
					
					DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						   dialog.cancel();
					   }
					};
					
					if(Common.My_Language.equals("Chinese")) //中文
					{
						ad_Action.setTitle(R.string.tm_OP_alert_ch);
						ad_Action.setMessage(R.string.tm_EVT_sms_toolong_ch);
						ad_Action.setNegativeButton("關閉",CancelClick);
					}
					else
					{
						ad_Action.setTitle(R.string.tm_OP_alert);
						ad_Action.setMessage(R.string.tm_EVT_sms_toolong);
						ad_Action.setNegativeButton("Close",CancelClick);
					}
					
					ad_Action.show();
				}
				else
				{
					try
					{
						myadp.insert_SYSMSG_Data( Common.Get_NowTime(Android_SMSvc.this), New_SmsDesc);	
						Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_sms_saved_suc , Toast.LENGTH_SHORT).show();
						refresh_SysMsgList();
					}
					catch(Exception e)
					{
						Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_sms_saved_fail, Toast.LENGTH_SHORT).show();
					}
				}
			}}
		);
		/* 刪除某個系統訊息清單  */
		bt_DelSms.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(et_MySms.getText().toString().equalsIgnoreCase("")) //沒有選取任何系統訊息
				{
					//Toast.makeText(Android_SMSvc.this,R.string.tm_EVT_sms_del_empty, Toast.LENGTH_SHORT).show();
					//顯示警告訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
					ad_Action.setIcon(R.drawable.alert);
					
					DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						   dialog.cancel();
					   }
					};
					
					if(Common.My_Language.equals("Chinese")) //中文
					{
						ad_Action.setTitle(R.string.tm_OP_alert_ch);
						ad_Action.setMessage(R.string.tm_EVT_sms_del_empty_ch);
						ad_Action.setNegativeButton("關閉",CancelClick);
					}
					else
					{
						ad_Action.setTitle(R.string.tm_OP_alert);
						ad_Action.setMessage(R.string.tm_EVT_sms_del_empty);
						ad_Action.setNegativeButton("Close",CancelClick);
					}
					
					ad_Action.show();
					
				}
				else
				{
					try
					{
						
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
						ad_Action.setIcon(R.drawable.question);
						
						//建立按下[確定]的事件
						DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
						{
						   public void onClick(DialogInterface dialog, int which) 
						   {
							   String Sel_SysSMS = et_MySms.getText().toString();
							   String Sel_SysSMS_short = Sel_SysSMS;
							   if(Sel_SysSMS_short.length()>20)
							   {
								   Sel_SysSMS_short=Sel_SysSMS_short.substring(0, 20);
							   }
							   //刪除相關訊息事件
							   String sSMS_ID=Get_SMS_ID(Sel_SysSMS);
							   int del_cnt = myadp.delete_MSGEVENT_bydelSMS(sSMS_ID);
							   
							   //刪除系統訊息
							   myadp.delete_SYSMSG_Data(Sel_SysSMS);
							   refresh_SysMsgList();
							   et_MySms.setText("");
							   
							   
							   
							   AlertDialog.Builder ad_Rslt = new AlertDialog.Builder(Android_SMSvc.this);

							   DialogInterface.OnClickListener Done_Click = new DialogInterface.OnClickListener()
							   {
								   public void onClick(DialogInterface dialog, int which) 
								   {
									   dialog.cancel();
								   }
							   };
							   
							   if(Common.My_Language.equals("Chinese")) //中文
							   {
								   ad_Rslt.setTitle(R.string.tm_EVT_sms_del_rslt_title_ch);
								   ad_Rslt.setMessage("成功刪除...\n五的簡訊："+Sel_SysSMS_short +"\n預約簡訊："+del_cnt+" 筆");
								   ad_Rslt.setPositiveButton("確定",Done_Click);
							   }
							   else
							   {
								   ad_Rslt.setTitle(R.string.tm_EVT_sms_del_rslt_title);
								   ad_Rslt.setMessage("Deleted...\nMySMS："+Sel_SysSMS_short +
								                      "\nSMS Event #: "+del_cnt );
								   ad_Rslt.setPositiveButton("OK",Done_Click);
							   }
							   
							   ad_Rslt.show();
						   }
						};
						//建立按下[取消]的事件
						DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
						{
						   public void onClick(DialogInterface dialog, int which) 
						   {
							   dialog.cancel();
						   }
						};
						
						if(Common.My_Language.equals("Chinese")) //中文
						{
							ad_Action.setTitle(R.string.tm_EVT_sms_del_confirm_title_ch);
							ad_Action.setMessage(R.string.tm_EVT_sms_del_confirm_ch);
							ad_Action.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
							ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
						}
						else
						{
							ad_Action.setTitle(R.string.tm_EVT_sms_del_confirm_title);
							ad_Action.setMessage(R.string.tm_EVT_sms_del_confirm);
							ad_Action.setPositiveButton(R.string.tm_OP_yes,OkClick);
							ad_Action.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
						}
						
						ad_Action.show();
						
					}
					catch(Exception ex)
					{
						Log.e("JB_TAG","錯誤:"+ex.getMessage());
						Toast.makeText(Android_SMSvc.this,R.string.tm_EVT_sms_del_rslt_fail, Toast.LENGTH_LONG).show();
					}
				}
				
			}});
		
		/* 儲存簡訊事件  */
		imb_SaveSmsEvent.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					if(tv_CheckList_NAME_CP.getText()=="") //無收件人
					{
						//Toast.makeText(Android_SMSvc.this,R.string.tm_EVT_smsevent_save_noreceiver, Toast.LENGTH_SHORT).show();
						//顯示警告訊息
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
						ad_Action.setIcon(R.drawable.alert);
						
						
						DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
						{
						   public void onClick(DialogInterface dialog, int which) 
						   {
							   dialog.cancel();
						   }
						};
						
						if(Common.My_Language.equals("Chinese")) //中文
						{
							ad_Action.setTitle(R.string.tm_OP_alert_ch);
							ad_Action.setMessage(R.string.tm_EVT_smsevent_save_noreceiver_ch);
							ad_Action.setNegativeButton("關閉",CancelClick);
						}
						else
						{
							ad_Action.setTitle(R.string.tm_OP_alert);
							ad_Action.setMessage(R.string.tm_EVT_smsevent_save_noreceiver);
							ad_Action.setNegativeButton("Close",CancelClick);
						}
						
						ad_Action.show();
					}
					else
					{
						String sSN = SmsEvent_SN;
						String sName;
					    String sPHONE_NUM;
					    String sYear = bt_SetYear.getText().toString();
					    if(sYear.equals("每年")||sYear.equals("EveryYear"))
					    {
					    	sYear="0000";
					    }
					    
					    String sMon = bt_SetMon.getText().toString();
					    if( sMon.equals("每月")||sMon.equals("EveryMonth") )
					    {
					    	sMon="00";
					    }
					    else
					    {
					    	sMon=Common.PadLeft(sMon,'0',2);
					    }
					    
					    String sDay = bt_SetDay.getText().toString();
					    if( sDay.equals("每天")||sDay.equals("EveryDay") )
					    {
					    	sDay="00";
					    }
					    else
					    {
					    	sDay=Common.PadLeft(sDay,'0',2);
					    }
					    String sHour = Common.PadLeft(bt_SetHour.getText().toString(),'0',2);
					    String sMin = Common.PadLeft(bt_SetMin.getText().toString(),'0',2);
					    String sMSG_ID = Get_SMS_ID(et_MySms.getText().toString());
					    int iSEND2GEO = 0;
					    int iSEND2ME = 0;
					    if(cb_Send2GEO.isChecked()) 
					    {
					    	iSEND2GEO=1;
					    }
					    if(cb_Send2Me.isChecked()) 
					    {
					    	iSEND2ME=1;
					    }
					    
					
					    switch(SmsEvent_STS)
					    {
					        case 0://新增
					        	//從ListView(lv_Contact)取得被選取的連絡人
							    SparseBooleanArray pos = lv_Contact.getCheckedItemPositions();
							    //跑ap迴圈來檢查是否被選取
							    for(int i=0;i<lv_Contact.getCount();i++)
							    {
							        if(pos.get(i)) //被選取的會回傳true
							        {
							            sName=Sel_UserName.get(i).toString();
							            sPHONE_NUM=Sel_UserPhonenum.get(i).toString();
							            myadp.insert_MSGEVENT_Data(sSN, sName, sPHONE_NUM, 
							    		                           sYear, sMon, sDay, 
							    		                           sHour, sMin,
							    		                           sMSG_ID, iSEND2GEO, iSEND2ME);
							         }
							    }
							    pos=null;
							    
					            //Toast.makeText(Android_SMSvc.this,R.string.tm_EVT_smsevent_save_suc, Toast.LENGTH_SHORT).show();
							    //顯示訊息
								AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
								ad_Action.setIcon(R.drawable.done);

								DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
								{
								   public void onClick(DialogInterface dialog, int which) 
								   {
									   dialog.cancel();
								   }
								};
								
								if(Common.My_Language.equals("Chinese")) //中文
								{
									ad_Action.setTitle(R.string.tm_OP_done_ch);
									ad_Action.setMessage(R.string.tm_EVT_smsevent_save_suc_ch);
									ad_Action.setNegativeButton("關閉",CancelClick);
								}
								else
								{
									ad_Action.setTitle(R.string.tm_OP_done);
									ad_Action.setMessage(R.string.tm_EVT_smsevent_save_suc);
									ad_Action.setNegativeButton("Close",CancelClick);
								}
								
								ad_Action.show();
								
					            break;
					        default:
						        break;
					    }
					}
					
				}
				catch(Exception ex) //無法儲存
				{
					Log.e("JB_TAG", "錯誤："+ex.getMessage());
					Toast.makeText(Android_SMSvc.this,R.string.tm_EVT_smsevent_save_fail, Toast.LENGTH_SHORT).show();
				}
			}
			}
		);
		
		/* 更新簡訊事件  */
		imb_UpdateSmsEvent.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					if(tv_CheckList_NAME_CP.getText()=="") //沒有任何收件者
					{
						//Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_smsevent_save_noreceiver, Toast.LENGTH_SHORT).show();
						//顯示訊息
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
						ad_Action.setIcon(R.drawable.alert);
						
						DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
						{
						   public void onClick(DialogInterface dialog, int which) 
						   {
							   dialog.cancel();
						   }
						};
						
						if(Common.My_Language.equals("Chinese")) //中文
						{
							ad_Action.setTitle(R.string.tm_OP_alert_ch);
							ad_Action.setMessage(R.string.tm_EVT_smsevent_save_noreceiver_ch);
							ad_Action.setNegativeButton("關閉",CancelClick);
						}
						else
						{
							ad_Action.setTitle(R.string.tm_OP_alert);
							ad_Action.setMessage(R.string.tm_EVT_smsevent_save_noreceiver);
							ad_Action.setNegativeButton("Close",CancelClick);
						}
						
						ad_Action.show();
					}
					else
					{
						String sSN = SmsEvent_SN;
						String sName;
					    String sPHONE_NUM;
					    String sYear = bt_SetYear.getText().toString();
					    if(sYear.equals("每年")||sYear.equals("EveryYear") )
					    {
					    	sYear="0000";
					    }
					    
					    String sMon = bt_SetMon.getText().toString();
					    if(sMon.equals("每月")||sMon.equals("EveryMonth") )
					    {
					    	sMon="00";
					    }
					    else
					    {
					    	sMon=Common.PadLeft(sMon,'0',2);
					    }
					    
					    String sDay = bt_SetDay.getText().toString();
					    if( sDay.equals("每天")||sDay.equals("EveryDay") )
					    {
					    	sDay="00";
					    }
					    else
					    {
					    	sDay=Common.PadLeft(sDay,'0',2);
					    }
					    String sHour = Common.PadLeft(bt_SetHour.getText().toString(),'0',2);
					    String sMin = Common.PadLeft(bt_SetMin.getText().toString(),'0',2);
					    String sMSG_ID = Get_SMS_ID(et_MySms.getText().toString());
					    int iSEND2GEO = 0;
					    int iSEND2ME = 0;
					    if(cb_Send2GEO.isChecked()) 
					    {
					    	iSEND2GEO=1;
					    }
					    if(cb_Send2Me.isChecked()) 
					    {
					    	iSEND2ME=1;
					    }
					    
					
					    switch(SmsEvent_STS)
					    {
					        case 1://更新
					        	//從ListView(lv_Contact)取得被選取的連絡人
							    SparseBooleanArray pos2 = lv_Contact.getCheckedItemPositions();
							    //跑ap迴圈來檢查是否被選取
							    for(int i=0;i<lv_Contact.getCount();i++)
							    {
							        if(pos2.get(i)) //被選取的會回傳true
							        {
							            sName=Sel_UserName.get(i).toString();
							            sPHONE_NUM=Sel_UserPhonenum.get(i).toString();
							            myadp.update_MSGEVENT_Data(sSN, sName, sPHONE_NUM, 
							    		                           sYear, sMon, sDay, 
							    		                           sHour, sMin,
							    		                           sMSG_ID, iSEND2GEO, iSEND2ME);
							         }
							    }
							    
							    pos2=null;
							    
					            //Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_smsevent_upt_suc, Toast.LENGTH_SHORT).show();
					            
							    //顯示訊息
								AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
								ad_Action.setIcon(R.drawable.done);

								DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
								{
								   public void onClick(DialogInterface dialog, int which) 
								   {
									   dialog.cancel();
								   }
								};
								
								if(Common.My_Language.equals("Chinese")) //中文
								{
									ad_Action.setTitle(R.string.tm_OP_done_ch);
									ad_Action.setMessage(R.string.tm_EVT_smsevent_upt_suc_ch);
									ad_Action.setNegativeButton("關閉",CancelClick);
								}
								else
								{
									ad_Action.setTitle(R.string.tm_OP_done);
									ad_Action.setMessage(R.string.tm_EVT_smsevent_upt_suc);
									ad_Action.setNegativeButton("Close",CancelClick);
								}
								
								ad_Action.show();
					            
					            //重新initial
					            SmsEvent_STS=0;
					            imb_SaveSmsEvent.setVisibility(0);
					            imb_UpdateSmsEvent.setVisibility(8);
					            break;
					        default:
						        break;
					    }
					}
					
				}
				catch(Exception ex)
				{
					Log.e("JB_TAG", "更新簡訊事件錯誤:"+ex.getMessage());
					Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_smsevent_upt_fail, 
							       Toast.LENGTH_SHORT).show();
				}
			}
			}
		);
		
		/* 紀錄 : 傳送發送紀錄提醒簡訊    */
		cb_SendLog2Me.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) 
			{
				boolean bRslt = false;
				if(isChecked)
				{
					bRslt = myadp.update_LOGCONTACT_Data(1);
				}
				else
				{
					bRslt = myadp.update_LOGCONTACT_Data(0);
				}
				
				if(bRslt==false) //更新失敗
				{
					//....
				}
		}});
		
		/* 選擇簡訊紀錄提醒的收件者  */
		bt_SendLog2Me.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
                
				Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			    startActivityForResult(intent, PICK_CONTACT_SENDLOG2ME);
				
		}});
		
		
		/* 儲存使用者的設定  */
		imb_SaveSetup.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					myadp.tuncate_USERSETUP_Data();
					
					//1.開機自動啟動服務 ----------------------------
					if(cb_StartupService.isChecked()==true)
					{
						myadp.insert_USERSETUP_Data(OPTION_STARTUP_SERVICE_STR, "I", "1");
					}
					else
					{
						myadp.insert_USERSETUP_Data(OPTION_STARTUP_SERVICE_STR, "I", "0");
					}
					
					//2.發送通知 ----------------------------
					if(cb_SendNotify.isChecked()==true)
					{
						myadp.insert_USERSETUP_Data(OPTION_SEND_NOTIFICATION_STR, "I", "1");
					}
					else
					{
						myadp.insert_USERSETUP_Data(OPTION_SEND_NOTIFICATION_STR, "I", "0");
					}
					
					//3.回傳簡訊通知  ----------------------------
					if(cb_SendLog2Me.isChecked()==true)
					{
						myadp.insert_USERSETUP_Data(OPTION_SEND_LOG_STR, "I", "1");
					}
					else
					{
						myadp.insert_USERSETUP_Data(OPTION_SEND_LOG_STR, "I", "0");
					}
					//4.傳送地理位置            ----------------------------
					if(cb_SendGEO.isChecked()==true)
					{
						myadp.insert_USERSETUP_Data(OPTION_SEND_GEO_STR, "I", "1");
					}
					else
					{
						myadp.insert_USERSETUP_Data(OPTION_SEND_GEO_STR, "I", "0");
					}
					
					//顯示儲存成功訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
					ad_Action.setIcon(R.drawable.done);
					
					DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						   dialog.cancel();
					   }
					};
					if(Common.My_Language.equals("Chinese")) //中文
					{
						ad_Action.setTitle(R.string.tm_OP_done_ch);
						ad_Action.setMessage(R.string.tm_EVT_setup_save_suc_ch);
						ad_Action.setNegativeButton("關閉",CancelClick);
					}
					else
					{
						ad_Action.setTitle(R.string.tm_OP_done);
						ad_Action.setMessage(R.string.tm_EVT_setup_save_suc);
						ad_Action.setNegativeButton("Close",CancelClick);
					}
					
					ad_Action.show();
				}
				catch(Exception ex)
				{
					Log.e("JB_TAG","錯誤："+ex.getMessage());
					//顯示訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
					ad_Action.setIcon(R.drawable.done);
					
					
					DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						   dialog.cancel();
					   }
					};
					
					if(Common.My_Language.equals("Chinese")) //中文
					{
						ad_Action.setTitle(R.string.tm_OP_done_ch);
						ad_Action.setMessage(R.string.tm_EVT_setup_save_fail_ch);
						ad_Action.setNegativeButton("關閉",CancelClick);
					}
					else
					{
						ad_Action.setTitle(R.string.tm_OP_done);
						ad_Action.setMessage(R.string.tm_EVT_setup_save_fail);
						ad_Action.setNegativeButton("Close",CancelClick);
					}
					ad_Action.show();
				}
				
		}});
		
		/************************************************
		 * 取得目前系統 UTC (GMT) 時間
		 ************************************************/
		String time_now = Common.Get_NowTime(Android_SMSvc.this);
		String sYear=time_now.substring(0, 4);//取得年
		String sMon=time_now.substring(4, 6);//取得月
		String sDay=time_now.substring(6, 8);//取得日
		String sHour=time_now.substring(8, 10);//取得小時
		String sMin=time_now.substring(10, 12);//取得分鐘
		String sSec=time_now.substring(12, 14);//取得秒
		
		bt_SetYear.setText(sYear);
		bt_SetMon.setText(sMon);
		bt_SetDay.setText(sDay);
		bt_SetHour.setText(sHour);
		bt_SetMin.setText(sMin);
		
		
		/* 簡訊事件預定時間：年 */
		bt_SetYear.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				
				AlertDialog.Builder ad_Year = new AlertDialog.Builder(Android_SMSvc.this);
				
				//建立選擇的事件
				DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   Sel_pos=which;
				   }
				};
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   if(Sel_pos>=0)
					   {
						   bt_SetYear.setText( mYearList.get(Sel_pos).toString() );
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
				
				//取得目前系統 UTC (GMT) 時間--------------------------------
				String time_now = Common.Get_NowTime(Android_SMSvc.this);
				String sYear=time_now.substring(0, 4);//取得年
				
				//設定[年份]內容值
				mYearList.clear();
				int iYear=Integer.parseInt(sYear);
				
				if(Common.My_Language.equals("Chinese")) mYearList.add("每年"); //中文版
				else mYearList.add("EveryYear"); //英文版
				
				for(int i=1; i<=3; i++ )
				{
					mYearList.add(Integer.toString(iYear));
					iYear++;
				}
				
				String [] sYearList=new String[mYearList.size()];
				for(int i=0; i<mYearList.size(); i++)
				{
					sYearList[i]=mYearList.get(i).toString();
				}
				
				Sel_pos=1;
				ad_Year.setSingleChoiceItems(sYearList, Sel_pos, ListClick);
				sYearList=null;
				
				
				if(Common.My_Language.equals("Chinese")) //中文
				{
					ad_Year.setTitle(R.string.tm_OP_year_title_ch);
					ad_Year.setPositiveButton(R.string.tm_OP_yes_ch,OkClick); 
					ad_Year.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick); 
				}
				else
				{
					ad_Year.setTitle(R.string.tm_OP_year_title);
					ad_Year.setPositiveButton(R.string.tm_OP_yes,OkClick); 
					ad_Year.setNegativeButton(R.string.tm_OP_cancel,CancelClick); 
				}
				ad_Year.show();
			}
		}
		);
		
		/* 簡訊事件預定時間：月份 */
		bt_SetMon.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder ad_Mon = new AlertDialog.Builder(Android_SMSvc.this);
				
				//建立選擇的事件
				DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   Sel_pos=which;
				   }
				};
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   if(Sel_pos>=0)
					   {
						   bt_SetMon.setText( mMonList.get(Sel_pos).toString() );
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
				
				//取得目前系統 UTC (GMT) 時間--------------------------------
				String time_now = Common.Get_NowTime(Android_SMSvc.this);
				String sMon=time_now.substring(4, 6);//取得月
				
				//設定[月份]內容值
				mMonList.clear();
				//int iMon=Integer.parseInt(sMon);
				String[] strMon = new String[13];
				int Sel_Pos_Mon=0; //選擇月份
				
				if(Common.My_Language.equals("Chinese")) strMon[0]="每月"; //中文版
				else strMon[0]="EveryMonth"; //英文版
				
				mMonList.add(strMon[0]);
				for(int i=1; i<=12; i++ )
				{
					strMon[i]=Common.PadLeft(Integer.toString(i),'0',2);
					mMonList.add(strMon[i]);
					if(strMon[i].equalsIgnoreCase(sMon))
					{
						Sel_Pos_Mon=i;
					}
				}
				
				Sel_pos=Sel_Pos_Mon;
				ad_Mon.setSingleChoiceItems(strMon, Sel_Pos_Mon, ListClick);
				strMon=null;
				
				if(Common.My_Language.equals("Chinese")) //中文
				{
					ad_Mon.setTitle( R.string.tm_OP_mon_title_ch );
					ad_Mon.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
					ad_Mon.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
				}
				else
				{
					ad_Mon.setTitle( R.string.tm_OP_mon_title );
					ad_Mon.setPositiveButton(R.string.tm_OP_yes,OkClick);
					ad_Mon.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
				}
				
				ad_Mon.show();
			}
		}
		);

		/* 簡訊事件預定時間：日 */
		bt_SetDay.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{				
				AlertDialog.Builder ad_Day = new AlertDialog.Builder(Android_SMSvc.this);
				
				//建立選擇的事件
				DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   Sel_pos=which;
				   }
				};
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   if(Sel_pos>=0)
					   {
						   bt_SetDay.setText( mDayList.get(Sel_pos).toString() );
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
				
				//取得目前系統 UTC (GMT) 時間--------------------------------
				String time_now = Common.Get_NowTime(Android_SMSvc.this);
				String sDay=time_now.substring(6, 8);//取得日
				
				//設定[日]內容值
				mDayList.clear();
				String[] strDay = new String[32];
				int Sel_Pos_Day=0; //選擇日
				
				if(Common.My_Language.equals("Chinese")) strDay[0]="每天"; //中文版
				else strDay[0]="EveryDay"; //英文版
				
				mDayList.add(strDay[0]);
				for(int i=1; i<=31; i++ )
				{
					strDay[i]=Common.PadLeft(Integer.toString(i),'0',2);
					mDayList.add(strDay[i]);
					if(strDay[i].equalsIgnoreCase(sDay))
					{
						Sel_Pos_Day=i;
					}
				}
				
				Sel_pos=Sel_Pos_Day;
				ad_Day.setSingleChoiceItems(strDay, Sel_Pos_Day, ListClick);
				strDay=null;
				
				if(Common.My_Language.equals("Chinese")) //中文
				{
					ad_Day.setTitle(R.string.tm_OP_day_title_ch);
					ad_Day.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
					ad_Day.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
				}
				else
				{
					ad_Day.setTitle(R.string.tm_OP_day_title);
					ad_Day.setPositiveButton(R.string.tm_OP_yes,OkClick);
					ad_Day.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
				}
				
				ad_Day.show();
			}
		}
		);
		
		/* 簡訊事件預定時間：時 */
		bt_SetHour.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String time_now = Common.Get_NowTime(Android_SMSvc.this);
				int HOUR_OF_DAY=Integer.parseInt(time_now.substring(8, 10));
				int MIN_OF_DAY=Integer.parseInt(time_now.substring(10, 12));
				new TimePickerDialog(Android_SMSvc.this,new TimePickerDialog.OnTimeSetListener()
						{
							public void onTimeSet(TimePicker view, int hourOfDay,int minute)
							{
								//設定時間
								bt_SetHour.setText(Integer.toString(hourOfDay));
								bt_SetMin.setText(Integer.toString(minute));
							}
						},HOUR_OF_DAY, MIN_OF_DAY, true).show();
				}
			}
		);
		
		
		/* 簡訊事件預定時間：分 */
		bt_SetMin.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String time_now = Common.Get_NowTime(Android_SMSvc.this);
				int HOUR_OF_DAY=Integer.parseInt(time_now.substring(8, 10));
				int MIN_OF_DAY=Integer.parseInt(time_now.substring(10, 12));
				new TimePickerDialog(Android_SMSvc.this,new TimePickerDialog.OnTimeSetListener()
						{
							public void onTimeSet(TimePicker view, int hourOfDay,int minute)
							{
								//設定時間
								bt_SetHour.setText(Integer.toString(hourOfDay));
								bt_SetMin.setText(Integer.toString(minute));
							}
						},HOUR_OF_DAY, MIN_OF_DAY, true).show();
				}
			}
		);
		
		/* *********************
		 * 開啟已經存在的資料庫
		 * ********************/
		myadp=new MyDataBaseAdapter(this);
		myadp.open();
		
		/* *********************
		 * 重新載入所有系統簡訊 
		 * ********************/
		refresh_SysMsgList();

		/* ************************
		 * 設定SharedPreferences
		 * ************************/
		Setup_SharePref();
		
		/* ************************
		 * 從資料庫作使用者預設設定
		 * ************************/
		Load_UserSetup();
		
	}//onCreate ends

	
	
	/*處理功能選單事件*/
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//得到目前勾選的MenuItem的ID,
		int item_id = item.getItemId();

		switch (item_id)
		{
		    case R.id.actions:
		    	
		    	AlertDialog.Builder ad_Page = new AlertDialog.Builder(Android_SMSvc.this);
		    	//ad_Page.setTitle(R.string.tm_OP_day_title);
				
				//建立選擇的事件
				DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   ChgActivity(which+1);
				   }
				};
				
				String[] strPage = new String[3];
				if(Common.My_Language.equals("Chinese")) //中文版
				{
					strPage[0] = "簡訊預約";
					strPage[1] = "簡訊管理";
					strPage[2] = "備份&還原";
				}
				else //英文版
				{
					strPage[0] = "SMS Event";
					strPage[1] = "SMS Browser";
					strPage[2] = "Backup & Recovery";
				}
				ad_Page.setItems(strPage, ListClick);
				//ad_Day.setSingleChoiceItems(strDay, Sel_Pos_Day, ListClick);
				strPage=null;
				ad_Page.show();
			    break;
			    
			case R.id.about:
				
				AlertDialog.Builder ad_about = new AlertDialog.Builder(Android_SMSvc.this);
				ad_about.setMessage(Common.My_About_string);
				DialogInterface.OnClickListener Done_Click = new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				};
				
				if(Common.My_Language.equals("Chinese")) //中文
				{
					ad_about.setTitle("關於...");
					ad_about.setPositiveButton("關閉",Done_Click);
				}
				else
				{
					ad_about.setTitle("Information");
					ad_about.setPositiveButton("Close",Done_Click);
				}
				ad_about.show();
				
				break;
			case R.id.exit:
				Android_SMSvc.this.finish();
				break;
		}
		return true;
	}

	/************************************
	 * Function Name: ChgActivity
	 * Input:
	 * Output:
	 * Description: 
	 *************************************/
	private void ChgActivity(int iPage)
	{
		Intent intent=new Intent();
		switch(iPage) //設定從此activity轉頁到哪一個activity
		{
		case 1:
			intent.setClass(Android_SMSvc.this, Android_SMSvc.class); 
			break;
		case 2:
			intent.setClass(Android_SMSvc.this, Android_SMSbs.class);
			break;
		case 3:
			intent.setClass(Android_SMSvc.this, Android_SMSbk.class);
			break;
		default:
			intent.setClass(Android_SMSvc.this, Android_SMSvc.class);
			break;
		}
		startActivityForResult(intent, 0);
		this.finish();
	}
	/************************************
	 * Function Name:Show_SysMsgList
	 * Input:
	 * Output:
	 * Description: 以AlertDialog顯示所有系統簡訊 
	 *************************************/
	public void Show_SysMsgList()
	{
		Sel_pos=-1;
		AlertDialog.Builder ad_SysSms = new AlertDialog.Builder(Android_SMSvc.this);
		
		//建立選擇的事件
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
		{
		   public void onClick(DialogInterface dialog, int which) 
		   {
			  //Toast.makeText(getApplicationContext(), SysMsg[which], Toast.LENGTH_SHORT).show();
			   Sel_pos=which;
		   }
		};
		//建立按下[確定]的事件
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
		{
		   public void onClick(DialogInterface dialog, int which) 
		   {
			   if(Sel_pos>=0)
			   {
				   et_MySms.setText( mSysMsgList.get(Sel_pos).toString() );
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
		String [] sSysMsgList=new String[mSysMsgList.size()];
		for(int i=0; i<sSysMsgList.length; i++)
		{
			sSysMsgList[i]=mSysMsgList.get(i).toString();
		}
		ad_SysSms.setSingleChoiceItems(sSysMsgList, -1, ListClick);
		sSysMsgList=null;
		
		if(Common.My_Language.equals("Chinese")) 
		{
			ad_SysSms.setTitle(R.string.tm_OP_option_ch);
			ad_SysSms.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
	        ad_SysSms.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
		}
		else
		{
			ad_SysSms.setTitle(R.string.tm_OP_option);
			ad_SysSms.setPositiveButton(R.string.tm_OP_yes,OkClick);
	        ad_SysSms.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
		}
		
        ad_SysSms.show();
	}
	/************************************
	 * Function Name:refresh_SysMsgList
	 * Input:
	 * Output:
	 * Description: 重新載入所有系統簡訊 
	 *************************************/
	public void refresh_SysMsgList()
	{
		mSysMsgList.clear();
		//取得資料庫JB_SYSMSG的Cursor
		Cursor cur = myadp.fetch_SYSMSG_AllData();
		
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();
			try
			{
			   for (int i = 0; i < numRows; ++i)
			   {
			      //String msg_id = cur.getString(0);
			      String msg_desc = cur.getString(1);
			      mSysMsgList.add(msg_desc);
			      cur.moveToNext();
			   }
			   
			   cur.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤 :"+ex.getMessage());
				Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_smsevent_load_fail, 
						       Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	/************************************
	 * Function Name: Load_UserSetup
	 * Input:
	 * Output:
	 * Description: 從資料庫設定使用者預設選項
	 *************************************/
	public void Load_UserSetup()
	{
		//1.開機自動啟動服務 ----------------------------
		Cursor cur01 = myadp.fetch_USERSETUP_Data(OPTION_STARTUP_SERVICE_STR);
		
		if (cur01 != null && cur01.getCount() > 0)
		{
			cur01.moveToFirst();
			try
			{
			   int iFlag01 =  Integer.parseInt(cur01.getString(2));
			   switch(iFlag01)
			   {
			   case 0:
				   cb_StartupService.setChecked(false);
				   break;
			   case 1:
				   cb_StartupService.setChecked(true);
				   break;
			   }
			   cur01.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			}
		}
		else
		{
			cb_StartupService.setChecked(false);
		}
		
		//2.發送通知 ----------------------------
        Cursor cur02 = myadp.fetch_USERSETUP_Data(OPTION_SEND_NOTIFICATION_STR);
		
		if (cur02 != null && cur02.getCount() > 0)
		{
			cur02.moveToFirst();
			try
			{
			   int iFlag02 =  Integer.parseInt(cur02.getString(2));
			   switch(iFlag02)
			   {
			   case 0:
				   cb_SendNotify.setChecked(false);
				   break;
			   case 1:
				   cb_SendNotify.setChecked(true);
				   break;
			   }
			   cur02.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			}
		}
		else
		{
			cb_StartupService.setChecked(false);
		}
		
		//3.回傳簡訊通知           ----------------------------
        Cursor cur03 = myadp.fetch_USERSETUP_Data(OPTION_SEND_LOG_STR);
		
		if (cur03 != null && cur03.getCount() > 0)
		{
			cur03.moveToFirst();
			try
			{
			   int iFlag03 =  Integer.parseInt(cur03.getString(2));
			   switch(iFlag03)
			   {
			   case 0:
				   cb_SendLog2Me.setChecked(false);
				   break;
			   case 1:
				   cb_SendLog2Me.setChecked(true);
				   break;
			   }
			   cur03.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			}
		}
		else
		{
			cb_SendLog2Me.setChecked(false);
		}
		
        Cursor cur_sp = myadp.fetch_LOGCONTACT_AllData();
		
		if (cur_sp != null && cur_sp.getCount() > 0)
		{
			cur_sp.moveToFirst();
			try
			{
			   String sSEND_NAME =  cur_sp.getString(0);
			   tv_SendLog2Me.setText(sSEND_NAME);
			   cur_sp.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			}
		}
		
		//4.傳送地理位置            ----------------------------
        Cursor cur04 = myadp.fetch_USERSETUP_Data(OPTION_SEND_GEO_STR);
		
		if (cur04 != null && cur04.getCount() > 0)
		{
			cur04.moveToFirst();
			try
			{
			   int iFlag04 =  Integer.parseInt(cur04.getString(2));
			   switch(iFlag04)
			   {
			   case 0:
				   cb_SendGEO.setChecked(false);
				   break;
			   case 1:
				   cb_SendGEO.setChecked(true);
				   break;
			   }
			   cur04.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			}
		}
		else
		{
			cb_SendGEO.setChecked(false);
		}
		
	}
	/************************************
	 * Function Name:Setup_SharePref
	 * Input:
	 * Output:
	 * Description: 設定SharedPreference 
	 *************************************/
	public void Setup_SharePref()
	{
		//....
	}
	
	/************************************
	 * Function Name:List_SmsEvent
	 * Input:
	 * Output:
	 * Description: 簡訊事件列表 
	 *************************************/
	public void List_SmsEvent()
	{
		Sms_Event_List.clear();
		HashMap<String, String> map;
		
		
		//獲得資料庫Phones的Cursor
		Cursor cur = myadp.fetch_MSGEVENT_AllData();
		
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();

			try
			{
			   for (int i = 0; i < numRows; ++i)
			   {
				  String SN = cur.getString(0);
				  String NAME = cur.getString(1);												
				  String PHONE_NUM = cur.getString(2);
				  String YEAR = cur.getString(3);
				  if( YEAR.equals("0000") )
				  {
					if(Common.My_Language.equals("Chinese"))
					{
						YEAR = "每年"; //中文版
					}
					else
					{
						YEAR = "EveryYear"; //英文版
					}
				  }
				  
				  String MON = cur.getString(4);
				  if( MON.equals("00") )
				  {
					if(Common.My_Language.equals("Chinese"))
					{
						MON = "每月"; //中文版
					}
					else
					{
						MON = "EveryMonth"; //英文版
					}
				  }
				  
				  String DAY = cur.getString(5);
				  if( DAY.equals("00") )
				  {
					if(Common.My_Language.equals("Chinese"))
					{
						DAY = "每天"; //中文版
					}
				    else
					{
				    	DAY = "EveryDay"; //英文版
					}
				  }
				  
				  String HOUR = cur.getString(6);
				  String MIN = cur.getString(7);
				  
				  String SMS_ID = cur.getString(8);
				  String SMS_DESC = Get_SMS_DESC(SMS_ID);
				  
				  int SEND_GEO_FLG = Integer.parseInt(cur.getString(9));
				  String SEND_GEO="N";
				  if(SEND_GEO_FLG==1)
				  {
					  SEND_GEO="Y";
				  }
				  
				  int SEND_ME_FLG = Integer.parseInt(cur.getString(10));
				  String SEND_ME="N";
				  if(SEND_ME_FLG==1)
				  {
					  SEND_ME="Y";
				  }
				  
				  String LST_SEND_DT = cur.getString(11);
				  //SmsEventList.add(SN+"#"+NAME+"#"+PHONE_NUM+"#"+YEAR+"/"+MON+"/"+DAY+"-"+HOUR+":"+MIN+"("+MSG_ID+")");
				  
				  map = new HashMap<String, String>();
				  map.clear();
				  map.put("SN", SN);
				  map.put("NAME", NAME);
				  map.put("PHONE_NUM", PHONE_NUM);
				  map.put("DATE_TIME", YEAR+"/"+MON+"/"+DAY+" "+HOUR+":"+MIN);
				  map.put("SMS_DESC", SMS_DESC);
				  map.put("SEND_GEO", SEND_GEO);
				  map.put("SEND_ME", SEND_ME);
				  map.put("LST_SEND_DT", LST_SEND_DT);
				  Sms_Event_List.add(map);
					
			      cur.moveToNext();
			   }
			   
			   cur.close();
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤 :"+ex.getMessage());
				Toast.makeText(Android_SMSvc.this, R.string.tm_EVT_smsevent_load_fail, 
						       Toast.LENGTH_SHORT).show();
			}
		}
		
		
	
		//設定顯示列表ListView
		/*
		SimpleAdapter SmpAdapter = 
			        new SimpleAdapter(this, Sms_Event_List, 
			        R.layout.sms_event,
		            new String[] {"SN", "NAME", "PHONE_NUM", "DATE_TIME", "SMS_DESC", "SEND_ME", "LST_SEND_DT"}, 
		            new int[] {R.id.CELL_SN, R.id.CELL_NAME, R.id.CELL_PHONE_NUM, R.id.CELL_DATETIME, 
				               R.id.CELL_SMS_DESC,R.id.CELL_SEND_ME,R.id.CELL_LSTSENDDT});
		*/
		SimpleAdapter SmpAdapter = 
	        new SimpleAdapter(this, Sms_Event_List, 
	        R.layout.sms_event,
            new String[] {"SN", "NAME", "PHONE_NUM", "DATE_TIME", "SMS_DESC", "SEND_GEO", "SEND_ME", "LST_SEND_DT"}, 
            new int[] {R.id.CELL_SN, R.id.CELL_NAME, R.id.CELL_PHONE_NUM, R.id.CELL_DATETIME, 
		               R.id.CELL_SMS_DESC,R.id.CELL_SEND_GEO, R.id.CELL_SEND_ME,R.id.CELL_LSTSENDDT});
		lv_SmsEvent.setAdapter(SmpAdapter);
		lv_SmsEvent.setOnItemClickListener(SmsEvent_ClkListener); //指定Item點擊事件:SmsEvent_ClkListener
        
		
		SmpAdapter=null;
	}
	/************************************
	 * Function Name:Show_CheckList
	 * Input:
	 * Output:
	 * Description: 儲存(或修改)前列出相關資訊
	 *************************************/
	public void Show_CheckList()
	{
		switch(SmsEvent_STS)
		{
		case 0://新增
			
			SmsEvent_SN = Common.Get_NowTime(this); //重新設定一組SN
			
			//設定cb_Send2GEO
			if(SendGEO_singleflg==1)
		    {
				cb_Send2GEO.setChecked(true);
		    }
		    else
		    {
		    	cb_Send2GEO.setChecked(false);
		    }
			
			//設定cb_Send2Me
			
			if(SendLog2Me_singleflg==1)
		    {
		    	cb_Send2Me.setChecked(true);
		    }
		    else
		    {
		    	cb_Send2Me.setChecked(false);
		    }
			
			break;
			
		case 1://修改
			
			//設定cb_Send2GEO
			if(SendGEO_singleflg==1)
		    {
				cb_Send2GEO.setChecked(true);
		    }
		    else
		    {
		    	cb_Send2GEO.setChecked(false);
		    }
			
			//設定cb_Send2Me
			if(SendLog2Me_singleflg==1)
		    {
		    	cb_Send2Me.setChecked(true);
		    }
		    else
		    {
		    	cb_Send2Me.setChecked(false);
		    }
			
			
			break;
		default:
			break;
		}
		String sSN= SmsEvent_SN;
		String sName = "";
		//從ListView(lv_Contact)取得被選取的連絡人
		SparseBooleanArray pos = lv_Contact.getCheckedItemPositions();
		//跑ap迴圈來檢查是否被選取
		for(int i=0;i<lv_Contact.getCount();i++)
		{
		    if(pos.get(i)) //被選取的會回傳true
		    {
		    	sName = sName+lv_Contact.getItemAtPosition(i).toString()+"; ";
		    }
		}

		String sYear = bt_SetYear.getText().toString();
		String sMon = Common.PadLeft(bt_SetMon.getText().toString(),'0',2);
		String sDay = Common.PadLeft(bt_SetDay.getText().toString(),'0',2);
		String sHour = Common.PadLeft(bt_SetHour.getText().toString(),'0',2);
		String sMin = Common.PadLeft(bt_SetMin.getText().toString(),'0',2);
		String sDateTime = sYear+"/"+sMon+"/"+sDay+" "+sHour+":"+sMin;
		tv_CheckList_SN_CP.setText(sSN);
	    tv_CheckList_NAME_CP.setText(sName);
	    tv_CheckList_DATETIME_CP.setText(sDateTime);
	    tv_CheckList_SMS_DESC_CP.setText(et_MySms.getText());
	    
	}
	/************************************
	 * Function Name:SmsEvent_ClkListener
	 * Input:
	 * Output:
	 * Description: 簡訊事件item click監聽
	 *************************************/
	public OnItemClickListener SmsEvent_ClkListener =  new OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Sel_SmsEvent_func=0; //預設功能清單選擇:[修改]
			Sel_SmsEvent_index=arg2; //選擇的訊息事件index
			
			AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
			ad_Action.setIcon(R.drawable.question);
			
			//建立選擇的事件
			DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which) 
			   {
				   Sel_SmsEvent_func=which;
			   }
			};
			//建立按下[確定]的事件
			DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which) 
			   {
			      switch(Sel_SmsEvent_func)
			      {
			      case 0: //更新
			    	  SmsEvent_STS=1;//設定修改
			    	  imb_UpdateSmsEvent.setVisibility(0);
			    	  imb_SaveSmsEvent.setVisibility(8);
			    	  
			    	  HashMap<String, String> map_mod = Sms_Event_List.get(Sel_SmsEvent_index);
			    	  
			    	  //SN -------------------------------
			    	  SmsEvent_SN=map_mod.get("SN");
			    	  //NAME -----------------------------
			    	  Clear_Contact();
			    	  String sName=map_mod.get("NAME");
			    	  Sel_UserName.add(sName);
			    	  if(Sel_UserName.size()>0)
			    	  {
			    		  ArrayAdapter<String> Contact_Adp = 
			    						new ArrayAdapter<String>(Android_SMSvc.this,android.R.layout.simple_list_item_multiple_choice, Sel_UserName);
			    		  lv_Contact.setAdapter(Contact_Adp);
			    		  //預設全部勾選
				          for(int i=0; i<lv_Contact.getCount(); i++)
				          {
				        	  lv_Contact.setItemChecked(i, true);
                          }
			    		  Contact_Adp=null;
			    	  }
			    	  //PHONE_NUM ------------------------
			    	  String sPhoneNum=map_mod.get("PHONE_NUM");
			          Sel_UserPhonenum.add( sPhoneNum );
			          
			    	  //DATE_TIME ------------------------
			    	  String sDataTime = map_mod.get("DATE_TIME");
			    	  
			    	  String tmpDataTime = sDataTime;
			    	  String sYear="";
			    	  String sMon="";
			    	  String sDay=""; 
			    	  String sHour="";
			    	  String sMin="";
			    	  int iLine = -1; //分隔符號 '/' or ':'
			    	  for(int i=0; i<5; i++) //抓 年,月,日,小時,分 共五個
			    	  {
			    		  switch(i)
			    		  {
			    		  case 0:
			    			  iLine = tmpDataTime.indexOf("/");
			    			  sYear = tmpDataTime.substring(0, iLine).trim();
			    			  tmpDataTime = tmpDataTime.substring(iLine+1, tmpDataTime.length());
			    			  break;
			    		  case 1:
			    			  iLine = tmpDataTime.indexOf("/");
			    			  sMon = tmpDataTime.substring(0, iLine).trim();
			    			  tmpDataTime = tmpDataTime.substring(iLine+1, tmpDataTime.length());
			    			  break;
			    		  case 2:
			    			  iLine = tmpDataTime.indexOf(" ");
			    			  sDay = tmpDataTime.substring(0, iLine).trim();
			    			  tmpDataTime = tmpDataTime.substring(iLine+1, tmpDataTime.length());
			    			  break;
			    		  case 3:
			    			  iLine = tmpDataTime.indexOf(":");
			    			  sHour = tmpDataTime.substring(0, iLine).trim();
			    			  tmpDataTime = tmpDataTime.substring(iLine+1, tmpDataTime.length());
			    			  break;
			    		  case 4:
			    			  sMin = tmpDataTime;
			    			  break;
			    		  default:
			    			  break;
			    		  }
			    	  }
			    	  
			    	  
			  		  //String sYear=sDataTime.substring(0, 4);//取得年
			  		  //String sMon=sDataTime.substring(5, 7);//取得月
			  		  //String sDay=sDataTime.substring(8, 10);//取得日
			  		  //String sHour=sDataTime.substring(11, 13);//取得小時
			  		  //String sMin=sDataTime.substring(14, 16);//取得分鐘
			    	  
			  		  bt_SetYear.setText(sYear);
			  		  bt_SetMon.setText(sMon);
			  		  bt_SetDay.setText(sDay);
			  		  bt_SetHour.setText(sHour);
			  		  bt_SetMin.setText(sMin);
			  		  
			    	  //SMS_DESC -------------------------
			    	  et_MySms.setText(map_mod.get("SMS_DESC"));
			    	  
			    	  //SEND GEO --------------------------
			    	  String sSEND_GEO=map_mod.get("SEND_GEO");
			    	  if(sSEND_GEO=="Y")
			    	  {
			    		  SendGEO_singleflg=1;
			    	  }
			    	  else
			    	  {
			    		  SendGEO_singleflg=0;
			    	  }
			    	  
			    	  //SEND2ME --------------------------
			    	  String sSEND_ME=map_mod.get("SEND_ME");
			    	  if(sSEND_ME=="Y")
			    	  {
			    		  SendLog2Me_singleflg=1;
			    	  }
			    	  else
			    	  {
			    		  SendLog2Me_singleflg=0;
			    	  }
			    	  
			    	  
			    	  //改變頁面到儲存頁面---------------------------------------------------
			    	  Set_Layout_Visible(5);
					  Show_CheckList();
			    	  
			    	  map_mod=null;
			    	  break;
			    	  
			      case 1:
                      HashMap<String, String> map_del = Sms_Event_List.get(Sel_SmsEvent_index);
			    	  
			    	  //SN -------------------------------
			    	  String sSN_del=map_del.get("SN");
			    	  //NAME -----------------------------
			    	  String sName_del=map_del.get("NAME");
			    	  //PHONE_NUM ------------------------
			    	  String sPhoneNum_del=map_del.get("PHONE_NUM");
			    	  try
			    	  {
			    		  myadp.delete_MSGEVENT_Data(sSN_del, sName_del, sPhoneNum_del); //從資料庫刪除一筆資料
			    		  List_SmsEvent(); //重新載入簡訊事件清單
			    		  
			    		  //Toast.makeText(getApplicationContext(), R.string.tm_EVT_smsevent_del_suc, Toast.LENGTH_SHORT).show();
			    		  //顯示刪除成功訊息
			    		  AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSvc.this);
			    		  ad_Action.setIcon(R.drawable.done);
						
			    		  DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
			    		  {
			    			  public void onClick(DialogInterface dialog, int which) 
			    			  {
			    				  dialog.cancel();
			    			  }
			    		  };
			    		  
			    		  if(Common.My_Language.equals("Chinese")) //中文版 
			    		  {
			    			  ad_Action.setTitle(R.string.tm_OP_done_ch);
				    		  ad_Action.setMessage(R.string.tm_EVT_smsevent_del_suc_ch);
			    			  ad_Action.setNegativeButton("關閉",CancelClick);
			    		  }
			    		  else
			    		  {
			    			  ad_Action.setTitle(R.string.tm_OP_done);
				    		  ad_Action.setMessage(R.string.tm_EVT_smsevent_del_suc);
			    			  ad_Action.setNegativeButton("Close",CancelClick);
			    		  }
			    		  
			    		  ad_Action.show();
			    	  }
			    	  catch(Exception ex)
			    	  {
			    		  Log.e("JB_TAG", "錯誤:"+ex.getMessage());
			    		  Toast.makeText(getApplicationContext(), R.string.tm_EVT_smsevent_del_fail, Toast.LENGTH_LONG).show();  
			    	  }
			    	  break;
			      default:
			    	  break;
			      }
			   }
			};
			//建立按下[取消]的事件
			DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which) 
			   {
				   dialog.cancel();
			   }
			};
			
			String  sActionList[]=new String[2];			
			if(Common.My_Language.equals("Chinese")) //中文版 
			{
				sActionList[0]="修改";
				sActionList[1]="刪除";
				ad_Action.setTitle(R.string.tm_OP_option_ch);
				ad_Action.setPositiveButton(R.string.tm_OP_yes_ch, OkClick);
				ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch, CancelClick);
			}
			else
			{
				sActionList[0]="Update";
				sActionList[1]="Delete";
				ad_Action.setTitle(R.string.tm_OP_option);
				ad_Action.setPositiveButton(R.string.tm_OP_yes, OkClick);
				ad_Action.setNegativeButton(R.string.tm_OP_cancel, CancelClick);
			}
			ad_Action.setSingleChoiceItems(sActionList, 0, ListClick);
			sActionList=null;
			
			ad_Action.show();
		}  
    };
    /************************************
	 * Function Name:Clear_Contact
	 * Input:
	 * Output:
	 * Description: 清除目前選擇的連絡人
	 *************************************/
	public void Clear_Contact()
	{
		Sel_UserName.clear();
		Sel_UserPhonenum.clear();
		
		if( lv_Contact.getCount() > 0 )
		{
			ArrayAdapter<String> Empty_List = 
				new ArrayAdapter<String>(Android_SMSvc.this,android.R.layout.simple_list_item_multiple_choice, Sel_UserName);
			lv_Contact.setAdapter(Empty_List);
			Empty_List=null;
		}
	}
	/************************************
	 * Function Name:Get_SMS_DESC
	 * Input:
	 * Output:
	 * Description: 取得簡訊編號對應的簡訊敘述
	 *************************************/
	public String Get_SMS_DESC(String SMS_ID)
	{
		String SMS_DESC="";
		
		// 獲得資料庫的Cursor
		Cursor cur = myadp.fetch_SYSMSG_Data(SMS_ID);
		
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();

			try
			{
			   SMS_DESC = cur.getString(0);
			   
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
				//Toast.makeText(Android_SMSvc.this,"無法查詢SQLite! "+e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		cur.close();
		return SMS_DESC;
	}
	/************************************
	 * Function Name:Get_SMS_ID
	 * Input:
	 * Output:
	 * Description: 取得簡訊敘述對應的簡訊編號
	 *************************************/
	public String Get_SMS_ID(String SMS_DESC)
	{
		String SMS_ID="";
		
		// 獲得資料庫的Cursor
		Cursor cur = myadp.fetch_SYSMSG_ID(SMS_DESC);
		
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();

			try
			{
			   SMS_ID = cur.getString(0);
			   
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG", "錯誤:"+ex.getMessage());
				//Toast.makeText(Android_SMSvc.this,"無法查詢SQLite! "+e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		cur.close();
		return SMS_ID;
	}
	
	/************************************
	 * Function Name: mBroadcastReceiver
	 * Input:
	 * Output:
	 * Description: 
	 *************************************/
	/*
	public class mBroadcastReceiver extends BroadcastReceiver
	{
	    @Override
	    public void onReceive(Context context, Intent intent)
	    {
	      // TODO Auto-generated method stub
	      try
	      {
	        if(intent.getAction().toString().equals(Service_Smsvc.JB_BROATCAST_ID))
	        {
	          Bundle bunde = intent.getExtras();
	          String strParam = bunde.getString("STR_PARAM");
	          //tv_Sms.setText(strParam);
	          
	          Android_SMSvc.started = true;
	          Android_SMSvc.updateServiceStatus();
	        }
	      }
	      catch(Exception e)
	      {
	        e.printStackTrace();
	      }
	    }
	}
	*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch (requestCode) 
	    {  
	      case PICK_CONTACT_SUBACTIVITY: 
	      case PICK_CONTACT_SENDLOG2ME:
	        String strName = "";
		    String strPhone = "";
	    	if(data != null)
	    	{
	    		final Uri uriRet = data.getData(); 
	    		if(uriRet != null) 
	    		{ 
	    			try 
	    			{
	    				Cursor c = managedQuery(uriRet, null, null, null, null);
	    				c.moveToFirst();
	    				
	                    
	    				/* 取得_id這個欄位得值 */
	    				int contactId = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
	           

	                    /*用_id去查詢電話的Cursor */
	    				Cursor curContacts = getContentResolver().
	    				query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	    						null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);

	    				if(curContacts.getCount()>0)
	    				{
	                        /* 2.0可以允許User設定多組電話號碼，我們只要撈出第一組電話 */
	    					curContacts.moveToFirst();
	                        /* 取得通訊錄名子*/
	    					strName = curContacts.getString(
	    							curContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	             
	                        /* 取得通訊錄電話*/
	    					strPhone = curContacts.getString(
	    							  curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	    					
	    					if(requestCode==PICK_CONTACT_SUBACTIVITY)
	    					{
	    						Sel_UserName.add(strName);
	    						Sel_UserPhonenum.add(strPhone);
	    					}
	    				}
	    				else
	    				{
	                        //nothing selected
	    				}
	    			} 
	    			catch(Exception e) 
	    			{             
	    				e.printStackTrace(); 
	    			} 
	    		}
	    	}
	        
	    	if(requestCode==PICK_CONTACT_SUBACTIVITY)
			{
	    		//將選擇的通訊錄名字加入lv_Contact
	    		if(Sel_UserName.size()>0)
	    		{
	    			ArrayAdapter<String> Sel_User_List = 
				                     new ArrayAdapter<String>(Android_SMSvc.this,
						                 android.R.layout.simple_list_item_multiple_choice, 
						                 Sel_UserName);
	    			lv_Contact.setAdapter(Sel_User_List);
	        	
	                //預設全部勾選
	    			for(int i=0; i<lv_Contact.getCount(); i++)
	    			{
	    				lv_Contact.setItemChecked(i, true);
	    			}
	    			Sel_User_List=null;
	    		}
			}
	    	else if(requestCode==PICK_CONTACT_SENDLOG2ME)
	    	{
	    		if(strName!="" && strPhone!="")
	    		{
	    			tv_SendLog2Me.setText(strName);
	    		
	    			//紀錄在資料庫
	    			cb_SendLog2Me.setClickable(true);
	    			myadp.truncate_LOGCONTACT_Data();
	    			myadp.insert_LOGCONTACT_Data(strName, strPhone, 1);
	    		}
	    	}
	        break; 
	        
	        default:
	        	
	    		break;
	    }   

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/************************************
	* Function Name: onPause
	* Input: 
	* Output: 
	* Description: 
	*************************************/
	@Override
	protected void onPause() {
		
		/* Unregister */
	    //unregisterReceiver(mReceiver01);
	    
	    /* Stop Service */
	    //Intent i = new Intent( Android_SMSvc.this, Service_Smsvc.class );
	    //stopService(i);	    
		super.onPause(); 
	}

	/************************************
	* Function Name: onDestroy
	* Input: 
	* Output: 
	* Description: 
	*************************************/
	@Override
	protected void onDestroy() {
		myadp.close();
		adView.destroy();
		super.onDestroy();
	}

	/************************************
	* Function Name: onResume
	* Input: 
	* Output: 
	* Description: 
	*************************************/
	@Override
	protected void onResume() {
		
		//調整UI語言
		Adjust_Language_Layout();
		//設定UI寬高
		Adjust_UI_Layout();
		
		/* Register Receiver */
		//IntentFilter mFilter01;
	    //mFilter01 = new IntentFilter(Service_Smsvc.JB_BROATCAST_ID);
	    //mReceiver01 = new mSMSReceiver();
	    //registerReceiver(mReceiver01, mFilter01);
		
	    /* Start Service */
		//Start_SMS_Send_Service();
	    
	    super.onResume();
	}

	
	/************************************
	 * Function Name: updateServiceStatus
	 * Input: 
	 * Output:
	 * Description: Service更新時觸發事件
	 *************************************/
	public static void updateServiceStatus()
	{
		Log.i("JB_TAG","Service status: "+ Svc_Sts_Desc);
	}

	
	/************************************
	 * Function Name: Set_Layout_Visible
	 * Input: int
	 * Output:
	 * Description: 顯示隱藏Layout
	 *************************************/
	public void Set_Layout_Visible(int index)
	{
		/*
		Layout_Contact  : 1
        Layout_DateTime : 2
        Layout_Sms      : 3
        Layout_SmsEvent : 4
        Layout_Save     : 5
        Layout_Setup    : 6
	    */
		Layout_admob.setVisibility(0);
		
		switch(index)  //VISIBLE:0, INVISIBLE:4, or GONE:8
		{
		case 0: //全部隱藏
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(8);
			break;
		case 1:
			Layout_Contact.setVisibility(0); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(8);
			break;	
		case 2:
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(0);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(8);
			break;
		case 3:
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(0);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(8);
			break;
		case 4:
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(0);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(8);
			break;
		case 5:
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(0);
			Layout_Setup.setVisibility(8);
			break;
		case 6:
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(0);
			break;
		default:
			Layout_Contact.setVisibility(8); 
			Layout_DateTime.setVisibility(8);
			Layout_Sms.setVisibility(8);
			Layout_SmsEvent.setVisibility(8);
			Layout_Save.setVisibility(8);
			Layout_Setup.setVisibility(8);
			break;
		}
	}
	
	/************************************
	 * Function Name: Adjust_UI_Layout
	 * Input:
	 * Output:
	 * Description: 調整UI寬高
	 *************************************/
	private void Adjust_UI_Layout()
	{
		/* 設定UI新寬高 */
		DisplayMetrics dm = getResources().getDisplayMetrics();  
		int ScreenWidth = dm.widthPixels;  
		int ScreenHeight = dm.heightPixels;
		
		int iMain_Pixel=0;    //中央圖新寬高
		int iOption1_WPixel=0; //主要選項(Type1)寬
		int iOption1_HPixel=0; //主要選項(Type1)高
		int iOption2_WPixel=0; //主要選項(Type2)寬
		int iOption2_HPixel=0; //主要選項(Type2)高
		int iSetDateTime_WPixel=0; //設定日期按鈕寬
		int iSetDateTime_HPixel=0; //設定日期按鈕高
		int iImgBtn_Pixel=0;  //圖型按鈕寬高
		
		if(ScreenWidth<ScreenHeight) //直立手機
		{
			iMain_Pixel=(int)(ScreenWidth*0.3);
			iOption1_WPixel=(int)(ScreenWidth*0.35);
			iOption1_HPixel=(int)(ScreenHeight*0.3/3);
			iOption2_WPixel=(int)(ScreenWidth*0.3);
			iOption2_HPixel=(int)(ScreenHeight*0.3/3);
			iSetDateTime_WPixel=(int)(ScreenWidth*0.3);
			iSetDateTime_HPixel=iSetDateTime_WPixel/2;
			iImgBtn_Pixel=(int)(ScreenWidth*0.25);
		}
		else  //橫放
		{
			iMain_Pixel=(int)(ScreenHeight*0.2);
			iMain_Pixel=(int)(ScreenHeight*0.2);
			iOption1_WPixel=(int)(ScreenHeight*0.4);
			iOption1_HPixel=(int)(ScreenWidth*0.2/3);
			iOption2_WPixel=(int)(ScreenHeight*0.3);
			iOption2_HPixel=(int)(ScreenWidth*0.2/3);
			iSetDateTime_WPixel=(int)(ScreenHeight*0.3);
			iSetDateTime_HPixel=iSetDateTime_WPixel/2;
			iImgBtn_Pixel=(int)(ScreenHeight*0.2);
		}

        //主要選項
		iv_Title.getLayoutParams().width = iMain_Pixel;
		iv_Title.getLayoutParams().height = iMain_Pixel;
		imb_contact.getLayoutParams().width = iOption1_WPixel;
		imb_contact.getLayoutParams().height = iOption1_HPixel;
		imb_time.getLayoutParams().width = iOption2_WPixel;
		imb_time.getLayoutParams().height = iOption2_HPixel;
		imb_sms.getLayoutParams().width = iOption1_WPixel;
		imb_sms.getLayoutParams().height = iOption1_HPixel;
		imb_eventlog.getLayoutParams().width = iOption2_WPixel;
		imb_eventlog.getLayoutParams().height = iOption2_HPixel;
		imb_setup.getLayoutParams().width = iOption1_WPixel;
		imb_setup.getLayoutParams().height = iOption1_HPixel;
		imb_save.getLayoutParams().width = iOption1_WPixel;
		imb_save.getLayoutParams().height = iOption1_HPixel;
		
		//圖型按鈕
		imb_open_contact.getLayoutParams().width = iImgBtn_Pixel;
		imb_open_contact.getLayoutParams().height = iImgBtn_Pixel;
		imb_clr_contact.getLayoutParams().width = iImgBtn_Pixel;
		imb_clr_contact.getLayoutParams().height = iImgBtn_Pixel;
		imb_SaveSmsEvent.getLayoutParams().width = iImgBtn_Pixel;
		imb_SaveSmsEvent.getLayoutParams().height = iImgBtn_Pixel;
		imb_UpdateSmsEvent.getLayoutParams().width = iImgBtn_Pixel;
		imb_UpdateSmsEvent.getLayoutParams().height = iImgBtn_Pixel;
		imb_SaveSetup.getLayoutParams().width = iImgBtn_Pixel;
		imb_SaveSetup.getLayoutParams().height = iImgBtn_Pixel;
		
		//設定日期按鈕
		bt_SetYear.getLayoutParams().width = iSetDateTime_WPixel;
		bt_SetYear.getLayoutParams().height = iSetDateTime_HPixel;
		bt_SetMon.getLayoutParams().width = iSetDateTime_WPixel;
		bt_SetMon.getLayoutParams().height = iSetDateTime_HPixel;
		bt_SetDay.getLayoutParams().width = iSetDateTime_WPixel;
		bt_SetDay.getLayoutParams().height = iSetDateTime_HPixel;
		bt_SetHour.getLayoutParams().width = iSetDateTime_WPixel;
		bt_SetHour.getLayoutParams().height = iSetDateTime_HPixel;
		bt_SetMin.getLayoutParams().width = iSetDateTime_WPixel;
		bt_SetMin.getLayoutParams().height = iSetDateTime_HPixel;
		
		/*
		bt_SetYear.setBackgroundColor(Color.GRAY);
		bt_SetMon.setBackgroundColor(Color.GRAY);
		bt_SetDay.setBackgroundColor(Color.GRAY);
		bt_SetHour.setBackgroundColor(Color.GRAY);
		bt_SetMin.setBackgroundColor(Color.GRAY);
		*/
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
			imb_contact.setBackgroundResource(R.drawable.imb_main01_01);
			imb_time.setBackgroundResource(R.drawable.imb_main01_02);
			imb_sms.setBackgroundResource(R.drawable.imb_main01_03);
			imb_save.setBackgroundResource(R.drawable.imb_main01_04);
			imb_eventlog.setBackgroundResource(R.drawable.imb_main01_05);
			imb_setup.setBackgroundResource(R.drawable.imb_main01_06);
			
			imb_open_contact.setBackgroundResource(R.drawable.bt_open_contact);
			imb_clr_contact.setBackgroundResource(R.drawable.bt_clr_contact);
			imb_SaveSmsEvent.setBackgroundResource(R.drawable.bt_savese);
			imb_UpdateSmsEvent.setBackgroundResource(R.drawable.bt_uptse);
			imb_SaveSetup.setBackgroundResource(R.drawable.imb_savesetup);
		
			bt_SysSms.setText(R.string.bt_SysSms);
			bt_DelSms.setText(R.string.bt_DelSms);
			bt_ClrSms.setText(R.string.bt_ClrSms);
			bt_SaveSms.setText(R.string.bt_SaveSms);
			bt_SendLog2Me.setText(R.string.bt_SendLog2Me);
			
			cb_StartupService.setText(R.string.cb_StartupService);
			cb_SendNotify.setText(R.string.cb_SendNotify);
			cb_SendLog2Me.setText(R.string.cb_SendLog2Me);
			cb_SendGEO.setText(R.string.cb_SendGEO);
			
			tv_CheckList_SN.setText(R.string.tv_CheckList_SN);
			tv_CheckList_NAME.setText(R.string.tv_CheckList_NAME);
			tv_CheckList_DATETIME.setText(R.string.tv_CheckList_DATETIME);
			tv_CheckList_SMS_DESC.setText(R.string.tv_CheckList_SMS_DESC);
			tv_CheckList_SENDGEO.setText(R.string.tv_CheckList_SENDGEO);
			tv_CheckList_SEBD2ME.setText(R.string.tv_CheckList_SEBD2ME);
			
			tv_NewSms.setText(R.string.tv_NewSms);
			tv_MySms.setText(R.string.tv_MySms);
			
		}
		else if( sMyLan.equals("Chinese") )
		{
			imb_contact.setBackgroundResource(R.drawable.imb_main01_01_ch);
			imb_time.setBackgroundResource(R.drawable.imb_main01_02_ch);
			imb_sms.setBackgroundResource(R.drawable.imb_main01_03_ch);
			imb_save.setBackgroundResource(R.drawable.imb_main01_04_ch);
			imb_eventlog.setBackgroundResource(R.drawable.imb_main01_05_ch);
			imb_setup.setBackgroundResource(R.drawable.imb_main01_06_ch);
			
			imb_open_contact.setBackgroundResource(R.drawable.bt_open_contact_ch);
			imb_clr_contact.setBackgroundResource(R.drawable.bt_clr_contact_ch);
			imb_SaveSmsEvent.setBackgroundResource(R.drawable.bt_savese_ch);
			imb_UpdateSmsEvent.setBackgroundResource(R.drawable.bt_uptse_ch);
			imb_SaveSetup.setBackgroundResource(R.drawable.imb_savesetup_ch);
		
			bt_SysSms.setText(R.string.bt_SysSms_ch);
			bt_DelSms.setText(R.string.bt_DelSms_ch);
			bt_ClrSms.setText(R.string.bt_ClrSms_ch);
			bt_SaveSms.setText(R.string.bt_SaveSms_ch);
			bt_SendLog2Me.setText(R.string.bt_SendLog2Me_ch);
			
			cb_StartupService.setText(R.string.cb_StartupService_ch);
			cb_SendNotify.setText(R.string.cb_SendNotify_ch);
			cb_SendLog2Me.setText(R.string.cb_SendLog2Me_ch);
			cb_SendGEO.setText(R.string.cb_SendGEO_ch);
			
			tv_CheckList_SN.setText(R.string.tv_CheckList_SN_ch);
			tv_CheckList_NAME.setText(R.string.tv_CheckList_NAME_ch);
			tv_CheckList_DATETIME.setText(R.string.tv_CheckList_DATETIME_ch);
			tv_CheckList_SMS_DESC.setText(R.string.tv_CheckList_SMS_DESC_ch);
			tv_CheckList_SENDGEO.setText(R.string.tv_CheckList_SENDGEO_ch);
			tv_CheckList_SEBD2ME.setText(R.string.tv_CheckList_SEBD2ME_ch);
			
			tv_NewSms.setText(R.string.tv_NewSms_ch);
			tv_MySms.setText(R.string.tv_MySms_ch);
		}
	}
	/************************************
	 * Function Name: Show_Admob_Banner
	 * Input: int
	 * Output:
	 * Description: 顯示廣告
	 *************************************/
	public void Show_Admob_Banner()
	{
		// Create the adView
	    adView = new AdView(this, AdSize.BANNER, "a14ea8cd393696c");

	    // Add the adView to it
	    Layout_admob.addView(adView);

	    // Initiate a generic request to load it with an ad
	    adView.loadAd(new AdRequest());
	}
}




