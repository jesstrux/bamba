package bomba.com.mobiads.bamba.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.jaredrummler.materialspinner.MaterialSpinner;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TuneDetails extends BaseActivity {

    @BindView(R.id.spinner)
    MaterialSpinner mSpinner;

    @OnClick(R.id.home_btn)
    public void OnHome(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tune_details);

        ButterKnife.bind(this);

        mSpinner.setItems("Duka la Dawa","Malaika Supermarket","Kilimanjaro Yetu","Kibanda Mkaa","Harold");

    }

}
