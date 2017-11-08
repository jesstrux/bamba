package bomba.com.mobiads.bamba.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTone extends BaseActivity {


    @OnClick(R.id.home_btn)
    public void OnHome(){
        NavUtils.navigateUpFromSameTask(this);
    }
    @OnClick(R.id.request_btn)
    public void RequestAudio(){
        startActivity(new Intent(this,ProAudio.class));
    }

    @OnClick(R.id.create_tone_btn)
    public void OnCreateTone(){
       startActivity(new Intent(this,MyTone.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tone);


        ButterKnife.bind(this);

    }

}
