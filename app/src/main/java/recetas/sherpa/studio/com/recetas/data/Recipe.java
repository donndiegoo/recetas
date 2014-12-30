package recetas.sherpa.studio.com.recetas.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import recetas.sherpa.studio.com.recetas.Constants;
import recetas.sherpa.studio.com.recetas.MyApplication;

/**
 * Created by diego on 13/12/14.
 */
public class Recipe {

    private String mTitle;
    private String mPath;
    private List<String> mListPictures;

    public Recipe()
    {
        mTitle = "";
        mListPictures = new ArrayList<String>();
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPath() { return mPath;  }

    public void setPath(String path) { mPath = path; }

    public List<String> getListPictures() {
        return mListPictures;
    }

    public void setListPictures(List<String> listPictures) {
        mListPictures = listPictures;
    }

    public void addPicture(String picturePath)
    {
        mListPictures.add(picturePath);
    }

    public String getFirstPicture()
    {
        if(mListPictures.size() > 0)
        {
            return mListPictures.get(0);
        }
        else
        {
            return "";
        }
    }

    public String getRecipeDirectoryPathLocal()
    {
        String baseDirectory = MyApplication.getRecipesBaseDirecotry();
        String recipesFolderNameLocal = baseDirectory + "/" + MyApplication.mFolderSelected;
        return recipesFolderNameLocal + "/" + getTitle();
    }

    public String getRecipeDirectoryPathRemote()
    {
        String recipesFolderNameRemote = "/" + MyApplication.mFolderSelected;
        return recipesFolderNameRemote + "/" + getTitle();
    }
}
