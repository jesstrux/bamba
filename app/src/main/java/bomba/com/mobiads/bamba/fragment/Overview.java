package bomba.com.mobiads.bamba.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
//import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import bomba.com.mobiads.bamba.Constants;
import bomba.com.mobiads.bamba.Home;
import bomba.com.mobiads.bamba.MoiUtils;
import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.TunesList;
import bomba.com.mobiads.bamba.ui.FullRecordActivity;
import bomba.com.mobiads.bamba.ui.ProAudio;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;

import static android.app.Activity.RESULT_OK;
import static bomba.com.mobiads.bamba.BaseActivity.Lato_Bold;


public class Overview extends Fragment implements SimpleDialog.OnDialogResultListener, TunesList.TunesObserver{

    private static final int CREATE_MY_AUDIO = 73;
    private static final int MANUAL_RECORD = 74;
    public static final int PICK_AUDIO = 17;
    final String CHOICE_DIALOG = "choice";
    private final String TONE_NAME = "TONE_NAME";
    private final String TONE_PHONE = "TONE_PHONE";
    private final String REGISTRATION_DIALOG = "REGISTER_TONE";
    private final String PICKED_TONE_DIALOG = "PICKED_TONE";
    private final String REGISTRATION_COMPLETE_DIALOG = "REGISTRATION_COMPLETE";
    String displayName = null;
    public static int ACTION_ADD_TUNE = 1;
    public static int ACTION_REMOVE_TUNE = 2;
    TunesList tunesList;

    private long addedId;
    TunesWatcher tunesWatcher;
    private ArrayList<String> filePaths;

    public interface TunesWatcher {
        void tunesChanged(int action, long id);
    }

    @Override
    public void tuneDeleted(String id) {
        Log.d("WOURA", "A tune with id: " + id + "was deleted in overview!");
        tunesWatcher.tunesChanged(Overview.ACTION_REMOVE_TUNE, Long.parseLong(id));
    }

    @BindView(R.id.mask_img)
    ImageView mImageMask;

    @BindView(R.id.title)
    TextView mTitle;

    FragmentModalBottomSheet fragmentModalBottomSheet;
    private boolean mForMe = false;

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

//        if(isResumed() && !isRemoving()){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            tunesList = TunesList.newInstance(true);
            fragmentTransaction.replace(R.id.tunesListPlaceholder, tunesList);
            fragmentTransaction.commit();
            tunesList.setTunesObserver(this);
//        }


