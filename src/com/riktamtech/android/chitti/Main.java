package com.riktamtech.android.chitti;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.riktamtech.android.chitti.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.LocalSocketAddress.Namespace;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity implements OnInitListener {
	
	private TextToSpeech myTTS;
	//status check code
	private int MY_DATA_CHECK_CODE = 0;
	
	public static final String EXAMPLE_TEST = "This is my small example "
			+ "string which I'm going to use for pattern matching.";
	

	
	public String input ;
	public String response;
	public boolean voice_wait = false;
	
	//public String[] key_words = new String[]{"call", "search_file", "search_google", "play_song", "open_file", "location"};
	
	public String[] key_words = new String[]{"call","message","search","weather","play","define","elevation","location","find","where","sms","calculate"};
	
	public String[] s_type = new String[]{"act_asr","act_what","act_can","act_yn","cas_asr","cas_what","cas_can","cas_yn"};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        voice_wait = false;
       
        final TextView tv1= (TextView) findViewById(R.id.textView1);
        final TextView tv2 = (TextView) findViewById(R.id.textView2);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        
        // setting the Welcome statements...
        
	        	
	        tv1.setText("Hi there ! How can I help you today ?");
	        int min = 0;
			int max= Text_corpus.welcome_responses.length - 1;
			Random rand = new Random();
			 int randomNum = rand.nextInt(max - min +1)+min;
			response = Text_corpus.welcome_responses[randomNum];
			tv1.setText(response);
			//speakWords(response);
        
        tv2.setText("");
        
        final EditText et1 = (EditText) findViewById(R.id.editText1);
        
        final ProgressDialog pd = new ProgressDialog(Main.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Please Wait..");
        pd.setIndeterminate(false);
        pd.setCancelable(true);
    
        final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        System.out.println(EXAMPLE_TEST.matches("\\w.*"));
//		String[] splitString = (EXAMPLE_TEST.split("\\s+"));
//		System.out.println(splitString.length);// Should be 14
//		for (String string : splitString) {
//			System.out.println(string);
//		}
//		// Replace all whitespace with tabs
//		System.out.println(EXAMPLE_TEST.replaceAll("\\s+", "\t"));
//        
        Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
								
				input = et1.getText().toString();							
				tv2.setText("");
				tv1.setText(input);
				
				String stat_type = "";   // This denotes what kind of a statement the input is...
				
				String key_word = "";
			
		        
	// start here the code for the choosing among the keywords....
				boolean flag = false;
				
				for(int i=0; i< key_words.length ; i++)
				{
					//String[] split = key_words[i].split("_");
					//Pattern pat_keyword = Pattern.compile(".*"+split[0]+".*"+"split[1]");
//					if(input.matches(".*"+split[0]+".*"+split[1]+".*"))
//					{
//						key_word = key_words[i];
//						break;
//					}
					
					if(input.toLowerCase().matches(".*"+key_words[i]+".*"))
					{
						key_word = key_words[i];
						stat_type = "act_";
						flag = true;
						break;
					}
					
				}
			
				if (!flag) {
					
					stat_type = "cas_";
					
				}
				
	// Then, you have to choose among the type of statements the given input is....
	//	Then, use it in your later part of response....
	// Like different kinds of response statements for different kinds of S_types...
				//So, proceeding with statement classification..
				
				if (input.toLowerCase().startsWith("what")) {
					
					stat_type = stat_type + "what";
				}
				
				else if(input.toLowerCase().startsWith("can"))
				{
					stat_type = stat_type + "can";
				}
				else if(input.toLowerCase().startsWith("[iI]s.*|[wW]as.*|will.*|are .*|were .*"))
				{
					stat_type = stat_type + "yn";
				}
				else if(input.toLowerCase().startsWith("how"))
				{
					stat_type = stat_type +"how";
				}
				else
				{
					stat_type = stat_type + "asr";
				}
	// Obviously, if your input doesn't contain any of the keywords,obviously, it should be a Casual Statement..
				
			//	Log.d("stat_type", stat_type);
			//	Log.d("key_word", key_word);
				
				
	// Now, we need to process the code for each of the keywords....
				boolean networkstate = conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting() || conman.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
				
				if(!networkstate)
				{
					response = "Sorry ! Seems there are issues with your Network Connectivity.... Please try again after connecting to the Network!";
					tv2.setText(response);
				}
				else
				{
				if(stat_type.contains("act"))
				{
					// here starts the code for just "Call" functionality ....		
					
					if(key_word.equals("call"))
					{
						Pattern pattern = Pattern.compile("\\d+([,\\s])?\\d+");
				        Matcher matcher = pattern.matcher(input);
				        String reqd = "";
				        int matcher_count = 0;
				        while(matcher.find())
				        {
//				        	System.out.println("Start index:"+matcher.start());
//				        	System.out.println("End index:"+matcher.end());
//				        	System.out.println(matcher.group().toString());
				        	
//				        	Log.d("start index:", Integer.toString(matcher.start()));
//				        	Log.d("End index:", Integer.toString(matcher.end()));
				        	
//				        	Log.d("number", matcher.group().toString());
				        			        	
				        	reqd = matcher.group().toString();
				        	
				        	matcher_count++ ;
				        	
				        
				        }
				        
				       // Toast.makeText(Main.this, Integer.toString(matcher_count), Toast.LENGTH_SHORT).show();
				        if(matcher_count == 0)
				        {
				        	// Code here for no number found.....
				        	response = "Please enter the number to be called";
				        }
				        else
				        {
				        	// initiate the call here....
				        	
				        	String number = reqd;
				        	response = "Sure..I'm calling to "+number+".....";
				        	
//				        	System.out.println("The required number is:"+number);
				        	call_function(number);
				        	
				        }
				        
				        tv2.setText(response);
					} // end of Call keyword
					
	// Code for the Search Action starts here....				
					else if(key_word.equals("search"))
					{
						// Now, as further diff kinds of sub actions are possible
						// in search, so divide them as such !
						
						// Search for the files.....
						if(input.toLowerCase().matches(".*file.*|.*sd card*.|.*pdf.*|.*mp3.*|.*doc.*|.*apk.*"))
						{
							// Got to proceed with searching of files here...
							String filename = "";
							String input_copy = input;
							if(input_copy.toLowerCase().matches(".*pdf.*"))
							{
								filename = "pdf";
							}
							else if(input_copy.toLowerCase().matches(".*mp3.*"))
							{
								filename = "mp3";
							}
							else if(input_copy.toLowerCase().matches(".*doc.*"))
							{
								filename = "doc";
							}
							else if(input_copy.toLowerCase().matches(".*apk.*"))
							{
										filename = "apk";						
							}
							else
							{
								String split[] = input.split(" ");
								filename = split[split.length - 1];
//								Log.d("filename", filename);
							}
							
						
							
							if(stat_type.equals("act_asr"))
							{
								 response = "All right! Searching for the required file...";
							}
							else if(stat_type.equals("act_can"))
							{
								response = "Yeah sure..! Searching for the required file...";
							}
							else if(stat_type.equals("act_what"))
							{
								response = "Searching the files you wanted...";
							}
//							Log.v("response", response);
							tv2.setText(response);
							
							Intent intent = new Intent(Main.this, Search_files.class);
							intent.putExtra("file_name", filename);
							Main.this.startActivity(intent);
							
						
						}
					
						else if(input.toLowerCase().matches(".*dictionary.*|.*meaning.*"))
						{
							// Proceed with the finding of the meaning of the words here....
							// Remove the phrases... Search and dictionary and meaning...
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceAll("dictionary", "");
							query = query.replaceAll("meaning", "");
							
							if(stat_type.equals("act_asr"))
							{
								response = "All right ! Searching in google for the required meaning";
							}
							else if(stat_type.equals("act_what"))
							{
								query = query.replaceAll("what", "").trim();
								query = query.replaceAll("is", "").trim();
								query = query.replaceAll("of ", "").trim();
								
								response = "Searching for the meaning you wanted...";
							}
							else if(stat_type.equals("act_can"))
							{
								response = "Yeah sure ! Enquiring google about the required meaning";
							}
							
							Uri uri = Uri.parse("http://www.google.co.in/search?q="+input_copy);
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
							
						}
						else if(input.toLowerCase().matches(".*google.*"))
						{
							// search on Google here ... mind it.. Only the remaining part of the 
							// sentence needs to be searched here....
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceFirst("search", "").trim();
							 query = query.replaceAll("google", "");
							 query = query.replaceFirst("you", "").trim();
							 
							 if(stat_type.equals("act_can"))
								{
									query = query.replaceFirst("can", "").trim();
								}
								if(stat_type.equals("act_what"))
								{
									query = query.replaceFirst("what", "").trim();
								}
							 if (query.startsWith("for")) {
								
								 query = query.replaceFirst("for", "").trim();
							 }
							 else if (query.startsWith("about")) {
								query = query.replaceFirst("about", "").trim();
							}
							 else if(query.startsWith("on"))
							 {
								 query = query.replaceFirst("on", "").trim();
							 }
							 
							Uri uri = Uri.parse("http://www.google.com/search?q="+query);
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
													
							response = "Ok! Searching in google for the given query..";
							tv2.setText(response);
						}
						else
						{
							// Search on google.....The query: input - "search" : That's all!
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceFirst("search", "").trim();
							query = query.replaceAll("you", "").trim();
							if(stat_type.equals("act_can"))
							{
								query = query.replaceFirst("can", "").trim();
							}
							if(stat_type.equals("act_what"))
							{
								query = query.replaceFirst("what", "").trim();
							}
							if (query.startsWith("for")) {
								
								 query = query.replaceFirst("for", "").trim();
							}
							else if (query.startsWith("about")) {
								query = query.replaceFirst("about", "").trim();
							}
							
							Uri uri = Uri.parse("http://www.google.com/search?q="+query);
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
							
							if(stat_type.equals("act_asr"))
							{
								response = "Fine! Let's see what Google says about this...";
							}
							
							else if(stat_type.equals("act_what"))
							{
								response = "OK ! Let's see what google says about this...";
							}
							
							else if(stat_type.equals("act_can"))
							{
								response = "Sure .. Let's enquire google about this..";
							}
							
							tv2.setText(response);
						}
						
						
					} // end of search key word
					
					else if(key_word.equals("define"))
					{
						voice_wait = true;
						final String input_copy = input;
						
						new AsyncTask<String, Void, String>()
						{
							protected void onPreExecute() {
								pd.show();
							};
							
							@Override
							protected String doInBackground(String... arg0) {
								// TODO Auto-generated method stub
								String input_copy_1 = arg0[0];
								String result;
								String query = input_copy_1.toLowerCase().replaceAll("define", "").trim();
								query = query.replaceAll("[:]", "").trim();
								query = query.replaceAll("[?]", "").trim();
								query = query.replaceFirst("the word", "").trim();
								query = query.replaceFirst("the phrase", "").trim();
								
								// First , search in wiki ... if found... fine!
								// if not, exception will be caught....
								// if perfect article doesn't come: Then, give a link of google over there...
								try
								{

									query = query.replaceAll(" ", "%20");
									URL website = new URL("http://en.wikipedia.org/wiki/"+query);
						            URLConnection connection = website.openConnection();
						            
						            BufferedReader in = new BufferedReader(
						                                    new InputStreamReader(
						                                        connection.getInputStream()));

						            StringBuilder strbuilder = new StringBuilder();
						            String inputLine;

						            while ((inputLine = in.readLine()) != null) 
						            {
						                strbuilder.append(inputLine);
						                if(inputLine.contains("</p>"))
						                {
						                	break;
						                }
						            }	
						            
						            in.close();
						            String mystring = strbuilder.toString();
//						            Log.d("mystring", mystring);
						            in.close();
//						            System.out.println(mystring);
								//String mystring = XMLfunctions.getXML("http://en.wikipedia.org/wiki/"+query);
					        	
					        	//Document doc = null;
					        	//doc = XMLfunctions.XMLfromString(mystring);
					        	
					        	Pattern pattern = Pattern.compile("<p>.*</p>");
					        	Matcher matcher = pattern.matcher(mystring);
					        	
					        	String reqd = "";
					        	
					        	while(matcher.find())
					        	{
					        		reqd = matcher.group().toString();
					        		break;
					        	}
					        	
					        	//NodeList n1 = doc.getElementsByTagName("p");
					        	
					        	
					        	//Log.d("no of nodes of type <p>", Integer.toString(n1.getLength()));
					            //Log.d("value of <p> tag", reqd);        
					            
					            Pattern pattern_start_tags = Pattern.compile("<");
					            Matcher matcher_start_tags = pattern_start_tags.matcher(reqd);
					            
					            Pattern pattern_end_tags = Pattern.compile(">");
					            Matcher matcher_end_tags = pattern_end_tags.matcher(reqd);
					            String remaining = "";
					            int start_len = matcher_start_tags.groupCount() ;
					            int end_len = matcher_end_tags.groupCount();
//					            Log.d("start_len", Integer.toString(start_len));
//					            Log.d("end_len", Integer.toString(end_len));
					            ArrayList<Integer> list_b = new ArrayList<Integer>();
					            ArrayList<Integer> list_e = new ArrayList<Integer>();
					           
					           
					            list_b.add(0);
					            	while(matcher_start_tags.find() && matcher_end_tags.find())
					            	{
					            		
					            		list_e.add(matcher_start_tags.start()-1);
					            		list_b.add(matcher_end_tags.end());
//					            		Log.d("<_matched", Integer.toString(matcher_start_tags.start()-1));
//					            		Log.d(">_matched", Integer.toString(matcher_end_tags.end()));
					            						            		           	
					            	}
					            	list_e.add(reqd.length()-1);
					            	
//					            	Log.d("list_start_len", Integer.toString(list_b.size()));
//					            	Log.d("list_end_len", Integer.toString(list_e.size()));
					            	
					            	 String reqd_string = "";
					            	for(int i=0; i<list_b.size();i++)
					            	{
//					            		System.out.println(list_b.get(i));
//					            		System.out.println(list_e.get(i));
					            		
					            		int start_index = list_b.get(i);
					            		int end_index = list_e.get(i);
					            		
					            		if(start_index <= end_index)
					            		{
					            			reqd_string += reqd.substring(start_index, end_index+1);
					            		}
					            		           		
					            	}
					            	
//					            	System.out.println(reqd_string);
					            	result = "Have a look at this:\n";
					            	result += reqd_string;
					            	result = result + "\nSource: Wikipedia\n";
					            	result += "Click here for more info:";
					            			        
								}
								catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();

						        	Uri uri = Uri.parse("http://www.google.com/search?q=define%20"+query);
									Intent intent = new Intent(Intent.ACTION_VIEW,uri);
									startActivity(intent);
						        	result = "Ok ! Let me try to google it ..";
								}
								
								
								
								return result;
							}
							protected void onPostExecute(String result) {
								pd.dismiss();
								response = result;
								// Do the link processing here...
								tv2.setText(response);
								String query = input_copy.toLowerCase().replaceAll("define", "").trim();
								query = query.replaceAll("[:]", "").trim();
								query = query.replaceAll("[?]", "").trim();
								query = query.replaceFirst("the word", "").trim();
								query = query.replaceFirst("the phrase", "").trim();
								query = query.replaceAll(" ", "%20");
								Pattern pattern = Pattern.compile("Click here for more info:");
								String wikiViewURL =  "http://en.wikipedia.org/wiki/"+query;
								   Linkify.addLinks(tv2, pattern, wikiViewURL,new Linkify.MatchFilter() {
									
									public boolean acceptMatch(CharSequence s, int start, int end) {
										// TODO Auto-generated method stub
										return true;
									}
								},new Linkify.TransformFilter() {
									
									public String transformUrl(Matcher match, String url) {
										// TODO Auto-generated method stub
										return "";
									}
								});
								   speakWords(response);
							};
							
						}.execute(input_copy);
																	
					}
					
					else if(key_word.equals("find"))
					{

						// Now, as further diff kinds of sub actions are possible
						// in search, so divide them as such !
						
						// Find for the files.....
						if(input.toLowerCase().matches(".*file.*|.*sd card*.|.*pdf.*|.*mp3.*|.*doc.*|.*apk.*"))
						{
							// Got to proceed with searching of files here...
							String filename = "";
							String input_copy = input;
							if(input_copy.toLowerCase().matches(".*pdf.*"))
							{
								filename = "pdf";
							}
							else if(input_copy.toLowerCase().matches(".*mp3.*"))
							{
								filename = "mp3";
							}
							else if(input_copy.toLowerCase().matches(".*doc.*"))
							{
								filename = "doc";
							}
							else if(input_copy.toLowerCase().matches(".*apk.*"))
							{
										filename = "apk";						
							}
							else
							{
								String split[] = input.split(" ");
								filename = split[split.length - 1];
//								Log.d("filename", filename);
							}
							
						
							
							if(stat_type.equals("act_asr"))
							{
								 response = "All right! Finding the required files...";
							}
							else if(stat_type.equals("act_can"))
							{
								response = "Yeah sure..! Searching for the required files...";
							}
							else if(stat_type.equals("act_what"))
							{
								response = "Finding the files you wanted...";
							}
//							Log.v("response", response);
							tv2.setText(response);
							
							Intent intent = new Intent(Main.this, Search_files.class);
							intent.putExtra("file_name", filename);
							Main.this.startActivity(intent);
							
						
						}
					
						else if(input.toLowerCase().matches(".*dictionary.*|.*meaning.*"))
						{
							// Proceed with the finding of the meaning of the words here....
							// Remove the phrases... Search and dictionary and meaning...
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceAll("dictionary", "");
							query = query.replaceAll("meaning", "");
							
							if(stat_type.equals("act_asr"))
							{
								response = "All right ! Searching in google for the required meaning";
							}
							else if(stat_type.equals("act_what"))
							{
								query = query.replaceAll("what", "").trim();
								query = query.replaceAll("is", "").trim();
								query = query.replaceAll("of ", "").trim();
								
								response = "Finding the meaning you wanted...";
							}
							else if(stat_type.equals("act_can"))
							{
								response = "Yeah sure ! Enquiring google about the required meaning";
							}
							
							Uri uri = Uri.parse("http://www.google.co.in/search?q="+input_copy);
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
							
						}
						else if(input.toLowerCase().matches(".*google.*"))
						{
							// search on Google here ... mind it.. Only the remaining part of the 
							// sentence needs to be searched here....
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceFirst("search", "").trim();
							 query = query.replaceAll("google", "");
							 query = query.replaceFirst("you", "").trim();
							 
							 if(stat_type.equals("act_can"))
								{
									query = query.replaceFirst("can", "").trim();
								}
								if(stat_type.equals("act_what"))
								{
									query = query.replaceFirst("what", "").trim();
								}
							 if (query.startsWith("for")) {
								
								 query = query.replaceFirst("for", "").trim();
							 }
							 else if (query.startsWith("about")) {
								query = query.replaceFirst("about", "").trim();
							}
							 else if(query.startsWith("on"))
							 {
								 query = query.replaceFirst("on", "").trim();
							 }
							 
							Uri uri = Uri.parse("http://www.google.com/search?q="+query);
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
													
							response = "Ok! Enquiring google for the given query..";
							tv2.setText(response);
						}
						else
						{
							// Search on google.....The query: input - "search" : That's all!
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceFirst("find", "").trim();
							query = query.replaceAll("you", "").trim();
							if(stat_type.equals("act_can"))
							{
								query = query.replaceFirst("can", "").trim();
							}
							if(stat_type.equals("act_what"))
							{
								query = query.replaceFirst("what", "").trim();
							}
							if (query.startsWith("for")) {
								
								 query = query.replaceFirst("for", "").trim();
							}
							else if (query.startsWith("about")) {
								query = query.replaceFirst("about", "").trim();
							}
							
							Uri uri = Uri.parse("http://www.google.com/search?q="+query);
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
							
							if(stat_type.equals("act_asr"))
							{
								response = "Fine! Let's see what Google says about this...";
							}
							
							else if(stat_type.equals("act_what"))
							{
								response = "OK ! Let's see what google says about this...";
							}
							
							else if(stat_type.equals("act_can"))
							{
								response = "Sure .. Let's enquire google about this..";
							}
							
							tv2.setText(response);
						}
						
						
					
						
					} // end of find key word
					
					else if(key_word.equals("location"))
					{
						if(input.toLowerCase().contains("current"))
						{
							LocationListener locationListener = new MyLocationListener();
							LocationManager locationmanager = (LocationManager) Main.this.getSystemService(Context.LOCATION_SERVICE);
							locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
							 Location lastknownlocation = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							 
							double latitude= lastknownlocation.getLatitude();
							double longitude = lastknownlocation.getLongitude();
							
							response = "Your Current Location is:\n"
									+ "Latitude:"+latitude+"\n"+"Longitude:"+longitude+"\n\n";
							
							Geocoder geocoder = new Geocoder(Main.this, Locale.ENGLISH);
							try {
								List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
								if(addresses != null)
								{
									Address returned_address = addresses.get(0);

									StringBuilder str_returned_address = new StringBuilder("Address: \n");
									for (int i = 0; i < returned_address.getMaxAddressLineIndex(); i++) {
										
										str_returned_address.append(returned_address.getAddressLine(i)).append("\n");
									}
									response += str_returned_address.toString();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tv2.setText(response);
							
							Uri uri = Uri.parse("https://maps.google.com/maps?q="+latitude+"+"+longitude+"&t=m");
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
							locationmanager.removeUpdates(locationListener);
						}
						else 
						{
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceAll("location", "").trim();
							//  So, now , search on Google maps for the required location... 
									// which is nothing but the term 'query' here...
							
							response = "Displaying the required locaiton in Maps..";
							tv2.setText(response);
							
							if(!query.equals(""))
							{
								Uri uri = Uri.parse("https://maps.google.com/maps?q="+query+"&t=m");
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
							
						}
						
				
					} // end of location keyword
					
					else if(key_word.equals("where"))
					{
						String input_copy = input;
						if(input_copy.toLowerCase().matches(".*am i.*"))
						{
							LocationListener locationListener = new MyLocationListener();
							LocationManager locationmanager = (LocationManager) Main.this.getSystemService(Context.LOCATION_SERVICE);
							locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
							 Location lastknownlocation = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							 
							double latitude= lastknownlocation.getLatitude();
							double longitude = lastknownlocation.getLongitude();
							
							response = "Your Current Location is:\n"
									+ "Latitude:"+latitude+"\n"+"Longitude:"+longitude+"\n\n";
							
							Geocoder geocoder = new Geocoder(Main.this, Locale.ENGLISH);
							try {
								List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
								if(addresses != null)
								{
									Address returned_address = addresses.get(0);

									StringBuilder str_returned_address = new StringBuilder("Address: \n");
									for (int i = 0; i < returned_address.getMaxAddressLineIndex(); i++) {
										
										str_returned_address.append(returned_address.getAddressLine(i)).append("\n");
									}
									response += str_returned_address.toString();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tv2.setText(response);
							
							Uri uri = Uri.parse("https://maps.google.com/maps?q="+latitude+"+"+longitude+"&t=m");
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
							locationmanager.removeUpdates(locationListener);
						}
						
						else
						{
							String query = input_copy.replaceFirst(" is ", " ").trim();
							query = query.replaceFirst(" are ", " ").trim();
							query = query.replaceFirst("where", "");
							
							response = "Opening map for \""+query+"\"";
							tv2.setText(response);
							Uri uri = Uri.parse("https://maps.google.com/maps?q="+query);
							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
							startActivity(intent);
						
						}
						
					} // end of where keyword
					
					else if(key_word.equals("play"))
					{
						String input_copy = input;
						
						String query = input_copy.toLowerCase().replaceFirst("play", "");
						
						String[] split = query.split(" ");
						
						String media_file = split[split.length - 1];
						
						
						if(stat_type.equals("act_asr"))
						{
							 response = "All right! Searching for the required media...";
						}
						else if(stat_type.equals("act_can"))
						{
							response = "Yeah sure..! Searching for the required media...";
						}
						else if(stat_type.equals("act_what"))
						{
							response = "Searching the media you wanted...";
						}
//						Log.v("response", response);
						tv2.setText(response);
						
						Intent intent = new Intent(Main.this, Search_songs.class);
						intent.putExtra("media_name", media_file);
						Main.this.startActivity(intent);
						
						
					}// end of play keyword
					
					else if (key_word.equals("message") ) {
						String input_copy = input;
						if(input_copy.toLowerCase().contains("send"))
						{
							if(stat_type.equals("act_asr"))
							{
								 response = "All right! You can send the message now...";
							}
							else if(stat_type.equals("act_can"))
							{
								response = "Yeah sure..! I'll do the needful...";
							}
						
//							Log.v("response", response);
							tv2.setText(response);
							
							
							 Intent sendIntent = new Intent(Intent.ACTION_VIEW);
						     sendIntent.putExtra("sms_body", "");
						     sendIntent.setType("vnd.android-dir/mms-sms");
						     startActivity(sendIntent);
						}
						else if(input_copy.toLowerCase().contains("unread"))
						{
						
							Intent intent = new Intent(Main.this,Message_handling.class);
							intent.putExtra("tag", "unread");
							Main.this.startActivity(intent);
							response = "OK then, searching for the Unread messages...";
							tv2.setText(response);
						}

						else if (input_copy.toLowerCase().contains("before")) {
							
							Pattern pattern = Pattern.compile("before.*");
					        Matcher matcher = pattern.matcher(input_copy.toLowerCase());
					        String reqd = "";
					        int matcher_count = 0;
					        while(matcher.find())
					        {
//					        	System.out.println("Start index:"+matcher.start());
//					        	System.out.println("End index:"+matcher.end());
//					        	System.out.println(matcher.group().toString());
					        	
//					        	Log.d("start index:", Integer.toString(matcher.start()));
//					        	Log.d("End index:", Integer.toString(matcher.end()));
					        	
//					        	Log.d("before", matcher.group().toString());
					        			        	
					        	reqd = matcher.group().toString().trim();
					        	
					        	matcher_count++ ;
					        	
					        
					        }
					        
					       // Log.d("reqd", reqd);
					        reqd = reqd.toLowerCase().replaceFirst("before", "").trim();
//					        Log.d("reqd", reqd);
					        // Now, 'reqd' only contains days/hours/seconds/ or whatever along with a numeral associated with it!
					        // So , write the regular expression to track the numeral associated over there...
					        // Then, try to know whether it is a day, week or whatever...
					        // and then, execute the time estimate and the query for messages accordingly...
					        Pattern pattern1 = Pattern.compile("\\d+[.]?\\d*");
					        Matcher matcher1 = pattern1.matcher(reqd);
					        String numeral="";
					        double num = 0;
					        while(matcher1.find())
					        {	
//					        	System.out.println("Start index:"+matcher.start());
//					        	System.out.println("End index:"+matcher.end());
//					        	System.out.println(matcher.group().toString());
					        	
//					        	Log.d("start index:", Integer.toString(matcher1.start()));
//					        	Log.d("End index:", Integer.toString(matcher1.end()));
					        	
//					        	Log.d("number", matcher1.group().toString());
					        			        	
					        	 numeral = matcher1.group().toString().trim();
					        	  num =	Double.parseDouble(numeral);
//					        	  Log.d("numeral", numeral);
//					        	  Log.d("num", Double.toString(num));
					        }
					        
					       String unit = reqd.replaceAll(numeral, "");
//					       System.out.println(unit);
					        double time_bound = 0;
					        boolean f = true;
					        if(unit.contains("day"))
					        {
					        	 time_bound =  num * 86400000;
					        }
					        else if(unit.contains("hour"))
					        {
					        	 time_bound = num * 3600000;
					        }
					        else if(unit.contains("week"))
					        {
					        	 time_bound = num * 86400000 * 7;
					        }
					        else if(unit.contains("min"))
					        {
					        	 time_bound = num * 60000;
					        }
					        else if(unit.contains("month"))
					        {
					        	time_bound = num * 30 * 86400000 ;
					        }
					        else if(unit.contains("year"))
					        {
					        	time_bound = num * 365 * 86400000;
					        }
					        else
					        {
					        	 f = false;
					        	response = "Please enter a valid time parameter..!";
					        	response += "\nCan be in min,hours,days,weeks,etc.";
					        	tv2.setText(response);
					        }
					        if(f)
					        {
//					        	Log.d("time_bound", Double.toString(time_bound));
							    Intent intent = new Intent(Main.this,Message_handling.class);
							    intent.putExtra("tag", "before_time");
							    intent.putExtra("time_bound",time_bound);
							    intent.putExtra("reqd", reqd);
							    Main.this.startActivity(intent);
							        
							    response = "Querying for all the messages before "+reqd+"...";
							    tv2.setText(response);
					        }
					       
						}
						else if(input_copy.toLowerCase().contains("search")||input_copy.toLowerCase().contains("find"))
						{
							
							input_copy = input_copy.toLowerCase();
							input_copy = input_copy.replaceAll("search", "").trim();
							input_copy = input_copy.replaceAll("find", "").trim();
							String[] split = input_copy.split(" ");
							if(split.length > 0 )
							{
								String query = split[split.length-1];
								query = query.replaceAll("[\"]", "").trim();

								if(stat_type.equals("act_asr"))
								{
									 response = "All right! Searching for the required messages...";
								}
								else if(stat_type.equals("act_can"))
								{
									response = "Yeah sure..! Searching for the required messages...";
								}
								else if(stat_type.equals("act_what"))
								{
									response = "Searching the messages you wanted...";
								}
								Intent intent = new Intent(Main.this, Message_handling.class);
								intent.putExtra("tag", "search");
								intent.putExtra("query", query);
								Main.this.startActivity(intent);
								tv2.setText(response);
							}
							
							else
							{
								response = "Please enter the text to be searched for messages!";								
							}
									
						}
						
						else
						{
							response = "What do you want to do with messages?";
							tv2.setText(response);
						}
					}// end of messages keyword
					
					else if(key_word.equals("sms"))
					{
						if(stat_type.equals("act_asr"))
						{
							 response = "All right! You can send the message now...";
						}
						else if(stat_type.equals("act_can"))
						{
							response = "Yeah sure..! I'll do the needful...";
						}
					
//						Log.v("response", response);
						tv2.setText(response);
						
						
						 Intent sendIntent = new Intent(Intent.ACTION_VIEW);
					     sendIntent.putExtra("sms_body", "");
					     sendIntent.setType("vnd.android-dir/mms-sms");
					     startActivity(sendIntent);
					}
					
					else if (key_word.equals("weather")) {
						
						if(input.toLowerCase().contains("current"))
						{
							
							LocationListener locationListener = new MyLocationListener();
							LocationManager locationmanager = (LocationManager) Main.this.getSystemService(Context.LOCATION_SERVICE);
							locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
							 Location lastknownlocation = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							 
							double latitude= lastknownlocation.getLatitude();
							double longitude = lastknownlocation.getLongitude();
							
							response = "Your Current Location is:\n"
									+ "Latitude:"+latitude+"\n"+"Longitude:"+longitude;
							tv2.setText(response);
							
						
							locationmanager.removeUpdates(locationListener);
							
							double mod_lat  = latitude * 1000000;
							double mod_long = longitude * 1000000 ;
							
//							System.out.println(mod_lat);
//							System.out.println(mod_long);
							
							long rounded_lat = Math.round(mod_lat);
							long rouned_long = Math.round(mod_long);
							String str_lat = Long.toString(rounded_lat);
							String str_long = Long.toString(rouned_long);
							
//							System.out.println(str_lat);
//							System.out.println(str_long);
							
							 String mystring = null;
					        
					        mystring = XMLfunctions.getXML("http://www.google.com/ig/api?weather=,,,"+str_lat+","+str_long);
							// mystring = XMLfunctions.getXML("http://www.google.com/ig/api?weather=hyderabad");
						        
						        Document doc = null; 
						        doc = XMLfunctions.XMLfromString(mystring);		
						        
						        NodeList n1 = doc.getElementsByTagName("current_conditions");						        
//						        Log.d("no of elements with current conditions tag..", Integer.toString(n1.getLength()));
						        Element e = (Element) n1.item(0);		
						        
						        NodeList n_temp_c =  e.getElementsByTagName("temp_c");
//						        Log.d("no of elements with temp_c tag", Integer.toString(n_temp_c.getLength()));	
	 						       
							        response = "The weather in the current location is:\n";		
						        if(n_temp_c.getLength()>0)
						        {
						        	 NamedNodeMap named_node_map_temp_c = n_temp_c.item(0).getAttributes();						       
								        Node data_temp_c = named_node_map_temp_c.getNamedItem("data");						       
								        String current_temp = data_temp_c.getNodeValue();						       
//								        System.out.println(current_temp);
								        response += "Current Temperature : "+current_temp+"C \n";
						        }
						      				       
						        
						       
						        NodeList n_condition = e.getElementsByTagName("condition");
//						        Log.d("no of elements with condition tag", Integer.toString(n_condition.getLength()));
						        
						        if(n_condition.getLength()>0)
						        {
						        	 NamedNodeMap nnmap_condition = n_condition.item(0).getAttributes();
								        Node data_condition = nnmap_condition.getNamedItem("data");
								        String condition = data_condition.getNodeValue();
//								        System.out.println(condition);
								        response +=  "Current condition: "+condition+"\n";	
						        }
						       
						        
						        NodeList n_humidity = e.getElementsByTagName("humidity");
//						        Log.d("no of elements with humidity tag", Integer.toString(n_humidity.getLength()));
						        
						        if(n_humidity.getLength()>0)
						        {
						        	 NamedNodeMap nnmap_humidity = n_humidity.item(0).getAttributes();
								        Node data_humidity = nnmap_humidity.getNamedItem("data");
								        String humidity = data_humidity.getNodeValue();
//								        System.out.println(humidity);
								        response += "Current "+humidity+"\n";
						        }
						       
						        
						        NodeList n_wind = e.getElementsByTagName("wind_condition");
//						        Log.d("no of elements with wind_condition tag", Integer.toString(n_wind.getLength()));
						        
						        if(n_wind.getLength()>0)
						        {
						        	NamedNodeMap nnmap_wind	 = n_wind.item(0).getAttributes();
							        Node data_wind = nnmap_wind.getNamedItem("data");
							        String wind = data_wind.getNodeValue();
//							        System.out.println(wind);
							        response +=wind;
						        	
						        }
						        
						        response += "\nSource : Google";
						        tv2.setText(response);
						    		   
						       
							
						}
						else
						{
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceFirst("weather", "");
							
							if(stat_type.equals("act_what"))
							{
								query = query.replaceAll("what", "").trim();
								
							}
							else if(stat_type.equals("act_can"))
							{
								query = query.replaceAll("can", "").trim();
							}
							query = query.toLowerCase().replaceFirst("how ", "").trim();
							query = query.toLowerCase().replaceFirst("at", "").trim();
							query = query.replaceFirst("in", "").trim();
							query = query.replaceFirst("is", "").trim();
							query = query.replaceAll("the", "");
							query = query.replaceAll(" ", "%20");
							 String mystring = null;
						        
						        mystring = XMLfunctions.getXML("http://www.google.com/ig/api?weather="+query);
								// mystring = XMLfunctions.getXML("http://www.google.com/ig/api?weather=hyderabad");
							       try {
							    	   Document doc = null; 
								        doc = XMLfunctions.XMLfromString(mystring);		
								        
								        NodeList n1 = doc.getElementsByTagName("current_conditions");						        
//								        Log.d("no of elements with current conditions tag..", Integer.toString(n1.getLength()));
								        Element e = (Element) n1.item(0);		
								        
								        NodeList n_temp_c =  e.getElementsByTagName("temp_c");
//								        Log.d("no of elements with temp_c tag", Integer.toString(n_temp_c.getLength()));	
								        
								        NamedNodeMap named_node_map_temp_c = n_temp_c.item(0).getAttributes();						       
								        Node data_temp_c = named_node_map_temp_c.getNamedItem("data");						       
								        String current_temp = data_temp_c.getNodeValue();						       
//								        System.out.println(current_temp);						       
								        response = "The weather in the required location is:\n";						       
								        response += "Current Temperature : "+current_temp+"C \n";
								       
								        NodeList n_condition = e.getElementsByTagName("condition");
//								        Log.d("no of elements with condition tag", Integer.toString(n_condition.getLength()));
								        
								        NamedNodeMap nnmap_condition = n_condition.item(0).getAttributes();
								        Node data_condition = nnmap_condition.getNamedItem("data");
								        String condition = data_condition.getNodeValue();
//								        System.out.println(condition);
								        response +=  "Current condition: "+condition+"\n";
								        
								        NodeList n_humidity = e.getElementsByTagName("humidity");
//								        Log.d("no of elements with humidity tag", Integer.toString(n_humidity.getLength()));
								        
								        NamedNodeMap nnmap_humidity = n_humidity.item(0).getAttributes();
								        Node data_humidity = nnmap_humidity.getNamedItem("data");
								        String humidity = data_humidity.getNodeValue();
//								        System.out.println(humidity);
								        response += "Current "+humidity+"\n";
								        
								        NodeList n_wind = e.getElementsByTagName("wind_condition");
//								        Log.d("no of elements with wind_condition tag", Integer.toString(n_wind.getLength()));
								        
								        NamedNodeMap nnmap_wind	 = n_wind.item(0).getAttributes();
								        Node data_wind = nnmap_wind.getNamedItem("data");
								        String wind = data_wind.getNodeValue();
//								        System.out.println(wind);
								        response +=wind;
								        response += "\nSource : Google";
								        tv2.setText(response);
								} catch (Exception e2) {
									// TODO: handle exception
									
									response = "Sorry! No weather locations matched.. or Please enter a valid location !";
									tv2.setText(response);
								}
							       
				
						}
					} // end of weather keyword
					
					else if (key_word.equals("elevation")) {
						
						if(input.toLowerCase().contains("current")||input.toLowerCase().contains("here")||input.toLowerCase().contains("my"))
						{
							LocationListener locationListener = new MyLocationListener();
							LocationManager locationmanager = (LocationManager) Main.this.getSystemService(Context.LOCATION_SERVICE);
							locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
							Location lastknownlocation = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							 
							double latitude= lastknownlocation.getLatitude();
							double longitude = lastknownlocation.getLongitude();
							
							locationmanager.removeUpdates(locationListener);
						       String mystring = null;
						       mystring = XMLfunctions.getXML("http://maps.googleapis.com/maps/api/elevation/xml?locations="+latitude+","+longitude+"&sensor=false");
						       Document doc = null;
						       doc = XMLfunctions.XMLfromString(mystring);
						       NodeList n_status = doc.getElementsByTagName("status");
//						       Log.d("no of nodes with status tag", Integer.toString(n_status.getLength()));
						       
						       NodeList n_elevation = doc.getElementsByTagName("elevation");
						       String elevation = XMLfunctions.getElementValue(n_elevation.item(0));
						       
						       response = "The elevation of the Current Location is:\n"+elevation+" metres above sea level";
						       response += "\nSource : Google";
						       tv2.setText(response);
						      
						}
						else
						{
							String input_copy = input;
							String query = input_copy.toLowerCase().replaceAll("elevation","");
							if(stat_type.equals("act_what"))
							{
								query = query.replaceAll("what", "").trim();
								
							}
							else if(stat_type.equals("act_can"))
							{
								query = query.replaceAll("can", "").trim();
								query = query.replaceAll("you", "").trim();
								query = query.replaceAll("find", "").trim();
								query = query.replaceAll("give", "").trim();
								query = query.replaceAll("get", "").trim();
								query = query.replaceAll("tell", "").trim();
								query = query.replaceAll("me", "").trim();
								query = query.replaceAll("about", "").trim();
								
							}
							query = query.toLowerCase().replaceFirst("give", "").trim();
							query = query.toLowerCase().replaceFirst("find", "").trim();
							query = query.toLowerCase().replaceFirst("at", "").trim();
							query = query.replaceFirst("in", "").trim();
							query = query.replaceFirst("is", "").trim();
							query = query.replaceAll("the", "").trim();
							query = query.replaceFirst("of", "").trim();
							
							
							
							Geocoder geocoder = new Geocoder(Main.this, Locale.ENGLISH);
							try {
								List<Address> addresses = geocoder.getFromLocationName(query, 10);
								if (addresses!=null) {
																		
										Address returned_address = addresses.get(0);
										double latitude = returned_address.getLatitude();
										double longitude  = returned_address.getLongitude();
										
										 String mystring = null;
									       mystring = XMLfunctions.getXML("http://maps.googleapis.com/maps/api/elevation/xml?locations="+latitude+","+longitude+"&sensor=false");
									       Document doc = null;
									       doc = XMLfunctions.XMLfromString(mystring);
									       NodeList n_status = doc.getElementsByTagName("status");
//									       Log.d("no of nodes with status tag", Integer.toString(n_status.getLength()));
									       
									       NodeList n_elevation = doc.getElementsByTagName("elevation");
									       String elevation = XMLfunctions.getElementValue(n_elevation.item(0));
									       
									       response = "The elevation of the given location is:\n"+elevation+" metres above sea level";
									       response += "\nSource : Google";
									       tv2.setText(response);
														
									}
								
								
							} 
							catch(IndexOutOfBoundsException e)
							{
								e.printStackTrace();
								tv2.setText("Please enter the location for the elevation");
							}
							catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								tv2.setText("Sorry, I could not find any existent location which you specified");
							}
							
							
						}
						
					} // end of elevation keyword..
					
					else if(key_word.equals("calculate"))
					{
						// Need to write the code for Expression evaluation here...
						// Get a regular expresssion matched with only Arithmetic expresiion..
						
						String input_copy = input;
						
						String query = input.toLowerCase().replaceAll("[a-z]","").trim();
						query = query.replaceAll(" ", "").trim();
						query = query.replace("+", "%2B");
						
						String input_query = input_copy.toLowerCase().replaceAll("[+]", "%2B");
						response = "Ok then, using the Google calculator for the required calculation..";
						

						Uri uri = Uri.parse("http://www.google.com/search?q="+input_query);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
						
						tv2.setText(response);
					}
					
					
					
				}// end of " if(stat_type.contains("act")) "
				
				if(stat_type.contains("cas"))
				{
					// This denotes that this is a casual statement..!
					// You can go for proper nouns recognition here for somewhat ..!
					String input_copy = input;
					
					if(input_copy.toLowerCase().matches(".* not .*|not .*|.* not|.* nothing .*|.* nothing|nothing .*|nothing|.* no .*|.* no|no .*|no"))
					{
						response = "It's Ok then !";
					}
					
					else if(input_copy.toLowerCase().matches(".* ok .*|.* ok|ok .*|ok|.* okay .*|.* okay|okay .*|.* okay .*|okay|.* kk .*|kk .*|.* kk|kk|.*all right.*|.*alright.*|.* sorry .*|.* sorry|sorry .*|sorry"))
					{
						int min = 0;
						int max= Text_corpus.ok_responses.length - 1;
						Random rand = new Random();
						 int randomNum = rand.nextInt(max - min +1)+min;
						response = Text_corpus.ok_responses[randomNum];
					}
					else if(input_copy.toLowerCase().matches(".*thank.*|.* tnx .*|tnx .*|.* tnx|tnx|.* thnx .*|.* thnx|thnx .*|thnx"))
					{
						int min = 0;
						int max = Text_corpus.thanks_giving.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min +1)+min;
						response = Text_corpus.thanks_giving[randomNum];
					}
					else if(input_copy.toLowerCase().matches("good|great|fine|cool|pleasant"))
					{
						int min = 0;
						int max = Text_corpus.pleasant_gestures.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min + 1)+ min;
						response = Text_corpus.pleasant_gestures[randomNum];
					}
					// for good words
					else if(input_copy.toLowerCase().matches(Text_corpus.reg_exp_corpus_good_words()))
					{
						int min = 0;
						int max = Text_corpus.prasing_responses.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min +1) + min;
						response = Text_corpus.prasing_responses[randomNum];
					}
					// for some good words
					else if(input_copy.toLowerCase().matches(".*great.*|.*super.*|.*fantastic.*|.*excellent.*|.*nice.*|.*terrific.*"))
					{

						int min=0;
						int max= Text_corpus.prasing_responses.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min +1)+ min;
						response = Text_corpus.prasing_responses[randomNum];
						
					}
					//for really good phrases
					else if(input_copy.toLowerCase().matches(Text_corpus.reg_exp_corpus_really_good_phrases()))
					{
						int min = 0;
						int max = Text_corpus.corpus_really_good_phrases_response.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min + 1) + min;
						response = Text_corpus.corpus_really_good_phrases_response[randomNum];
					}
					// recognition of scolding words
					else if(input_copy.toLowerCase().matches(Text_corpus.reg_exp_bad_words()))
					{
						response = "I don't appreciate being talked to like that !";
						
					}
					// for hi responses
					else if(input_copy.toLowerCase().matches(".* hi .*|.* hi|hi .*|hi|.* hello .*|hello .*|.* hello|hello|.* hey .*|hey .*|.* hey|hey"))
					{
						int min = 0;
						int max = Text_corpus.hi_responses.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min +1) + min;
						response = Text_corpus.hi_responses[randomNum];
					}
					// for names
					else if(input_copy.toLowerCase().matches(".* name .*|.* name.*"))
					{
						if(input_copy.toLowerCase().matches(".* your .*|your .*"))
						{
							response = "I am Chitti , your assistant ! Always at your fingertips for your service !";
						}
						else if(input_copy.toLowerCase().matches("my .*"))
						{
							String[] split = input_copy.split(" ");
							String name = split[split.length - 1];
							response = "Hi there, nice to meet you "+name;
							response +="\nAnd I am Chitti, your personal assistant !";
						}
						else if(input_copy.toLowerCase().matches(".* am .*"))
						{
							if(input_copy.toLowerCase().startsWith("who"))
							{
								if(input_copy.toLowerCase().matches(".* i.*"))
								{
									response = "You are my boss! I am Chitti, at your service !";
								}
								else
								{
									response = "Sorry, I'm a bit unclear about your response";
								}
							}
							else
							{
								String[] split = input_copy.split(" ");
								String name = split[split.length - 1];
								response = "Hi "+name+", nice to meet you !";
							}
						}
					}
					else if(input_copy.toLowerCase().matches(".* am .*"))
					{
						voice_wait = true;
						final String input_copy_1 = input_copy;
						new AsyncTask<String , Void, String>()
						{
							protected void onPreExecute() {
								pd.show();
							};
							@Override
							protected String doInBackground(
									String... params) {
								// TODO Auto-generated method stub
								String input_copy_1 = params[0];
								String result = "";
								
								if(input_copy_1.toLowerCase().startsWith("who"))
								{
									if(input_copy_1.toLowerCase().matches(".* i.*"))
									{
										result = "You are my boss! I am Chitti, at your service !";
									}
									else
									{
										result = "Sorry, I'm a bit unclear about your response..!";
									}
								}
								else if(input_copy_1.toLowerCase().matches(".* i .*|i .*"))
								{
									
									String query = input_copy_1.toLowerCase().replaceFirst("i", "").trim();
									 query = query.toLowerCase().replaceFirst("am", "").trim();
									 
									 Spell_checker spell_checker_demo = new Spell_checker(getApplicationContext());
									// You may want to add spinner here....
									 boolean spell_check = spell_checker_demo.test_phrase_whether_grammatically_correct(query);
									 
									 // Wrongly spelled implies that the word present is a name only...
									 if(!spell_check)
									 {
										 // Get the wrongly spelled word here....
//										 String[] split = input_copy.split(" ");
//											String name = split[split.length - 1];
										 
										 	query = query.replaceAll("[?]", "").trim();
										 	query = query.replaceAll("[.]", "").trim();
										 	String name = query;
											result = "Hi "+name+", nice to meet you !\n";
											result = result + "And I am Chitti, your personal assistant !";
									 }
									 else
									 {
										 // Now, you need to check the query among the list of available
										 // nouns and adjectives, and then, process accordingly...
										 if(query.toLowerCase().matches(Text_corpus.reg_exp_common_nouns()+"|"+Text_corpus.reg_exp_common_adjectives()))
										 {
											 // Give a response from the wiki about this query...
											 // Giving the response from the wiki..
											 
											 try
												{
												
													URL website = new URL("http://en.wikipedia.org/wiki/"+query);
										            URLConnection connection = website.openConnection();
										            
										            BufferedReader in = new BufferedReader(
										                                    new InputStreamReader(
										                                        connection.getInputStream()));

										            StringBuilder strbuilder = new StringBuilder();
										            String inputLine;

										            while ((inputLine = in.readLine()) != null) 
										            {
										                strbuilder.append(inputLine);
										                if(inputLine.contains("</p>"))
										                {
										                	break;
										                }
										            }	
										            
										            in.close();
										            String mystring = strbuilder.toString();
//										            Log.d("mystring", mystring);
										            in.close();
//										            System.out.println(mystring);
												
									           	Pattern pattern = Pattern.compile("<p>.*</p>");
									        	Matcher matcher = pattern.matcher(mystring);
									        	
									        	String reqd = "";
									        	
									        	while(matcher.find())
									        	{
									        		reqd = matcher.group().toString();
									        		break;
									        	}
									        	
									        	//NodeList n1 = doc.getElementsByTagName("p");
									        	
									        	
									        	//Log.d("no of nodes of type <p>", Integer.toString(n1.getLength()));
									            //Log.d("value of <p> tag", reqd);        
									            
									            Pattern pattern_start_tags = Pattern.compile("<");
									            Matcher matcher_start_tags = pattern_start_tags.matcher(reqd);
									            
									            Pattern pattern_end_tags = Pattern.compile(">");
									            Matcher matcher_end_tags = pattern_end_tags.matcher(reqd);
									            String remaining = "";
									            int start_len = matcher_start_tags.groupCount() ;
									            int end_len = matcher_end_tags.groupCount();
//									            Log.d("start_len", Integer.toString(start_len));
//									            Log.d("end_len", Integer.toString(end_len));
									            ArrayList<Integer> list_b = new ArrayList<Integer>();
									            ArrayList<Integer> list_e = new ArrayList<Integer>();
									           
									           
									            list_b.add(0);
									            	while(matcher_start_tags.find() && matcher_end_tags.find())
									            	{
									            		
									            		list_e.add(matcher_start_tags.start()-1);
									            		list_b.add(matcher_end_tags.end());
//									            		Log.d("<_matched", Integer.toString(matcher_start_tags.start()-1));
//									            		Log.d(">_matched", Integer.toString(matcher_end_tags.end()));
									            						            		           	
									            	}
									            	list_e.add(reqd.length()-1);
									            	
//									            	Log.d("list_start_len", Integer.toString(list_b.size()));
//									            	Log.d("list_end_len", Integer.toString(list_e.size()));
									            	
									            	 String reqd_string = "";
									            	for(int i=0; i<list_b.size();i++)
									            	{
//									            		System.out.println(list_b.get(i));
//									            		System.out.println(list_e.get(i));
									            		
									            		int start_index = list_b.get(i);
									            		int end_index = list_e.get(i);
									            		
									            		if(start_index <= end_index)
									            		{
									            			reqd_string += reqd.substring(start_index, end_index+1);
									            		}
									            		           		
									            	}
									            	
//									            	System.out.println(reqd_string);
									            	result = "These are my views on: "+query+"\n";
									            	result = result +reqd_string;
									            	result = result + "\nSource: Wikipedia";
									            	result += "Click here for more info:";
//									            	result = result + "\n"+Html.fromHtml("<a href='http://en.wikipedia.org/wiki/"+query+"'>Link</a>");
									        } 
									        
									        catch (Exception e) {
												// TODO: handle exception
									        	e.printStackTrace();
									        	result = "Ok ! Let me try to google it ..";
									        	Uri uri = Uri.parse("http://www.google.com/search?q="+query);
												Intent intent = new Intent(Intent.ACTION_VIEW,uri);
												startActivity(intent);
									        	
											}
												// if not, enquire it in google....
												Uri uri = Uri.parse("http://www.google.com/search?q="+query);
												Intent intent = new Intent(Intent.ACTION_VIEW,uri);
												startActivity(intent);
										 }
										 else
										 {
											 result = "Sorry, I am a bit unclear about that!";
										 }
									 }
								}
								else
								{
									result = "Sorry, I'm unable to get you !";
								}
							
								return result;
							}
							protected void onPostExecute(String result) {
								pd.dismiss();
								response = result;
								tv2.setText(response);
								String query = input_copy_1.toLowerCase().replaceFirst("i", "").trim();
								 query = query.toLowerCase().replaceFirst("am", "").trim();
								 
								 Pattern pattern = Pattern.compile("Click here for more info:");
									String wikiViewURL =  "http://en.wikipedia.org/wiki/"+query;
									   Linkify.addLinks(tv2, pattern, wikiViewURL,new Linkify.MatchFilter() {
										
										public boolean acceptMatch(CharSequence s, int start, int end) {
											// TODO Auto-generated method stub
											return true;
										}
									},new Linkify.TransformFilter() {
										
										public String transformUrl(Matcher match, String url) {
											// TODO Auto-generated method stub
											return "";
										}
									});
								speakWords(response);
							};
						}.execute(input_copy_1);
						}
					else if(input_copy.toLowerCase().matches("who .*|.* who|.* who .*"))
					{
						voice_wait = true;
						final String input_copy_1 = input_copy;
						new AsyncTask<String, Void, String>()
						{
							protected void onPreExecute() {
								pd.show();
							};
							@Override
							protected String doInBackground(String... params) {
								// TODO Auto-generated method stub
								String input_copy_1 = params[0];
								String result="";
								String query = input_copy_1.toLowerCase().replaceAll("[?]", "").trim();
								
								if(query.toLowerCase().matches(".* you .*|.* you.|.* you"))
								{
									result = "I am Chitti, your personal assistant! Always at your service !";
								}
								else
								{
									query = query.replaceAll("who", "").trim();
									query = query.replaceAll(Text_corpus.reg_exp_helping_verbs(), " ").trim();
									query = query.replaceAll(Text_corpus.reg_exp_most_common_conjunctions(), " ").trim();
									
									try
										{
											query = query.replaceAll(" ", "%20").trim();
											URL website = new URL("http://en.wikipedia.org/wiki/"+query);
								            URLConnection connection = website.openConnection();
								            
								            BufferedReader in = new BufferedReader(
								                                    new InputStreamReader(
								                                        connection.getInputStream()));

								            StringBuilder strbuilder = new StringBuilder();
								            String inputLine;

								            while ((inputLine = in.readLine()) != null) 
								            {
								                strbuilder.append(inputLine);
								                if(inputLine.contains("</p>"))
								                {
								                	break;
								                }
								            }	
								            
								            in.close();
								            String mystring = strbuilder.toString();
//								            Log.d("mystring", mystring);
								            in.close();
//								            System.out.println(mystring);
										//String mystring = XMLfunctions.getXML("http://en.wikipedia.org/wiki/"+query);
							        	
							        	//Document doc = null;
							        	//doc = XMLfunctions.XMLfromString(mystring);
							        	
							        	Pattern pattern = Pattern.compile("<p>.*</p>");
							        	Matcher matcher = pattern.matcher(mystring);
							        	
							        	String reqd = "";
							        	
							        	while(matcher.find())
							        	{
							        		reqd = matcher.group().toString();
							        		break;
							        	}
							        	
							        	//NodeList n1 = doc.getElementsByTagName("p");
							        	
							        	
							        	//Log.d("no of nodes of type <p>", Integer.toString(n1.getLength()));
							            //Log.d("value of <p> tag", reqd);        
							            
							            Pattern pattern_start_tags = Pattern.compile("<");
							            Matcher matcher_start_tags = pattern_start_tags.matcher(reqd);
							            
							            Pattern pattern_end_tags = Pattern.compile(">");
							            Matcher matcher_end_tags = pattern_end_tags.matcher(reqd);
							            String remaining = "";
							            int start_len = matcher_start_tags.groupCount() ;
							            int end_len = matcher_end_tags.groupCount();
//							            Log.d("start_len", Integer.toString(start_len));
//							            Log.d("end_len", Integer.toString(end_len));
							            ArrayList<Integer> list_b = new ArrayList<Integer>();
							            ArrayList<Integer> list_e = new ArrayList<Integer>();
							           
							           
							            list_b.add(0);
							            	while(matcher_start_tags.find() && matcher_end_tags.find())
							            	{
							            		
							            		list_e.add(matcher_start_tags.start()-1);
							            		list_b.add(matcher_end_tags.end());
//							            		Log.d("<_matched", Integer.toString(matcher_start_tags.start()-1));
//							            		Log.d(">_matched", Integer.toString(matcher_end_tags.end()));
							            						            		           	
							            	}
							            	list_e.add(reqd.length()-1);
							            	
//							            	Log.d("list_start_len", Integer.toString(list_b.size()));
//							            	Log.d("list_end_len", Integer.toString(list_e.size()));
							            	
							            	 String reqd_string = "";
							            	for(int i=0; i<list_b.size();i++)
							            	{
//							            		System.out.println(list_b.get(i));
//							            		System.out.println(list_e.get(i));
							            		
							            		int start_index = list_b.get(i);
							            		int end_index = list_e.get(i);
							            		
							            		if(start_index <= end_index)
							            		{
							            			reqd_string += reqd.substring(start_index, end_index+1);
							            		}
							            		           		
							            	}
							            	
//							            	System.out.println(reqd_string);
							            	
							            	result = reqd_string;
							            	result = result + "\nSource: Wikipedia\n";
							            	result += "Click here for more info:";
							        } 
							        
							        catch (Exception e) {
										// TODO: handle exception
							        	e.printStackTrace();
							        	result = "Ok ! Let me try to google it ..";
							        	Uri uri = Uri.parse("http://www.google.com/search?q="+input_copy_1);
										Intent intent = new Intent(Intent.ACTION_VIEW,uri);
										startActivity(intent);
							        	
										}
										// Enquire it in google anyways finally....
										Uri uri = Uri.parse("http://www.google.com/search?q="+input_copy_1);
										Intent intent = new Intent(Intent.ACTION_VIEW,uri);
										startActivity(intent);
									
									
								}
								return result;
							}
							protected void onPostExecute(String result) {
								pd.dismiss();
								response = result;
								tv2.setText(response);
								String query = input_copy_1.toLowerCase().replaceAll("[?]", "").trim();
								query = query.replaceAll("who", "").trim();
								query = query.replaceAll(Text_corpus.reg_exp_helping_verbs(), " ").trim();
								query = query.replaceAll(Text_corpus.reg_exp_most_common_conjunctions(), " ").trim();
								query = query.replaceAll(" ", "%20").trim();
								Pattern pattern = Pattern.compile("Click here for more info:");
								String wikiViewURL =  "http://en.wikipedia.org/wiki/"+query;
								   Linkify.addLinks(tv2, pattern, wikiViewURL,new Linkify.MatchFilter() {
									
									public boolean acceptMatch(CharSequence s, int start, int end) {
										// TODO Auto-generated method stub
										return true;
									}
								},new Linkify.TransformFilter() {
									
									public String transformUrl(Matcher match, String url) {
										// TODO Auto-generated method stub
										return "";
									}
								});
								   speakWords(response);
							};
						}.execute(input_copy_1);
						
					}
					else if(input_copy.toLowerCase().matches("yes|yeah|yes .*|yeah .*"))
					{
						int min = 0;
						int max = Text_corpus.yes_responses.length - 1;
						Random rand = new Random();
						int randomNum = rand.nextInt(max - min +1) + min;
						response = Text_corpus.yes_responses[randomNum] ;
						
					}
					else if(input_copy.toLowerCase().contains("what"))
					{
						voice_wait = true;
						 final String input_copy_1 = input_copy;
						
					new AsyncTask<String, Void, String>() {

						protected void onPreExecute() {
							pd.show();
						};
						@Override
						protected String doInBackground(String... params) {
							// TODO Auto-generated method stub
							String input_copy_1 = params[0];
							String result = "";
							String query ;
							query = input_copy_1.toLowerCase().replaceAll("what", "").trim();
							query = query.replaceAll("is", "").trim();
							query = query.replaceAll("[?]", "").trim();
							//query = query.replaceAll("[ ]", "%20").trim();
							
							// Now, query is what has to be searched on google...
							// Or mind it, if possible has to be searched in wikipedia first 
							// and if not available, then search it in google
							
							
							Spell_checker spell_checker_obj = new Spell_checker(getApplicationContext());
							
							// If possible, run a Spinner here...
							boolean spell_check = spell_checker_obj.test_phrase_whether_grammatically_correct(query);
							// if grammmatically correct...
							if(spell_check)
							{
								// search in wiki....
								// if found in wiki, fine, display the response as such..
								if(input_copy_1.contains("your skills")||input_copy_1.contains("you do"))
								{
									Intent intent = new Intent(Main.this, Skill_set.class);
									Main.this.startActivity(intent);
									
									result = "Well, I have got several functionalities with me. All right, I am opening the Skill Set Page..";
								}
								else
								{
								try
								{
									query = query.replaceAll(" ", "%20").trim();
									URL website = new URL("http://en.wikipedia.org/wiki/"+query);
						            URLConnection connection = website.openConnection();
						            
						            BufferedReader in = new BufferedReader(
						                                    new InputStreamReader(
						                                        connection.getInputStream()));

						            StringBuilder strbuilder = new StringBuilder();
						            String inputLine;

						            while ((inputLine = in.readLine()) != null) 
						            {
						                strbuilder.append(inputLine);
						                if(inputLine.contains("</p>"))
						                {
						                	break;
						                }
						            }	
						            
						            in.close();
						            String mystring = strbuilder.toString();
//						            Log.d("mystring", mystring);
						            in.close();
//						            System.out.println(mystring);
								//String mystring = XMLfunctions.getXML("http://en.wikipedia.org/wiki/"+query);
					        	
					        	//Document doc = null;
					        	//doc = XMLfunctions.XMLfromString(mystring);
					        	
					        	Pattern pattern = Pattern.compile("<p>.*</p>");
					        	Matcher matcher = pattern.matcher(mystring);
					        	
					        	String reqd = "";
					        	
					        	while(matcher.find())
					        	{
					        		reqd = matcher.group().toString();
					        		break;
					        	}
					        	
					        	//NodeList n1 = doc.getElementsByTagName("p");
					        	
					        	
					        	//Log.d("no of nodes of type <p>", Integer.toString(n1.getLength()));
					            //Log.d("value of <p> tag", reqd);        
					            
					            Pattern pattern_start_tags = Pattern.compile("<");
					            Matcher matcher_start_tags = pattern_start_tags.matcher(reqd);
					            
					            Pattern pattern_end_tags = Pattern.compile(">");
					            Matcher matcher_end_tags = pattern_end_tags.matcher(reqd);
					            String remaining = "";
					            int start_len = matcher_start_tags.groupCount() ;
					            int end_len = matcher_end_tags.groupCount();
//					            Log.d("start_len", Integer.toString(start_len));
//					            Log.d("end_len", Integer.toString(end_len));
					            ArrayList<Integer> list_b = new ArrayList<Integer>();
					            ArrayList<Integer> list_e = new ArrayList<Integer>();
					           
					           
					            list_b.add(0);
					            	while(matcher_start_tags.find() && matcher_end_tags.find())
					            	{
					            		
					            		list_e.add(matcher_start_tags.start()-1);
					            		list_b.add(matcher_end_tags.end());
//					            		Log.d("<_matched", Integer.toString(matcher_start_tags.start()-1));
//					            		Log.d(">_matched", Integer.toString(matcher_end_tags.end()));
					            						            		           	
					            	}
					            	list_e.add(reqd.length()-1);
					            	
//					            	Log.d("list_start_len", Integer.toString(list_b.size()));
//					            	Log.d("list_end_len", Integer.toString(list_e.size()));
					            	
					            	 String reqd_string = "";
					            	for(int i=0; i<list_b.size();i++)
					            	{
//					            		System.out.println(list_b.get(i));
//					            		System.out.println(list_e.get(i));
					            		
					            		int start_index = list_b.get(i);
					            		int end_index = list_e.get(i);
					            		
					            		if(start_index <= end_index)
					            		{
					            			reqd_string += reqd.substring(start_index, end_index+1);
					            		}
					            		           		
					            	}
					            	
//					            	System.out.println(reqd_string);
					            	
					            	result = reqd_string;
					            	result = result + "\nSource: Wikipedia\n";
					            	result += "Click here for more info:";
					            	
					        } 
					        
					        catch (Exception e) {
								// TODO: handle exception
					        	e.printStackTrace();
					        	
					        	
					        	Uri uri = Uri.parse("http://www.google.com/search?q="+query);
								Intent intent = new Intent(Intent.ACTION_VIEW,uri);
								startActivity(intent);
					        	
								result = "Ok ! Let me try to google it ..";
								}
								// Enquire it in google anyways finally....
								Uri uri = Uri.parse("http://www.google.com/search?q="+query);
								Intent intent = new Intent(Intent.ACTION_VIEW,uri);
								startActivity(intent);
							}
						}
							else
							{
								
								result = "Sorry! I'm a bit confused ! Seems to be spelled wrongly....\n";
								result += "Anyways, I'll query it in google about this.";
								
								Uri uri = Uri.parse("http://www.google.com/search?q="+input_copy_1);
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
								
							}
							return result;
						};
												
						protected void onPostExecute(String result) {
							pd.dismiss();
							response = result;
							tv2.setText(response);
							String query = input_copy_1.toLowerCase().replaceAll("what", "").trim();
							query = query.replaceAll("is", "").trim();
							query = query.replaceAll("[?]", "").trim();
							query = query.replaceAll(" ", "%20").trim();
							Pattern pattern = Pattern.compile("Click here for more info:");
							String wikiViewURL =  "http://en.wikipedia.org/wiki/"+query;
							   Linkify.addLinks(tv2, pattern, wikiViewURL,new Linkify.MatchFilter() {
								
								public boolean acceptMatch(CharSequence s, int start, int end) {
									// TODO Auto-generated method stub
									return true;
								}
							},new Linkify.TransformFilter() {
								
								public String transformUrl(Matcher match, String url) {
									// TODO Auto-generated method stub
									return "";
								}
							});
							
							   speakWords(response);
						}
												
					}.execute(input_copy_1);
						
					
																							
					}
					// Now, we are dealing with the most generalistic response...
					// The stuff I was always interested in ...
					
					else
					{
						voice_wait =true;
						String input_copy_1 = input_copy;
						new AsyncTask<String, Void, String>()
						{
							protected void onPreExecute() {
								pd.show();
							};
							@Override
							protected String doInBackground(String... params) {
								// TODO Auto-generated method stub
								String input_copy_1 = params[0];
								String result = "";
								
								// First spell check...
								Spell_checker spell_checker_obj = new Spell_checker(getApplicationContext());
								boolean spell_check = spell_checker_obj.test_phrase_whether_grammatically_correct(input_copy_1);
								
								if(!spell_check)
								{
									result = "Please rephrase your statement, appears to be wrongly spelled! I'm perplexed !";
								}
								else
								{
									
									// Find nouns out here...
									
									// Some sort of POS implementation is required here...
									
									// Remove all the (most common) conjunctions first...
									
									String query = input_copy_1.toLowerCase().replaceAll("[?]", "").trim();
									
									query = query.replaceAll(Text_corpus.reg_exp_most_common_conjunctions(), " ").trim();
									query = query.replaceAll(Text_corpus.reg_exp_helping_verbs(), " ").trim();
//									Log.d("query", query);
									String[] each_word = query.split(" ");
									ArrayList<String> nouns = new ArrayList<String>();
									ArrayList<String> verbs = new ArrayList<String>();
									ArrayList<String> pronouns = new ArrayList<String>();
									for(int i=0;i<each_word.length ;i++)
									{
										if(each_word[i].matches(Text_corpus.reg_exp_nouns()))
										{
											nouns.add(each_word[i]);
//											Log.d("nouns", each_word[i]);
										}
										else if(each_word[i].matches(Text_corpus.reg_exp_common_verbs()))
										{
											verbs.add(each_word[i]);
//											Log.d("verbs", each_word[i]);
										}
										else if(each_word[i].matches(Text_corpus.reg_exp_common_pronouns()))
										{
											pronouns.add(each_word[i]);
//											Log.d("pronouns", each_word[i]);
										}
									}
									// Now, we have the list of all nouns, pronouns and verbs matched...
									
									if(nouns.size()>0)
									{
										try
										{
										
											URL website = new URL("http://en.wikipedia.org/wiki/"+nouns.get(0));
								            URLConnection connection = website.openConnection();
								            
								            BufferedReader in = new BufferedReader(
								                                    new InputStreamReader(
								                                        connection.getInputStream()));

								            StringBuilder strbuilder = new StringBuilder();
								            String inputLine;

								            while ((inputLine = in.readLine()) != null) 
								            {
								                strbuilder.append(inputLine);
								                if(inputLine.contains("</p>"))
								                {
								                	break;
								                }
								            }	
								            
								            in.close();
								            String mystring = strbuilder.toString();
//								            Log.d("mystring", mystring);
								            in.close();
//								            System.out.println(mystring);
										
							           	Pattern pattern = Pattern.compile("<p>.*</p>");
							        	Matcher matcher = pattern.matcher(mystring);
							        	
							        	String reqd = "";
							        	
							        	while(matcher.find())
							        	{
							        		reqd = matcher.group().toString();
							        		break;
							        	}
							        	
							        	//NodeList n1 = doc.getElementsByTagName("p");
							        	
							        	
							        	//Log.d("no of nodes of type <p>", Integer.toString(n1.getLength()));
							            //Log.d("value of <p> tag", reqd);        
							            
							            Pattern pattern_start_tags = Pattern.compile("<");
							            Matcher matcher_start_tags = pattern_start_tags.matcher(reqd);
							            
							            Pattern pattern_end_tags = Pattern.compile(">");
							            Matcher matcher_end_tags = pattern_end_tags.matcher(reqd);
							            String remaining = "";
							            int start_len = matcher_start_tags.groupCount() ;
							            int end_len = matcher_end_tags.groupCount();
//							            Log.d("start_len", Integer.toString(start_len));
//							            Log.d("end_len", Integer.toString(end_len));
							            ArrayList<Integer> list_b = new ArrayList<Integer>();
							            ArrayList<Integer> list_e = new ArrayList<Integer>();
							           					           
							            list_b.add(0);
							            	while(matcher_start_tags.find() && matcher_end_tags.find())
							            	{
							            		
							            		list_e.add(matcher_start_tags.start()-1);
							            		list_b.add(matcher_end_tags.end());
//							            		Log.d("<_matched", Integer.toString(matcher_start_tags.start()-1));
//							            		Log.d(">_matched", Integer.toString(matcher_end_tags.end()));
							            						            		           	
							            	}
							            	list_e.add(reqd.length()-1);
							            	
//							            	Log.d("list_start_len", Integer.toString(list_b.size()));
//							            	Log.d("list_end_len", Integer.toString(list_e.size()));
							            	
							            	 String reqd_string = "";
							            	for(int i=0; i<list_b.size();i++)
							            	{
//							            		System.out.println(list_b.get(i));
//							            		System.out.println(list_e.get(i));
							            		
							            		int start_index = list_b.get(i);
							            		int end_index = list_e.get(i);
							            		
							            		if(start_index <= end_index)
							            		{
							            			reqd_string += reqd.substring(start_index, end_index+1);
							            		}
							            		           		
							            	}
							            	
//							            	System.out.println(reqd_string);
							            	result = "These are my views on: "+nouns.get(0)+"\n";
							            	result = result +reqd_string;
							            	result = result + "\nSource: Wikipedia";
							            	
							        } 
							        
							        catch (Exception e) {
										// TODO: handle exception
							        	e.printStackTrace();
							        	result = "Ok ! Let me try to google it ..";
							        	Uri uri = Uri.parse("http://www.google.com/search?q="+query);
										Intent intent = new Intent(Intent.ACTION_VIEW,uri);
										startActivity(intent);
							        	
									}
										// if not, enquire it in google....
										Uri uri = Uri.parse("http://www.google.com/search?q="+input_copy_1);
										Intent intent = new Intent(Intent.ACTION_VIEW,uri);
										startActivity(intent);
									}
									else if(verbs.size() > 0)
									{
										result = "What do you want me to "+verbs.get(0)+"\n";
										result += "I'm confused.. Please mention it in any other way that would make me understand";
									}
									else
									{
										if(input_copy_1.toLowerCase().matches("what .*|how .*|when .*|why .*|which .*"))
										{
											// Yoy have to enquire in google then...
											
											Uri uri = Uri.parse("http://www.google.com/search?q="+input_copy_1);
											Intent intent = new Intent(Intent.ACTION_VIEW,uri);
											startActivity(intent);
											
											result = "Ok seems complex to my skills...Let's try to google it!";
										}
										else
										{
											result = "Sorry, I'm not skilled enough to answer that yet...!";
										}
										
									}
								}
								// This is the final one...Cannot get any response out there...
								// even if the above things aren't working, then ...
								// finally, give this response...
								//response = "I'm not sure I follow !";
																
								// add your code for features determining here....
							
								
								return result;
							}
							protected void onPostExecute(String result) {
								pd.dismiss();
								response = result;
								tv2.setText(response);
								speakWords(response);
							};
							
						}.execute(input_copy_1);
			}
				
					tv2.setText(response);
				}
				}	
			et1.setText("");
			if (!voice_wait) {
				  speakWords(response);	
			}
		      voice_wait = false;
			}// end of button click..
		});
        
        
    }// end of Oncreate...
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	myTTS.stop();
    	myTTS.shutdown();
    }
    public void call_function(String number)
    {
    	Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		Main.this.startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.options_menu,menu);
    	return true;
    }
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	   
	   if(item.getItemId()==R.id.skill_set)
	   {
		   Intent intent = new Intent(Main.this,Skill_set.class);
		   Main.this.startActivity(intent);
		   return true;
	   }
	  
	   else
	   {
		   return super.onOptionsItemSelected(item);
	   }
	  }
	
    public void search_message()
    {
    	
    }
    public void open_file()
    {
    	
    }
    public void open_message()
    {
    	
    }
    public void play_or_open_song()
    {
    	
    }
    public void current_loc()
    {
    	
    }
    public void search_message(String p1, String p2)
    {
    	
    }
	//speak the user text
	private void speakWords(String speech) {

			//speak straight away
	    	myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
	}
	
		//act on result of TTS data check
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				//the user has the necessary data - create the TTS
			myTTS = new TextToSpeech(this, this);
			}
			else {
					//no data - install it now
				Intent installTTSIntent = new Intent();
				installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
			}
		}
	}
	public void onInit(int initStatus) {
		// TODO Auto-generated method stub
		//check for successful instantiation
		if (initStatus == TextToSpeech.SUCCESS) {
			if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
				myTTS.setLanguage(Locale.US);
		}
		else if (initStatus == TextToSpeech.ERROR) {
			Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
		}
	}
    

}

