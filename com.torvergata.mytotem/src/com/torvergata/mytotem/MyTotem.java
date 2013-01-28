package com.torvergata.mytotem;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;
import com.torvergata.mytotem.http.AsyncDownloader;
import com.torvergata.mytotem.http.AsyncHttp;
import com.torvergata.mytotem.http.AsyncHttpDownloader;
import com.torvergata.mytotem.student.StudentLogin;

import android.widget.LinearLayout;
import android.widget.Toast;

// Uso questa classe per dichiarare variabili e metodi globali a tutta l'app.

public class MyTotem extends Application {
	//public HttpRequest loginHandler;
	public String AppVersion = "2.3.1"; // Versione dell'App - Serve per mostrare il log delle modifiche
	public boolean debug = false; // Disabilita/Abilita i Log
	public boolean ADV = true; // Disabilita/Abilita la pubblicità all'interno dell'APP
	public String deviceID = "3782c0a039abff53"; // DeviceID di TEST per AdMob
	public String admobID = "a15059c769ecd30"; // AdMob Key	
	public AdSize admobSize = AdSize.SMART_BANNER;
	public int sfondo; /* Usare questa variabile per impostare sfondi */
	public int bordo;
	private String colore 	   = "#008a63";
	private String coloreScuro = "#00573e";
	private boolean forceUpdate = false;
	public boolean hasTelephony;

	// Preferenze
	private SharedPreferences mPreferences;
	private String sharedPreferencesFileName = "com.torvergata.mytotem.preferences";
	private String lastVersionRan;
	
	// Dati presi dal Totem
	private String nomeStudente,
				   matricola,
				   password,
				   corso;
	private int numEsamiValidi;
	private int numEsamiIdonei;
	private String[][] esami_verbalizzati;
	private int colonneCampiEsame = 13;
	
	// Prenotazioni
	@SuppressWarnings("unused")
	private int numPrenotazioni;
	private String[][] esami_prenotati;
	private int colonneCampiEsamiPrenotati = 11;
	// Dettagli prenotazione
	private String[][] dettagli_prenotazioni;
	private int colonneCampiDettagliPrenotazioni = 8;
	// Dati personali
	private String[] dati_personali;
	private int colonneCampiDatiPersonali = 30;
	
	private Map<String, String> SiteURL = new HashMap<String, String>();
	
	
	private String server = "http://mytotem.torengine.it/appcontents";
	
	// URL Remoti
	private String XML_offerta_formativa = "offerta_formativa.xml";
	private String XML_segreterie        = "segreterie.xml";
	private String XML_guide             = "guide_studente.xml";
	private String versionFile           = "mytotem_version";
	
	// PDFs
	private String PDF_guida_immatricolazione    = "guida_immatricolazione_2012_2013.pdf";
	private String PDF_guida_immatricolazione_url    = server +"/pdf/guida_immatricolazione_2012_2013.pdf";
	
    // Path locali
    private File   local_XML_offerta_formativa;
    private File   local_XML_segreterie;
    private File   local_XML_guide;
    private File   localVersionFile;
    private File   localVersionFileTemp;
    private File   appFilesDirectory;
    private File   loginRemFile;
    private String localDirName = "Android/data/com.torvergata.mytotem";
    private String localDirName_OLD = "mytotem";
    public String android_id;
        
    // Local PDFs
    private File   local_PDF_guida_immatricolazione;
    		
	private boolean logged;
	
	// Offerta formativa
	private String[][] OF_facolta;
	
	// Segreterie
	private String[][] SEG_facolta;
	private String[][] SEG_segreterie;
	
	// Totale di corsi per ogni facolta
	private int totcorsi;
	private String OF_corsi[][];
	
	// Guide
	private String[][] GUIDE;
	
	// Rendimento
	private String[][] RENDIMENTO;
	
	// Calendario Esami
	private String appelli[][];
	
    // Struttura del file XML
    private String[] nomiNodiOF = { "nome", "totcorsi" };
    private String[] nomiNodiOFcorsi = { "nomecorso", "descrizione", "obiettivi", "prospettive", "web" };    
    private String[] nomiNodiSEG_facolta = { "nome", "totsegreterie" };    
    private String[] nomiNodiSegreterie = { "nome", "orario", "tel1", "tel2", "tel3", "tel4", "fax", "email", "responsabile", "indirizzo"}; 
    private String[] nomiNodiGuide = {"nome", "dimensione", "filename", "url", "type" }; 
    
    // Menu
    private String InformazioniUtiliMenu[] = {"Guida dello Studente", "Guida all'immatricolazione", "Mensa Universitaria", "Laziodisu", "Tasse - ISEEU"};
    	
    
    // Sezione Docenti e Didattica web
    private String GOOGLE_LUCKY_URL = "http://www.google.com/search?ie=UTF-8&oe=UTF-8&sourceid=navclient&gfns=1&q="; // segue variabile divisa da + Es: mario+abundo+uniroma2
    private Map<String, String> elencoCorsi = new HashMap<String, String>();
    //private String facolta[] = {"Economia", "Giurisprudenza", "Ingegneria", "Lettere e Filosia", "Medicina e Chilurgia", "Scienze MM.FF.NN"};
        
    //public String[] getFacolta() { return facolta; }
    
