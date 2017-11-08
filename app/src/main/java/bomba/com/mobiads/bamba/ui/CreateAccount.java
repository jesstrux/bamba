package bomba.com.mobiads.bamba.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAccount extends BaseActivity {

    @BindView(R.id.bg_image)
    ImageView mBgImage;

    @OnClick(R.id.signin_btn)
    public void OnSignIn(){
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ButterKnife.bind(this);

        Glide.with(this)
                .load(R.drawable.signin_bg)
                .centerCrop()
                .into(mBgImage);

    }

}
