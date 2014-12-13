package recetas.sherpa.studio.com.recetas.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 13/12/14.
 */
public class RecipesManager {

    private List<Recipe> mListRecipes;
    private static RecipesManager mInstance;



    private RecipesManager()
    {
        mListRecipes = new ArrayList<Recipe>();
    }

    public static RecipesManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new RecipesManager();
        }
        return mInstance;
    }

    public void setListReceipes(List<Recipe> list)
    {
        mListRecipes = list;
    }

    public List<Recipe> getListReceipes() {
        return mListRecipes;
    }

}
