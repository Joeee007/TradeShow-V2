package hello;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import utils.*;
@WebServlet("/myServlet")
public class MyServlet extends HttpServlet {

    @ApiAction(actionName = "GET")
    protected void handleAction1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("This is Action 1");
    }

    @ApiAction(actionName = "GET")
    @ApiParamGroup(
            value = {
                @ApiParam(name = "name", type = "String"),
            }
    )
    @ResponseParam(responseCode = "200", description = "Successful Response", mediaType = "application/json")
    protected void handleAction2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("This is Action 2");
    }

    @Override
    @ApiParamGroup(
        value = {
            @ApiParam(name = "action", type = "String"),@ApiParam(name = "bankai", type = "int"),
        }
    )
//    @ResponseParams(
//    		value={
//        @ResponseParam(responseCode = "200", description = "Successful Response", mediaType = "application/json"),
//        @ResponseParam(responseCode = "400", description = "Bad Request", mediaType = "application/json"),
//        @ResponseParam(responseCode = "500", description = "Internal Server Error", mediaType = "application/json")
//    })
    @ResponseParam(
            responseCode = "200",
            description = "Successful response",
            mediaType = "application/json",
            headers = {"Content-Type: application/json"},
            examples = {"{\"message\": \"Success\"}"}
        )
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        String action = request.getParameter("name");
        // String action2 = request.getParameter("age");
        // System.out.println(action + " " + action2);
        // response.getWriter().write("name=" + action + "age=" + action2);


        try {
            if ("action1".equals(action)) {
                handleAction1(request, response);
            } else if ("action2".equals(action)) {
                handleAction2(request, response);
            } else {
                response.getWriter().write("Invalid action");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request");
        }
    }
}