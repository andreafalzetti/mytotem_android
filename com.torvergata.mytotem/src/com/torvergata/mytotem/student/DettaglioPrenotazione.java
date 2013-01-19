package com.torvergata.mytotem.student;

import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.ShowAnim;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DettaglioPrenotazione extends Activity
{
	
	MyTotem global;
	Typeface face;
	Animation ani;
	boolean dettagliShown = false;
	int heightSum = 0;
	
	public void misuraElementi()
	{
		addHeight((TextView) findViewById(R.id.appello));
		addHeight((TextView) findViewById(R.id.inizio));
		addHeight((TextView) findViewById(R.id.fine));
		addHeight((TextView) findViewById(R.id.attiva));
		addHeight((TextView) findViewById(R.id.ciclo));
		addHeight((TextView) findViewById(R.id.aa));		
		addHeight((TextView) findViewById(R.id.tipologia));	
		addHeight((TextView) findViewById(R.id.numPrenotati));	
		addHeight((TextView) findViewById(R.id.id));
		addHeight((TextView) findViewById(R.id.comunicazioni));	
	}
	
	public void addHeight(TextView t)
	{
		heightSum += t.getLineCount() * t.getLineHeight();
		Log.v("Height", "Attuale -> " + heightSum);
	}
	
	public void hideLayoutDettagli()
	{
		LinearLayout ll = (LinearLayout) findViewById(R.id.row4);
		ll.getLayoutParams().height = 0;
	}
	
	public void setVal(int campo, String frase, String value, boolean titolo)
	{
		TextView txt = (TextView) findViewById(campo);
		txt.setText(frase+value);
		if(titolo) face=Typeface.createFromAsset(getAssets(),"fonts/Chunkfive.otf");
		else	   face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
		txt.setTypeface(face);
		
		if(!titolo)
		{
			Spannable WordtoSpan = new SpannableString(frase+value);      
			WordtoSpan.setSpan(new ForegroundColorSpan(global.bordo), 0, frase.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			txt.setText(WordtoSpan);
		}
	}
	
	public void setVal(int campo, String frase, String value)
	{
		setVal(campo, frase, value, false);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dettaglio_prenotazione); 
        Bundle extras = getIntent().getExtras();
                
        global = ((MyTotem) this.getApplication()); 
        int position = (Integer) extras.get("position");
        String prenotazione[] = global.getPrenotazione(position);
        String dettagli[]	  = global.getDettagliPrenotazione(position);

        // Inserimento valori
        setVal(R.id.nomeEsame,     "", 				             prenotazione[0], true);
        setVal(R.id.dataEsame,     "Data esame: ",               prenotazione[5]);
        setVal(R.id.docente,       "Docente: ",   				 prenotazione[1]);
        setVal(R.id.orario,  	   "Orario: ",     			     dettagli[0]);
        setVal(R.id.modalita, 	   "Modalità: ",                 dettagli[2]);
        setVal(R.id.edificio, 	   "Edificio: ", 			     dettagli[4]);
        setVal(R.id.aula, 		   "Aula: ",				     dettagli[5]);
        setVal(R.id.stato,         "Confermata: ", 				     dettagli[6]);
        setVal(R.id.appello,       "Appello: ",			         prenotazione[6]);
        setVal(R.id.inizio,        "Data inizio prenotazione: ", dettagli[1]);
        setVal(R.id.fine,          "Data fine prenotazione: ",	 prenotazione[4]);
        setVal(R.id.attiva,        "Prenotazione attiva: ",		 prenotazione[9]);
        setVal(R.id.ciclo,         "Ciclo: ",                 	 dettagli[3]);
        setVal(R.id.aa, 		   "Anno Accademico: ",          prenotazione[2]);
        setVal(R.id.tipologia, 	   "Tipologia: ", 			     prenotazione[3]);
        setVal(R.id.numPrenotati,  "Numero prenotazioni: ", 	 prenotazione[7]);
        setVal(R.id.id,            "ID: ", 	                     prenotazione[8]);
        setVal(R.id.comunicazioni, "Comunicazioni: ", 	         dettagli[7]);
        
        TextView button= (TextView) findViewById(R.id.buttonDettagli);
        
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mostraDettagli();
            }
        });
        
        
        misuraElementi();
        Log.v("Height", "Finale ->" + heightSum);
       // hideLayoutDettagli();
    }
    
    public void mostraDettagli()
    {
    	LinearLayout l = (LinearLayout) findViewById(R.id.row4); 
    	if(dettagliShown)
    	{
    		Log.v("Animazione", "Chiudo");
    		animaLayout(l, 0);
    		dettagliShown = false;
    	}
		else
		{
			// Sommo le altezze dei figli
			Log.v("Animazione", "Apro a: " +heightSum);
			animaLayout(l, 1000);
			dettagliShown = true;
		}
    	
    }
    
	public void animaLayout(LinearLayout l, int target)
	{
		ani = new ShowAnim(l, target);
		ani.setDuration(800);
		ani.setStartTime(AnimationUtils.currentAnimationTimeMillis());
		l.startAnimation(ani);
	//	l.invalidate();
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