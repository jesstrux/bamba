package bomba.com.mobiads.bamba.fragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
//import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import bomba.com.mobiads.bamba.Constants;
import bomba.com.mobiads.bamba.Home;
import bomba.com.mobiads.bamba.MoiUtils;
import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.adapter.BuyTuneAdapter;
import bomba.com.mobiads.bamba.data.BambaContract;
import bomba.com.mobiads.bamba.data.BambaDbHelper;
import bomba.com.mobiads.bamba.dataset.MyTunes;
import bomba.com.mobiads.bamba.ui.FullRecordActivity;
import bomba.com.mobiads.bamba.ui.ProAudio;
import bomba.com.mobiads.bamba.ui.RecordActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;
import nl.changer.audiowife.AudioWife;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static bomba.com.mobiads.bamba.BaseActivity.Lato_Bold;


public class Overview extends Fragment implements SimpleDialog.OnDialogResultListener{

    private static final int CREATE_MY_AUDIO = 73;
    private static final int MANUAL_RECORD = 74;
    public static final int PICK_AUDIO = 17;
    final String CHOICE_DIALOG = "choice";
    private final String TONE_NAME = "TONE_NAME";
    private final String REGISTRATION_DIALOG = "REGISTER_TONE";
    private final String PICKED_TONE_DIALOG = "PICKED_TONE";
    private final String REGISTRATION_COMPLETE_DIALOG = "REGISTRATION_COMPLETE";
    String displayName = null;

    @BindView(R.id.mask_img)
    ImageView mImageMask;

    @BindView(R.id.title)
    TextView mTitle;

    FragmentModalBottomSheet fragmentModalBottomSheet;

    @OnClick(R.id.tone_btn)
    public void OnTone(){
//        startActivity(new Intent(getActivity(), CreateTone.class));
        SimpleListDialog.build()
                .title(R.string.title_activity_create_tone)
                .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                .items(getActivity(), R.array.create_tone_list_titles)
                .show(this, CHOICE_DIALOG);
    }

    public static Overview newInstance() {
        Overview fragment = new Overview();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        ButterKnife.bind(this,view);

        mTitle.setTypeface(Lato_Bold);

        Glide.with(getActivity())
                .load(R.drawable.signin_bg)
                .into(mImageMask);
        return view;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (CHOICE_DIALOG.equals(dialogTag)){
            ArrayList<String> labels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);

            if(labels!=null &&!labels.isEmpty()){
                String label = String.valueOf(labels.get(0));
                Log.i("WOURA", label);

                switch (label) {
                    case "Your Own":
//                        startActivity(new Intent(getActivity(), MyTone.class));
                        showBottomSheetFragment();
                        break;
                    case "For Someone":
                        Toast.makeText(getActivity(), "Create tone for someone", Toast.LENGTH_SHORT).show();
                        break;
                    case "Request pro":
                        startActivity(new Intent(getActivity(), ProAudio.class));
                        break;
                    default:
                        break;
                }
            }

            return true;
        }

        else if (REGISTRATION_DIALOG.equals(dialogTag)){
            String name = extras.getString(TONE_NAME);

            if(MoiUtils.persistInfo((AppCompatActivity) getActivity(), name, displayName)){
                SimpleDialog.build()
                        .title("Success")
                        .msg("Your tone was successfully saved! \n Tone name: "+name+"\n")
                        .show(this, REGISTRATION_COMPLETE_DIALOG);
            }

            return true;
        }

        return false;
    }

    private void showBottomSheetFragment() {
        fragmentModalBottomSheet = new FragmentModalBottomSheet();
        fragmentModalBottomSheet.show(getActivity().getSupportFragmentManager(), "BottomSheet Fragment");
        fragmentModalBottomSheet.setTargetFragment(this, CREATE_MY_AUDIO);
    }

    private void chooseFile(){
        if (hasStoragePermission(getActivity())) {
            pickAudio();
        }else{
            final AppCompatActivity activity = (AppCompatActivity) getActivity();
            new AlertDialog.Builder(activity)
                    .setTitle("Hello there!")
                    .setMessage("Please provide the permission to choose the file.")
                    .setPositiveButton("Okay",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void recordAudio(){
        Intent intent = new Intent(getActivity(), FullRecordActivity.class);
//        startActivityForResult(intent, MANUAL_RECORD);
        startActivity(intent);
    }

    private void pickAudio(){
        new MaterialFilePicker()
                .withSupportFragment(this)
                .withRequestCode(PICK_AUDIO)
                .withFilter(Pattern.compile(".*\\.mp3$"))
                .withHiddenFiles(false)
                .start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickAudio();
        } else {
            final AppCompatActivity activity = (AppCompatActivity) getActivity();
            new AlertDialog.Builder(getActivity())
                    .setTitle("You rejected permission!!")
                    .setMessage("This feature won't work without the permission, please reconsider.")
                    .setPositiveButton("Okay",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .show();
        }
    }

    public static boolean hasStoragePermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Overview.PICK_AUDIO && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Toast.makeText(getActivity(), "Chosen File path" + filePath, Toast.LENGTH_SHORT).show();
            File chosenFile = new File(filePath);
            displayName = chosenFile.getName();
            String err = null;

            Log.d("WOURA", "Chosen File exists: " + chosenFile.exists() + ", it's name is: " + displayName);
            try {
                String mbasepath = getActivity().getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
                FileUtils.copyFile(chosenFile, new File(mbasepath, displayName));
                Log.d("WOURA", "File was successfully copied!!");

                SimpleFormDialog.build()
                        .title("Save Tone")
                        .fields(
                                Input.name(TONE_NAME).required().hint("Tone Name")
                        )
                        .pos("SUBMIT")
                        .show(this, REGISTRATION_DIALOG);
            } catch (Exception e) {
                err = e.getMessage();
                Log.d("WOURA", "Error copying file!!");
                Log.d("WOURA", "\nError: " + e.getMessage()+"\n");

                Toast.makeText(getActivity(), "Sorry, we couldn't copy your tune.", Toast.LENGTH_LONG).show();
            }
        }

        else if (requestCode == Overview.MANUAL_RECORD && resultCode == RESULT_OK) {
            String file_path = String.valueOf(data.getData());
            if(file_path != null){
                Log.i("WOURA", "No result returned from record activity");
                Log.i("WOURA", "" + file_path);
                Toast.makeText(getActivity(), "Result returned from record activity", Toast.LENGTH_LONG).show();
            }else{
                Log.i("WOURA", "No result returned from record activity");
                Toast.makeText(getActivity(), "No Result returned from record activity", Toast.LENGTH_LONG).show();
            }
        }

        else if(requestCode == Overview.CREATE_MY_AUDIO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            int choice = (int) extras.get("choice");

            if(choice == 0)
                chooseFile();
            else
                recordAudio();
        }
    }
}
