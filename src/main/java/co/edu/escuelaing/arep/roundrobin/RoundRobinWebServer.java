package co.edu.escuelaing.arep.roundrobin;

import spark.Spark;

import static spark.Spark.*;

public class RoundRobinWebServer {
    private static final Integer[] servers = {35001, 35002, 35003};
    private static Integer serverNumber = 0;
    private static final String baseURL = "http://18.209.167.253:";
    public static void main(String... args){
        port(getPort());
        //Defining the route from static files
        Spark.staticFiles.location("/static");
        //Header access to static file
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
        //configuration for request from front
        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });
        // More configuration for request from back
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "*");
            res.type("application/json");
        });
        //Endpoints
        path("/api/messages", () -> {
            post("", (req, res) -> {
                Integer indexPort = serverNumber % 3;
                serverNumber++;
                String server_endpoint = baseURL + servers[indexPort] + "/api/messages";
                HttpRemoteCaller.setMethod("POST");
                HttpRemoteCaller.setGetUrl(server_endpoint);
                String responseJson = HttpRemoteCaller.doRequestPost(req.body());
                return responseJson;
            });
            get("", (req, res) -> {
                Integer indexPort = serverNumber % 3;
                serverNumber++;
                String server_endpoint = baseURL + servers[indexPort] + "/api/messages";
                HttpRemoteCaller.setMethod("GET");
                HttpRemoteCaller.setGetUrl(server_endpoint);
                String responseJson = HttpRemoteCaller.doRequestGet();
                return responseJson;
            });;
        });

    }

    /**
     * Method to get port from PORT ENV variable
     * @return int with the number of port
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 8080;
    }

}
 
