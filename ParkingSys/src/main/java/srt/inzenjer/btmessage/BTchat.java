package srt.inzenjer.btmessage;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

 public class BTchat extends Activity {
	
	
	//private static final String TAG = "BTchat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Layout Views
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;
	private TextToSpeech txtsp;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BTChatService mChatService = null;
	
	String StringFromReceiver="5";
	Button breq;
	Button p1,p2,p3,p4,p5,p6;
	 TTsmanager ttsManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D)
			Log.e("BTchat", "+++ ON CREATE +++");
        
		// Set up the window layout
		setContentView(R.layout.btmain);
		
		breq=(Button)findViewById(R.id.request_bt);
		
		p1=(Button)findViewById(R.id.pit1);
        p2=(Button)findViewById(R.id.pit2);
        p3=(Button)findViewById(R.id.pit3);
        p4=(Button)findViewById(R.id.pit4);
        p5=(Button)findViewById(R.id.pit5);
        p6=(Button)findViewById(R.id.pit6);
		
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mSendButton = (Button) findViewById(R.id.button_send);
		
		mOutEditText.setEnabled(false);
		mOutEditText.setVisibility(View.INVISIBLE);
		mSendButton.setEnabled(false);
		mSendButton.setVisibility(View.INVISIBLE);
		
		breq.setVisibility(View.VISIBLE);
		breq.setEnabled(true);
		
		
		p1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDialog("Palayam","rc0");
			}
		});
		
		p2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDialog("Thampanoor","rc1");
			}
		});
		
		p3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDialog("Statue", "rc2");
			}
		});
		
		p4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDialog("Statue", "rc3");
			}
		});
		
		p5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDialog("Statue", "rc4");
			}
		});
		
		p6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDialog("Statue", "rc5");
			}
		});
		
		ttsManager = new TTsmanager();
        ttsManager.init(this);
        

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}    
		
    }
    
    public void openDialog(String sloc, String scode){
	      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	      
	   final String lc=sloc; final String cd=scode;
	     
	    	 alertDialogBuilder.setTitle("Please choose an action!");
		      alertDialogBuilder.setMessage("Send request for reserving slot for 10 min...");
	    	 alertDialogBuilder.setPositiveButton("Accept & Navigate", new DialogInterface.OnClickListener() {
	         @Override
	         public void onClick(DialogInterface arg0, int arg1) {
	            
	            Toast.makeText(getApplicationContext(),"Navigating...",Toast.LENGTH_SHORT).show();
	            
	            sendMessage(cd);
	            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
          		    Uri.parse("google.navigation:q="+lc));
	            startActivity(intent);
	        
	         }
	      });
	      
	      alertDialogBuilder.setNegativeButton("Reject",new DialogInterface.OnClickListener() {
	         @Override
	         public void onClick(DialogInterface dialog, int which) {
	        	
	        	 Toast.makeText(getApplicationContext(),"Request rejected.",Toast.LENGTH_SHORT).show();
	        
	        	 //finish();
	         }
	      });
	      
	    	
	      
	      AlertDialog alertDialog = alertDialogBuilder.create();
	      alertDialog.show();
	   }
	

