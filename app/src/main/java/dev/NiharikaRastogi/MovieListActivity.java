package dev.NiharikaRastogi;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.NiharikaRastogi.adapter.RecyclerAdapter;
import dev.NiharikaRastogi.database.FavMoviesContract;
import dev.NiharikaRastogi.database.FavMoviesProvider;
import dev.NiharikaRastogi.models.Structure;
import dev.NiharikaRastogi.utils.AppController;
import dev.NiharikaRastogi.utils.GridAutofitLayoutManager;

public class MovieListActivity extends AppCompatActivity {
    public static boolean mTwoPane;
    ArrayList<Structure> arrayList = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;
    AlertDialog dialog;
    int selected;
    @BindString(R.string.POPULARITY)
    String sortBy;
    @BindString(R.string.baseURL)
    String baseURL;
    @BindString(R.string.KEY)
    String KEY;
    int page;
    String TAG = "MovieListActivity";
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = ButterKnife.findById(this, R.id.recyclerView);
        gridLayoutManager = new GridAutofitLayoutManager(getApplicationContext(), 150);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
            setupRecyclerView();
        } else {
            selected = 0;
            page = 1;
            setupRecyclerView();
            fetch();
        }
        Log.d("OnCreate", "OnCreate called");
        if (ButterKnife.findById(this, R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        } else mTwoPane = false;
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerAdapter = new RecyclerAdapter(MovieListActivity.this, arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (selected != 2 && gridLayoutManager.findLastVisibleItemPosition() == arrayList.size() - 1) {
                    fetch();
                }
            }
        });
    }

    private void fetch() {
        String finalURL = baseURL + KEY + "&sort_by=" + sortBy + "&page=" + page;
        Log.d("URL", finalURL);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(finalURL,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                arrayList.add(new Structure(jsonObject.getInt("id"), jsonObject.getString("title"), jsonObject.getString("poster_path"), jsonObject.getString("popularity"), jsonObject.getString("vote_average"), jsonObject.getString("overview"), jsonObject.getString("release_date"), jsonObject.getString("backdrop_path")));
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            page++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "Error: " + error.getMessage());
//                for (int i = 0; i < 10; i++) {
//                    arrayList.add(new Structure(12345, " Test Title", "poster_path", "100", "8.9", "overview", "release_date", "backdrop_path"));
//                }
//                recyclerAdapter.notifyDataSetChanged();
                Snackbar.make(findViewById(R.id.root), "Aw, Snap! Something went wrong", Snackbar.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "JSON_OBJECT_REQUEST");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("DATA", arrayList);
        outState.putInt("selected", selected);
        outState.putParcelable("LayoutManager", gridLayoutManager.onSaveInstanceState());
        outState.putInt("page", page);
        Log.d("onSaveInstanceState", "onSaveInstanceState called");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        arrayList = savedInstanceState.getParcelableArrayList("DATA");
        selected = savedInstanceState.getInt("selected");
        gridLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("LayoutManager"));
        page = savedInstanceState.getInt("page");
        Log.d("onRestoreInstanceState", "onRestoreInstanceState called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort) {
            displayDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Sort by")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.options, selected,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, which + " selected");
                                if (which != selected) {
                                    selected = which;
                                    page = 1;
                                    arrayList.clear();
                                    recyclerAdapter.notifyDataSetChanged();
                                    if (selected == 0) {
                                        sortBy = getResources().getString(R.string.POPULARITY);
                                        fetch();
                                    } else if (selected == 1) {
                                        sortBy = getResources().getString(R.string.RATING);
                                        fetch();
                                    } else if (selected == 2) {
                                        loadFavouriteMovies();
                                    }
                                }
                                dialog.dismiss();
                            }
                        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    public void loadFavouriteMovies() {

        // Retrieve movie records
        FavMoviesProvider provider = new FavMoviesProvider(this);

        Cursor c = provider.query(Uri.parse(FavMoviesContract.CONTENT_URI.toString()), null, null, null, "title");
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    arrayList.add(new Structure(
                            c.getInt(c.getColumnIndex("id")),
                            getValueFor(c, "title"),
                            getValueFor(c, "posterImgURL"),
                            getValueFor(c, "popularity"),
                            getValueFor(c, "rating"),
                            getValueFor(c, "overview"),
                            getValueFor(c, "releaseDate"),
                            getValueFor(c, "backDropImgURL")

                    ));
                } while (c.moveToNext());
            }
            c.close();
        }

        if (arrayList.size() > 0) {
            Log.d("fav", Arrays.toString(arrayList.toArray()));
            recyclerAdapter.notifyDataSetChanged();
            setupRecyclerView();
        } else {
            Snackbar.make(ButterKnife.findById(this, R.id.root), "No favorite movies yet!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private String getValueFor(Cursor c, String column) {
        return c.getString(c.getColumnIndex(column));
    }
}
