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

package com.FlickrCity.FlickrAPI;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

import com.FlickrCity.FlickrCityAndroid.Utils.Constants;

public class FlickrAuth extends DefaultApi10a
{
	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public String getAccessTokenEndpoint()
	  {
		  return Constants.ACCESS_TOKEN_ENDPOINT_URL;
	  }

	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public String getAuthorizationUrl(Token requestToken)
	  {
	    return "http://www.flickr.com/services/oauth/authorize?oauth_token=" + requestToken.getToken();
	  }

	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public String getRequestTokenEndpoint()
	  {
		  return Constants.REQUEST_TOKEN_ENDPOINT_URL;
	  }
	}	
