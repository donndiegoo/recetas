package recetas.sherpa.studio.com.recetas.activities;

import android.content.res.Configuration;
import android.os.Bundle;
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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import recetas.sherpa.studio.com.recetas.MyApplication;
import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.utils.DropboxManager;
import recetas.sherpa.studio.com.recetas.fragments.RecipesFragment;

public class RecipesActivity extends ActionBarActivity implements  SearchView.OnQueryTextListener, Observer {

    private static final String TAG = RecipesActivity.class.getSimpleName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private RecipesFragment mFragment;

    private SearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mProgresView;

    private boolean isLoading;

    private void configureActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.reyetas);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        configureActionBar();

        mFragment = new RecipesFragment();
        isLoading = false;

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
        }

        configureMenu();
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
            mProgresView = MyApplication.showProgressView(this, mProgresView);
        }

       loadRecipes(true);
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
                "Sincronizar recetas"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(mDrawerList);
                loadRecipes(true);
            }
        });
    }

    private void loadRecipes(boolean forceLoad) {
        isLoading = true;
        invalidateOptionsMenu();
        mProgresView = MyApplication.showProgressView(RecipesActivity.this, mProgresView);
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

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        mSearchView.setOnQueryTextListener(this);

    }

    public boolean onQueryTextChange(String newText) {
        Log.d("Search","Query = " + newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        Log.d("Search", "Query = " + query + " : submitted");

        return false;
    }

//    @Override
//    public boolean onMenuItemActionExpand(MenuItem item) {
//        Log.d("*******","onMenuItemActionExpand");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF000000));
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemActionCollapse(MenuItem item) {
//        //do what you want to when close the sesarchview
//        //remember to return true;
//        Log.d("*******","onMenuItemActionCollapse");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.theme_color)));
//        return true;
//    }

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
        MyApplication.hideProgressView(mProgresView);
        isLoading = false;
        invalidateOptionsMenu();
    }


    @Override
    public void update(Observable observable, Object data) {
        //TODO update menu
        boolean changed = (boolean) data;

        Log.d(TAG, "Recipes changed? " + changed);
        MyApplication.hideProgressView(mProgresView);
        isLoading = false;
        invalidateOptionsMenu();
    }
}
