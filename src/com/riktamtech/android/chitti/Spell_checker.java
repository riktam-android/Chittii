package com.riktamtech.android.chitti;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.riktamtech.android.chitti.R;

import android.content.Context;



public class Spell_checker {

	Context context;
	public Spell_checker(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public boolean spell_check_for_a_word(String word)
	{
		boolean result=false;;
		try {
			 
			  // Get the object of DataInputStream
			char start_char = word.charAt(0);
			
			 InputStream in = get_input_stream(start_char);
			 // DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
//			  System.out.println (strLine);
			  if(strLine.equals(word))
			  {
				  result = true;
				  break;
			  }
			 
			  }
		
			  //Close the input stream
			  in.close();
		}
		catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		
		return result;
	} 
	
	public boolean test_phrase_whether_grammatically_correct(String phrase)
	{
		boolean result = true;
		
		// First, need to tokenize each word in the phrase to be checked for consistency
		
		String query = phrase.toLowerCase().replaceAll("[+]", "").trim();
		query = query.toLowerCase().replaceAll("[.]", "").trim();
		query = query.toLowerCase().replaceAll("[,]", "").trim();
		query = query.toLowerCase().replaceAll("[?]", "").trim();
		String[] each_word = query.split(" ");
		for(int i=0;i<each_word.length;i++)
		{
			if(!spell_check_for_a_word(each_word[i]))
			{
				result = false;
			}
		}
		return result;
	}
	
	public InputStream get_input_stream(char start_char)
	{
		InputStream in;
		switch (start_char) {
		case 'a':
			in = Spell_checker.this.context.getResources().openRawResource(R.raw.a);
			break;
		case 'b':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.b);
			break;
		case 'c':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.c);
			break;
		case 'd':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.d);
			break;
		case 'e':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.e);
			break;
		case 'f':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.f);
			break;
		case 'g':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.g);
			break;
		case 'h':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.h);
			break;
		case 'i':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.i);
			break;
		case 'j':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.j);
			break;
		case 'k':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.k);
			break;
		case 'l':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.l);
			break;
		case 'm':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.m);
			break;
		case 'n':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.n);
			break;
		case 'o':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.o);
			break;
		case 'p':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.p);
			break;
		case 'q':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.q);
			break;
		case 'r':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.r);
			break;
		case 's':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.s);
			break;
		case 't':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.t);
			break;
		case 'u':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.u);
			break;
		case 'v':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.v);
			break;
		case 'w':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.w);
			break;
		case 'x':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.x);
			break;
		case 'y':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.y);
			break;
		case 'z':
			 in = Spell_checker.this.context.getResources().openRawResource(R.raw.z);
			break;
		default:
			in = null;
			break;
		}
		return in;
	}
}
	
