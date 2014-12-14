package recetas.sherpa.studio.com.recetas.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 13/12/14.
 */
public class Recipe {



    private String mTitle;
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
}
