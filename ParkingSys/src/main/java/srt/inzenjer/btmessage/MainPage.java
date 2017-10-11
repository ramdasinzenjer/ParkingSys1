package srt.inzenjer.btmessage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import srt.inzenjer.connnectors.Connectivity;
import srt.inzenjer.connnectors.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainPage extends Activity{
	
	Button bl,br;
	EditText et1,et2;
	String s1,s2,resultout;
	LinearLayout linlaHeaderProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);
		
		et1=(EditText)findViewById(R.id.edituid);
        et2=(EditText)findViewById(R.id.editpass);
        
        bl=(Button)findViewById(R.id.btnlog);
        br=(Button)findViewById(R.id.btnreg);
        linlaHeaderProgress=(LinearLayout)findViewById(R.id.linlaHeaderProgress);
        
        br.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(MainPage.this,RegPage.class);
				startActivity(i);
			}
		});
        bl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				s1=et1.getText().toString();
				s2=et2.getText().toString();
				
				SharedPreferences share=getSharedPreferences("mKey", MODE_WORLD_READABLE);
				SharedPreferences.Editor ed=share.edit();
				ed.putString("keyuid", s1);
				ed.commit();
				
				new LoginApiTask().execute();							
				
			}
		});	
        
		
	}
	public class LoginApiTask extends AsyncTask<String,String,String> {
	    
	    @Override
	    protected String doInBackground(String... params) {


	            String urlParameters = null;
	            try {
	                urlParameters =  "password=" + URLEncoder.encode(s2, "UTF-8") + "&&"
	                        + "username=" + URLEncoder.encode(s1, "UTF-8");
	            } catch (UnsupportedEncodingException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }

	        String    result = Connectivity.excutePost(Constants.LOGIN_URL,
	                    urlParameters);
	            Log.e("You are at", "" + result);
	            resultout=result;

	       return result;
	    }

	    @Override
	    protected void onPostExecute(String s) {
	        super.onPostExecute(s);
	        
	        linlaHeaderProgress.setVisibility(View.GONE);
	        //Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
	      
	        if(resultout.contains("success"))
	        {
	        	
	        Toast.makeText(getApplicationContext(), ""+resultout, Toast.LENGTH_SHORT).show();
	        Intent i=new Intent(getApplicationContext(),BTchat.class);
	        startActivity(i);
	        finish();
	        
	        }
	        else
	        {
	        	Toast.makeText(getApplicationContext(), ""+resultout, Toast.LENGTH_SHORT).show();
	        }
	        
	    }

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();

	        linlaHeaderProgress.setVisibility(View.VISIBLE);

	    }
	}
}
