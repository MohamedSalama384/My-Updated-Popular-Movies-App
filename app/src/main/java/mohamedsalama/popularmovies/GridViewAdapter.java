package mohamedsalama.popularmovies;

/**
 * Created by Mohamed Salama on 3/24/2016.
 */
    import java.util.ArrayList;
    import android.app.Activity;
    import android.content.Context;
    import android.graphics.Bitmap;
    import android.graphics.Point;
    import android.text.Html;
    import android.util.DisplayMetrics;
    import android.view.Display;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.BaseAdapter;
    import android.widget.ImageView;
    import android.widget.TextView;
    import com.squareup.picasso.Picasso;

    public class GridViewAdapter extends BaseAdapter {

        Context context;
        ArrayList <String> path;
        public GridViewAdapter(Context c)
        {
            path=new ArrayList<String>();
            this.context=c;
        }

        public void setData(ArrayList<String> p){
            path.clear();
            path.addAll(p);
        }
        @Override
        public int getCount() {
            return path.size();
        }

        @Override
        public Object getItem(int position) {
            return path.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_posters, parent, false);
            }
            ImageView imageView=(ImageView)convertView.findViewById(R.id.list_item_posters_imageview);

            Picasso.with(context).load("https://image.tmdb.org/t/p/w185"+path.get(position)).into(imageView);
        return convertView;
        }
    }

