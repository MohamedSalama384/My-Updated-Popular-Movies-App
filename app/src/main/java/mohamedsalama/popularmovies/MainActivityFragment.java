package mohamedsalama.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public final String apiKey="00261430013d509dc8231c3d590aee24";
    final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    DBHelper dpHelper;
    ArrayList<MovieItem>list;
    GridView gridView;
    GridViewAdapter gridViewAdapter;
    ArrayList <String>paths;
    MovieItem mI;
    MovieListener movieListener;
    public MainActivityFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        dpHelper=DBHelper.getInstance(getActivity());
         list=new ArrayList<MovieItem>();
         gridView=(GridView)rootView.findViewById(R.id.gridview_posters);
         paths=new ArrayList<String>();
         gridViewAdapter=new GridViewAdapter(getActivity());
         gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie=list.get(position);
                String movieID=movie.getID();
                String movieTitle=movie.getTITLE();
                String moviePoster=movie.getPOSTER();
                String movieOverview=movie.getOVERVIEW();
                String movieVote=movie.getVOTE_AVERAGED();
                String movieReleaseDate=movie.getRELEASE();
                //////////////new work/////////////////
                movieListener.setSelectedMovie(movieID,movieTitle,moviePoster,movieOverview,movieVote,movieReleaseDate);
                //////////////////////////////////////
//                Intent intent=new Intent(getActivity(),DetailActivity.class);
//                intent.putExtra(MOVIE_ID,movieID);
//                intent.putExtra(MOVIE_TITLE,movieTitle);
//                intent.putExtra(POSTER_PATH,moviePoster);
//                intent.putExtra(MOVIE_OVERVIEW,movieOverview);
//                intent.putExtra(MOVIE_VOTE_AVERAGE,movieVote);
//                intent.putExtra(MOVIE_RELEASE_DATE, movieReleaseDate);
//                startActivity(intent);
            }
        });
         return rootView;
    }

    public void setMovieListener(MovieListener m)
    {
        movieListener=m;
    }

    private void updateMovies(String type)
    {
        GetMovies fetchMovies=new GetMovies();
        fetchMovies.execute(type);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String type = prefs.getString(getString(R.string.pref_category_key),
                getString(R.string.pref_category_default));
        if(type.equals("favorites"))
        {

            list=dpHelper.getAll();
             paths=getAllPaths(list);
            gridViewAdapter.setData(paths);
            gridView.setAdapter(gridViewAdapter);
        }
        else
        {
            if (isNetworkAvailable())
            {
                updateMovies(type);
            }
            else
            {
                Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_LONG).show();
            }

        }

    }
    public ArrayList<String> getAllPaths(ArrayList<MovieItem>list)
    {
        ArrayList<String>array_list=new ArrayList<String>();
        for (int i=0;i<list.size();i++)
        {
            array_list.add(list.get(i).getPOSTER());
        }
        return array_list;
    }
    private class  GetMovies extends AsyncTask<String, Void, ArrayList<MovieItem>>
    {
        private final String LOG_TAG = GetMovies.class.getSimpleName();
        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            try
            {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+params[0]+"?";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, apiKey)
                        .build();
                System.out.println(builtUri);
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<MovieItem> getMovieDataFromJson(String movieJsonStr) throws JSONException {
            ArrayList<MovieItem>movieItems=new ArrayList<MovieItem>();

            String id;
            String title;
            String poster;
            String overview;
            String vote;
            String release;
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            System.out.print("ana");
            if(paths!=null) {
                System.out.print("ama");
            paths.clear();
            paths=new ArrayList<>();

            }

            for (int i = 0; i < movieArray.length();i++)
            {
                JSONObject jsonObject = movieArray.getJSONObject(i);
                id=jsonObject.getString(MOVIE_ID);
                title=jsonObject.getString(MOVIE_TITLE);
                poster = jsonObject.getString(POSTER_PATH);
                overview=jsonObject.getString(MOVIE_OVERVIEW);
                vote=jsonObject.getString(MOVIE_VOTE_AVERAGE);
                release=jsonObject.getString(MOVIE_RELEASE_DATE);
                MovieItem movie=new MovieItem(id,title,poster,overview,vote,release);
                paths.add(poster);
                movieItems.add(movie);
            }
            return movieItems;
        }
        @Override
        protected void onPostExecute(ArrayList<MovieItem> array) {

            list=array;
           gridViewAdapter.setData(paths);
            gridView.setAdapter(gridViewAdapter);

        }
    }
}
