package recetas.sherpa.studio.com.recetas.data;

/**
 * Created by diego on 14/12/14.
 */
public class RecipeWeb extends Recipe{

    public String getHtmlFilePath() {
        return mHtmlFilePath;
    }

    public void setHtmlFilePath(String htmlFilePath) {
        this.mHtmlFilePath = htmlFilePath;
    }

    private String mHtmlFilePath;

    public RecipeWeb(){
        super();
        mHtmlFilePath = "";
    }


}
