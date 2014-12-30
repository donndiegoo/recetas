package recetas.sherpa.studio.com.recetas.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import recetas.sherpa.studio.com.recetas.MyApplication;
import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.fragments.RecipesFragment;
import recetas.sherpa.studio.com.recetas.utils.dropbox.DropboxManager;

public class RecipesActivity extends ActionBarActivity implements  SearchView.OnQueryTextListener, SearchView.OnCloseListener, Observer{

    private static final String TAG = RecipesActivity.class.getSimpleName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private RecipesFragment mFragment;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mProgressView;

    private boolean isLoading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        configureMenu();
        configureActionBar();
        configureFragment(savedInstanceState);

        DropboxManager.getInstance().addObserver(RecipesActivity.this);
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        List recipes = RecipesManager.getInstance().getListReceipes();

        if (recipes == null || recipes.size() == 0)
        {
            mProgressView = MyApplication.showProgressView(this, mProgressView);
        }

       loadRecipes(false);
    }

    private void configureFragment(Bundle savedInstanceState) {
        mFragment = new RecipesFragment();
        isLoading = false;

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
        }
    }

    private void configureMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,  R.string.drawer_open,  R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        String[] values = new String[]{
                "Comprobar si hay recetas nuevas", "Borar recetas y volver a cargar"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0)
                {
                    mDrawerLayout.closeDrawer(mDrawerList);
                    loadRecipes(true);
                }
                else
                {
                    if(MyApplication.isConnected())
                    {
                        MyApplication.cleanAllRecipies();

                        mDrawerLayout.closeDrawer(mDrawerList);
                        loadRecipes(true);
                    }
                    else
                    {
                        Toast.makeText(RecipesActivity.this, "Tienes que estar conectado para poder recargar las recetas", Toast.LENGTH_LONG);
                    }

                }
            }
        });
    }

    private void configureActionBar(){

        if(isLoading)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.reyetas);
    }

    /**
     * Load the recipies
     * @param forceLoad if {@code true}, It will try to load ignoring the limit of queries per day.
     */
    private void loadRecipes(boolean forceLoad) {
        isLoading = true;
        invalidateOptionsMenu();
        configureActionBar();

        mProgressView = MyApplication.showProgressView(RecipesActivity.this, mProgressView);
        DropboxManager.getInstance().loadRecipes(RecipesActivity.this, forceLoad);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem itemCancel = menu.findItem(R.id.action_cancel);
        MenuItem itemSearch = menu.findItem(R.id.action_search);

        itemCancel.setVisible(isLoading);
        itemSearch.setVisible(!isLoading);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        else if (item.getItemId() == R.id.action_search) {
            return true;
        }
        else if (item.getItemId() == R.id.action_cancel)
        {
            cancelLoadRequests();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onQueryTextChange(String newText) {
        Log.d("Search","Query = " + newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        Log.d("Search", "Query = " + query + " : submitted");
        mFragment.applySearchQuery(query);

        return false;
    }


    @Override
    public boolean onClose() {
        Log.d("Search", "Close search");
        mFragment.applySearchQuery("");
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }


    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mDrawerList))
        {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        else if(isLoading)
        {
           cancelLoadRequests();
        }
        else{
            super.onBackPressed();
        }


    }

    private void cancelLoadRequests() {
        DropboxManager.getInstance().cancelAllRequests();
        MyApplication.hideProgressView(mProgressView);
        isLoading = false;
        invalidateOptionsMenu();
        configureActionBar();
    }


    @Override
    public void update(Observable observable, Object data) {
        //TODO update menu
        boolean changed = (boolean) data;

        Log.d(TAG, "Recipes changed? " + changed);
        MyApplication.hideProgressView(mProgressView);
        isLoading = false;
        invalidateOptionsMenu();
        configureActionBar();
    }
}
