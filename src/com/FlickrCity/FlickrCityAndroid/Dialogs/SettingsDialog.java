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

package com.FlickrCity.FlickrCityAndroid.Dialogs;

import com.FlickrCity.FlickrCityAndroid.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class SettingsDialog extends Dialog {
	
	public SettingsDialog(Context context){
		super(context);
	}
	 @Override
     public void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.settings);
             setTitle(R.string.settings);
             
	 }
}