package recetas.sherpa.studio.com.recetas.data;

/**
 * Created by diego on 15/12/14.
 */
public class RecipeFile extends Recipe{

    private String mFileHtmlPath;

    public RecipeFile(){
        super();
        mFileHtmlPath = "";
    }
    public String getFileHtmlPath() {return mFileHtmlPath;}

    public void setFileHtmlPath(String path){ mFileHtmlPath = path;}
}
