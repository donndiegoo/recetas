package recetas.sherpa.studio.com.recetas.utils.dropbox;
public interface DropboxListenerTask
{
    public void onChangesTaskFinished(Object ... result);
    public void onRecipesTaskFinsihed(boolean result);
}