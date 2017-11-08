package bomba.com.mobiads.bamba.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyTone extends BaseActivity {

    @OnClick(R.id.home_btn)
    public void OnHome(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tone);

        ButterKnife.bind(this);

    }

}
