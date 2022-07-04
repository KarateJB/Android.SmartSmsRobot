package com.jb.Android.JBSMS_GP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.provider.Contacts;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;

public class Common 
{
	public static String My_Language = "";
	public static String My_About_string = 
		"作者：The FORCE studio"+"\n"+
        "版本：v2.1"+"\n"+
        "Blog：http://karatejb.blogspot.com/";
	
	/************************************
	 * Function Name: Show_Message
	 * Input: Context, Title, Msg
	 * Output:
	 * Description: 顯示訊息窗
	 *************************************/
	public static void Show_Message(Context context, String sTitle, String sMsg)
	{
		if(sTitle=="")
		{
			new AlertDialog.Builder(context).setMessage(sMsg).create().show();
		}
		else
		{
			new AlertDialog.Builder(context).setTitle(sTitle).setMessage(sMsg).create().show();
		}
	}
	/************************************
	* Function Name:Get_NowTime
	* Input:
	* Output:YYYYMMDDHHmmss
	* Description: 取得目前系統時間
	*************************************/
	public static String Get_NowTime(Context context)
	{
		AlarmManager alarm = (AlarmManager)context.getSystemService(Android_SMSvc.ALARM_SERVICE);
		alarm.setTimeZone("Asia/Taipei");
		//alarm.setTimeZone(TimeZone.getDefault().toString());
		
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
		//取得秒
		int mSecond=mCalendar.get(Calendar.SECOND);
		
		String NowTime=mYear+PadLeft(Integer.toString(mMon),'0',2)+
		                     PadLeft(Integer.toString(mDay),'0',2)+
		                     PadLeft(Integer.toString(mHour),'0',2)+
		                     PadLeft(Integer.toString(mMin),'0',2)+
		                     PadLeft(Integer.toString(mSecond),'0',2);
		return NowTime;
	}
	
	/************************************
	* Function Name: PadLeft
	* Input: 原始文字, 要Pad的字元, 總長度
	* Output: String
	* Description: Left Pad
	*************************************/
	public static String PadLeft(String Ori_word, char key_word, int Total_Len)
	{
		while(Ori_word.length()<Total_Len)
		{
			Ori_word=key_word+Ori_word;
		}
		return Ori_word;
	}

	/************************************
	* Function Name: Get_User_SetLanguage
	* Input: 
	* Output: "Chinese"/"English"   ("English"為預設)
	* Description: 取得使用者設定的語言
	*************************************/
	public static String Get_User_SetLanguage(Context context)
	{
		SharePref mySharePref = new SharePref(context);
		String sLan = mySharePref.ReadData(0, 0, "UserLanguage");
		mySharePref=null;
		
		if(sLan.length()==0)
		{
			My_Language = "English"; 
			return "English";
		}
		else
		{
			My_Language = sLan;
			return sLan;
		}
	}
	/************************************
	* Function Name: Set_User_SetLanguage
	* Input: Context, String
	* Output: "Chinese"/"English"   ("English"為預設)
	* Description: 設定使用者語言
	*************************************/
	public static void Set_User_SetLanguage(Context context, String sLan)
	{
		if(sLan.length()==0)
		{
			My_Language = "English"; 
			sLan =  "English";
		}
		else
		{
			My_Language = sLan;
		}
		
		SharePref mySharePref = new SharePref(context);
		mySharePref.WriteData(0, 0, "UserLanguage", sLan);
		mySharePref=null;
	}
	
	/************************************
	* Function Name: Get_ContactName
	* Input: Context, 簡訊來源電話號碼
	* Output: String
	* Description: 由電話號碼查找電話簿聯絡人姓名
	*************************************/
	public static String Get_ContactName(Context context,String sPhoneNum)
	{
		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

		if(c.moveToFirst())
		{
			int index_ID = c.getColumnIndex(ContactsContract.Contacts._ID);
			int index_Name = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int index_HasPhone = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
			//
			int index=0;
			do
			{
				String ContactID = c.getString(index_ID);
				String ContactName = c.getString(index_Name);
				String ContactHasPhone = c.getString(index_HasPhone);
				
				//if (Boolean.parseBoolean(ContactHasPhone))
				if( ContactHasPhone.equals("1") )
				{
					//Error: 不能直接取電號碼碼的index，會得到 -1
					//int index_PhoneNumCol = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					
					Cursor phones = context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ ContactID, null, null); 
					while (phones.moveToNext()) 
					{
						//取得電話簿的電話號碼
						String ContactPhoneNum = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						//整裡電話號碼（如 091-123-5555  整理成 091123555）
						ContactPhoneNum = Get_Pure_PhoneNumber(ContactPhoneNum);
						sPhoneNum = Get_Pure_PhoneNumber(sPhoneNum);
						
						//檢查此連絡人的電話號碼是否跟 收到簡訊的號碼 一樣，若相同則回傳 連絡人名稱
						if(ContactPhoneNum.equalsIgnoreCase(sPhoneNum))
						{
							return ContactName;
						}
					}     
					phones.close();
					
				}
				index++;
				
			}while(c.moveToNext());
		}
		
