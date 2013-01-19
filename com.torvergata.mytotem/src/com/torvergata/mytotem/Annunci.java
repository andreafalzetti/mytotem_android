package com.torvergata.mytotem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class Annunci extends Activity implements AdapterView.OnItemSelectedListener {

	MyTotem global;
	Animation ani;
	boolean insertOpened = false;
	
	private static final int SELECT_PHOTO = 100;
	//private static final int REQ_CODE_PICK_IMAGE = 0;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.annunci);        
        global = ((MyTotem) this.getApplication()); 
        
        
       // Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	//	photoPickerIntent.setType("image/*");
		//startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
        
        String categorie[] = {"Scelgi una categoria", "Affitti", "Libri", "Tecnologia", "Ripetizioni", "Altro"};
        Spinner spin = (Spinner) findViewById(R.id.spinner);
		spin.setOnItemSelectedListener(this);
        ArrayAdapter<?> aa = new ArrayAdapter<Object>(this,android.R.layout.simple_spinner_item, categorie);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);
		spin.setSelection(0);    
		
		String modalità[] = {"[Vendo]", "[Regalo]", "[Cerco]", "[Affitto]"};
        spin = (Spinner) findViewById(R.id.spinnerMode);
		spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<Object>(this,android.R.layout.simple_spinner_item, modalità);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);
		spin.setSelection(0);	
		
        spin = (Spinner) findViewById(R.id.spinnerCategoria);
		spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter<Object>(this,android.R.layout.simple_spinner_item, categorie);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(aa);
		spin.setSelection(0);	
		
		Button myBtn=(Button)findViewById(R.id.btnInsert);
		myBtn.setFocusableInTouchMode(true);
		myBtn.requestFocus();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
        case SELECT_PHOTO:
            if(resultCode == RESULT_OK){  
                Uri selectedImage = imageReturnedIntent.getData();
                String selectedImagePath = getPath(selectedImage);

                //Toast.makeText(getApplicationContext(), "1..."+selectedImage.getPath(), Toast.LENGTH_SHORT).show();
                //msg("path = " + selectedImagePath);
                /*
                InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                
        		
                File image1 = new File(selectedImage.getPath());
                if(image1.exists())
                	msg("esiste");
                else
                	msg("non esiste");
                
               // msg(image1.toString());
                */
                
                String url = "http://mytotem.torengine.it/appcontents/index.php";
               
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("from", "androidDevice"));
                nameValuePairs.add(new BasicNameValuePair("image", selectedImagePath));
                
                //File test = new File(selectedImagePath);
                /*if(test.exists())
                	msg("esiste");
                else
                	msg("non esiste");
                */
                post(url, nameValuePairs);
            }
        }
    }
    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    
    public void msg(String s)
    {
    	Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
        
    public void post(String url, List<NameValuePair> nameValuePairs) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < nameValuePairs.size(); index++) {
                if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
                    // If the key equals to "image", we use FileBody to transfer the data
                    entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
                    msg("add img");
                } else {
                    // Normal string data
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                }
            }

            httpPost.setEntity(entity);
            
            HttpResponse response = httpClient.execute(httpPost, localContext);
            String str = response.getEntity().getContent().toString();
            msg("Form inviato");
        } catch (IOException e) {
            e.printStackTrace();
            msg("ERRORE!!! --> " + e.toString());
        }
    }

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void svuota(View v)
	{
		EditText ET = (EditText) v;
		ET.setText("");
	}
	
	public void inserisciAnnuncioShowForm(View v)
	{
		Button btnInsert = (Button)v;
		LinearLayout l = (LinearLayout) findViewById(R.id.row3_hidden);
		if(insertOpened == false)
		{
			animaLayout(l, 550);
			btnInsert.setText("Annulla");
			insertOpened = true;
		}
		else
		{
			animaLayout(l, 0);
			btnInsert.setText("Inserisci il tuo annuncio");
			insertOpened = false;
		}
		
		
	}
	
	public void animaLayout(LinearLayout l, int target)
	{
		ani = new ShowAnim(l, target);
		ani.setDuration(800);
		ani.setStartTime(AnimationUtils.currentAnimationTimeMillis());
		l.invalidate();
		l.startAnimation(ani);
		l.invalidate();
		l.startAnimation(ani);
	}
	
	public void ad_continua(View v)
	{
		String title = getValueFromET(R.id.ad_titolo);
		String msg = getValueFromET(R.id.ad_messaggio);
		String mode = getValueFromET(R.id.spinnerMode);
		String category = getValueFromET(R.id.spinnerCategoria);
		String contact = getValueFromET(R.id.ad_contatto);
        String url = global.getURL("insert-ad");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("from", "androidDevice"));
        nameValuePairs.add(new BasicNameValuePair("title", title));
        nameValuePairs.add(new BasicNameValuePair("message", msg));
        nameValuePairs.add(new BasicNameValuePair("contact", contact));
        nameValuePairs.add(new BasicNameValuePair("mode", mode));
        nameValuePairs.add(new BasicNameValuePair("category", category));
        //post(url, nameValuePairs);
	}
	
	public String getValueFromET(int id)
	{
		EditText v = (EditText) findViewById(id);
		return v.getText().toString();
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
