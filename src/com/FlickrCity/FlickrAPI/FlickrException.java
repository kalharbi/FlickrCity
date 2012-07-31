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

public class FlickrException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String error_message;
	public FlickrException(int code){
		super();
		switch(code){
		case 1:
			error_message=code +": User not found";
			break;
		case 96:
			error_message=code+": Invalid signature";
			break;
		case 97:
			error_message=code+ ": Missing signature";
			break;
		case 98:
			error_message=code+ ": Login failed / Invalid auth token";
			break;
		case 99:
			error_message=code+ ": User not logged in / Insufficient permissio";
			break;
		case 100:
			error_message=code+": Invalid API Key";
			break;
		case 105:
			error_message=code+": Service currently unavailable";
			break;
		case 111:
			error_message=code+ ": Format \"xxx\" not found";
			break;
		case 112:
			error_message=code+ ": Method \"xxx\" not found";
			break;
		case 114:
			error_message=code+ ": Invalid SOAP envelope";
			break;
		case 115:
			error_message=code+ ": Invalid XML-RPC Method Call";
			break;
		case 116:
			error_message=code+ ": Bad URL found";
			break;
		case 108:
			error_message=code+ ": Invalid frob";
			break;
		default:
			error_message="Error code: "+code;
			break;
		}
	}
	public String getErrorMessage(){
		return error_message;
	}
}
