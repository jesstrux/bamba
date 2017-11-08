package bomba.com.mobiads.bamba;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bomba.com.mobiads.bamba.adapter.BuyTuneAdapter;
import bomba.com.mobiads.bamba.data.BambaContract;
import bomba.com.mobiads.bamba.data.BambaDbHelper;
import bomba.com.mobiads.bamba.dataset.MyTunes;
import bomba.com.mobiads.bamba.ui.ProAudio;
import butterknife.BindView;
import butterknife.ButterKnife;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;
import nl.changer.audiowife.AudioWife;

public class TunesList extends Fragment implements SimpleDialog.OnDialogResultListener, BuyTuneAdapter.ItemClickCallback{
    private static final String ARG_PARAM_ISMAIN = "is_main";
    private final String TONE_ACTIONS_DIALOG = "tone_actions";

    private boolean mIsMain;
    private ViewGroup mRootView;
    LayoutInflater mInflater;

    @BindView(R.id.no_tunes)
    TextView noTunesTextView;

    @BindView(R.id.list_item)
    RecyclerView mList;

    private int mClickedPosition;

    private SQLiteDatabase database;
    ArrayList<MyTunes> myTunesArrayList = new ArrayList<>();
    MyTunes myTunes;
    Cursor mCursor;

    BuyTuneAdapter buyTuneAdapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCursor != null)
            mCursor.close();
    }

    private OnFragmentInteractionListener mListener;

    public TunesList() {}

    public static TunesList newInstance(String isMain) {
        TunesList fragment = new TunesList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ISMAIN, isMain);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsMain = getArguments().getBoolean(ARG_PARAM_ISMAIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = container;
        mInflater = inflater;

        View view = inflater.inflate(R.layout.fragment_tunes_list, container, false);
        ButterKnife.bind(this,view);


        BambaDbHelper dbhelper = new BambaDbHelper(getActivity());
        database = dbhelper.getReadableDatabase();
        mCursor = queryTones();

//        setData();
        setRealData();
        if(!myTunesArrayList.isEmpty())
            noTunesTextView.setVisibility(View.GONE);
        else
            noTunesTextView.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);
        buyTuneAdapter = new BuyTuneAdapter(getActivity(),myTunesArrayList);
        buyTuneAdapter.setItemClickCallback(this);
        mList.setAdapter(buyTuneAdapter);

        cleanSweep();

        return view;
    }


    private void cleanSweep(){
        ArrayList<String> names = new ArrayList<>();

        for (MyTunes tune : myTunesArrayList) {
            names.add(tune.getFile_path());
        }

        List<File> files = (List<File>) FileUtils.listFiles(new File(getActivity().getFilesDir().getPath(), Constants.AUDIO_RECORDER_FOLDER), null, true);
        for (File file : files) {
            try {
                if(names.contains(file.getName())){
                    Log.d("WOURA", "Audio file needed.");
                }else{
                    Log.d("WOURA", "Halipo hilo, futa: " + file.getCanonicalPath());
                    file.delete();
                }
            } catch (IOException e) {
                Log.d("WOURA", "Audio fetch error: " + e.getMessage());
            }
        }
    }

    private Cursor queryTones(){
        return database.query(
                BambaContract.TonesEntry.TABLE_NAME,
                BambaContract.TonesEntry.PROJECTION,
                null, null, null, null, BambaContract.TonesEntry.COLUMN_CREATED_AT + " DESC"
        );
    }

    public void setRealData(){
        myTunesArrayList.clear();

        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            MyTunes tune = MyTunes.fromCursor(mCursor);
            myTunesArrayList.add(tune);
            mCursor.moveToNext();
        }
    }

    public void setData(){
        myTunesArrayList.clear();

        for (int i = 0; i<4; i++){
            myTunes = new MyTunes(
                    "1",
                    "French Cuisine",
                    "path",
                    "Pending",
                    "June 3rd");
            myTunesArrayList.add(myTunes);
        }

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClick(int p) {
        mClickedPosition = p;
        SimpleListDialog.build()
                .title(R.string.title_activity_create_tone)
                .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                .items(getActivity(), R.array.tone_actions_titles)
                .show(this, TONE_ACTIONS_DIALOG);
    }

    @Override
    public void onItemPlay(int p) {
        MyTunes tune = myTunesArrayList.get(p);
        String file_path = tune.getFile_path();
        String base_path = getActivity().getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
        File file = new File(base_path, file_path);

        if(file.exists()){
            Log.d("WOURA", "Audio File found...");
            playAudio(Uri.fromFile(file));
        }else{
            Toast.makeText(getActivity(), "Audio file not found, might've been deleted!", Toast.LENGTH_SHORT).show();
            Log.d("WOURA", "Audio File not found!!: " + tune.getFile_path());
        }
    }

    private void actionOnTune(String label){
        final MyTunes tune = myTunesArrayList.get(mClickedPosition);

        switch (label) {
            case "Play":
                onItemPlay(mClickedPosition);
                break;
            case "Delete":
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete tune!")
                        .setMessage(Html.fromHtml("Are you sure you want to delete "+"<br><b>"+tune.getName()+"</b>?"))
                        .setNegativeButton("No, Cancel", null)
                        .setPositiveButton("Yes, I'm Sure",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("WOURA", "Deleting tune, id: " + tune.getId());

                                boolean tune_deletion = MoiUtils.deleteTune((AppCompatActivity) getActivity(), tune.getId());
                                if(tune_deletion){
                                    Toast.makeText(getActivity(), tune.getName() + " deleted!", Toast.LENGTH_SHORT).show();
                                    buyTuneAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            default:
                break;
        }
    }

    private void playAudio(Uri uri){
        AudioWife.getInstance().init(getContext(), uri).useDefaultUi(mRootView, getActivity().getLayoutInflater()).play();
        Log.d("WOURA", "Playing audio file...");
//        MediaPlayer player = new MediaPlayer();
//
//        try {
//            player.setDataSource(uri.getPath());
//
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    Log.d("WOURA", "onCompletion: Song completed!!");
//                }
//            });
//
//
//            player.setOnPreparedListener(
//                new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        Log.d("WOURA", "Song ready to play!!");
//                        mp.start();
//                    }
//                }
//            );
//        } catch (Exception e) {
//            Log.d("WOURA", "Error setting song!!");
//            Log.d("WOURA", e.getMessage());
//        }
//
//        player.prepareAsync();
//        Intent intent = new Intent(ctx, AccountInfoActivity.class);
//        Bundle b = toBundle(mValues.get(getAdapterPosition()));
//        intent.putExtras(b);
//        ctx.startActivity(intent);
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (TONE_ACTIONS_DIALOG.equals(dialogTag)){
            ArrayList<String> labels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);

            if(labels!=null &&!labels.isEmpty()){
                String label = String.valueOf(labels.get(0));
                Log.i("WOURA", label);
                actionOnTune(label);
            }

            return true;
        }

        return false;
    }
}