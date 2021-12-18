package parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XMLParser {
    public static ArrayList<Item> getItemsFromOnlineRSS(String urlXmlFile)
    {
        ArrayList<Item> itemList = new ArrayList<Item>();

        try {
            Document doc = Jsoup.connect(urlXmlFile).get();

            Elements items = doc.select("item");

            for(Element itemElement : items)
            {
                Element titleElement = itemElement.getElementsByTag("title").first();
                Element descriptionElement = itemElement.getElementsByTag("description").first();
                Element linkElement = itemElement.getElementsByTag("link").first();

                Item item = new Item();
                item.setTitle(Jsoup.parse(titleElement.text()).text());

                String description = descriptionElement.text();

                if(description.startsWith("<img") || description.startsWith("&lt;img"))
                {
                    //daca descrierea este o imagine
                    description = "Fără descriere!";
                }
                else
                {
                    //daca descrierea este foarte lunga mai tai din ea
                    description = Jsoup.parse(description).text();
                    if(description.length() > 300)
                        description = description.substring(0, 300) + " ...";
                }

                item.setDescription(description);

                String link = linkElement.text();
                if(link.length() == 0){
                    link = doc.location();
                }
                item.setLink(link);

                itemList.add(item);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            itemList = null;
            e.printStackTrace();
        }

        return itemList;
    }

    public static ArrayList<Site> getAllSitesFromXML(String xmlFile)
    {
        ArrayList<Site> siteList = new ArrayList<Site>();

        try {
            Document doc = Jsoup.parse(new File(xmlFile), "UTF-8");

            Elements sites = doc.select("site");

            for(Element site : sites)
            {
                Element nameElement = site.getElementsByTag("name").first();
                Element rssFeedElement = site.getElementsByTag("rss-feed").first();

                String name = nameElement.text();
                String rssFeed = rssFeedElement.text();

                siteList.add(new Site(name, rssFeed));
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            siteList = null;
            e.printStackTrace();
        }

        return siteList;
    }

}
