package com.FlickrCity.FlickrCityAndroid.Utils;


import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class HttpGetNetworkTask extends AsyncTask <String, Void,String>{
	
    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
	
	public HttpGetNetworkTask(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.setMessage("Progressing...");
        dialog.show();
    }
	@Override
	public String doInBackground(String...urls){
		try{
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urls[0]);
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		}
			catch (Exception e){
				e.printStackTrace();
			}
		return null;
	}
	
	@Override
    protected void onPostExecute(String result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
 }
}
