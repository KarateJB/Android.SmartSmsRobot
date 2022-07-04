package com.jb.Android.JBSMS_GP;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MyDataBaseAdapter
{
	// 用於列印log
	private static final String	TAG	= "MyDataBaseAdapter";

	// 資料庫表名
	private static final String	DB_TABLE1 = "JB_SYSMSG";
	private static final String	DB_TABLE2 = "JB_SMS_EVENT";
	private static final String	DB_TABLE3 = "JB_SENDLOG_CONTACT";
	private static final String	DB_TABLE4 = "JB_SMS_BOX";
	private static final String	DB_TABLE5 = "JB_USER_SETUP";
	
	
	// JB_SYSMSG表中欄位名稱
	public static final String	JB_SYSMSG_COL01="ID";	 										
	public static final String	JB_SYSMSG_COL02="MSG";
	
	// JB_MSG_EVENT 表中欄位名稱
	public static final String	JB_MSG_EVENT_COL01="SN";
	public static final String	JB_MSG_EVENT_COL02="NAME";												
	public static final String	JB_MSG_EVENT_COL03="PHONE_NUM";
	public static final String	JB_MSG_EVENT_COL04="YEAR";
	public static final String	JB_MSG_EVENT_COL05="MON";
	public static final String	JB_MSG_EVENT_COL06="DAY";
	public static final String	JB_MSG_EVENT_COL07="HOUR";
	public static final String	JB_MSG_EVENT_COL08="MIN";
	public static final String	JB_MSG_EVENT_COL09="SMS_ID";
	public static final String	JB_MSG_EVENT_COL10="SEND_GEO";
	public static final String	JB_MSG_EVENT_COL11="SEND_ME_FLG";
	public static final String	JB_MSG_EVENT_COL12="LST_SEND_DT";
	
	// JB_SENDLOG_CONTACT表中欄位名稱
	public static final String	JB_SENDLOG_CONTACT_COL01="NAME";	 										
	public static final String	JB_SENDLOG_CONTACT_COL02="PHONE_NUM";
	public static final String	JB_SENDLOG_CONTACT_COL03="SEND_FLG"; //0:不傳送, 1:傳送
	
	// JB_SMS_BOX表中欄位名稱
	public static final String	JB_SMS_BOX_COL01="SN";
	public static final String	JB_SMS_BOX_COL02="DATETIME";
	public static final String	JB_SMS_BOX_COL03="ENC_FLG";	//0:未加密,  1:加密										
	public static final String	JB_SMS_BOX_COL04="SENDER";
	public static final String	JB_SMS_BOX_COL05="FILE_NAME";
	public static final String	JB_SMS_BOX_COL06="KEY";
	public static final String	JB_SMS_BOX_COL07="SMS";
	
	// JB_USER_SETUP表中欄位名稱
	public static final String	JB_USER_SETUP_COL01="NAME";
	public static final String	JB_USER_SETUP_COL02="TYPE"; //S:String , I: int , B: boolean
	public static final String	JB_USER_SETUP_COL03="VALUE01";
	

	// 資料庫名稱
	private static final String	DB_NAME	= "SMART_SMS_ROBOT.db";
	
	
	
	// 資料庫版本
	private static final int DB_VERSION = 1;

	// 本機Context對像
	private Context mContext = null;
	
	//建立一個表
	private static final String	DB_CREATE_01 = 
		                                              "CREATE TABLE "+DB_TABLE1+ " ("+
		                                               JB_SYSMSG_COL01 +" TEXT primary key,"+ 
		                                               JB_SYSMSG_COL02 +" TEXT )";
	private static final String	DB_CREATE_02 = 
                                                      "CREATE TABLE "+DB_TABLE2+" ("+
                                                       JB_MSG_EVENT_COL01+" TEXT,"+
		                                               JB_MSG_EVENT_COL02+" TEXT,"+ 
		                                               JB_MSG_EVENT_COL03+" TEXT,"+
		                                               JB_MSG_EVENT_COL04+" TEXT,"+
		                                               JB_MSG_EVENT_COL05+" TEXT,"+
		                                               JB_MSG_EVENT_COL06+" TEXT,"+
		                                               JB_MSG_EVENT_COL07+" TEXT,"+
		                                               JB_MSG_EVENT_COL08+" TEXT,"+
		                                               JB_MSG_EVENT_COL09+" TEXT,"+
		                                               JB_MSG_EVENT_COL10+" INTEGER," +
		                                               JB_MSG_EVENT_COL11+" INTEGER," +
		                                               JB_MSG_EVENT_COL12+" TEXT)";
	
	private static final String	DB_CREATE_03 = 
                                                      "CREATE TABLE "+DB_TABLE3+ " ("+
                                                       JB_SENDLOG_CONTACT_COL01 +" TEXT,"+ 
                                                       JB_SENDLOG_CONTACT_COL02 +" TEXT,"+
                                                       JB_SENDLOG_CONTACT_COL03 +" INTEGER)";
	
	private static final String	DB_CREATE_04 = 
											        "CREATE TABLE "+DB_TABLE4+ " ("+
											        JB_SMS_BOX_COL01 +" TEXT primary key,"+ 
											        JB_SMS_BOX_COL02 +" TEXT,"+
											        JB_SMS_BOX_COL03 +" INTEGER,"+
											        JB_SMS_BOX_COL04 +" TEXT,"+
											        JB_SMS_BOX_COL05 +" TEXT,"+
											        JB_SMS_BOX_COL06 +" BLOB,"+
											        JB_SMS_BOX_COL07 +" BLOB)";
	
	private static final String	DB_CREATE_05 = 
											        "CREATE TABLE "+DB_TABLE5+ " ("+
											        JB_USER_SETUP_COL01 +" TEXT primary key,"+ 
											        JB_USER_SETUP_COL02 +" TEXT,"+
											        JB_USER_SETUP_COL03 +" TEXT)";
	
	
	// 執行open（）開啟資料庫時，儲存傳回的資料庫對像
	private SQLiteDatabase mSQLiteDatabase = null;

	// 由SQLiteOpenHelper繼承過來
	private DatabaseHelper mDatabaseHelper = null;
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/* 建構函數-建立一個資料庫 */
		DatabaseHelper(Context context)
		{
			//當呼叫getWritableDatabase() 
			//或 getReadableDatabase()方法時
			//則建立一個資料庫
			
			super(context, DB_NAME, null, DB_VERSION);	
			Log.v("JB_TAG","資料庫載入...");
		}

		/* 建立一個表 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Log.v("JB_TAG","Create Tables");
			// 資料庫沒有表時建立一個
			db.execSQL(DB_CREATE_01);
			db.execSQL(DB_CREATE_02);
			db.execSQL(DB_CREATE_03);
			db.execSQL(DB_CREATE_04);
			db.execSQL(DB_CREATE_05);
		}

		/* 升級資料庫 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
	
	/* 建構函數-取得Context */
	public MyDataBaseAdapter(Context context)
	{
		mContext = context;
	}


	/************************************
	 * Function: open
	 * Input: 
	 * Output:
	 * Description: 開啟資料庫，傳回資料庫對像
	 *************************************/
	public void open() throws SQLException
	{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}


	/************************************
	 * Function: close
	 * Input: 
	 * Output:
	 * Description: 關閉資料庫
	 *************************************/
	public void close()
	{
		mDatabaseHelper.close();
	}

	/************************************
	 * Function: insert_SYSMSG_Data
	 * Input: 
	 * Output:
	 * Description: 新增一筆系統簡訊資料
	 *************************************/
	public long insert_SYSMSG_Data(String sID, String sMSG)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(JB_SYSMSG_COL01, sID);
		initialValues.put(JB_SYSMSG_COL02, sMSG);

		return mSQLiteDatabase.insert(DB_TABLE1, null, initialValues);
	}
	
	/************************************
	 * Function: insert_MSGEVENT_Data
	 * Input: 
	 * Output:
	 * Description: 新增一條發送簡訊事件資料
	 *************************************/
	public long insert_MSGEVENT_Data(String sSN,String sName, String sPHONE_NUM,String sYear,
			                   String sMon,String sDay,String sHour,String sMin,
			                   String sMSG_ID,int iSEND_GEO, int iSEND_ME)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(JB_MSG_EVENT_COL01, sSN);
		initialValues.put(JB_MSG_EVENT_COL02, sName);
		initialValues.put(JB_MSG_EVENT_COL03, sPHONE_NUM);
		initialValues.put(JB_MSG_EVENT_COL04, sYear);
		initialValues.put(JB_MSG_EVENT_COL05, sMon);
		initialValues.put(JB_MSG_EVENT_COL06, sDay);
		initialValues.put(JB_MSG_EVENT_COL07, sHour);
		initialValues.put(JB_MSG_EVENT_COL08, sMin);
		initialValues.put(JB_MSG_EVENT_COL09, sMSG_ID);
		initialValues.put(JB_MSG_EVENT_COL10, iSEND_GEO);
		initialValues.put(JB_MSG_EVENT_COL11, iSEND_ME);
		initialValues.put(JB_MSG_EVENT_COL12, "");

		return mSQLiteDatabase.insert(DB_TABLE2, null, initialValues);
	}
	
	/************************************
	 * Function: insert_LOGVCONTACT_Data
	 * Input: 
	 * Output:
	 * Description: 新增一筆寄送簡訊紀錄收件者資料
	 *************************************/
	public long insert_LOGCONTACT_Data(String sNAME, String sPHONE, int iSEND_FLG)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(JB_SENDLOG_CONTACT_COL01, sNAME);
		initialValues.put(JB_SENDLOG_CONTACT_COL02, sPHONE);
		initialValues.put(JB_SENDLOG_CONTACT_COL03, iSEND_FLG);

		return mSQLiteDatabase.insert(DB_TABLE3, null, initialValues);
	}
	/************************************
	 * Function: insert_SMSBOX_Data
	 * Input: 
	 * Output:
	 * Description: 新增一筆簡訊紀錄
	 *************************************/
	public long insert_SMSBOX_Data(String sSN, String sDATETIME, int iENC_FLG, 
			                       String sSENDER, String sFILE_NAME, byte[] bKEY, byte[] bSMS)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(JB_SMS_BOX_COL01, sSN);
		initialValues.put(JB_SMS_BOX_COL02, sDATETIME);
		initialValues.put(JB_SMS_BOX_COL03, iENC_FLG);
		initialValues.put(JB_SMS_BOX_COL04, sSENDER);
		initialValues.put(JB_SMS_BOX_COL05, sFILE_NAME);
		initialValues.put(JB_SMS_BOX_COL06, bKEY);
		initialValues.put(JB_SMS_BOX_COL07, bSMS);
		

		return mSQLiteDatabase.insert(DB_TABLE4, null, initialValues);
	}

	/************************************
	 * Function: insert_USERSETUP_Data
	 * Input: 
	 * Output:
	 * Description: 新增一筆使用者選項資料
	 *************************************/
	public long insert_USERSETUP_Data(String sNAME, String sType, String sValue)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(JB_USER_SETUP_COL01, sNAME);
		initialValues.put(JB_USER_SETUP_COL02, sType);
		initialValues.put(JB_USER_SETUP_COL03, sValue);

		return mSQLiteDatabase.insert(DB_TABLE5, null, initialValues);
	}
	
	/************************************
	 * Function: update_MSGEVENT_Data
	 * Input: 
	 * Output:
	 * Description: 更新一條發送簡訊事件資料
	 *************************************/
	public boolean update_MSGEVENT_Data(String sSN,String sName, String sPHONE_NUM,String sYear,
                                        String sMon,String sDay,String sHour,String sMin,
                                        String sMSG_ID,int iSEND_GEO, int iSEND_ME)
	{
		ContentValues newValues = new ContentValues();
		newValues.put(JB_MSG_EVENT_COL01, sSN);
		newValues.put(JB_MSG_EVENT_COL02, sName);
		newValues.put(JB_MSG_EVENT_COL03, sPHONE_NUM);
		newValues.put(JB_MSG_EVENT_COL04, sYear);
		newValues.put(JB_MSG_EVENT_COL05, sMon);
		newValues.put(JB_MSG_EVENT_COL06, sDay);
		newValues.put(JB_MSG_EVENT_COL07, sHour);
		newValues.put(JB_MSG_EVENT_COL08, sMin);
		newValues.put(JB_MSG_EVENT_COL09, sMSG_ID);
		newValues.put(JB_MSG_EVENT_COL10, iSEND_GEO);
		newValues.put(JB_MSG_EVENT_COL11, iSEND_ME);
		return mSQLiteDatabase.update(DB_TABLE2, newValues,
				                      JB_MSG_EVENT_COL01 + "='" + sSN +"' AND " +
				                      JB_MSG_EVENT_COL02 + "='" + sName +"' AND " +
				                      JB_MSG_EVENT_COL03 + "='" + sPHONE_NUM +"' "
				                      , null) > 0;
	}
	
	/*****************************************************
	 * Function: update_MSGEVENT_DateTime
	 * Input: 
	 * Output:
	 * Description: 更新一條發送簡訊事件資料的最近發送日期
	 *****************************************************/
	public boolean update_MSGEVENT_DateTime(String sSN,String sName, String sPHONE_NUM,String sLST_SEND_DT)
	{
		ContentValues newValues = new ContentValues();
		newValues.put(JB_MSG_EVENT_COL01, sSN);
		newValues.put(JB_MSG_EVENT_COL02, sName);
		newValues.put(JB_MSG_EVENT_COL03, sPHONE_NUM);
		newValues.put(JB_MSG_EVENT_COL12, sLST_SEND_DT);
		return mSQLiteDatabase.update(DB_TABLE2, newValues,
				                      JB_MSG_EVENT_COL01 + "='" + sSN +"' AND " +
				                      JB_MSG_EVENT_COL02 + "='" + sName +"' AND " +
				                      JB_MSG_EVENT_COL03 + "='" + sPHONE_NUM +"' "
				                      , null) > 0;
	}
	
	/************************************
	 * Function: update_LOGCONTACT_Data
	 * Input: 
	 * Output:
	 * Description: 更新簡訊紀錄接受者的傳送狀態
	 *************************************/
	public boolean update_LOGCONTACT_Data(int iSEND_FLG)
	{
		try
		{
			ContentValues newValues = new ContentValues();
			newValues.put(JB_SENDLOG_CONTACT_COL03, iSEND_FLG);
			
			return mSQLiteDatabase.update(DB_TABLE3, newValues,
					                      null, null) > 0;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	
	/************************************
	 * Function: update_USERSETUP_Data
	 * Input: 
	 * Output:
	 * Description: 更新JB_USER_SETUP
	 *************************************/
	public boolean update_USERSETUP_Data(String sName, String sNew_Type, String sNew_Value)
	{
		ContentValues newValues = new ContentValues();
		newValues.put(JB_USER_SETUP_COL01, sName);
		newValues.put(JB_USER_SETUP_COL02, sNew_Type);
		newValues.put(JB_USER_SETUP_COL03, sNew_Value);
		
		return mSQLiteDatabase.update(DB_TABLE5, newValues,
				                      JB_USER_SETUP_COL01 + "='" + sName +"'"
				                      , null) > 0;
	}
	
	/************************************
	 * Function: delete_SYSMSG_Data
	 * Input: sSMS_DESC
	 * Output:
	 * Description: 刪除一條系統簡訊資料
	 *************************************/
	public boolean delete_SYSMSG_Data(String sSMS_DESC)
	{
		return mSQLiteDatabase.delete(DB_TABLE1, JB_SYSMSG_COL02+"='"+sSMS_DESC+"'", null) > 0;
	}
	
	/************************************
	 * Function: delete_MSGEVENT_Data
	 * Input: sSN,sName,sPHONE_NUM
	 * Output:
	 * Description: 刪除一條簡訊事件資料
	 *************************************/
	public boolean delete_MSGEVENT_Data(String sSN,String sName, String sPHONE_NUM)
	{
		return mSQLiteDatabase.delete(DB_TABLE2, 
				                      JB_MSG_EVENT_COL01 + "='" + sSN +"' AND " +
                                      JB_MSG_EVENT_COL02 + "='" + sName +"' AND " +
                                      JB_MSG_EVENT_COL03 + "='" + sPHONE_NUM +"' "
				                      , null) > 0;
	}
	
	/************************************
	 * Function: delete_MSGEVENT_bydelSMS
	 * Input: 系統訊息代號
	 * Output:
	 * Description: 刪除系統簡訊被刪除的訊息事件
	 *************************************/
	public int delete_MSGEVENT_bydelSMS(String sSMS_ID)
	{
		return mSQLiteDatabase.delete(DB_TABLE2, 
				                      JB_MSG_EVENT_COL09 + "='" + sSMS_ID +"'"
				                      , null);
	}
	
	/************************************
	 * Function: delete_SMSBOX_Data
	 * Input: 簡訊序號
	 * Output:
	 * Description: 刪除已儲存的簡訊資料
	 *************************************/
	public int delete_SMSBOX_Data(String sSN)
	{
		return mSQLiteDatabase.delete(DB_TABLE4, 
				                      JB_SMS_BOX_COL01 + "='" + sSN +"'"
				                      , null);
	}
	
	
	
	/************************************
	 * Function: tuncate_SYSMSG_Data
	 * Input:
	 * Output:
	 * Description: 清空系統簡訊資料
	 *************************************/
	public boolean tuncate_SYSMSG_Data()
	{
		return mSQLiteDatabase.delete(DB_TABLE1,"", null) > 0;
	}
	/************************************
	 * Function: tuncate_MSGEVENT_Data
	 * Input:
	 * Output:
	 * Description: 清空發送簡訊事件資料
	 *************************************/
	public boolean tuncate_MSGEVENT_Data()
	{
		return mSQLiteDatabase.delete(DB_TABLE2,"", null) > 0;
	}
	/************************************
	 * Function: truncate_LOGCONTACT_Data
	 * Input:
	 * Output:
	 * Description: 清空寄送簡訊紀錄收件者資料
	 *************************************/
	public boolean truncate_LOGCONTACT_Data()
	{
		return mSQLiteDatabase.delete(DB_TABLE3, null, null) > 0;
	}
	/************************************
	 * Function: tuncate_USERSETUP_Data
	 * Input:
	 * Output:
	 * Description: 清空使用者設定表格
	 *************************************/
	public boolean tuncate_USERSETUP_Data()
	{
		return mSQLiteDatabase.delete(DB_TABLE5,"", null) > 0;
	}
	
	/************************************
	 * Function: fetch_SYSMSG_AllData
	 * Input:
	 * Output:
	 * Description: 查詢系統簡訊資料
	 *************************************/
	public Cursor fetch_SYSMSG_AllData()
	{
		return mSQLiteDatabase.query(DB_TABLE1, new String[] { JB_SYSMSG_COL01,JB_SYSMSG_COL02 }, null, null, null, null, JB_SYSMSG_COL01);
	}
	
	/************************************
	 * Function: fetch_MSGEVENT_AllData
	 * Input:
	 * Output:
	 * Description: 查詢所有簡訊事件
	 *************************************/
	public Cursor fetch_MSGEVENT_AllData()
	{
		try
		{
			String[] sCol=new String[]{JB_MSG_EVENT_COL01,JB_MSG_EVENT_COL02,JB_MSG_EVENT_COL03,
					                JB_MSG_EVENT_COL04,JB_MSG_EVENT_COL05,JB_MSG_EVENT_COL06,
					                JB_MSG_EVENT_COL07,JB_MSG_EVENT_COL08,JB_MSG_EVENT_COL09,
					                JB_MSG_EVENT_COL10,JB_MSG_EVENT_COL11,JB_MSG_EVENT_COL12};
			return mSQLiteDatabase.query(DB_TABLE2, sCol, null, null, null, null, JB_MSG_EVENT_COL01);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG", "錯誤："+ex.getMessage());
			return null;
		}
	}

	/************************************
	 * Function: fetch_MSGEVENT_AllData
	 * Input: 今天的年月日
	 * Output: 簡訊事件筆數
	 * Description: 查詢今天有幾則簡訊事件
	 *************************************/
	public int fetch_MSGEVENT_TodayData(String sYear,String sMon,String sDay)
	{
		try
		{
			String[] sCol=new String[]{JB_MSG_EVENT_COL01,JB_MSG_EVENT_COL02,JB_MSG_EVENT_COL03,
					                JB_MSG_EVENT_COL04,JB_MSG_EVENT_COL05,JB_MSG_EVENT_COL06,
					                JB_MSG_EVENT_COL07,JB_MSG_EVENT_COL08,JB_MSG_EVENT_COL09,
					                JB_MSG_EVENT_COL10,JB_MSG_EVENT_COL11,JB_MSG_EVENT_COL12};
			
			String sCondition = "("+JB_MSG_EVENT_COL04 + "='" + sYear+"' OR " +JB_MSG_EVENT_COL04+"='0000') AND "+
            "("+JB_MSG_EVENT_COL05 + "='" + sMon+"' OR " +JB_MSG_EVENT_COL05+"='00') AND "+
            "("+JB_MSG_EVENT_COL06 + "='" + sDay+"' OR " +JB_MSG_EVENT_COL06+"='00') ";
			
			Cursor mCursor = mSQLiteDatabase.query(
					DB_TABLE2, sCol, sCondition, null, null, null, JB_MSG_EVENT_COL01);
			
			if (mCursor == null)
			{
				return 0;
			}
			else
			{
				return mCursor.getCount();
			}
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG", "錯誤："+ex.getMessage());
			return 0;
		}
	}
	/************************************
	 * Function: fetch_SYSMSG_ID
	 * Input:系統簡訊編號
	 * Output:
	 * Description: 查詢系統簡訊敘述
	 *************************************/
	public Cursor fetch_SYSMSG_Data(String SMS_ID) throws SQLException
	{
		
		Cursor mCursor =
		mSQLiteDatabase.query(
				 true, DB_TABLE1, 
				 new String[] { JB_SYSMSG_COL02}, 
				 JB_SYSMSG_COL01+"='"+ SMS_ID +"'", null, null, null, null, null);

		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
		
	}

	/************************************
	 * Function: fetch_SYSMSG_ID
	 * Input:系統簡訊敘述
	 * Output:
	 * Description: 查詢系統簡訊序號
	 *************************************/
	public Cursor fetch_SYSMSG_ID(String SMS_DESC) throws SQLException
	{
		
		Cursor mCursor =
		mSQLiteDatabase.query(
				 true, DB_TABLE1, 
				 new String[] { JB_SYSMSG_COL01}, 
				 JB_SYSMSG_COL02+"='"+ SMS_DESC +"'", null, null, null, null, null);

		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
		
	}
	
	/************************************
	 * Function Name:fetch_MSGEVENT_Data
	 * Input:
	 * Output:
	 * Description: 查詢簡訊事件指定資料
	 * Condition:當時的日期時間
	 *************************************/
	public Cursor fetch_MSGEVENT_Data(String sYear,String sMon,String sDay,String sHour,String sMin) throws SQLException
	{
		String sCondition = "("+JB_MSG_EVENT_COL04 + "='" + sYear+"' OR " +JB_MSG_EVENT_COL04+"='0000') AND "+
		                    "("+JB_MSG_EVENT_COL05 + "='" + sMon+"' OR " +JB_MSG_EVENT_COL05+"='00') AND "+
		                    "("+JB_MSG_EVENT_COL06 + "='" + sDay+"' OR " +JB_MSG_EVENT_COL06+"='00') AND "+
		                    "("+JB_MSG_EVENT_COL07 + "='" + sHour+"') AND "+
		                    "("+JB_MSG_EVENT_COL08 + "='" + sMin+"') ";
		//Log.v("JB_TAG",sCondition);
		
		Cursor mCursor =
		mSQLiteDatabase.query(
				 true, DB_TABLE2, 
				 new String[] { JB_MSG_EVENT_COL01, //SN
						        JB_MSG_EVENT_COL02, //NAME
						        JB_MSG_EVENT_COL03, //PHONE_NUM
						        JB_MSG_EVENT_COL09, //SMS_ID
						        JB_MSG_EVENT_COL10, //SEND_GEO
						        JB_MSG_EVENT_COL11, //SEND_ME_FLG
						        JB_MSG_EVENT_COL12},//LST_SEND_DT
				 sCondition, null, null, null, null, null);

		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
		
	}
	
	/************************************
	 * Function fetch_LOGCONTACT_AllData
	 * Input:
	 * Output:
	 * Description: 查詢簡訊紀錄接收者
	 *************************************/
	public Cursor fetch_LOGCONTACT_AllData()
	{
		String[] sCol=new String[]{JB_SENDLOG_CONTACT_COL01,JB_SENDLOG_CONTACT_COL02,JB_SENDLOG_CONTACT_COL03};
		return mSQLiteDatabase.query(DB_TABLE3, sCol, null, null, null, null, JB_SENDLOG_CONTACT_COL01);
	}
	
	/************************************
	 * Function: fetch_SMSBOX_AllData
	 * Input:
	 * Output:
	 * Description: 查詢已儲存的簡訊資料
	 *************************************/
	public Cursor fetch_SMSBOX_AllData()
	{
		return mSQLiteDatabase.query(DB_TABLE4, 
				new String[] { JB_SMS_BOX_COL01,JB_SMS_BOX_COL02, JB_SMS_BOX_COL03, JB_SMS_BOX_COL04, JB_SMS_BOX_COL05, JB_SMS_BOX_COL06, JB_SMS_BOX_COL07}, 
				null, null, null, null, JB_SMS_BOX_COL01);
	}
	
	/************************************
	 * Function: fetch_SMSBOX_Key
	 * Input:
	 * Output:
	 * Description: 查詢已儲存的簡訊資料
	 *************************************/
	public Cursor fetch_SMSBOX_Data(String sSN)
	{
		String sCondition = "("+JB_SMS_BOX_COL01 + "='" + sSN+"')";

		Cursor mCursor =
		mSQLiteDatabase.query(
		true, DB_TABLE4, 
		new String[] { JB_SMS_BOX_COL01,JB_SMS_BOX_COL02, JB_SMS_BOX_COL03, JB_SMS_BOX_COL04, JB_SMS_BOX_COL05, JB_SMS_BOX_COL06, JB_SMS_BOX_COL07}, 
		sCondition, null, null, null, null, null);
		
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/************************************
	 * Function fetch_USERSETUP_Data
	 * Input:
	 * Output:
	 * Description: 查詢某筆使用者選項
	 *************************************/
	public Cursor fetch_USERSETUP_Data(String sName)
	{
		String sCondition = "("+JB_USER_SETUP_COL01 + "='" + sName+"')";

		try
		{
			Cursor mCursor = null;
			mCursor =
			mSQLiteDatabase.query(
			true, DB_TABLE5, 
			new String[] { JB_USER_SETUP_COL01,JB_USER_SETUP_COL02, JB_USER_SETUP_COL03}, 
			               sCondition, null, null, null, null, null);
			
			if (mCursor != null && mCursor.getCount()>0)
			{
				mCursor.moveToFirst();
				return mCursor;
			}
			else
			{
				return null;
			}
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return null;
		}
	}
}

