package bomba.com.mobiads.bamba.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.jaredrummler.materialspinner.MaterialSpinner;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Pay extends BaseActivity {

    @BindView(R.id.no_months)
    MaterialSpinner mMonths;
    @BindView(R.id.payment_method)
    MaterialSpinner mPayment_method;

    @OnClick(R.id.home_btn)
    public void OnHome(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        ButterKnife.bind(this);

        mMonths.setItems("1","3","6","12");
        mPayment_method.setItems("Mpesa", "Tigo Pesa","Airtel Money");


    }

}
