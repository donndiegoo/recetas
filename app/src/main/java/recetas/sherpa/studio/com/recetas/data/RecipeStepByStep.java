package recetas.sherpa.studio.com.recetas.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 15/12/14.
 */
public class RecipeStepByStep extends Recipe{

    public final static String INGRIDIENT_STARTER = "-";

    private List<String> mListIngridients;
    private List<String> mListInstructions;

    public RecipeStepByStep(){
        super();
        mListIngridients = new ArrayList<>();
        mListInstructions = new ArrayList<>();

    }


    public List<String> getListIngridients() {return mListIngridients;}
    public List<String> getListInstructions() {return mListInstructions;}

    public void parseListIngridientsAndInstructions(File recipeElement) {
        List<String> list = new ArrayList<>();
        try
        {
            FileInputStream inputStream = new FileInputStream(recipeElement);

            if (inputStream != null) {
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(streamReader);

                String line;
                while (( line = bufferedReader.readLine()) != null) {
                    if(line.startsWith("*"))
                    {
                        mListIngridients.addAll(list);
                        list.clear();
                    }
                    else{
                        list.add(line);
                    }
                }
                mListInstructions.addAll(list);
            }

            inputStream.close(); //close the file
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void parseListIngridients(String ingridients) {
        List<String> list = new ArrayList<>();
        try
        {
            if (ingridients != null) {
                String[] lines = ingridients.split(System.getProperty("line.separator"));

                for(String line : lines) {
                    if(line.startsWith("*"))
                    {
                        mListIngridients.addAll(list);
                        break;
                    }
                    else{
                        list.add(line);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void parseListInstructions(String instructions) {
        List<String> list = new ArrayList<>();
        try
        {
            if (instructions != null) {
                String[] lines = instructions.split(System.getProperty("line.separator"));

                for(String line : lines) {
                    if(line.equals(lines[lines.length - 1]))
                    {
                        mListInstructions.addAll(list);
                        break;
                    }
                    else{
                        list.add(line);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
