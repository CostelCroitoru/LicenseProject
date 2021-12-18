package com.costel.license.LicenseProject.controllers;

import algorithms.KNN;
import algorithms.VectorCreator;
import com.costel.license.LicenseProject.LicenseProjectApplication;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@RestController
public class AlgorithmsController {

    @RequestMapping(value = "/isClickBait", method = RequestMethod.POST)
    public int isClickBait(@RequestParam String link){

//        System.out.println("\n ------------------------- BEGIN ------------------------------\n");
//        long start = System.currentTimeMillis();

        if(link.contains("#")){
            link = link.split("#")[0];
        }

        MongoCollection<Document> newsCollection = LicenseProjectApplication.newsCollection;

        // ---------------------- > Daca stirea deja exista in baza de date, preiau direct rezultatul
        Document docFromCollection = newsCollection.find(eq("link", link)).first();
        if(docFromCollection != null){  //if news is already in database
            String category = docFromCollection.getString("category");
            if(category.equals("relevant")){

                return 1;
            }
            return 0;
        }
        // <-------------------------------------------------------------------------------------------


        //lista cu documentele corespunzatoare stirilor in baza de date
        List<Document> documentList = newsCollection.find().into(new ArrayList<>());

        VectorCreator vectorCreator = new VectorCreator(LicenseProjectApplication.stopWords);
        Document newsDocument = vectorCreator.createNewsDocument(link);

        //Deci: documentList este o lista cu documentele corespunzatoare stirilor din baza de date
        //      newsDocument este documentul corespunzator stirii de la link-ul primit ca parametru

        //System.out.println(newsDocument.getString("title"));

        KNN kNN = new KNN(documentList, newsDocument);
        ArrayList<Document> utilDocList = (ArrayList<Document>) kNN.getDocumentUtilList();

        //cele 3 voturi
        int frequencyCategory = getVote1(newsDocument);
        int kNN_MajorityCategory = -1;  //categoria majoritatii
        int kNN_WeightCategory = -1;    //categoria majoritatii calculata cu pondere


        int finalCategory = frequencyCategory;



        if(utilDocList.size() != 0){       //in cazul in care nu avem niciun titlu comun, pastrez doar primul vot
            int majorityCategory = KNN.getCategory(utilDocList, 9);
            int categoryUsingWeight = KNN.getCategoryUsingWeight(utilDocList, 9);

            kNN_MajorityCategory = majorityCategory;
            kNN_WeightCategory = categoryUsingWeight;

            finalCategory = getCategoryFromVotes(frequencyCategory, kNN_MajorityCategory, kNN_WeightCategory);
        }

//        long stop = System.currentTimeMillis();
//        System.out.println((stop - start) + " milisecunde");
//        System.out.println(newsDocument.get("vector").toString());
//
//        displayCommonNewsInfoToConsole(utilDocList);  //afisare informatii la consola
//
//        System.out.println("Frequency category: " + frequencyCategory);
//        System.out.println("Simple category: " + kNN_MajorityCategory);
//        System.out.println("Weight category: " + kNN_WeightCategory);


        if(finalCategory == 0){
            newsDocument.append("positiveNotes", 0)
                .append("negativeNotes", 1)
                .append("category", "irrelevant")
                .append("users", Arrays.asList());
        }else {
            newsDocument.append("positiveNotes", 1)
                .append("negativeNotes", 0)
                .append("category", "relevant")
                .append("users", Arrays.asList());
        }

        Document systemVotes = new Document()   //adaug voturile sistemului pentru stirea curenta; va fi util la testare
                .append("frequencyCategory", frequencyCategory)
                .append("KNN_majorityCategory", kNN_MajorityCategory)
                .append("KNN_weightCategory", kNN_WeightCategory);

        newsDocument.append("systemVotes", systemVotes);
        newsCollection.insertOne(newsDocument);


//        System.out.println("\n -------------------------  END  ------------------------------\n");
        return finalCategory;
    }