public void	req_data(View view)
{
	
	sendMessage("request");
	Toast.makeText(this, "Message Send",
			Toast.LENGTH_LONG).show();
	
}

   

	@Override
	public void onStart() {
		super.onStart();
		
		if (D)
			Log.e("BTchat", "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e("BTchat", "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BTChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d("BTchat", "setupChat()");

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.btmsg);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BTChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e("BTchat", "- ON PAUSE -");
		
		
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e("BTchat", "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		//super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e("BTchat", "--- ON DESTROY ---");
		
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d("BTchat", "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	public void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BTChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();				//message send
				sendMessage(message);
			}
			if (D)
				Log.i("BTchat", "END onEditorAction");
			return true;
		}
	};

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i("BTchat", "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BTChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));
					mConversationArrayAdapter.clear();
					break;
				case BTChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BTChatService.STATE_LISTEN:
				case BTChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  " + writeMessage); //view of sent message
				break;
			case MESSAGE_READ:
				
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  "   //view of recieved  msg
						+ readMessage);
				
				Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_SHORT).show();
				
                if(readMessage.contains("a,A")){
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
 	                p2.setBackgroundColor(getResources().getColor(R.color.green));
 	                p3.setBackgroundColor(getResources().getColor(R.color.green));

 	                p4.setBackgroundColor(getResources().getColor(R.color.green));
 	                p5.setBackgroundColor(getResources().getColor(R.color.green));
 	                p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("b,B")){
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("c,C")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("d,D")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("e,E")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("f,F")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("g,G")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("h,H")){
                   
                	 p1.setBackgroundColor(getResources().getColor(R.color.red));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("a,B")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("a,C")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("a,D")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("a,E")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("a,F")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("a,G")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("a,H")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("b,A")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("b,C")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("b,D")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("b,E")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("b,F")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("b,G")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("b,H")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("c,A")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("c,B")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("c,D")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("c,E")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("c,F")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("c,G")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("c,H")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("d,A")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("d,B")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("d,C")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("d,E")){
                   
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("d,F")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("d,G")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("d,H")){
                   
                	 p1.setBackgroundColor(getResources().getColor(R.color.green));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("e,A")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.red));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("e,B")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("e,C")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("e,D")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("e,F")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("e,G")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.red));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("e,H")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("f,A")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("f,B")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("f,C")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("f,D")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("f,E")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("f,G")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.red));
                     p2.setBackgroundColor(getResources().getColor(R.color.green));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.red));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("f,H")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("g,A")){
                    
                	 p1.setBackgroundColor(getResources().getColor(R.color.red));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.green));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.green));
                     p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("g,B")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("g,C")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("g,D")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("g,E")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("g,F")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("g,H")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("h,A")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("h,B")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("h,C")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("h,D")){
                   
                	 p1.setBackgroundColor(getResources().getColor(R.color.red));
                     p2.setBackgroundColor(getResources().getColor(R.color.red));
                     p3.setBackgroundColor(getResources().getColor(R.color.red));

                     p4.setBackgroundColor(getResources().getColor(R.color.green));
                     p5.setBackgroundColor(getResources().getColor(R.color.red));
                     p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("h,E")){
                    
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }else if (readMessage.contains("h,F")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.red));
                }else if (readMessage.contains("h,G")){
                   
                	p1.setBackgroundColor(getResources().getColor(R.color.red));
                    p2.setBackgroundColor(getResources().getColor(R.color.red));
                    p3.setBackgroundColor(getResources().getColor(R.color.red));

                    p4.setBackgroundColor(getResources().getColor(R.color.red));
                    p5.setBackgroundColor(getResources().getColor(R.color.red));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }

                else {

                    p1.setBackgroundColor(getResources().getColor(R.color.green));
                    p2.setBackgroundColor(getResources().getColor(R.color.green));
                    p3.setBackgroundColor(getResources().getColor(R.color.green));
                    p4.setBackgroundColor(getResources().getColor(R.color.green));
                    p5.setBackgroundColor(getResources().getColor(R.color.green));
                    p6.setBackgroundColor(getResources().getColor(R.color.green));
                }

				/*if(readMessage.contains("W"))
				{
					Toast.makeText(getBaseContext(), "Please select Mode of Guidance.",Toast.LENGTH_SHORT).show();
					
				
				//Boolean enabled=true;
				}
				
				if(readMessage.contains("P"))
				{
					
					speak("Pieta : The?Pieta is a work of?Renaissance?sculpture by?Michelangelo Buonarroti, housed in?St. Peter's Basilica,?Vatican City. It is the first of a number of works of the?same theme?by the artist. The statue was commissioned for the French?Cardinal?Jean de Bilheres, who was a representative in Rome. The sculpture, in?Carrara marble, was made for the cardinal's funeral monument, but was moved to its current location, the first chapel on the right as one enters the basilica, in the 18th century. It is the only piece Michelangelo ever signed. This famous work of art depicts the body of?Jesus?on the lap of his mother?Mary?after the?Crucifixion. The theme is of Northern origin, popular by that time in France but not yet in Italy. Michelangelo's interpretation of the?Piet?is unprecedented in Italian sculpture. It is an important work as it balances the?Renaissance?ideals of?classical beauty?with naturalism. ");
		
					
				}
				if(readMessage.contains("J"))
				{
					
					
					speak("Last judgment : The Last Judgment, or?The Last Judgment is a?fresco?by master?Michelangelo?executed on the?altar?wall of the?Sistine Chapel?in?Vatican City. It is a depiction of the?Second Coming of Christ?and the?final and eternal judgment?by God of all humanity. The souls of humans rise and descend to their fates, as judged by Christ surrounded by prominent?saints?including Saints Catherine of Alexandria, Peter, Lawrence, Bartholomew, Paul, Sebastian, John the Baptist, and others. ");
				
					
					
				}
				if(readMessage.contains("M"))
				{
					
					speak("Mona Lisa : The?Mona Lisa? is a half-length portrait of a woman by the?Italian?artist?Leonardo da Vinci, which has been acclaimed as 'the best known, the most visited, the most written about, the most sung about, the most parodied work of art in the world'.The painting, thought to be a portrait of?Lisa Gherardini, the wife of Francesco del Giocondo, is in oil on a white?Lombardy poplar panel, and is believed to have been painted between 1503 and 1506. Leonardo may have continued working on it as late as 1517. It was acquired by?King Francis I of France?and is now the property of the?French Republic, on permanent display at the?Louvre Museum");
		
					
				}
				if(readMessage.contains("S"))
				{
					
					
					speak("Last Supper : The?Last Supper?is the final meal that, in the?Gospel?accounts,?Jesus?shared with his?Apostles?in Jerusalem?before?his crucifixion.?The Last Supper is commemorated by Christians especially on Monday Thursday.?Moreover, the Last Supper provides the scriptural basis for the?Eucharist, also known as 'Holy Communion' or 'The Lord's Supper'.");
				
					
					
				}*/
				
				
				
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	
	protected void speak(String ss) {
		
		// TODO Auto-generated method stub
		 String toSpeak = ss;
         //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        // txt.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
		 ttsManager.initQueue(toSpeak);
		 
    }
	protected void TTS_stop()
	{
		ttsManager.shutDown();
		sendMessage("*C");
		sendMessage("*5");
	}
	

	protected void speak1() {
		// TODO Auto-generated method stub
		 String toSpeak1 = "hi good evng roni whwn will you marry mini....hurrey";
         //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        // txt.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
		 ttsManager.addQueue(toSpeak1);
	}
	

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d("BTchat", "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d("BTchat", "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	
	

	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				BTDeviceList.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.btoption_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, BTDeviceList.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	

    
}
