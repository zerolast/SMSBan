package com.example.smsben;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.sms.db.DOA;

public class ContactBanTimer extends CountDownTimer{

	private String TAG="ContactBanTimer";
	public String number_attached_with_timer;
	public String name_attached_with_timer;
	public Context context;
	public int lastSecondNoticed;
	public DOA obj ;
	public MyApplication application;

	
	public ContactBanTimer(long millisInFuture, long countDownInterval,Context con) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		context=con;
		application=(MyApplication)con.getApplicationContext();
	}
	
	public void changeContext(Context con)
	{
		context=con;
	}
	
	
	public String getNumber_attached_with_timer() {
		return number_attached_with_timer;
	}

	public void setNumber_attached_with_timer(String number_attached_with_timer) {
		this.number_attached_with_timer = number_attached_with_timer;
	}
	
	public int getLastSecondNoticed() {
		return lastSecondNoticed;
	}

	public void setLastSecondNoticed(int lastSecondNoticed) {
		this.lastSecondNoticed = lastSecondNoticed;
	}

	public String getName_attached_with_timer() {
		return name_attached_with_timer;
	}

	public void setName_attached_with_timer(String name_attached_with_timer) {
		this.name_attached_with_timer = name_attached_with_timer;
	}


	

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		Log.i(TAG + " "+name_attached_with_timer, "TIMER FINISHED ");
		resetCountToZeroForThisContact();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		Log.i(TAG + " "+name_attached_with_timer, ""+(millisUntilFinished/1000));
		lastSecondNoticed=(int)millisUntilFinished/1000;
	}
	
	public void resetCountToZeroForThisContact()
	{
		try
		{
			String Message="";
			obj = new DOA(context);
	        obj.openToWrite();
	        
	        
	        //also show alerts and clear fields for that number
	        
	    	Cursor curser_for_filled_row=obj.getRowForMatchedContact(number_attached_with_timer);
			if(curser_for_filled_row!=null)
			{
				Log.i(TAG, "CURSOR IS NOT NULL AND NUMBER OF ROWS ARE "+curser_for_filled_row.getCount());
				curser_for_filled_row.moveToFirst();
				do {
					Log.i(TAG, "CONTACT is "+curser_for_filled_row.getString(1)+" Down Count is "+curser_for_filled_row.getString(3));
					Log.i(TAG, "msg in 1 column is "+curser_for_filled_row.getString(4));
					Log.i(TAG, "msg in 2 column is "+curser_for_filled_row.getString(5));
					Log.i(TAG, "msg in 3 column is "+curser_for_filled_row.getString(6));
					if(curser_for_filled_row.getString(4)!=null && (!curser_for_filled_row.getString(4).equals("")))
					{
						Message=Message+" Message 1:"+curser_for_filled_row.getString(4);
					}
					else
					{
						Message=Message+"";
					}
					
					if(curser_for_filled_row.getString(5)!=null && (!curser_for_filled_row.getString(5).equals("")))
					{
						Message=Message+"\n"+" Message 2:"+curser_for_filled_row.getString(5);
					}
					else
					{
						Message=Message+"";
					}
					
					if(curser_for_filled_row.getString(6)!=null && (!curser_for_filled_row.getString(6).equals("")))
					{
						Message=Message+"\n"+" Message 3:"+curser_for_filled_row.getString(6);
					}
					else
					{
						Message=Message+"";
					}
					
				} while (curser_for_filled_row.moveToNext());
				curser_for_filled_row.close();
			}
			else
			{
				Log.i(TAG, "CURSOR IS NULL");
			}
	        
			if(context!=null)
			{
				  
			       
				if(((MainActivity)context).isFinishing())
				{
					
					Log.i(TAG, "activity is finished");
					
				      int x= obj.update_pending_msg_value(1,number_attached_with_timer);
				      Log.i(TAG, "number of rows updated to have a pending count is with given counter is "+x);
					
				}
				else
				{
					 
					 Log.i(TAG, "Reset Counter for number "+number_attached_with_timer);
				       int x= obj.update_count(0, number_attached_with_timer);
				       obj.clear_Msg_Columns(number_attached_with_timer);
				       Log.i(TAG, "number of rows updated with given counter is "+x);
				       
				       //this method will update the adapter with updated count
				       //create handler to update the list
				       application=(MyApplication)context.getApplicationContext();
				       application.global_handler.sendEmptyMessage(0);
				       
					Log.i(TAG, "activity is not finished");
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
				    alertDialog.setTitle("Text Messgaes From "+name_attached_with_timer);
				    alertDialog.setMessage(Message);
				    
				   
				    alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

				      public void onClick(DialogInterface dialog, int id) {

				        dialog.dismiss();

				    }});
				    
				   AlertDialog dialog= alertDialog.create();
				   dialog.setCanceledOnTouchOutside(true);
				   dialog.show();

				}
			    		        
			}
			else
			{
				Log.i(TAG, "CONTEXT IS NULL");

			}
			
	    
		       obj.CloseDatabase();

	       
		}
		catch(Exception e)
		{
			Log.i(TAG, "WE HAVE EXCEPTION HERE ()()()()()()()");
			e.printStackTrace();
		       obj.CloseDatabase();

		}
		 
        
	}
	
	
	

}
