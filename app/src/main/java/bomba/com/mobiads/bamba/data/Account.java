package bomba.com.mobiads.bamba.data;

import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WAKY on 4/g3/2017.
 */
public class Account {
    private String id;
    private String name;
    private String phone;
    private String gender;
    private String location;
    private String file_path;
    private Boolean saved;
    private String created_at;

    public Account() {
    }

    public Account(String id, String name, String phone, Boolean saved, String created_at) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.saved = saved;
        this.created_at = created_at;
    }

    public Account(String id, String name, String phone, String gender, String location, String file_path, Boolean saved, String created_at) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.location = location;
        this.file_path = file_path;
        this.saved = saved;
        this.created_at = created_at;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public String getCreated_at() {
        return created_at;
    }

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

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static Account fromCursor(Cursor cursor) {
        return new Account(String.valueOf(cursor.getInt(0)), cursor.getString(1), cursor.getString(2), Boolean.valueOf(cursor.getString(3)), cursor.getString(4));
    }

    public static Account fromCursorFull(Cursor cursor) {
        return new Account(String.valueOf(cursor.getInt(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),  Boolean.valueOf(cursor.getString(6)), cursor.getString(7));
    }
}
