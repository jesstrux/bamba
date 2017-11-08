package bomba.com.mobiads.bamba.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import bomba.com.mobiads.bamba.Constants;
import bomba.com.mobiads.bamba.Home;
import bomba.com.mobiads.bamba.MoiUtils;
import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.data.BambaContract;
import bomba.com.mobiads.bamba.data.BambaDbHelper;
import bomba.com.mobiads.bamba.dataset.MyTunes;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;

import static bomba.com.mobiads.bamba.MoiUtils.prepareValues;

public class FullRecordActivity extends AppCompatActivity implements View.OnClickListener, SimpleDialog.OnDialogResultListener {
    private int cur_state = 0;
    private final int STATE_INIT = 0;
    private final int STATE_RECORDING = 1;
    private final int STATE_STOPPED = 2;
    private final int STATE_PLAYING = 3;
    private final int STATE_PAUSED = 4;
    private ImageButton recordBtn, saveBtn, restartBtn, backButton;
    private TextView timeIn;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String file_name, mBasePath, mFullPath;
    private Visualizer mVisualizer;
    VisualizerView mVisualizerView;
    RecorderVisualizerView mRecorderVisualizerView;
    private Handler mHandler = new Handler();
    private Handler handler = new Handler(); // Handler for updating the
    private int currentTime = 0;
    private final String TONE_NAME = "TONE_NAME";
    private final String USER_PHONE = "USER_PHONE";
    private final String REGISTRATION_DIALOG = "REGISTER_TONE";
    private final String REGISTRATION_COMPLETE_DIALOG = "REGISTRATION_COMPLETE";
    private SQLiteDatabase database;
    private boolean unUsedRecordedFile;

    //    RECORD
//    Record button to stop button
//    STOP
//    Record button to play, show reset button
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(player != null){
            handler.removeCallbacks(updateTimeIn);
            player.stop();
            player.release();
            player = null;
        }

        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
        }

        if(recorder != null){
            unUsedRecordedFile = true;
            handler.removeCallbacks(updateRecordingVisualizer);
            recorder.stop();
            recorder.release();
            recorder = null;
        }

