package com.example.smsben;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.sms.db.DOA;

public class MainActivity extends Activity implements OnItemClickListener ,OnClickListener{

	ListView lv;
	public ArrayList<ListItem> item_list;
	int name,phone;
	public ContactListAdapter adapter;
	private DOA obj ;
	private String TAG="MainActivity";
//	public HashMap<String, Integer> map_numbers_counts; 
	private Button button_home,button_baned,button_favourite;
	public MyUpdateThroughTimerHandler mhandler=null;
	ProgressDialog pd;
	public Cursor global_c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "onCreate()");
		lv=(ListView)findViewById(R.id.listView1);
		mhandler=new MyUpdateThroughTimerHandler();
		button_baned=(Button)findViewById(R.id.button_baned);
		button_favourite=(Button)findViewById(R.id.button_favourite);
		button_home=(Button)findViewById(R.id.button_home);
		
		
		registerListener();
		
		
		obj = new DOA(getApplicationContext());
        obj.openToWrite();
        item_list=new ArrayList<ListItem>();
        
        MyContactLoadAsyncTask task=new MyContactLoadAsyncTask();
        task.execute();
        
        
		MyApplication application=(MyApplication)getApplicationContext();
		application.global_handler=mhandler;

	}
	
	private void registerListener()
	{
		lv.setOnItemClickListener(this);
		button_baned.setOnClickListener(this);
		button_favourite.setOnClickListener(this);
		button_home.setOnClickListener(this);
		
		button_baned.setEnabled(false);
		button_favourite.setEnabled(false);
		button_home.setEnabled(false);
		
	}
	
	private void printContactFromDatabase()
	{
		Log.i(TAG, "printContactFromDatabase()");
        obj.openToWrite();
		Cursor c=obj.selcet();
		Log.i(TAG, "Total number of contacts are "+c.getCount());
		c.close();
		obj.CloseDatabase();
	}
	
    public Bitmap getPhoto(String phoneNumber) 
    {
        Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Uri photoUri = null;
        ContentResolver cr = this.getContentResolver();
        Cursor contact = cr.query(phoneUri, new String[] { ContactsContract.Contacts._ID }, null, null, null);

        if(contact!=null)
        {
        	if (contact.moveToFirst()) 
            {
                long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
                photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
            }
        }
        else 
        {
            //Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
        	Bitmap defaultPhoto=null;
        	return defaultPhoto;
        }
        if (photoUri != null) 
        {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
            if (input != null) 
            {
                return BitmapFactory.decodeStream(input);
            }
        }
        else 
        {
        	Bitmap defaultPhoto=null;
//            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
            return defaultPhoto;
        }
    	Bitmap defaultPhoto=null;
    	contact.close();
//        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_report_image);
        return defaultPhoto;
    }
	
	
	public void fetchContacts()
	{
		Log.i(TAG, "fetchContacts()");
		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		startManagingCursor(cursor);
		if(cursor!=null)
		{
	        if(cursor.moveToFirst()){
	            do{
	           	 ListItem temp_item=new ListItem();
	           		temp_item.contact_name=cursor
	   						.getString(cursor
	   								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	   				temp_item.contact_number=cursor
	   						.getString(cursor
	   								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	   				temp_item.photo=getPhoto(cursor
	   						.getString(cursor
	   								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
	   				
	   				
	   				temp_item.pending_flag_count=obj.getPnedingCountValue(cursor
	   						.getString(cursor
	   								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
	   				
	   				if(!obj.isNumberPresent(cursor
	   						.getString(cursor
	   								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))))
	   				{
	   					//if contact is not present already than add it
	   					Log.i(TAG, "contact not present in db so insert it ");
	   					obj.insert(temp_item.contact_name,temp_item.contact_number);

	   					temp_item.down_count=0;

	   					
	   				}
	   				else
	   				{
	   					Log.i(TAG, "Contact already present");
	   					int downvalue;
	   					downvalue=obj.getDownCountValue(cursor
	   						.getString(cursor
	   								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
	   					temp_item.down_count=downvalue;
	   					//Log.i(TAG, " with the down count value "+downvalue);
	   					
	   				}
	   				
	   				item_list.add(temp_item);

	            }while(cursor.moveToNext());
	           }
	           
	           stopManagingCursor(cursor);

	           cursor.close();
		}
		else
		{
			Log.i(TAG, "CURSOR IS NULL");
		}
		obj.CloseDatabase();
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onItemClicked");
		MyDialog dialog=new MyDialog(MainActivity.this, item_list.get(position),position);
		dialog.setCancelable(true);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);	
		dialog.show();
	}
	
	public class MyDialog extends Dialog implements android.view.View.OnClickListener
	{
		Context c;
		ListItem item_clicked;
		int position_clicked;
		RelativeLayout layout_call,layout_text,layout_pending;
		public MyDialog(Activity context,ListItem item,int pos) {
			super(context);
			// TODO Auto-generated constructor stub
			c=context;
			item_clicked=item;
			position_clicked=pos;
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_for_item_click);
			layout_call=(RelativeLayout)findViewById(R.id.layout_call);
			layout_text=(RelativeLayout)findViewById(R.id.layout_text);
			layout_pending=(RelativeLayout)findViewById(R.id.layout_pending);
			layout_call.setOnClickListener(this);
			layout_text.setOnClickListener(this);
			layout_pending.setOnClickListener(this);
			
			if(item_clicked.pending_flag_count==1)
			{
				layout_pending.setVisibility(View.VISIBLE);
			}
			else
			{
				layout_pending.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			if(v==layout_call)
			{
				if(item_clicked!=null)
				{
					 String uri = "tel:" + item_clicked.contact_number.trim().toString() ;
					 Intent intent = new Intent(Intent.ACTION_CALL);
					 intent.setData(Uri.parse(uri));
					 startActivity(intent);
				}
			}
			else if(v==layout_text)
			{
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.putExtra("address", item_clicked.contact_number);
				startActivity(smsIntent);
			}
			else if(v==layout_pending)
			{
				// show alert with column values 
				resetCountToZeroForClickedContact(item_clicked.contact_number,item_clicked.contact_name,position_clicked);
			}
			
			MyDialog.this.dismiss();
			
		}
		
	}
	
	public void resetCountToZeroForClickedContact(String number_of_clicked_contact,String name_of_person,int position_clicked)
	{
        obj.openToWrite();
		Log.i(TAG, "resetCountToZeroForClickedContact()");
		try
		{
			String Message="";

	        
	        //also show alerts and clear fields for that number
	        
	    	Cursor curser_for_filled_row=obj.getRowForMatchedContact(number_of_clicked_contact);
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
	        
				       
						Log.i(TAG, "activity is not finished");
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
					    alertDialog.setTitle("Text Messgaes From "+name_of_person);
					    alertDialog.setMessage(Message);
					    alertDialog.setNegativeButton("Cancel",new MyDialogDismissListener(number_of_clicked_contact,name_of_person,position_clicked));
					    
				    
				   AlertDialog dialog= alertDialog.create();
				   dialog.setCanceledOnTouchOutside(true);
				   dialog.show();
			    		        
		    	   obj.CloseDatabase();
		}
		catch(Exception e)
		{
			Log.i(TAG, "WE HAVE EXCEPTION HERE <><><><>><><><>><");
			e.printStackTrace();
		    obj.CloseDatabase();

		}
		
	
		 
        
	}
	
	class MyDialogDismissListener implements DialogInterface.OnClickListener
	{
		
		String number,name;
		int position_clicked;
		public MyDialogDismissListener(String num,String nam,int position) {
			// TODO Auto-generated constructor stub
			number=num;
			name=nam;
			position_clicked=position;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub

			
				obj.openToWrite();
				dialog.dismiss();
		        int row_updated= obj.update_pending_msg_value(0,number);
			    Log.i(TAG, "number of rows updated to have a pending count is with given counter is "+row_updated);
				
				 
				   Log.i(TAG, "Reset Counter for number "+number);
			       int x= obj.update_count(0, number);
			       obj.clear_Msg_Columns(number);
			       Log.i(TAG, "number of rows updated with given counter is "+x);
			 
			       obj.CloseDatabase();     
			       
			       
			       item_list.get(position_clicked).pending_flag_count=0;
			       item_list.get(position_clicked).down_count=0;
			       
			       adapter.notifyDataSetChanged();
			       //getMainList();
			   //   MainActivity.this.mhandler.sendEmptyMessage(0);
			      
			 
		}
		
	}
	
	public long fetchContactIdFromPhoneNumber(String phoneNumber) {
	    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
	        Uri.encode(phoneNumber));
	    Cursor cursor = this.getContentResolver().query(uri,
	        new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
	        null, null, null);

	    String contactId = "";

	    if (cursor.moveToFirst()) {
	        do {
	        contactId = cursor.getString(cursor
	            .getColumnIndex(PhoneLookup._ID));
	        } while (cursor.moveToNext());
	    }

	    return Long.parseLong(contactId);
	  }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==button_home)
		{
			button_baned.setEnabled(true);
			button_favourite.setEnabled(true);
			button_home.setEnabled(false);

			
			MyOperationSelectionAsyncTask task=new MyOperationSelectionAsyncTask(1);
			task.execute();
		}
		else if(v==button_baned)
		{
			button_baned.setEnabled(false);
			button_favourite.setEnabled(true);
			button_home.setEnabled(true);

			MyOperationSelectionAsyncTask task=new MyOperationSelectionAsyncTask(2);
			task.execute();
		}
		else if(v==button_favourite)
		{
			button_baned.setEnabled(true);
			button_favourite.setEnabled(false);
			button_home.setEnabled(true);

			MyOperationSelectionAsyncTask task=new MyOperationSelectionAsyncTask(3);
			task.execute();
		}
	}
	
	private void getMainList()
	{
		ArrayList<ListItem> temp_dummy_list=new ArrayList<ListItem>();
		Log.i(TAG, "GetMainList()");
			obj.openToWrite();
			item_list.clear();
			global_c=obj.getRows();
			startManagingCursor(global_c);
			if(global_c!=null)
			{
				Log.i(TAG, "all rows count is "+global_c.getCount());
				if(global_c.moveToFirst())
				{
					do {
						ListItem item=new ListItem();
						item.contact_name=global_c.getString(1);
						item.contact_number=global_c.getString(2);
						item.down_count=global_c.getInt(3);
						item.pending_flag_count=global_c.getInt(8);
//						item_list.add(item);
						temp_dummy_list.add(item);
					} while (global_c.moveToNext());
				}
			}
			
			//start loading images from background
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.notifyDataSetChanged();
					
				}
			});
			global_c.close();
			obj.CloseDatabase();
			stopManagingCursor(global_c);
			item_list.addAll(temp_dummy_list);
			temp_dummy_list.clear();			
			
		

	}
	
	private void getBannedList()
	{
		ArrayList<ListItem> temp_dummy_list=new ArrayList<ListItem>();
		obj.openToWrite();
		item_list.clear();
		global_c=obj.getRowsWhereContactIsBaned();
		startManagingCursor(global_c);
		if(global_c!=null)
		{
			global_c.moveToFirst();
			if(global_c.moveToFirst())
			{
				do {
					ListItem item=new ListItem();
					item.contact_name=global_c.getString(1);
					item.contact_number=global_c.getString(2);
					item.down_count=global_c.getInt(3);
					item.pending_flag_count=global_c.getInt(8);
//					item_list.add(item);
					temp_dummy_list.add(item);
				} while (global_c.moveToNext());
			}
			
		}
		//start loading images from background
		stopManagingCursor(global_c);
		global_c.close();
		obj.CloseDatabase();
		item_list.addAll(temp_dummy_list);
		temp_dummy_list.clear();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();

			}
		});
		
	}
	
	private void getFavouriteList()
	{
		ArrayList<ListItem> temp_dummy_list=new ArrayList<ListItem>();
		obj.openToWrite();
		item_list.clear();
		global_c=obj.getRowsWhereContactIsFavourite();
		if(global_c!=null)
		{
			global_c.moveToFirst();
			if(global_c.moveToFirst())
			{
				do {
					ListItem item=new ListItem();
					item.contact_name=global_c.getString(1);
					item.contact_number=global_c.getString(2);
					item.down_count=global_c.getInt(3);
					item.pending_flag_count=global_c.getInt(8);
					temp_dummy_list.add(item);
//					item_list.add(item);
				} while (global_c.moveToNext());

			}
		}
		//start loading images from background
		
		global_c.close();
		obj.CloseDatabase();
		item_list.addAll(temp_dummy_list);
		temp_dummy_list.clear();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();

			}
		});
		
	}
	
	public class MyUpdateThroughTimerHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.i(TAG, "HANDLER CALLED TO UPDATE THE LIST");
			MyOperationSelectionAsyncTask task=new MyOperationSelectionAsyncTask(1);
			task.execute();
		}
	}

	public class MyPhotoLoadingAsyncTask extends AsyncTask<Void, Void, Void>
	{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			for(int i=0;i<item_list.size();i++)
			{
				String number=item_list.get(i).contact_number;
				item_list.get(i).photo=getPhoto(number);
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			adapter.notifyDataSetChanged();
		}
		
	}
	
	public class MyOperationSelectionAsyncTask extends AsyncTask<Void, Void, Void>
	{
		int method_to_call;
		MyOperationSelectionAsyncTask(int i)
		{
			method_to_call=i;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if(method_to_call==1)
			{
				getMainList();
			}
			else if(method_to_call==2)
			{
				getBannedList();
			}
			else if(method_to_call==3)
			{
				getFavouriteList();
			}
		
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			MyPhotoLoadingAsyncTask task=new MyPhotoLoadingAsyncTask();
			task.execute();
			adapter.notifyDataSetChanged();
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.i(TAG, "onResume()");
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause()");
	}
	
	public class MyContactLoadAsyncTask extends AsyncTask<Void, Void, Void>
	{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			pd=new ProgressDialog(MainActivity.this);
			pd.setCancelable(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("Wait Please...");
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			fetchContacts();
			printContactFromDatabase();

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter=new ContactListAdapter(MainActivity.this,item_list);
			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			
			
			button_baned.setEnabled(true);
			button_favourite.setEnabled(true);
			pd.dismiss();
		}
		
	}

}
