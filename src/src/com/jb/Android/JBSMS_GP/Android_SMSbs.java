package com.jb.Android.JBSMS_GP;

import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class Android_SMSbs  extends Activity
{
	private ImageView iv_Title               = null;
	
	private ImageButton imb_browse           = null;
	private ImageButton imb_latest           = null;
	private ImageButton imb_lock             = null;
	private ImageButton imb_unlock           = null;
	private ImageButton imb_menu03           = null;
	private ImageButton imb_menu04           = null;
	
	private ImageButton imb_save_sms         = null;
	private ImageButton imb_reply_sms         = null;
    private ImageButton imb_skip_sms         = null;
    private ImageButton imb_del_sms          = null;
    private ImageButton imb_back_sms         = null;
    private ImageButton imb_lock_sms         = null;
    private ImageButton imb_unlock_sms       = null;
    
    private TextView tv_MySmsDateTime        = null;
    private TextView tv_MySmsSender          = null;
    private TextView tv_MySmsPNum            = null;
    private TextView tv_MySms                = null;
    private TextView tv_OpenSmsSN            = null;  
    private TextView tv_OpenSms              = null;
    private TextView tv_LockPwd              = null;
    private TextView tv_UnlockPwd            = null;
    
    private EditText et_ReplySms             = null;
    
    private EditText et_InputLockPwd         = null;
    private EditText et_InputUnlockPwd       = null;
    
    private LinearLayout Layout_EncSms       = null;  //index : 0
    private LinearLayout Layout_OpenSms      = null;  //index : 1
    private LinearLayout Layout_SmsBox       = null;  //index : 2
    private LinearLayout Layout_Lock         = null;  //index : 5
    private LinearLayout Layout_Unlock       = null;  //index : 6
    private LinearLayout Layout_admob        = null;
    
    //Admob AdView
	private AdView adView;
	
    /* SQLite */
    private MyDataBaseAdapter myadp = null;
	
	/* 簡訊Box列表  */
    private ArrayList<HashMap<String, String>> Sms_Box_List = new ArrayList<HashMap<String, String>>();
	private ListView lv_SmsBox;
	//private String SmsBox_SN;
	
	private int Sel_SmsBox_index=-1; //點選的訊息事件index
	private int Sel_SmsBox_func=-1; //User選取的訊息事件處理(刪除)
	
	private boolean LockSMS_flg = false;
	
	
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
	* Function Name: onDestroy
	* Input: 
	* Output: 
	* Description: 
	*************************************/
    @Override
	protected void onDestroy() 
    {
		myadp.close();
		adView.destroy();
		super.onDestroy();
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
		setContentView(R.layout.sms2);
		
		iv_Title = (ImageView)this.findViewById(R.id.iv_Title);
		imb_browse=(ImageButton)this.findViewById(R.id.imb_browse);
		imb_latest=(ImageButton)this.findViewById(R.id.imb_latest);
		imb_lock=(ImageButton)this.findViewById(R.id.imb_lock);
		imb_unlock=(ImageButton)this.findViewById(R.id.imb_unlock);
		imb_menu03=(ImageButton)this.findViewById(R.id.imb_menu03);
	    imb_menu04=(ImageButton)this.findViewById(R.id.imb_menu04);
		
		imb_save_sms=(ImageButton)this.findViewById(R.id.imb_save_sms);
		imb_reply_sms=(ImageButton)this.findViewById(R.id.imb_reply_sms);
	    imb_skip_sms=(ImageButton)this.findViewById(R.id.imb_skip_sms);
	    imb_del_sms=(ImageButton)this.findViewById(R.id.imb_del_sms);
	    imb_back_sms=(ImageButton)this.findViewById(R.id.imb_back_sms);
	    imb_lock_sms=(ImageButton)this.findViewById(R.id.imb_lock_sms);
	    imb_unlock_sms=(ImageButton)this.findViewById(R.id.imb_unlock_sms);
	    
	    
	    tv_MySmsDateTime = (TextView)this.findViewById(R.id.tv_MySmsDateTime);
	    tv_MySmsSender = (TextView)this.findViewById(R.id.tv_MySmsSender);
	    tv_MySmsPNum = (TextView)this.findViewById(R.id.tv_MySmsPNum);
	    tv_MySms = (TextView)this.findViewById(R.id.tv_MySms);
	    tv_OpenSmsSN = (TextView)this.findViewById(R.id.tv_OpenSmsSN);
	    tv_OpenSms = (TextView)this.findViewById(R.id.tv_OpenSms);
	    tv_LockPwd = (TextView)this.findViewById(R.id.tv_InputLockPwd);
	    tv_UnlockPwd = (TextView)this.findViewById(R.id.tv_InputUnlockPwd);
	    
	    et_ReplySms = (EditText)this.findViewById(R.id.et_ReplySms); 
	    
	    Layout_EncSms=(LinearLayout)this.findViewById(R.id.Layout_EncSms);
	    Layout_SmsBox=(LinearLayout)this.findViewById(R.id.Layout_SmsBox);
	    Layout_OpenSms=(LinearLayout)this.findViewById(R.id.Layout_OpenSms);
	    Layout_Lock=(LinearLayout)this.findViewById(R.id.Layout_Lock);
	    Layout_Unlock=(LinearLayout)this.findViewById(R.id.Layout_Unlock);
	    Layout_admob=(LinearLayout)this.findViewById(R.id.Layout_admob);
	    
	    et_InputLockPwd=(EditText)this.findViewById(R.id.et_InputLockPwd);
	    et_InputUnlockPwd=(EditText)this.findViewById(R.id.et_InputUnlockPwd);
	    
	    lv_SmsBox = (ListView)this.findViewById(R.id.lv_SmsBox);
	    
	    
	    //先隱藏每個圖層
	    Set_Layout_Visible(0);
	    
	    //開啟廣告
		Show_Admob_Banner();
		
		//設定元件
		Initial_UI();
		
		/* 瀏覽簡訊紀錄  */
		imb_browse.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Set_Layout_Visible(3);
				List_SmsBox();
			}
		});
		
		/* 查看最後一次收到的簡訊  */
		imb_latest.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Set_Layout_Visible(1);
				if( tv_MySms.length() == 0) //沒有內容，不顯示
				{
					imb_save_sms.setVisibility(8);
					imb_reply_sms.setVisibility(8);
					et_ReplySms.setVisibility(8);
				}
				else
				{
					imb_save_sms.setVisibility(1);
					imb_reply_sms.setVisibility(1);
					et_ReplySms.setVisibility(1);
				}
			}
		});
		
		/* Lock頁面  */
		imb_lock.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Set_Layout_Visible(5);
			}
		});
		
		/* Unlock頁面  */
		imb_unlock.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Set_Layout_Visible(6);
			}
		});
		
		/* 選擇Skip簡訊  */
		imb_skip_sms.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Android_SMSbs.this.finish();
			}
		});
		
		/* 選擇儲存加密簡訊  */
		imb_save_sms.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//簡訊序號
				//String sSn = tv_MySmsSN.getText().toString();
				//日期時間
				String sDateTime = tv_MySmsDateTime.getText().toString();
				//簡訊寄件人
				String sSender = tv_MySmsSender.getText().toString();
				//簡訊內容(含日期時間 發信人)
				String sMySms = tv_MySms.getText().toString();
				
				if(sMySms=="")
				{
					//Toast.makeText(Android_SMSbs.this,R.string.tm_SMS_nosave_empty, Toast.LENGTH_SHORT).show();
					//顯示警告訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
		    		  	ad_Action.setMessage(R.string.tm_SMS_nosave_empty_ch);
		    		  	ad_Action.setNegativeButton("關閉",CancelClick);
	    		  	}
	    		  	else //英文
	    		  	{
	    		  		ad_Action.setTitle(R.string.tm_OP_done);
		    		  	ad_Action.setMessage(R.string.tm_SMS_nosave_empty);
		    		  	ad_Action.setNegativeButton("Close",CancelClick);
	    		  	}
	    		  	ad_Action.show();
				}
				else
				{
					//作加密 -----------------------------------------------------
					byte[] bKey = null;
					byte[] bEncryptSms = null;
					String strKey="";
					String sEncryptSms = "";
					//String sDecryptSms ="";
					try
					{
						//設定要使用的加密演算法
						KeyGenerator keyG = KeyGenerator.getInstance("AES");
						//設定key的長度
						keyG.init(256);
						//產生SecretKey
						SecretKey secuK = keyG.generateKey();
						//取得要用來加密的key(解密也需使用這把key)
						bKey = secuK.getEncoded();
						strKey = Encrypt.Convert_ByteToHexString(bKey);
						
						//sEncryptSms = Encrypt.Get_EncrptSMS(Android_SMSbs.this, bKey, sMySms);
						bEncryptSms = Encrypt.Get_EncrptSMS_Byte(Android_SMSbs.this, bKey, sMySms);
						
						boolean bRslt_db = Save_SMS_toBOX(sDateTime, true, sSender, bKey, bEncryptSms);
						
						
						//顯示警告訊息
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
						
		    		  	DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
		    		  	{
		    			  public void onClick(DialogInterface dialog, int which) 
		    			  {
		    				  dialog.cancel();
		    			  }
		    		  	};
		    		  	
		    		  	if( bRslt_db==true)
						{
							ad_Action.setIcon(R.drawable.done);
							if(Common.My_Language.equals("Chinese")) //中文
							{
								ad_Action.setTitle(R.string.tm_OP_done_ch);
				    		  	ad_Action.setMessage(R.string.tm_SMS_saved_suc_ch);
				    		  	ad_Action.setNegativeButton("關閉",CancelClick);
							}
							else //英文
							{
								ad_Action.setTitle(R.string.tm_OP_done);
				    		  	ad_Action.setMessage(R.string.tm_SMS_saved_suc);
				    		  	ad_Action.setNegativeButton("Close",CancelClick);
							}
						}
						else
						{
							ad_Action.setIcon(R.drawable.alert);
							if(Common.My_Language.equals("Chinese")) //中文
							{
								ad_Action.setTitle(R.string.tm_OP_alert_ch);
				    		  	ad_Action.setMessage(R.string.tm_SMS_saved_fail_ch);
				    		  	ad_Action.setNegativeButton("關閉",CancelClick);
							}
							else //英文
							{
								ad_Action.setTitle(R.string.tm_OP_alert);
				    		  	ad_Action.setMessage(R.string.tm_SMS_saved_fail);
				    		  	ad_Action.setNegativeButton("Close",CancelClick);
							}
						}
		    		  	
		    		  	ad_Action.show();
						
					}
					catch(Exception ex)
					{
						Log.e("JB_TAG","錯誤:"+ex.getMessage());
					}
					
				}
			}
		});
		
		/* 選擇回覆簡訊  */
		imb_reply_sms.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				/*
				Intent it = new Intent(Intent.ACTION_VIEW);  
				it.putExtra("sms_body", "The SMS text");   
				it.setType("vnd.android-dir/mms-sms");  
				startActivity(it);
				*/  
				
				String sPhoneNum = tv_MySmsPNum.getText().toString();
				String sSendSMS = et_ReplySms.getText().toString();
				boolean bRslt = Send_SMS_To(sPhoneNum, sSendSMS);
				if(bRslt==true)
				{
					//顯示發送成功訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
		    		  	ad_Action.setMessage(R.string.tm_SMS_reply_suc_ch);
	    		  		ad_Action.setNegativeButton("關閉",CancelClick);
					}
	    		  	else   //英文
	    		  	{
	    		  		ad_Action.setTitle(R.string.tm_OP_done);
		    		  	ad_Action.setMessage(R.string.tm_SMS_reply_suc);
	    		  		ad_Action.setNegativeButton("Close",CancelClick);
	    		  	}
	    		  	
	    		  	ad_Action.show();
				}
				else
				{
					//顯示發送失敗訊息
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
	    		  		ad_Action.setTitle(R.string.tm_OP_done_ch);
		    		  	ad_Action.setMessage(R.string.tm_SMS_reply_fail_ch);
	    		  		ad_Action.setNegativeButton("關閉",CancelClick);
					}
	    		  	else   //英文
	    		  	{
	    		  		ad_Action.setTitle(R.string.tm_OP_done);
		    		  	ad_Action.setMessage(R.string.tm_SMS_reply_fail);
	    		  		ad_Action.setNegativeButton("Close",CancelClick);
	    		  	}
	    		  	
	    		  	ad_Action.show();
				}
				
			}
		});
		
		/*點擊回覆簡訊的EditText*/
		et_ReplySms.setOnClickListener(new EditText.OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				et_ReplySms.setEnabled(true);
				et_ReplySms.setText("");
			}
		});
		
		/* 回簡訊列表 */
		imb_back_sms.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				Set_Layout_Visible(3);
				List_SmsBox();
			}
		});
		
		/* 刪除簡訊 */
		imb_del_sms.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				final String sSN_del = tv_OpenSmsSN.getText().toString();
				
				//確認是否備份
				AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
				ad_Action.setIcon(R.drawable.question);
				
				
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					 try
					 {
						myadp.delete_SMSBOX_Data(sSN_del); //從資料庫刪除一筆資料
						Set_Layout_Visible(3); //回簡訊列表
						List_SmsBox(); //重新載入簡訊事件清單
						
						
						//Toast.makeText(getApplicationContext(), R.string.tm_SMS_del_suc, Toast.LENGTH_SHORT).show();
						//顯示刪除成功訊息
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
			    		  	ad_Action.setMessage(R.string.tm_SMS_del_suc_ch);
		    		  		ad_Action.setNegativeButton("關閉",CancelClick);
						}
		    		  	else   //英文
		    		  	{
		    		  		ad_Action.setTitle(R.string.tm_OP_done);
			    		  	ad_Action.setMessage(R.string.tm_SMS_del_suc);
		    		  		ad_Action.setNegativeButton("Close",CancelClick);
		    		  	}
		    		  	
		    		  	ad_Action.show();
					 }
					 catch(Exception ex)
					 {
						Log.e("JB_TAG", "刪除簡訊錯誤："+ex.getMessage());
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
				
				if(Common.My_Language.equals("Chinese"))
				{
					ad_Action.setTitle(R.string.tm_SMS_del_confirm_title_ch);
					ad_Action.setMessage(R.string.tm_SMS_del_confirm_str_ch);
					ad_Action.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
					ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
				}
				else
				{
					ad_Action.setTitle(R.string.tm_SMS_del_confirm_title);
					ad_Action.setMessage(R.string.tm_SMS_del_confirm_str);
					ad_Action.setPositiveButton(R.string.tm_OP_yes,OkClick);
					ad_Action.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
				}
				ad_Action.show();
				
				
			}
		});
		
		/* Lock 簡訊  */
		imb_lock_sms.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				final String sLockPwd = et_InputLockPwd.getText().toString();
				
				if(sLockPwd.length()==0)
				{
					Toast.makeText(Android_SMSbs.this, R.string.tm_SMS_Lock_NoPwd, Toast.LENGTH_SHORT).show();
				}
				else
				{
					//確認是否用此密碼Lock
					AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
					ad_Action.setIcon(R.drawable.question);
					
					//建立按下[確定]的事件
					DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						 try
						 {
							SharePref mySharePref = new SharePref(Android_SMSbs.this);
							mySharePref.WriteData(0, 0, "LockPwd", sLockPwd);
							mySharePref=null;
							LockSMS_flg = true;
							
							imb_lock.setEnabled(false);
							imb_unlock.setEnabled(true);
							
							//顯示訊息 --------
							AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
				    		  	ad_Action.setMessage(R.string.tm_SMS_Lock_ok_ch);
			    		  		ad_Action.setNegativeButton("關閉",CancelClick);
							}
			    		  	else
			    		  	{
			    		  		ad_Action.setTitle(R.string.tm_OP_done);
				    		  	ad_Action.setMessage(R.string.tm_SMS_Lock_ok);
			    		  		ad_Action.setNegativeButton("Close",CancelClick);
			    		  	}
			    		  	
			    		  	ad_Action.show();
			    		  	
			    		  	
			    		  	Set_Layout_Visible(0);
						 }
						 catch(Exception ex)
						 {
							Log.e("JB_TAG", "錯誤："+ex.getMessage());
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
					
					
					if(Common.My_Language.equals("Chinese")) //中文
					{
						ad_Action.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
						ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
						ad_Action.setTitle(R.string.tm_SMS_Lock_confirm_title_ch);
						ad_Action.setMessage("密碼:"+sLockPwd +" ,請確認?" );
					}
					else //英文
					{
						ad_Action.setPositiveButton(R.string.tm_OP_yes,OkClick);
						ad_Action.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
						ad_Action.setTitle(R.string.tm_SMS_Lock_confirm_title);
						ad_Action.setMessage("Password:"+sLockPwd +" ,Confirm?" );
					}
					
					ad_Action.show();

				}
			}
		});
		
		/* Unlock 簡訊  */
		imb_unlock_sms.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					String sUnlockPwd = et_InputUnlockPwd.getText().toString();
				
					SharePref mySharePref = new SharePref(Android_SMSbs.this);
					String sLockPwd = mySharePref.ReadData(0, 0, "LockPwd");
					
					if( sUnlockPwd.equals(sLockPwd)) //成功Unlock
					{
						mySharePref.WriteData(0, 0, "LockPwd", "");
						
						LockSMS_flg = false;
						imb_lock.setEnabled(true);
						imb_unlock.setEnabled(false);
						
						//顯示訊息
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
			    		  	ad_Action.setMessage(R.string.tm_SMS_Unlock_suc_ch);
		    		  		ad_Action.setNegativeButton("關閉",CancelClick);
		    		  	}
		    		  	else
		    		  	{
		    		  		ad_Action.setTitle(R.string.tm_OP_done);
			    		  	ad_Action.setMessage(R.string.tm_SMS_Unlock_suc);
		    		  		ad_Action.setNegativeButton("Close",CancelClick);
		    		  	}
		    		  	
		    		  	ad_Action.show();
					}
					else
					{
						//顯示訊息
						AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
			    		  	ad_Action.setMessage(R.string.tm_SMS_Unlock_fail_ch);
		    		  		ad_Action.setNegativeButton("關閉",CancelClick);
		    		  	}
		    		  	else
		    		  	{
		    		  		ad_Action.setTitle(R.string.tm_OP_alert);
			    		  	ad_Action.setMessage(R.string.tm_SMS_Unlock_fail);
		    		  		ad_Action.setNegativeButton("Close",CancelClick);
		    		  	}
		    		  	
		    		  	ad_Action.show();
					}
					mySharePref=null;
				}
				catch(Exception ex)
				{
					Log.e("JB_TAG", "錯誤："+ex.getMessage());
				}
			}
		});
		
		/************************************************
		 * 取得目前系統 UTC (GMT) 時間
		 ************************************************/
		String time_now = Common.Get_NowTime(Android_SMSbs.this);
		String sYear=time_now.substring(0, 4);//取得年
		String sMon=time_now.substring(4, 6);//取得月
		String sDay=time_now.substring(6, 8);//取得日
		String sHour=time_now.substring(8, 10);//取得小時
		String sMin=time_now.substring(10, 12);//取得分鐘
		String sSec=time_now.substring(12, 14);//取得秒
		
		/***************************************************
		 * 若有新簡訊，會從mSMSReceiver接收到傳回來的簡訊內容
		 ***************************************************/
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			if(this.getIntent().getAction().toString().equals(mSMSReceiver.BROATCAST_ACTION_ID))
            {
				//隱藏所有Layout，除了顯示簡訊內容外
				Set_Layout_Visible(1);
				
				Bundle bunde = this.getIntent().getExtras();
				
				String sSn = sYear+sMon+sDay+sHour+sMin+sSec; //如 20111010131030
				String sDateTime = sYear+"/"+sMon+"/"+sDay+" "+sHour+":"+sMin+":"+sSec; //如 2011/10/10 13:10:30
				String sFromPNum = bunde.getString("STR_PARAM01");
				String sSenderName = Common.Get_ContactName(this, sFromPNum);
				String sPrefix = "From "+sSenderName;
				String sMySms = bunde.getString("STR_PARAM02");
				
				tv_MySmsDateTime.setText(sDateTime);
				tv_MySmsPNum.setText(sFromPNum);
				tv_MySmsSender.setText(sSenderName);
				tv_MySms.setText(sDateTime +"\n\n"+ sPrefix +"\n\n"+ sMySms );
				
				
				et_ReplySms.setText("Enter reply text...");
				et_ReplySms.clearFocus();
				tv_MySms.requestFocus();
				/*
				String sDateTime = sYear+"/"+sMon+"/"+sDay+" "+sHour+":"+sMin;
				String sFromPNum = bunde.getString("STR_PARAM01");
				String sPrefix = "From "+Common.Get_ContactName(this, sFromPNum);
				String sMySms = bunde.getString("STR_PARAM02");
				String sEncryptSms = "";
				String sDecryptSms ="";
				try
				{
					//設定要使用的加密演算法
					KeyGenerator keyG = KeyGenerator.getInstance("AES");
					//設定key的長度
					keyG.init(256);
					//產生SecretKey
					SecretKey secuK = keyG.generateKey();
					//取得要用來加密的key(解密也需使用這把key)
					byte[] key = secuK.getEncoded();
					sEncryptSms = Encrypt.Get_EncrptSMS(this, key, sMySms);
					
					
					sDecryptSms = Encrypt.Get_DecrptSMS(
							this, Encrypt.Convert_ByteToHexString(key), sEncryptSms);
				}
				catch(Exception ex)
				{
					Log.e("JB_TAG","錯誤:"+ex.getMessage());
				}
				
				
				tv_MySms.setText(sDateTime +"\n\n"+ sPrefix +"\n\n"+ sMySms 
						         +"\n\n"+sEncryptSms+"\n\n"+sDecryptSms);
		        */
            }
		} 
		
		
		/* *********************
		 * 開啟已經存在的資料庫
		 * ********************/
		myadp=new MyDataBaseAdapter(this);
		myadp.open();
		
		
	} //onCreate Ends
	
	/************************************
	* Function Name: onResume
	* Input: 
	* Output: 
	* Description: 
	*************************************/
	@Override
	protected void onResume() 
	{
		//調整UI語言
		Adjust_Language_Layout();
		//設定UI寬高
		Adjust_UI_Layout();
		
		
	    /* Start Service */
		//Start_SMS_Send_Service();
	    
	    
	    /* 檢查是否有Lock */
	    try
	    {
		    SharePref mySharePref = new SharePref(Android_SMSbs.this);
			String sLockPwd = mySharePref.ReadData(0, 0, "LockPwd");
			mySharePref=null;
			if( sLockPwd.length()>0 ) //lock狀態
			{
				imb_lock.setEnabled(false);
				imb_unlock.setEnabled(true);
				LockSMS_flg = true;
				Toast.makeText(Android_SMSbs.this,"SMS is locked now.", Toast.LENGTH_SHORT).show();
			}
			else
			{
				imb_lock.setEnabled(true);
				imb_unlock.setEnabled(false);
				LockSMS_flg = false;
			}
			
	    }
	    catch(Exception ex)
		{
			Log.e("JB_TAG","檢查是否有Lock，發生錯誤:"+ex.getMessage());
		}
	    
	    
	    super.onResume();
	}

	
	/************************************
	* Function Name: updateServiceStatus
	* Input: 
	* Output: 
	* Description:
	*************************************/
	public static void updateServiceStatus()
	{
		//
	}
	
	/************************************
	* Function Name: Initial_UI
	* Input: 
	* Output: 
	* Description: 設定元件
	*************************************/
	private void Initial_UI()
	{
		/* 抓螢幕長寬，判斷要用橫的還是直的Layout */
		DisplayMetrics dm = getResources().getDisplayMetrics(); //取得螢幕顯示的資料     
        int ScreenWidth = dm.widthPixels;  
        int ScreenHeight = dm.heightPixels; 
        
        if(ScreenWidth<ScreenHeight) //直立手機
		{
        	tv_OpenSms.setWidth(ScreenWidth);
		}
		else  //橫放 
        {
			tv_OpenSms.setWidth(ScreenWidth);
        }
        
	}
	/************************************
	* Function Name: onOptionsItemSelected
	* Input: 
	* Output: 
	* Description: 處理選單事件
	*************************************/
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//得到目前勾選的MenuItem的ID,
		int item_id = item.getItemId();

		switch (item_id)
		{
			case R.id.actions:
				
				AlertDialog.Builder ad_Page = new AlertDialog.Builder(Android_SMSbs.this);
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
				
				AlertDialog.Builder ad_about = new AlertDialog.Builder(Android_SMSbs.this);
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
				Android_SMSbs.this.finish();
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
			intent.setClass(Android_SMSbs.this, Android_SMSvc.class); 
			break;
		case 2:
			intent.setClass(Android_SMSbs.this, Android_SMSbs.class);
			break;
		case 3:
			intent.setClass(Android_SMSbs.this, Android_SMSbk.class);
			break;
		default:
			intent.setClass(Android_SMSbs.this, Android_SMSvc.class);
			break;
		}
		startActivityForResult(intent, 0);
		this.finish();
	}
	
	/************************************
	* Function Name: Save_SMS_toBOX
	* Input: 
	* Output: 
	* Description: 新增簡訊資料到資料庫
	*************************************/
	public boolean Save_SMS_toBOX(String sDATETIME, boolean bENC_FLG, 
			                      String sSENDER, byte[] bKEY, byte[] bSMS)
	{
		String sSN = Common.Get_NowTime(this);
		String sFILE_NAME = "SMS_"+sSN+".sms";
		int iENC_FLG=0;
		if(bENC_FLG==true)
		{
			iENC_FLG=1;
		}
	
	    try
	    {
		    myadp.insert_SMSBOX_Data(sSN, sDATETIME, iENC_FLG, sSENDER, sFILE_NAME, bKEY, bSMS);
		    return true;
	    }
	    catch(Exception ex)
	    {
	    	Log.e("JB_TAG","錯誤:"+ex.getMessage());
	    	//Toast.makeText(Android_SMSbs.this,"錯誤! 無法新增到資料庫!", Toast.LENGTH_SHORT).show();
	    	return false;
	    }
	}
	
	
	/************************************
	 * Function Name: Get_ByteKey
	 * Input:
	 * Output:
	 * Description: 取得解密的金鑰(Byte Array)
	 *************************************/
	public byte[] Get_ByteKey(String sSN)
	{
		// 獲得資料庫的Cursor
		Cursor cur = myadp.fetch_SMSBOX_Data(sSN);
		byte[] bKey= null;
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();

			try
			{
			   for (int i = 0; i < numRows; ++i)
			   {
				  bKey = cur.getBlob(5);
			   }
			   
			   return bKey;
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG","錯誤:"+ex.getMessage());
				//Toast.makeText(Android_SMSbs.this,"無法查詢SQLite! "+e.getMessage(), Toast.LENGTH_LONG).show();
				return null;
			}
			finally
			{
				cur.close();
			}
			
		}
		else return null;
		
	}
	/************************************
	 * Function Name: Get_ByteSMS
	 * Input:
	 * Output:
	 * Description: 取得加密的SMS(Byte Array)
	 *************************************/
	public byte[] Get_ByteSMS(String sSN)
	{
		// 獲得資料庫的Cursor
		Cursor cur = myadp.fetch_SMSBOX_Data(sSN);
		byte[] bSMS= null;
		if (cur != null && cur.getCount() > 0)
		{
			int numRows = cur.getCount();
			cur.moveToFirst();

			try
			{
			   for (int i = 0; i < numRows; ++i)
			   {
				   bSMS = cur.getBlob(6);
			   }
			   
			   return bSMS;
			}
			catch(Exception ex) 
			{
				Log.e("JB_TAG","錯誤:"+ex.getMessage());
				//Toast.makeText(Android_SMSbs.this,"無法查詢SQLite! "+e.getMessage(), Toast.LENGTH_LONG).show();
				return null;
			}
			finally
			{
				cur.close();
			}
			
		}
		else return null;
		
	}
	
	/************************************
	 * Function Name:List_SmsBox
	 * Input:
	 * Output:
	 * Description: 所有儲存的簡訊列表 
	 *************************************/
	public void List_SmsBox()
	{
		Sms_Box_List.clear();
		HashMap<String, String> map;
		
		try
		{
			// 獲得資料庫Phones的Cursor
			Cursor cur = myadp.fetch_SMSBOX_AllData();
			
			if (cur != null && cur.getCount() > 0)
			{
				int numRows = cur.getCount();
				cur.moveToFirst();
	
				try
				{
				   for (int i = 0; i < numRows; ++i)
				   {
					  String SN = cur.getString(0);
					  String DATETIME = cur.getString(1);
					  int ENC_FLG = cur.getInt(2);												
					  String SENDER = cur.getString(3);
					  String FILE_NAME = cur.getString(4);
					  byte[] KEY = cur.getBlob(5);
					  byte[] SMS = cur.getBlob(6);
					  
		
	
					  String ENC_FLG_ch="N";
					  if(ENC_FLG==1)
					  {
						  ENC_FLG_ch="Y";
					  }
					  
					  map = new HashMap<String, String>();
					  map.clear();
					  map.put("SN", SN);
					  map.put("DATETIME", DATETIME);
					  map.put("ENC_FLG", ENC_FLG_ch);
					  map.put("SENDER", SENDER);
					  map.put("FILE_NAME", FILE_NAME);
					  map.put("KEY",Encrypt.Convert_ByteToHexString(KEY));
					  map.put("SMS",Encrypt.Convert_ByteToHexString(SMS));
					  
		
					  Sms_Box_List.add(map);
						
				      cur.moveToNext();
				   }
				}
				catch(Exception ex) 
				{
					Log.e("JB_TAG","無法查詢SQLite! 原因 :"+ex.getMessage());
					Toast.makeText(Android_SMSbs.this,"無法查詢SQLite! "+ex.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			cur.close();
			
		
			SimpleAdapter SmpAdapter = 
		        new SimpleAdapter(this, Sms_Box_List, 
		        R.layout.sms_box,
	            new String[] {"SN", "DATETIME", "ENC_FLG", "SENDER", "FILE_NAME", "KEY", "SMS"}, 
	            new int[] {R.id.CELL_SN, R.id.CELL_DATETIME, R.id.CELL_ENC_FLG, R.id.CELL_SENDER, 
		        		   R.id.CELL_FILE_NAME,R.id.CELL_KEY,R.id.CELL_SMS});
			lv_SmsBox.setAdapter(SmpAdapter);
			
			lv_SmsBox.setOnItemClickListener(SmsBox_ClkListener); //指定Item點擊事件:SmsEvent_ClkListener
			//lv_SmsBox.setOnCreateContextMenuListener(SmsBox_LongClkListener); //指定Item長點擊事件:SmsBox_LongClkListener
			
			SmpAdapter=null;
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
		}
	}
	
	/************************************
	 * Function Name:SmsBox_ClkListener
	 * Input:
	 * Output:
	 * Description: 簡訊事件item click監聽
	 *************************************/
	public OnItemClickListener SmsBox_ClkListener =  new OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Sel_SmsBox_func=0; //預設功能清單選擇:[瀏覽]
			Sel_SmsBox_index=arg2; //選擇的訊息事件index
			
			AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
			ad_Action.setIcon(R.drawable.question);
			
			//建立選擇的事件
			DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which) 
			   {
				   Sel_SmsBox_func=which;
			   }
			};
			//建立按下[確定]的事件
			DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which) 
			   {
			      switch(Sel_SmsBox_func)
			      {
			      case 0: //瀏覽
			    	  List_Browse_SMS();
			    	  break;
			    	  
			      case 1: //刪除
			    	  List_Delete_SMS(1);
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
			
			String  sActionList[] = new String[2];
			if(Common.My_Language.equals("Chinese")) //中文
			{
				sActionList[0] = "開啟";
				sActionList[1] = "刪除";
				
				ad_Action.setTitle(R.string.tm_OP_option_ch);
				ad_Action.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
				ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
			}
			else
			{
				sActionList[0] = "Open";
				sActionList[1] = "Delete";
				
				ad_Action.setTitle(R.string.tm_OP_option);
				ad_Action.setPositiveButton(R.string.tm_OP_yes,OkClick);
				ad_Action.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
			}
			
			ad_Action.setSingleChoiceItems(sActionList, 0, ListClick);
			sActionList=null;
			
			ad_Action.show();
		
		}  
    };
    
    /************************************
	 * Function Name: SmsBox_LongClkListener
	 * Input:
	 * Output:
	 * Description: 簡訊事件item click監聽
	 *************************************/
	public OnCreateContextMenuListener SmsBox_LongClkListener =  new OnCreateContextMenuListener()
    {

		@Override
		public void onCreateContextMenu(ContextMenu arg0, View arg1,ContextMenuInfo arg2) 
		{
			List_Browse_SMS();
		}
	  
    };

    /************************************
	 * Function Name: List_Browse_SMS
	 * Input: 
	 * Output:
	 * Description: 顯示選擇的簡訊內容
	 *************************************/
	public void List_Browse_SMS()
	{
		HashMap<String, String> map_bs = Sms_Box_List.get(Sel_SmsBox_index);
  	  
		String sSN_bs = map_bs.get("SN"); //SN 		
	    String sFILE_NAME_bs = map_bs.get("FILE_NAME"); //FILE_NAME
	    String sENC_FLG_bs = map_bs.get("ENC_FLG"); //ENC_FLG
	    //String sKEY_bs = "";
	    byte[] bKEY_bs = null;
	    byte[] bSMS_bs = Get_ByteSMS(sSN_bs);
	  
	    if(sENC_FLG_bs=="Y")
	    {
	    	//sKEY_bs = map_bs.get("KEY"); //KEY
	    	bKEY_bs = Get_ByteKey(sSN_bs);
	    }
	
	    try
	    {
	    	//String mySMS = Common.Read_Sms_File(Android_SMSbs.this, sFILE_NAME_bs, sKEY_bs);
	    	//String mySMS = Common.Read_Sms_File(Android_SMSbs.this, sFILE_NAME_bs, bKEY_bs, bSMS_bs);
	    	
	    	String mySMS = "";
	    	
	    	if(LockSMS_flg==false)
	    	{
		    	//取得解密後的資料
		    	mySMS = Encrypt.Get_DecrptSMS(Android_SMSbs.this, bKEY_bs, bSMS_bs);
	    	}
	    	else
	    	{
	    		//加密資料
		    	mySMS = Encrypt.Convert_ByteToHexString(bSMS_bs);
	    	}
		  
	    	Set_Layout_Visible(2); //打開Layout
	    	tv_OpenSmsSN.setText(sSN_bs);
	    	tv_OpenSms.setText(mySMS);
	    }
	    catch(Exception ex)
	    {
	    	Toast.makeText(getApplicationContext(), R.string.tm_SMS_read_fail, Toast.LENGTH_SHORT).show();
	    	Log.e("JB_TAG","查詢簡訊錯誤:"+ex.getMessage());
	    }
	}
	
	/************************************
	 * Function Name: List_Delete_SMS
	 * Input: 0:刪除選擇的多筆簡訊, 1:刪除單筆簡訊
	 * Output:
	 * Description: 刪除選擇的簡訊內容
	 *************************************/
	public void List_Delete_SMS(int iCase)
	{
		switch(iCase)
		{
		case 0: //刪除選擇的多筆簡訊
			break;
			
		case 1: //刪除單筆簡訊
			HashMap<String, String> map_del = Sms_Box_List.get(Sel_SmsBox_index);
	    	  
			String sSN_del=map_del.get("SN"); //SN
			try
			{
				myadp.delete_SMSBOX_Data(sSN_del); //從資料庫刪除一筆資料
				List_SmsBox(); //重新載入簡訊事件清單
				
				//Toast.makeText(getApplicationContext(), R.string.tm_SMS_del_suc, Toast.LENGTH_SHORT).show();
				//顯示刪除成功訊息
				AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbs.this);
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
        		  	ad_Action.setMessage(R.string.tm_SMS_del_suc_ch);
    		  		ad_Action.setNegativeButton("關閉",CancelClick);
    		  	}
    		  	else
    		  	{
    		  		ad_Action.setTitle(R.string.tm_OP_done);
        		  	ad_Action.setMessage(R.string.tm_SMS_del_suc);
    		  		ad_Action.setNegativeButton("Close",CancelClick);
    		  	}
    		  	
    		  	ad_Action.show();
			}
			catch(Exception ex)
			{
				Toast.makeText(getApplicationContext(), R.string.tm_SMS_del_fail, Toast.LENGTH_SHORT).show();
				Log.e("JB_TAG","刪除簡訊錯誤:"+ex.getMessage());
			}
			break;
		default:
			break;
		}
	}
    /************************************
	 * Function Name: Set_Layout_Visible
	 * Input: int
	 * Output:
	 * Description: 顯示隱藏Layout
	 * VISIBLE:0, INVISIBLE:4, or GONE:8
	 *************************************/
	public void Set_Layout_Visible(int index)
	{
		/*
		Layout_EncSms  index : 1
	    Layout_OpenSms index : 2
	    Layout_SmsBox  index : 3
	    Layout_Lock    index : 5
	    Layout_Unlock  index : 6
	    */
		Layout_admob.setVisibility(0);
		
		switch(index)
		{
		case 0: //全部隱藏
			Layout_EncSms.setVisibility(8);
			Layout_OpenSms.setVisibility(8);
			Layout_SmsBox.setVisibility(8);
			Layout_Lock.setVisibility(8);
		    Layout_Unlock.setVisibility(8);
			break;
		case 1:
			Layout_EncSms.setVisibility(0);
			Layout_OpenSms.setVisibility(8);
			Layout_SmsBox.setVisibility(8);
			Layout_Lock.setVisibility(8);
		    Layout_Unlock.setVisibility(8);
			break;	
		case 2:
			Layout_EncSms.setVisibility(8);
			Layout_OpenSms.setVisibility(0);
			Layout_SmsBox.setVisibility(8);
			Layout_Lock.setVisibility(8);
		    Layout_Unlock.setVisibility(8);
			break;
		case 3:
			Layout_EncSms.setVisibility(8);
			Layout_OpenSms.setVisibility(8);
			Layout_SmsBox.setVisibility(0);
			Layout_Lock.setVisibility(8);
		    Layout_Unlock.setVisibility(8);
			break;
		case 5:
			Layout_EncSms.setVisibility(8);
			Layout_OpenSms.setVisibility(8);
			Layout_SmsBox.setVisibility(8);
			Layout_Lock.setVisibility(0);
		    Layout_Unlock.setVisibility(8);
			break;
		case 6:
			Layout_EncSms.setVisibility(8);
			Layout_OpenSms.setVisibility(8);
			Layout_SmsBox.setVisibility(8);
			Layout_Lock.setVisibility(8);
		    Layout_Unlock.setVisibility(0);
			break;
		default:
			Layout_EncSms.setVisibility(8);
			Layout_OpenSms.setVisibility(8);
			Layout_SmsBox.setVisibility(8);
			Layout_Lock.setVisibility(8);
		    Layout_Unlock.setVisibility(8);
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
		int iImgBtn_Pixel=0;  //圖型按鈕寬高
		
		if(ScreenWidth<ScreenHeight) //直立手機
		{
			iMain_Pixel=(int)(ScreenWidth*0.3);
			iOption1_WPixel=(int)(ScreenWidth*0.35);
			iOption1_HPixel=(int)(ScreenHeight*0.3/3);
			iOption2_WPixel=(int)(ScreenWidth*0.3);
			iOption2_HPixel=(int)(ScreenHeight*0.3/3);
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
			iImgBtn_Pixel=(int)(ScreenHeight*0.2);
		}
		
        //主要選項
		iv_Title.getLayoutParams().width = iMain_Pixel;
		iv_Title.getLayoutParams().height = iMain_Pixel;
		imb_browse.getLayoutParams().width = iOption1_WPixel;
		imb_browse.getLayoutParams().height = iOption1_HPixel;
		imb_latest.getLayoutParams().width = iOption1_WPixel;
		imb_latest.getLayoutParams().height = iOption1_HPixel;
		imb_lock.getLayoutParams().width = iOption1_WPixel;
		imb_lock.getLayoutParams().height = iOption1_HPixel;
		imb_unlock.getLayoutParams().width = iOption1_WPixel;
		imb_unlock.getLayoutParams().height = iOption1_HPixel;
		imb_menu03.getLayoutParams().width = iOption2_WPixel;
		imb_menu03.getLayoutParams().height = iOption2_HPixel;
		imb_menu04.getLayoutParams().width = iOption2_WPixel;
		imb_menu04.getLayoutParams().height = iOption2_HPixel;
		
		//圖型按鈕
		imb_save_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_save_sms.getLayoutParams().height = iImgBtn_Pixel;
		imb_reply_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_reply_sms.getLayoutParams().height = iImgBtn_Pixel;
		imb_skip_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_skip_sms.getLayoutParams().height = iImgBtn_Pixel;
		imb_del_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_del_sms.getLayoutParams().height = iImgBtn_Pixel;
		imb_back_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_back_sms.getLayoutParams().height = iImgBtn_Pixel;
		imb_lock_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_lock_sms.getLayoutParams().height = iImgBtn_Pixel;
		imb_unlock_sms.getLayoutParams().width = iImgBtn_Pixel;
		imb_unlock_sms.getLayoutParams().height = iImgBtn_Pixel;
		
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
			imb_browse.setBackgroundResource(R.drawable.imb_main02_01);
			imb_latest.setBackgroundResource(R.drawable.imb_main02_02);
			imb_lock.setBackgroundResource(R.drawable.imb_main02_05);
			imb_unlock.setBackgroundResource(R.drawable.imb_main02_06);

			imb_save_sms.setBackgroundResource(R.drawable.bt_save_sms);
			imb_reply_sms.setBackgroundResource(R.drawable.bt_reply_sms);
			imb_skip_sms.setBackgroundResource(R.drawable.bt_skip_sms);
			imb_del_sms.setBackgroundResource(R.drawable.bt_del_sms);
			imb_back_sms.setBackgroundResource(R.drawable.bt_back_sms);
			imb_lock_sms.setBackgroundResource(R.drawable.bt_lock);
			imb_unlock_sms.setBackgroundResource(R.drawable.bt_unlock);
			
			tv_LockPwd.setText(R.string.tv_LockPwd);
		    tv_UnlockPwd.setText(R.string.tv_UnlockPwd);
		}
		else if( sMyLan.equals("Chinese") )
		{
			imb_browse.setBackgroundResource(R.drawable.imb_main02_01_ch);
			imb_latest.setBackgroundResource(R.drawable.imb_main02_02_ch);
			imb_lock.setBackgroundResource(R.drawable.imb_main02_05_ch);
			imb_unlock.setBackgroundResource(R.drawable.imb_main02_06_ch);

			imb_save_sms.setBackgroundResource(R.drawable.bt_save_sms_ch);
			imb_reply_sms.setBackgroundResource(R.drawable.bt_reply_sms_ch);
			imb_skip_sms.setBackgroundResource(R.drawable.bt_skip_sms_ch);
			imb_del_sms.setBackgroundResource(R.drawable.bt_del_sms_ch);
			imb_back_sms.setBackgroundResource(R.drawable.bt_back_sms_ch);
			imb_lock_sms.setBackgroundResource(R.drawable.bt_lock_ch);
			imb_unlock_sms.setBackgroundResource(R.drawable.bt_unlock_ch);
			
			tv_LockPwd.setText(R.string.tv_LockPwd_ch);
		    tv_UnlockPwd.setText(R.string.tv_UnlockPwd_ch);
		}
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
