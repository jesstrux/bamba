package bomba.com.mobiads.bamba.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.jaredrummler.materialspinner.MaterialSpinner;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterBusiness extends BaseActivity {

    @BindView(R.id.spinner)
    MaterialSpinner mSpinner;

    @OnClick(R.id.home_btn)
    public void OnHome(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @OnClick(R.id.upgrade_btn)
    public void OnUpgrade(){
        startActivity(new Intent(this,Pay.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business);

        ButterKnife.bind(this);

        mSpinner.setItems("Internet providers","Content Providers","Digital Agency","Software Designers");


    }



}
