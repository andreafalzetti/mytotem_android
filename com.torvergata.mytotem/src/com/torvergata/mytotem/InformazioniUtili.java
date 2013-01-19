package com.torvergata.mytotem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class InformazioniUtili extends Activity
{
	
	MyTotem global;
	private ListView mainListView;
	private String view = "mainview";
	private int numTasti;
	String tasti[];
	String fileToOpen_path;
	ProgressDialog pDialog;
	LinearLayout linearLayout;
	private AdView adView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offerta_didattica); 
        global = ((MyTotem) this.getApplication()); 
        
        linearLayout = (LinearLayout)findViewById(R.id.row1); 
        
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
        
        mainListView = (ListView) findViewById(R.id.mainListView);  
        mainListView.setCacheColorHint(global.sfondo);

        tasti = global.getInformazioniUtiliMenu();
        numTasti = tasti.length;
        cambiaListView(tasti, R.layout.just1row);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        	public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
        		Object o = mainListView.getItemAtPosition(position);
        		Log.v("Click", "ID -> (" + position + ") " + o.toString());
        		if(view.equals("mainview"))
        		{
        			if(position == 0)
        			{
        				// Guida dello Studente
        				view = "guide";
        				cambiaListView(global.getGuideCol("nome"), R.layout.just1row);
        			}
        			else if(position == 1)
        			{
        				// Apro il pdf della guida all'immatricolazione
        				final String fileurl = global.getGuidaImmatricolazione("url");
        				final String filename = global.getGuidaImmatricolazione("filename");
        				String downloadPhrase = "", yes = "", no = "";
        				String filesize = "846 kB";
        				boolean exists = false;
        				if(global.fileInAppDirExists(filename))
        				{
        					String data = global.lastModify(global.getAppDir()+"/"+filename);
        					Log.v("Guida", "Il file (" + filename + ") richiesto è presente nella cartella dell'app => Avverto");
        					downloadPhrase = "Il file " + filename + " è stato già scaricato il:\n\n" + data+ "\n\nDimensione: " + filesize;
        					yes = "Si";
        					yes = "Apri";
        					no  = "Scarica di nuovo";
        					exists = true;
        				}
        				else
        				{
        					Log.v("Guida", "Il file (" + filename + ") richiesto non esiste nella cartella dell'app => Chiedo se scaricare");
        					downloadPhrase = "Scaricare il file " + filename + "?\n\nDimensione: " + filesize;
        					yes = "Si";
        					no  = "No";
        					exists = false;
        				}
        				final boolean exists2 = exists;
     			       new AlertDialog.Builder(InformazioniUtili.this)
     		           .setIcon(android.R.drawable.ic_dialog_alert)
     		           .setTitle("Conferma")
     		           .setMessage(downloadPhrase)
     		           .setPositiveButton(yes, new DialogInterface.OnClickListener()
     		           {     
     		        	   
     		        	   public void onClick(DialogInterface dialog, int which) {
     		        		   if(exists2)
     		        		   {
     		        			   // Apri
     		        			   openPDF(filename);
     		        		   }
     		        		   else
     		        		   {
     		        			   // Scarica nuovamente
     		        			   Toast.makeText(InformazioniUtili.this, "Download in corso", Toast.LENGTH_LONG).show(); 
     		        			   fileToOpen_path = filename;
     		        			   global.download_and_store(fileurl, filename, true);
     		        			   openPDF(filename);        		        	            
     		        		   }
     		        	   }
     		           })
     		           .setNegativeButton(no, new DialogInterface.OnClickListener()
     		           {     
     		        	   
     		        	   public void onClick(DialogInterface dialog, int which) {
     		        		   if(exists2)
     		        		   {
     		        			   // Si
     		        			   Toast.makeText(InformazioniUtili.this, "Download in corso", Toast.LENGTH_LONG).show(); 
     		        			   fileToOpen_path = filename;
     		        			   global.download_and_store(fileurl, filename, true);
     		        			   openPDF(filename);       
     		        		   }
     		        	   }
     		           })
     		           .show();
        				
        			}
        			else if(position == 2)
        			{
        				final String url = "http://www.laziodisu.it/default.asp?id=795";
        				Log.v("Mensa", "Informazioni sulla mensa universitaria -> "+url);
     			       new AlertDialog.Builder(InformazioniUtili.this)
    		           .setIcon(android.R.drawable.ic_dialog_alert)
    		           .setTitle("Conferma")
    		           .setMessage("Vuoi consultare informazioni sulla mensa dell'Ateneo dal sito: \n\n "+url)
    		           .setPositiveButton("Si", new DialogInterface.OnClickListener()
    		           {     
    		        	   
    		        	   public void onClick(DialogInterface dialog, int which) {
    		        		   global.openURL(url);
    		        	   }
    		           })
    		           .setNegativeButton("No", null)
    		           .show();
        			}
        			else if(position == 3)
        			{
        				final String url = "http://www.laziodisu.it";
        				Log.v("Laziodisu", "Informazioni Laziodisu -> "+url);
     			       new AlertDialog.Builder(InformazioniUtili.this)
    		           .setIcon(android.R.drawable.ic_dialog_alert)
    		           .setTitle("Conferma")
    		           .setMessage("Vuoi consultare il sito web: \n\n "+url)
    		           .setPositiveButton("Si", new DialogInterface.OnClickListener()
    		           {     
    		        	   
    		        	   public void onClick(DialogInterface dialog, int which) {
    		        		   global.openURL(url);
    		        	   }
    		           })
    		           .setNegativeButton("No", null)
    		           .show();
        			}
        			else if(position == 4)
        			{
        				final String url = "http://iseeu.uniroma2.it";
        				Log.v("Tasse", "Informazioni Tasse -> "+url);
     			       new AlertDialog.Builder(InformazioniUtili.this)
    		           .setIcon(android.R.drawable.ic_dialog_alert)
    		           .setTitle("Conferma")
    		           .setMessage("Cerchi informazioni sulle tasse universitarie?\nVuoi consultare il sito web: \n\n "+url)
    		           .setPositiveButton("Si", new DialogInterface.OnClickListener()
    		           {     
    		        	   
    		        	   public void onClick(DialogInterface dialog, int which) {
    		        		   global.openURL(url);
    		        	   }
    		           })
    		           .setNegativeButton("No", null)
    		           .show();
        			}

        		}
        		else if(view.equals("guide"))
        		{
        			// Se è type=url apro il pdf reader
        			String type = global.getGuideCol("type")[position];
        			if(type.toLowerCase().equals("pdf"))
        			{
        				boolean exists = false;
        				String downloadPhrase = "", yes = "", no = "";
        				
        				// Controllo se il file esiste nella cartella altrimenti chiedo se vuole scaricarlo
        				String filename = global.getGuideCol("filename")[position];
        				String filesize = global.getGuideCol("dimensione")[position];
        				if(global.fileInAppDirExists(filename))
        				{
        					String data = global.lastModify(global.getAppDir()+"/"+filename);
        					Log.v("Guida", "Il file (" + filename + ") richiesto è presente nella cartella dell'app => Avverto");
        					downloadPhrase = "Il file " + filename + " è stato già scaricato il:\n\n" + data+ "\n\nDimensione: " + filesize;
        					yes = "Si";
        					yes = "Apri";
        					no  = "Scarica di nuovo";
        					exists = true;
        				}
        				else
        				{
        					Log.v("Guida", "Il file (" + filename + ") richiesto non esiste nella cartella dell'app => Chiedo se scaricare");
        					downloadPhrase = "Scaricare il file " + filename + "?\n\nDimensione: " + filesize;
        					yes = "Si";
        					no  = "No";
        					exists = false;
        				}
        				
        				final boolean exists2 = exists;
        			       new AlertDialog.Builder(InformazioniUtili.this)
        		           .setIcon(android.R.drawable.ic_dialog_alert)
        		           .setTitle("Conferma")
        		           .setMessage(downloadPhrase)
        		           .setPositiveButton(yes, new DialogInterface.OnClickListener()
        		           {     
        		        	   
        		        	   public void onClick(DialogInterface dialog, int which) {
        		        		   if(exists2)
        		        		   {
        		        			   // Apri
        		        			   openPDF(global.getGuideCol("filename")[position]);
        		        		   }
        		        		   else
        		        		   {
        		        			   // Scarica nuovamente
        		        			   Toast.makeText(InformazioniUtili.this, "Download in corso", Toast.LENGTH_LONG).show(); 
        		        			   fileToOpen_path = global.getGuideCol("filename")[position];
        		        			   global.download_and_store(global.getGuideCol("url")[position], global.getGuideCol("filename")[position], true);
        		        			   openPDF(global.getGuideCol("filename")[position]);        		        	            
        		        		   }
        		        	   }
        		           })
        		           .setNegativeButton(no, new DialogInterface.OnClickListener()
        		           {     
        		        	   
        		        	   public void onClick(DialogInterface dialog, int which) {
        		        		   if(exists2)
        		        		   {
        		        			   // Si
        		        			   Toast.makeText(InformazioniUtili.this, "Download in corso", Toast.LENGTH_LONG).show(); 
        		        			   fileToOpen_path = global.getGuideCol("filename")[position];
        		        			   global.download_and_store(global.getGuideCol("url")[position], global.getGuideCol("filename")[position], true);
        		        			   openPDF(global.getGuideCol("filename")[position]);
        		        		   }
        		        	   }
        		           })
        		           .show();
        			}
        			if(type.toLowerCase().equals("url"))// Se type=url apro il browser
        			{
        				final String url = global.getGuideCol("url")[position];
        				Log.v("Tasse", "Informazioni Tasse -> "+url);
     			       new AlertDialog.Builder(InformazioniUtili.this)
    		           .setIcon(android.R.drawable.ic_dialog_alert)
    		           .setTitle("Conferma")
    		           .setMessage("Vuoi consultare il sito web: \n\n "+url)
    		           .setPositiveButton("Si", new DialogInterface.OnClickListener()
    		           {     
    		        	   
    		        	   public void onClick(DialogInterface dialog, int which) {
    		        		   global.openURL(url);
    		        	   }
    		           })
    		           .setNegativeButton("No", null)
    		           .show();
        			}
        		}
        		
        	}
        });
    }   	

    private void cambiaListView(String[] lista, int adapterXML)
	{
		String vuoto[] = new String[lista.length];
		for(int i=0; i<vuoto.length; i++)
			vuoto[i] = "";
		
		cambiaListView(lista, vuoto, adapterXML);
	}
	
	private void cambiaListView(String[] lista, String[] descrizioni, int adapterXML)
	{
        ArrayList<RigaDoppia> personList=new ArrayList<RigaDoppia>(); //lista delle persone che la listview visualizzerà
        ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
    
        RigaDoppia [] people = new RigaDoppia[lista.length];
        for(int i=0; i<lista.length; i++)
        {
        	people[i] = new RigaDoppia(lista[i], descrizioni[i]);
        }
        
        for(int i=0;i<people.length;i++){
                personList.add(people[i]);
        }
       
        for(int i=0;i<personList.size();i++){
        	RigaDoppia p=personList.get(i);// per ogni persona all'inteno della ditta
                
                HashMap<String,Object> personMap=new HashMap<String, Object>();//creiamo una mappa di valori
                
              //  personMap.put("image", p.getPhotoRes()); // per la chiave image, inseriamo la risorsa dell immagine
                personMap.put("nome", p.getNome()); // per la chiave name,l'informazine sul nome
                personMap.put("descrizione", p.getDescrizione());// per la chiave surnaname, l'informazione sul cognome
                data.add(personMap);  //aggiungiamo la mappa di valori alla sorgente dati
        }
       
        
        String[] from={"nome","descrizione"}; //dai valori contenuti in queste chiavi
        int[] to={R.id.nome,R.id.descrizione};//agli id delle view
        
        //costruzione dell adapter
        SimpleAdapter adapter=new SimpleAdapter(
                        getApplicationContext(),
                        data,//sorgente dati
                        adapterXML, //layout contenente gli id di "to"
                        from,
                        to);
       
        //utilizzo dell'adapter
        ((ListView)findViewById(R.id.mainListView)).setAdapter(adapter);
        mainListView.setClickable(true);
	}
	
	@Override
	public void onBackPressed() {
	    if(view.equals("mainview"))
	    {
	    	// Go Home
	    	InformazioniUtili.this.finish();
	    }
	    else
	    {
	        cambiaListView(tasti, R.layout.just1row);
	        view = "mainview";
	    }
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
    
    public void openPDF(String filename)
    {
    	File pdfFile = new File(global.getAppDir().toString(), filename);
		
		if(pdfFile.exists()) 
		{
			Log.v("Apro-PDF", "PDF -> "+ pdfFile.toString());
			Uri path = Uri.fromFile(pdfFile); 
	        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
	        pdfIntent.setDataAndType(path, "application/pdf");
	        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	        try
	        {
	        	startActivity(pdfIntent);
	        }
	        catch(Exception e)
	        {
	        	Toast.makeText(InformazioniUtili.this, "Impossibile aprire: nessun lettore pdf installato sul dispositivo.", Toast.LENGTH_LONG).show(); 
	        }
	    }
		else
		{
			Log.v("Apro-PDF", "Non esiste il PDF -> "+ pdfFile.toString());
		}
    }
	
}
