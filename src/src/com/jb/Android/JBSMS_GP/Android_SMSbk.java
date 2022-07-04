package com.jb.Android.JBSMS_GP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Android_SMSbk  extends Activity
{
	private ImageView iv_Title               = null;
	private ImageButton imb_backup           = null;
    private ImageButton imb_recovery         = null;
    private ImageButton imb_menu02           = null;
	private ImageButton imb_menu03           = null;
	private ImageButton imb_menu05           = null;
	private ImageButton imb_menu06           = null;
    
    /* SQLite */
	MyDataBaseAdapter myadp = null;
	
	
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
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms3);
		
		iv_Title = (ImageView)this.findViewById(R.id.iv_Title);
		imb_backup = (ImageButton)this.findViewById(R.id.imb_backup);
		imb_recovery = (ImageButton)this.findViewById(R.id.imb_recovery);
		imb_menu02 = (ImageButton)this.findViewById(R.id.imb_menu02);
		imb_menu03 = (ImageButton)this.findViewById(R.id.imb_menu03);
		imb_menu05 = (ImageButton)this.findViewById(R.id.imb_menu05);
		imb_menu06 = (ImageButton)this.findViewById(R.id.imb_menu06);
		
		
		/* 先開啟一次資料庫 */
		myadp=new MyDataBaseAdapter(this);
		myadp.open();
		myadp.close();
		
		/* 備份  */
		imb_backup.setOnClickListener(new ImageButton.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//確認是否備份
				AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
				ad_Action.setIcon(R.drawable.question);
				
				
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					 try
					 {
					   imb_recovery.setEnabled(false);
					   
						
					   //檢查是否有SD Card
					   if (Android_SMSbk.this.isExternalStorageAvail())
					   {                
						  new ExportDatabaseFileTask().execute();
					   }  
					   else 
					   {
					      //顯示警告訊息
						  AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
					      ad_Action.setIcon(R.drawable.alert);
							
						  DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
						  {
							public void onClick(DialogInterface dialog, int which) 
							{
						      dialog.cancel();
							}
						  };

						  if(Common.My_Language.equals("Chinese"))
						  {
							  ad_Action.setTitle(R.string.tm_OP_alert_ch);
						      ad_Action.setMessage(R.string.tm_BK_backup_fail01_ch);
							  ad_Action.setNegativeButton("關閉",CancelClick);
						  }
						  else
						  {
							  ad_Action.setTitle(R.string.tm_OP_alert);
						      ad_Action.setMessage(R.string.tm_BK_backup_fail01);
							  ad_Action.setNegativeButton("Close",CancelClick);
						  }
						  
						  ad_Action.show();
							
					   } 
					   imb_recovery.setEnabled(true);
					 }
					 catch(Exception ex)
					 {
						Log.e("JB_TAG", "備份錯誤："+ex.getMessage());
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
					ad_Action.setTitle(R.string.tm_BK_backup_confirm_title_ch);
					ad_Action.setMessage(R.string.tm_BK_backup_confirm_str_ch);
					ad_Action.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
					ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
				}
				else
				{
					ad_Action.setTitle(R.string.tm_BK_backup_confirm_title);
					ad_Action.setMessage(R.string.tm_BK_backup_confirm_str);
					ad_Action.setPositiveButton(R.string.tm_OP_yes,OkClick);
					ad_Action.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
				}
				
				ad_Action.show();
					
		}});
		
		/* 還原  */
		imb_recovery.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				//確認是否備份
				AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
				ad_Action.setIcon(R.drawable.question);
				
				//建立按下[確定]的事件
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					 try
					 {
						imb_backup.setEnabled(false);
						
						//檢查是否有SD Card
						if (Android_SMSbk.this.isExternalStorageAvail())
						{                
							new ImportDatabaseFileTask().execute();
						} 
						else 
						{
							//顯示警告訊息
							AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
							ad_Action.setIcon(R.drawable.alert);
							
							DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
							{
							   public void onClick(DialogInterface dialog, int which) 
							   {
								   dialog.cancel();
							   }
							};
							
							if(Common.My_Language.equals("Chinese"))
							{
								ad_Action.setTitle(R.string.tm_OP_alert_ch);
								ad_Action.setMessage(R.string.tm_BK_recovery_fail01_ch);
								ad_Action.setNegativeButton("關閉",CancelClick);
							}
							else
							{
								ad_Action.setTitle(R.string.tm_OP_alert);
								ad_Action.setMessage(R.string.tm_BK_recovery_fail01);
								ad_Action.setNegativeButton("Close",CancelClick);
							}
							
							ad_Action.show();
							
						} 
						imb_backup.setEnabled(true);
					 }
					 catch(Exception ex)
					 {
						Log.e("JB_TAG", "還原錯誤："+ex.getMessage());
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
					ad_Action.setTitle(R.string.tm_BK_recovery_confirm_title_ch);
					ad_Action.setMessage(R.string.tm_BK_recovery_confirm_str_ch);
					ad_Action.setPositiveButton(R.string.tm_OP_yes_ch,OkClick);
					ad_Action.setNegativeButton(R.string.tm_OP_cancel_ch,CancelClick);
				}
				else
				{
					ad_Action.setTitle(R.string.tm_BK_recovery_confirm_title);
					ad_Action.setMessage(R.string.tm_BK_recovery_confirm_str);
					ad_Action.setPositiveButton(R.string.tm_OP_yes,OkClick);
					ad_Action.setNegativeButton(R.string.tm_OP_cancel,CancelClick);
				}
				
				ad_Action.show();
				

			}
		});
	}
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	@Override
	protected void onResume()
	{
		//調整UI語言
		Adjust_Language_Layout();
		//設定UI寬高
		Adjust_UI_Layout();
		
		super.onResume();
	}
	
	/*處理功能選單事件*/
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//得到目前勾選的MenuItem的ID,
		int item_id = item.getItemId();

		switch (item_id)
		{
		    case R.id.actions:
		    	AlertDialog.Builder ad_Page = new AlertDialog.Builder(Android_SMSbk.this);
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
				
				AlertDialog.Builder ad_about = new AlertDialog.Builder(Android_SMSbk.this);
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
				Android_SMSbk.this.finish();
				break;
		}
		return true;
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
		imb_backup.getLayoutParams().width = iOption1_WPixel;
		imb_backup.getLayoutParams().height = iOption1_HPixel;
		imb_recovery.getLayoutParams().width = iOption1_WPixel;
		imb_recovery.getLayoutParams().height = iOption1_HPixel;
		imb_menu02.getLayoutParams().width = iOption2_WPixel;
		imb_menu02.getLayoutParams().height = iOption2_HPixel;
		imb_menu03.getLayoutParams().width = iOption2_WPixel;
		imb_menu03.getLayoutParams().height = iOption2_HPixel;
		imb_menu05.getLayoutParams().width = iOption2_WPixel;
		imb_menu05.getLayoutParams().height = iOption2_HPixel;
		imb_menu06.getLayoutParams().width = iOption2_WPixel;
		imb_menu06.getLayoutParams().height = iOption2_HPixel;
		
		
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
			imb_backup.setBackgroundResource(R.drawable.imb_main03_01);
		    imb_recovery.setBackgroundResource(R.drawable.imb_main03_02);
		}
		else if( sMyLan.equals("Chinese") )
		{
			imb_backup.setBackgroundResource(R.drawable.imb_main03_01_ch);
		    imb_recovery.setBackgroundResource(R.drawable.imb_main03_02_ch);
		}
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
			intent.setClass(Android_SMSbk.this, Android_SMSvc.class); 
			break;
		case 2:
			intent.setClass(Android_SMSbk.this, Android_SMSbs.class);
			break;
		case 3:
			intent.setClass(Android_SMSbk.this, Android_SMSbk.class);
			break;
		default:
			intent.setClass(Android_SMSbk.this, Android_SMSvc.class);
			break;
		}
		startActivityForResult(intent, 0);
		this.finish();
	}
	/************************************
	 * Function Name: isExternalStorageAvail
	 * Input:
	 * Output:
	 * Description: 檢查是否有外部裝置
	 *************************************/
	private boolean isExternalStorageAvail() 
	{       
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);    
	}
	
	/************************************
	* Function Name: copyFile
	* Input: 原始路徑,目標路徑
	* Output:
	* Description: 複製檔案
	*************************************/
   private void copyFile(File src, File dst) throws IOException 
   {
     FileChannel inChannel = new FileInputStream(src).getChannel();          
     FileChannel outChannel = new FileOutputStream(dst).getChannel();          
     try 
     {             
       inChannel.transferTo(0, inChannel.size(), outChannel);          
     } 
     finally 
     {             
       if (inChannel != null)                
    	   inChannel.close();             
       if (outChannel != null)                
    	   outChannel.close();          
     }       
   } 
	/************************************
	 * Function Name: ExportDatabaseFileTask
	 * Input:
	 * Output:
	 * Description: 匯出資料庫Task
	 *************************************/
	private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> 
	{       
	   private final ProgressDialog dialog = new ProgressDialog(Android_SMSbk.this);         
	   // can use UI thread here       
	   protected void onPreExecute() 
	   {  
		 if(Common.My_Language.equals("Chinese")) //中文版
		 {
			 this.dialog.setMessage("資料庫備份中...");
		 }
		 else //英文版
		 {
			 this.dialog.setMessage("Exporting Database,plz wait...");
		 }
		 this.dialog.show();       
	   }
	   
	   /************************************
		* Function Name: doInBackground
		* Input:
		* Output:
		* Description: 開始匯出 Thread
		* automatically done on worker thread (separate from UI thread)   
		*************************************/      
	   protected Boolean doInBackground(final String... args) 
	   { 
		 try
		 { Thread.sleep(2000); }
		 catch(Exception ex){};
		 
		 //原始路徑
		 File dbFile = new File(Environment.getDataDirectory() + "/data/com.jb.Android.JBSMS_GP/databases/SMART_SMS_ROBOT.db");
		 //匯出路徑
		 File exportDir = new File(Environment.getExternalStorageDirectory(), "Smart SMS robot");
		 if (!exportDir.exists())
		 {             
			 exportDir.mkdirs();
		 }
		 File file = new File(exportDir, dbFile.getName());            
		 try 
		 {             
			 file.createNewFile();             
			 copyFile(dbFile, file);
			 return true;          
		 } 
		 catch (IOException ex) 
		 {             
			 Log.e( "JB_TAG", ex.getMessage());
			 return false;          
		 }
	   }      
	   
	   /************************************
		* Function Name: onPostExecute
		* Input:
		* Output:
		* Description: 匯出 Thread 結束
		* !! can use UI thread here !! 
		*************************************/     
	   protected void onPostExecute(final Boolean success) 
	   {          
		 if (this.dialog.isShowing()) 
		 {             
		   this.dialog.dismiss();          
		 }          
		 if (success) //匯出成功
		 {              
			//顯示警告訊息
			AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
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
				ad_Action.setMessage(R.string.tm_BK_backup_suc_ch);
				ad_Action.setNegativeButton("關閉",CancelClick);
			}
			else
			{
				ad_Action.setTitle(R.string.tm_OP_done);
				ad_Action.setMessage(R.string.tm_BK_backup_suc);
				ad_Action.setNegativeButton("Close",CancelClick);
			}
			
			ad_Action.show();
		 } 
		 else //匯出失敗
		 {             
			//顯示警告訊息
			AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
			ad_Action.setIcon(R.drawable.alert);
			
			
			DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which)
			   {
				   dialog.cancel();
			   }
			};
			
			if(Common.My_Language.equals("Chinese")) //中文版
		    {
				ad_Action.setTitle(R.string.tm_OP_alert_ch);
				ad_Action.setMessage(R.string.tm_BK_backup_fail02_ch);
				ad_Action.setNegativeButton("關閉",CancelClick);
		    }
			else //英文版
			{
				ad_Action.setTitle(R.string.tm_OP_alert);
				ad_Action.setMessage(R.string.tm_BK_backup_fail02);
				ad_Action.setNegativeButton("Close",CancelClick);
			}
			
			ad_Action.show();
		 }       
	   }         
	      
	} 
	
	
	
	/************************************
	 * Function Name: ExportDatabaseFileTask
	 * Input:
	 * Output:
	 * Description: 匯入資料庫Task
	 *************************************/
	private class ImportDatabaseFileTask extends AsyncTask<String, Void, Boolean> 
	{       
	   private final ProgressDialog dialog = new ProgressDialog(Android_SMSbk.this);         
	   // can use UI thread here       
	   protected void onPreExecute() 
	   {  
	     if(Common.My_Language.equals("Chinese")) //中文版
		 {
	    	 this.dialog.setMessage("資料庫匯入中...");  
		 }
		 else //英文版
		 {
			 this.dialog.setMessage("Importing Database,plz wait...");  
		 }
		 this.dialog.show();       
	   }
	   
	   /************************************
		* Function Name: doInBackground
		* Input:
		* Output:
		* Description: 開始匯入Thread
		* automatically done on worker thread (separate from UI thread)   
		*************************************/      
	   protected Boolean doInBackground(final String... args) 
	   {     
		 try
		 { Thread.sleep(2000); }
	     catch(Exception ex){};
	     
		 //原始路徑
		 File dbFile = new File(Environment.getExternalStorageDirectory()+"/Smart SMS robot/SMART_SMS_ROBOT.db");
		 //匯入路徑
		 File importDir = new File(Environment.getDataDirectory() + "/data/com.jb.Android.JBSMS_GP/databases");
		 
		 /*
		 if (!importDir.exists())
		 {             
			 importDir.mkdirs();
		 }
		 */
		 File file = new File(importDir, dbFile.getName());            
		 try 
		 {             
			 file.createNewFile();             
			 copyFile(dbFile, file);
			 return true;          
		 } 
		 catch (IOException ex) 
		 {             
			 Log.e( "JB_TAG", ex.getMessage());
			 return false;          
		 }
	   }      
	   
	   /************************************
		* Function Name: onPostExecute
		* Input:
		* Output:
		* Description: 匯出 Thread 結束
		* !! can use UI thread here !! 
		*************************************/     
	   protected void onPostExecute(final Boolean success) 
	   {          
		 if (this.dialog.isShowing()) 
		 {             
		   this.dialog.dismiss();          
		 }          
		 if (success) //匯入成功
		 {              
			//顯示警告訊息
			AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
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
				ad_Action.setMessage(R.string.tm_BK_recovery_suc_ch);
				ad_Action.setNegativeButton("關閉",CancelClick);
		    }
			else //英文版
			{
				ad_Action.setTitle(R.string.tm_OP_done);
				ad_Action.setMessage(R.string.tm_BK_recovery_suc);
				ad_Action.setNegativeButton("Close",CancelClick);
			}
			ad_Action.show();
		 } 
		 else //匯入失敗
		 {             
			//顯示警告訊息
			AlertDialog.Builder ad_Action = new AlertDialog.Builder(Android_SMSbk.this);
			ad_Action.setIcon(R.drawable.alert);
			
			DialogInterface.OnClickListener CancelClick = new DialogInterface.OnClickListener()
			{
			   public void onClick(DialogInterface dialog, int which)
			   {
				   dialog.cancel();
			   }
			};
			
			if(Common.My_Language.equals("Chinese")) //中文版
			{
				ad_Action.setTitle(R.string.tm_OP_alert_ch);
				ad_Action.setMessage(R.string.tm_BK_recovery_fail02_ch);
				ad_Action.setNegativeButton("關閉",CancelClick);
			}
			else //英文版
			{
				ad_Action.setTitle(R.string.tm_OP_alert);
				ad_Action.setMessage(R.string.tm_BK_recovery_fail02);
				ad_Action.setNegativeButton("Close",CancelClick);
			}
			
			ad_Action.show();
		 }       
	   }         
	     
	} 
}
