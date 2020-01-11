package com.example.smsben;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sms.db.DOA;


public class ContactListAdapter extends BaseAdapter{

	String TAG="ContactListAdapter";
	public ArrayList<ListItem> list_of_item;
	Context con;
	ImageView image_reset;
	TextView text_name = null,text_number = null,count,text_status;
	Button button_up = null,button_down = null;
	ImageView photo;
	LinearLayout reset_container;
	//public HashMap<String, Integer> map_number_down_countes;
	private DOA obj ;
	private MyApplication application;
	private EditText edittext;
	private String current_contact_tag;
	private AlertDialog alert_dialog;
	private boolean alertReady;
	
	public ContactListAdapter(Context c,ArrayList<ListItem> list) {
		// TODO Auto-generated constructor stub
		list_of_item=list;
		obj = new DOA(c);
		application=(MyApplication)c.getApplicationContext();
		application.map_number_timerobj=new HashMap<String, ContactBanTimer>();
		
		con=c;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_of_item.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list_of_item.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v=convertView;
		Log.i(TAG+" getView() ", " position is "+position+" Contact name is "+list_of_item.get(position).contact_name);
		if(v==null)
		{
			Log.i(TAG, "Convertview null");
			LayoutInflater inflater=(LayoutInflater)con.getSystemService(con.LAYOUT_INFLATER_SERVICE);
			v=inflater.inflate(R.layout.contact_list_row_item, parent, false);
			image_reset=(ImageView)v.findViewById(R.id.image_reset);
			text_name=(TextView)v.findViewById(R.id.textView1);
			text_number=(TextView)v.findViewById(R.id.textView2);
			button_up=(Button)v.findViewById(R.id.button1);
			button_down=(Button)v.findViewById(R.id.button2);
			count=(TextView)v.findViewById(R.id.count);
			text_status=(TextView)v.findViewById(R.id.text_status_data);
			
			reset_container=(LinearLayout)v.findViewById(R.id.reset_container);
			
			if(list_of_item.get(position).pending_flag_count==0)
			{
				reset_container.setBackgroundColor(Color.parseColor("#22b14c"));
			}
			else
			{
				reset_container.setBackgroundColor(Color.RED);
			}
			
			photo=(ImageView)v.findViewById(R.id.photo);
			if(list_of_item.get(position).photo!=null)
			photo.setImageBitmap(list_of_item.get(position).photo);
			button_up.setOnClickListener(new MyClickListener(v));
			button_up.setTag(position+"up");
			button_down.setOnClickListener(new MyClickListener(v));
			button_down.setTag(position+"down");
			image_reset.setOnClickListener(new MyClickListener(v));
			image_reset.setTag(position+"reset");
			
			count.setTag(position+"count");
			count.setText(String.valueOf(list_of_item.get(position).down_count));
			
				if(list_of_item.get(position).down_count>5)
				{
					text_status.setText("Favorite");
				}
				if(list_of_item.get(position).down_count<-5)
				{
					text_status.setText("Ban");
				}
				if(list_of_item.get(position).down_count>-6 && list_of_item.get(position).down_count<6)
				{
					text_status.setText("None");

				}
				text_status.setTag(position+"status");
			
			
		}
		
		
		Log.i(TAG, "Convertview is not Null");
		text_name=(TextView)v.findViewById(R.id.textView1);
		text_number=(TextView)v.findViewById(R.id.textView2);
		count=(TextView)v.findViewById(R.id.count);
		text_status=(TextView)v.findViewById(R.id.text_status_data);
		text_name.setText(list_of_item.get(position).contact_name);
		text_number.setText(list_of_item.get(position).contact_number);
		

		button_up=(Button)v.findViewById(R.id.button1);
		button_down=(Button)v.findViewById(R.id.button2);
		image_reset=(ImageView)v.findViewById(R.id.image_reset);
		
		reset_container=(LinearLayout)v.findViewById(R.id.reset_container);
		
		if(list_of_item.get(position).pending_flag_count==0)
		{
			reset_container.setBackgroundColor(Color.parseColor("#22b14c"));
		}
		else
		{
			reset_container.setBackgroundColor(Color.RED);
		}
		
		
		photo=(ImageView)v.findViewById(R.id.photo);
		if(list_of_item.get(position).photo!=null)
		photo.setImageBitmap(list_of_item.get(position).photo);
		

		button_up.setOnClickListener(new MyClickListener(v));
		button_up.setTag(position+"up");
		button_down.setOnClickListener(new MyClickListener(v));
		button_down.setTag(position+"down");
		image_reset.setOnClickListener(new MyClickListener(v));
		image_reset.setTag(position+"reset");
		count.setTag(position+"count");
		count.setText(String.valueOf(list_of_item.get(position).down_count));
		
		if(list_of_item.get(position).down_count>5)
		{
			text_status.setText("Favorite");
		}
		if(list_of_item.get(position).down_count<-5)
		{
			text_status.setText("Ban");
		}
		if(list_of_item.get(position).down_count>-6 && list_of_item.get(position).down_count<6)
		{
			text_status.setText("None");

		}
		text_status.setTag(position+"status");
		return v;
	}
	
