package utils;

import java.lang.reflect.Field;
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
	
    List<List<String>> paramTypeList = new ArrayList<>();
    List<List<String>> paramNameList = new ArrayList<>();
    List<List<String>> bodyTypeList=new ArrayList<>();
    List<List<String>> bodyNameList=new ArrayList<>();
    List<List<String>> headerTypeList=new ArrayList<>();
    List<List<String>> headerNameList=new ArrayList<>();
    List<List<JSONObject>> responseList=new ArrayList<>();
    List<List<String>> mainFieldList=new ArrayList<>();
    
        
        public JSONArray scanServlets() {
            JSONArray endpoints = new JSONArray();
    
            // Scan the entire root package
            Reflections reflections = new Reflections(ConfigLoader.getPackageName(), Scanners.TypesAnnotated);
            Set<Class<?>> servlets = reflections.getTypesAnnotatedWith(WebServlet.class);
            
            for (Class<?> servlet : servlets) {
            	bodyNameList.clear();
            	bodyTypeList.clear();
            	headerNameList.clear();
            	headerTypeList.clear();
            	paramTypeList.clear();
            	paramNameList.clear();
            	responseList.clear();
            	mainFieldList.clear();
                WebServlet annotation = servlet.getAnnotation(WebServlet.class);
                if (annotation != null) {
                    JSONObject endpoint = new JSONObject();
                    endpoint.put("name", servlet.getSimpleName());
                    endpoint.put("url", annotation.value()[0]); // First URL pattern
                    endpoint.put("methods", getHttpMethods(servlet));
    
                    // Clear lists before processing

                       
                    endpoint.put("requestDataName", paramNameList);
                    endpoint.put("requestDataType", paramTypeList);
                    endpoint.put("requestBodyName", bodyNameList);
                    endpoint.put("requestBodyType", bodyTypeList);
                    endpoint.put("requestHeaderName", headerNameList);
                    endpoint.put("requestHeaderType", headerTypeList);
                    endpoint.put("bodyFields", mainFieldList);
                    endpoint.put("response", responseList);
                    endpoint.put("ProjectName",ConfigLoader.getProjectName());
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
                    getParams(method);
                    getBody(method);
                    getHeader(method);
                    getResponseParam(method);
                }
                if (method.getName().equals("doPost")) {
                    methods.add("POST");
                    getParams(method);
                    getBody(method);
                    getHeader(method);
                    getResponseParam(method);
                }
                if (method.getName().equals("doPut")) {
                    methods.add("PUT");
                    getParams(method);
                    getBody(method);
                    getHeader(method);
                    getResponseParam(method);
                }
                if (method.getName().equals("doDelete")) {
                    methods.add("DELETE");
                    getParams(method);
                    getBody(method);
                    getHeader(method);
                    getResponseParam(method);
                }
                if (method.isAnnotationPresent(ApiAction.class)) {
                    ApiAction apiAction = method.getAnnotation(ApiAction.class);
                    methods.add(apiAction.actionName()+".sub");
                    getParams(method);
                    getBody(method);
                    getHeader(method);
                    getResponseParam(method);
                }
                
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
                paramNameList.add(subNameList);
                paramTypeList.add(subTypeList);
        
    }
    public void getBody(Method method) {
    	ArrayList<String> bodysubNameList=new ArrayList<>();
	    ArrayList<String> bodysubTypeList=new ArrayList<>();
	    ArrayList<String>subFieldList=new ArrayList<>();
	    if (method.isAnnotationPresent(ApiBody.class)) {
            ApiBody apiBody = method.getAnnotation(ApiBody.class);
            System.out.println("Key: " + apiBody.name() + ", Value: " + apiBody.type());
            bodysubNameList.add(apiBody.name());
            bodysubTypeList.add(apiBody.type());
            subFieldList=getField(apiBody.schema());
        }
	    bodyNameList.add(bodysubNameList);
        bodyTypeList.add(bodysubTypeList);
        mainFieldList.add(subFieldList);
    }
    public void getHeader(Method method) {
    	ArrayList<String> headersubNameList=new ArrayList<>();
	    ArrayList<String> headersubTypeList=new ArrayList<>();
	    if (method.isAnnotationPresent(ApiHeader.class)) {
            ApiHeader apiHeader = method.getAnnotation(ApiHeader.class);
            System.out.println("Key: " + apiHeader.name() + ", Value: " + apiHeader.type());
            headersubNameList.add(apiHeader.name());
            headersubTypeList.add(apiHeader.type());
        }
	    headerNameList.add(headersubNameList);
        headerTypeList.add(headersubTypeList);
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
        
    public ArrayList<String> getField(String className) {
    	ArrayList<String> fieldList=new ArrayList<>();
    	Class<?> clazz=null;
		try {
			clazz = Class.forName(ConfigLoader.getPackageName()+"."+className);
			Field[] fields = clazz.getDeclaredFields();

	        for (Field field : fields) {
	            fieldList.add(field.getName());
	            System.out.println(field.getName());
	        }
		} catch (ClassNotFoundException e) {
			
		}
    	
		if(clazz==null) {
			fieldList.add("Example String");
		}
        return fieldList;
    }
    public JSONObject generateOpenApiSpec() {
        JSONObject openApi = new JSONObject();
        openApi.put("openapi", "3.0.0");

        // Info Object
        JSONObject info = new JSONObject();
        info.put("title", ConfigLoader.getProjectName());
        info.put("version", "1.0.0");
        openApi.put("info", info);

        // Paths Object
        JSONObject paths = new JSONObject();
        JSONArray endpoints = scanServlets();

        for (int i = 0; i < endpoints.length(); i++) {
            JSONObject endpoint = endpoints.getJSONObject(i);
            String url = endpoint.getString("url");
            JSONObject pathItem = new JSONObject();

            JSONArray methods = endpoint.getJSONArray("methods");
            for (int j = 0; j < methods.length(); j++) {
                String method = methods.getString(j).toLowerCase();
                JSONObject operation = new JSONObject();
                operation.put("summary", endpoint.getString("name"));

                // Parameters
                JSONArray parameters = new JSONArray();
                JSONArray requestDataNames = endpoint.getJSONArray("requestDataName");
                JSONArray requestDataTypes = endpoint.getJSONArray("requestDataType");
                for (int k = 0; k < requestDataNames.length(); k++) {
                    JSONArray names = requestDataNames.getJSONArray(k);
                    JSONArray types = requestDataTypes.getJSONArray(k);
                    for (int l = 0; l < names.length(); l++) {
                        JSONObject parameter = new JSONObject();
                        parameter.put("name", names.getString(l));
                        parameter.put("in", "query");
                        parameter.put("schema", new JSONObject().put("type", types.getString(l)));
                        parameters.put(parameter);
                    }
                }

                // Request Body
                if (method.equals("post") || method.equals("put")) {
                    JSONObject requestBody = new JSONObject();
                    JSONObject content = new JSONObject();
                    JSONObject schema = new JSONObject();
                    JSONArray requestBodyNames = endpoint.getJSONArray("requestBodyName");
                    JSONArray requestBodyTypes = endpoint.getJSONArray("requestBodyType");
                    JSONArray bodyFields = endpoint.getJSONArray("bodyFields");

                    if (requestBodyNames.length() > 0) {
                        JSONObject properties = new JSONObject();
                        for (int k = 0; k < requestBodyNames.length(); k++) {
                            JSONArray names = requestBodyNames.getJSONArray(k);
                            JSONArray types = requestBodyTypes.getJSONArray(k);
                            JSONArray fields = bodyFields.getJSONArray(k);
                            for (int l = 0; l < names.length(); l++) {
                                properties.put(names.getString(l), new JSONObject().put("type", types.getString(l)));
                            }
                        }
                        schema.put("type", "object");
                        schema.put("properties", properties);
                    }

                    content.put("application/json", new JSONObject().put("schema", schema));
                    requestBody.put("content", content);
                    operation.put("requestBody", requestBody);
                }

                // Responses
                JSONObject responses = new JSONObject();
                JSONArray responseList = endpoint.getJSONArray("response");
                for (int k = 0; k < responseList.length(); k++) {
                    JSONArray subResponseList = responseList.getJSONArray(k);
                    for (int l = 0; l < subResponseList.length(); l++) {
                        JSONObject response = subResponseList.getJSONObject(l);
                        JSONObject responseObject = new JSONObject();
                        responseObject.put("description", response.getString("Description"));
                        responses.put(response.getString("ResponseCode"), responseObject);
                    }
                }
                operation.put("responses", responses);

                pathItem.put(method, operation);
            }

            paths.put(url, pathItem);
        }

        openApi.put("paths", paths);

        return openApi;
    }
    public static void main(String[] args) {
        ServletScanner scanner = new ServletScanner();
        System.out.println(scanner.scanServlets());
    }
}