        ((Home) getActivity()).initiateOverviewTunesBroadCast(new Home.OverviewTunesBroadCast() {
            @Override
            public void tunesChanged(int action, long id) {
                if(action == Overview.ACTION_REMOVE_TUNE){
//                    Toast.makeText(getActivity(), "Remove tune in Overview from broadCast!", Toast.LENGTH_SHORT).show();
                    tunesList.removeTone(Long.toString(id));
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            tunesWatcher = (TunesWatcher) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
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
                        mForMe = true;
                        showBottomSheetFragment();
                        break;
                    case "For Someone":
                        mForMe = false;
                        showBottomSheetFragment();
//                        Toast.makeText(getActivity(), "Create tone for someone", Toast.LENGTH_SHORT).show();
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
            if(which == SimpleDialog.OnDialogResultListener.CANCELED){
                String basepath = getActivity().getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
                File file = new File(basepath, displayName);
                if(file.delete())
                    Log.d("WOURA", "Unused picked file deleted!!");

                Toast.makeText(getActivity(), "You cancelled, tune not sasved.", Toast.LENGTH_LONG).show();
                return false;
            }

            String phone;
            if(!mForMe)
                phone = extras.getString(TONE_PHONE);
            else
                phone = "0717138056"; /*TODO get from logged in user*/

            String name = extras.getString(TONE_NAME);
            addedId = MoiUtils.persistInfo((AppCompatActivity) getActivity(), phone, name, displayName);
            if(addedId != -1){
                SimpleDialog.build()
                        .title("Success")
                        .msg("Your tone was successfully saved! \n Tone name: "+name+"\n")
                        .show(this, REGISTRATION_COMPLETE_DIALOG);
            }

            return true;
        }

        //UPDATE TUNE LISTS IN OVEVRVIEW AND TUNE LIST FRAGMENTS
        else if (REGISTRATION_COMPLETE_DIALOG.equals(dialogTag)){
            tunesList.newTone(addedId);
            tunesWatcher.tunesChanged(ACTION_ADD_TUNE, addedId);
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
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 93);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void recordAudio(){
        Intent intent = new Intent(getActivity(), FullRecordActivity.class);

        if(mForMe)
            intent.putExtra("userNumber", "0717138056"); /*TODO get from logged in user*/
//        startActivityForResult(intent, MANUAL_RECORD);
        startActivity(intent);
    }

    private void pickAudio(){
//        DialogProperties properties = new DialogProperties();
//        properties.selection_mode = DialogConfigs.SINGLE_MODE;
//        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File(DialogConfigs.DEFAULT_DIR);
//        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = new String[]{"mp3"};
//        properties.offset = new File("/mnt/");
//        FilePickerDialog dialog = new FilePickerDialog(getActivity(),properties);
//        dialog.setTitle("Select Tone");
//
//        dialog.setDialogSelectionListener(new DialogSelectionListener() {
//            @Override
//            public void onSelectedFilePaths(String[] files) {
//                handlePickedFile(files[0]);
//            }
//        });
//
//        dialog.show();
//        new MaterialFilePicker()
//                .withSupportFragment(this)
//                .withRequestCode(PICK_AUDIO)
//                .withFilter(Pattern.compile(".*\\.mp3$"))
//                .withPath("/mnt/")
//                .withHiddenFiles(false)
//                .start();

        FilePickerBuilder.getInstance().setMaxCount(10)
                .setSelectedFiles(filePaths)
//                .setActivityTheme(R.style.AppTheme)
                .addFileSupport("MUSIC", new String[]{".mp3"})
                .pickFile(this);
    }

    private boolean isThirtySeconds(String filePath){
        boolean b = false;
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(filePath);

        String out = "";
        // get mp3 info

        // convert duration to minute:seconds
        String duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.v("time", duration);
        long dur = Long.parseLong(duration);
        int seconds = Integer.parseInt(String.valueOf((dur % 60000) / 1000));

        Log.v("WOURA", "File duration is: " + seconds);
        return seconds <= 30;
    }

    private void handlePickedFile(String filePath){
        Log.d("WOURA", "Chosen File path" + filePath);
        File chosenFile = new File(filePath);
        displayName = chosenFile.getName();
        String err = null;

        Log.d("WOURA", "Chosen File exists: " + chosenFile.exists() + ", it's name is: " + displayName);

        if(!isThirtySeconds(filePath)){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Tune too long!!")
                    .setMessage("Your tune needs to be 30 seconds or less.")
                    .setPositiveButton("Okay",  null)
                    .setCancelable(false)
                    .show();
//            Toast.makeText("File is too long, should be 30 seconds or less.");
            return;
        }
        try {
            String mbasepath = getActivity().getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
            FileUtils.copyFile(chosenFile, new File(mbasepath, displayName));
            Log.d("WOURA", "File was successfully copied!!");

            if(mForMe)
                SimpleFormDialog.build()
                        .title("Save Tone")
                        .fields(
                                Input.name(TONE_NAME).required().hint("Tone Name")
                        )
                        .pos("SUBMIT")
                        .show(this, REGISTRATION_DIALOG);
            else
                SimpleFormDialog.build()
                        .title("Save Tone")
                        .fields(
                                Input.phone(TONE_PHONE).required().hint("Phone Number"),
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        String s = String.format(Locale.getDefault(), "Persmission results, request code is: %d was granted: %s", requestCode, grantResults[0] == PackageManager.PERMISSION_GRANTED);
        Log.d("WOURA", "\n\nThere was a result from permission.\n\n");
        Log.d("WOURA", s);
        if (requestCode == 93 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickAudio();
        } else {
            final AppCompatActivity activity = (AppCompatActivity) getActivity();
            new AlertDialog.Builder(getActivity())
                    .setTitle("You rejected permission!!")
                    .setMessage("This feature won't work without the permission, please reconsider.")
                    .setPositiveButton("Okay",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
        if(requestCode == FilePickerConst.REQUEST_CODE_DOC){
            if(resultCode== RESULT_OK && data!=null)
            {
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                Log.d("WOURA", "Selected filepath is: " + filePaths.get(0));
                handlePickedFile(filePaths.get(0));
            }
        }
        else if (requestCode == Overview.PICK_AUDIO && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Log.d("WOURA", "Chosen File path" + filePath);
            File chosenFile = new File(filePath);
            displayName = chosenFile.getName();
            String err = null;

            Log.d("WOURA", "Chosen File exists: " + chosenFile.exists() + ", it's name is: " + displayName);
            try {
                String mbasepath = getActivity().getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
                FileUtils.copyFile(chosenFile, new File(mbasepath, displayName));
                Log.d("WOURA", "File was successfully copied!!");

                if(mForMe)
                    SimpleFormDialog.build()
                        .title("Save Tone")
                        .fields(
                                Input.name(TONE_NAME).required().hint("Tone Name")
                        )
                        .pos("SUBMIT")
                        .show(this, REGISTRATION_DIALOG);
                else
                    SimpleFormDialog.build()
                            .title("Save Tone")
                            .fields(
                                    Input.phone(TONE_PHONE).required().hint("Phone Number"),
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
