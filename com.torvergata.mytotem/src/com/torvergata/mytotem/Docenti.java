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
import java.util.Map;

import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;
import com.torvergata.mytotem.http.AsyncHttp;
import com.torvergata.mytotem.http.HttpRequestObject;

import android.util.Log;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;

public class Docenti extends Activity implements AdapterView.OnItemSelectedListener {
	
	MyTotem global;
    private ListView mainListView;
    private int view = 0; /* view=0 -> facoltà, view=1 -> corsi */
    private boolean entrato = false;
	LinearLayout linearLayout;
	private AdView adView;
	private String docenti[][];
	private int numDocenti;
	private String nomeFacolta;
	private ProgressDialog pDialog = null;
	private String facolta[];
	private int globalPos;
	private String lettere[];
	private int numLettere;
	private ArrayAdapter<?> aa;
	private Spinner spin;
	private boolean sceltaAA_ALL;
	private boolean ADV_CLICKED = false;
    
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
            	AlertDialog.Builder builder = new AlertDialog.Builder(Docenti.this);
            	builder.setMessage("Vuoi visualizzare i docenti dei corsi di quest'anno accademico (A.A) o l'elenco completo?\n\nAttenzione: L'operazione può richiedere alcuni secondi, i corsi di alcune facoltà sono centinaia, soprattutto se si desidera visualizzare l'elenco completo.")
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
            
