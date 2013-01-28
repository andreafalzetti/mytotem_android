package com.torvergata.mytotem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.torvergata.mytotem.student.*;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.AdapterView.*;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

public class Home extends Activity {
	MyTotem global;
	LinearLayout linearLayout;
	private AdView adView;
	View mainView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.home);        
        mainView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        global = ((MyTotem) this.getApplication()); 
        global.eliminaVecchiaCartella();
        global.loadPreferences(Home.this);
        //global.cleanPreferences();
        
        // NEl caso di aggiornamento, mostra il log con le nuove funzionalità.
        if(global.checkIfNewAppVersion())
        	showNewFeatures();
        
        linearLayout = (LinearLayout)findViewById(R.id.row1);   
       
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
        
        if(global.isOnline())
        	Log.v("Stato connessione", "Dispositivo connesso");
        else
        	Log.v("Stato connessione", "Dispositivo disconnesso");

        String IMEI, IEMI;
        global.hasTelephony = Home.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if(global.hasTelephony == true)
        {
        	TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        	IMEI = telephonyManager.getDeviceId();
        	IEMI = new StringBuffer(IMEI).reverse().toString();
        }
        else
        {
        	IMEI = "il_tablet_non_3g_non_ha_imei";
        	IEMI = new StringBuffer(IMEI).reverse().toString();
        }              

        if(global.debug) Log.v("Secure", "IMEI rilevato -> " + IMEI);
        if(global.debug) Log.v("Secure", "Nome file -> " + global.MD5(IMEI+IEMI));
        global.setRemFile(IMEI);
        global.careAboutRemFile();
        
        ArrayList<VoceMenu> listaVociMenu=new ArrayList<VoceMenu>();
        VoceMenu [] vociMenu = {
        		new VoceMenu("Gestione carriera", "Accedi al tuo Totem sempre e ovunque",                   		R.drawable.studente),
        		new VoceMenu("Cerca docente", "Informazioni sui docenti",                                     		R.drawable.profesor),
        		new VoceMenu("Didattica Web", "Il sito del tuo corso",                                              R.drawable.didatticaweb),
        		//new VoceMenu("La tua facoltà", "Forum per ogni corso di laurea. Hai una domanda? Falla qui! ",    R.drawable.chat),
        		new VoceMenu("Informazioni utili", "Guida dello studente, immatricolazione, mensa, tasse, moduli.", R.drawable.immatricolazione),
        		new VoceMenu("Facoltà & Corsi", "Offerta Formativa A.A 2012-2013", 								    R.drawable.facolta),
        		//new VoceMenu("Post-Laurea", "(Non attiva) Dottorati, Master di I e II livello e Corsi di perfezionamento", 		R.drawable.master),
        		new VoceMenu("Mercatino", "Cerchi un libro o una stanza? Vuoi vendere il tuo smartphone?", 	        R.drawable.annunci),
        		new VoceMenu("Chiama la segreteria", "Numeri di telefono, email, orari delle segreterie", 						R.drawable.contatti),
        		new VoceMenu("Informazioni", "Segnala un bug e scopri di più", 							    	    R.drawable.info)
        };
        
        for(int i=0; i<vociMenu.length; i++)
        {
        	listaVociMenu.add(vociMenu[i]);
        }
        
        ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
        
        for(int i=0;i<listaVociMenu.size();i++){
        	VoceMenu p=listaVociMenu.get(i);
        	
        	HashMap<String,Object> personMap=new HashMap<String, Object>();//creiamo una mappa di valori
        	
        	personMap.put("img", p.getImg()); // per la chiave image, inseriamo la risorsa dell immagine
        	personMap.put("nome", p.getName()); // per la chiave name,l'informazine sul nome
        	personMap.put("descrizione", p.getSurname());// per la chiave surnaname, l'informazione sul cognome
        	data.add(personMap);  //aggiungiamo la mappa di valori alla sorgente dati
        }
       
        
        String[] from={"img","nome","descrizione"}; //dai valori contenuti in queste chiavi
        int[] to={R.id.personImage,R.id.personName,R.id.personSurname};//agli id dei layout
        
        //costruzione dell adapter
        SimpleAdapter adapter=new SimpleAdapter(
        		getApplicationContext(),
        		data,//sorgente dati
        		R.layout.riga_doppia, //layout contenente gli id di "to"
        		from,
        		to);
       
        ((ListView)findViewById(R.id.mainListView)).setAdapter(adapter);
        ListView list = (ListView) findViewById(R.id.mainListView);
        list.setCacheColorHint(global.sfondo);
        
        list.setOnItemClickListener(new OnItemClickListener() 
        {
        	public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
        	{
        		Intent i = null;
        		boolean online = global.isOnline();
        		Log.v("CLICK", "ID="+position);
        		
        		if(position == 0) 
        		{
        			// Gestione carriera
        			if(online)
        			{
        				// Se lo studente è loggato apro il totem, altrimenti login
        				if(global.isLogged())
        					i = new Intent(Home.this, ProfiloStudente.class);
        				else
        					i = new Intent(Home.this, StudentLogin.class);
        			}
        			else
        				msgNeedConnection();
        		}
        		else if(position == 1)
        		{
        			// Cerca docente
        			if(online)
        				i = new Intent(Home.this, Docenti.class);
        			else
        				msgNeedConnection();
        			
        		}
        		else if(position == 2)
        		{
        			// Didattica Web
        			if(online)
        				i = new Intent(Home.this, DidatticaWeb.class);
        			else
        				msgNeedConnection();
        			
        		}
        		/*else if(position == 3)
        		{
        			// Forum
        			global.openURL(global.getURL("forum"));
        			
        		}*/
        		else if(position == 3)
        		{
        			// Informazioni utili (joomla)
        			if(online)
        				i = new Intent(Home.this, InformazioniUtili.class);
        			else
        				msgNeedConnection();
        			
        		}
        		else if(position == 4)
        		{
        			// Offerta didattica (asset version)
        			i = new Intent(Home.this, OffertaDidattica.class);
        			
        		}
        		else if(position == 5)
        		{
        			msgLavoriInCorso();
        			i = null;
        			// Annunci
        			/*
        			if(online)
        			{
        				i = new Intent(Home.this, Annunci.class);
        			}
        			else
        				msgNeedConnection();
        			*/
        			
        			
        		}
        		else if(position == 6)
        		{
        			// Segreterie
        			i = new Intent(Home.this, Segreterie.class);
        		}
        		
        		else if(position == 7)
        		{
        			// Author info
        			i = new Intent(Home.this, AppInfo.class);
        			i.putExtra("solo-supportaci", "");
        		}
        		else i = new Intent(Home.this, Home.class);
        		
        		try
            	{
            		if(i!=null) startActivity(i);
            	} catch (ActivityNotFoundException e) {
            		// Do nothing
            	}
        	}
        });
        
        // Se la Cartella dell'app non esiste devo crearla, copiandoci dentro i file dell'assets
        // La copia dell'asset va fatta prima dell'update, altrimenti l'update viene vanificato
        if(global.getAppDir().exists() == false)
        {
        	global.getAppDir().mkdir();
        	CopyAssets();
        }
        
  	  Message msg = new Message(); // Messaggio per progressDialog
  	  String textTochange;
  	  textTochange = "check-update";
  	  msg = new Message();
  	  msg.obj = textTochange;
  	  mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
            String text = (String)msg.obj;
            if(global.debug) Log.v("MESSAGGIO RICEVUTO", text);
            
            if(text.equals("check-update"))
            {
                global.updateFiles();  
            }
        }
    };
    
    
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
    
    public void msgNeedConnection()
    {
    	Toast.makeText(getApplicationContext(), "Per accedere a questo servizio è necessaria una connessione ad Internet", Toast.LENGTH_SHORT).show();
    }
   
    public void msgLavoriInCorso()
    {
    	Toast.makeText(getApplicationContext(), "Attualmente in fase di sviluppo! Assicurati di scaricare i prossimi aggiornamenti dell'app per scoprire se la funzione è stata introdotta!", Toast.LENGTH_SHORT).show();
    }
    
   private void CopyAssets() {
       AssetManager assetManager = getAssets();
       String[] files = null;
       try {
           files = assetManager.list("Files");
       } catch (IOException e) {
           Log.e("tag", e.getMessage());
       }

       for(String filename : files) {
           InputStream in = null;
           OutputStream out = null;
           try {
        	 Log.v("Asset-Copy", "Copio: " + "Files/"+filename);
             in = assetManager.open("Files/"+filename);
             out = new FileOutputStream(global.getAppDir().toString() + "/" + filename);
             copyFile(in, out);
             in.close();
             in = null;
             out.flush();
             out.close();
             out = null;
           } catch(Exception e) {
               Log.e("tag", e.getMessage());
           }
       }
   }
   private void copyFile(InputStream in, OutputStream out) throws IOException {
       byte[] buffer = new byte[1024];
       int read;
       while((read = in.read(buffer)) != -1){
         out.write(buffer, 0, read);
       }
   }
 
   @Override
   public void onBackPressed() {
       new AlertDialog.Builder(this)
           .setIcon(android.R.drawable.ic_dialog_alert)
           .setTitle("Conferma")
           .setMessage("Vuoi uscire dall'applicazione?")
           .setPositiveButton("Si", new DialogInterface.OnClickListener()
           {     
        	   public void onClick(DialogInterface dialog, int which) {
        		   System.exit(0);
        	   }
           })
       .setNegativeButton("No", null)
       .show();
   }

   public void showNewFeatures()
   {
	   String log = "";
	   log += "L'applicazione è diventata OPEN SOURCE, il codice è su GitHub, chiunque può consultarlo e collaborare. Questo dimostra la serietà che stiamo mettendo in questo progetto e il fatto che i vostri dati sono al sicuro, nessun dato viene memorizzato in alcun modo!!!!\n";
	   log += "\n[Bug Risolti]\n";
	   log += "- lettura esami verbalizzati\n";
	   log += "[Novità in arrivo]\n";
	   log += "- Calendario Esami\n";
	   log += "- Mercatino annuncio esclusivo tra studenti di Tor Vergata\n";
	   log += "\nPer qualsiasi segnalazione, info o commento: afalzettidroid@gmail.com\n";
	   log += "[Versione: 2.3.1]\n";
	   
	   final Dialog dialog = new Dialog(Home.this);
	   dialog.setContentView(R.layout.alert_dialog);
	   dialog.setTitle("Novità!");
	   TextView text = (TextView) dialog.findViewById(R.id.text);
	   text.setText(log);

	   Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
	   dialogButtonOK.setOnClickListener(new OnClickListener() {
		   public void onClick(View v) {
			   dialog.dismiss();
		   }
	   });
	   
	   Button dialogButtonRate = (Button) dialog.findViewById(R.id.dialogButtonRate);
	   dialogButtonRate.setOnClickListener(new OnClickListener() {
		   public void onClick(View v) {
			   global.rate(mainView);
		   }
	   });
	 
	   dialog.show();
	   global.updateLastVersionRan();
   }
   
   
}