package bomba.com.mobiads.bamba;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bomba.com.mobiads.bamba.fragment.Overview;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by fred on 07/08/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static Typeface Roboto_Light, Lato_Bold;

    //attach calligraphy font to activity
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        super.onCreate(savedInstanceState);

        setupTypeface(this);

    }


    public static void setupTypeface(Context ctx) {
        Roboto_Light = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Light.ttf");
        Lato_Bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/Lato-Bold.ttf");

    }
}