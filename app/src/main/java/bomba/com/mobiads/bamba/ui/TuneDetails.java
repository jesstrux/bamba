package bomba.com.mobiads.bamba.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import bomba.com.mobiads.bamba.BaseActivity;
import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.adapter.MiniPlayerActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TuneDetails extends BaseActivity {

    @BindView(R.id.spinner)
    MaterialSpinner mSpinner;

    @BindView(R.id.audioTitle)
    TextView audioTitle;

    @BindView(R.id.audioDescription)
    TextView audioDescription;

    String iPath;
    String iName;

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

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        iName = args.getString("name");
        String iDescription = args.getString("description");
        iPath = args.getString("file_path");

        if(iName != null)
            audioTitle.setText(iName);

        if(iDescription != null)
            audioDescription.setText(iDescription);

        Log.d("WOURA", "Intent args: " + args.toString());
    }

    public void playAudio(View view) {
        Intent intent = new Intent(this, MiniPlayerActivity.class);
        intent.putExtra("file_path", iPath);
        intent.putExtra("file_name", iName);
        startActivity(intent);
    }
}
