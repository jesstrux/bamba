package bomba.com.mobiads.bamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Locale;

import bomba.com.mobiads.bamba.data.BambaContract;
import bomba.com.mobiads.bamba.data.BambaDbHelper;
import bomba.com.mobiads.bamba.dataset.MyTunes;

/**
 * Created by WAKY on 2/23/2017.
 */
public class MoiUtils {
    public static String comadNum(int number){
        return String.format("%,d", number);
    }

    public static boolean isNetworkAvailable(ConnectivityManager cm) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String withLeadingZero(int length){
        String progress_str = length < 10 ? "0" + length : "" + length;
        return String.format(Locale.getDefault(), "00:%s", progress_str);
    }

    public static ContentValues prepareValues(MyTunes tune){
        ContentValues acValues = new ContentValues();
        acValues.put(BambaContract.TonesEntry.COLUMN_PHONE, tune.getPhone());
        acValues.put(BambaContract.TonesEntry.COLUMN_NAME, tune.getName());
        acValues.put(BambaContract.TonesEntry.COLUMN_PATH, tune.getFile_path());
        acValues.put(BambaContract.TonesEntry.COLUMN_STATUS, tune.getStatus());
        acValues.put(BambaContract.TonesEntry.COLUMN_CREATED_AT, tune.getCreated_at());

        return acValues;
    }

    public static boolean persistInfo(AppCompatActivity ctx, String phone, String name, String file_name){
        BambaDbHelper dbhelper = new BambaDbHelper(ctx);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        MyTunes tune = new MyTunes();
        tune.setName(name);
        tune.setPhone(phone);
        tune.setFile_path(file_name);
        tune.setStatus("pending");
        Date time = new Date();
        tune.setCreated_at(String.valueOf(time.getTime()));

        ContentValues acValues = prepareValues(tune);
        Log.i("WOURA", acValues.toString());

        long newToneId = database.insert(BambaContract.TonesEntry.TABLE_NAME, null, acValues);
        if(newToneId != -1){
            Toast.makeText(ctx, "TUNE SAVED", Toast.LENGTH_SHORT).show();
            Log.i("WOURA", "TUNE SAVED");
            return true;
        }else{
            Toast.makeText(ctx, "TUNE SAVING FAILED", Toast.LENGTH_SHORT).show();
            Log.i("WOURA", "FAILED TO SAVE TUNE");
            return false;
        }
    }

    public static boolean deleteTune(AppCompatActivity ctx, String id){
//        String table = "beaconTable";
//        String whereClause = BambaContract.TonesEntry._ID+"=?";
//        String[] whereArgs = new String[] { String.valueOf(BambaContract.TonesEntry._ID) };
//        return database.delete(BambaContract.TonesEntry.TABLE_NAME, whereClause, whereArgs) > 0;
        BambaDbHelper dbhelper = new BambaDbHelper(ctx);
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        return database.delete(BambaContract.TonesEntry.TABLE_NAME, BambaContract.TonesEntry._ID + "=" + id, null) > 0;
    }

    public static void copyFile(FileInputStream sourceFile, File destFile) throws IOException {
        Exception ex = null;

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = sourceFile.getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e){
            Log.i("WOURA", "Couldn't do the copy!");
            ex = e;
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }

            if(ex == null)
                Log.i("WOURA", "Successfully copied file!");
        }
    }

    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
