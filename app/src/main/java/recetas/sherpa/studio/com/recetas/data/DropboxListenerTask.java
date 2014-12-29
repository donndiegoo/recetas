package recetas.sherpa.studio.com.recetas.data;
public interface DropboxListenerTask
{
    public void onChangesTaskFinished(Object ... result);
    public void onRecipesTaskFinsihed(boolean result);
}