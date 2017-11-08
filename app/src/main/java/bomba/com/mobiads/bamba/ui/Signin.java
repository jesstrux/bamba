package bomba.com.mobiads.bamba.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.Home;
import bomba.com.mobiads.bamba.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Signin extends BaseActivity {

    @BindView(R.id.bg_image)
    ImageView mBgImage;

    @OnClick(R.id.signin_btn)
    public void OnSignIn(){
        startActivity(new Intent(this, Home.class));
    }

    @OnClick(R.id.register)
    public void OnRegister(){
        startActivity(new Intent(this, CreateAccount.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        ButterKnife.bind(this);

        Glide.with(this)
                .load(R.drawable.signin_bg)
                .centerCrop()
                .into(mBgImage);

    }

}
