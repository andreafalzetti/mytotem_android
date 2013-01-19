package com.torvergata.mytotem;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.util.Log;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;


public class OffertaDidattica extends Activity {
	
	MyTotem global;
    private ListView mainListView;
    private int view = 0; /* view=0 -> facoltà, view=1 -> corsi */
    String tipologia;
	LinearLayout linearLayout;
	private AdView adView;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.offerta_didattica);
        //Bundle extras = getIntent().getExtras();
        // Da qui prendo variabili e metodi globali all'app
        global = ((MyTotem) this.getApplication());
        global.parseXMLOffertaFormativa();
        // oggetto ListView
        mainListView = (ListView) findViewById(R.id.mainListView);  
        mainListView.setCacheColorHint(global.sfondo);
        
        linearLayout = (LinearLayout)findViewById(R.id.row1);        

        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
        
        String facolta[] = global.getFacolta();
        cambiaListView(facolta, R.layout.just1row);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        		Object o = mainListView.getItemAtPosition(position);
        		
        		boolean entrato = false;
        		for(int j=0; j<6; j++)
        		{
        			if(global.debug) Log.v("Object", "Object -> " + o.toString());
        			if(global.debug) Log.v("Facoltà", "Facoltà -> " + global.getFacolta()[j]);
        			if(view == 0 && o.toString().substring(o.toString().indexOf("nome=")+5).equals(global.getFacolta()[j]+"}"))
        			{
        				cambiaListView(global.getCorsi(j), R.layout.just1row);
        				entrato = true;
        				view = 1;
        			}
        		}
        		
        		if(entrato==false)
        		{
        			if(global.debug) Log.v("Cambio-activity", "Cambio e mostro il dettaglio del corso di laurea");
        			Intent i = new Intent(OffertaDidattica.this, DettagliCorsoLaurea.class);
        			String n = o.toString().substring(o.toString().indexOf("nome=")+5);
        			n = n.substring(0, n.length()-1);
        			Log.v("GLI PASSO", n);
        			i.putExtra("nomeCorso", n);
        			startActivity(i);
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
	    if(view == 0)
	    {
	    	// Go Home
	    	OffertaDidattica.this.finish();
	    }
	    else if(view == 1)
	    {
	    	String facolta[] = global.getFacolta();
	        cambiaListView(facolta, R.layout.just1row);
	        view = 0;
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
    
    public void myClickHandler(View v)
    {
    	Intent i = null;

    	try
    	{
    		if(view == 0)
    		{
    			startActivity(i);    		
    		}
    	} catch (ActivityNotFoundException e) {
    		// Do nothing
    	}
    }  
}
