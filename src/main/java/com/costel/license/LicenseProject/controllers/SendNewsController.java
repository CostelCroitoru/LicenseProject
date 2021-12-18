package com.costel.license.LicenseProject.controllers;

import algorithms.VectorCreator;
import com.costel.license.LicenseProject.LicenseProjectApplication;
import com.costel.license.LicenseProject.mappers.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tables.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

@RestController
public class SendNewsController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;


    @RequestMapping(value = "/insertNews", method = RequestMethod.POST)
    public String insertNews(@RequestBody DateNews dateNews)
    {

/*		System.out.println("\nS-a apelat insertNews\n");
		System.out.println("ClickDate: " + dateNews.getClickDate());
		System.out.println("Note: " + dateNews.getNote());
		System.out.println("Title: " + dateNews.getTitle());
		System.out.println("Desc: " + dateNews.getDescription());
		System.out.println("Link: " + dateNews.getLink());
		System.out.println("SendDate: " + dateNews.getSendDate());*/


		//adaug in MongoDB
        MongoCollection<Document> newsCollection = LicenseProjectApplication.newsCollection;
        MongoCollection<Document> usersCollection = LicenseProjectApplication.usersCollection;



        Authentication getAuth = SecurityContextHolder.getContext().getAuthentication();
        Document userFromDB = usersCollection.find(eq("username", getAuth.getPrincipal().toString())).first();

        Document docFromCollection = newsCollection.find(eq("link", dateNews.getLink())).first();
        if(docFromCollection == null){  //daca nu exista
            VectorCreator vectorCreator = new VectorCreator(LicenseProjectApplication.stopWords);

            HashMap<String, Integer> titleWordsFrequency = vectorCreator.create(dateNews);
            ArrayList<Document> vector = VectorCreator.convertHashToBsonDocuments(titleWordsFrequency);

            Document newsDocument = new Document("title", dateNews.getTitle())
                    .append("link", dateNews.getLink());

            int note = Integer.parseInt(dateNews.getNote());
            if(note == 1){
                newsDocument.append("positiveNotes", 1)
                        .append("negativeNotes", 0)
                        .append("category", "relevant");
            }else{
                newsDocument.append("positiveNotes", 0)
                        .append("negativeNotes", 1)
                        .append("category", "irrelevant");
            }
            newsDocument.append("vector", vector)
                        .append("users", Arrays.asList());

            ;

            if(userFromDB != null){
                newsCollection.insertOne(newsDocument);

                System.out.println("ID: " + newsDocument.get("_id"));
                Document docUpdated  = new Document("$push", new Document("newsVoted", newsDocument.getObjectId("_id")));
                usersCollection.updateOne(eq("username", getAuth.getPrincipal().toString()), docUpdated);
            }else{
                System.out.println("Nu exista user-ul cu username = " + getAuth.getPrincipal().toString());
            }


        }else{  //daca exista deja, doar actualizez notele

            int note = Integer.parseInt(dateNews.getNote());

            int positiveNotes = docFromCollection.getInteger("positiveNotes");
            int negativeNotes = docFromCollection.getInteger("negativeNotes");

            Document docUpdated = new Document();
            if(note == 1){
                docUpdated.append("$inc", new Document("positiveNotes", 1));   //incrementez numarul de note pozitive
                if(positiveNotes+1 >= negativeNotes){
                    docUpdated.append("$set", new Document("category", "relevant"));
                }
            }else{
                docUpdated.append("$inc", new Document("negativeNotes", 1));   //incrementez numarul de note negative
                if(negativeNotes+1 > positiveNotes){
                    docUpdated.append("$set", new Document("category", "irrelevant"));
                }
            }

            newsCollection.updateOne(eq("link", dateNews.getLink()), docUpdated);

            //adaug stirea la lista de stiri votate dde utilizatorul curent
            Document docUserUpdated = new Document("$push", new Document("newsVoted", docFromCollection.getObjectId("_id")));
            usersCollection.updateOne(eq("username", getAuth.getPrincipal().toString()), docUserUpdated);

        }
        //=============================================================================================================


		//================================== Adaug stirea in H2 DB====================================================>
        News news = newsMapper.findByTitleAndDescription(dateNews.getTitle(), dateNews.getDescription());

        if(news != null)
        {
            //stirea este deja in tabela, deci nu o mai adaug
        }
        else    //stirea nu exista in tabela, deci o adaug
        {
            news = new News();
            news.setId(newsMapper.count() == 0 ? 1 : newsMapper.getIdMax() + 1);
            news.setTitle(dateNews.getTitle());
            news.setDescription(dateNews.getDescription());
            news.setLink(dateNews.getLink());

            newsMapper.insert(news);    //adaug in h2 database


            if(newsMapper.findByTitleAndDescription(news.getTitle(), news.getDescription()) == null){
                return "0";	//error: verific daca s-a inserat cu succes; in caz contrar ies si se anuleaza votul
            }


        }
        //=============================================================================================================|


        //Authentication getAuth = SecurityContextHolder.getContext().getAuthentication();

        User user = userMapper.getByUsername(getAuth.getPrincipal().toString());

        if(user != null){
            if(evaluationMapper.getByUserAndNews(user.getId(), news.getId()) != null)
            {
                //utilizatorul inca nu a votat stirea curenta
            }
            else    //nu exista aceasta evaluare
            {
                Evaluation evaluation = new Evaluation();
                evaluation.setId(evaluationMapper.count() == 0 ? 1 : evaluationMapper.getIdMax() + 1);
                evaluation.setIdNews(news.getId());
                evaluation.setIdUser(user.getId());
                evaluation.setUserNote(Integer.parseInt(dateNews.getNote()));
                evaluation.setSendDate(dateNews.getSendDate());
                evaluation.setClickDate(dateNews.getClickDate());

                evaluationMapper.insert(evaluation);

                if(evaluationMapper.getByUserAndNews(user.getId(), news.getId()) == null){
                    return "0";
                }
            }
        }


        return "1";
    }


   /* @RequestMapping(value = "/testInsert", method = RequestMethod.POST)
    public String testInsert(@RequestParam String ip, @RequestParam int note){

        System.out.println("Sa apelat testInsert !!!!!!!!!!!!!!");
        //System.out.println("data: " + dateNews.getTitle());
        System.out.println("Ip: " + ip);
        System.out.println("Note: " + note);

        return "25";
    }*/

}
