package parsers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ProcessFile {

    public static String getStringFromFile(String filePath){
        File file = new File(filePath);
        return getStringFromFile(file);
    }

    public static String getStringFromFile(File file)
    {
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String text = "";
            String line = "";
            while((line = bufferedReader.readLine()) != null)
            {
                text += line + " ";
            }
            bufferedReader.close();
            fileReader.close();
            return text;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("Unable to open file '" + file.getName() + "'");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error reading file '" + file.getName() + "'");
            e.printStackTrace();
        }
        return "";
    }

    //get list of strings where element of list is one line from file
    // file should be UTF-8 encoded
    public static ArrayList<String> getLinesFromFile(String fileName){
        return getLinesFromFile(new File(fileName));
    }

    public static ArrayList<String> getLinesFromFile(File file)
    {
        ArrayList<String> lines = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            String line = "";
            while((line = bufferedReader.readLine()) != null)
            {
                lines.add(line);
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("Unable to open file '" + file.getName() + "'");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error reading file '" + file.getName() + "'");
            e.printStackTrace();
        }
        return lines;
    }

    //File must contains just one word per line
    public static HashMap<String, Integer> hashWithWordFromFile(File file)
    {
        HashMap<String, Integer> words = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = "";

            while((line = bufferedReader.readLine()) != null)
            {
                words.put(line.toLowerCase().trim(), 1);
            }
            bufferedReader.close();
            fileReader.close();


        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + file.getName() + "'");
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error reading file '" + file.getName() + "'");
            e.printStackTrace();
        }

        return words;
    }
}
