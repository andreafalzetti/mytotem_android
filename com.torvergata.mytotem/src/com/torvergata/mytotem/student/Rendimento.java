package com.torvergata.mytotem.student;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;
import com.torvergata.mytotem.R.style;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;

public class Rendimento extends Activity {
  
	LinearLayout linearLayout;
	private AdView adView;
	MyTotem global;
	 	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_info);     
        linearLayout = (LinearLayout)findViewById(R.id.row1);
        global = ((MyTotem) this.getApplication()); 
        
        findViewById(R.id.btnGoSite).setVisibility(View.GONE);
        findViewById(R.id.btnGoFBPage).setVisibility(View.GONE);

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        int style_Title = R.style.whiteMed;
        int style_Message = R.style.blackMed;
        
        String rendimento[][] = global.getRendimento();
        int row = rendimento.length,
        	col = rendimento[0].length,
        	i, j;
        
        for(i=0; i<row; i++)
        {
        	addTv(rendimento[i][0],  style_Title,   face);  
        	addTv(rendimento[i][1],  style_Message, face);
        }
       
        // Create the adView
        adView = new AdView(this, AdSize.SMART_BANNER, global.admobID);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        adView.setLayoutParams(lp);
        linearLayout.addView(adView);
        AdRequest adRequest = new AdRequest();
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addTestDevice(global.deviceID);
        adView.loadAd(adRequest);
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
   public void onBackPressed() {
	   Rendimento.this.finish();
   }
   
   @Override
   public void onDestroy() {
     if (adView != null) {
       adView.destroy();
     }
     super.onDestroy();
   }
   
}