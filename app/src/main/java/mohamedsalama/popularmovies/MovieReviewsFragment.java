package mohamedsalama.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieReviewsFragment extends Fragment {
    ArrayList<String>reviewsList;
    ListView listView;
    private ArrayAdapter<String> reviewAdapter;
    public MovieReviewsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        listView= (ListView) rootView.findViewById(R.id.Listview_reviews);
        Intent intent=getActivity().getIntent();
        reviewsList=intent.getStringArrayListExtra("reviews");
        reviewAdapter=new ArrayAdapter<String>(getActivity(),
                R.layout.list_movie_reviews,
                R.id.list_movie_textview,reviewsList);
        listView.setAdapter(reviewAdapter);
        return rootView;
    }
}
