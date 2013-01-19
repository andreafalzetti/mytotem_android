package com.torvergata.mytotem.student;

import java.util.ArrayList;
import java.util.HashMap;

import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.RigaDoppia;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;
import com.torvergata.mytotem.R.style;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EsamiPrenotati extends Activity implements OnClickListener
{
	
	MyTotem global;
    private ListView mainListView;
    LinearLayout linearLayout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.offerta_didattica);
        linearLayout = (LinearLayout)findViewById(R.id.row1);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        //Bundle extras = getIntent().getExtras();
        // Da qui prendo variabili e metodi globali all'app
        global = ((MyTotem) this.getApplication());
        // oggetto ListView
        mainListView = (ListView) findViewById(R.id.mainListView);  
        mainListView.setCacheColorHint(global.sfondo);
        mainListView.setDivider(null);
        mainListView.setDividerHeight(0);
        
        
        String prenotazioni[] = global.getPrenotazioniCol(0);
        if(prenotazioni.length > 0)
        {
        	Log.v("Prenotazioni", "DIM = " + prenotazioni.length);
        	cambiaListView(prenotazioni, R.layout.just1row);
        }
        else
        {
        	String domanda, risposta;
            int style_Title = R.style.whiteMed;
            int style_Message = R.style.blackMed;
            domanda = "Nessuna prenotazione trovata";
            risposta = "Le prenotazioni vengono resettate ogni tanto. Per certezza controllare sul sito web.";
            addTv(domanda,  style_Title,   face);  
            addTv(risposta, style_Message, face);        	
        }
        mainListView.setOnItemClickListener(new OnItemClickListener() 
        {
        	Intent i;
        	public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
        	{
        		
        		i = new Intent(EsamiPrenotati.this, DettaglioPrenotazione.class);
        		i.putExtra("position", position);

        		Log.v("CLICK", "ID="+position);
        		
        		try
            	{
            		startActivity(i);
            	} catch (ActivityNotFoundException e) {
            		// Do nothing
            	}
        	}
        });
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
    
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
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
