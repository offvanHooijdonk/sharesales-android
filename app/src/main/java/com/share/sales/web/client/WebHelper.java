package com.share.sales.web.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WebHelper {

	public static String convertInputStreamToString(InputStream is) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		
		return total.toString();
	}
	
	public static void setRequestBody(HttpURLConnection conn, String body) throws IOException {
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.writeUTF(body);
	}
	
	public static boolean checkConnectionAvailable(Context ctx) {
		boolean connected;
		
		ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			connected = true;
		} else {
			connected = false;
		}
		
		return connected;
	}
}
