package com.example.sms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DOA 
{
	private String TAG="DOA";
	public static final String DATABASE_NAME = "mydatabase";
	public static final String TABLE_NAME = "contacts";
	public static final int DATABASE_VERSION = 2;
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_DOWNCOUNT = "count";
	public static final String COLUMN_MSG1 = "msg1";
	public static final String COLUMN_MSG2 = "msg2";
	public static final String COLUMN_MSG3 = "msg3";
	public static final String COLUMN_LAST_UPDATED_MSG_COL= "lastupdated";
	public static final String COL_HAS_PENDING_MSG="has_pending";
	public static final String QUERY_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME + " text, " + COLUMN_NUMBER + " text, " + COLUMN_DOWNCOUNT + " integer DEFAULT 0, "+ COLUMN_MSG1 + " text, "+ COLUMN_MSG2 + " text, "+ COLUMN_MSG3 + " text, "+ COLUMN_LAST_UPDATED_MSG_COL + " text, "+ COL_HAS_PENDING_MSG + " integer DEFAULT 0)";
	
	SQLiteHelper helper = null;
	SQLiteDatabase sqlDatabase = null;
	Context c;
	
	public DOA(Context c) 
	{
		super();
		this.c = c;
		helper = new SQLiteHelper(c, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	public void openToRead()
	{
		sqlDatabase = helper.getReadableDatabase();
	}
	
	public void openToWrite()
	{
		sqlDatabase = helper.getWritableDatabase();
	}
	
	public void CloseDatabase()
	{
		if(sqlDatabase.isOpen())
		sqlDatabase.close();
	}
	
	public long insert(String name,String number)
	{
		ContentValues value = new ContentValues();
		
		value.put(COLUMN_NAME, name);
		value.put(COLUMN_NUMBER, number);
		return sqlDatabase.insert(TABLE_NAME, null, value);
			
	}
	
	//fill all other fields other than columns 
	public void insert(String name,String number,int count,String data,String column_name)
	{
		ContentValues value = new ContentValues();
		
		value.put(COLUMN_NAME, name);
		value.put(COLUMN_NUMBER, number);
		value.put(COLUMN_DOWNCOUNT, count);
		value.put(column_name, data);
		sqlDatabase.insert(TABLE_NAME, null, value);
			
	}
	
	public Cursor selcet()
	{
		
		String[] columns = {COLUMN_ID,COLUMN_NAME,COLUMN_NUMBER,COLUMN_DOWNCOUNT,COLUMN_MSG1,COLUMN_MSG2,COLUMN_MSG3};
		Cursor cursor = sqlDatabase.query(TABLE_NAME, columns, null, null, null, null, null);
		return cursor;
	}
	
	public boolean isContactPresent(String number)
	{
		boolean present =false;
		
		Cursor c_temp=sqlDatabase.query(TABLE_NAME, new String[]{COLUMN_NUMBER}, COLUMN_NUMBER+"=?", new String[]{number}, null, null, null);
		c_temp.moveToFirst();
		int count=c_temp.getCount();
		//Log.i(TAG, "number of contacts with "+number+" is "+count);
		if(count==1)
		{
			present=true;
		}
		else
		{
			present=false;
		}
		c_temp.close();
		return present;
	}
	
	
	public int update_count(int count,String number)
	{
		int rows_updated=0;
		String where_args[]={number};
		ContentValues value = new ContentValues();
		value.put(COLUMN_DOWNCOUNT, count);
		rows_updated=sqlDatabase.update(TABLE_NAME, value, COLUMN_NUMBER+"=?",where_args);
		return rows_updated;
	}
	
	
	//if pending message is 0 than it means there is no pending message
	//if pending message is 1 that it means there is some pending message
	public int update_pending_msg_value(int count,String number)
	{
		int rows_updated=0;
		String where_args[]={number};
		ContentValues value = new ContentValues();
		value.put(COL_HAS_PENDING_MSG, count);
		rows_updated=sqlDatabase.update(TABLE_NAME, value, COLUMN_NUMBER+"=?",where_args);
		return rows_updated;
	}
	
	public int store_Message(String data,String number,String column_name,int updat_col)
	{
		int rows_updated=0;
		String where_args[]={number};
		ContentValues value = new ContentValues();
		value.put(column_name, data);
		value.put(COLUMN_LAST_UPDATED_MSG_COL, updat_col);
		rows_updated=sqlDatabase.update(TABLE_NAME, value, COLUMN_NUMBER+"=?",where_args);
		return rows_updated;
	}
	
	public int clear_Msg_Columns(String number)
	{
		int rows_updated=0;
		String where_args[]={number};
		ContentValues value = new ContentValues();
		value.putNull(COLUMN_MSG1);
		value.putNull(COLUMN_MSG2);
		value.putNull(COLUMN_MSG3);
		value.put(COLUMN_LAST_UPDATED_MSG_COL, 0);
		rows_updated=sqlDatabase.update(TABLE_NAME, value, COLUMN_NUMBER+"=?",where_args);
		return rows_updated;
	}
	
	public void deleteAllRows()
	{
		String delete_query="Delete From "+TABLE_NAME;
		sqlDatabase.rawQuery(delete_query, null);
	}
	
	
	public boolean isNumberPresent(String number)
	{
		String delete_query="select * From "+TABLE_NAME+" where "+COLUMN_NUMBER+" = "+"'"+number+"'";
		boolean present =false;
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		c_temp.moveToFirst();
		int count=c_temp.getCount();
		//Log.i(TAG, "number of contacts with "+number+" is "+count);

		if(count>=1)
		{
			present=true;
		}
		c_temp.close();
		return present;
	}
	
	public int getDownCountValue(String number)
	{
		String delete_query="select * From "+TABLE_NAME+" where "+COLUMN_NUMBER+" = "+"'"+number+"'";
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		c_temp.moveToFirst();
		int value_of_downcount=0;
		if(c_temp.getString(3)==null)
		{
			value_of_downcount=0;
		}
		else
		{
			value_of_downcount=Integer.parseInt(c_temp.getString(3));

		}
		c_temp.close();
		return value_of_downcount;
	}
	
	public int getPnedingCountValue(String number)
	{
		String delete_query="select * From "+TABLE_NAME+" where "+COLUMN_NUMBER+" = "+"'"+number+"'";
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		int value_of_pendingcount=0;

		if(c_temp!=null)
		{
			if(c_temp.getCount()>0)
			{
				c_temp.moveToFirst();
				if(c_temp.getString(8)==null)
				{
					value_of_pendingcount=0;
				}
				else
				{
					value_of_pendingcount=Integer.parseInt(c_temp.getString(8));

				}
			}
			else
			{
				Log.i(TAG, "c_temp.count is 0");
			}
		}
		else
		{
			Log.i(TAG, "C_temp is null");
		}
		
		c_temp.close();
		return value_of_pendingcount;
	}

	
	public Cursor getRowForMatchedContact(String number)
	{
		String delete_query="select * From "+TABLE_NAME+" where "+COLUMN_NUMBER+" = "+"'"+number+"'";
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		return c_temp;
	}
	
	public Cursor getRowsWhereContactIsBaned()
	{
		String delete_query="select * From "+TABLE_NAME+" where "+COLUMN_DOWNCOUNT+" < -5 ";
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		return c_temp;

	}
	
	public Cursor getRowsWhereContactIsFavourite()
	{
		String delete_query="select * From "+TABLE_NAME+" where "+COLUMN_DOWNCOUNT+" > 5 ";
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		return c_temp;

	}
	
	public Cursor getRows()
	{
		String delete_query="select * From "+TABLE_NAME;
		Cursor c_temp=sqlDatabase.rawQuery(delete_query, null);
		c_temp.moveToFirst();
		return c_temp;

	}
	
	
}
