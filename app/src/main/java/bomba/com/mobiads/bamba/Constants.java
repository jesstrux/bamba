package bomba.com.mobiads.bamba;

import android.media.AudioFormat;

/**
 * Created by WAKY on 1/11/2017.
 */
public class Constants {
//    public static String API_BASE_URL = "http://10.0.2.2/mobiad/";
//    public static String API_BASE_URL = "http://192.168.43.242/mobiad/";
//    public static String API_BASE_URL = "http://192.168.8.107/mobiad/";
    public static String API_BASE_URL = "http://ithaca-searchlights.000webhostapp.com/";
    public static int CALL_COST_PER_SEC = 3;

    public static int RECORDER_BPP = 16;
    public static String AUDIO_RECORDER_FILE_EXT = ".3gp";
    public static String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    public static String AUDIO_RECORDER_FOLDER = "Bamba";
    public static String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    public static int RECORDER_SAMPLERATE = 44100;
    public static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
}
