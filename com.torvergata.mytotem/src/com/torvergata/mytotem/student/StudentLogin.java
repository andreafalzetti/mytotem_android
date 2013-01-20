package com.torvergata.mytotem.student;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;
import com.torvergata.mytotem.http.HttpRequestObject;
import com.torvergata.mytotem.http.AsyncHttp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class StudentLogin extends Activity {
	
	MyTotem global;
	EditText txtMatricola;
	EditText txtPassword;
	CheckBox salvaLogin;
	HttpClient httpclient;
	HttpRequestObject httpRequest = new HttpRequestObject();
	ProgressDialog pDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login); 
        
        global = ((MyTotem) this.getApplication()); 
        
        txtMatricola = (EditText) findViewById(R.id.txt_matricola);
        txtPassword = (EditText) findViewById(R.id.txt_password);        
        salvaLogin = (CheckBox) findViewById(R.id.checkSave);
        
        toggleStatoLoginButton(false);
        
        txtMatricola.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if 's' is empty 
            	// Prendo anche la lunghezza della password
            	
            	if(s.length() > 0 && txtPassword.getText().length() > 0)
            		toggleStatoLoginButton(true);
            	else
            		toggleStatoLoginButton(false);
            }

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
        });
        
        
        txtPassword.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check if 's' is empty 
            	// Prendo anche la lunghezza della password
            	
            	if(s.length() > 0 && txtPassword.getText().length() > 0)
            		toggleStatoLoginButton(true);
            	else
            		toggleStatoLoginButton(false);
            }

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
        });
        
        // Controllo se l'utente ha salvato i suoi dati, in caso li leggo, eseguo il login e cambio activity
        String matricola = global.getMatricola();
        String password = global.getPassword();
        if(matricola.length() > 0 && password.length() > 0)
        {        	
        	if(global.debug) Log.v("Login", "Login automatico");
        	txtMatricola.setText(matricola);
        	txtPassword.setText(password); 	
        	salvaLogin.setChecked(true);
        	toggleStatoLoginButton(true);
        }
        controllaOrario();
    }
    
    public void controllaOrario()
    {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        
        if(hour == 0)
        {
        	Toast.makeText(getApplicationContext(), "Attenzione - Il sito Delphi è in manutenzione in questa fascia oraria, non è quindi possibile effettuare il login.", Toast.LENGTH_LONG).show();
        }
        else if(hour == 1 && min > 0 && min < 30)
        {
        	Toast.makeText(getApplicationContext(), "Attenzione - Il sito Delphi è in manutenzione in questa fascia oraria, non è quindi possibile effettuare il login.", Toast.LENGTH_LONG).show();
        }       	
    	
    }
    
    public void myClickHandler(View v){ 
    	Intent i=null;
    	
    	if(v.getId() == R.id.btn_login) i = new Intent(this, ProfiloStudente.class);
    	try
    	{
    		final String matricola = String.valueOf(txtMatricola.getText());
    		final String password = String.valueOf(txtPassword.getText());
    		
    		// Se richiesto, salvo le informazioni
            if(salvaLogin.isChecked())
            	global.storeAccount(matricola, password);
            else
            	global.destroyLoginRemFile();
            
    		global.setMatricola(matricola);
            global.setPassword(password);
            
            if(global.debug) Log.v("Login", "Matricola = " + matricola);
            //if(global.debug) Log.v("Login", "Password = " + password);
            //// Eseguo il login
            pDialog = new ProgressDialog(StudentLogin.this);
            pDialog.setTitle("Caricamento");
            pDialog.setMessage("Login in corso...");
            pDialog.show();
           
           // boolean successoLogin = false;
            new Thread() 
            {
                @Override
              public void run() 
              {
                 try
                   {
                	  TotemLogin(matricola, password);
                	 

              // do the background process or any work that takes time to see progreaa dialog

                  }
                catch (Exception e)
                {
                    Log.e("tag",e.toString());
                }
            // dismiss the progressdialog   
              //pDialog.dismiss();
             }
            }.start();  
            
            //pDialog.dismiss();
            
           /* if(global.isLogged())
            {
            	i = new Intent(StudentLogin.this, ProfiloStudente.class);
            	startActivity(i);
            }
*/    	} catch (ActivityNotFoundException e) {
    		// Do nothing
    	}
    }  
    
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
            String text = (String)msg.obj;
            if(global.debug) Log.v("MESSAGGIO RICEVUTO", text);
            if(text.equals("LOGIN-OK"))
            {
            	if(global.debug) Log.v("User login", "Login Successfull");
            	pDialog.dismiss();
            	global.setLoggedState(true);
            	Intent i = new Intent(StudentLogin.this, ProfiloStudente.class);
            	startActivity(i);
            }
            else if(text.equals("LOGIN-FAIL"))
            {
            	pDialog.dismiss();
            	if(global.debug) Log.v("User login", "Login Failed");
            	Toast.makeText(getApplicationContext(), "Non è stato possibile eseguire il login. Ricontrollare Matiricola e Password. Ricordiamo che nella fascia d'orario 00:00 1:30 il Totem non è raggiungibile.", Toast.LENGTH_LONG).show();
            }
            else
            
            {
            	if(global.debug) Log.v("Cambio pDialog", "Message->"+text);
            	pDialog.setMessage(text);
            }
            	
        }
    };
    
    public void TotemLogin(String matricola, String password) throws IOException {

    	Message msg = new Message(); // Messaggio per progressDialog
    	String textTochange;
    	boolean STOP = false;
    	//textTochange = "Lettura.. Esami";
    	//msg = new Message();
    	//msg.obj = textTochange;
    	//mHandler.sendMessage(msg);
    	
      	
    	// LOGIN
        httpRequest.setURL(global.getURL("login"));
        httpRequest.setMethod("POST");
        httpRequest.addParam("login", matricola);
        httpRequest.addParam("password", password);
        httpRequest.addParam("language", "IT");
        httpRequest.addParam("entra", "Entra");  
                
        AsyncHttp asyncHttp = new AsyncHttp();
        asyncHttp.execute(httpRequest);
        global.waitAsyncTask(asyncHttp, 30);
        String response = httpRequest.getResponse();
        if(response.indexOf("Inserisci Login e Password") > 0 || response.indexOf("CCD Pagina Errore") > 0)
        {
        	if(global.debug) Log.v("LOGIN", "LOGIN-FAIL!!!!!");
        	textTochange = "LOGIN-FAIL";
            msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	STOP = true;
        }
        else Log.v("LOGIN", "LOGIN-SUCCESSFULL!");

        if(STOP == false)
        {
        	if(global.debug) Log.v("LOGIN", "Accedo");
        	
        	if(global.debug) Log.v("Login-Ok", "Il login è andato a buon fine");
        	// ESAMI VERBALIZZATI     
        	textTochange = "Lettura.. Esami";
        	msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	httpRequest.purgeParamsList();
        	httpRequest.setURL(global.getURL("esami_verbalizzati"));
        	httpRequest.setMethod("GET");
        	asyncHttp = new AsyncHttp();
        	asyncHttp.execute(httpRequest);
        	global.waitAsyncTask(asyncHttp, 30); // Timeout 10 seconds
        	response = httpRequest.getResponse();
        	if(global.debug) Log.v("LOGIN", response.substring(response.indexOf("LOGOUT")));
        	getEsamiVerbalizzati(response);
        	getRendimento(response);
        
        	
        
        	// PRENOTAZIONI
        	textTochange = "Lettura.. Prenotazioni";
        	msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	httpRequest.purgeParamsList();
        	httpRequest.setURL(global.getURL("prenotazioni"));
        	httpRequest.setMethod("GET");
        	asyncHttp = new AsyncHttp();
        	asyncHttp.execute(httpRequest);
        	global.waitAsyncTask(asyncHttp, 10); // Timeout 10 seconds
        	response = httpRequest.getResponse();
        	if(response.indexOf("<form method=") > 0)
        		getPrenotazioni(response.substring(response.indexOf("<form method=")));
        	else
        		global.setNumPrenotazioni(0);
        
        	// ANAGRAFICA
        	textTochange = "Lettura.. Anagrafica";
        	msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	httpRequest.purgeParamsList();
        	httpRequest.setURL(global.getURL("dati_personali"));
        	httpRequest.setMethod("GET");
        	asyncHttp = new AsyncHttp();
        	asyncHttp.execute(httpRequest);
        	global.waitAsyncTask(asyncHttp, 10); // Timeout 10 seconds
        	response = httpRequest.getResponse();        	        
        	estrapolaDettagliPersonali(response);
                   
        	textTochange = "LOGIN-OK";
        	msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	
        }
    } 
    
    public void getRendimento(String html)
    {
    	String textTochange = "Lettura.. Rendimento";
        Message msg = new Message();
    	msg.obj = textTochange;
    	mHandler.sendMessage(msg);
    	
    	// Ritaglio lo stretto necessario
    	String paletto1 = "RENDIMENTO",
    		   paletto2 = "Sono visibili";
    	int    pos1     = html.indexOf(paletto1),
    		   pos2     = html.indexOf(paletto2);
    	html = html.substring(pos1, pos2);
    	if(global.debug) Log.v("Rendimento-HTML", "HTML = " +html);
    	
    	// Rendimento è una matrice così composta:
    	// Nome Campo - Valore
    	// ....		  - ....	
    	String rendimento[][] = new String[5][2];
    	String frasi[] = {"Esami validi", "Esame peggiore", "Esame migliore", "Media aritmetica", "Media ponderata"};
    	// 0 - Esami validi
    	// 1 - Esame peggiore
    	// 2 - Esame migliore
    	// 3 - Media aritmetica
    	// 4 - Media ponderata
    	paletto1 = "</B>:</B> ";
    	paletto2 = "<br>"; // questo paletti valgno per 4 [0-3] elementi del vettore
    	//rendimento[0][1] = "" + global.getNumEsamiValidi(); // Estrapolata in precedenza
    	//if(global.debug) Log.v("Rendimento-Val", frasi[0] + "-> " + rendimento[0][1]);
    	for(int i=0; i<=3; i++)
    	{
    		pos1 = html.indexOf(paletto1) + paletto1.length();
    		pos2 = (html.substring(pos1)).indexOf(paletto2);
    		if(global.debug) Log.v("Rendimento-Cut", "Estrapolo String da pos1="+pos1 + " a " + (pos1+pos2));
    		rendimento[i][0] = frasi[i];
    		rendimento[i][1] = html.substring(pos1, (pos1+pos2));
    		if(global.debug) Log.v("Rendimento-Val", frasi[i] + "-> " + rendimento[i][1]);
    		html = html.substring(pos1+pos2+paletto2.length());
    		if(global.debug) Log.v("Rendimento-HTML", "HTML = " +html);
    	}
    	paletto1 = "</B>:</B> ";
    	paletto2 = "</td>"; // questo paletti valgno il 5 elemento
    	if(global.debug) Log.v("Rendimento-Cut", "Estrapolo String da pos1="+pos1 + " a " + (pos1+pos2));
		pos1 = html.indexOf(paletto1) + paletto1.length();
		pos2 = html.substring(pos1).indexOf(paletto2);
		rendimento[4][0] = frasi[4];
		rendimento[4][1] = html.substring(pos1, pos1+pos2);
		rendimento[4][1] = rendimento[4][1].replace(" ", "");
		rendimento[4][1] = rendimento[4][1].replace("\t", "");
		rendimento[4][1] = rendimento[4][1].replace("\n", "");
		if(global.debug) Log.v("Rendimento-Val", frasi[4] + " -> " + rendimento[4][1]);
    	global.setRendimento(rendimento);
    	
    }
    
    public void getDettagliPersonali() throws ClientProtocolException, IOException
    {
    	try
    	{
    		HttpGet httpget = new HttpGet(global.getURL("dati_personali"));
            HttpResponse response = httpclient.execute(httpget);
            String  str = inputStreamToString(response.getEntity().getContent()).toString();
            estrapolaDettagliPersonali(str);
    		
        } catch (ClientProtocolException e) {
        	if(global.debug) Log.v("ERRORE", "Errore " + e.toString());
        	e.printStackTrace();
        } catch (IOException e) {
        	if(global.debug) Log.v("ERRORE", "Errore " + e.toString());
        	e.printStackTrace();
        }
    }
    
    public void estrapolaDettagliPersonali(String html)
    {
    	String tmp, backup = html;
    	int pos1, pos2;
    	
    	String inizio[] = {"Cognome", 
    					   "Nome",
    					   "Codice Fiscale",
    					   "Data di Nascita",
    					   "Comune di Nascita",
    					   "Provincia",
    					   "", // Cellulare
    					   "", // Email
    					   "Indirizzo", 
    					   "Comune",
    					   "Provincia",
    					   "CAP",
    					   "", // Telefono
    					   "Indirizzo", 
    					   "Comune",
    					   "Provincia",
    					   "CAP",
    					   "", // Telefono
    					   "Matricola",
    					   "Passaggio di corso effettuato l' AAA", // OPZIONALE: NON TUTTI CE L'HANNO
    					   "Facolt&agrave; di provenienza",        // OPZIONALE: NON TUTTI CE L'HANNO
    					   "Corso di provenienza",                 // OPZIONALE: NON TUTTI CE L'HANNO
    					   "Facolt&agrave;",
    					   "Corso",
    					   "Tipologia",
    					   "Sede del Corso",
    					   "Codice Corso",
    					   "Anno di Corso",
    					   "AA immatricolazione",
    					   "AA ultima iscrizione"
    					  };
    	String s2 = "class=\"dati\" bgcolor=\"#FFFFFF\">";
    	
    	for(int i=0; i<inizio.length; i++)
    	{
    		if(inizio[i].length() > 0)
    		{
    			pos1 = html.indexOf(inizio[i]);
    			if(pos1 != -1)
    			{
    			   pos2 = html.substring(pos1).indexOf(s2) + s2.length();
    			   //Log.v("Debug", "Inizio taglio = " + pos1+pos2);
    			   //Log.v("Debug", "Fine taglio = " + pos1+pos2+html.substring(pos1+pos2).indexOf("</td>"));
    			   tmp  = html.substring(pos1+pos2, pos1+pos2+html.substring(pos1+pos2).indexOf("</td>"));
    			   if(global.debug) Log.v("Dettaglio", inizio[i] + " -> " + tmp);
    			   global.setDettaglioPersonale(i, tmp);
    			   html = html.substring(pos1+pos2+html.substring(pos1+pos2).indexOf("</td>"));
    			}
    			else {
    				 tmp = "";
    				 Log.v("Dettaglio", inizio[i] + " -> " + tmp);		
    				 global.setDettaglioPersonale(i, tmp);
    			}
    		}
    	}
    	
    	/* Prendo quelli mancanti:
    		[6]  -> Cellulare
    		[7]  -> E-mail
    		[12] -> Telefono
    		[17] -> Telefono
    	*/
    	
    	int idMancanti[] = {6,7,12,17};
    	String inizio2[] = {"Cellulare", "E-Mail", "Telefono", "Telefono"};
    	html = backup;
    	s2 = "value=\"";
    	s2 = "bgcolor=\"#FFFFFF\">";
    	for(int i=0; i<inizio2.length; i++)
    	{
    		pos1 = html.indexOf(inizio2[i]);
    		pos2 = html.substring(pos1).indexOf(s2) + s2.length();
    		tmp  = html.substring(pos1+pos2, pos1+pos2+html.substring(pos1+pos2).indexOf("</td>"));
    		if(global.debug) Log.v("Dettaglio", inizio2[i] + " -> " + tmp);
    		global.setDettaglioPersonale(idMancanti[i], tmp);
    		html = html.substring(pos1+pos2+html.substring(pos1+pos2).indexOf("\""));

    	}
    	
    }
    
    public void getDettagliPrenotazioni(int position)
    {
    	
    	    if(global.debug) Log.v("Dettagli Prenotazione", "Richiesta di dettaglio per prenotazione["+position+"]");
    		int dim = global.getNumCampiPrenotazione();
    		if(global.debug) Log.v("Dim ", "dim campi prenotazioni = " + dim);
    		
    		String ordine = global.getPrenotazioniCol(dim-1)[position];
    		String idPrenotazione = global.getPrenotazioniCol(dim-3)[position];
    		String attiva = global.getPrenotazioniCol(dim-2)[position];
    		if(attiva.equals("attiva")) attiva = "1";
    		else attiva = "0";
    		// Prendo i dettagli delle prenotazioni
                		
    		String textTochange = "Lettura.. Dettagli prenotazione [" + position + "]";
            Message msg = new Message();
        	msg.obj = textTochange;
        	mHandler.sendMessage(msg);
        	
    		httpRequest.purgeParamsList();
            httpRequest.setURL(global.getURL("dettagli_prenotazioni"));
            httpRequest.setMethod("POST");
            httpRequest.addParam("attiva", attiva);
            httpRequest.addParam("ordine", ordine);
            httpRequest.addParam("idPrenotazioneSeguita", idPrenotazione);
            httpRequest.addParam("indexPrenotazione", ""+position);
            httpRequest.addParam("azione", "Dettagli");
            httpRequest.addParam("com_doc", "");
            /*
            Log.v("Parametri", "attiva = " + attiva);
            Log.v("Parametri", "ordine = " + ordine);
            Log.v("Parametri", "idPrenotazioneSeguita = " + idPrenotazione);
            Log.v("Parametri", "indexPrenotazione = " + position);
            Log.v("Parametri", "azione = Dettagli");
            */
            AsyncHttp asyncHttp = new AsyncHttp();
            asyncHttp.execute(httpRequest);
            global.waitAsyncTask(asyncHttp, 10);
            
            String response = httpRequest.getResponse();
    		estrapolaDettagliPrenotzione(position, response);
    }
    
    public void resettaHttpClientPerDettagli()
    {
    	String url = "https://delphi.uniroma2.it/totem/jsp/prenotazioni/dettagliPrenotazione.jsp?Indietro=si&pagina=null";
    	httpRequest.setURL(url);
    	httpRequest.setMethod("GET");
    	AsyncHttp asyncHttp = new AsyncHttp();
    	asyncHttp.execute(httpRequest);
    	global.waitAsyncTask(asyncHttp, 10);
    }
    
    public void estrapolaDettagliPrenotzione(int position, String html)
    {
    	//Log.v("--------------------", "-------------------------------------"+html);
    	String tmp;
    	int pos1, pos2;
    	int dim = global.getNumCampiDettagliPrenotazione();
    	String dettagli[] = new String[dim];
    	
    	String inizio[] = {"orario", "inizio prenotazione", "modalita", "ciclo", "edificio", "aula", "stato prenotazione", "comunicazioni"};
    	String s2 = "align=\"left\"><b>";
    	
    	for(int i=0; i<inizio.length; i++)
    	{
    		pos1 = html.indexOf(inizio[i]);
    		if(global.debug) Log.v("Debug", "pos1 = " + pos1);
    		pos2 = html.substring(pos1).indexOf(s2) + s2.length();
    		if(global.debug) Log.v("Debug", "pos2 = " + pos2);
    		tmp  = html.substring(pos1+pos2, pos1+pos2+html.substring(pos1+pos2).indexOf("</b>"));
    		if(global.debug) Log.v("Dettaglio", inizio[i] + " -> " + tmp);
    		dettagli[i] = tmp;
    		global.setCampoDettagliPrenotazione(position, i, tmp);
    	}
    	
    }
    
    private StringBuilder inputStreamToString(InputStream is) {
     String line = "";
     StringBuilder total = new StringBuilder();
     // Wrap a BufferedReader around the InputStream
     BufferedReader rd = new BufferedReader(new InputStreamReader(is));
     // Read response until the end
     try {
      while ((line = rd.readLine()) != null) { 
        total.append(line); 
      }
     } catch (IOException e) {
      e.printStackTrace();
     }
     // Return full string
     return total;
    }
 
    public void getPrenotazioni(String html)
    {
    	if(global.debug) Log.v("Prenotazioni", "Inizio");
    	List<List<String>> prenotazioni = new ArrayList<List<String>>();
    	
    	String s = "<form method=",s2 = "Ristampa";
    	String tmp;    	
    	int pos1,pos2,i=0;
    	if(global.debug) Log.v("Prenotazioni", "primapos= " + html);
    	if(html.length() > 0)
    	{
    		while(html.indexOf(s) >= 0)
    		{
    			pos1 = html.indexOf(s);
    			pos2 = html.substring(pos1).indexOf(s2);
    			tmp = html.substring(pos1, pos1+pos2);
    			prenotazioni.add(estrapolaPrenotazione(tmp));
    			i++;
    			if(global.debug) Log.v("Prenotazioni", "Trovata! " + i);
    			html = html.substring(pos2);
    			
    		}
    	}
    	if(global.debug) Log.v("Prenotazioni", "Numero di prenotazioni = " + i);
    	global.setNumPrenotazioni(i);
    	for(int z=0; z<prenotazioni.size(); z++)
    	{
    		for(int k=0; k<prenotazioni.get(0).size(); k++)
    		{
    			global.setCampoPrenotazione(z, k, prenotazioni.get(z).get(k));
    			
    		}
			getDettagliPrenotazioni(z);
			resettaHttpClientPerDettagli();
			
    	}
    	
    	// Testing
    	String debug[][] = global.getPrenotazioniMatrice();
    	for(int z=0;  z<debug.length; z++)
    	{
    		for(int k=0; k<debug[0].length; k++)
    		{
    			if(global.debug)
    				Log.v("Prenotazioni", "[" + z + "][" + k + "] -> " + debug[z][k]);
    		}
    	}
    }
    
    public ArrayList<String> estrapolaPrenotazione(String htmlRow)
    {
    	String backup = htmlRow;
    	int dim = global.getNumCampiPrenotazione();
    	String[] prenotazione = new String[dim];
    	int i;
    	String s = "<td style=\"text-align:left\" class", s2 = "</b></td>", s1 = "<b>", s3 = "<input", tmp;
    	int pos1, pos2;
    	for(i=0; i < dim-2; i++)
    	{
    		//Log.v("DEBUG " + i, htmlRow);
    		pos1 = htmlRow.indexOf(s);
    		if(i==7)
    			pos2 = htmlRow.substring(pos1).indexOf(s3);
    		else
    			pos2 = htmlRow.substring(pos1).indexOf(s2);
    		tmp = htmlRow.substring(pos1, pos1+pos2);
    		tmp = tmp.substring(tmp.indexOf(s1)+s1.length());
    		if(tmp.trim().length() > 100) prenotazione[i] = "Errore di lettura!";
    		else prenotazione[i] = tmp.trim();
    		if(global.debug) Log.v("Prenotazione", "Valore ["+i+"]-> " +prenotazione[i] );
    	
    		htmlRow = htmlRow.substring(pos1+pos2);
    	}
    	htmlRow = backup;
    	//Log.v("nue", htmlRow);
    	i= dim-3;
    	s = "idPrenotazioneSeguita\" value=\"";
    	tmp = htmlRow.substring(htmlRow.indexOf(s)+s.length());
    	tmp = tmp.substring(0, tmp.indexOf("\""));
    	prenotazione[i] = tmp;
    	if(global.debug) Log.v("Prenotazione", "Valore ["+i+"]-> " + prenotazione[i]);
    	
    	
    	// Prendo l'id della prenotazione
    	i = dim-2;
    	if(htmlRow.indexOf("green") > 0)  prenotazione[i] = "attiva";
    	else if(htmlRow.indexOf("red") > 0)  prenotazione[i] = "disattiva";
    	else prenotazione[i] = "Errore";
    	if(global.debug) Log.v("Prenotazione", "Valore ["+i+"]-> " + prenotazione[i]);
    	
    	// Prendo l'ordine della prenotazione
    	i= dim-1;
    	s = "ordine\" value=\"";
    	tmp = htmlRow.substring(htmlRow.indexOf(s)+s.length());
    	tmp = tmp.substring(0, tmp.indexOf("\""));
    	prenotazione[i] = tmp;
    	if(global.debug) Log.v("Prenotazione", "Valore ["+i+"]-> " + prenotazione[i]);
    	
    	// Per inviare il form (annulla etc) devo salvarmi:
    	// idPrenotazioneSeguita, indexPrenotazione
    	// Le azioni sono: azione={Dettagli, Cancella, Ristampa
    	ArrayList<String> res = new ArrayList<String>();
    	for(int k=0; k<dim; k++)
    	{
    		if(global.debug) Log.v("Copia", "dim="+k+" valore->"+prenotazione[k]);
    		res.add(prenotazione[k]);
    	}
    	return res;
    }
    
    public String getStudentName(String html)
    {
    	String nome = "";
    	if(html.length() > 0)
    	{
    		int pos1 = html.indexOf("<B>Studente</B>: ");
    		int pos2 = html.substring(pos1).indexOf("<br>");
    		nome = html.substring(pos1+17, pos1+pos2);
    		global.setNomeStudente(nome);
    	}
    	return nome;
    }
    
    public String getCorsoDiLaurea(String html)
    {
    	String corso = "";
    	if(html.length() > 0)
    	{
    		int pos1 = html.indexOf("<B>Corso di Laurea</B>: ");
    		int pos2 = html.substring(pos1).indexOf("</td>");
    		corso = html.substring(pos1+24, pos1+pos2);
    		global.setCorso(corso);
    	}
    	return corso;
    }
    
    public void getEsamiVerbalizzati(String html)
    {
    	if(global.debug) Log.v("Esami verbalizzati" , "entro in esami verbalizzati");
    	String s,s2;
    	String tmp;
    	if(html.length() > 0)
    	{
    		s = "Esami validi</B>:</B> ";
    		int pos1 = html.indexOf(s);
    		int pos2 = html.substring(pos1).indexOf("<br>");
    		tmp = html.substring(pos1+s.length(), pos1+pos2);
    		int numEsami = Integer.parseInt(tmp);
    		global.setNumEsamiValidi(numEsami);
    		
    		// Ora so quanti esami ha fatto lo studente.
    		// Dall'html totale estrapolo il blocco delle righe della tabella
    		s = "Data Orig.</font></td>";
    		s2 = "riepilogo asinistra";
    		pos1 = html.indexOf(s);
    		pos2 = html.substring(pos1).indexOf(s2);
    		String htmlNew = html.substring(pos1+s.length()+10, pos1+pos2-25);
    		//Log.v("INFO", "HTML -> " + htmlNew);
    		String campo;
    		s = "esamidispari";
    		int i = 0; // Contatore riga (esame)
    		int j = 0; // Contatore colonna (campo)
    		//while(htmlNew.indexOf("<tr>") >= 0)
    		while(i<numEsami)
    		{
    			if(global.debug) Log.v("COLH", htmlNew);
    			if(global.debug) Log.v("COLH", "########################################");
    			campo = estrapolaColonna(htmlNew);
    			global.setCampoEsameVerbalizzato(i, j, campo);
    			if(global.debug) Log.v("INFO", campo);
    			htmlNew = htmlNew.substring(htmlNew.indexOf("</td>") + 5);
    			j++;
    			if(j==global.getColonneEsami()) {i++; j=0; }
    		}
    	}
    }
    
    public String estrapolaColonna(String rigaHtml)
    {
    	try
    	{
    	String s = "pari\">";
    	String s2 = "</td>";
		int pos1 = rigaHtml.indexOf(s);
		int pos2 = rigaHtml.substring(pos1).indexOf(s2);
		if(global.debug) Log.v("EstrapolaColonna", "pos1="+pos1 + ", pos2="+pos2);
		return rigaHtml.substring(pos1+s.length(), pos1+pos2);
    	}
    	catch (Exception e)
    	{
    		return "";
    	}
    }
    
    public void toggleStatoLoginButton(boolean status)
    {
    	Button loginButton = (Button) findViewById(R.id.btn_login);
    	loginButton.setEnabled(status);
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