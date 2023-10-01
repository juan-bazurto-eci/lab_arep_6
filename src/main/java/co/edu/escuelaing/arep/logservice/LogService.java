package co.edu.escuelaing.arep.logservice;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;
import spark.Spark;

import java.util.Date;

import static spark.Spark.*;

public class LogService {
    public static void main(String... args){
        port(getPort());
        Spark.staticFiles.location("/static");
        Spark.staticFiles.header("Access-Control-Allow-Origin", "*");
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

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "*");
            res.type("application/json");
        });


        //Endpoints
        path("/api/messages", () -> {
            post("", (req, res) -> {
                res.type("application/json");
                String body = req.body();
                body = body.replace("{","");
                body = body.replace("}","");
                body = body.replaceAll("\"","");
                String[] data = body.split(":");
                MongoDatabase dbmongo = createConnection();/// cambiar si se cambia la db
                MongoCollection table = dbmongo.getCollection("messages");
                Document document = new Document().append(data[0], data[1]);
                document.append("date", new Date());
                table.insertOne(document);
                return "ok";
            });
            Object gson = new Gson();
            get("", (req, res) -> {
                MongoDatabase database = createConnection();/// cambiar si se cambia la db
                //Obtener objeto colección. Si no existe lo crea
                MongoCollection<Document> messages = database.getCollection("messages");

                //Obtiene un iterable
                Document doc = new Document("date", -1);
                String jsonString = doc.toJson();
                Document document = Document.parse(jsonString);
                FindIterable<Document> iterable = messages.find().limit(10).sort(document);
                MongoCursor<Document> cursor = iterable.iterator();

                //Recorre el iterador obtenido del iterable
                Integer numberMsg = 0;
                String responseJson = "{";
                while (cursor.hasNext()) {
                    Document s = cursor.next();
                    System.out.println(s);
                    System.out.println(s.get("data"));
                    System.out.println(s.get("date"));
                    responseJson += "\"msg" + numberMsg.toString() + "\":" + "\"" +
                            s.get("data") + "  ||  " +s.get("date") + "\"";
                    if(cursor.hasNext()){
                        responseJson += ", ";
                        numberMsg += 1;
                    }
                }
                responseJson += "}";
                System.out.println("RESPONSE FROM DB IN LOGSERVICE: " + responseJson);
                res.type("application/json");
                return responseJson;
            });;
        });

    }

    public static MongoDatabase openConnection(){
        ConnectionString connectionString = new ConnectionString("mongodb+srv://juanqui:XWnCjNQzm9Cwl4Lt@cluster0.tfoojhr.mongodb.net:27017/?ssl=true&authSource=admin&retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("lab_arep_6");
        return database;
    }

    public static MongoDatabase createConnection() {
        MongoDatabase mongoDatabase = null;
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://44.203.153.167:27017/?maxPoolSize=20&w=majority");
            mongoDatabase = mongoClient.getDatabase("lab_arep_6");
        } catch (MongoException ex){
            System.out.println(ex.toString());
        }
        return  mongoDatabase;
    }



    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35001;
    }

}