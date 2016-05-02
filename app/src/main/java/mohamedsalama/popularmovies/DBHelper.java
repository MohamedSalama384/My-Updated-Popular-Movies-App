package mohamedsalama.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Mohamed Salama on 4/26/2016.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private static DBHelper mInstance = null;
    public static final String DATABASE_NAME = "FavoritesDB.db";
    public static final String MOVIES_TABLE_NAME = "movies";
    public static final String MOVIES_COLUMN_ID = "id";
    public static final String MOVIES_COLUMN_NAME = "name";
    public static final String MOVIES_COLUMN_POSTER = "poster";
    public static final String MOVIES_COLUMN_OVERVIEW = "overview";
    public static final String MOVIES_COLUMN_VOTE = "vote_average";
    public static final String MOVIES_COLUMN_RELEASE = "release_date";
    private Context mCxt;
//    public DBHelper(Context context)
//    {
//        super(context, DATABASE_NAME, null, 1);
//    }

/////////////////////////
    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.mCxt = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table movies " +
                        "(id text primary key, name text,poster text,overview text, vote_average text,release_date text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movies");
        onCreate(db);
    }

    public boolean insertMovie  (String id, String name, String poster, String overview, String vote,String release)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIES_COLUMN_ID, id);
        contentValues.put(MOVIES_COLUMN_NAME, name);
        contentValues.put(MOVIES_COLUMN_POSTER, poster);
        contentValues.put(MOVIES_COLUMN_OVERVIEW, overview);
        contentValues.put(MOVIES_COLUMN_VOTE, vote);
        contentValues.put(MOVIES_COLUMN_RELEASE, release);
        db.insert(MOVIES_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getSpecificMovie(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from movies where id="+id+"", null );
        return res;
    }
    public ArrayList<MovieItem> getAll()
    {
        ArrayList<MovieItem>list=new ArrayList<MovieItem>();
        MovieItem mi;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from movies", null );
        while (res.moveToNext())
        {

                String id=res.getString(0);
                String title=res.getString(1);
                String poster=res.getString(2);
                String overview=res.getString(3);
                String vote=res.getString(4);
                String release=res.getString(5);
                mi=new MovieItem(id,title,poster, overview,vote,release);
                list.add(mi);

        }

        return list;
    }


    public int deleteMovie (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MOVIES_TABLE_NAME,
                "id ="+id,null);
    }

    public void handleAddCheck(View v,String i,String t,String o,String p,String vote,String r)
    {
        Button b= (Button) v;
        String text= (String)b.getText();
        if (text.equals("Add to favorites"))
        {
            boolean check=this.insertMovie(i,t,p,o,vote,r);
            if (check==true) {
                b.setText("Remove from favorites");
            }
        }
        else
        {
            int x= this.deleteMovie(i.toString());
            if (x>0){
                b.setText("Add to favorites");
            }
        }
    }
    public int checkIfMovieExists(String id) {
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from movies where id = " + id;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return 0;
        }
        cursor.close();
        return 1;
    }



}
