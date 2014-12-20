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
}
