package mohamedsalama.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
public class DetailActivityFragment extends Fragment {
    public final String apiKey="00261430013d509dc8231c3d590aee24";
    final String RESULTS = "results";
    final String KEY = "key";
    final String NAME = "name";
    private static final String REVIEW_CONTENT = "content";
    DBHelper mydp;
    ArrayList<String>moviesReviews;
    ArrayList<String>movieTrailersNames;
    ArrayList<Trailer>trailerArrayList;
    ImageView imageView;
    TextView movieTitle;
    TextView movieOverview;
    TextView movieRelease;
    TextView movieVote;
    ListView reviewslistView;
    ListView trailersListView;
    ArrayAdapter reviewsAdapter;
    ArrayAdapter trailersAdapter;
    String id ;
    String title ;
    String path ;
    String overview ;
    String vote ;
    String release;
Button controlFavorites;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        mydp=DBHelper.getInstance(getActivity());
        moviesReviews=new ArrayList<String>();
        movieTrailersNames=new ArrayList<String>();
        trailerArrayList=new ArrayList<Trailer>();
        reviewslistView=(ListView)rootView.findViewById(R.id.reviews);
        trailersListView=(ListView)rootView.findViewById(R.id.trailers);
        Bundle extras=getActivity().getIntent().getExtras();
        if ( extras == null ){
            id = getArguments().getString("id");

            title = getArguments().getString("title");
            path = getArguments().getString("poster_path");
            overview = getArguments().getString("overview");
            vote = getArguments().getString("vote_average");
            release = getArguments().getString("release_date");
        }

        else {
             id = extras.getString("id");

            title = extras.getString("title");
             path = extras.getString("poster_path");
            overview = extras.getString("overview");
             vote = extras.getString("vote_average");
             release = extras.getString("release_date");
        }
        //final String id=intent.getStringExtra("id");
       // final String title=intent.getStringExtra("title");
       // final String overview=intent.getStringExtra("overview");
       // final String path=intent.getStringExtra("poster_path");
       // final String vote=intent.getStringExtra("vote_average");
       // final String release=intent.getStringExtra("release_date");
        imageView=(ImageView)rootView.findViewById(R.id.movieImage);
        movieTitle=(TextView)rootView.findViewById(R.id.movieTitle);
        movieOverview=(TextView)rootView.findViewById(R.id.movieOverview);
        movieRelease=(TextView)rootView.findViewById(R.id.movieRelease);
        movieVote=(TextView)rootView.findViewById(R.id.movieVote);
        Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185"+path).into(imageView);
        movieTitle.setText(title);
        movieOverview.setText(overview);
        movieRelease.setText(release);
        movieVote.setText(vote);
        controlFavorites=(Button)rootView.findViewById(R.id.favor);
        int y=mydp.checkIfMovieExists(id);
        if (y==1)
        {
            System.out.println();
         controlFavorites.setText("Remove from favorites");
        }
        else
        {
        controlFavorites.setText("Add to favorites");
        }
        controlFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydp.handleAddCheck(v, id, title, overview, path, vote, release);
            }
        });
        trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key=trailerArrayList.get(position).getKey();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+key)));
            }
        });

        return  rootView;
    }
@Override
public void onStart()
{
    super.onStart();
    GetReviews getReviews=new GetReviews();
    GetTrailers getTrailers=new GetTrailers();
    if (isNetworkAvailable())
    {
        getReviews.execute(id);
        getTrailers.execute(id);
    }

}
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
    protected class GetReviews extends AsyncTask<String, Void, ArrayList<String>>
    {
        private final String LOG_TAG = GetReviews.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            try
            {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+params[0]+"/reviews?";
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
                return getMovieReviewsFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        public ArrayList<String> getMovieReviewsFromJson(String review)throws JSONException
        {
            ArrayList<String>reviews=new ArrayList<>();
            String content;
            JSONObject movieJson = new JSONObject(review);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            for (int i=0;i<movieArray.length();i++)
            {
                JSONObject jsonObject = movieArray.getJSONObject(i);
                content=jsonObject.getString(REVIEW_CONTENT);
                reviews.add(content);
            }
            return reviews;
        }
        @Override
        protected void onPostExecute(ArrayList<String> r)
      {
          moviesReviews=r;
          reviewsAdapter=new ArrayAdapter(getActivity(),R.layout.reviews_layout,R.id.reviews_text,moviesReviews);
          reviewslistView.setAdapter(reviewsAdapter);
          ListUtils.setDynamicHeight(reviewslistView);
        }
    }

    //////////////////////trailers asyanctask class///////////////////////////////
 protected class GetTrailers extends AsyncTask<String,Void,ArrayList<Trailer>>
    {
        private final String LOG_TAG = GetTrailers.class.getSimpleName();
        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            try
            {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+params[0]+"/videos?";
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
                return getMovieTrailersFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        private ArrayList<Trailer>getMovieTrailersFromJson(String movieJson) throws JSONException {
            ArrayList<Trailer> movieTrailers=new ArrayList<Trailer>();
            Trailer trailer;
            String key;
            String name;
            JSONObject trailerJsonObject = new JSONObject(movieJson);
            JSONArray trailersArray = trailerJsonObject.getJSONArray(RESULTS);
            for (int i = 0; i < trailersArray.length();i++)
            {
                JSONObject jsonObject = trailersArray.getJSONObject(i);
                key=jsonObject.getString(KEY);
                name=jsonObject.getString(NAME);
                trailer=new Trailer(key,name);
                movieTrailers.add(trailer);
            }
            return movieTrailers;
        }
        @Override
        protected void  onPostExecute(ArrayList<Trailer>trailers)
        {
            trailerArrayList=trailers;
            for (int i=0;i<trailers.size();i++)
            {
                movieTrailersNames.add(trailers.get(i).getName());
            }

            trailersAdapter=new ArrayAdapter(getActivity(),R.layout.trailers_layout,R.id.trailers_text,movieTrailersNames);
            trailersListView.setAdapter(trailersAdapter);
            ListUtils.setDynamicHeight(trailersListView);
        }
    }
}
