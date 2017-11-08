package bomba.com.mobiads.bamba.dataset;

/**
 * Created by fred on 07/08/2017.
 */

public class TuneItem {

    String artist_name;
    String artist_song;
    String amount;
    int img;


    public TuneItem(String artist_name, String artist_song, String amount, int img){
        this.artist_name = artist_name;
        this.artist_song = artist_song;
        this.amount = amount;
        this.img = img;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_song() {
        return artist_song;
    }

    public void setArtist_song(String artist_song) {
        this.artist_song = artist_song;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
