package com.riktamtech.android.chitti;

import java.io.File;
import java.util.ArrayList;

import com.riktamtech.android.chitti.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Search_files extends ListActivity{
	
	File exc;
	String[] pr0 = new String[500];
	String[] pr1 = new String[60];
	String[] pr2 = new String[20];
	int pr0_count=0;int pr1_count=0;int pr2_count=0;
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.search_files);
		
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		
		Bundle extras = getIntent().getExtras();
		String song;
		
		song = extras.getString("file_name");
	
		// Code start here...
		if(song.length() >= 3)
		{
		String path="";
		pr0_count =0;pr1_count=0;pr2_count=0;
		
		File root = Environment.getExternalStorageDirectory();
		exc = new File(Environment.getExternalStorageDirectory()+"/.android_secure");
		
		ArrayList<String> listItems = new ArrayList<String>();
		int count = 0;
		
		visitfiles(root, song);
		
		for(int i=0;i<pr2_count;i++)
		{
			listItems.add(pr2[i]);
			count++;
		}
		for(int i=0;i<pr1_count;i++)
		{
			listItems.add(pr1[i]);
			count++;
		}
		for(int i=0;i<pr0_count; i++)
		{
			listItems.add(pr0[i]);
			count++;
		} 

        setListAdapter(new ArrayAdapter<String>(Search_files.this,android.R.layout.simple_list_item_1,listItems ));
        lv = getListView();
		lv.setTextFilterEnabled(true);
		
		if(listItems.size() > 0)
		{
			tv1.setText("Here are some of the files which matched \""+song+"\"\nYou can open them by just tapping on the required one...!");
			
		}
		else
		{
			tv1.setText("Sorry! Seems there are no files which matched \""+song+"\"");
		}
		
		lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
              // When clicked, show a toast with the TextView text
              Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                  Toast.LENGTH_SHORT).show();
              try
              {
              Intent intent = new Intent();
              intent.setAction(android.content.Intent.ACTION_VIEW);
              File file = new File(((TextView) view).getText().toString());
              String extension =  android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
//              Log.d("file extension", extension);
              String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//              Log.d("file mimetype", mimetype);
              if(mimetype.contains("audio"))
              {
            	  mimetype = "audio/*";
              }
              intent.setDataAndType(Uri.fromFile(file), mimetype);
              startActivity(intent);
              }
             catch (Exception e) {
				// TODO: handle exception
            	 
            	 e.printStackTrace();
             }
              
            }
          });		
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(Search_files.this);
			builder.setMessage("Please enter a file name of atleast 3 characters..");
			builder.setCancelable(false);
			
			builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		        	   
		           }
		       });
			
			
			 AlertDialog alert = builder.create();
			 alert.show();
			
		}
		
		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
	 
    public void visitfiles(File dir, CharSequence song)
    {
    	
		
    	if(dir.isDirectory() && !dir.equals(exc))
		{
			
			String[] children = dir.list();
			
			for(int i=0; i<children.length ; i++)
			{
				visitfiles(new File(dir, children[i]), song);
			}
			
		}
		else
		{
		
			//Process your file...that is nothing but the variable 'dir' itself
			//i.e., process(dir)
			
					String path = dir.getAbsolutePath();
					
					if(dir.getName().toLowerCase().contains(song))
					{
						//Need to implement priority based search....
					
						int priority;
						String name = dir.getName().toLowerCase();
						String song1 = song.toString().toLowerCase();
						if(name.equals(song1))
						{
							priority = 2;
							pr2[pr2_count] = dir.getAbsolutePath();
							pr2_count++;
						}
						else 
						{
							String song2 = " "+song1+" ";
						
						
							if(name.contains(song2) || (name.startsWith(song1) && name.contains(song1+" ")) || (name.endsWith(song1) && name.contains(" "+song1)))
							{
								priority = 1;
								pr1[pr1_count] = dir.getAbsolutePath();
								pr1_count++;
							}
							else
							{
								priority = 0;
								pr0[pr0_count] = dir.getAbsolutePath();
								pr0_count++;
							}
						
						}
					
//					Log.d("required songs",path+" Priority:"+Integer.toString(priority));
					
					}
				
		
		}
    }
}
