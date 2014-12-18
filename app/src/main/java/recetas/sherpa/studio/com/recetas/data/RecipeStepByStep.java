package recetas.sherpa.studio.com.recetas.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 15/12/14.
 */
public class RecipeStepByStep extends Recipe{

    private List<String> mListIngridients;

    public RecipeStepByStep(){
        super();
        mListIngridients = new ArrayList<>();
    }

    public void setListIngridients(List<String> list)
    {
        mListIngridients = list;
    }

    public List<String> getmListIngridients()
    {return mListIngridients;}

}
