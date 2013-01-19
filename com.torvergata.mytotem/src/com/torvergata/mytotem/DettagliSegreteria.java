package com.torvergata.mytotem;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DettagliSegreteria extends Activity {
	
	MyTotem global;
	Typeface face;
	TextView campi[];
	LinearLayout linearLayout;
	private AdView adView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.dettaglio_segreteria);
        Bundle extras = getIntent().getExtras();
        global = ((MyTotem) this.getApplication());
        
        linearLayout = (LinearLayout)findViewById(R.id.rowAD);        
        // Create the adView
        adView = new AdView(this, AdSize.SMART_BANNER, global.admobID);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        adView.setLayoutParams(lp);
        linearLayout.addView(adView);
        AdRequest adRequest = new AdRequest();
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addTestDevice(global.deviceID);
        adView.loadAd(adRequest);
        
        String nomeSegreteria = extras.getString("nomeSegreteria");  
        if(global.debug) Log.v("DettaglioSegreteria", "nome segreteria = " + nomeSegreteria);
        String segreteria[] = global.getSegreteriaByName(nomeSegreteria);    
        for(int i=0; i<segreteria.length; i++)
        {
        	Log.v("Dettaglio Segreteria", i + "] -> "+segreteria[i]);
        }
        
        int idTextView[] = {R.id.nomeSegreteria, R.id.orario, R.id.tel1,  R.id.tel2,  R.id.tel3,  R.id.tel4, R.id.fax, R.id.email, R.id.responsabile, R.id.indirizzo };
        TextView t;
        
        for(int i=0; i<idTextView.length; i++)
        {
        	t = getTV(idTextView[i]);
        	t.setText(segreteria[i]);
        }    
        
        setVal(R.id.nomeSegreteria,  "", 				segreteria[0], true);
        setVal(R.id.orario,          "Orario: ", 		segreteria[1]);
        setVal(R.id.tel1,            "Tel: ", 			segreteria[2]);
        setVal(R.id.tel2,            "Tel: ", 			segreteria[3]);
        setVal(R.id.tel3,            "Tel: ", 		    segreteria[4]);
        setVal(R.id.tel4,            "Tel: ", 			segreteria[5]);
        setVal(R.id.fax,             "Fax: ", 			segreteria[6]);
        setVal(R.id.email,           "E-Mail: ", 		segreteria[7]);
        setVal(R.id.responsabile,    "Responsabile: ",  segreteria[8]);
        setVal(R.id.indirizzo,       "Indirizzo: ", 	segreteria[9]); 
        
        
        TextView tv = ((TextView) findViewById(R.id.map));
        if(segreteria[9].equals(""))
        {
        	tv.setVisibility(View.GONE);      			
        }
        else
        {
        	Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
        	tv.setTypeface(face);
        	
        }
        
	}
	
	
	@Override
	public void onBackPressed() {
		DettagliSegreteria.this.finish();
	}
    
    private TextView getTV(int id)
    {
    	return (TextView) findViewById(id);
    }
    
	public void setVal(int campo, String frase, String value, boolean titolo)
	{
		TextView txt = (TextView) findViewById(campo);
		txt.setText(frase+value);
		if(titolo) face=Typeface.createFromAsset(getAssets(),"fonts/Chunkfive.otf");
		else	   face=Typeface.createFromAsset(getAssets(),"fonts/Aller_Bd.ttf");
		txt.setTypeface(face);
		
		if(!titolo)
		{
			Spannable WordtoSpan = new SpannableString(frase+value);      
			WordtoSpan.setSpan(new ForegroundColorSpan(global.bordo), 0, frase.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			txt.setText(WordtoSpan);
		}
		
		if(value.equals(""))
		{
			txt.setVisibility(View.GONE);
		}
	}
	
	
	public void setVal(int campo, String frase, String value)
	{
		setVal(campo, frase, value, false);
	}
	
	public void myClickHandler(View v)
	{
		final int id = v.getId();
		
		if(id == R.id.tel1 || id == R.id.tel2 || id == R.id.tel3 || id == R.id.tel4)
		{
			String value = getTV(id).getText().toString();
   			value = value.substring(2+value.indexOf(":"));
   			value.replace(" ", "");
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Conferma")
	        .setMessage("Chiamare " + value + "?")
	        .setPositiveButton("Si, chiama", new DialogInterface.OnClickListener()
	        {     
	     	   public void onClick(DialogInterface dialog, int which) {
	     		// Call
	   			String value = getTV(id).getText().toString();
	   			value = value.substring(2+value.indexOf(":"));
	   			value.replace(" ", "");
	   			Log.v("Chiamo segreteria", "Numero = " + value);
	   			global.phoneCall(value);
	     	   }
	        })
	    .setNegativeButton("No", null)
	    .show();
			
		}
		else if(id == R.id.email)
		{
			String value = getTV(id).getText().toString();
			value = value.substring(2+value.indexOf(":"));
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Conferma")
	        .setMessage("Scrivere un'email a " + value + "?")
	        .setPositiveButton("Si", new DialogInterface.OnClickListener()
	        {     
	     	   public void onClick(DialogInterface dialog, int which) {
	     		// Send Email
	   			String value = getTV(id).getText().toString();
	   			global.sendEmail(value);
	     	   }
	        })
	    .setNegativeButton("No", null)
	    .show();
			
		}
		else if(id == R.id.map)
		{
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Conferma")
	        .setMessage("Raggiungere l'indirizzo con Google Maps?")
	        .setPositiveButton("Si, vai!", new DialogInterface.OnClickListener()
	        {     
	     	   public void onClick(DialogInterface dialog, int which) {
	     		// Show Google Maps
	   			String value = getTV(R.id.indirizzo).getText().toString();
	       		GeoPoint coordinate_destinazione = global.coordinatesFromAddress(value);
	       		if(coordinate_destinazione != null)
	       		{
	         		double lat = coordinate_destinazione.getLatitudeE6() / 1E6;
	         		double lon = coordinate_destinazione.getLongitudeE6() / 1E6;
	         		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon)); //saddr=41.863344,12.54958&
	         		startActivity(i);
	       		}
	       		else
	       		{
	       			Log.v("Errore Mappa", "Coordinate non trovate!");
	       			// Cerco di nuovo
	       			value = getTV(R.id.indirizzo).getText().toString();
	       			value = value.substring(value.indexOf("Via"), value.indexOf("Roma")+4);
	       			coordinate_destinazione = global.coordinatesFromAddress(value);
	       			if(coordinate_destinazione != null)
		       		{
		         		double lat = coordinate_destinazione.getLatitudeE6() / 1E6;
		         		double lon = coordinate_destinazione.getLongitudeE6() / 1E6;
		         		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon)); //saddr=41.863344,12.54958&
		         		startActivity(i);
		       		}
		       		else
		       		{
		       			Log.v("Errore Mappa", "Coordinate non trovate per la seconda volta!");
		       			Toast.makeText(getApplicationContext(), "Destinazione non raggiungibile tramite questa funzione, le coordinate non sono state trovate.", Toast.LENGTH_SHORT).show();
		       			
		       		}
	       			
	       		}

	     	   }
	        })
	    .setNegativeButton("No", null)
	    .show();
	        
			
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
	
}
