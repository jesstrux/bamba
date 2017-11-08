package bomba.com.mobiads.bamba.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.ui.RegisterBusiness;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static bomba.com.mobiads.bamba.BaseActivity.Lato_Bold;

public class MyAccount extends Fragment {

    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.no_tunes)
    TextView mNoTunes;

    @OnClick(R.id.business_btn)
    public void OnBusiness(){
        startActivity(new Intent(getActivity(), RegisterBusiness.class));
    }

    public static MyAccount newInstance() {
        MyAccount fragment = new MyAccount();
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
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        ButterKnife.bind(this,view);

        mUserName.setTypeface(Lato_Bold);
        mNoTunes.setTypeface(Lato_Bold);

        return view;
    }
}
