package mohamedsalama.popularmovies;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements MovieListener {
     boolean chechTablet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn);
        FrameLayout frameLayout=(FrameLayout)findViewById(R.id.detailFrameLayoute);
        if(frameLayout==null)
        {
            chechTablet=false;
        }
        else
        {
            chechTablet=true;
        }
//   if(savedInstanceState==null)

     MainActivityFragment mainActivityFragment=new MainActivityFragment();
       mainActivityFragment.setMovieListener(this);
       getSupportFragmentManager().beginTransaction().replace(R.id.fragment,mainActivityFragment).commit();

    }



    @Override
    public void setSelectedMovie(String id, String title, String poster, String overview, String vote, String release)
    {
     if(chechTablet)
     {
     DetailActivityFragment detailActivityFragment=new DetailActivityFragment();
         Bundle extras=new Bundle();
         extras.putString("id", id);
         extras.putString("title",title);
         extras.putString("poster_path",poster);
         extras.putString("overview",overview);
         extras.putString("vote_average",vote);
         extras.putString("release_date", release);
         detailActivityFragment.setArguments(extras);
         getSupportFragmentManager().beginTransaction().replace(R.id.detailFrameLayoute, detailActivityFragment).commit();
       //  getSupportFragmentManager().beginTransaction().add(R.id.detailFrameLayoute,new DetailActivityFragment());
     }
        else
     {
         Intent intent=new Intent(this,DetailActivity.class);

                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("poster_path",poster);
                intent.putExtra("overview",overview);
                intent.putExtra("vote_average",vote);
                intent.putExtra("release_date", release);
                startActivity(intent);
     }
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
