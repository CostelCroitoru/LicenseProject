package com.costel.license.LicenseProject;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import parsers.ProcessFile;

import java.io.File;
import java.util.HashMap;

@SpringBootApplication
public class LicenseProjectApplication {

	private static final String HOST = "localhost";
	private static final int PORT = 27017;

	//Credentials
	private static final String userName = "costelcroitoru";
	private static final String databaseName = "lincenseMongoDB";
	private static final char[] password = "License_mongoDB_pass".toCharArray();


	//News Collection
	private static final String newsCollectionName = "newsCollection";
	private static final String usersCollectionName = "usersCollection";

	public static MongoCollection<Document> newsCollection = null;
	public static MongoCollection<Document> usersCollection = null;
	public static HashMap<String, Integer> stopWords = null;

	public static void main(String[] args) {
		SpringApplication.run(LicenseProjectApplication.class, args);


		try{
			//Connecting to server
			MongoClient mongoClient = new MongoClient(HOST , PORT);
			System.out.println("Server connection successfully to locahost on port 27017!");

			// Creating Credentials
			MongoCredential credential = MongoCredential.createCredential(userName, databaseName, password);
			System.out.println("Connected to the database successfully!");

			// Accessing the database
			MongoDatabase database = mongoClient.getDatabase(databaseName);
			System.out.println("Credentials: " + credential);

			newsCollection = database.getCollection(newsCollectionName);
			usersCollection = database.getCollection(usersCollectionName);
			System.out.println("Collections  selected successfully!");

			stopWords = ProcessFile.hashWithWordFromFile(new File("stopWords.txt"));

		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
