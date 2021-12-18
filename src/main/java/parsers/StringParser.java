package parsers;

public class StringParser {

    //this function remove just lowerCase diacritics
    public static String removeRoumanianDiaritics(String string){
        String diacritics =  "ăăâîșşțţ";
        String corespondents ="aaaisstt";

        String resultString = "";
        boolean wasModified;
        for(int i=0;i<string.length(); ++i){
            wasModified = false;
            for(int j=0;j<diacritics.length(); ++j){
                if(string.charAt(i) == diacritics.charAt(j)){
                    resultString += corespondents.charAt(j);
                    wasModified = true;
                    break;
                }
            }
            if(!wasModified){
                resultString += string.charAt(i);
            }
        }
        return resultString;
    }

}
