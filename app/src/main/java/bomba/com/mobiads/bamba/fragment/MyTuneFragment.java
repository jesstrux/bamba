package bomba.com.mobiads.bamba.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

import bomba.com.mobiads.bamba.Constants;
import bomba.com.mobiads.bamba.OnBackPressedListener;
import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.adapter.MyTunesAdapter;
import bomba.com.mobiads.bamba.data.BambaContract;
import bomba.com.mobiads.bamba.data.BambaDbHelper;
import bomba.com.mobiads.bamba.dataset.MyTunes;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.changer.audiowife.AudioWife;


public class MyTuneFragment extends Fragment implements OnBackPressedListener, MyTunesAdapter.ItemClickCallback {

    @BindView(R.id.list_item)
    RecyclerView mList;

    @BindView(R.id.no_tunes)
    TextView noTunesTextView;

    @BindView(R.id.openSearchViewBtn)
    ImageButton openSearchViewBtn;

    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    private SQLiteDatabase database;
    ArrayList<MyTunes> myTunesArrayList = new ArrayList<>();
    MyTunesAdapter myTunesAdapter;
    Cursor mCursor;

    MyTunes myTunes;
    private ViewGroup mRootView;


    public static MyTuneFragment newInstance() {
        MyTuneFragment fragment = new MyTuneFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_tones_new, container, false);
        mRootView = container;
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
        myTunesAdapter = new MyTunesAdapter(getActivity(),myTunesArrayList);
        myTunesAdapter.setItemClickCallback(this);

        mList.setAdapter(myTunesAdapter);

        if(!myTunesArrayList.isEmpty()){
            openSearchViewBtn.setVisibility(View.VISIBLE);
            openSearchViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.showSearch();
                }
            });
        }else{
            openSearchViewBtn.setVisibility(View.GONE);
        }

        return view;
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

//            for(int i = 0; i < 26; i++)
                myTunesArrayList.add(tune);

            mCursor.moveToNext();
        }
    }


    public void setData(){
        myTunesArrayList.clear();

        for(int i = 0; i < 6; i++){
            myTunes = new MyTunes(
                    "1",
                    "French Cuisine",
                    "path",
                    "Pending",
                    "June 3rd");
            myTunesArrayList.add(myTunes);
        }

    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }else{
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onItemPlay(int p) {
        MyTunes tune = myTunesArrayList.get(p);
        String file_path = tune.getFile_path();
        String base_path = getActivity().getFilesDir().getPath() + "/" + Constants.AUDIO_RECORDER_FOLDER;
        File file = new File(base_path, file_path);

        if(file.exists()){
            Log.d("WOURA", "Audio File found...");
            AudioWife.getInstance().init(getContext(), Uri.fromFile(file)).useDefaultUi(mRootView, getActivity().getLayoutInflater()).play();
            Log.d("WOURA", "Playing audio file...");
        }else{
            Toast.makeText(getActivity(), "Audio file not found, might've been deleted!", Toast.LENGTH_SHORT).show();
            Log.d("WOURA", "Audio File not found!!: " + tune.getFile_path());
        }
    }
}
