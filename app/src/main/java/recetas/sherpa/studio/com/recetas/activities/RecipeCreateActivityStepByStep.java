package recetas.sherpa.studio.com.recetas.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.EditText;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;

public class RecipeCreateActivityStepByStep extends RecipeCreateActivity {

    private static final int CAMERA_REQUEST = 1888;

    private EditText mIngridients;
    private EditText mInstructions;

    public static void startActivity(Context context)
    {
        Intent intent = new Intent(context, RecipeCreateActivityStepByStep.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_create_step_by_step);
        setUpInterface();

    }

    @Override
    protected void setUpInterface() {
        super.setUpInterface();
        mIngridients = (EditText) findViewById(R.id.editText_ingredients);
        mInstructions = (EditText) findViewById(R.id.editText_instructions);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mTemporaryRecipe == null)
        {
            mTemporaryRecipe = new RecipeStepByStep();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_creation_finish) {

            if(mIngridients.getText().length() == 0)
            {
                mError = true;
                this.showDialog("No se puede guardar","Al menos pon un ingrediente, no?");
            }
            else if(mInstructions.getText().length() == 0)
            {
                mError = true;
                this.showDialog("No se puede guardar","Si no escribes las instrucciones no voy a saber hacerlo...");
            }
            else if(super.onOptionsItemSelected(item))
            {
                guardarReceta();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void guardarReceta() {

        super.guardarReceta();

        ((RecipeStepByStep)mTemporaryRecipe).parseListIngridients(mIngridients.getText().toString());
        ((RecipeStepByStep)mTemporaryRecipe).parseListInstructions(mInstructions.getText().toString());

        RecipesManager.getInstance().createRecipe(mTemporaryRecipe);


        super.showDialog("Felicitaciones!", "Has creado una nueva receta !");

    }
}
