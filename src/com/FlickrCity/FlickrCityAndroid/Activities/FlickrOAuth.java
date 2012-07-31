/*
 *  Copyright 2012 Khalid Alharbi

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.FlickrCity.FlickrCityAndroid.Activities;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.FlickrCity.FlickrCityAndroid.R;

public class FlickrOAuth extends Activity {
	private WebView webView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		String authorizationURL=null;
		if (extras != null) {
			authorizationURL=extras.getString("authorizationUrl");
		}
		else
			return;
		setContentView(R.layout.flickroauth);
		webView = (WebView) findViewById(R.id.webViewOAuth);
		webView.loadUrl(authorizationURL);
		webView.getSettings().setJavaScriptEnabled(true);
		//attach WebViewClient to intercept the callback url
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){
				//check for our custom callback protocol
				//otherwise use default behavior
				
				if(url.startsWith("oauth")){
					//authorization complete
					Uri uri = Uri.parse(url);
					String verifier = uri.getQueryParameter("oauth_verifier");
					Intent i=new Intent();
					i.putExtra("verifier", verifier);
					setResult(RESULT_OK, i);
					finish();
					return true;
				}
				 return super.shouldOverrideUrlLoading(view, url);	
					
				}
			});
	}
}
