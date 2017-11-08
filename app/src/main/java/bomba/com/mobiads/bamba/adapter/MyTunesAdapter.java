package bomba.com.mobiads.bamba.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bomba.com.mobiads.bamba.R;
import bomba.com.mobiads.bamba.dataset.MyTunes;
import bomba.com.mobiads.bamba.ui.TuneDetails;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fred on 07/08/2017.
 */

public class MyTunesAdapter extends RecyclerView.Adapter<MyTunesAdapter.ViewHolder> {

    ArrayList<MyTunes> mValue;
    Context mContext;
    private int lastAnimatedPosition = -1;

    public MyTunesAdapter(Context context, ArrayList<MyTunes> list){
        mContext = context;
        mValue = list;
    }

    private MyTunesAdapter.ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onItemPlay(int p);
    }

    public void setItemClickCallback(final MyTunesAdapter.ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytune_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mItem = mValue.get(position);

        holder.mTitle.setText(holder.mItem.getName());
        holder.mDetails.setText(holder.mItem.getCreatedAtStr());
        holder.mStatus.setText(holder.mItem.getStatus());
//        holder.mView.setOnClickListener(this);

        animateView(holder.mView,position);
    }

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final LinearLayout mView;

        @BindView(R.id.tunePlayIcon)
        ImageView mIcon;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.details)
        TextView mDetails;
        @BindView(R.id.status)
        TextView mStatus;

        public MyTunes mItem;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = (LinearLayout)itemView;
            ButterKnife.bind(this,itemView);

            mView.setOnClickListener(this);
            mIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.tunePlayIcon)
                itemClickCallback.onItemPlay(getAdapterPosition());
            else
                mContext.startActivity(new Intent(mContext, TuneDetails.class));
        }
    }


    //Animate view after its attached to window
    private void animateView(View view, int position){
        if(position > lastAnimatedPosition){
            lastAnimatedPosition = position;
            view.setTranslationY(300);
            view.animate()
                    .translationY(0.0f)
                    .setInterpolator(new DecelerateInterpolator(3.0f))
                    .setDuration(700)
                    .start();
        }
    }
}
