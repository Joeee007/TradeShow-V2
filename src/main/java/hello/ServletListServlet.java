package hello;

 

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import org.json.JSONObject;

 

import utils.ServletScanner;

 

@WebServlet("/jsonCon")

public class ServletListServlet extends HttpServlet {

private static final long serialVersionUID = 1L;

 

protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

response.setContentType("application/json");

response.setCharacterEncoding("UTF-8");

String action = request.getParameter("action");

if("apiSpec".equalsIgnoreCase(action))

response.getWriter().write(new ServletScanner().generateOpenApiSpec().toString(2));

else

response.getWriter().write(new ServletScanner().scanServlets().toString());

 

}

}
