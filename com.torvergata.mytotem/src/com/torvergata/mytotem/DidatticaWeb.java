package com.torvergata.mytotem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.torvergata.mytotem.http.AsyncHttp;
import com.torvergata.mytotem.http.HttpRequestObject;

import android.util.Log;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;

public class DidatticaWeb extends Activity implements AdapterView.OnItemSelectedListener {
	
	MyTotem global;
    private ListView mainListView;
    private int view = 0; /* view=0 -> facoltà, view=1 -> corsi */
    private boolean entrato = false;
	LinearLayout linearLayout;
	private AdView adView;
	private String corsi[][];
	private int numCorsi;
	private String nomeFacolta;
	private ProgressDialog pDialog = null;
	private String facolta[];
	private int globalPos;
	private final String TAG = "DidatticaWeb";
	private String lettere[];
	private int numLettere;
	private ArrayAdapter<?> aa;
	private Spinner spin;
	private boolean sceltaAA_ALL;
	//private String nomiDocentiStampati[];
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.listview_with_spinner);
        //Bundle extras = getIntent().getExtras();
        global = ((MyTotem) this.getApplication());
        
        // oggetto ListView
        mainListView = (ListView) findViewById(R.id.mainListView);  
        mainListView.setCacheColorHint(global.sfondo);
        
        linearLayout = (LinearLayout)findViewById(R.id.row1);        
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
        
        spin = (Spinner) findViewById(R.id.spinnerAlfabeto);
		spin.setOnItemSelectedListener(this);
		
        
        facolta = global.getFacolta();
        cambiaListView(facolta, R.layout.just1row);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        
        	public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
        		globalPos = position;
        		
        		if(view == 0)
        		{
        		// Chiedo dell'anno accademico corrente o tutti
            	AlertDialog.Builder builder = new AlertDialog.Builder(DidatticaWeb.this);
            	builder.setMessage("Vuoi visualizzare i corsi di quest'anno accademico (A.A) o l'elenco completo?\n\nAttenzione: L'operazione può richiedere alcuni secondi, i corsi di alcune facoltà sono centinaia, soprattutto se si desidera visualizzare l'elenco completo.")
            	   .setCancelable(true)
            	   .setPositiveButton("A.A Corrente", new DialogInterface.OnClickListener() {
            	       public void onClick(DialogInterface dialog, int id) {
            	    	   sceltaAA_ALL = false;
            	    	   startParsing();
            	    	   
            	       }
            	   })
            	   .setNegativeButton("A.A precedenti", new DialogInterface.OnClickListener() {
            	       public void onClick(DialogInterface dialog, int id) {
            	    	   sceltaAA_ALL = true;
            	    	   startParsing();
            	       }
            	   });
            	AlertDialog alert = builder.create();
            	alert.show();
        		}
        		else
        		{
        			startParsing();
        		}
        	}
        });        
	}
	
	private void startParsing()
	{
  	  Message msg = new Message(); // Messaggio per progressDialog
  	  String textTochange;
  	  textTochange = "start-parsing";
  	  msg = new Message();
  	  msg.obj = textTochange;
  	  mHandler.sendMessage(msg);
	}
    
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
            String text = (String)msg.obj;
            if(global.debug) Log.v("MESSAGGIO RICEVUTO", text);
            
            if(text.equals("fine"))
            {
            	cambiaListView(getElencoCorsiFacolta(nomeFacolta), R.layout.just1row);
				entrato = true;
				view = 1;
				setTitle("Elenco corsi ("+ numCorsi +") - " + facolta[globalPos]);
				pDialog.dismiss();
				
		        // Spinner lettere dell'alfabeto	
				if(lettere.length>1)
				{
					aa = new ArrayAdapter<Object>(DidatticaWeb.this,android.R.layout.simple_spinner_item, lettere);
					aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin.setAdapter(aa);
					spin.setSelection(0);   
					spin.setVisibility(View.VISIBLE);
				}
				else
				{
					String txtAA;
					if(!sceltaAA_ALL) txtAA = " dell'anno accademico corrente";
					else			 txtAA = " degli anni accademici precedenti";
					Toast.makeText(getApplicationContext(), "Nessun corso di " + capitalizeFirstLetter(nomeFacolta) + " trovato" + txtAA , Toast.LENGTH_LONG).show();
					facolta = global.getFacolta();
			        cambiaListView(facolta, R.layout.just1row);
			        view = 0;
			        setTitle("Elenco corsi");
			        spin.setVisibility(View.GONE);
				}
            }
            else if(text.equals("start-parsing"))
            {
            	parsing();
            }
            	
        }
    };
	

    public void parsing()
    {
    	Object o = mainListView.getItemAtPosition(globalPos);
		if(global.debug) Log.v(TAG, "Object -> " + o.toString());
		if(global.debug) Log.v(TAG, "View -> " +view);
		if(global.debug) Log.v(TAG, "Entrato -> " + entrato);
		entrato = false;
		boolean just1clickPlease = false;
		for(int j=0; j<6 && !just1clickPlease && view==0; j++)
		{
			if(view == 0 && o.toString().substring(o.toString().indexOf("nome=")+5).equals(global.getFacolta()[globalPos]+"}"))
			{
				just1clickPlease = true;
				if(global.debug) Log.v(TAG, "Mostro elenco della facoltà ->" + facolta[globalPos]);
				if(global.debug) Log.v(TAG, "ENTRO IN " + globalPos);
				nomeFacolta = facolta[globalPos].toLowerCase();
				if(nomeFacolta.indexOf(" ") > 0)
				   nomeFacolta = nomeFacolta.substring(0, nomeFacolta.indexOf(" "));
				
   				pDialog = new ProgressDialog(DidatticaWeb.this);
				pDialog.setTitle("Caricamento");
				pDialog.setMessage("Lettura corsi "+ nomeFacolta +"...");
				pDialog.show();
				
				setTitle("Elenco corsi - " + facolta[globalPos]);
	            new Thread() 
	            {
	              @Override
	              public void run() 
	              {
	            	  parseCorsi(nomeFacolta);
	            	  Message msg = new Message(); // Messaggio per progressDialog
	            	  String textTochange;
	            	  textTochange = "fine";
	            	  msg = new Message();
	            	  msg.obj = textTochange;
	            	  mHandler.sendMessage(msg);
	              }
	            }.start();  
			}
		}
		
		if(view == 1)
		{
			if(global.debug) Log.v(TAG, "Mostro alert dialog");
			showCourseDialog();
		}
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
        if(pDialog!=null)
        	pDialog.dismiss();
	}
	
	@Override
	public void onBackPressed() {
		if(pDialog!=null)
        	pDialog.dismiss();
	    if(view == 0)
	    {
	    	// Go Home
	    	DidatticaWeb.this.finish();
	    }
	    else if(view == 1)
	    {
	    	String facolta[] = global.getFacolta();
	        cambiaListView(facolta, R.layout.just1row);
	        view = 0;
	        setTitle("Elenco corsi");
	        spin.setVisibility(View.GONE);
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
    
    public void parseCorsi(String facolta)
    {
    	String lastLetteraTrovata = "",
    			currentLettera = "";
    	numLettere = 0;
    	List<String> lettereTrovate = new ArrayList<String>();
    	 
    	if(global.debug) Log.v(TAG, "Richiesta parsing elenco corsi per facoltà ->"+facolta);
    	String url = global.getURLElencoCorsi(facolta, sceltaAA_ALL); // true=>all
    	if(global.debug) Log.v(TAG, "URL="+url);
    	HttpRequestObject httpRequest = new HttpRequestObject(url, "GET");
    	AsyncHttp asyncHttp = new AsyncHttp();
    	asyncHttp.execute(httpRequest);
    	global.waitAsyncTask(asyncHttp, 60);
        String html = httpRequest.getResponse();
        
        // Parsing dell'html
        String paletto1 = "return true\"><b>";
        String paletto2 = "</b></a></font></td><td>";
        String paletto3 = "<font face=arial,helvetica size=2><a href=http";
        String paletto4 = "/preview.html";
        String tmp, tmp2;
        int pos1, pos2, pos3, pos4;
        List<String> prof_nomiTrovati = new ArrayList<String>();
        List<String> prof_mailTrovati = new ArrayList<String>();
        while(html.indexOf(paletto1) > 0)
        {
        	tmp2 = "";
        	pos1 = html.indexOf(paletto1) + paletto1.length();
        	pos2 = html.substring(pos1).indexOf(paletto2);
        	//if(global.debug) Log.v(TAG, "pos1 = " + pos1 + ", pos2 = " + (pos1+pos2));
        	tmp = html.substring(pos1, pos1+pos2);
        	currentLettera = tmp.substring(0, 1).toUpperCase();
        	if(!currentLettera.equals(lastLetteraTrovata))
        	{
        		numLettere++;
        		lastLetteraTrovata = currentLettera;
        		lettereTrovate.add(currentLettera);
        	}
        	// Se il professore non ha l'email tmp è pronta, sennò devo eliminare il tag <a>..</a>
        	
        	//if(tmp.indexOf("</A>") > 0)
        	//{
        		// prendo l'email ed
        		// elimino il tag a
        	pos3 = html.indexOf(paletto3) + paletto3.length() - 4; // -4 sottrae al taglio -> http
        	pos4 = html.substring(pos3).indexOf(paletto4);
        	tmp2 = html.substring(pos3, pos3+pos4);
        		//tmp2 = html.substring(html.indexOf(paletto3)+paletto3.length());
        		//tmp2 = tmp2.substring(0, tmp2.indexOf("\"")); // -> email found        		
        		//tmp = tmp.substring(tmp.indexOf(paletto3)+paletto3.length()-4, tmp.indexOf(paletto4)); 
        	//}
        	html = html.substring(pos1+pos2+paletto2.length());
        	tmp = tmp.trim();
        	tmp = capitalizeFirstLetter(tmp);
        	if(isAlreadyStored(prof_nomiTrovati, tmp) == false)
        	{
        		//if(tmp.length() < 25)
        		//{
        			//int occorrenzePunto = numOccorrenze(tmp, ".");
        			//int occorrenzeTrattino = numOccorrenze(tmp, "-");
        			//if(occorrenzePunto <= 1 && occorrenzeTrattino == 0)
        			//{
        				//tmp = capitalizeString(tmp);
        				//tmp2 = tmp2.toLowerCase();
        				prof_nomiTrovati.add(tmp);
        				prof_mailTrovati.add(tmp2);
        				//if(global.debug) Log.v(TAG, "Nome corso -> " + tmp + "  # url = " + tmp2);
        			//}
        		//}
        	}
        }
        
        Collections.sort(lettereTrovate);
        numLettere = numLettere + 1;
        lettereTrovate.add(0,"Scegli la lettera iniziale del corso");
        lettere = new String[numLettere];
        for(int i=0; i<lettereTrovate.size(); i++)
        {
        	lettere[i] = lettereTrovate.get(i);
        }
        
        /*
        lettere = new String[numLettere+1];
        lettere[0] = "Scegli la lettera iniziale del corso";
        Collections.sort(lettereTrovate);
        for(int i=1; i<lettereTrovate.size(); i++)
        {
        	lettere[i] = lettereTrovate.get(i-1);
        }
        
        */
        
        numCorsi = prof_nomiTrovati.size();
        if(global.debug) Log.v(TAG, "Num corsi = " + numCorsi);
        corsi = new String[numCorsi][2];
        for(int i=0; i<numCorsi; i++)
        {
        	corsi[i][0] = prof_nomiTrovati.get(i);
        	corsi[i][1] = prof_mailTrovati.get(i);
        }
        if(numCorsi>0)
        ordinaMatrice();
    }
    
    public String[] getElencoCorsiFacolta(String facolta)
    {
    	// In base alla facoltà richiesta elaboro un parsing sulla pagina web presente su didattica web
    	// estrapolando la lista dei docenti, ordinandola in modo alfabetico.
    	String nomiCorsi[] = new String[numCorsi];
    	for(int i=0; i<nomiCorsi.length; i++)
    	{
    		nomiCorsi[i] = corsi[i][0];
    	}
    	return nomiCorsi;
    }
    
    public static String capitalizeFirstLetter(String string) {
    	  char[] chars = string.toLowerCase().toCharArray();
    	  boolean found = false;
    	  int i=0;
    	  //for (int i = 0; i < chars.length; i++) {
    	    if (!found && Character.isLetter(chars[i])) {
    	      chars[i] = Character.toUpperCase(chars[i]);
    	      found = true;
    	    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
    	      found = false;
    	    }
    	 // }
    	  return String.valueOf(chars);
    	}
    
    public boolean isAlreadyStored(List<String> lista, String nome)
    {
    	String s1, s2;
    	// Dato un nome, controlla che non sia presente in lista
        for(int i=0; i< lista.size(); i++)
        {
        	s1 = capitalizeFirstLetter(lista.get(i).trim().toLowerCase());
        	s2 = capitalizeFirstLetter(nome.trim().toLowerCase());
        	//int contenute = s1.indexOf(s2) + s2.indexOf(s1);
        	//Log.v("Contenute", "s1 = " + s1);
        	//Log.v("Contenute", "s2 = " + s2);
        	//Log.v("Contenute", "compare = " + contenute);
        	if(s1.equals(s2))
        	{
        		//if(contenute <= 0)
        			return true;
        	}
        }
    	return false;
    }
    
    public void ordinaMatrice()
    {
    	int row = corsi.length,
    	    i, cmp;
    	String tmp;
    	while(!isSorted())
    	for(i=0; i<row-1; i++)
    	{
    		cmp = corsi[i][0].compareTo(corsi[i+1][0]);
    		if(cmp < 0)
    		{
    			// docenti[i] è minore
    			// non faccio nulla
    		}
    		else
    		{
    			// docenti[i+1] è minore
    			// scambio.. nome
    			tmp = corsi[i][0];
    			corsi[i][0] = corsi[i+1][0];
    			corsi[i+1][0] = tmp;
       			// scambio.. email
    			tmp = corsi[i][1];
    			corsi[i][1] = corsi[i+1][1];
    			corsi[i+1][1] = tmp;
    		}
    	}
    }
    
    private boolean isSorted()
    {
    	int row = corsi.length,
        		col = corsi[0].length,
        	    i,j, cmp;
    	for(i=0; i<row-1; i++)
    	{
    		for(j=0; j<col-1; j++)
    		{
    			cmp = corsi[i][0].compareTo(corsi[i+1][0]);
    			if(cmp > 0) return false;    			
    		}
    	}
    	return true;
    }
    
    public int numOccorrenze(String search, String find)
    {
    	int occurrences = 0;
    	int index = 0;
    	while (index < search.length() && (index = search.indexOf(find, index)) >= 0) {
    	    occurrences++;
    	    index += find.length(); //length of 'the'
    	}
    	return occurrences;
    }
    
    private void showCourseDialog()
    {
    	String nomeCorso = corsi[globalPos][0];
    	String urlCorso = corsi[globalPos][1];
    	String messaggio = "Corso:\n"+nomeCorso;
    	if(urlCorso.length() <= 0) urlCorso = "Sito web non disponibile.";
    	else messaggio += "\n\nSito Web:\n"+urlCorso;
    	
    	final String urlCorsoFinal = urlCorso;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(messaggio)
    	   .setCancelable(true)
    	   .setPositiveButton("Apri Sito Web", new DialogInterface.OnClickListener() {
    	       public void onClick(DialogInterface dialog, int id) {
   	    	    String email = corsi[globalPos][1];
   	    	    if(email.length() > 0)
   	    	    {
   	    	    	global.openURL(urlCorsoFinal);
   	    	    }
   	    	    else
   	    	    {
   	    	    	Toast.makeText(getApplicationContext(), "Sito web non disponibile", Toast.LENGTH_SHORT).show();
   	    	    }
    	       }
    	   })
    	   .setNegativeButton("Annulla", null);
    	AlertDialog alert = builder.create();
    	alert.show();
    }

	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		if(position > 0) // Prendo la lettera scelta
		{
			boolean trovato = false;
		  Object item = spin.getItemAtPosition(position);
		  String lettera = item.toString(),
				 tmp;
		  // Scorro tutta la listView, quando trovo uno con quella lettera lo sparo
		  int i = 0;
		  while(!trovato)
		  {
			  i++;
			  tmp = mainListView.getItemAtPosition(i).toString();
			  tmp = tmp.substring(tmp.indexOf("nome=")+5, tmp.length()-1);
			  if(lettera.equals(tmp.substring(0,1).toUpperCase()))
			  {
				  trovato=true;
				  mainListView.setSelection(i);
				  
			  }
		  }
		}
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
