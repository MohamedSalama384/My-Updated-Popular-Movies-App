package mohamedsalama.popularmovies;

/**
 * Created by Mohamed Salama on 4/29/2016.
 */
public class Trailer
{
    private String key;
    private String name;

    Trailer(String k,String n)
    {

        this.key=k;
        this.name=n;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }
    public String getKey() {
        return key;
    }
}
