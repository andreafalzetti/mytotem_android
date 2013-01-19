package com.torvergata.mytotem;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ads.*;


public class AppInfo extends Activity {
	
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
        Bundle extras = getIntent().getExtras();
        
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        int style_Title = R.style.whiteMed;
        int style_Message = R.style.blackSmall;
        String domanda, risposta;
        AdRequest adRequest;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                
        domanda = "Cos'è MyTotem?";
        risposta = "MyTotem è un'applicazione dedicata agli studenti dell'Università degli Studi di Roma 'Tor Vergata', permette di accedere al proprio totem, sempre e ovunque tu sia.";
        addTv(domanda,  style_Title,   face);  
        addTv(risposta, style_Message, face);
 
        domanda = "Cos'è il Totem?";
        risposta = "Dal totem puoi consultare tutta la tua 'carriera' universitaria, il sito ufficiale è delphi.uniroma2.it.";
        addTv(domanda,  style_Title,   face);
        addTv(risposta, style_Message, face);
        
        domanda = "Disclaimer";
        risposta = "Attenzione\nqualsiasi studente immatricolato, dotato di una connessione internet ed un web-browser (firefox, chrome, etc) può accedere alle informazioni che questa applicazione propone. Questo software si limita ad effettuare le stesse operazioni del browser. L'autore non ha alcuna responsabilità della veridicità dei contenuti estrapolati dal sito delphi.uniroma2.it o di eventuali interruzioni di servizio dello stesso. I dati personali sono ad uso esclusivo dell'utente e dell'applicazione, pertanto non vi è alcuna conoscenza, nè possibilità di reperimento, uso o trattamento. Ragion per cui nessuna possibile violazione di quanto previsto dalla discplina legale del \"Codice in materia di protezione dei dati personali\" (D.lgs  n. 196/2003) da parte dello sviluppatore";
        addTv(domanda,  style_Title,   face);  
        addTv(risposta, style_Message, face);

        domanda = "Come si usa?";
        risposta = "Semplicemente effettuando il login come se stessi sul sito delphi.uniroma2.it, inserendo Matricola e Passowrd.";
        addTv(domanda,  style_Title,   face);
        addTv(risposta, style_Message, face);
        
        domanda = "E' gratis?";
        risposta = "L'app è completamente gratuita, se ti risulta particolarmente utile e vuoi supportare lo sviluppo futuro dell'app e migliorarla, puoi farlo cliccando sui banner presenti o offrendomi un caffè :-P";
        addTv(domanda,  style_Title,   face); 
        addTv(risposta, style_Message, face);
        
        domanda = "Info Sviluppo";
        risposta = "Ho realizzato quest'app per avere a portata di mano il totem, ho pensato che potesse servire a tutti e l'ho pubblicata. Mi piacerebbe fare un porting su iOS, forse in futuro :-)\n\nSviluppatore: Andrea Falzetti\nEmail: afalzettidroid@gmail.com\nFacoltà di Scienze MM.FF.NN - Informatica";
        addTv(domanda,  style_Title,   face);  
        addTv(risposta, style_Message, face);
        
        domanda = "Credits";
        risposta = "Tutto questo non sarebbe stato possibile senza l'aiuto di:\nLucrezia Farina - Supporter\nClaudio Cavarretta - Supporto legale\nMarco Ferri - Aiuto grafico\nMatteo Russo - Aiuto contenuti\nPatryk Rzucidlo - Hoster/Tester\nEmanuele Vicino - Aiuto grafico\nSimone Zaccariello - Tester";
        addTv(domanda,  style_Title,   face);  
        addTv(risposta, style_Message, face);
        
        domanda = "Supportaci";
        risposta = "Clicca (anche più volte) sul banner sottostante per supportarci, Grazie!";
        addTv(domanda,  style_Title,   face);  
        addTv(risposta, style_Message, face);
        
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);  
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
    
    	AppInfo.this.finish();   
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
    
}
