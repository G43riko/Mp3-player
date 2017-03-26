package com.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHandler {
	private static String readInputStream(InputStreamReader input){
		try{
			StringBuilder result = new StringBuilder();
			String inputLine;
			BufferedReader in = new BufferedReader(input);
	        while ((inputLine = in.readLine()) != null){
	        	result.append(inputLine);
	        }
	        in.close();
	        return result.toString();
		}
		catch(IOException e){
			System.out.println("Nepodarilo sa prečítan InputStreamReader: " + e);
		};
		return "";
	}
	
	public static String getResponse(String link){
		try{
			return readInputStream(new InputStreamReader(new URL(link).openStream()));
		}
		catch(Exception e){};
		return "";
	}
	
	public static String getResponse(String link, Map<String, String> data){
		try{
			URL url = new URL(link);
			URLConnection uc = url.openConnection();
			
			uc.setRequestProperty("X-Requested-With", "Curl");
	
			for(Entry<String, String> item : data.entrySet()){
				uc.setRequestProperty(item.getKey(), item.getValue());
			}
			return readInputStream(new InputStreamReader(uc.getInputStream()));
		}
		catch(IOException e){
			System.out.println("nepodarilo sa odoslaž request s dátami: " + e);
		}
		return "";
	}
}
