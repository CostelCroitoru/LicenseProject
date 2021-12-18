package algorithms;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class KNN {
    private List<Document> documentList;
    private Document document;

    public KNN(List<Document> documentList, Document document) {
        this.documentList = documentList;
        this.document = document;
    }

    //return the util list
    public List<Document> getDocumentUtilList(){
        List<Document> commonNews = new ArrayList<>();
        for(Document doc : documentList){
            Double distance = euclidDistance(doc, document);
            if(distance != null) {
                doc.append("distanceTo", distance);
                commonNews.add(doc);
            }
        }

        //Sorting ascending by distanceTo and descending by number of common words
        commonNews.sort((d1, d2) -> {
            int compare = Double.compare(d1.getDouble("distanceTo"), d2.getDouble("distanceTo"));
            if(compare == 0){
                compare = Integer.compare(d2.getInteger("common"), d1.getInteger("common"));
            }
            return  compare;
        });

        return commonNews;
    }



    public static Double euclidDistance(Document newsDocument1, Document newsDocument2){

        List<Document> vector1 = (List<Document>) newsDocument1.get("vector");
        List<Document> vector2 = (List<Document>) newsDocument2.get("vector");

        double sumSquare = 0.0;
        int nrCommonTerms = 0;

        for(Document doc1 : vector1){
            Document docFind = getDocumentByTerm(vector2, doc1.getString("term"));
            if(docFind != null){
                int count1 = doc1.getInteger("count");
                int count2 = docFind.getInteger("count");

                System.out.println(doc1.getString("term") + " : " + count1);
                sumSquare += (count1 - count2) * (count1 - count2);
                nrCommonTerms ++;
            }
        }
        newsDocument1.append("common", nrCommonTerms);
//        if(nrCommonTerms > 0){
//            System.out.println("cumune: " + nrCommonTerms);
//            System.out.println("Title: " + newsDocument1.getString("title") + "\n");
//        }


        if(nrCommonTerms > 0){
            return (1.0/(nrCommonTerms * nrCommonTerms)) * Math.sqrt(sumSquare);
        }

        return null;    //daca stirile nu au niciun cuvant comun, nu mai calculez distanta si returnez null
    }

    private static Document getDocumentByTerm(List<Document> documentList, String term){
        for(Document document : documentList){
            String findTerm = document.getString("term");
            if(findTerm.equals(term)){
                return document;
            }
        }
        return null;
    }


    public static int getCategory(List<Document> documents, int k){
        if(k<1)
            return -1;

        k = (k > documents.size()) ? documents.size() : k;

        int nrPositive = 0;
        int nrNegative = 0;

        for(int i=0;i<k;++i){

            if(documents.get(i).getString("category").equals("relevant")){
                nrPositive ++;
            }else{
                nrNegative++;
            }
        }

        return nrPositive >= nrNegative ? 1 : 0;
    }


    /*pentru cele k stiri alese, aleg categoria majoritaii dar calculata ponderat, in sensul ca o stirea care este mai "apropiata"
     * are o pondere mult mai mare decat alta mai "indepartata"
     *
     * */
    public static int getCategoryUsingWeight(List<Document> documents, int k){
        if(k<1)
            return -1;

        k = (k > documents.size()) ? documents.size() : k;

        double sumNrPositive = 0.0;
        double sumNrNegative = 0.0;

        for(int i=0;i<k;++i){
            Double distance = documents.get(i).getDouble("distanceTo");
            distance += 0.0000001;    //this is for skip divide by zero; it's insignificant for distances great than zero

            if(documents.get(i).getString("category").equals("relevant")){
                sumNrPositive += 1.0/(distance);
            }else {
                sumNrNegative += 1.0 / (distance);
            }
        }

        return sumNrPositive > sumNrNegative ? 1 : 0;
    }



}