	public MyTotem() {
		elencoCorsi.put("economia",           "http://www.uniroma2.it/didattica/web/elenco_economia.html");
		elencoCorsi.put("economia-all",       "http://www.uniroma2.it/didattica/web/elenco_economia_all.html");
		elencoCorsi.put("giurisprudenza",     "http://www.uniroma2.it/didattica/web/elenco_giurisprudenza.html");
		elencoCorsi.put("giurisprudenza-all", "http://www.uniroma2.it/didattica/web/elenco_giurisprudenza_all.html");
		elencoCorsi.put("ingegneria",         "http://www.uniroma2.it/didattica/web/elenco_ingegneria.html");
		elencoCorsi.put("ingegneria-all",     "http://www.uniroma2.it/didattica/web/elenco_ingegneria_all.html");
		elencoCorsi.put("lettere",            "http://www.uniroma2.it/didattica/web/elenco_lettere.html");
		elencoCorsi.put("lettere-all",        "http://www.uniroma2.it/didattica/web/elenco_lettere_all.html");
		elencoCorsi.put("medicina",           "http://www.uniroma2.it/didattica/web/elenco_medicina.html");
		elencoCorsi.put("medicina-all",       "http://www.uniroma2.it/didattica/web/elenco_medicina_all.html");
		elencoCorsi.put("scienze",            "http://www.uniroma2.it/didattica/web/elenco_scienze.html");
		elencoCorsi.put("scienze-all",        "http://www.uniroma2.it/didattica/web/elenco_scienze_all.html");

		
		SiteURL.put("login",                            "https://delphi.uniroma2.it/totem/jsp/Iscrizioni/sStudentiLoginIntro.jsp?browser=si");
		SiteURL.put("esami_verbalizzati",               "https://delphi.uniroma2.it/totem/jsp/Iscrizioni/esamiVerbalizzati.jsp");
		SiteURL.put("dati_personali",                   "https://delphi.uniroma2.it/totem/jsp/Iscrizioni/datiStudente.jsp");
		SiteURL.put("prenota-esami1",                   "https://delphi.uniroma2.it/totem/jsp/prenotazioni/preVisualizzaPrenotabiliEmail.jsp");
		SiteURL.put("prenota-esami2",                   "https://delphi.uniroma2.it/totem/jsp/prenotazioni/preVisualizzaPrenotabili.jsp");
		SiteURL.put("prenotazioni",                     "https://delphi.uniroma2.it/totem/jsp/prenotazioni/submenuPrenotazioni.jsp?Entra=visualizzaPrenotazioni.jsp&attiva=si");
		SiteURL.put("dettagli_prenotazioni",            "https://delphi.uniroma2.it/totem/jsp/prenotazioni/visualizzaPrenotazioni.jsp");
		SiteURL.put("insert-ad",                        "http://mytotem.torengine.it/appcontents/index.php");
		SiteURL.put("sito",                             "http://mytotem.torengine.it");
		SiteURL.put("forum",                            "http://mytotem.torengine.it/forum");
		SiteURL.put("facebook",                         "https://www.facebook.com/MyTotemTorVergata");
		SiteURL.put("domanda-immatricolazione",         "https://delphi.uniroma2.it/totem/jsp/stampa.pdf?tipoStampa=5");
		SiteURL.put("domanda-immatricolazione-genera",  "https://delphi.uniroma2.it/totem/jsp/Iscrizioni/StampaDomandaEngine.jsp");
		SiteURL.put("domanda-immatricolazione-check",   "https://delphi.uniroma2.it/totem/jsp/Iscrizioni/StampaDomandaEngine.jsp");
		SiteURL.put("calendario-esami-preparazione-1",  "https://delphi.uniroma2.it/totem/jsp/prenotazioni/menuPrenotazioni.jsp");
		SiteURL.put("calendario-esami-preparazione-2",  "https://delphi.uniroma2.it/totem/jsp/prenotazioni/menuPrenotazioni.jsp?Entra=calendarioEsami.jsp");
		SiteURL.put("calendario-esami",                 "https://delphi.uniroma2.it/totem/jsp/prenotazioni/calendarioEsami.jsp");
		
		
		
		appFilesDirectory = new File(Environment.getExternalStorageDirectory() + "/" + localDirName);
		localVersionFile           = new File(appFilesDirectory, versionFile);
    	localVersionFileTemp       = new File(appFilesDirectory, versionFile+".tmp");
    	local_XML_offerta_formativa = new File(appFilesDirectory, XML_offerta_formativa);
    	local_XML_segreterie  = new File(appFilesDirectory, XML_segreterie);
    	local_PDF_guida_immatricolazione  = new File(appFilesDirectory, PDF_guida_immatricolazione);
    	local_XML_guide  = new File(appFilesDirectory, XML_guide);
    	
		// Colore verde di sfondo comune a tutta l'App
		sfondo = Color.parseColor(colore);
		bordo = Color.parseColor(coloreScuro);
		
		dati_personali = new String[colonneCampiDatiPersonali];
		parseXMLOffertaFormativa();
		parseXMLSegreterie();
		parseXMLGuide();
	}

	public void setCampoEsameVerbalizzato(int row, int col, String value)
	{
		if(debug) Log.v("Inserimento campo", "[" + row + "][" + col + "] -> " + value);
		esami_verbalizzati[row][col] = value;
	}	
	
	public void setNumEsamiValidi(int n)
	{
		numEsamiValidi = n; 
		esami_verbalizzati = new String[n][colonneCampiEsame];
		if(debug) Log.v("Matrice Esami", "DIM = " + n + "x" + colonneCampiEsame);	
	}
	public void setNumIdoneita(int n)
	{
		numEsamiIdonei = n;
	}
	

	public void setDettaglioPersonale(int position, String value)
	{
		if(position >= 0 && position < colonneCampiDatiPersonali)
			dati_personali[position] = value;
	}
	
