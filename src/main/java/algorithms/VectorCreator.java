package algorithms;

import org.bson.Document;
import parsers.HTMLParser;
import parsers.StringParser;
import tables.News;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VectorCreator {
    private HashMap<String, Integer> stopWords;

    public VectorCreator(HashMap<String, Integer> stopWords) {
        this.stopWords = stopWords;
    }


    public Document createNewsDocument (String newsLink){

        HTMLParser htmlParser = new HTMLParser(newsLink);
        String titleContent = htmlParser.getTitleOfContent();
        String textContent = htmlParser.getBodyText();

        titleContent = titleContent.toLowerCase();
        textContent = textContent.toLowerCase();

        titleContent = StringParser.removeRoumanianDiaritics(titleContent);
        textContent = StringParser.removeRoumanianDiaritics(textContent);

        String[] titleWords = titleContent.split("[^a-zA-Z0-9]");
        String[] textWords = textContent.split("[^a-zA-Z0-9]");

        HashMap<String, Integer> titleWordsFrequency = new HashMap<>();
        for(String word : titleWords){
            if(word.length() > 0){
                if(!stopWords.containsKey(word)){
                    titleWordsFrequency.put(word, 0);
                }
            }
        }

        //in acest moment eu am puse toate cuvintele relevante din titlu intr-un hashMap
        for(String textWord : textWords){
            if(textWord.length() > 0){
                if(titleWordsFrequency.containsKey(textWord)){
                    titleWordsFrequency.replace(textWord, titleWordsFrequency.get(textWord) + 1);
                }
            }
        }

        List<Document> documentTitleList = new ArrayList<>();
        //pentru ca e pe pagina html preiau si titlul din nou, acum voi scadea 1 din freceventa cuvintelor
        for(Map.Entry<String, Integer> entry : titleWordsFrequency.entrySet()){
            int count = entry.getValue();
            String term = entry.getKey();

            if(count > 0){
                count -= 1;
            }

            Document documentTerm = new Document("term", term).append("count", count);
            documentTitleList.add(documentTerm);
        }

        //se creeaza docmentul mongo coresupunzator noii stiri
        Document newsDocument = new Document("title", titleContent)
                .append("link", newsLink)
                .append("vector", documentTitleList);


        return newsDocument;
    }



    //primeste ca parametru o strire(care contine titlul si link-ul catre pagina sursa) si returneaza vectorul de frecvente al cuvintelor in text
    public HashMap<String, Integer> create(News news){

        String titleContent = "";
        String textContent = "";


        HTMLParser htmlParser = new HTMLParser(news.getLink());

        if(news.getTitle().equals("")){
            news.setTitle(htmlParser.getTitleOfContent());
        }

        titleContent = news.getTitle().toLowerCase();
        textContent = htmlParser.getBodyText().toLowerCase();

        titleContent = StringParser.removeRoumanianDiaritics(titleContent);
        textContent = StringParser.removeRoumanianDiaritics(textContent);


        //System.out.println("Title: " + titleContent);
        //System.out.println("Text: " + textContent);


        String[] titleWords = titleContent.split("[^a-zA-Z0-9]");
        String[] textWords = textContent.split("[^a-zA-Z0-9]");
        //System.out.println("Length : " + textWords.length);

        HashMap<String, Integer> titleWordsFrequency = new HashMap<>();

        for(String word : titleWords){
            if(word.length() > 0){

                if(!stopWords.containsKey(word)){
                    titleWordsFrequency.put(word, 0);
                }
            }
        }

        //in acest moment eu am puse toate cuvintele relevante din titlu intr-un hashMap
        for(String textWord : textWords){
            if(textWord.length() > 0){

                if(titleWordsFrequency.containsKey(textWord)){
                    titleWordsFrequency.replace(textWord, titleWordsFrequency.get(textWord) + 1);
                }
            }
        }

        //pentru ca e pe pagina html preiau si titlul din nou, acum voi scadea 1 din freceventa cuvintelor
        for(Map.Entry<String, Integer> entry : titleWordsFrequency.entrySet()){
            int value = entry.getValue();
            if(value > 0){
                entry.setValue(value - 1);
            }
        }

        return titleWordsFrequency;
    }

    public static ArrayList<Document> convertHashToBsonDocuments(HashMap<String, Integer> stringIntegerHashMap){

        ArrayList<Document> documents = new ArrayList<>();

        for(Map.Entry<String, Integer> entry : stringIntegerHashMap.entrySet()){

            String term = entry.getKey();
            int count = entry.getValue();

            Document document = new Document("term", term)
                    .append("count", count);

            documents.add(document);
        }
        return documents;
    }

}
