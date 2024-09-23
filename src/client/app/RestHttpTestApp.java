package client.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;

public class RestHttpTestApp {

	// Constructor
	public RestHttpTestApp() {
		System.out.println(":: " +  getClass().getSimpleName() + " default Constructor call\n");
	}

	
	// Method
	public static void main(String[] args) throws Exception {
		
		// GET/Post, uri, param
		RestHttpTestApp.useCodehaus("GET", "/product/json/getProduct/10006/±è¹Ú»ç/manage", null);

	}
	
	
	public static void useCodehaus(String getOrPost, String uri, String param) 
								   throws Exception {
		
		System.out.println(String.format("getOrPost= %s, uri= %s, param= %s\n", getOrPost, uri, param));
		
		param = (param == null)? "" : param;
		getOrPost = (getOrPost == null)? "GET" : getOrPost.toUpperCase();
		
		HttpClient httpClient = new DefaultHttpClient();
		
		String url = "http://127.0.0.1:8080"+ uri + ((param != null && !param.equals(""))? "?" + param : "");
		
		HttpRequestBase httpGetOrPost = null;
		
		if (getOrPost.equals("GET")) {
			httpGetOrPost = new HttpGet(url);
			
		} else if (getOrPost.equals("POST")) {
			httpGetOrPost = new HttpPost(url);
			
		}
		
		httpGetOrPost.setHeader("Accpet", "application/json");
		httpGetOrPost.setHeader("Content-Type", "application/json");
		
		HttpResponse httpResponse = httpClient.execute(httpGetOrPost);
		
		System.out.println(httpResponse);
		System.out.println();
		
		HttpEntity httpEntity = httpResponse.getEntity();
		
		InputStream is = httpEntity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		JSONObject jsonObj = (JSONObject) JSONValue.parse(br);
		System.out.println(jsonObj);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		System.out.println("\n\n=============================================");
		
		if (uri.contains("product")) {
			
			if (uri.contains("list")) {
				List<JSONObject> jsonList = (List<JSONObject>)((Map<String, Object>)jsonObj.get("map")).get("list");
				
				for (JSONObject value : jsonList) {
					Product product = objectMapper.readValue(value.toString(), Product.class);
					System.out.println(product);
				}
				
			} else if (uri.contains("get")) {
				Product product = objectMapper.readValue(jsonObj.toString(), Product.class);
				System.out.println(product);
				
			}
			
			
		} else if (uri.contains("purchase")) {
			Purchase purchase = objectMapper.readValue(jsonObj.toString(), Purchase.class);
			System.out.println(purchase);
			
		}
		
	}

}