		return sPhoneNum;
	}
	
	/************************************
	* Function Name: Get_Pure_PhoneNumber
	* Input: 電話號碼
	* Output: String
	* Description: 整裡電話號碼
	*************************************/
	public static String Get_Pure_PhoneNumber(String DirtyPhoneNum)
	{
		String PurePhoneNum="";
		
		/* 取代'+886' */
		DirtyPhoneNum = DirtyPhoneNum.replace("+886", "0");
		
		/* 清除 '-' 字元 */
		do
		{
			int _pos = DirtyPhoneNum.indexOf("-");
			if(_pos!=-1)
			{
				PurePhoneNum+=DirtyPhoneNum.substring(0, ( _pos) );
				DirtyPhoneNum = DirtyPhoneNum.substring( ( _pos+1), DirtyPhoneNum.length());
			}
			else
			{
				PurePhoneNum+=DirtyPhoneNum;
				DirtyPhoneNum="";
			}
		}while( DirtyPhoneNum.length() >0 );
		
		return PurePhoneNum;
	}
	
	
	/************************************
	* Function Name: Save_Sms_File
	* Input: 
	* Output:true(成功),false(失敗)
	* Description: 把簡訊存成檔案
	*************************************/
	public static boolean Save_Sms_File(Context context, String sSn, String sMySms, String sFileName)
	{
		FileOutputStream fOut = null; 
		OutputStreamWriter osw = null; 
        
		try
		{ 
			//與Java不同的開啟檔案程式，但結果是一樣的
			fOut = context.openFileOutput(sFileName, Activity.MODE_PRIVATE); 
			osw = new OutputStreamWriter(fOut); 
			osw.write(sMySms); 
			osw.flush(); 
			return true;
		} 
		catch (Exception ex) 
		{ 
			Log.e("JB_TAG","檔案寫入錯誤:"+ex.getMessage());
			return false;
		} 
		finally 
		{ 
			try 
			{ 
				osw.close(); 
				fOut.close(); 
			} 
			catch(IOException ex) 
			{ 
				Log.e("JB_TAG","關閉FileStream錯誤:"+ex.getMessage());
			} 
		} 
	}
	
	/************************************
	* Function Name: Read_Sms_File
	* Input: Context, 檔案名稱, 解密金鑰
	* Output: String
	* Description: 把簡訊從資料庫讀出來
	*************************************/
	public static String Read_Sms_FromDB(Context context, String sFileName, byte[] bKey, byte[] bSMS)
	{
		FileInputStream fIn = null; 
		InputStreamReader isr = null; 
		
		char[] inputBuffer = new char[255]; 
		String data = ""; 

		try
		{ 
			fIn = context.openFileInput( sFileName ); 
			isr = new InputStreamReader(fIn); 
			isr.read(inputBuffer); 
			data = new String(inputBuffer);
			
			if(bKey==null) //不用解密
			{
				return Encrypt.Convert_ByteToHexString(bSMS);
			}
			else //需要解密
			{
				String sDecryptSms = Encrypt.Get_DecrptSMS(context, bKey, bSMS);
				return sDecryptSms;
			}
		} 
		catch (Exception ex) 
		{ 
			Log.e("JB_TAG","檔案讀取錯誤:"+ex.getMessage());
			return "無法讀取!";
		} 
		finally 
		{ 
			try 
			{ 
				isr.close(); 
				fIn.close(); 
			} 
			catch (IOException ex) 
			{ 
				Log.e("JB_TAG","關閉FileStream錯誤:"+ex.getMessage());
			} 
		}
		 
	}
	/*
	public static String Read_Sms_File(Context context, String sFileName, String sKey)
	{
		FileInputStream fIn = null; 
		InputStreamReader isr = null; 
		
		//FileInputStream kIn = null; 
		//InputStreamReader ksr = null; 

		char[] inputBuffer = new char[255]; 
		String data = ""; 

		try
		{ 
			fIn = context.openFileInput( sFileName ); 
			isr = new InputStreamReader(fIn); 
			isr.read(inputBuffer); 
			data = new String(inputBuffer);
			
			if(sKey=="") //不用解密
			{
				return data;
			}
			else //需要解密
			{
				String sDecryptSms = Encrypt.Get_DecrptSMS(context, sKey, data.toString());
				return sDecryptSms;
			}
		} 
		catch (Exception ex) 
		{ 
			Log.e("JB_TAG","檔案讀取錯誤:"+ex.getMessage());
			return "無法讀取!";
		} 
		finally 
		{ 
			try 
			{ 
				isr.close(); 
				fIn.close(); 
			} 
			catch (IOException ex) 
			{ 
				Log.e("JB_TAG","關閉FileStream錯誤:"+ex.getMessage());
			} 
		}
		 
	}
	*/
}
