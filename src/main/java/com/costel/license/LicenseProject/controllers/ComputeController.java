package com.costel.license.LicenseProject.controllers;

import algorithms.VectorCreator;
import com.costel.license.LicenseProject.LicenseProjectApplication;
import com.costel.license.LicenseProject.mappers.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import parsers.ProcessFile;
import tables.ComplexNews;
import tables.Evaluation;
import tables.News;
import tables.User;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


@Controller
public class ComputeController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;


    @RequestMapping(value = "/createVector", method = RequestMethod.GET)
    public String createVector(){

        System.out.println("Starting creating vectors...");
        ArrayList<News> newsArrayList = (ArrayList<News>) newsMapper.getAllNews();

        System.out.println("Nr stiri: " + newsArrayList.size());
        long start = System.currentTimeMillis();


        VectorCreator vectorCreator = new VectorCreator(ProcessFile.hashWithWordFromFile(new File("stopWords.txt")));

        //pentru fiecare stire din baza de date, preiau numarul de note pozitive si numarul de note negative

        MongoCollection<Document> documentMongoCollection = getCollection();


        for(int i=0;i<newsArrayList.size(); ++i){
            System.out.print(i+1);

            News news = newsArrayList.get(i);
            System.out.println("\t" + news.getLink());

            int positiveNotes = evaluationMapper.getNumberPositiveNotes(news.getId());
            int negativeNotes = evaluationMapper.getNumberNegativeNotes(news.getId());

            String category = positiveNotes > negativeNotes ? "relevant" : "irrelevant";


            ComplexNews complexNews = new ComplexNews();
                        complexNews.setId(news.getId());
                        complexNews.setTitle(news.getTitle());
                        complexNews.setDescription(news.getDescription());
                        complexNews.setLink(news.getLink());
                        complexNews.setPositiveNotes(positiveNotes);
                        complexNews.setNegativeNotes(negativeNotes);
                        //complexNews.setTitleWordsFrequency(vectorCreator.create(news));

            Document newsDocument = new Document()
                    .append("id", complexNews.getId())
                    .append("title", complexNews.getTitle())
                    .append("description", complexNews.getDescription())
                    .append("link", complexNews.getLink())
                    .append("positiveNotes", complexNews.getPositiveNotes())
                    .append("negativeNotes", complexNews.getNegativeNotes())
                    .append("category", category)
                    .append("vector", VectorCreator.convertHashToBsonDocuments(vectorCreator.create(news)));

            documentMongoCollection.insertOne(newsDocument);




        }

        long stop = System.currentTimeMillis();


        System.out.println("End success!");




       /* News news = new News();
        news.setTitle("Băsescu, despre decizia lui Dragnea de a mutat ambasada României din Israel: Un neisprăvit din Teleorman a făcut praf și pulbere 60 de ani de diplomație consistentă și inteligentă în Orientul Apropiat");
        news.setLink("https://www.activenews.ro/stiri-politic/Basescu-despre-decizia-lui-Dragnea-de-a-mutat-ambasada-Romaniei-din-Israel-Un-neispravit-din-Teleorman-a-facut-praf-si-pulbere-60-de-ani-de-diplomatie-consistenta-si-inteligenta-in-Orientul-Apropiat-150445");

        VectorCreator vectorCreator = new VectorCreator(ProcessFile.hashWithWordFromFile(new File("stopWords.txt")));

        HashMap<String, Integer> frequency = vectorCreator.create(news);

        System.out.println(frequency.toString());*/
        return "/test";

    }








    private MongoCollection<Document> getCollection(){
        final String HOST = "localhost";
        final int PORT = 27017;

        //Credentials
        final String userName = "costelcroitoru";
        final String databaseName = "lincenseMongoDB";
        final char[] password = "License_mongoDB_pass".toCharArray();


        //Index Collections
        final String newsCollectionName = "newsCollection";


        MongoClient mongoClient = new MongoClient(HOST , PORT);
        System.out.println("Server connection successfully to locahost on port 27017!");

        MongoCredential credential = MongoCredential.createCredential(userName, databaseName, password);
        System.out.println("Connected to the database successfully!");

        // Accessing the database
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        System.out.println("Credentials: " + credential);

        MongoCollection<Document> documentMongoCollection = database.getCollection(newsCollectionName);

        return documentMongoCollection;
    }


    @RequestMapping(value = "/copyDB", method = RequestMethod.GET)
    public String copyDB(){


        ArrayList<String> StringUsers = ProcessFile.getLinesFromFile("users.txt");


        evaluationMapper.delete();
        newsMapper.delete();
        userMapper.delete();

        System.out.println("Nr users: " + StringUsers.size()/6);
        for(int i=0; i<StringUsers.size(); i+=6){
            int id = Integer.parseInt(StringUsers.get(i));
            String name = StringUsers.get(i+1);
            String username = StringUsers.get(i+2);
            String password = StringUsers.get(i+3);
            String role = StringUsers.get(i+4);

            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);

            try{
                userMapper.insert(user);
            }catch (Exception e){
                System.out.println("User-ul nu poate fi inserat!");
                e.printStackTrace();
                break;
            }

        }

        ArrayList<String> stringsNews = ProcessFile.getLinesFromFile(new File("stiri.txt"));


        System.out.println("Nr stiri: " + stringsNews.size()/5);
        for(int i=0;i<stringsNews.size(); i = i+5){
            int id = Integer.parseInt(stringsNews.get(i));
            String title = stringsNews.get(i+1);
            String description = stringsNews.get(i+2);
            String link = stringsNews.get(i+3);

            News news =  new News();
                news.setId(id);
                news.setTitle(title);
                news.setDescription(description);
                news.setLink(link);


                try {
                    newsMapper.insert(news);
                }catch (Exception ex){
                    System.out.println("Stirea nu poate fi inserata!");
                    ex.printStackTrace();
                    break;
                }

        }


        ArrayList<String> stringEvaluations = ProcessFile.getLinesFromFile(new File ("evaluations.txt"));


        System.out.println("Nr evaluari : " + stringEvaluations.size()/7);

        for(int i=0;i<stringEvaluations.size(); i+=7){
            int id = Integer.parseInt(stringEvaluations.get(i));
            int idNews = Integer.parseInt(stringEvaluations.get(i+1));
            int idUser = Integer.parseInt(stringEvaluations.get(i+2));
            int userNote = Integer.parseInt(stringEvaluations.get(i+3));

            DateFormat dateFormat =  new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
            try {
                Date clickDate = dateFormat.parse(stringEvaluations.get(i+4));
                Date sendDate = dateFormat.parse(stringEvaluations.get(i+5));

                Evaluation evaluation = new Evaluation();
                evaluation.setId(id);
                evaluation.setIdNews(idNews);
                evaluation.setIdUser(idUser);
                evaluation.setUserNote(userNote);
                evaluation.setClickDate(clickDate);
                evaluation.setSendDate(sendDate);

                evaluationMapper.insert(evaluation);

            } catch (ParseException e) {

                System.out.println("Data nu poate fi convertita!");
                e.printStackTrace();
                break;
            }

        }

        System.out.println("Inserare cu succes!");
        return "/test";
    }



    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){

        System.out.println("S-a apelat test!");
        return "/test";
    }




}
