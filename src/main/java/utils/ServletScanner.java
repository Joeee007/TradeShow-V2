package utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;



public class ServletScanner {
	ConfigLoader loader=new ConfigLoader();
	
    List<List<String>> typeList = new ArrayList<>();
    List<List<String>> nameList = new ArrayList<>();
    List<List<JSONObject>> responseList=new ArrayList<>();
    
        
        public JSONArray scanServlets() {
            JSONArray endpoints = new JSONArray();
    
            // Scan the entire root package
            Reflections reflections = new Reflections(loader.getPackageName(), Scanners.TypesAnnotated);
            Set<Class<?>> servlets = reflections.getTypesAnnotatedWith(WebServlet.class);
            
            for (Class<?> servlet : servlets) {
            	typeList.clear();
            	nameList.clear();
            	responseList.clear();
                WebServlet annotation = servlet.getAnnotation(WebServlet.class);
                if (annotation != null) {
                    JSONObject endpoint = new JSONObject();
                    endpoint.put("name", servlet.getSimpleName());
                    endpoint.put("url", annotation.value()[0]); // First URL pattern
                    endpoint.put("methods", getHttpMethods(servlet));
    
                    // Clear lists before processing

                       
                    endpoint.put("requestDataName", nameList);
                    endpoint.put("requestDataType", typeList);
                    endpoint.put("response", responseList);
    
                    endpoints.put(endpoint);
                }
            }
            System.out.println(servlets.toString());
            return endpoints;
        }
    
        private List<String> getHttpMethods(Class<?> servlet) {
            List<String> methods = new ArrayList<>();
            
            Method[] declaredMethods = servlet.getDeclaredMethods();
            System.out.println(Arrays.toString(declaredMethods));
            for (Method method : declaredMethods) {
                if (method.getName().equals("doGet")) {
                    methods.add("GET");
                }
                if (method.getName().equals("doPost")) {
                    methods.add("POST");
                }
                if (method.getName().equals("doPut")) {
                    methods.add("PUT");
                }
                if (method.getName().equals("doDelete")) {
                    methods.add("DELETE");
                }
                if (method.isAnnotationPresent(ApiAction.class)) {
                    ApiAction apiAction = method.getAnnotation(ApiAction.class);
                    methods.add(apiAction.actionName());
                }
                getParams(method);
                getResponseParam(method);
            }
            return methods;
        }
    
        public void getParams(Method method) {
        	    ArrayList<String> subNameList=new ArrayList<>();
        	    ArrayList<String> subTypeList=new ArrayList<>();
                if (method.isAnnotationPresent(ApiParamGroup.class)) {
                    ApiParamGroup apiParamGroup = method.getAnnotation(ApiParamGroup.class);
                    ApiParam[] entries = apiParamGroup.value(); 
    
                    for (ApiParam entry : entries) {
                        System.out.println("Key: " + entry.name() + ", Value: " + entry.type());
                        subNameList.add(entry.name());
                        subTypeList.add(entry.type());
                        
                    }
                } else if (method.isAnnotationPresent(ApiParam.class)) {
                    ApiParam apiParam = method.getAnnotation(ApiParam.class);
                    System.out.println("Key: " + apiParam.name() + ", Value: " + apiParam.type());
                    subNameList.add(apiParam.name());
                    subTypeList.add(apiParam.type());
                }
                else{
                    System.out.println(" not supported");
            }
                nameList.add(subNameList);
                typeList.add(subTypeList);
        
    }
        
        public void getResponseParam(Method method) {
   		   ArrayList<JSONObject> subResponseList=new ArrayList<>();

   	        try {
   	                if (method.isAnnotationPresent(ResponseParams.class)) {
   	                    ResponseParams responseParams = method.getAnnotation(ResponseParams.class);
                           
   	                    
   	                    for (ResponseParam annotation : responseParams.value()) {
   	                    	JSONObject output=new JSONObject();
   	                    	output.put("Method", method.getName());
   	                    	output.put("ResponseCode", annotation.responseCode());
   	                    	output.put("Description", annotation.description());
   	                    	output.put("MediaType", annotation.mediaType());
   	                    	output.put("Schema", annotation.schema().getSimpleName());
   	                    	output.put("Deprecated", annotation.deprecated());
   	                        

   	                        if (annotation.headers().length > 0) {
   	                        	output.put("Headers", annotation.headers());
   	                        }

   	                        if (annotation.examples().length > 0) {
   	                        	output.put("Examples", annotation.examples());
   	                        }
   	                        subResponseList.add(output);
   	                    }
   	                    
   	                }
   	                else if(method.isAnnotationPresent(ResponseParam.class)) {
   	                	JSONObject output=new JSONObject();
   	                	ResponseParam responseParams = method.getAnnotation(ResponseParam.class);
   	                	output.put("Method", method.getName());
   	                    	output.put("ResponseCode", responseParams.responseCode());
   	                    	output.put("Description", responseParams.description());
   	                    	output.put("MediaType", responseParams.mediaType());
   	                    	output.put("Schema", responseParams.schema().getSimpleName());
   	                    	output.put("Deprecated", responseParams.deprecated());
   	                        

   	                        if (responseParams.headers().length > 0) {
   	                        	output.put("Headers", responseParams.headers());
   	                        }

   	                        if (responseParams.examples().length > 0) {
   	                        	output.put("Examples", responseParams.examples());
   	                        }
   	                     subResponseList.add(output);
   	                    
   	                }
   	            
   	        } catch (Exception e) {
   	        }
   	        responseList.add(subResponseList);
   	        
   	}

    public static void main(String[] args) {
        ServletScanner scanner = new ServletScanner();
        System.out.println(scanner.scanServlets());
    }
}