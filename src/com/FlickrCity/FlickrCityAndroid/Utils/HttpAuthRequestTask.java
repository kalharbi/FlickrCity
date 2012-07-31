package com.FlickrCity.FlickrCityAndroid.Utils;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class HttpAuthRequestTask extends AsyncTask<Object,Void,String> {
    /** progress dialog to show user that the background process is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Context myContext;
    
	public HttpAuthRequestTask(Context context){
		myContext = context;
		dialog = new ProgressDialog(myContext);
	}
	@Override
	protected void onPreExecute() {
        dialog.setMessage("Progressing...");
        dialog.show();
    }
	
	@Override
	protected String doInBackground(Object... arg0) {
		
		OAuthService service=(OAuthService)arg0[0];
		Token accessToken=(Token)arg0[1];
		OAuthRequest request=(OAuthRequest)arg0[2];
		
		service.signRequest(accessToken, request);
		Response response= request.send();
		return response.getBody();
	}
	
	@Override
    protected void onPostExecute(String result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
	}

}
