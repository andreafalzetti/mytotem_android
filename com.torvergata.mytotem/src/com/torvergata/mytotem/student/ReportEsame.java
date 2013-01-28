package com.torvergata.mytotem.student;

import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

public class ReportEsame extends Activity implements OnClickListener
{
	
	MyTotem global;
	WebView webView;
	TextView campiEsame[];
	int idCampiEsame[];
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.report_esame); 
        Bundle extras = getIntent().getExtras();
        
        global = ((MyTotem) this.getApplication()); 
        int position = (Integer) extras.get("position");
        
        String esame[] = global.getEsame(position);
        idCampiEsame = new int[esame.length];
        idCampiEsame[0] = R.id.nomeEsame;
        idCampiEsame[1] = R.id.votoEsame;
        idCampiEsame[2] = R.id.dataEsame;
        idCampiEsame[3] = R.id.aaEsame;
        idCampiEsame[4] = R.id.ssdEsame;
        idCampiEsame[5] = R.id.cfuEsame;
        idCampiEsame[6] = R.id.riconosciutoDa;
        idCampiEsame[7] = R.id.cfuOrig;
        idCampiEsame[8] = R.id.votoOrig;
        idCampiEsame[9] = R.id.dataOrig;
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Chunkfive.otf");
        String frasi[] = {"", "", "Data: ", "A.A: ", "SSD: ", "CFU: ", "N° Verbale: ", "Riconosciuto da: ", "CFU orig: ", "Voto orig: ", "Data orig: ", "Note: "};
        int posizioniColonne[] = {1, 6, 4, 3, 2, 5, 8, 9, 10, 11, 12, 7};
        campiEsame = new TextView[esame.length];
        Log.v("ReportEsame", "campiEsame = " + campiEsame);
        for(int i=0; i<10; i++)
        {
        	if(global.getEsamiCol(posizioniColonne[i])[position].length() > 0)
        	{
        		String s = frasi[i] + global.getEsamiCol(posizioniColonne[i])[position];
        		campiEsame[i] = (TextView) findViewById(idCampiEsame[i]);
        		if(i>1)
        		{
        			Spannable WordtoSpan = new SpannableString(s);      
        			WordtoSpan.setSpan(new ForegroundColorSpan(global.bordo), 0, frasi[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        			campiEsame[i].setText(WordtoSpan);
        		}
        		else
        		{
        			campiEsame[i].setText(s);
        		}
        		
        		if(i==2)
        		{
        			face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        		}
        		campiEsame[i].setTypeface(face);
        	}
        }
        
        
        /*
        face=Typeface.createFromAsset(getAssets(),"fonts/Chunkfive.otf");
        campiEsame[0].setTypeface(face);
        campiEsame[1].setTypeface(face);
        face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        campiEsame[2].setTypeface(face);
        campiEsame[3].setTypeface(face);
        campiEsame[4].setTypeface(face);
        campiEsame[5].setTypeface(face);
        campiEsame[6].setTypeface(face);
        campiEsame[7].setTypeface(face);
        campiEsame[8].setTypeface(face);
        campiEsame[9].setTypeface(face);
        */
    }
    
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
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
