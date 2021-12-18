package parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HTMLParser {
    private String url;
    private Document document;



    public HTMLParser(String url) {
        this.url = url;

        if(url != null && url.length() > 1){
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            document = null;
        }

    }

    public String getTitleOfContent(){
        String title = "";
        if(document != null){

            //se incearca prealuarea titlului din primul element h1 nenul gasit
            Elements h1Elements = document.select("h1");
            if(h1Elements != null){
                for(Element h1Element : h1Elements){
                    title = h1Element.text();

                    if(title.equals("") || title.length() < 3){
                        continue;
                    }else{
                        break;
                    }
                }
            }
            //in caz ca nu este gasit, se incearca in elementul <h2>
            if(title.equals("") || title.length()< 3){
                Elements h2Elements = document.select("h2");
                if(h2Elements != null){
                    for(Element h2Element : h2Elements){
                        title = h2Element.text();
                        if(title.equals("") || title.length() < 3){
                            continue;
                        }else{
                            break;
                        }
                    }
                }
            }


            //daca nici acolo nu este gasit, se preia din elementul <title> al paginii
            if(title.equals("")){
                title = document.title();
                if(title.contains("|")) //de regula, textul din elementul title, pe langa titlul propriu zis,
                                        //mai contine si numele paginii
                {
                    int indexBar = title.indexOf('|');
                    String part1 = title.substring(0, indexBar);
                    String part2 = title.substring(indexBar + 1, title.length());
                    if(part1.length() > part2.length())
                        title = part1;
                    else
                        title = part2;
                }
            }
        }

        return title;
    }

    public String getBodyText(){

        String bodyText = "";

        if(document != null){
            Element bodyElement = document.body();
            bodyElement.select("a").remove();   //elimin toate link-urile

            bodyElement.select("header").remove();
            bodyElement.select("#header").remove();
            bodyElement.select(".header").remove();

            bodyElement.select("footer").remove();  //elimin footer-ul
            bodyElement.select("#footer").remove();
            bodyElement.select(".footer").remove();


            bodyText = bodyElement.text();
        }

        return bodyText;
    }




    //static functions


    public static String getTitleOfContent(String url){
        String title = "";
        try {
            Document document = Jsoup.connect(url).get();

            Elements h1Elements = document.select("h1");

            if(h1Elements != null){
                for(Element h1Element : h1Elements){
                    title = h1Element.text();

                    if(title.equals("") || title.length() < 3){
                        continue;
                    }else{
                        break;
                    }
                }
            }

            if(title.equals("")){
                title = document.title();
                if(title.contains("|"))
                {
                    int indexBar = title.indexOf('|');
                    String part1 = title.substring(0, indexBar);
                    String part2 = title.substring(indexBar + 1, title.length());
                    if(part1.length() > part2.length())
                        title = part1;
                    else
                        title = part2;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return title;
    }

    public static String getBodyText(String link){
        String bodyText = "";

        if(link != null && !link.equals("")) {
            try {
                Element bodyElement = Jsoup.connect(link).get().body();
                bodyElement.select("a").remove();

                bodyElement.select("header").remove();
                bodyElement.select("#header").remove();
                bodyElement.select(".header").remove();

                bodyElement.select("footer").remove();  //elimin footer-ul
                bodyElement.select("#footer").remove();
                bodyElement.select(".footer").remove();


                bodyText = bodyElement.text();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bodyText;
    }

}
