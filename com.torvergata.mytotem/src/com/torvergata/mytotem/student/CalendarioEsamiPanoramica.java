package com.torvergata.mytotem.student;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.*;
import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.RigaDoppia;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;
import com.torvergata.mytotem.http.AsyncHttp;
import com.torvergata.mytotem.http.AsyncHttpDownloader;
import com.torvergata.mytotem.http.HttpFile;
import com.torvergata.mytotem.http.HttpRequestObject;


public class CalendarioEsamiPanoramica extends Activity implements AdapterView.OnItemSelectedListener {
	
	LinearLayout linearLayout;
	private AdView adView;
	MyTotem global;
	private ArrayAdapter<?> aa;
	private Spinner spin;
	private String selected_AA,
				   selected_Lettera = "";
	private ProgressDialog pDialog;
	private ListView mainListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendarioesamipanoramica); 
        linearLayout = (LinearLayout)findViewById(R.id.rowAD);
        global = ((MyTotem) this.getApplication()); 
        Bundle extras = getIntent().getExtras();
        selected_AA = extras.getString("AA");
        selected_Lettera = extras.getString("lettera");
        TextView descrizione = (TextView) findViewById(R.id.descrizione);        
        descrizione.setText("Anno " + selected_AA + " - Lettera '"+ selected_Lettera +"'");
        AdRequest adRequest;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
        mainListView = (ListView) findViewById(R.id.mainListView);  
        mainListView.setCacheColorHint(global.sfondo);
        mainListView.setDivider(null);
        mainListView.setDividerHeight(0);
        
        // Estrapolo il tutto
        // Mostro un dialog di caricamento
        pDialog = new ProgressDialog(CalendarioEsamiPanoramica.this);
        pDialog.setTitle("Caricamento");
        pDialog.setMessage("Cerco appelli...");
        pDialog.show();
        new Thread() 
        {
            @Override
          public void run() 
          {
             try
               {
            	  cercaAppelli(selected_AA, selected_Lettera);
              }
            catch (Exception e)
            {
                Log.e("tag",e.toString());
            }
         }
        }.start();  
        
        // Create the adView
        adView = new AdView(this, AdSize.SMART_BANNER, global.admobID);
        adView.setLayoutParams(lp);
        linearLayout.addView(adView);
        adRequest = new AdRequest();
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
    public void onDestroy() {
      if (adView != null) {
        adView.destroy();
      }
      super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
    
    	CalendarioEsamiPanoramica.this.finish();   
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

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if(arg1.getId() == R.id.spinnerAA)
			selected_AA = ((Spinner)arg1).getItemAtPosition(arg2).toString();
		else if(arg1.getId() == R.id.spinnerIniziale)
			selected_Lettera = ((Spinner)arg1).getItemAtPosition(arg2).toString();
		
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void myClickHandler()
	{
		if(selected_AA.length() > 0 && selected_Lettera.length() > 0)
		{
			Intent i = new Intent(CalendarioEsamiPanoramica.this, CalendarioEsamiPanoramica.class);
		}
	}
	
	public void cercaAppelli(String AA, String iniziale)
	{
    	// Eseguo il login
    	HttpClient httpClient = null;
		HttpRequestObject httpRequest = new HttpRequestObject();
		Message msg = new Message(); // Messaggio per progressDialog
    	String textTochange;
    	boolean STOP = false;
    	
    	// Devo simulare il login
        httpRequest.setURL(global.getURL("login"));
        httpRequest.setMethod("POST");
        httpRequest.addParam("login", global.getMatricola());
        httpRequest.addParam("password", global.getPassword());
        httpRequest.addParam("language", "IT");
        httpRequest.addParam("entra", "Entra");  
        
        AsyncHttp asyncHttp = new AsyncHttp();
        asyncHttp.execute(httpRequest);
        global.waitAsyncTask(asyncHttp, 30);
        String response = httpRequest.getResponse();

        if(response.indexOf("Inserisci Login e Password") > 0 || response.indexOf("CCD Pagina Errore") > 0)
        {
        	textTochange = "LOGIN-FAIL";
            msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	STOP = true;
        }
        if(STOP == false)
        {        	
        	// Per poter fare la richiesta POST ed ottenere i dati interessanti, devo simulare il passaggio per le seguenti pagine
        	// con una richiesta GET ......... altrimenti non funziona -_-"
        	httpRequest.purgeParamsList();
        	httpRequest.setURL(global.getURL("calendario-esami-preparazione-1"));
        	httpRequest.setMethod("GET");
        	asyncHttp = new AsyncHttp();
        	asyncHttp.execute(httpRequest);
        	global.waitAsyncTask(asyncHttp, 30);
        	
        	httpRequest.purgeParamsList();
        	httpRequest.setURL(global.getURL("calendario-esami-preparazione-2"));
        	httpRequest.setMethod("GET");
        	asyncHttp = new AsyncHttp();
        	asyncHttp.execute(httpRequest);
        	global.waitAsyncTask(asyncHttp, 30);     	

        	// Eccoci!
        	httpRequest.purgeParamsList();
        	httpRequest.setMethod("POST");
        	httpRequest.setURL(global.getURL("calendario-esami"));
        	httpRequest.addParam("iniziale", "A");
        	httpRequest.addParam("anno", "2011/2012 ");
        	httpRequest.addParam("sessione", "TUTTE");
        	httpRequest.addParam("Avanti", "Avanti");
        	asyncHttp = new AsyncHttp();
            asyncHttp.execute(httpRequest);
            global.waitAsyncTask(asyncHttp, 10);
            response = httpRequest.getResponse();
            Log.v("ESAMI", response);

        	// Eseguo il parsing dell'html
            estrapolaAppelli(response);
        }
	}
	
	public void estrapolaAppelli(String html)
	{
		
		
	}
	
	Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
            String text = (String)msg.obj;
            if(text.equals("OK"))
            {
            	pDialog.dismiss();
            	// Mostro i risultati
            	
            	String appelli[] = global.getAppelliCol(1);
                Log.v("Esami", "DIM = " + appelli.length);
                cambiaListView(appelli, R.layout.just1row);
            }
            else if(text.equals("FAIL"))
            {
            	pDialog.dismiss();
            	Toast.makeText(getApplicationContext(), "Non è stato possibile recuperare le informazioni richieste a causa di un errore.", Toast.LENGTH_LONG).show();
            }
            else if(text.equals("LOGIN-FAIL"))
            {
            		pDialog.dismiss();
            		Log.v("User login", "Login Failed");
            		Toast.makeText(getApplicationContext(), "Non è stato possibile eseguire il login. Ricontrollare Matiricola e Password. Ricordiamo che nella fascia d'orario 00:00 1:30 il Totem non è raggiungibile.", Toast.LENGTH_LONG).show();
            }
            else
            
            {
            	Log.v("Cambio pDialog", "Message->"+text);
            	pDialog.setMessage(text);
            }
            	
        }
    };
    
    
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
    
}
