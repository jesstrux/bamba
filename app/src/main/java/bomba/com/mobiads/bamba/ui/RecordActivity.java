package bomba.com.mobiads.bamba.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import bomba.com.mobiads.bamba.Constants;
import bomba.com.mobiads.bamba.MoiUtils;
import bomba.com.mobiads.bamba.R;

public class RecordActivity extends AppCompatActivity {
    private int progressStatus = 0;
    private Handler handler;
    private ProgressBar pb;
    private TextView cur_time, fin_time;
    private boolean recording_stopped = true;
    private int cur_state;
    View rootView;
    private ImageButton btnPlayPause;
    private Button btnStart, btnStop;
    private View btnSeparator;
    private MediaPlayer mediaPlayer;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private String file_name;
    private String mBasePath;

    private Handler myHandler = new Handler();

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        handler = new Handler();
        mediaPlayer = null;

        bufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        file_name = System.currentTimeMillis() + Constants.AUDIO_RECORDER_FILE_EXT_WAV;
        mBasePath = getBaseContext().getFilesDir().getPath();

        rootView = findViewById(R.id.container);
        pb = (ProgressBar) rootView.findViewById(R.id.pb);
        cur_time = (TextView) rootView.findViewById(R.id.cur_time_text);
        fin_time = (TextView) rootView.findViewById(R.id.fin_time_text);
        btnStart = (Button) rootView.findViewById(R.id.btnStart);
        btnStop = (Button) rootView.findViewById(R.id.btnStop);
        btnSeparator = rootView.findViewById(R.id.btnSeparator);
        btnPlayPause = (ImageButton) rootView.findViewById(R.id.btnPlayPause);

        btnStart.setOnClickListener(btnClick);
        btnStop.setOnClickListener(btnClick);
        btnPlayPause.setOnClickListener(btnClick);

        setRecorderState(0);
    }


    private void startRecording(){
        progressStatus = 0;
        recording_stopped = false;

        pb.setProgress(0);
        cur_time.setText("00:00");
        setRecorderState(1);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if(i==1)
            recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"AdRecorder Thread");

        recordingThread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 30 && !recording_stopped){
                    progressStatus +=1;

                    // Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(1000);
                    }catch(InterruptedException e){
                        Log.e("WOURA", "Thread Error: " + e.getMessage());
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(progressStatus);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cur_time.setText(MoiUtils.withLeadingZero(progressStatus));
                                }
                            });
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRecorderState(2);
                    }
                });
            }
        }).start(); // Start the operation
    }

    private void setRecorderState(int state){
        cur_state = state;

        switch (state){
            case 0:{
                btnStart.setText("Record");
                btnStop.setText("Cancel");
//                btnStop.setEnabled(false);
                btnPlayPause.setVisibility(View.GONE);
                cur_time.setVisibility(View.VISIBLE);
                fin_time.setText("00:30");
                pb.setProgress(progressStatus);

//                getDialog().setCanceledOnTouchOutside(true);
                break;
            }

            case 1:{
                if(mediaPlayer != null){
                    if(mediaPlayer.isPlaying())
                        mediaPlayer.pause();

                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                btnStart.setVisibility(View.GONE);
                btnSeparator.setVisibility(View.GONE);
                btnStop.setText("Stop");

//                getDialog().setCanceledOnTouchOutside(false);
                break;
            }

            case 2:{
                stopRecording();

                pb.setProgress(0);
                fin_time.setText(cur_time.getText().toString());
                btnPlayPause.setVisibility(View.VISIBLE);
                cur_time.setVisibility(View.GONE);

                btnSeparator.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                btnStart.setText("Done");
                btnStop.setText("Retry");

                break;
            }
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnStart:{
                    startBtnClick();
                    break;
                }
                case R.id.btnStop:{
                    stopBtnClick();
                    break;
                }
                case R.id.btnPlayPause:{
                    playPauseBtnClick();
                    break;
                }
            }
        }
    };

    private void startBtnClick(){
        switch (cur_state){
            case 0:{
                startRecording();
                break;
            }
            case 2:{
                setRecorderState(0);
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                }

//                ((PremiumRegisterActivity)getActivity()).doneRecording(file_name);
                returnResult();
                break;
            }
        }
    }

    private void stopBtnClick(){
        switch (cur_state){
            case 0:{
//                getDialog().hide();
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                break;
            }
            case 1:{
                recording_stopped = true;
                break;
            }
            case 2:{
                setRecorderState(0);
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                }
                startRecording();
                break;
            }
        }
    }

    private void playPauseBtnClick(){
//        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(mBasePath, Constants.AUDIO_RECORDER_FOLDER);
        if(!file.exists()){
            file.mkdirs();
        }

        final String full_path = file.getAbsolutePath() + "/" + file_name;

        Log.i("WOURA", "File Name: " + getFilename());

        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(full_path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                myHandler.postDelayed(UpdateSongTime,100);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mPlayer) {
                        Log.i("WOURA", "Imemalize");
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(full_path);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                    }
                });

                Log.i("WOURA", "PLAYER" + mediaPlayer.toString());
            } catch (Exception e) {
                Log.i("WOURA", "Setting player path imefail" + e.getMessage());
            };
        }else{
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play);
            }else{
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    //    RecordING THUNGS
    private void stopRecording(){
        if(null != recorder){
            isRecording = false;

            int i = recorder.getState();
            if(i==1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(),getFilename());
        deleteTempFile();
    }

    private void returnResult(){
        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse(file_name));
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private String getFilename(){
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        String filepath = getBaseContext().getFilesDir().getPath();
        File file = new File(mBasePath,Constants.AUDIO_RECORDER_FOLDER);
        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + file_name);
    }

    private String getTempFilename(){
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        String filepath = getExternalCacheDir().getAbsolutePath();
        File file = new File(mBasePath,Constants.AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(mBasePath,Constants.AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + Constants.AUDIO_RECORDER_TEMP_FILE);
    }

    private void writeAudioDataToFile(){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = Constants.RECORDER_SAMPLERATE;
        int channels = 1;
        long byteRate = Constants.RECORDER_BPP * Constants.RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

//            AppLog.logString("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = (byte) Constants.RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(mediaPlayer != null){
                int startTime = mediaPlayer.getCurrentPosition();
                long mins = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
                long secs = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) startTime));
                String minZ = mins <= 9 ? "0" : "";
                String secZ = secs <= 9 ? "0" : "";

                pb.setProgress(startTime * 30 / mediaPlayer.getDuration());
                myHandler.postDelayed(this, 100);
            }
        }
    };
}
