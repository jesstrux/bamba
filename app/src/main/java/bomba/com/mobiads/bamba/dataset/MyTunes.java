package bomba.com.mobiads.bamba.dataset;

import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fred on 07/08/2017.
 */

public class MyTunes {

    String id;
    String name;
    String status;
    String file_path;
    String created_at;

    public MyTunes(String id, String name, String path, String status, String date_created){
        this.id = id;
        this.name = name;
        this.file_path = path;
        this.status = status;
        this.created_at = date_created;
    }

    public MyTunes(String name, String status, String date_created){
        this.id = id;
        this.name = name;
        this.status = status;
        this.created_at = date_created;
    }

    public MyTunes (){}

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile_path() {return file_path;}

    public void setFile_path(String file_path) {this.file_path = file_path;}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {return created_at;}

    public void setCreated_at(String date_created) {this.created_at = date_created;}

    public String getCreatedAtStr() {
        try {
            Date date = new Date(Long.valueOf(created_at));
            SimpleDateFormat diffFormat = new SimpleDateFormat(
                    "MMM dd, HH:mm", Locale.getDefault());
            return diffFormat.format(date);
        } catch (Exception e) {
            Log.e("WOURA", "Parsing datetime failed", e);
            return created_at;
        }
    }

    public static MyTunes fromCursor(Cursor cursor) {
        return new MyTunes(
                String.valueOf(cursor.getInt(0)), //id
                cursor.getString(1), //name
                cursor.getString(2), //file_path
                cursor.getString(3), //status
                cursor.getString(4) //created_at
        );
    }
}
