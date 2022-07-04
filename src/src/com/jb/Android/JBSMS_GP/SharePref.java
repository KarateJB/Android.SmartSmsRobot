package com.jb.Android.JBSMS_GP;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharePref
{
	private Context context;
	private String spFileName = "SmartSmsRobot";
	public SharePref(Context context_in)
	{
		context=context_in;
	}
	/************************************
	 * Function: ReadData
	 * Input: 
	 *        iMODE( 0:MODE_PRIVATE, 1:MODE_WORLD_READABLE , 2:MODE_WORLD_WRITEABLE)
	 *        iGetType(0:String, 1:Int, 2:Boolean, 3:Long, 4:Float)
	 *        sParam(參數名稱)  
	 * Output: 參數值
	 * Description: 讀取SharedPreferences
	 *************************************/
	public String ReadData(int iMODE,int iGetType,String sParam)
	{
		String Return_Rslt;
		try
		{
			//取得Preferences對像
			SharedPreferences SharePref = context.getSharedPreferences(spFileName, iMODE);
			//取值
			switch(iGetType)
			{
			case 0: //String
				Return_Rslt = SharePref.getString(sParam, "");
				break;
			case 1: //Int
			    Return_Rslt= Integer.toString( SharePref.getInt(sParam, 0) );
			    break;
			case 2: //Boolean
			    Return_Rslt = Boolean.toString( SharePref.getBoolean(sParam, false) );
			    break;
			case 3: //Long
			    Return_Rslt = Long.toString( SharePref.getLong(sParam, 0) );
			    break;
			case 4: //Float
			    Return_Rslt = Float.toString( SharePref.getFloat(sParam, 0) );
			    break;
			default:
			    Return_Rslt = SharePref.getString(sParam, "");
			    break;
			}
			SharePref=null;
			return Return_Rslt;
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","TempDataCS:ReadData錯誤，原因:"+ex.getMessage());
			return "";
		}
		
	}
	/************************************
	 * Function: WriteData
	 * Input: 
	 *        iMODE( 0:MODE_PRIVATE, 1:MODE_WORLD_READABLE , 2:MODE_WORLD_WRITEABLE)
	 *        iPutType(0:String, 1:Int, 2:Boolean, 3:Long, 4:Float)
	 *        sParam(參數名稱)  
	 *        sValue(值)
	 * Output: true(成功),false(失敗)
	 * Description: 寫入SharedPreferences
	 *************************************/
	public boolean WriteData(int iMODE,int iPutType, String sParam, String sValue)
	{
		try
		{
			//取得活動的preferences對像
			SharedPreferences SharePref = context.getSharedPreferences(spFileName,iMODE);
			//取得編輯對像
			SharedPreferences.Editor editor = SharePref.edit();
			//放入參數及其值
			switch(iPutType)
			{
			case 0: //String
				editor.putString(sParam, sValue);
				break;
			case 1: //Int
				editor.putInt(sParam, Integer.parseInt(sValue));
				break;
			case 2: //Boolean
				if( sValue.compareToIgnoreCase("true")==0 )
				{
					editor.putBoolean(sParam, true);
				}
				else if(sValue.compareToIgnoreCase("false")==0)
				{
					editor.putBoolean(sParam, false);
				}
				else
				{
					Log.e("JB_TAG","無法儲存SharedPreferences!參數名稱:"+sParam);
				}
				break;
			case 3: //Long
				editor.putLong(sParam, Long.parseLong(sValue));
				break;
			case 4: //Float
				editor.putFloat(sParam, Float.parseFloat(sValue));
				break;
			default:
				Log.e("JB_TAG","無此SharedPreferences型態!參數名稱:"+sParam);
				break;
			}
			editor.commit();
			
			editor=null;
			SharePref=null;
			return true;
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","TempDataCS:WriteData錯誤，原因:"+ex.getMessage());
			return false;
		}
	}
}
