package com.torvergata.mytotem.student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;
import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;


public class CalendarioEsamiFilterSelection extends Activity implements com.google.ads.AdListener {
	
	LinearLayout linearLayout;
	private AdView adView;
	MyTotem global;
	private String anniAccademici[] = {"2012/2013","2011/2012","2010/2011","2009/2010","2008/2009","2007/2008","2006/2007","2005/2006","2004/2003","2003/2004"};
	private String lettere[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private ArrayAdapter<?> aa;
	private Spinner spin;
	private String selected_AA = "2012/2013", selected_Lettera = "A";
	private boolean ADV_CLICKED = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendarioesamifilteselection); 
        linearLayout = (LinearLayout)findViewById(R.id.rowAD);
        global = ((MyTotem) this.getApplication()); 
        AdRequest adRequest;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                
        spin = (Spinner) findViewById(R.id.spinnerAA);
        aa = new ArrayAdapter<Object>(this,android.R.layout.simple_spinner_item, anniAccademici);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);
		spin.setSelection(0);   
		spin.setVisibility(View.VISIBLE);
		
        spin = (Spinner) findViewById(R.id.spinnerIniziale);
        aa = new ArrayAdapter<Object>(this,android.R.layout.simple_spinner_item, lettere);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);
		spin.setSelection(0);   
		spin.setVisibility(View.VISIBLE);
		
        // Create the adView
        if(global.ADV)
        {
        	//global.draw_Advertising(this, linearLayout);
        	AdView adView = new AdView(this, global.admobSize, global.admobID);
        	lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        	adView.setLayoutParams(lp);
        	linearLayout.addView(adView);
        	adRequest = new AdRequest();
        	adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        	adRequest.addTestDevice(global.deviceID);
        	adView.loadAd(adRequest);
        	adView.setAdListener(this);
        }   
        
        
    }
    
    
    public void addTv(String s, int apparence, Typeface face)
    {
    	TextView valueTV = new TextView(this);
    	valueTV.setText(s);
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	lp.setMargins(10, 1, 10, 1);
        valueTV.setLayoutParams(lp);
        valueTV.setTextAppearance(getApplicationContext(), apparence);
        valueTV.setTypeface(face);
        valueTV.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

    	linearLayout.addView(valueTV);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.activity_login, menu);
        return super.onCreateOptionsMenu(menu);
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	global.handlerMenu(item);
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onDestroy() {
      if (adView != null) {
        adView.destroy();
      }
      super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
    
    	CalendarioEsamiFilterSelection.this.finish();   
    }
    
    public void btnGO(View v)
    {
    	int btnID = v.getId();
    	if(btnID == R.id.btnGoFBPage)
    	{
    		try
    		{
    			Intent intent = new Intent(Intent.ACTION_VIEW);
    			intent.setClassName("com.facebook.katana", "com.facebook.katana.ProfileTabHostActivity");
    			intent.putExtra("extra_user_id", "349459645146295");
    			this.startActivity(intent);
    		}
    		catch (Exception e)
    		{
    			global.openURL(global.getURL("facebook"));
    		}
    	}
    	else if(btnID == R.id.btnGoSite)
    	{
    		global.openURL(global.getURL("sito"));
    	}
    }

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if(arg1.getId() == R.id.spinnerAA)
			selected_AA = ((Spinner)arg1).getItemAtPosition(arg2).toString();
		else if(arg1.getId() == R.id.spinnerIniziale)
			selected_Lettera = ((Spinner)arg1).getItemAtPosition(arg2).toString();
		
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void myClickHandler(View v)
	{
		if(!ADV_CLICKED)
			clickAdvToUnlockSearch();
			
		if(ADV_CLICKED && selected_AA.length() > 0 && selected_Lettera.length() > 0)
		{
			Intent i = new Intent(CalendarioEsamiFilterSelection.this, CalendarioEsamiPanoramica.class);
			i.putExtra("AA", selected_AA);
			i.putExtra("lettera", selected_Lettera);
			startActivity(i);
		}
	}


	public void onReceiveAd(Ad ad) {
		// TODO Auto-generated method stub
		
	}


	public void onFailedToReceiveAd(Ad ad, ErrorCode error) {
		// TODO Auto-generated method stub
		
	}


	public void onPresentScreen(Ad ad) {
		// TODO Auto-generated method stub
		
	}


	public void onDismissScreen(Ad ad) {
		// TODO Auto-generated method stub
		
	}


	public void onLeaveApplication(Ad ad) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Ricerca esami abilitata temporaneamente", Toast.LENGTH_LONG).show();
		ADV_CLICKED = true;
	}
	
	public void clickAdvToUnlockSearch()
	{
		Toast.makeText(getApplicationContext(), "Per supportare lo sviluppatore clicca una sola volta banner per abilitare la ricerca degli esami", Toast.LENGTH_LONG).show();
	}
    
}