	public class MyClickListener implements OnClickListener
	{
		View row_view;
		View parent;
		public MyClickListener(View v) {
			// TODO Auto-generated constructor stub
		row_view=v;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			String tag=String.valueOf(v.getTag());
			parent=v.getRootView();
			if(tag.contains("up"))
			{
				 obj.openToWrite();

					tag=tag.replace("up", "");
					Log.i(TAG, "position of the up  button is "+tag);
					tag=tag.trim().toString();
					Log.i(TAG, "number on "+tag+" item is "+list_of_item.get(Integer.parseInt(tag)).contact_number);
					int current_count=list_of_item.get(Integer.parseInt(tag)).down_count;
					list_of_item.get(Integer.parseInt(tag)).down_count= current_count+1;

					obj.update_count(current_count+1, list_of_item.get(Integer.parseInt(tag)).contact_number);
					MyApplication application=(MyApplication)con.getApplicationContext();
					
					TextView count=(TextView)parent.findViewWithTag(tag+"count");
					count.setText(String.valueOf((current_count+1)));
	
					
					if((current_count+1)>-6)
					{
						try {
							ContactBanTimer timer= application.map_number_timerobj.get(list_of_item.get(Integer.parseInt(current_contact_tag)).contact_number);
							getColumnValues(list_of_item.get(Integer.parseInt(tag)).contact_number);
							if(timer!=null)
							{
								timer.cancel();
							}
							else
							{
								Log.i(TAG, "timer is null");
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
				    	

					}
					
					if(current_count+1>5)
					{
						TextView status=(TextView)parent.findViewWithTag(tag+"status");
						status.setText("Favourite");
					}
					//start 3 mins timer for this contact
					obj.CloseDatabase();
			}
			
			else if(tag.contains("down"))
			{
		        obj.openToWrite();

				tag=tag.replace("down", "");
				Log.i(TAG, "position of the down  button is "+tag);
				tag=tag.trim().toString();
				current_contact_tag=tag;
				Log.i(TAG, "number on "+tag+" item is "+list_of_item.get(Integer.parseInt(tag)).contact_number);
				int current_count=list_of_item.get(Integer.parseInt(tag)).down_count;
				list_of_item.get(Integer.parseInt(tag)).down_count=current_count-1;

				obj.update_count(current_count-1, list_of_item.get(Integer.parseInt(tag)).contact_number);
				
//				application.map_number_down_countes_global=map_number_down_countes;
				
				
				TextView count=(TextView)parent.findViewWithTag(tag+"count");
				count.setText(String.valueOf((current_count-1)));
				obj.CloseDatabase();
				
				if((current_count-1)<-5)
				{
					TextView status=(TextView)parent.findViewWithTag(tag+"status");
					status.setText("Ban");
					obj.CloseDatabase();
					callForTimerSet();
				}
		        obj.CloseDatabase();
		        
	

			}
			else if(tag.contains("reset"))
			{
		        obj.openToWrite();

				tag=tag.replace("reset", "");
				Log.i(TAG, "position of the down  button is "+tag);
				tag=tag.trim().toString();
				Log.i(TAG, "number on "+tag+" item is "+list_of_item.get(Integer.parseInt(tag)).contact_number);
				list_of_item.get(Integer.parseInt(tag)).down_count= 0;

				obj.update_count(0, list_of_item.get(Integer.parseInt(tag)).contact_number);
				MyApplication application=(MyApplication)con.getApplicationContext();
//				application.map_number_down_countes_global=map_number_down_countes;
				
				TextView count=(TextView)parent.findViewWithTag(tag+"count");
				count.setText(String.valueOf((0)));
				
				TextView status=(TextView)parent.findViewWithTag(tag+"status");
				status.setText("None");
				
				//canceling timer if present
				try {
					ContactBanTimer timer= application.map_number_timerobj.get(list_of_item.get(Integer.parseInt(current_contact_tag)).contact_number);
					if(timer!=null)
					{
						timer.cancel();
					}
					else
					{
						Log.i(TAG, "timer is null");
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				obj.CloseDatabase();
			}
			
			
		}
		
		public void callForTimerSet()
		{
	        edittext =new EditText(con);
	        edittext.setInputType(InputType.TYPE_CLASS_PHONE);
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);
		    alertDialog.setTitle("Timer");
		    alertDialog.setMessage("How many minutes you want to ban this contact");
		    alertDialog.setView(edittext);
		    alertDialog.setPositiveButton("set", new DialogInterface.OnClickListener() 
		    {
		      public void onClick(DialogInterface dialog, int id) 
		      {
		    	  
		      } 
		      
		    }); 
		    
		    alertDialog.setNeutralButton("ban",new DialogInterface.OnClickListener() {

			      public void onClick(DialogInterface dialog, int id) {

			        dialog.dismiss();

			    }});

		   					    

		    alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

		      public void onClick(DialogInterface dialog, int id) {

		        dialog.dismiss();

		    }});
		   alert_dialog= alertDialog.create();
		   alert_dialog.setCanceledOnTouchOutside(true);
		   alertReady=false;
		   alert_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
		        @Override
		        public void onShow(final DialogInterface dialog) {
		            if (alertReady == false) {
		                Button button = alert_dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		                button.setOnClickListener(new View.OnClickListener() {
		                    @Override
		                    public void onClick(View v) {
		                        //do something
		                    	
		                      String timer_to_set=edittext.getEditableText().toString();
		      		    	  int time=Integer.parseInt(timer_to_set);
		      		    	  
		      		    	  if(time>(8*60))
		      		    	  {
		      		    		Toast.makeText(con, "Timer limit is too high", Toast.LENGTH_LONG).show();
		      		    	  }
		      		    	  else
		      		    	  {
		      		    		  ContactBanTimer timer=new ContactBanTimer(time*60*1000,1000,con);
		      			    	  timer.setNumber_attached_with_timer(list_of_item.get(Integer.parseInt(current_contact_tag)).contact_number);
		      			    	  timer.setName_attached_with_timer(list_of_item.get(Integer.parseInt(current_contact_tag)).contact_name);
		      			    	  timer.start();
		      			    	  application.map_number_timerobj.put(list_of_item.get(Integer.parseInt(current_contact_tag)).contact_number, timer);
		      			    	  dialog.dismiss();
		      		    	  }
		      		    	  
		                    }
		                });
		                alertReady = true;
		            }
		        }
		    });
		   alert_dialog.show();

		}
		
	}
	
	public void getColumnValues(String number_attached)
	{
		Log.i(TAG, "getColumnValues()");
		try
		{
			String Message="";
	        obj.openToWrite();
	        
	        
	        //also show alerts and clear fields for that number
	        
	    	Cursor curser_for_filled_row=obj.getRowForMatchedContact(number_attached);
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
	        
			
			if(Message!=null && (!Message.equals("")))
			{
				   AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);
				    alertDialog.setTitle("Text Messgaes From "+number_attached);
				    alertDialog.setMessage(Message);
				    
				   
				    alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

				      public void onClick(DialogInterface dialog, int id) {

				        dialog.dismiss();

				    }});
				    
				   AlertDialog dialog= alertDialog.create();
				   dialog.setCanceledOnTouchOutside(true);
				   dialog.show();
			        

			}
			else
			{
				Log.i(TAG, "MESSAGES ARE NULL");
			}
					
			Log.i(TAG, "Reset Counter for number "+number_attached);
	       int x= obj.update_count(0, number_attached);
	       obj.clear_Msg_Columns(number_attached);
	       Log.i(TAG, "number of rows updated with given counter is "+x);
	        
	       obj.CloseDatabase();
	       
	       
		}
		catch(Exception e)
		{
			Log.i(TAG, "WE HAVE EXCEPTION HERE ()()()()()()()");
			e.printStackTrace();
		}
		 
        
	}
	

}
