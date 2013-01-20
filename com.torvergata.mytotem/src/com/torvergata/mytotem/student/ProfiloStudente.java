package com.torvergata.mytotem.student;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.torvergata.mytotem.Home;
import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.VoceMenu;
import com.torvergata.mytotem.R.drawable;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class ProfiloStudente extends Activity {
	MyTotem global;
	private ListView mainListView;
	LinearLayout linearLayout;
	private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profilo_studente); 
        global = ((MyTotem) this.getApplication()); 
        linearLayout = (LinearLayout)findViewById(R.id.row1);
        mainListView = (ListView) findViewById(R.id.mainListView);  
        
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
        
        String[] vociMenu = {"Totem",
        					 "Esami verbalizzati",
        					 "Certificati",
        					 "Prenotazioni",
        					 "Media",    
        					 "Calendario esami",
        					 };
        
        String[] descMenu = {"Le tue info (Anagrafica e Carriera)",
        					 "I tuoi esami superati",  
        					 "Certificati di iscrizione, freq, esami",        					 
        					 "Elenco degli esami prenotati",
        					 "Il tuo rendimento",
        					 "Controlla le date d'esame"
        					 };
        
        int[]    fotoMenu = {R.drawable.studente,  
        					 R.drawable.esame,
        					 R.drawable.certificati,
        					 R.drawable.prenotati,
        					 R.drawable.statistiche,
        					 R.drawable.calendario
        					 };
        
        cambiaListView(mainListView, vociMenu, descMenu, fotoMenu);
        mainListView.setCacheColorHint(global.sfondo);
        
        mainListView.setOnItemClickListener(new OnItemClickListener() 
        {        	
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
        	{
        		Intent i = null;
        		if(global.debug) Log.v("CLICK", "ID="+position);
        		if(position == 0)
        		{
        			// Dati personali
        			i = new Intent(ProfiloStudente.this, DettagliPersonali.class);
        		}
        		if(position == 1) 
        		{
        			// Gestione carriera
        			i = new Intent(ProfiloStudente.this, EsamiVerbalizzati.class);
        		}
        		else if(position == 2)
        		{
        			// Certificati
        			i = new Intent(ProfiloStudente.this, Certificati.class);
        		}
        		else if(position == 3)
        		{
        			// Esami prenotati
        			i = new Intent(ProfiloStudente.this, EsamiPrenotati.class);
        		}
        		else if(position == 4)
        		{
        			// Rendimento
        			i = new Intent(ProfiloStudente.this, Rendimento.class);
        		}
        		else if(position == 5)
        		{
        			msgLavoriInCorso();
        			// Calendario esami        			
        			//i = new Intent(ProfiloStudente.this, CalendarioEsamiFilterSelection.class);
        		}
        		//else i = new Intent(ProfiloStudente.this, Home.class);
        		
        		try
            	{
            		if(i!= null) startActivity(i);
            	} catch (ActivityNotFoundException e) {
            		// Do nothing
            	}
        	}
        });
    }  
    
    public void msgLavoriInCorso()
    {
    	Toast.makeText(getApplicationContext(), "Attualmente in fase di sviluppo! Assicurati di scaricare i prossimi aggiornamenti dell'app per scoprire se la funzione è stata introdotta!", Toast.LENGTH_SHORT).show();
    }
    
	private void cambiaListView(ListView lw, String[] lista, String[] descrizioni, int [] photos)
	{
        ArrayList<VoceMenu> personList=new ArrayList<VoceMenu>();
        ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
    
        VoceMenu [] people = new VoceMenu[lista.length];
        for(int i=0; i<lista.length; i++)
        {
        	people[i] = new VoceMenu(lista[i], descrizioni[i], photos[i]);
        }
        
        for(int i=0;i<people.length;i++){
                personList.add(people[i]);
        }
       
        for(int i=0;i<personList.size();i++){
        	VoceMenu p=personList.get(i);
                
                HashMap<String,Object> menuVociMap=new HashMap<String, Object>();

                menuVociMap.put("img", p.getImg());
                menuVociMap.put("nome", p.getName());
                menuVociMap.put("descrizione", p.getSurname());
                
                data.add(menuVociMap);
        }
       
        String[] from={"img","nome","descrizione"};
        int[] to={R.id.personImage,R.id.personName,R.id.personSurname};
        
        //costruzione dell adapter
        SimpleAdapter adapter=new SimpleAdapter(
                        getApplicationContext(),
                        data,//sorgente dati
                        R.layout.riga_doppia, //layout contenente gli id di "to"
                        from,
                        to);
       
        //utilizzo dell'adapter
        //((ListView)findViewById(R.id.mainListView)).setAdapter(adapter);
        //mainListView.setClickable(true);
        lw.setAdapter(adapter);
        lw.setClickable(true);
        
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	// Sono loggato, quindi tornando indietro finisco alla home
	    	Intent i = new Intent(ProfiloStudente.this, Home.class);
	    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(i);
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
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