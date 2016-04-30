package mohamedsalama.popularmovies;

/**
 * Created by Mohamed Salama on 4/2/2016.
 */
public class MovieItem {
    private String ID;
    private String TITLE;
    private String POSTER;
    private String OVERVIEW;
    private String VOTE_AVERAGED;
    private String RELEASE;
    public MovieItem(String id,String title,String poster,String overview,String vote,String release)
    {
        this.ID=id;
        this.TITLE=title;
        this.POSTER=poster;
        this.OVERVIEW=overview;
        this.VOTE_AVERAGED=vote;
        this.RELEASE=release;
    }
    public void setID(String id)

    {
        this.ID=id;
    }
    public void setTITLE(String title)
    {
        this.TITLE=title;
    }
    public void setPOSTER(String poster)
    {
        this.POSTER=poster;
    }
    public void setOVERVIEW(String overview)
    {
        this.OVERVIEW=overview;
    }
    public void setVOTE_AVERAGED(String vote)
    {
        this.VOTE_AVERAGED=vote;
    }
    public void setRELEASE(String release)
    {
        this.RELEASE=release;
    }
    public String getID()
    {
        return this.ID;
    }
    public String getTITLE()
    {
        return  this.TITLE;
    }
    public String getPOSTER()
    {
        return  this.POSTER;
    }
    public String getOVERVIEW()
    {
        return  this.OVERVIEW;
    }
    public String getVOTE_AVERAGED()
    {
        return this.VOTE_AVERAGED;
    }
    public String getRELEASE()
    {
        return this.RELEASE;
    }
}