	public String getDettaglioPersonale(int position)
	{
		if(position >= 0 && position < colonneCampiDatiPersonali)
			return dati_personali[position];
		else
			return "";
	}
	
	public void setCampoPrenotazione(int row, int col, String value)
	{
		if(debug) Log.v("Inserimento campo", "[" + row + "][" + col + "] -> " + value);
		esami_prenotati[row][col] = value;
	}
	
	public void setNumPrenotazioni(int n)
	{
		numPrenotazioni = n; 
		esami_prenotati = new String[n][colonneCampiEsamiPrenotati];
		if(debug) Log.v("Matrice Prenotazioni", "DIM = " + n + "x" + colonneCampiEsamiPrenotati);	
		
		dettagli_prenotazioni = new String[n][colonneCampiDettagliPrenotazioni];
		if(debug) Log.v("Matrice Dettagli Prenotazioni", "DIM = " + n + "x" + colonneCampiDettagliPrenotazioni);	
	}
	
	public void setCampoDettagliPrenotazione(int row, int col, String value)
	{
		if(debug) Log.v("Inserimento campo", "[" + row + "][" + col + "] -> " + value);
		dettagli_prenotazioni[row][col] = value;
	}
	
	public void setRendimento(String r[][])
	{
		RENDIMENTO = r;
	}
	
	public void setNomeStudente(String s) { nomeStudente = s; }
	public void setMatricola(String s)    { matricola = s; }	
	public void setPassword(String s)     { password = s; }
	public void setCorso(String s)        { corso = s; } 
	public void setLoggedState(boolean s) { logged = s; }	
	public void setRemFile(String source) { loginRemFile = new File(appFilesDirectory, MD5(source)); }
	public void setAppelli(String _appelli[][]) { appelli = _appelli; }
	public String getNomeStudente() 	 { return nomeStudente; }	
	public String getMatricola()    	 { return matricola; }	
	public String getPassword()     	 { return password; }	
	public String getCorso()        	 { return corso; }	
	public boolean isLogged()       	 { return logged; }	
	public File     getAppDir()			 { return appFilesDirectory; }
	public int getNumEsamiValidi()       { return numEsamiValidi; }
	public int getNumIdoneita()          { return numEsamiIdonei; }
	public String getURL(String s)       { return (String) SiteURL.get(s); }	
	public int getNumCampiPrenotazione() { return colonneCampiEsamiPrenotati; }
	public int getNumCampiDettagliPrenotazione() { return colonneCampiDettagliPrenotazioni; }
	public int getNumCampiDettagliPersonali() { return colonneCampiDatiPersonali; }
	public String[][] getRendimento() { return RENDIMENTO; }
	public int getColonneEsami() { return colonneCampiEsame; }
	public String[] getFacolta()
	{
		if(debug) Log.v("Debuggo", "Dimensione matrice facolta = " + OF_facolta.length);
		String res[] = new String[OF_facolta.length];
		for(int i=0; i<res.length; i++)
		{
			res[i] = OF_facolta[i][0];
		}
		return res;		
	}
	
	public String[] getSEGFacolta()
	{
		if(debug) Log.v("Debuggo", "Dimensione matrice SEGfacolta = " + SEG_facolta.length);
		String res[] = new String[SEG_facolta.length];
		for(int i=0; i<res.length; i++)
		{
			res[i] = SEG_facolta[i][0];
		}
		return res;		
	}

	public String getFacoltaInfo(int f, String campo)
	{
		int colonna = 0;
		if(campo.equals("nome")) colonna = 0;
		else if(campo.equals("totcorsi")) colonna = 1;
		return OF_facolta[f][colonna];
	}
	
	public String getSEGFacoltaInfo(int f, String campo)
	{
		int colonna = 0;
		if(campo.equals("nome")) colonna = 0;
		else if(campo.equals("totsegreterie")) colonna = 1;
		return SEG_facolta[f][colonna];
	}
	
	public int getNumCampiCorso()
	{
		return nomiNodiOFcorsi.length;
	}
	
	public String[] getCorsoByName(String nomeCorso)
	{
		int dim = getNumCampiCorso();
		String res[] = new String[dim];
		for(int i=0; i<OF_corsi.length; i++)
		{
			if(OF_corsi[i][0].equals(nomeCorso))
			{
				for(int j=0; j<OF_corsi[0].length; j++)
				{
					res[j] = OF_corsi[i][j];
				}
				return res;
			}
		}
		return res;
	}
	
	public String[] getSegreteriaByName(String nomeSegreteria)
	{
		int dim = nomiNodiSegreterie.length;
		String res[] = new String[dim];
		for(int i=0; i<SEG_segreterie.length; i++)
		{
			if(SEG_segreterie[i][0].equals(nomeSegreteria))
			{
				for(int j=0; j<SEG_segreterie[0].length; j++)
				{
					res[j] = SEG_segreterie[i][j];
					if(debug) Log.v("SegreteriaByName", "Valore ["+j+"] -> " + res[j]);
				}
				return res;
			}
		}
		if(debug) Log.v("SegreteriaByName", "Vettore nullo [nome="+nomeSegreteria+"]");
		return res;
	}
	
