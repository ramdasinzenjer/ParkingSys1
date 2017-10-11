package srt.inzenjer.connnectors;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Connectivity {

	public static boolean getInternetConnectionStatus(Context context) {
		// get Connectivity Manager object to check connection
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Check for network connections
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
				||

				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
			return true;
		} else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}


	public static String excutePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			if (urlParameters != null) {

				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				connection.setRequestProperty("Content-Length",
						"" + Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty("Content-Language", "en-US");
				Log.e("You are at ","executepOst");
			}

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			if (urlParameters != null) {
				// Send request
				DataOutputStream wr = new DataOutputStream(
						connection.getOutputStream());

				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
			}
			// Get Response
			Log.e("responsecode", connection.getResponseCode() + "");

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			if (connection.getResponseCode() == 200)
				return response.toString();
			else
				return null;

		} catch (Exception e) {

			e.printStackTrace();
			Log.e("","Error"+e);
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}


}