//        DELETE RECORDED FILE
        if(unUsedRecordedFile){
            File file = new File(mFullPath);
            if(file.delete())
                Log.d("WOURA", "Unused recorded file deleted!!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && player != null) {
            player.release();
            player = null;
        }

        if(mVisualizer != null){
            mVisualizer.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_full);
        recordBtn = (ImageButton) findViewById(R.id.btnRecord);
        saveBtn = (ImageButton) findViewById(R.id.btnSave);
        restartBtn = (ImageButton) findViewById(R.id.btnRestart);
        backButton = (ImageButton) findViewById(R.id.backButton);
        timeIn = (TextView) findViewById(R.id.cur_time_text);
        player = null;
        recorder = null;
        mBasePath = getBaseContext().getFilesDir().getPath();
        mVisualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);
        mRecorderVisualizerView = (RecorderVisualizerView) findViewById(R.id.recordVisualizerView);

        BambaDbHelper dbhelper = new BambaDbHelper(this);
        database = dbhelper.getWritableDatabase();

        backButton.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        restartBtn.setOnClickListener(this);

        setRecorderState(STATE_INIT);
    }

    private long updateInfo(MyTunes tune){
        ContentValues contentValues = prepareValues(tune);
        return database.update(BambaContract.TonesEntry.TABLE_NAME, contentValues, BambaContract.TonesEntry.COLUMN_ID + "=?",new String[] {tune.getId()});
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnRecord:{
                recordBtnClick();
                break;
            }
            case R.id.btnSave:{
                saveBtnClick();
                break;
            }
            case R.id.btnRestart:{
                restartBtnClick();
                break;
            }
            case R.id.backButton:{
                goBack();
                break;
            }
        }
    }

    private void setRecorderState(int state){
        cur_state = state;
        stateChanged(state);
    }

    private void stateChanged(int state){
        switch(state){
            case STATE_INIT:{
                saveBtn.setVisibility(View.GONE);
                restartBtn.setVisibility(View.GONE);
                recordBtn.setImageResource(R.drawable.ic_record);
                mVisualizerView.setVisibility(View.GONE);
                mRecorderVisualizerView.setVisibility(View.VISIBLE);
                timeIn.setText("00:00");
                currentTime = 0;

                break;
            }
            case STATE_RECORDING:{
                recordBtn.setImageResource(R.drawable.ic_stop);
                mVisualizerView.setVisibility(View.GONE);
                mRecorderVisualizerView.setVisibility(View.VISIBLE);
                break;
            }
            case STATE_STOPPED:{
                saveBtn.setVisibility(View.VISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
                recordBtn.setImageResource(R.drawable.ic_play);
                mVisualizerView.setVisibility(View.VISIBLE);
                mRecorderVisualizerView.setVisibility(View.GONE);
                currentTime = 0;
                mRecorderVisualizerView.clear();
                break;
            }
            case STATE_PLAYING:{
                recordBtn.setImageResource(R.drawable.ic_pause);
                break;
            }
            case STATE_PAUSED:{
                recordBtn.setImageResource(R.drawable.ic_play);
                break;
            }
        }
    }

    private void recordBtnClick(){
        if(cur_state == STATE_INIT){
            startRecording();
        }else if (cur_state == STATE_RECORDING){
            stopRecording();
        }
        else if (cur_state == STATE_STOPPED){
            playAudio();
        }
        else if (cur_state == STATE_PLAYING){
            pauseAudio();
        }
        else if (cur_state == STATE_PAUSED){
            playAudio();
        }
    }

    private void saveBtnClick(){
        if(player !=null){
            player.stop();
            player.release();
            player = null;
        }

        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
        }


        SimpleFormDialog.build()
                .title("Save Tone")
                .fields(
                        Input.name(TONE_NAME).required().hint("Tone Name")
                )
                .pos("SUBMIT")
                .show(this, REGISTRATION_DIALOG);
    }

    private void restartBtnClick(){
        if(player !=null){
            player.stop();
            player.release();
            player = null;
        }

        if(mVisualizer != null){
            mVisualizer.setEnabled(false);
        }

        setRecorderState(STATE_INIT);
    }

    private void goBack(){
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    private void startRecording(){
        if(!hasRecordPermission(this)){
            final AppCompatActivity activity = this;
            new AlertDialog.Builder(activity)
                    .setTitle("Hey there!")
                    .setMessage("Please provide the permission to start recording.")
                    .setPositiveButton("Okay",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
//                            }
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }else{
            doTheRecording();
        }
    }

    private void runRecorder(File folder){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        currentTime = 0;

        file_name = System.currentTimeMillis() + Constants.AUDIO_RECORDER_FILE_EXT;
        mFullPath = folder.getAbsolutePath() + "/" + file_name;
        recorder.setOutputFile(mFullPath);
        try {
            recorder.prepare();
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            recorder.start();
            handler.post(updateRecordingVisualizer);
            setRecorderState(STATE_RECORDING);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error setting up recorder", Toast.LENGTH_LONG).show();
            Log.i("WOURA", e.getMessage());
        }
    }

    private void doTheRecording(){
//        mBasePath =  + "/" + Constants.AUDIO_RECORDER_FOLDER;

        File folder = new File(mBasePath,Constants.AUDIO_RECORDER_FOLDER);
//        File folder = new File(Environment.getExternalStorageDirectory(), Constants.AUDIO_RECORDER_FOLDER);

        if(!folder.exists()){
            Log.i("WOURA", "App directory doesn't exist!");
            try {
                boolean filesCreated = folder.mkdirs();
                Log.i("WOURA", "App directory creation: " + filesCreated);
                runRecorder(folder);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Failed to create directory!", Toast.LENGTH_LONG).show();
                Log.i("WOURA", "Failed to create directory!");
                Log.i("WOURA", e.getMessage());
            }
        }else{
            runRecorder(folder);
        }
    }

    private void stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;
        handler.removeCallbacks(updateRecordingVisualizer);
        setRecorderState(STATE_STOPPED);
    }

    private void playAudio(){
        if(player == null){
            player = new MediaPlayer();
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            timeIn.setText("00:00");
            currentTime = 0;

            try {
                player.setDataSource(mFullPath);

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        setRecorderState(STATE_PAUSED);
                        mVisualizer.setEnabled(false);
                    }
                });


                player.setOnPreparedListener(
                        new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                setupVisualizerFxAndUI(mp.getAudioSessionId());
                                mVisualizer.setEnabled(true);
                                mp.start();
                                handler.post(updateTimeIn);
                                setRecorderState(STATE_PLAYING);
                            }
                        }
                );

                player.prepareAsync();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error setting up player!", Toast.LENGTH_LONG).show();
                Log.i("WOURA", "Error: " + e.getMessage());
            }
        }else{
            player.start();
            handler.post(updateTimeIn);
            mVisualizer.setEnabled(true);
            setRecorderState(STATE_PLAYING);
        }
    }

    private void pauseAudio(){
        player.pause();
        setRecorderState(STATE_PAUSED);
        handler.removeCallbacks(updateTimeIn);
    }

    private void setupVisualizerFxAndUI(int source) {
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(source);

        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    private int milliTime;
    Runnable updateRecordingVisualizer = new Runnable() {
        @Override
        public void run() {
            if (cur_state == STATE_RECORDING) // if we are already recording
            {
                // get the current amplitude
                int x = recorder.getMaxAmplitude();
                mRecorderVisualizerView.addAmplitude(x); // update the VisualizeView
                mRecorderVisualizerView.invalidate(); // refresh the VisualizerView

                milliTime += 40;

                if (milliTime >= 1000) {
                    milliTime = 0;
                    currentTime++;
                    timeIn.setText(getDoubleNumbers(currentTime));
                }

                if(currentTime > 29){
                    stopRecording();
                }

                // update in 40 milliseconds
                handler.postDelayed(this, 40);

            }
        }
    };

    Runnable updateTimeIn = new Runnable() {
        @Override
        public void run() {
            if (cur_state == STATE_PLAYING)
            {
                currentTime++;
                timeIn.setText(getDoubleNumbers(currentTime));
                // update in 1 second
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (REGISTRATION_DIALOG.equals(dialogTag)){
            if(which == SimpleDialog.OnDialogResultListener.CANCELED){
                unUsedRecordedFile = true;
                return false;
            }

            unUsedRecordedFile = false;
            String name = extras.getString(TONE_NAME);
            if(MoiUtils.persistInfo(this, name, file_name)){
                SimpleDialog.build()
                        .title("Success")
                        .msg("Your tone was successfully saved! \n Tone name: "+name+"\n" + "\n File path: " + mFullPath)
                        .show(this, REGISTRATION_COMPLETE_DIALOG);
            }

            return true;
        }else if(REGISTRATION_COMPLETE_DIALOG.equals(dialogTag)){
            setRecorderState(STATE_INIT);
            startActivity(new Intent(this, Home.class));
//            returnResult();
        }
        return false;
    }

    private String getDoubleNumbers(int n){
        String str =  n > 9 ? "" + n : "0" + n;
        return "00:" + str;
    }

    private void returnResult(){
        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse(mFullPath));
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doTheRecording();
        } else {
            final AppCompatActivity activity = (AppCompatActivity) this;
            new AlertDialog.Builder(this)
                    .setTitle("You rejected permission!!")
                    .setMessage("This feature won't work without the permission, please reconsider.")
                    .setPositiveButton("Okay",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
//                            }
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .show();
        }
    }

    public static boolean hasRecordPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(recorder != null){
            Log.d("WOURA", "user pressed the back button");
//            recorder.pause();
            final AppCompatActivity activity = (AppCompatActivity) this;
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Recording")
                    .setMessage("Are you sure you want to cancel recording?")
                    .setPositiveButton("Yes, I'm Sure!",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getBaseContext(), "Recording stopped!", Toast.LENGTH_LONG).show();
                            FullRecordActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No, Cancel!", null)
                    .setCancelable(false)
                    .show();
        }else
            super.onBackPressed();
    }
}
