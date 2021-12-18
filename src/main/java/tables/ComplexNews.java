package tables;


import org.bson.Document;

import java.util.HashMap;
import java.util.List;

public class ComplexNews extends News {
    private int positiveNotes;
    private int negativeNotes;
    private List<Document> titleWordsFrequency;
    //private HashMap<String, Integer> titleWordsFrequency;

    public ComplexNews() {
        this.positiveNotes = 0;
        this.negativeNotes = 0;
        this.titleWordsFrequency = null;
    }

    public int getPositiveNotes() {
        return positiveNotes;
    }

    public void setPositiveNotes(int positiveNotes) {
        this.positiveNotes = positiveNotes;
    }

    public int getNegativeNotes() {
        return negativeNotes;
    }

    public void setNegativeNotes(int negativeNotes) {
        this.negativeNotes = negativeNotes;
    }

//    public HashMap<String, Integer> getTitleWordsFrequency() {
//        return titleWordsFrequency;
//    }
//
//    public void setTitleWordsFrequency(HashMap<String, Integer> titleWordsFrequency) {
//        this.titleWordsFrequency = titleWordsFrequency;
//    }


    public List<Document> getTitleWordsFrequency() {
        return titleWordsFrequency;
    }

    public void setTitleWordsFrequency(List<Document> titleWordsFrequency) {
        this.titleWordsFrequency = titleWordsFrequency;
    }
}
