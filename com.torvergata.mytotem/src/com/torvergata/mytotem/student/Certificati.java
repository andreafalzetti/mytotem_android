package com.torvergata.mytotem.student;

import java.io.File;

import org.apache.http.client.HttpClient;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.torvergata.mytotem.MyTotem;
import com.torvergata.mytotem.R;
import com.torvergata.mytotem.R.id;
import com.torvergata.mytotem.R.layout;
import com.torvergata.mytotem.R.menu;
import com.torvergata.mytotem.http.AsyncHttp;
import com.torvergata.mytotem.http.AsyncHttpDownloader;
import com.torvergata.mytotem.http.HttpFile;
import com.torvergata.mytotem.http.HttpRequestObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Certificati extends Activity {
  
	private LinearLayout linearLayout;
	private AdView adView;
	private MyTotem global;
	private ProgressDialog pDialog;
	private File f;
	private String filename;
	 	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.certificati);     
        linearLayout = (LinearLayout)findViewById(R.id.row1);
        global = ((MyTotem) this.getApplication()); 

        
        TextView titolo = (TextView) findViewById(R.id.tipoCertificato);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        titolo.setTypeface(face);
        
        // Create the adView
        if(global.ADV)
        	global.draw_Advertising(this, linearLayout);
    }
    
    public void scaricaCertificato(View v)
    {
    	pDialog = new ProgressDialog(Certificati.this);
		pDialog.setTitle("Caricamento");
		pDialog.setMessage("Download certificato in corso...");
		pDialog.show();
		
        new Thread() 
        {
          @Override
          public void run() 
          {
        	  getCertificato();
        	  Message msg = new Message(); // Messaggio per progressDialog
        	  String textTochange;
        	  textTochange = "fine";
        	  msg = new Message();
        	  msg.obj = textTochange;
        	  mHandler.sendMessage(msg);
          }
        }.start();  
        
    }
    
    public void getCertificato()
    {        
    	filename = "Richiesta_Certificato";
    	CheckBox tipo3 = (CheckBox) findViewById(R.id.chkTipo3);
    	CheckBox tipo2 = (CheckBox) findViewById(R.id.chkTipo2);
    	CheckBox tipo1 = (CheckBox) findViewById(R.id.chkTipo1);
    	CheckBox tipo0 = (CheckBox) findViewById(R.id.chkTipo0);
    	CheckBox chkApri = (CheckBox) findViewById(R.id.apri);
    	CheckBox chkSalva = (CheckBox) findViewById(R.id.salva);
    	
    	boolean t0 = tipo0.isChecked(),
    			t1 = tipo1.isChecked(),
    			t2 = tipo2.isChecked(),
    			t3 = tipo3.isChecked(),
    			apri = chkApri.isChecked(),
    			salva = chkSalva.isChecked();
    	
    	String generator = "https://delphi.uniroma2.it/totem/jsp/studenti/richiestaCertificati/indexCertificati.jsp?motivo=-1&richiedi=Avanti&bollo=1&lingua=1";
    	if(t0) { generator +=  "&tipoCertificato=0"; filename += "_0"; }
    	if(t1) { generator +=  "&tipoCertificato=1"; filename += "_1"; }
    	if(t2) { generator +=  "&tipoCertificato=2"; filename += "_2"; }
    	if(t3) { generator +=  "&tipoCertificato=3"; filename += "_3"; }
    	
    	filename += ".pdf";
    	
    	if(t0 || t1 || t2 || t3)
    	{
    		// Eseguo il login
    		// Almeno uno
    		// LOGIN
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
            	httpClient = httpRequest.getHttpClient();
            	f = new File(global.getAppDir() + "/" + filename);
            	HttpFile hf = new HttpFile(generator, httpClient, f);
            	AsyncHttpDownloader AHD = new AsyncHttpDownloader();
            	AHD.execute(hf);
            	global.waitAsyncTask(AHD, 10);
            	
            	textTochange = "FILE-DOWNLOADED";
                msg = new Message();
            	msg.obj = textTochange;
            	mHandler.sendMessage(msg);
            	STOP = true;
            	
            	if(salva)
            	{
            		textTochange = "upload";
                    msg = new Message();
                	msg.obj = textTochange;
                	mHandler.sendMessage(msg);
            	}
            		
            	if(apri)
            	{
            		textTochange = "open";
                    msg = new Message();
                	msg.obj = textTochange;
                	mHandler.sendMessage(msg);
            	}
            }
            else
            {
            	Toast.makeText(getApplicationContext(), "Attenzione - Login error", Toast.LENGTH_SHORT).show();
            }
    	}
    	else
    	{
    		// Controllare i dati immessi
    		Toast.makeText(getApplicationContext(), "Attenzione - Selezionare almeno un tipo di certificato. E' possibile selezionarne anche più d'uno.", Toast.LENGTH_SHORT).show();
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
   
   @Override
   public void onBackPressed() {
	   Certificati.this.finish();
   }
   
   @Override
   public void onDestroy() {
     if (adView != null) {
       adView.destroy();
     }
     super.onDestroy();
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
	        	Toast.makeText(Certificati.this, "Impossibile aprire: nessun lettore pdf installato sul dispositivo.", Toast.LENGTH_LONG).show(); 
	        }
	    }
		else
		{
			Log.v("Apro-PDF", "Non esiste il PDF -> "+ pdfFile.toString());
		}
   }
   
   Handler mHandler = new Handler() {
       @Override
       public void handleMessage(Message msg) {
       	
           String text = (String)msg.obj;
           Log.v("MESSAGGIO RICEVUTO", text);
           if(text.equals("FILE-DOWNLOADED"))
           {
           		pDialog.dismiss();
           }
           else if(text.equals("LOGIN-FAIL"))
           {
           		pDialog.dismiss();
           		Log.v("User login", "Login Failed");
           		Toast.makeText(getApplicationContext(), "Non è stato possibile eseguire il login. Ricontrollare Matiricola e Password. Ricordiamo che nella fascia d'orario 00:00 1:30 il Totem non è raggiungibile.", Toast.LENGTH_LONG).show();
           }
           else if(text.equals("upload"))
           {
       			// Share
       			Uri uri = Uri.parse(f.toString());
       			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
       			sharingIntent.setType("text/pdf");
       			sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
       			startActivity(Intent.createChooser(sharingIntent,"Invia PDF.."));
           }
           else if(text.equals("open"))
           {
        	   // Apri
        	   openPDF(filename); 
           }
           else         
           {
           		Log.v("Cambio pDialog", "Message->"+text);
           		pDialog.setMessage(text);
           }
           	
       }
   };
}