package com.sourcey.materiallogindemo;
import androidx.appcompat.app.AppCompatActivity;
import com.mongodb.MongoClient;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import org.bson.*;
import com.alibaba.fastjson.JSONObject;

public final class MongoDBUtil extends AppCompatActivity {
    private static final String connectionURI = "mongodb://182.254.149.49:27017";
    private static MongoClientURI connectionString;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static Document query;
    private static long count;
    private static long su_count;
    private static int counter;
    private MongoDBUtil(){
    }

    public static MongoClient MongoDBConnect(){
        try{
            connectionString = new MongoClientURI(connectionURI);
            mongoClient = new MongoClient(connectionString);
        }catch (Exception e){

        }
        return mongoClient;
    }

    public static MongoDatabase DatabaseSelect(String Database){
        database = mongoClient.getDatabase(Database);
        return database;
    }

    public static MongoCollection<Document> CollectionSelect(String Collection){
        collection = database.getCollection(Collection);
        return collection;
    }
    public static Document Query(String name,Document Doc){
        query = new Document(name, Doc);
        return query;
    }
    public static Document DocGen(String Operation,String Value){
        Document DocGened = new Document(Operation,Value);
        return DocGened;
    }
    public static int count(MongoCollection<Document> collection,Document query){
        count = collection.countDocuments(query);
        return (int)count;
    }

    public static void close(){
        mongoClient.close();
    }
    public static int GetSuperAdmin(){
        MongoDBConnect();
        DatabaseSelect("android_permission");
        CollectionSelect("android");
        Query("group",MongoDBUtil.DocGen("$eq","nonadmin"));
        count = count(collection,query);
        return (int)count;
    }

    public static int counter(){
        counter = (int)count;
        return counter;
    }
    public static boolean exits(){
        if(count!=0){
            return true;
        }
        else{
            return false;
        }

    }
}


//original code
//MongoClientURI connectionString = new MongoClientURI("mongodb://182.254.149.49:27017");
//MongoClient mongoCldatabaseient = new MongoClient(connectionString);
//MongoDatabase  = mongoClient.getDatabase("android_permission");
//MongoCollection<Document> collection = database.getCollection("android");
//Document query = new Document("email", new Document("$eq", email));