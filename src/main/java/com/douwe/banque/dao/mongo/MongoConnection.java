/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.douwe.banque.dao.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 *
 * @author guidona
 */
public class MongoConnection {
    private static final DB connection = createConnection();
    
    private static DB createConnection(){
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("Bank");
        return database;
    }

    public static DBCollection getConnection(String collection) {
        return connection.getCollection(collection);
    }
}

