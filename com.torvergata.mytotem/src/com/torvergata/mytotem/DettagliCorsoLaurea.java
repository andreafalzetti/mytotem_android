package com.torvergata.mytotem;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DettagliCorsoLaurea extends Activity
{
	
	MyTotem global;
	Animation ani;
	int Height1 = 0,
		Height2 = 0,
		Height3 = 0;
	boolean current1 = true,
			current2 = true,
			current3 = true;
	String url;
	LinearLayout linearLayout;
	private AdView adView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dettaglio_corsolaurea); 
        Bundle extras = getIntent().getExtras();
        String nomeCorso = extras.getString("nomeCorso");  

        global = ((MyTotem) this.getApplication()); 
        
        linearLayout = (LinearLayout)findViewById(R.id.rowAD);
        // Create the adView
        adView = new AdView(this, AdSize.SMART_BANNER, global.admobID);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        adView.setLayoutParams(lp);
        linearLayout.addView(adView);
        AdRequest adRequest = new AdRequest();
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addTestDevice(global.deviceID);
        adView.loadAd(adRequest);
        
        if(global.debug) Log.v("DettaglioCorsoLaurea", "nome corso = " + nomeCorso);
        String corso[] = global.getCorsoByName(nomeCorso);    
        url = corso[4];
        
        View l1 =  findViewById(R.id.row1_hidden);
        View l2 =  findViewById(R.id.row2_hidden);
        View l3 =  findViewById(R.id.row3_hidden);
        
        TextView t;
        t = (TextView) findViewById(R.id.nomeCorso);
        t.setText(corso[0]);
           
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
                
        if(corso[1].equals(""))
        {
        	LinearLayout l = (LinearLayout) findViewById(R.id.row1);
        	l.setVisibility(View.GONE);
        }
        t = new TextView(this);
        t.setTextAppearance(getApplicationContext(), R.style.whiteSmall);
        t.setSingleLine(false);
        t.setLayoutParams(childParams);
        t.setText(corso[1]);
        ((LinearLayout)l1).addView(t);
        
        if(corso[2].equals(""))
        {
        	LinearLayout l = (LinearLayout) findViewById(R.id.row2);
        	l.setVisibility(View.GONE);
        }
        t = new TextView(this);
        t.setTextAppearance(getApplicationContext(), R.style.whiteSmall);
        t.setSingleLine(false);
        t.setLayoutParams(childParams);
        t.setText(corso[2]);
        ((LinearLayout)l2).addView(t);
        
        if(corso[3].equals(""))
        {
        	LinearLayout l = (LinearLayout) findViewById(R.id.row3);
        	l.setVisibility(View.GONE);
        }
        else
        {
        	t = new TextView(this);
        	t.setTextAppearance(getApplicationContext(), R.style.whiteSmall);
        	t.setSingleLine(false);
        	t.setLayoutParams(childParams);
        	t.setText(corso[3]);
        	((LinearLayout)l3).addView(t);
        }
        
    }
    
    public void clickHandler(View v)
    {
    	if(Height1 == 0)
    	{
    		// Prendo le misure
        	Height1 = ((LinearLayout) findViewById(R.id.row1_hidden)).getHeight();
        	Height2 = ((LinearLayout) findViewById(R.id.row2_hidden)).getHeight();
        	Height3 = ((LinearLayout) findViewById(R.id.row3_hidden)).getHeight();
    	}
    	/*
    	if(current1 == true && ((LinearLayout) findViewById(R.id.row1_hidden)).getHeight() == 0)
    		animaLayout((LinearLayout)findViewById(R.id.row1_hidden), Height1);
    	if(current2 == true && ((LinearLayout) findViewById(R.id.row2_hidden)).getHeight() == 0)
    		animaLayout((LinearLayout)findViewById(R.id.row2_hidden), Height2);
    	if(current3 == true && ((LinearLayout) findViewById(R.id.row3_hidden)).getHeight() == 0)
    		animaLayout((LinearLayout)findViewById(R.id.row3_hidden), Height3);
*/
    	int layoutTarget = 0;
    	int heightTarget = 0;
    	
    	if(v.getId() == R.id.row1){
    		Log.v("Animazione", "Animazione su Row 1");
    		layoutTarget = R.id.row1_hidden;
    		if(!current1)    
    		{
    			heightTarget = Height1;
    			current1 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current1 = false;
    		}
    		Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	else if(v.getId() == R.id.row2){
    		Log.v("Animazione", "Animazione su Row 2");
    		layoutTarget = R.id.row2_hidden;
    		if(!current2)    			
    		{
    			heightTarget = Height2;
    			current2 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current2 = false;
    		}
    		Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	else if(v.getId() == R.id.row3){
    		Log.v("Animazione", "Animazione su Row 3");
    		layoutTarget = R.id.row3_hidden;
    		if(!current3)    			
    		{
    			heightTarget = Height3;
    			current3 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current3 = false;
    		}
    		Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	else if(v.getId() == R.id.row4){
    		
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    		startActivity(browserIntent);
    	}
    	
    	if(layoutTarget != 0)
    	{
    		Log.v("Animazione", "Target -> " + layoutTarget + " - Altezza: "  +heightTarget);
    		LinearLayout l = (LinearLayout)findViewById(layoutTarget);
    		animaLayout(l, heightTarget);
    	}
    	
    	
    }
    
	public void animaLayout(LinearLayout l, int target)
	{
		ani = new ShowAnim(l, target);
		ani.setDuration(800);
		ani.setStartTime(AnimationUtils.currentAnimationTimeMillis());
		l.invalidate();
		l.startAnimation(ani);
		l.invalidate();
		l.startAnimation(ani);
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
}