            if(text.equals("fine-ricerca-prof"))
            {
            	cambiaListView(getElencoDocentiFacolta(nomeFacolta), R.layout.just1row);
				entrato = true;
				view = 1;
				setTitle("Elenco docenti (" + numDocenti + ") - " + facolta[globalPos]);
				pDialog.dismiss();
            	
				if(lettere.length>1)
				{
					aa = new ArrayAdapter<Object>(Docenti.this,android.R.layout.simple_spinner_item, lettere);
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
					Toast.makeText(getApplicationContext(), "Nessun docente di " + capitalizeFirstLetter(nomeFacolta) + " trovato" + txtAA , Toast.LENGTH_LONG).show();
					facolta = global.getFacolta();
			        cambiaListView(facolta, R.layout.just1row);
			        view = 0;
			        setTitle("Elenco docenti");
			        spin.setVisibility(View.GONE);
				}
            }
            else if(text.equals("start-parsing"))
            {
            	parsing();
            }
            	
        }
    };
    
    private void parsing()
    {
    	Object o = mainListView.getItemAtPosition(globalPos);
		if(global.debug) Log.v("Docenti.Object", "Object -> " + o.toString());
		if(global.debug) Log.v("Docenti.view", "View -> " +view);
		if(global.debug) Log.v("Docenti.entrato", "Entrato -> " + entrato);
		entrato = false;
		boolean just1clickPlease = false;
		for(int j=0; j<6 && !just1clickPlease && view==0; j++)
		{
			if(view == 0 && o.toString().substring(o.toString().indexOf("nome=")+5).equals(global.getFacolta()[globalPos]+"}"))
			//if(view == 0)
			//{
				just1clickPlease = true;
				if(global.debug) Log.v("Docenti.Elenco", "Mostro elenco della facoltà ->" + facolta[globalPos]);
				if(global.debug) Log.v("ENTROO", "ENTRO IN " + globalPos);
				nomeFacolta = facolta[globalPos].toLowerCase();
				if(nomeFacolta.indexOf(" ") > 0)
				   nomeFacolta = nomeFacolta.substring(0, nomeFacolta.indexOf(" "));
				
   				pDialog = new ProgressDialog(Docenti.this);
				pDialog.setTitle("Caricamento");
				pDialog.setMessage("Lettura docenti "+ nomeFacolta +"...");
				pDialog.show();
				
				setTitle("Elenco docenti - " + facolta[globalPos]);
	            new Thread() 
	            {
	              @Override
	              public void run() 
	              {
	            	  //nomeFacolta = facolta[position].toLowerCase();
      				  //cambiaListView(getElencoDocentiFacolta(nomeFacolta), R.layout.just1row);
    				  //entrato = true;
    				  //view = 1;
    				  //setTitle("Elenco docenti - " + facolta[position]);
	            	  parseDocenti(nomeFacolta);
	            	  
	              	Message msg = new Message(); // Messaggio per progressDialog
	            	String textTochange;
	            	textTochange = "fine-ricerca-prof";
	            	msg = new Message();
	            	msg.obj = textTochange;
	            	mHandler.sendMessage(msg);
	              }
	            }.start();  
			//}
		}
		
		if(view == 1)
		{
			Log.v("Docenti", "Mostro alert dialog docente");
			showDocenteDialog();
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
	    	Docenti.this.finish();
	    }
	    else if(view == 1)
	    {
	    	String facolta[] = global.getFacolta();
	        cambiaListView(facolta, R.layout.just1row);
	        view = 0;
	        setTitle("Elenco docenti");
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
    
    public void parseDocenti(String facolta)
    {
    	String lastLetteraTrovata = "",
    			currentLettera = "";
    	numLettere = 0;
    	List<String> lettereTrovate = new ArrayList<String>();
    	 
    	final String TAG = "ParseDocenti";
    	if(global.debug) Log.v(TAG, "Richiesta parsing elenco docenti per facoltà ->"+facolta);
    	String url = global.getURLElencoCorsi(facolta, sceltaAA_ALL); // true=>all
    	if(global.debug) Log.v(TAG, "URL="+url);
    	HttpRequestObject httpRequest = new HttpRequestObject(url, "GET");
    	AsyncHttp asyncHttp = new AsyncHttp();
    	asyncHttp.execute(httpRequest);
    	global.waitAsyncTask(asyncHttp, 60);
        String html = httpRequest.getResponse();
        
        // Parsing dell'html
        String paletto1 = "</b></a></font></td><td><font face=arial,helvetica size=2>";
        String paletto2 = "</font>";
        String paletto3 = "class=geo>";
        String paletto4 = "</A>";
        String tmp, tmp2, htmlRow;
        int pos1, pos2;
        List<String> prof_nomiTrovati = new ArrayList<String>();
        List<String> prof_mailTrovati = new ArrayList<String>();
        while(html.indexOf(paletto1) > 0)
        {
        	tmp2 = "";
        	pos1 = html.indexOf(paletto1) + paletto1.length();
        	pos2 = html.substring(pos1).indexOf(paletto2);
        	//if(global.debug) Log.v(TAG, "pos1 = " + pos1 + ", pos2 = " + (pos1+pos2));
        	tmp = html.substring(pos1, pos1+pos2);
        	// Se il professore non ha l'email tmp è pronta, sennò devo eliminare il tag <a>..</a>
        	
        	if(tmp.indexOf("</A>") > 0)
        	{
        		// prendo l'email ed
        		// elimino il tag a
        		tmp2 = tmp.substring(tmp.indexOf("mailto:")+7);
        		tmp2 = tmp2.substring(0, tmp2.indexOf("\"")); // -> email found        		
        		tmp = tmp.substring(tmp.indexOf(paletto3)+paletto3.length(), tmp.indexOf(paletto4));
        	}
        	html = html.substring(pos1+pos2+paletto2.length());
        	tmp = tmp.trim();
        	currentLettera = tmp.substring(0, 1).toUpperCase();
        	if(!currentLettera.equals(lastLetteraTrovata) && letteraIsStored(lettereTrovate, currentLettera)==false)
        	{
        		Log.v("Diff", currentLettera + " != " + lastLetteraTrovata);
        		numLettere++;
        		lastLetteraTrovata = currentLettera;
        		lettereTrovate.add(currentLettera);
        	}
        	
        	if(isAlreadyStored(prof_nomiTrovati, tmp) == false)
        	{
        		if(tmp.length() < 25)
        		{
        			int occorrenzePunto = numOccorrenze(tmp, ".");
        			int occorrenzeTrattino = numOccorrenze(tmp, "-");
        			if(occorrenzePunto <= 1 && occorrenzeTrattino == 0)
        			{
        				tmp = capitalizeString(tmp);
        				tmp2 = tmp2.toLowerCase();
        				prof_nomiTrovati.add(tmp);
        				prof_mailTrovati.add(tmp2);
        				//if(global.debug) Log.v(TAG, "Nome docente -> " + tmp + "  # email = " + tmp2);
        			}
        		}
        	}
        }
        
        Collections.sort(lettereTrovate);
        lettereTrovate.add(0, "Scegli la lettera iniziale del docente");
        numLettere++;
        Log.v("Lettere", "numLettere = "+lettereTrovate.size());
        lettere = new String[numLettere];
        //lettere[0] = "Scegli la lettera iniziale del docente";
        for(int i=0; i<lettereTrovate.size(); i++)
        {
        	lettere[i] = lettereTrovate.get(i);
        	Log.v("Lettere", "i="+i+" -> "+lettereTrovate.get(i));
        }
        
        //Collections.sort(numDocenti);
        numDocenti = prof_nomiTrovati.size();
        if(global.debug) Log.v(TAG, "Num docenti = " + numDocenti);
        docenti = new String[numDocenti][2];
        for(int i=0; i<numDocenti; i++)
        {
        	docenti[i][0] = prof_nomiTrovati.get(i);
        	docenti[i][1] = prof_mailTrovati.get(i);
        }
        if(numDocenti>0)
        ordinaMatrice();
    }
    
    public String[] getElencoDocentiFacolta(String facolta)
    {
    	// In base alla facoltà richiesta elaboro un parsing sulla pagina web presente su didattica web
    	// estrapolando la lista dei docenti, ordinandola in modo alfabetico.
    	String nomiDocenti[] = new String[numDocenti];
    	for(int i=0; i<nomiDocenti.length; i++)
    	{
    		nomiDocenti[i] = docenti[i][0];
    	}
    	return nomiDocenti;
    }
    
    public static String capitalizeString(String string) {
    	  char[] chars = string.toLowerCase().toCharArray();
    	  boolean found = false;
    	  for (int i = 0; i < chars.length; i++) {
    	    if (!found && Character.isLetter(chars[i])) {
    	      chars[i] = Character.toUpperCase(chars[i]);
    	      found = true;
    	    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
    	      found = false;
    	    }
    	  }
    	  return String.valueOf(chars);
    	}
    
    public boolean isAlreadyStored(List<String> lista, String nome)
    {
    	String s1, s2;
    	// Dato un nome, controlla che non sia presente in lista
        for(int i=0; i< lista.size(); i++)
        {
        	s1 = capitalizeString(lista.get(i).trim().toLowerCase());
        	s2 = capitalizeString(nome.trim().toLowerCase());
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
    	int row = docenti.length,
    		col = docenti[0].length,
    	    i,j, cmp;
    	String tmp;
    	while(!isSorted())
    	for(i=0; i<row-1; i++)
    	{
    		cmp = docenti[i][0].compareTo(docenti[i+1][0]);
    		if(cmp < 0)
    		{
    			// docenti[i] è minore
    			// non faccio nulla
    		}
    		else
    		{
    			// docenti[i+1] è minore
    			// scambio.. nome
    			tmp = docenti[i][0];
    			docenti[i][0] = docenti[i+1][0];
    			docenti[i+1][0] = tmp;
       			// scambio.. email
    			tmp = docenti[i][1];
    			docenti[i][1] = docenti[i+1][1];
    			docenti[i+1][1] = tmp;
    		}
    	}
    }
    
    private boolean isSorted()
    {
    	int row = docenti.length,
        		col = docenti[0].length,
        	    i,j, cmp;
    	for(i=0; i<row-1; i++)
    	{
    		for(j=0; j<col-1; j++)
    		{
    			cmp = docenti[i][0].compareTo(docenti[i+1][0]);
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
    
    private void showDocenteDialog()
    {
    	String nomeDocente = docenti[globalPos][0];
    	String email = docenti[globalPos][1];
    	String messaggio = "Docente:\n"+nomeDocente;
    	if(email.length() <= 0) email = "E-Mail non disponibile.";
    	else messaggio += "\n\nE-Mail:\n"+email;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(messaggio)
    	   .setCancelable(true)
    	   .setPositiveButton("Invia E-Mail", new DialogInterface.OnClickListener() {
    	       public void onClick(DialogInterface dialog, int id) {
   	    	    String email = docenti[globalPos][1];
   	    	    if(email.length() > 0)
   	    	    {
   	    	    	global.sendEmail(email);
   	    	    }
   	    	    else
   	    	    {
   	    	    	Toast.makeText(getApplicationContext(), "E-Mail docente non disponibile", Toast.LENGTH_SHORT).show();
   	    	    }
    	       }
    	   })
    	   .setNegativeButton("Apri Sito Web", new DialogInterface.OnClickListener() {
    	       public void onClick(DialogInterface dialog, int id) {
    	    	    String nomeDocente = docenti[globalPos][0];
    	            String url = global.getURLDocente(nomeDocente);
    	            global.openURL(url);
    	       }
    	   });
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
			  Log.v("MMM", tmp);
			  if(lettera.equals(tmp.substring(0,1).toUpperCase()))
			  {
				  trovato=true;
				  mainListView.setSelection(i);
				  
			  }
		  }
		}
		
	}
	
	private boolean letteraIsStored(List<String> lista, String lettera)
    {
    	String s1, s2;
        for(int i=0; i< lista.size(); i++)
        {
        	s1 = lista.get(i).toUpperCase();
        	s2 = lettera.toUpperCase();
        	if(s1.equals(s2))
        	{
        		return true;
        	}
        }
    	return false;
    }
    

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
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
}
