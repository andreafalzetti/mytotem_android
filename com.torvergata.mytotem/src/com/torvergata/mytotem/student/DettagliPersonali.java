package com.torvergata.mytotem.student;

import java.io.File;

import org.apache.http.client.HttpClient;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.ShowAnim;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;
import com.torvergata.mytotem.R.style;
import com.torvergata.mytotem.http.AsyncHttp;
import com.torvergata.mytotem.http.AsyncHttpDownloader;
import com.torvergata.mytotem.http.HttpFile;
import com.torvergata.mytotem.http.HttpRequestObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DettagliPersonali extends Activity
{
	
	private MyTotem global;
	private Animation ani;
	private int Height1 = 0,
		Height2 = 0,
		Height3 = 0,
		Height4 = 0;
	
	private boolean current1 = true,
					current2 = true,
					current3 = true,
					current4 = true;
	private ProgressDialog pDialog;
	private String nomeFileDomanda = "domanda_immatricolazione.pdf";
	private boolean available = false;
	private LinearLayout linearLayout;
	private AdView adView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dettagli_personali); 
        //Bundle extras = getIntent().getExtras();
                
        global = ((MyTotem) this.getApplication()); 
                
        // Create the adView
        linearLayout = (LinearLayout)findViewById(R.id.rowAD);
        
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
        
        
        for(int i=0; i<global.getNumCampiDettagliPersonali(); i++)
        {
        	if(global.debug) Log.v("Dettagli-Personali", "Verifica ["+i+"]" + global.getDettaglioPersonale(i));
        }
        View linearLayout =  findViewById(R.id.row1_hidden);
        
        TextView heightTest = new TextView(this);
        heightTest.setText("HeightTest");
        heightTest.setId(5);
        heightTest.measure(0, 0);
        Height1 += heightTest.getMeasuredHeight() * 8;
        Height1 += Height1 / 100 * 15; // + 10% !
        
        Height2 += heightTest.getMeasuredHeight() * 5;
        Height2 += Height2 / 100 * 15; // + 10% !
        
        Height3 = Height2;
        
        Height4 += heightTest.getMeasuredHeight() * 13;
        Height4 += Height4 / 100 * 15; // + 10% !
        
        if(global.debug) Log.v("Altezza", "H1 = " + Height1);
        if(global.debug) Log.v("Altezza", "H2 = " + Height2);
        if(global.debug) Log.v("Altezza", "H3 = " + Height3);
        if(global.debug) Log.v("Altezza", "H4 = " + Height4);
        
        // Inserisco tutte le textview
    	String frasi[] = {"Cognome: ", 
				   		  "Nome: ",
						  "Codice Fiscale: ",
						  "Data di Nascita: ",
						  "Comune di Nascita: ",
						  "Provincia: ",
						  "Cellulare: ",
						  "E-Mail: ",
						  "Indirizzo: ", 
						  "Comune: ",
						  "Provincia: ",
						  "CAP: ",
						  "Telefono: ",
						  "Indirizzo: ", 
						  "Comune: ",
						  "Provincia: ",
						  "CAP: ",
						  "Telefono: ",
						  "Matricola: ",
						  "AA Passaggio: ",
   					   	  "EX Facoltà: ",
   					      "EX Corso: ",
						  "Facoltà: ",
						  "Corso: ",
						  "Tipologia: ",
						  "Sede del Corso: ",
						  "Codice Corso: ",
						  "Anno di Corso: ",
						  "AA immatricolazione: ",
						  "AA ultima iscrizione: "
				  		};
    	
    	TextView txtTv;
    	String v;
    	int idLayout; 
        for(int i=0; i<frasi.length; i++)
        {
        	if(global.getDettaglioPersonale(i).length() > 0)
        	{
        		v = frasi[i] + global.getDettaglioPersonale(i);
        		txtTv = new TextView(this);
        		txtTv.setId(i);
        		txtTv.setTextAppearance(getApplicationContext(), R.style.whiteSmall);
        		Spannable WordtoSpan = new SpannableString(v);      
				WordtoSpan.setSpan(new ForegroundColorSpan(global.bordo), 0, frasi[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				txtTv.setText(WordtoSpan);
				txtTv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        	
        		if(i < 8) 
        			idLayout = R.id.row1_hidden;
        		else if(i < 13)
        			idLayout = R.id.row2_hidden;
        		else if(i < 18)
        			idLayout = R.id.row3_hidden;
        		else
        			idLayout = R.id.row4_hidden;
        	
        		linearLayout = (LinearLayout)findViewById (idLayout);
        		( (LinearLayout)linearLayout).addView(txtTv);
        	}
        }
    }
    
    public void clickHandler(View v)
    {
    	int layoutTarget = 0;
    	int heightTarget = 0;
    	
    	if(v.getId() == R.id.row1){
    		if(global.debug) Log.v("Animazione", "Animazione su Row 1");
    		layoutTarget = R.id.row1_hidden;
    		if(!current1)    
    		{
    			heightTarget = Height1;
    			current1 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current1 = false;
    		}
    		if(global.debug) Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	else if(v.getId() == R.id.row2){
    		if(global.debug) Log.v("Animazione", "Animazione su Row 2");
    		layoutTarget = R.id.row2_hidden;
    		if(!current2)    			
    		{
    			heightTarget = Height2;
    			current2 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current2 = false;
    		}
    		if(global.debug) Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	else if(v.getId() == R.id.row3){
    		if(global.debug) Log.v("Animazione", "Animazione su Row 3");
    		layoutTarget = R.id.row3_hidden;
    		if(!current3)    			
    		{
    			heightTarget = Height3;
    			current3 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current3 = false;
    		}
    		if(global.debug) Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	else if(v.getId() == R.id.row4){
    		if(global.debug) Log.v("Animazione", "Animazione su Row 4");
    		layoutTarget = R.id.row4_hidden;
    		if(!current4)    			
    		{
    			heightTarget = Height4;
    			current4 = true;
    		}
    		else 
    		{
    			heightTarget = 0;	
    			current4 = false;
    		}
    		if(global.debug) Log.v("Animazione", "Target -> " + heightTarget);
    	}
    	
    	if(layoutTarget != 0)
    	{
    		if(global.debug) Log.v("Animazione", "Target -> " + layoutTarget + " - Altezza: "  +heightTarget);
    		LinearLayout l = (LinearLayout)findViewById(layoutTarget);
    		animaLayout(l, heightTarget);
    	}
    }
    
	public void animaLayout(LinearLayout l, int target)
	{
		ani = new ShowAnim(l, target);
		ani.setDuration(800);
		ani.setStartTime(AnimationUtils.currentAnimationTimeMillis());
		l.startAnimation(ani);
		l.invalidate();
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
    
    public void domandaImmatricolazione(View v)
    {
    	pDialog = new ProgressDialog(DettagliPersonali.this);
		pDialog.setTitle("Caricamento");
		pDialog.setMessage("Download domanda di immatricolazione in corso...");
		pDialog.show();
		
        new Thread() 
        {
          @Override
          public void run() 
          {
        	  getDomandaImmatricolazione();
        	  Message msg = new Message(); // Messaggio per progressDialog
        	  String textTochange;
        	  textTochange = "fine";
        	  msg = new Message();
        	  msg.obj = textTochange;
        	  mHandler.sendMessage(msg);
          }
        }.start();  
        
    }
    
    public boolean isAvailable(String response)
    {        
    	// Verifica se è possibile scaricare la domanda di immatricolazione
    	if(response.indexOf("La ricerca ha avuto esito positivo") != -1)
    	{
    		available = true;
    	}
    	else
    	{
    		available = false;
    	}
    	
    	return available;
    }
    
    public void getDomandaImmatricolazione()
    {
    	// Fa il lavoro sporco
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
        global.waitAsyncTask(asyncHttp, 10);
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
        	// Faccio una richiesta get
        	String urlDomanda = global.getURL("domanda-immatricolazione-genera");
        	httpRequest.purgeParamsList();
        	httpRequest.setURL(urlDomanda);
        	httpRequest.setMethod("GET");
        	asyncHttp = new AsyncHttp();
        	asyncHttp.execute(httpRequest);
        	global.waitAsyncTask(asyncHttp, 10); // Timeout 10 seconds
        	response = httpRequest.getResponse();
        	if(isAvailable(response))
        	{
        		// Cerco di scaricare la domanda, 
        		urlDomanda = global.getURL("domanda-immatricolazione");
        		httpClient = httpRequest.getHttpClient();
        		File f = new File(global.getAppDir() + "/" + nomeFileDomanda);
        		HttpFile hf = new HttpFile(urlDomanda, httpClient, f);
        		AsyncHttpDownloader AHD = new AsyncHttpDownloader();
        		AHD.execute(hf);
        		global.waitAsyncTask(AHD, 30);
        	}
        	else
        	{
            	textTochange = "not-available";
                msg = new Message();
            	msg.obj = textTochange;
            	mHandler.sendMessage(msg);
        	}
        	
        }
    }
    
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
            String text = (String)msg.obj;
            if(global.debug) Log.v("MESSAGGIO RICEVUTO", text);
            pDialog.dismiss();
            if(text.equals("fine") && available == true)
            {
				

	        	// Chiedo se vuole aprire o condividere
	        	new AlertDialog.Builder(DettagliPersonali.this)
		           .setIcon(android.R.drawable.ic_dialog_alert)
		           .setTitle("Scegli un azione")
		           .setMessage("La domanda è stata scaricata, come vuoi procedere?")
		           .setPositiveButton("Apri PDF", new DialogInterface.OnClickListener()
		           {     
		        	   
		        	   public void onClick(DialogInterface dialog, int which)
		        	   {
		        		   openPDF(nomeFileDomanda);
		        	   }
		           })
		           .setNegativeButton("Upload / E-Mail", new DialogInterface.OnClickListener()
		           {     
		        	   
		        	   public void onClick(DialogInterface dialog, int which)
		        	   {
		        		   File f = new File(global.getAppDir() + "/" + nomeFileDomanda);
							Uri uri = Uri.parse(f.toString());
		            		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		            		sharingIntent.setType("text/pdf");
		            		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		            		startActivity(Intent.createChooser(sharingIntent,"Invia PDF.."));
		        	   }
		           })
		           .show();
            }
            else if(text.equals("LOGIN-FAIL"))
            {
            	Toast.makeText(getApplicationContext(), "Attenzione - Non è stato possibile reperire la domanda di immatricolazione - problema con il Log-In", Toast.LENGTH_SHORT).show();
            }
            else if(text.equals("not-available"))
            {
            	Toast.makeText(getApplicationContext(), "Attenzione - Domanda di immatricolazione non disponibile", Toast.LENGTH_SHORT).show();
            }
            	
        }
    };
    
    public void openPDF(String filename)
    {
    	File pdfFile = new File(global.getAppDir().toString(), filename);
    	Log.v("PDF", "PDF -> "+ pdfFile.toString());
 		if(pdfFile.exists()) 
 		{
 			
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
 	        	Toast.makeText(DettagliPersonali.this, "Impossibile aprire: nessun lettore pdf installato sul dispositivo.", Toast.LENGTH_LONG).show(); 
 	        }
 	    }
 		else
 		{
 			Log.v("Apro-PDF", "Non esiste il PDF -> "+ pdfFile.toString());
 		}
    }
}