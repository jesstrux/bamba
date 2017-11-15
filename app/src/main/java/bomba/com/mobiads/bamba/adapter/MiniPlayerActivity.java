package bomba.com.mobiads.bamba.adapter;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import bomba.com.mobiads.bamba.Constants;
import bomba.com.mobiads.bamba.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.changer.audiowife.AudioWife;

public class MiniPlayerActivity extends AppCompatActivity {
    AudioWife player = null;

    @BindView(R.id.audioTitle)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_player);
        ButterKnife.bind(this);

        String file_name = getIntent().getStringExtra("file_name");
        if(file_name != null)
            mTitle.setText(file_name);

        String file_path = getIntent().getStringExtra("file_path");

        if(file_path == null)
            return;

        String base_path = getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
        File file = new File(base_path, file_path);
        if(!file.exists()){
            Toast.makeText(this, "Audio file not found, might've been deleted!", Toast.LENGTH_SHORT).show();
            Log.d("WOURA", "Audio File not found!!: " + file_path);
            return;
        }

        if(file_name == null){
            String name = file.getName();
            mTitle.setText(name.substring(0, name.lastIndexOf("."))); //name without extension
        }

        Uri uri = Uri.fromFile(file);
        ViewGroup mRootView = (ViewGroup) findViewById(R.id.activityWrapper);

//        player = AudioWife.getInstance();
        player = new AudioWife();
        player.init(this, uri)
                .useDefaultUi(mRootView, getLayoutInflater())
                .play();

        Log.d("WOURA", "Playing audio file...");
    }

    public void closeActivity(View view){
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    @Override
    protected void onDestroy() {
        if(player != null){
            player.pause();
            player.release();
        }
        super.onDestroy();
    }
}
