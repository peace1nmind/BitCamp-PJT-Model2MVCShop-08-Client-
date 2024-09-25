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
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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
		
		// listProduct
//		RestHttpTestApp.useCodehaus("GET", "/json/product/listProduct", "menu=search");
		
		// getProduct
//		RestHttpTestApp.useCodehaus("GET", "/json/product/getProduct/10036", null);
		
		// updateProduct Get
//		RestHttpTestApp.useCodehaus("Get", "/json/product/updateProduct/10036", null);
		
		// updateProduct Post
		RestHttpTestApp.useCodehaus("Post", "/json/product/updateProduct", 
									"prodNo=10036&"
								  + "prodName=testRestChanged&"
								  + "manuDate=2024-09-25&"
								  + "price=909090&"
								  + "fileName=empty.GIF");

	}
	
	
	public static void useCodehaus(String method, String uri, String param) 
								   throws Exception {
		
		System.out.println(String.format("method= %s, uri= %s, param= %s\n", method, uri, param));
		
		/* Request 로직 */
		param = (param == null)? "" : param;
		method = (method == null)? "GET" : method.toUpperCase();
		
		HttpClient httpClient = new DefaultHttpClient();
		
		String url = "http://127.0.0.1:8080"+ uri + ((method.equals("GET"))?((param != null && !param.equals(""))? "?" + param : ""):"");
		
		System.out.println("url= "+url);
		System.out.println();
		
		HttpResponse httpResponse = null;
		
		if (method.equals("GET")) {
			HttpGet httpGet = new HttpGet(url);
			
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-Type", "application/json");
			
			httpResponse = httpClient.execute(httpGet);
			
		} else if (method.equals("POST")) {
			HttpPost httpPost = new HttpPost(url);
			
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");
			
			HttpEntity httpPostEntity = null;
			
			// POST 인 경우 JSON 객체로 데이터를 넘겨줘야한다
			/* 데이터 넘기는 로직 추가 - param으로 받아서 파싱해서 JSON 객체에 넣어주기 */
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			
			if (!(param == null || param.equals(""))) {
				
				String[] params = param.split("&");
				
				for (String p : params) {
					String key = p.split("=")[0];
					String value = p.split("=")[1];
					
					builder.addTextBody(key, value);
				}
				
			} else {
				throw new IllegalArgumentException("Error : 파라미터 값을 잘못 입력");
				
			}
			
			System.out.println("builder= "+builder);
			httpPostEntity = builder.build();
			
			httpPost.setEntity(httpPostEntity);
			httpResponse = httpClient.execute(httpPost);
			
		}
		
		
		/* Response 이후 로직 */
		
		System.out.println(httpResponse);
		System.out.println();
		
		HttpEntity httpEntity = httpResponse.getEntity();
		
		InputStream is = httpEntity.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		JSONObject jsonObj = (JSONObject) JSONValue.parse(br);
		System.out.println("\n전달받은 jsonObject.toString()=\n");
		System.out.println(jsonObj);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		System.out.println("\n\n=============================================");
		
		switch (method) {
		
			case "GET":
				
				if (uri.contains("product")) {
					
					if (uri.contains("list")) {
						List<JSONObject> jsonList = (List<JSONObject>)((Map<String, Object>)jsonObj.get("map")).get("list");
						
						System.out.println("\n바인딩 한 내용=");
						
						for (JSONObject value : jsonList) {
							Product product = objectMapper.readValue(value.toString(), Product.class);
							System.out.println(product);
						}
						
					} else if (uri.contains("get")) {
						Product product = objectMapper.readValue(jsonObj.toString(), Product.class);
						System.out.println(product);
						
					} else if (uri.contains("update")) {
						Product product = objectMapper.readValue(jsonObj.toString(), Product.class);
						System.out.println(product);
					}
					
					
				} else if (uri.contains("purchase")) {
					Purchase purchase = objectMapper.readValue(jsonObj.toString(), Purchase.class);
					System.out.println(purchase);
					
				}
				
				break;
	
				
			case "POST":
				
				if (uri.contains("product")) {
					
					if (uri.contains("update")) {
						Product product = objectMapper.readValue(jsonObj.toString(), Product.class);
						System.out.println(product);
					}
					
					
				} else if (uri.contains("purchase")) {
					
					
				}
				
				break;
				
		}
		
		
	}

}
