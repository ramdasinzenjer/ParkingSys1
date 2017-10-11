package srt.inzenjer.btmessage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import srt.inzenjer.connnectors.Connectivity;
import srt.inzenjer.connnectors.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegPage extends Activity{
	
	Button brg;
	
	EditText etn,etun,etpas,etcpas,etph,etmail,etli,etad;
	String sn,sun,spas,scpas,sphn,smail,slicense,sad,result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_page);
		
		etn=(EditText)findViewById(R.id.etname);
		etun=(EditText)findViewById(R.id.etuname);
		etpas=(EditText)findViewById(R.id.etpass);
		etcpas=(EditText)findViewById(R.id.etcps);
		etph=(EditText)findViewById(R.id.etphn);
		etmail=(EditText)findViewById(R.id.etemail);
		etli=(EditText)findViewById(R.id.etlicense);
		etad=(EditText)findViewById(R.id.etadd);
		brg=(Button)findViewById(R.id.btnregister);
		
		brg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sn=etn.getText().toString();
				sun=etun.getText().toString();
				spas=etpas.getText().toString();
				scpas=etcpas.getText().toString();
				sphn=etph.getText().toString();
				smail=etmail.getText().toString();
				sad=etad.getText().toString();
				slicense=etli.getText().toString();

				new RegisterApiTask().execute();
			}
		});
	}

	
public class RegisterApiTask extends AsyncTask<String,String,String> {
	    
	    @Override
	    protected String doInBackground(String... params) {


	            String urlParameters = null;
	            try {
	                urlParameters =  "name=" + URLEncoder.encode(sn, "UTF-8") + "&&"
	                        + "email=" + URLEncoder.encode(smail, "UTF-8")+ "&&" 
	                        + "username=" + URLEncoder.encode(sun, "UTF-8")+ "&&" 
	                        + "address=" + URLEncoder.encode(sad, "UTF-8")+ "&&" 
	                        + "password=" + URLEncoder.encode(spas, "UTF-8")+ "&&" 
	                        + "license=" + URLEncoder.encode(slicense, "UTF-8")+ "&&"
	                        + "phone=" + URLEncoder.encode(sphn, "UTF-8");
	            } catch (UnsupportedEncodingException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }		

	            result = Connectivity.excutePost(Constants.REGISTER_URL,
	                    urlParameters);
	            Log.e("You are at", "" + result);

	       return result;
	    }

	    @Override
	    protected void onPostExecute(String s) {
	        super.onPostExecute(s);
	        
	      //  linlaHeaderProgress.setVisibility(View.GONE);
	        if(result.contains("success"))
	        {
	        	
	        Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
	        Intent i=new Intent(getApplicationContext(),MainPage.class);
	        startActivity(i);
	        
	        }
	        else
	        {
	        	Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
	        }
	        
	    }

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();

	    }
	}


}
