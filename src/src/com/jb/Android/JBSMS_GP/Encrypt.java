package com.jb.Android.JBSMS_GP;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Log;

public class Encrypt 
{
	/************************************
	* Function Name: Get_EncrptSMS
	* Input:
	* Output: String 
	* Description: 取得加密後的文字內容(String)
	*************************************/
	public static String Get_EncrptSMS(Context context, byte[] key, String sMySms)
	{
		try
		{
			SecretKeySpec spec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			
			//設定為加密模式
			cipher.init(Cipher.ENCRYPT_MODE, spec);
		    //將字串加密，並取得加密後的資料
			byte[] encryptData = cipher.doFinal(sMySms.getBytes());
			
			//加密後字串
			return Convert_ByteToHexString(encryptData);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return "加密失敗...";
		}
	}
	
	/************************************
	* Function Name: Get_EncrptSMS_Byte
	* Input:
	* Output: byte array
	* Description: 取得加密後的文字內容(byte array)
	*************************************/
	public static byte[] Get_EncrptSMS_Byte(Context context, byte[] key, String sMySms)
	{
		try
		{
			SecretKeySpec spec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			
			//設定為加密模式
			cipher.init(Cipher.ENCRYPT_MODE, spec);
		    //將字串加密，並取得加密後的資料
			byte[] encryptData = cipher.doFinal(sMySms.getBytes());
			
			//加密後字串
			return encryptData;
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return null;
		}
	}
	
	/************************************
	* Function Name: Get_DecrptSMS
	* Input:
	* Output: String 
	* Description: 取得解密後的文字內容
	*************************************/
	public static String Get_DecrptSMS(Context context, String key, String sEncrptSms)
	{
		try
		{
			//把HexString轉成Byte[]
			byte[] bKey = Convert_HexStringToByte(key); 
			
			byte[] bEncrptSms = Convert_HexStringToByte(sEncrptSms); 
			
			
			SecretKeySpec spec = new SecretKeySpec(bKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			//設定為解密模式
			cipher.init(Cipher.DECRYPT_MODE, spec);
			byte[] decryptData = cipher.doFinal( bEncrptSms );
			
			return new String(decryptData);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return "解密失敗...";
		}
	}
	
	/************************************
	* Function Name: Get_DecrptSMS
	* Input:
	* Output: String 
	* Description: 取得解密後的文字內容
	*************************************/
	public static String Get_DecrptSMS(Context context, byte[] bKey, byte[] bSMS)
	{
		try
		{
			SecretKeySpec spec = new SecretKeySpec(bKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			//設定為解密模式
			cipher.init(Cipher.DECRYPT_MODE, spec);
			byte[] decryptData = cipher.doFinal( bSMS );
			
			return new String(decryptData);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return "解密失敗...";
		}
	}
	/*
	public static String Get_DecrptSMS(Context context, byte[] bKey, String sEncrptSms)
	{
		try
		{
			//把HexString轉成Byte[]
			byte[] bEncrptSms = Convert_HexStringToByte(sEncrptSms); 
			
			SecretKeySpec spec = new SecretKeySpec(bKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			//設定為解密模式
			cipher.init(Cipher.DECRYPT_MODE, spec);
			byte[] decryptData = cipher.doFinal( bEncrptSms );
			
			return new String(decryptData);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return "解密失敗...";
		}
	}
	*/
	/************************************
	* Function Name: Convert_ByteToHexString
	* Input:
	* Output: String 
	* Description: 取得解密後的文字內容
	*************************************/
	public static String Convert_ByteToHexString(byte[] in)
	{
		try
		{
			BigInteger temp = new BigInteger(in);
			return temp.toString(16);
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return null;
		}
	}
	
	/************************************
	* Function Name: Convert_HexStringToByte
	* Input:
	* Output: String 
	* Description: 取得解密後的文字內容
	*************************************/
	public static byte[] Convert_HexStringToByte(String in)
	{
		try
		{
			BigInteger temp = new BigInteger(in, 16);
			return temp.toByteArray();
		}
		catch(Exception ex)
		{
			Log.e("JB_TAG","錯誤:"+ex.getMessage());
			return null;
		}
	}
}
