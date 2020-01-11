package com.example.smsben;

import java.util.HashMap;

import com.example.sms.db.DOA;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	String TAG = "MyReceiver";
	String body, num,number_after_removing_first_char,number_after_removing_second_char,number_after_removing_third_char;
	Intent intent_to_Service;
	SmsMessage sms[];
	private Context con;
	private DOA obj ;
	String number_successfully_matched;
	public static final String COLUMN_MSG1 = "msg1";
	public static final String COLUMN_MSG2 = "msg2";
	public static final String COLUMN_MSG3 = "msg3";
	
	public String msg1_data,msg2_data,msg3_data;
	public int last_updated_column;
	MyApplication application;
//	HashMap<String, Integer> temp_map;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "on Receive");
		con=context;
		Bundle bundle = intent.getExtras();
		obj = new DOA(context);
        obj.openToWrite();
        
        
//      application=(MyApplication)con.getApplicationContext();
//		temp_map=application.map_number_down_countes_global;
	
        
        //add data from database into temp map
        
        
		if (bundle != null) {

			Object[] ob = (Object[]) bundle.get("pdus");
			sms = new SmsMessage[ob.length];
			for (int i = 0; i < ob.length; i++) {
				sms[i] = SmsMessage.createFromPdu((byte[]) ob[i]);
				body = sms[i].getMessageBody();
				num = sms[i].getOriginatingAddress();
			}
			System.out.println(TAG + " Body :" + body);
			System.out.println(TAG + " Num :" + num);
			
			//check if this number is in the ben list 
			//if it is present than save this sms in the file with his name in datbase
		
			
			/*Cursor curser_before_msg_insertion=obj.selcet();
			if(curser_before_msg_insertion!=null)
			{
				Log.i(TAG, "CURSOR IS NOT NULL AND NUMBER OF ROWS ARE "+curser_before_msg_insertion.getCount());
				curser_before_msg_insertion.moveToFirst();
				do {
					
					Log.i(TAG, "MESSAGE 1 for CONTACT "+curser_before_msg_insertion.getString(2)+" is "+curser_before_msg_insertion.getString(4)+ " AND COUNT IS "+curser_before_msg_insertion.getString(3));
				} while (curser_before_msg_insertion.moveToNext());
			}
			else
			{
				Log.i(TAG, "CURSOR IS NULL");
			}*/
			
			Log.i(TAG + " BEFORE ", "checkForDownCountOFNumber-----------");
			checkForDownCountOFNumber();
			obj.CloseDatabase();

			
		}
	}
	
	public void checkForDownCountOFNumber()
	{
			
		//to get the all possible numbers of given number
		getAllPossibleNumbers();
		
		if(obj.isNumberPresent(num))
		{
			number_successfully_matched=num;
			banSMSAndStoreMessage(number_successfully_matched);
		}
		else if(obj.isNumberPresent(number_after_removing_first_char))
		{
			number_successfully_matched=number_after_removing_first_char;
			banSMSAndStoreMessage(number_successfully_matched);
		}
		else if(obj.isNumberPresent(number_after_removing_second_char))
		{
			number_successfully_matched=number_after_removing_second_char;
			banSMSAndStoreMessage(number_successfully_matched);
		}
		else if(obj.isNumberPresent(number_after_removing_third_char))
		{
			number_successfully_matched=number_after_removing_third_char;
			banSMSAndStoreMessage(number_successfully_matched);
		}
		else
		{
			Log.i(TAG, "number not present in the map ");
		}
	}
		
		public void banSMSAndStoreMessage(String number)
		{
			int count=obj.getDownCountValue(number);
			Log.i(TAG, "Down count for given number is "+count);
			if(count<-5)
			{
				Log.i(TAG, "This CONTACT IS BAN ");
				
				//user record before storing message
				Log.i(TAG, "before Storing Message");
				
				Cursor curser_before_inserting_message=obj.getRowForMatchedContact(number);
				if(curser_before_inserting_message!=null)
				{
					Log.i(TAG, "CURSOR IS NOT NULL AND NUMBER OF ROWS ARE "+curser_before_inserting_message.getCount());
					Log.i(TAG, "Number of columns are "+curser_before_inserting_message.getColumnCount());
					curser_before_inserting_message.moveToFirst();
					do {
							Log.i(TAG, "CONTACT is "+curser_before_inserting_message.getString(1)+" Down Count is "+curser_before_inserting_message.getString(3));
							last_updated_column=curser_before_inserting_message.getInt(7);
							if(last_updated_column==0)
							{
								Log.i(TAG, "MSG Columns is 1");
								Log.i(TAG, "message is "+curser_before_inserting_message.getString(4));
							}
							else if(last_updated_column==1)
							{
								Log.i(TAG, "MSG Columns is 2");
								Log.i(TAG, "message is "+curser_before_inserting_message.getString(5));
							}
							else if(last_updated_column==2)
							{
								Log.i(TAG, "MSG Columns is 3");
								Log.i(TAG, "message is "+curser_before_inserting_message.getString(6));
							}
							else if(last_updated_column==3)
							{
								Log.i(TAG, "MSG Columns is 1");
								Log.i(TAG, "message is "+curser_before_inserting_message.getString(4));
							}

					} while (curser_before_inserting_message.moveToNext());
					curser_before_inserting_message.close();
				}
				else
				{
					Log.i(TAG, "CURSOR IS NULL");
				}
				
				
				
				//check to store it in next msg comuns 
				if(last_updated_column==0)
				{
					//enter data in to first message
					obj.store_Message(body, number,COLUMN_MSG1,1);

				}
				else if(last_updated_column==1)
				{
					//enter data into second message
					obj.store_Message(body, number,COLUMN_MSG2,2);

				}
				else if(last_updated_column==2)
				{
					//enter data into third message
					obj.store_Message(body, number,COLUMN_MSG3,3);

				}
				else if(last_updated_column==3)
				{
					//enter data into first message
					obj.store_Message(body, number,COLUMN_MSG1,1);

				}
				
				
				
				
				abortBroadcast();
				Log.i(TAG, "AFTER ABORTING BROADCAST");

				//check whether data has inserted in db or not 
				Log.i(TAG, "---------------after storing the message------------");
				
				
				
				
				Cursor curser_for_filled_row=obj.getRowForMatchedContact(number);
				if(curser_for_filled_row!=null)
				{
					Log.i(TAG, "CURSOR IS NOT NULL AND NUMBER OF ROWS ARE "+curser_for_filled_row.getCount());
					curser_for_filled_row.moveToFirst();
					do {
						Log.i(TAG, "CONTACT is "+curser_for_filled_row.getString(1)+" Down Count is "+curser_for_filled_row.getString(3));
						Log.i(TAG, "msg in 1 column is "+curser_for_filled_row.getString(4));
						Log.i(TAG, "msg in 2 column is "+curser_for_filled_row.getString(5));
						Log.i(TAG, "msg in 3 column is "+curser_for_filled_row.getString(6));
					} while (curser_for_filled_row.moveToNext());
					curser_for_filled_row.close();
				}
				else
				{
					Log.i(TAG, "CURSOR IS NULL");
				}
				
			}
			else
			{
				Log.i(TAG, "This user has no NEGETIVE VOTE ");
			}
		}
	
	
	public void getAllPossibleNumbers()
	{
		number_after_removing_first_char=num.substring(1);
		Log.i(TAG, "number_after_removing_first_char "+number_after_removing_first_char);
		
		number_after_removing_second_char=num.substring(2);
		Log.i(TAG, "number_after_removing_second_char "+number_after_removing_second_char);
		
		number_after_removing_third_char=num.substring(3);
		Log.i(TAG, "number_after_removing_third_char "+number_after_removing_third_char);
		
	}

}
