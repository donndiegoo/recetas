package recetas.sherpa.studio.com.recetas.data;

/**
 * Created by diego on 15/12/14.
 */
public class RecipeFile extends Recipe{

    private String mFilePath;

    public RecipeFile(){
        super();
        mFilePath = "";
    }
    public String getFilePath() {return mFilePath;}

    public void setFilePath(String path){ mFilePath = path;}
}
