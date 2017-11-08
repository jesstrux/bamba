package bomba.com.mobiads.bamba.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProAudio extends BaseActivity {

    @OnClick(R.id.home_btn)
    public void OnHome(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_audio);

        ButterKnife.bind(this);


    }


}