	public String[] getEsame(int row)
	{
		int dim = esami_verbalizzati[0].length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = esami_verbalizzati[row][i];
		}
		return res;
	}
	
	public String[] getEsamiCol(int col)
	{
		int dim = esami_verbalizzati.length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = esami_verbalizzati[i][col];
		}
		return res;
	}
	
	public String[][] getEsamiMatrice()
	{
		return esami_verbalizzati;
	}
	
	
	public String[] getAppello(int row)
	{
		int dim = appelli[0].length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = appelli[row][i];
		}
		return res;
	}
	
	public String[] getAppelliCol(int col)
	{
		int dim = appelli.length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = appelli[i][col];
		}
		return res;
	}
	
	public String[][] getAppelliMatrice()
	{
		return appelli;
	}
	public String[] getPrenotazione(int row)
	{
		int dim = esami_prenotati[0].length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = esami_prenotati[row][i];
		}
		return res;
	}
	
	public String[] getPrenotazioniCol(int col)
	{
		int dim = esami_prenotati.length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = esami_prenotati[i][col];
		}
		return res;
	}
	
	public String[][] getPrenotazioniMatrice()
	{
		return esami_prenotati;
	}
	
	public String[] getDettagliPrenotazione(int row)
	{
		int dim = dettagli_prenotazioni[0].length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = dettagli_prenotazioni[row][i];
		}
		return res;
	}
	
	public String[] getDettagliPrenotazioniCol(int col)
	{
		int dim = dettagli_prenotazioni.length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = dettagli_prenotazioni[i][col];
		}
		return res;
	}
	
	public String[][] getDettagliPrenotazioniMatrice()
	{
		return dettagli_prenotazioni;
	}
	
	public String[] getCorsi(int facolta)
	{
		// facolta è l'indice di riga della matrice OF_facolta.
		// La seconda colonna di OF_facolta e' il numero di corsi che ha quella facolta.
		// In base alla facolta' richiesta devo trovare il range esatto e restituire un vettore dei soli corsi di quella facolta.
		int counter = 0;
		
		int dim_corsi = Integer.parseInt(getFacoltaInfo(facolta, "totcorsi")); // numero di corsi che dovrò raccogliere
		if(debug) Log.v("CORSI", "Preparo un vettore di output lungo: " + dim_corsi);
		String res[] = new String[dim_corsi];
		for(int i=0; i<facolta; i++)
		{
			counter += Integer.parseInt(getFacoltaInfo(i, "totcorsi")); // sommo il numero di corsi di tutte le facolta precedenti
		}
		int k = 0;
		if(debug) Log.v("CORSI", "TOTALE CORSI: " + OF_corsi.length);
		if(debug) Log.v("CORSI", "Prendo i corsi da: " + counter + " a " + (counter+dim_corsi-1));
		for(int j=counter; j<=counter+dim_corsi-1; j++)
		{
			if(debug) Log.v("CORSI", "Prendo corso: " + j);
			res[k] = OF_corsi[j][0];
			k++;
		}
		return res;
	}
	
	public String[] getSegreterie(int facolta)
	{
		int counter = 0;
		
		int dim_corsi = Integer.parseInt(getSEGFacoltaInfo(facolta, "totsegreterie")); // numero di corsi che dovrò raccogliere
		if(debug) Log.v("CORSI", "Preparo un vettore di output lungo: " + dim_corsi);
		String res[] = new String[dim_corsi];
		for(int i=0; i<facolta; i++)
		{
			counter += Integer.parseInt(getSEGFacoltaInfo(i, "totsegreterie")); // sommo il numero di corsi di tutte le facolta precedenti
		}
		int k = 0;
		if(debug) Log.v("CORSI", "TOTALE Segreterie: " + SEG_segreterie.length);
		if(debug) Log.v("CORSI", "Prendo le Segreterie da: " + counter + " a " + (counter+dim_corsi-1));
		for(int j=counter; j<=counter+dim_corsi-1; j++)
		{
			if(debug) Log.v("Segreterie", "Prendo corso: " + j);
			res[k] = SEG_segreterie[j][0];
			k++;
		}
		return res;
	}
		
	public String[][] getGuide()
	{
		return GUIDE;
	}
	
	public String[] getGuideCol(String s)
	{
		int col = 0;
		for(int j=0; j<nomiNodiGuide.length; j++)
		{
			if(nomiNodiGuide[j].equals(s))
				col = j;
		}
		
		int dim = GUIDE.length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = GUIDE[i][col];
		}
		return res;
	}
	
	public String[] getSegreteria(int row)
	{
		int dim = SEG_segreterie[0].length;
		String res[] = new String[dim];
		for(int i=0; i<dim; i++)
		{
			res[i] = SEG_segreterie[row][i];
		}
		return res;
	}
	
    // Metodo per determinare se l'utente è connesso a internet
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && 
           cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    
    public boolean haveToUpdate()
    {
    	// Versione dei file attuali
    	String currentVersion = getCurrentVersion();
    	// Scarico e leggo la versione dei file sul server
    	download_and_store(versionFile, versionFile+".tmp");
    	String versionControl = readFile(localVersionFileTemp);
    	
    	if(debug) Log.v("Versione", "Versione corrente: "+currentVersion);
    	if(debug) Log.v("Versione", "Versione server: "  +versionControl);
    	if(!currentVersion.equalsIgnoreCase(versionControl))
    	{
    		if(debug) Log.v("Aggiornamento", "L'applicazione richiede un aggiornamento dei contenuti sul server");
    		return true;
    	}
    	else
    	{
    		if(debug) Log.v("Aggiornamento", "Non occorre aggiornamento");
    		return false;
    	}
    }
    
    public void forzaAggiornamento()
    {
    	forceUpdate = true;
    	updateFiles();
    }
    
    public String getCurrentVersion()
    {
    	String currentVersion;
    	if(!localVersionFile.exists())
    	{
    		if(debug) Log.v("Versione", "Il file di versione non esiste sul dispositivo." + localVersionFile.toString());
    		currentVersion = "not-exist"; // Empty string => No version available
    	}
    	else
    	{
    		currentVersion = readFile(localVersionFile);
    	}
    	return currentVersion;
    }
    
    
    public String readFile(File f)
    {
    	//if(debug) Log.v("File", "Lettura file: " + f.toString());
    	StringBuilder text = new StringBuilder();
    	try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(f.toString()));
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	        text.append(line);
    	        text.append('\n');
    	    }
    	}
    	catch (IOException e) {
    	    //You'll need to add proper error handling here
    		if(debug) Log.v("Errore", "Errore nella lettura del file di versione");
    	}
    	//if(debug) Log.v("File", "Il file contiene: "  + text.toString());
    	return text.toString();
    }
    
    
    // Metodo che scarica e aggiorna, se necessario, i files necessari (presentazione, contatti, etc)
    public void updateFiles()
    {
    	
    	if(haveToUpdate() || forceUpdate == true)
    	{
    		
    		if(debug) Log.v("Aggiornamento", "Avvio l'aggiornamento dei files");
    		download_and_store(XML_offerta_formativa, "");
    		download_and_store(XML_segreterie, "");
    		download_and_store(XML_guide, "");
    		//download_and_store(remoteXML_offerta_didattica, "");
    		// Cancello il file di versione attuale e rinomino quello temporaneo
    		localVersionFileTemp.renameTo(new File(getAppDir() + "/" + versionFile));
    		File purgetmp = new File(getAppDir() + "/" + versionFile + ".tmp");
    		if(debug) Log.v("File", "Elimino il .tmp ->"+getAppDir() + "/" + versionFile + ".tmp");
    		purgetmp.delete();
    		parseXMLOffertaFormativa();
    		parseXMLSegreterie();
    		parseXMLGuide();
    		Toast.makeText(getApplicationContext(), "I contenuti sono stati aggiornati", Toast.LENGTH_SHORT).show();
    		
    	}
    }
    
    public void parseXMLGuide()
    {
    	try {
            InputStream is;
            // Estrapolo il nome del file dalla classe globale
            if(debug) Log.v("Parsing-Guide", "Accedo al file: "+XML_guide.toString());
            File file = local_XML_guide;                 
            is = new BufferedInputStream(new FileInputStream(file));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(is); 
            
            NodeList guide = doc.getElementsByTagName("guida");
            int dim_guide = guide.getLength();
            if(debug)Log.v("Parsing-Guide", "Numero di guide: " + dim_guide);
            
            GUIDE = new String[dim_guide][nomiNodiGuide.length];
            for(int i=0; i<dim_guide; i++)
            {
            	if(debug) Log.v("Guide", "Guida -> " + i);
            	for(int j=0; j<nomiNodiGuide.length; j++)
            	{
            		GUIDE[i][j] = getNodeValue(guide, i, nomiNodiGuide[j]);
            		//if(debug)Log.v("Guide", "     ["+j+"] -> "+ GUIDE[i][j]);
            	}
            }
         } catch (Exception e) {
         	if(debug) Log.v("ERRORE XML", "XML Pasing Excpetion = " + e);
         }
    }
       
    public void parseXMLOffertaFormativa()
    {
    	try {
            InputStream is;
            // Estrapolo il nome del file dalla classe globale
            if(debug) Log.v("Parsing-OffertaDidattica", "Accedo al file: "+XML_offerta_formativa.toString());
            File file = local_XML_offerta_formativa;                 
            is = new BufferedInputStream(new FileInputStream(file));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(is); 
            
            NodeList facolta = doc.getElementsByTagName("facolta");
            
            int dim_facolta = facolta.getLength();
            
            if(debug)Log.v("Parsing-OffertaDidattica", "Numero di facolta: " + dim_facolta);
            
            OF_facolta = new String[dim_facolta][nomiNodiOF.length];
            for (int i = 0; i < dim_facolta; i++)
            {
            	for(int j=0; j<nomiNodiOF.length; j++)
            		OF_facolta[i][j] = getNodeValue(facolta, i, nomiNodiOF[j]);
            }  
            
            // Per ogni facolta cerco i suoi corsi
            NodeList corsi = doc.getElementsByTagName("corso");
            int dim_corsi = corsi.getLength();
            //Log.v("DIM CORSI", "Numeri di campi per corso = " + nomiNodiOFcorsi.length);
            OF_corsi = new String[dim_corsi][nomiNodiOFcorsi.length];
            for (int i = 0; i < dim_corsi; i++)
            {
            	for(int j=0; j<nomiNodiOFcorsi.length; j++)
            	{
            		OF_corsi[i][j] = getNodeValue(corsi, i, nomiNodiOFcorsi[j]);
            	//	Log.v("CORSO", "Colonna " + j + " Valore: " + OF_corsi[i][j]);
            	}            	
            }        
         } catch (Exception e) {
         	if(debug) Log.v("ERRORE XML", "XML Pasing Excpetion = " + e);
         }
    }
    
    public void parseXMLSegreterie()
    {
    	try {
            InputStream is;
            // Estrapolo il nome del file dalla classe globale
            if(debug) Log.v("Parsing-Segreterie", "Accedo al file: "+XML_segreterie.toString());
            File file = local_XML_segreterie;                 
            is = new BufferedInputStream(new FileInputStream(file));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(is); 
            
            NodeList facolta = doc.getElementsByTagName("facolta");
            
            int dim_facolta = facolta.getLength();
            
            if(debug)Log.v("Parsing-Segreterie", "Numero di facolta (seg): " + dim_facolta);
            
            SEG_facolta = new String[dim_facolta][nomiNodiSEG_facolta.length];
            for (int i = 0; i < dim_facolta; i++)
            {
            	for(int j=0; j<nomiNodiSEG_facolta.length; j++)
            		SEG_facolta[i][j] = getNodeValue(facolta, i, nomiNodiSEG_facolta[j]);
            }  
            
            // Per ogni facolta cerco i suoi corsi
            NodeList segreterie = doc.getElementsByTagName("segreteria");
            int dim_corsi = segreterie.getLength();
            SEG_segreterie = new String[dim_corsi][nomiNodiSegreterie.length];
            for (int i = 0; i < dim_corsi; i++)
            {
            	for(int j=0; j<nomiNodiSegreterie.length; j++)
            	{
            		SEG_segreterie[i][j] = getNodeValue(segreterie, i, nomiNodiSegreterie[j]);
            		//if(debug)Log.v("Segreteria", "Colonna " + j + " Valore: " + SEG_segreterie[i][j]);
            	}            	
            }        
         } catch (Exception e) {
         	if(debug) Log.v("ERRORE XML", "XML Pasing Excpetion = " + e);
         }
    }
    
    public String getNodeValue(NodeList nodeList, int nodeNum, String key)
    {
    	String value = "";
    	Node node = nodeList.item(nodeNum);  
    	Element fstElmnt = (Element) node;
    	NodeList nameList = fstElmnt.getElementsByTagName(key);
    	Element nameElement = (Element) nameList.item(0);
    	nameList = nameElement.getChildNodes();
    	if(nameList.getLength() > 0)
  	    {
    		value = ((Node) nameList.item(0)).getNodeValue();
  	    }
    	return value;
    }
    
    public String MD5(String md5) {
    	   try {
    	        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
    	        byte[] array = md.digest(md5.getBytes());
    	        StringBuffer sb = new StringBuffer();
    	        for (int i = 0; i < array.length; ++i) {
    	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
    	       }
    	        return sb.toString();
    	    } catch (java.security.NoSuchAlgorithmException e) {
    	    }
    	    return null;
    }
    
    public void storeAccount(String matricola, String password)
    {
    	String data = matricola + ":" + password;
    	data = Base64.encodeToString( data.getBytes(), Base64.DEFAULT );
    	try
    	{
    		FileWriter fw = new FileWriter(loginRemFile.toString());
    		fw.write(data);
    		fw.close();
    		if(debug) Log.v("USER", "Scrito il loginrem file");
    	}
    	catch (Exception e)
    	{
    		if(debug) Log.v("USER", "Impossibile scrivere su file -> " + loginRemFile.toString());
    	}
    }
    
    public void careAboutRemFile()
    {
    	matricola = "";
    	password = "";
    	if(debug) Log.v("USER", "Care function called");
    	if(loginRemFile.exists())
    	{
    		String data = readFile(loginRemFile);
    		if(data.length() > 0)
    		{
    			// Estrapolo matricola:password
    			if(debug) Log.v("USER", "Data read: " + data);
    			data = new String( Base64.decode( data, Base64.DEFAULT ) );
    			matricola = data.substring(0, data.indexOf(":"));
    			password =  data.substring(data.indexOf(":")+1);
    		}
    		else
    		{
    			if(debug) Log.v("USER", "Empty LoginRem File");
    		}
    		
    	}
    	else
    	{
    		try
    		{
    			FileWriter fw = new FileWriter(loginRemFile.toString());
    			if(debug) Log.v("USER", "Creo il file rem");
    			fw.close();
    		}
    		catch (Exception e)
    		{
    			if(debug) Log.v("USER", "Impossibile scrivere su file -> " + loginRemFile.toString());
    		}
    		
    	}
    }
    
    public void destroyLoginRemFile()
    {
    	if(loginRemFile.exists())
    	{
    		if(debug) Log.v("Secure", "Distruzione login rem file");
    		loginRemFile.delete();
    		if(loginRemFile.exists())
    			if(debug) Log.v("Secure", "File indistruttibile..");
    		else
    			if(debug) Log.v("Secure", "File eliminato con successo");
    	}
    	else
    		if(debug) Log.v("Secure", "Richiesta di cancellazione login rem file ma non esiste!");
    	
    }
    
    public void handlerMenu(MenuItem item)
    {
    	int sID = item.getItemId();
    	if(debug) Log.v("Menu", sID + " pressed");
    	
    	if(sID == R.id.aggiorna)
    	{
    		if(debug) Log.v("Handler-Menu", "Menu -> Aggiorna");
    		forzaAggiornamento();
    	}
    	else if(sID == R.id.logout)
    	{
    		if(debug) Log.v("Handler-Menu", "Menu -> Logout");
    		Intent i = new Intent(this, Home.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    startActivity(i);
    		logged = false;
    	}
    	else if(sID == R.id.login)
    	{    		
    		if(debug) Log.v("Handler-Menu", "Menu -> Login");
    		Intent i = new Intent(this, StudentLogin.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    startActivity(i);
    	}
    	else if(sID == R.id.info)
    	{
    		if(debug) Log.v("Handler-Menu", "Menu -> AppInfo");
    		Intent i = new Intent(this, AppInfo.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    startActivity(i);
    	}
    }
    
    
    /* Networking Area */
    public void HttpPostRequest(String url, List<NameValuePair> params)
    {
    	// Android ICS compatible
    	
    	
    }
    
    /* Scarica dal server il file richiesto, aggiornando quello vecchio
    @var: source 			  -> stringa che descrive l'URL del file.
	@var: fileNameDestination -> stringa che indica il percorso sulla memoria del telefono dove salvare il file sorgente. 
	@var: sourceWithOutPrefix -> booleano che, se falso, antepone l'URL del server ai file da scaricare.
     */
    public void download_and_store(String source, String fileNameDestination, boolean sourceWithOutServerPrefix)
    {
    	//try {
    		if(fileNameDestination == "") fileNameDestination = source;
    		if(!sourceWithOutServerPrefix) source = server+"/"+source;
    		if(debug) Log.v("Download", "Cerco di scaricare: " + source);
    		if(debug) Log.v("Download", "Con destinazione: " + fileNameDestination);
    	    
        	try {
				//new AsyncDownload().execute(source, fileNameDestination).get(120, TimeUnit.SECONDS);
				
				AsyncDownloader downloader = new AsyncDownloader();
			    downloader.execute(source, getAppDir().toString(), fileNameDestination);
			    //downloader.get(5, TimeUnit.SECONDS);
			    downloader.get();
        		//new AsyncDownloader().execute(source, getAppDir().toString(), fileNameDestination).get(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				if(debug) Log.v("Exception Download" , e.toString());
			} catch (ExecutionException e) {
				e.printStackTrace();
				if(debug) Log.v("Exception Download" , e.toString());
			} /*catch (TimeoutException e) {
				e.printStackTrace();
				if(debug) Log.v("Exception Download" , e.toString());
			}*/
        	
        	
            /*try {
    			asyncHttp.get(seconds, TimeUnit.SECONDS);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		} catch (ExecutionException e) {
    			e.printStackTrace();
    		} catch (TimeoutException e) {
    			e.printStackTrace();
    		}*/
        }
    /*
	private class HttpRequest extends AsyncTask<String, Void, Void> {
		// Questa classe ha il metodo downloadFile
		@Override
		protected Void doInBackground(String... params) {
			downloadFile(params[0],params[1]);
			return null;
		}
 
    }
    */
        /*
    	private class AsyncDownload extends AsyncTask<String, Void, Void> {
    		// Questa classe ha il metodo downloadFile
    		@Override
    		protected Void doInBackground(String... params) {
    			downloadFile(params[0],params[1]);
    			return null;
    		}
     
        }*/
     
        /*private void downloadFile(String source, String fileNameDestination) {
            try {
            	URL url = new URL(source);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                InputStream is = url.openStream();
                if (!appFilesDirectory.exists()) {
                	appFilesDirectory.mkdir();
                }
                FileOutputStream fos = new FileOutputStream(appFilesDirectory + "/" + fileNameDestination);
                byte data[] = new byte[1024];
                int count = 0;
                long total = 0;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    total += count;
                    int progress_temp = (int) total * 100 / lenghtOfFile;
                    if (progress_temp % 10 == 0 && progress != progress_temp) {
                        progress = progress_temp;
                    }
                    fos.write(data, 0, count);
                }
                is.close();
                fos.close();
                
                if(debug) Log.v("Download", "Ho scaricato: " + appFilesDirectory + "/" + fileNameDestination);
            } catch (ClientProtocolException e) {
            	if(debug) Log.d("HTTPCLIENT", e.getLocalizedMessage());
            } catch (IOException e) {
            	if(debug) Log.d("HTTPCLIENT", e.getLocalizedMessage());
            }
        }*/

    public void download_and_store(String source, String fileNameDestination)
    {
    	download_and_store(source, fileNameDestination, false);
    }
       
    
    public void waitAsyncTask(AsyncHttp asyncHttp, int seconds)
    {/*
        try {
			asyncHttp.get(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}*/
    	
    	try {
			asyncHttp.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void waitAsyncTask(AsyncHttpDownloader asyncHttpDownloader, int seconds)
    {
    	/*
        try {
        	asyncHttpDownloader.get(seconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}*/
    	
    	try {
			asyncHttpDownloader.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public GeoPoint coordinatesFromAddress(String strAddress)
	{
		GeoPoint p1;
		Geocoder coder = new Geocoder(this);
		List<Address> address;

		try {
		    address = coder.getFromLocationName(strAddress,5);
		    if (address == null) {
		        return null;
		    }
		    Address location = address.get(0);
		    location.getLatitude();
		    location.getLongitude();

		    p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
		                      (int) (location.getLongitude() * 1E6));
		    if(debug) Log.v("Coordinate rilevate", "Longitudine = " + p1.getLongitudeE6()/1E6);
		    if(debug) Log.v("Coordinate rilevate", "Latitudine = " + p1.getLatitudeE6()/1E6);
		    return p1;
		}
		catch (Exception e)
		{
			if(debug) Log.v("Errore-Mappa", e.toString());
			return null;
		}
	}
    
    public void sendEmail(final String dest)
    {
    	try
    	{            
        	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  	
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {dest});
            emailIntent.setType("text/plain");
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(emailIntent);
        }
    	catch (Exception e) {
    		if(debug) Log.v("ERRORE-EmailSend", dest+ ", errore->"+e.toString());
        }
    }
   
    public void phoneCall(String number)
    {
    	try
    	{            
    		Intent callIntent = new Intent(Intent.ACTION_CALL);
        	callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	callIntent.setData(Uri.parse("tel:"+number));
        	startActivity(callIntent);
        }
    	catch (Exception e) {
    		if(debug) Log.v("ERRORE-phoneCall", number+ ", errore->"+e.toString());
        }
    }

    public String getGuidaImmatricolazione(String s)
    {
    	if(s.equals("url"))
    		return PDF_guida_immatricolazione_url;
    	else
    		return PDF_guida_immatricolazione;
    }
    
    public boolean fileInAppDirExists(String filename)
    {
    	File search = new File(appFilesDirectory, filename);
    	if(debug) Log.v("Cerca File", "Cerco il file: " + search.toString());
    	return search.exists();
    }
    
    public void openURL(String url)
    {
    	try
    	{
    		Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setType("text/plain");
            i.setData(Uri.parse(url));
            startActivity(i);    	
        }
    	catch (Exception e) {
    		if(debug) Log.v("ERRORE-Redirect", url+ " non raggiungibile, errore->"+e.toString());
        }
    }
    
    public String[] getInformazioniUtiliMenu()
    {
    	return InformazioniUtiliMenu;
    }
    
    public String lastModify(String filePath)
    {
    	File file = new File(filePath);
    	Date lastModDate = new Date(file.lastModified());
    	if(debug) Log.v("ultima modifica", "File richiesto: " + file.toString());
    	if(debug) Log.v("ultima modifica", "      Data: " + lastModDate.toString());
    	return lastModDate.toString();
    }
    
    // Funzioni docenti
    public String getURLDocente(String nomeDocente)
    {
    	String url = "";
    	nomeDocente.trim();
    	nomeDocente = nomeDocente.replaceAll(" ", "+");
    	nomeDocente = nomeDocente.replaceAll("%20", "+");    	
    	url = GOOGLE_LUCKY_URL + nomeDocente + "+uniroma2.it";
    	if(debug) Log.v("URL-Docente", url);
    	return url;
    }
        
    public String getURLElencoCorsi(String facolta, boolean all)
    {
    	if(!all)
    		return (String) elencoCorsi.get(facolta);
    	else
    		return (String) elencoCorsi.get(facolta+"-all");
    }
    
    public boolean isTabletDevice(Context activityContext) {
        // Verifies if the Generalized Size of the device is XLARGE to be
        // considered a Tablet
        boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & 
                            Configuration.SCREENLAYOUT_SIZE_MASK) == 
                            Configuration.SCREENLAYOUT_SIZE_XLARGE);

        // If XLarge, checks if the Generalized Density is at least MDPI
        // (160dpi)
        if (xlarge) {
            DisplayMetrics metrics = new DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
            // DENSITY_TV=213, DENSITY_XHIGH=320
            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

                // Yes, this is a tablet!
                return true;
            }
        }

        // No, this is not a tablet!
        return false;
    }
    
    // Elimina la cartella "mytotem" posizionata nella root 
    public void eliminaVecchiaCartella()
    {
    	File oldDir = new File(Environment.getExternalStorageDirectory() + "/" + localDirName_OLD);
    	if(oldDir.exists())
    		oldDir.delete();
    	
    }
    
	public void draw_Advertising(Activity activity, LinearLayout linearLayout)
	{
		AdView adView = new AdView(activity, admobSize, admobID);
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	adView.setLayoutParams(lp);
    	linearLayout.addView(adView);
    	AdRequest adRequest = new AdRequest();
    	adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
    	adRequest.addTestDevice(deviceID);
    	adView.loadAd(adRequest);
	}
	
	public void rate(View view) {
		  Intent intent = new Intent(Intent.ACTION_VIEW);
		  intent.setData(Uri.parse("market://details?id=com.torvergata.mytotem"));
		  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  startActivity(intent);
	}
	
	public void cleanPreferences()
	{
		mPreferences.edit().remove("lastVersionRan").commit();
	}
	
	public void loadPreferences(Context c)
	{
		mPreferences = c.getSharedPreferences(sharedPreferencesFileName, MODE_PRIVATE);
	}
	
	public void updateLastVersionRan()
	{
		SharedPreferences.Editor editor = mPreferences.edit();
	    editor.putString("lastVersionRan", AppVersion);
	    editor.commit();
	}
	
	public boolean checkIfNewAppVersion()
	{
		lastVersionRan = mPreferences.getString("lastVersionRan", "0");
		Version running = new Version(AppVersion);
		Version last    = new Version(lastVersionRan);
		if(running.compareTo(last) > 0) // == 1
		{
			// Nuova versione!
			return true;
		}
		else
			return false; // Stessa versione (o in caso ANOMALO: più datata)
	}
	
	public String getPreference_string(String preferenceName, String defaultValue)
	{
		return mPreferences.getString(preferenceName, defaultValue);
	}
	
	public boolean getPreference_boolean(String preferenceName, boolean defaultValue)
	{
		return mPreferences.getBoolean(preferenceName, defaultValue);
	}
	
	public int getPreference_int(String preferenceName, int defaultValue)
	{
		return mPreferences.getInt(preferenceName, defaultValue);
	}
	
	public void updatePreference_string(String preferenceName, String value)
	{
		SharedPreferences.Editor editor = mPreferences.edit();
	    editor.putString(preferenceName, value);
	    editor.commit();
	}
	
	public void updatePreference_boolean(String preferenceName, boolean value)
	{
		SharedPreferences.Editor editor = mPreferences.edit();
	    editor.putBoolean(preferenceName, value);
	    editor.commit();
	}
	
	public void updatePreference_int(String preferenceName, int value)
	{
		SharedPreferences.Editor editor = mPreferences.edit();
	    editor.putInt(preferenceName, value);
	    editor.commit();
	}
	
}