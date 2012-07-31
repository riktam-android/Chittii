package com.riktamtech.android.chitti;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.riktamtech.android.chitti.R;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Message_handling extends ListActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_handling);
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		
		// Extracting the Tag information from the intent...
		
		Bundle extras = getIntent().getExtras();
		
		String tag = extras.getString("tag");
		
		  final Uri sms = Uri.parse("content://sms/inbox");
	        final ContentResolver cr = this.getContentResolver();
	        
	        if(tag.equals("unread"))
	        {
	        	ArrayList<String> listItems = new ArrayList<String>();
				Cursor cursor = cr.query(sms, null, null, null, null);
				cursor.moveToFirst();
				String result = "";
				while(!cursor.isAfterLast())
				{
					String id = cursor.getString(0);
					String address = cursor.getString(cursor.getColumnIndex("address"));
					String read = cursor.getString(cursor.getColumnIndex("read"));
					String body = cursor.getString(cursor.getColumnIndex("body"));
					
					if(read.equalsIgnoreCase("0"))
					{
						result = "From:"+address+"\n"+body;
						listItems.add(result);
					}
					cursor.moveToNext();
				}
				cursor.close();
		        setListAdapter(new ArrayAdapter<String>(Message_handling.this,android.R.layout.simple_list_item_1,listItems ));
		        
		        if(listItems.size()>0)
		        {
		        	tv1.setText("Displaying all the Unread Messages...");
		        	
		        }
		        
		        else
		        {
		        	tv1.setText("There seems to be no Unread Messages present in your inbox");
		        }
		        
	        }// end of Unread messages reading and displaying part...
	        
	        else if(tag.equals("search"))
	        {
	        	String result;
	        	String query = extras.getString("query");
	        	
	        	ArrayList<String> listItems = new ArrayList<String>();
				
				Cursor cursor = cr.query(sms, null, null, null, null);
		        cursor.moveToFirst();
		        while(!cursor.isAfterLast())
		        {
		        	String id = cursor.getString(0);
		        	String address = cursor.getString(cursor.getColumnIndex("address"));
		        	String body = cursor.getString(cursor.getColumnIndex("body"));
		        	
		        	if(body.toLowerCase().contains(query.toLowerCase()))
		        	{
		        		result =  "From:"+address+"\n"+body+"\n";
		        		listItems.add(result);		        		
		        	}
		        	cursor.moveToNext();
		        }
		        cursor.close();
		        setListAdapter(new ArrayAdapter<String>(Message_handling.this,android.R.layout.simple_list_item_1,listItems ));
		        if(listItems.size() > 0)
		        {
		        	tv1.setText("Displaying all the messages which matched :\""+query+"\"");
		        }
		        else
		        {
		        	tv1.setText("Seems there are no messages which have \""+query+"\" in their content");
		        	
		        }
	        }
	     
	        else if (tag.equals("before_time"))
	        {
	        	String result;
				ArrayList<String> listItems = new ArrayList<String>();
				Cursor cursor = cr.query(sms, null, null, null, null);
				cursor.moveToFirst();
				double time_bound = extras.getDouble("time_bound");
				String reqd = extras.getString("reqd");
				while(!cursor.isAfterLast())
				{
					String id = cursor.getString(0);
					String address = cursor.getString(cursor.getColumnIndex("address"));
					String read = cursor.getString(cursor.getColumnIndex("read"));
					String body = cursor.getString(cursor.getColumnIndex("body"));
					long time = cursor.getLong(cursor.getColumnIndex("date"));
			
//					Calendar calendar_bound = Calendar.getInstance();
//					Calendar calendar_now = Calendar.getInstance();
//					long now = System.currentTimeMillis();
//					calendar_now.setTimeInMillis(now);
//					calendar_now.get(Calendar.DAY_OF_MONTH);
//					calendar_bound.set(calendar_now.get(Calendar.YEAR), calendar_now.get(Calendar.MONTH), calendar_now.get(Calendar.DAY_OF_MONTH)-6, calendar_now.get(Calendar.HOUR_OF_DAY), calendar_now.get(Calendar.MINUTE), calendar_now.get(Calendar.SECOND));
//					long bounded_time = calendar_bound.getTimeInMillis();
					
					double now = System.currentTimeMillis();
					
					double bounded_time = now - time_bound ;
					
					Calendar calendar_message = Calendar.getInstance();
					calendar_message.setTimeInMillis(time);
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
					
					if(time >= bounded_time)
					{
					result = "From:"+address+"\n"+body+"\n"+"Time of Message:\n"+formatter.format(calendar_message.getTime());
					listItems.add(result);
					}
					cursor.moveToNext();
				}
				 setListAdapter(new ArrayAdapter<String>(Message_handling.this,android.R.layout.simple_list_item_1,listItems ));
				
				 if(listItems.size() > 0)
				 {
					 tv1.setText("Displaying all the messages which have been received before: "+reqd);
					 
				 }
				 else
				 {
					 tv1.setText("Well, there aren't any messages which have been received before: "+reqd);
				 }
				cursor.close();
			}
	        
	        Button b1 = (Button) findViewById(R.id.button1);
	        b1.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					finish();
				}
			});
	}
}
