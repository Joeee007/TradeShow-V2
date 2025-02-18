package hello;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.ApiBody;
import utils.ApiParam;
import utils.ApiParamGroup;
import utils.ResponseParam;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	response.getWriter().write("Get from UserServlet");

    }
    
    
    
    
    @ApiParamGroup(
            value = {
            		@ApiParam(name = "header", type = "string"),@ApiParam(name = "recived", type = "JSON") ,
            }
        )
    @ApiBody(name="body",type="JSON")
    @ResponseParam(
            responseCode = "200",
            description = "Successful response",
            mediaType = "application/json",
            headers = {"Content-Type: application/json"},
            examples = {"{\"message\": \"Success\"}"}
        )
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	response.setContentType("application/json");


        // Read request headers
        String customHeader = request.getHeader("Custom-Header");

        // Read JSON request body
        StringBuilder jsonBody = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }

        // Build JSON response
        String jsonResponse = "{ \"received\": " + jsonBody.toString() + ", \"header\": \"" + customHeader + "\" }";
        response.getWriter().write(jsonResponse);
    }
}
