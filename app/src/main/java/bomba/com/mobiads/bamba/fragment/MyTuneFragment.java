package bomba.com.mobiads.bamba.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import bomba.com.mobiads.bamba.Home;
import bomba.com.mobiads.bamba.OnBackPressedListener;
import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.TunesList;
import bomba.com.mobiads.bamba.dataset.MyTunes;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MyTuneFragment extends Fragment implements OnBackPressedListener, TunesList.DataFetchedCallback, TunesList.TunesObserver {

    @BindView(R.id.openSearchViewBtn)
    ImageButton openSearchViewBtn;

    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    private ViewGroup mRootView;
    private TunesList tunesList;

    TunesWatcher tunesWatcher;

    @Override
    public void tuneDeleted(String id) {
        Log.d("WOURA", "A tune with id: " + id + "was deleted in MyTunes!");
        tunesWatcher.tunesChanged(Overview.ACTION_REMOVE_TUNE, Long.parseLong(id));
    }

    public interface TunesWatcher {
        void tunesChanged(int action, long id);
    }

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

//        if(isResumed() && !isRemoving()){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            tunesList = TunesList.newInstance(true);
            tunesList.setDataFetchedCallback(this);
            tunesList.setTunesObserver(this);
            fragmentTransaction.replace(R.id.myTunesListPlaceholder, tunesList);
            fragmentTransaction.commit();
//        }

        ((Home) getActivity()).initiateTuneBroadCast(new Home.TunesBroadCast() {
            @Override
            public void tunesChanged(int action, long id) {
                if(action == Overview.ACTION_ADD_TUNE){
                    Log.d("WOURA", "A new tune was created in MyTunes from broadcast!");
                    tunesList.newTone(id);
                }
                else if(action == Overview.ACTION_REMOVE_TUNE){
                    Log.d("WOURA", "Remove tune in MyTunes from broadcast!");
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
        } catch (Exception e) {
            Log.d("WOURA", "Error setting up watcher: " + e.getMessage());
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    public void dataFetched(ArrayList<MyTunes> tunesList){
        Log.d("WOURA", "Response from datafetch adapter!" + tunesList.isEmpty());

        if(!tunesList.isEmpty()){
            openSearchViewBtn.setVisibility(View.VISIBLE);
            openSearchViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupSearch();
                }
            });
        }else{
            openSearchViewBtn.setVisibility(View.GONE);
        }
    }

    private void setupSearch(){
        searchView.showSearch();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("WOURA", "Searched string is: " + newText);
                tunesList.filterItems(newText);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }else{
            getActivity().onBackPressed();
        }
    }
}