    @RequestMapping(value = "/setCategory", method = RequestMethod.POST)
    public boolean setCategory(@RequestParam int category, @RequestParam String ip, @RequestParam String link){

        if(!isValidIPv4(ip) || !isValidCategory(category)){   //daca nu este o adresa corecta de ip sau nu este o categorie valida, anulez votul
            return false;
        }

        //System.out.println("Categorie: " + category + "\nIp: " + ip);

        if(link.contains("#")){
            link = link.split("#")[0];
        }   //elimin tot ce este dupa diez
        //System.out.println("Link: " + link);


        MongoCollection<Document> newsCollection = LicenseProjectApplication.newsCollection;

        //preiau documentul care are link-ul trimis ca parametru
        Document docFromDB = newsCollection.find(new Document("link", link)).first();

        if(docFromDB != null){  //daca exista stirea in mongo
            //System.out.println("Stirea este in MongoDB!");
            ArrayList<String> usersIp = (ArrayList<String>) docFromDB.get("users"); //preiau ip-ul utilizatorilor care au votat deja stirea curenta

            if(usersIp == null || !usersIp.contains(ip)){

                //System.out.println("\tIp-ul: " + ip + " nu este!");
                int positiveNotes = docFromDB.getInteger("positiveNotes");
                int negativeNotes = docFromDB.getInteger("negativeNotes");

                Document docModified  = new Document("$push", new Document("users", ip));

                if(category == 1){
                    docModified.append("$inc", new Document("positiveNotes", 1));   //incrementez numarul de note pozitive
                    if(positiveNotes+1 >= negativeNotes){
                        docModified.append("$set", new Document("category", "relevant"));
                    }
                }else{
                    docModified.append("$inc", new Document("negativeNotes", 1));   //incrementez numarul de note negative
                    if(negativeNotes+1 > positiveNotes){
                        docModified.append("$set", new Document("category", "irrelevant"));
                    }
                }

                newsCollection.updateOne(eq("link", link), docModified);    //modific documentul corespunzator link-ului respectiv

            }
//            else{
//                System.out.println("\tIp-ul: " + ip + " este deja!");
//            }
        }
        else{
            //foarte putin probabil sa intre pe aceasta ramura, dar este pentru siguranta
                //intra cand un utilizator doreste sa voteze o stire care nu exista in baza de date

            /*
            VectorCreator vectorCreator = new VectorCreator(LicenseProjectApplication.stopWords);
            Document newsDocument = vectorCreator.createNewsDocument(link);


            if(category == 0){
                newsDocument.append("positiveNotes", 0)
                        .append("negativeNotes", 1)
                        .append("category", "irrelevant")
                        .append("users", Arrays.asList(ip));
            }else {
                newsDocument.append("positiveNotes", 1)
                        .append("negativeNotes", 0)
                        .append("category", "relevant")
                        .append("users", Arrays.asList(ip));
            }
            newsCollection.insertOne(newsDocument);
            */

        }


        return true;
    }



    private static int getCategoryFromVotes(int... categories){

        int nrPosCateg = 0; //nr. of positive categories
        int nrNegCateg = 0; //nr. of negative categories
        for(int i=0;i<categories.length; ++i){
            if(categories[i] == 1){
                nrPosCateg++;
            }else{
                nrNegCateg++;
            }
        }
        return nrPosCateg > nrNegCateg ? 1 : 0;
    }


    private static int getVote1(Document newsDocument){
        ArrayList<Document> vectorFrequency = (ArrayList<Document>) newsDocument.get("vector");
        int nrWordsInTitle = vectorFrequency.size();
        int sumFrequency = 0;

        for(int i=0;i<vectorFrequency.size(); ++i){
            sumFrequency += vectorFrequency.get(i).getInteger("count");
        }


        double voteReport = 1.0;
        if((double)sumFrequency/nrWordsInTitle > voteReport){
            return 1;
        }
        return  0;
    }


    public static boolean isValidIPv4(String ip){
        if(ip == null || ip.isEmpty())
            return false;

        String[] parts = ip.split("\\.");
        if(parts.length != 4)
            return false;


        for(String numberString: parts){
            int number = Integer.parseInt(numberString);
            if(number<0 || number > 255)
                return false;
        }

        return true;
    }

    private static boolean isValidCategory(int category){
        //category should be 0 or 1
        if(category == 1 || category ==0){
            return true;
        }
        return false;
    }


    private static void displayCommonNewsInfoToConsole(ArrayList<Document> documents){
        int i=0;
        for(Document doc: documents){
            if(doc.getString("title").length() >= 50){
                System.out.println("Stire " + (++i) + ": " + doc.getString("title").substring(0, 50) +
                        ",\tdist = " + doc.getDouble("distanceTo") +
                        ", nrCommonWords: " +doc.getInteger("common") + ",\tcateg: "+doc.getString("category"));
            }else{
                System.out.println("Stire " + (++i) + ": " + doc.getString("title") + ",\tdist = " + doc.getDouble("distanceTo") +
                        ", nrCommons: " +doc.getInteger("common") + ",\tcateg: "+doc.getString("category"));
            }


        }
    }


}
