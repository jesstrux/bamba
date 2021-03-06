package bomba.com.mobiads.bamba.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import nl.changer.audiowife.AudioWife;

/**
 * Created by fred on 07/08/2017.
 */

public class BuyTuneAdapter extends RecyclerView.Adapter<BuyTuneAdapter.ViewHolder> {

    ArrayList<MyTunes> mValue;
    Context mContext;
    private int lastAnimatedPosition = -1;

    public BuyTuneAdapter(Context context, ArrayList<MyTunes> list){
        mContext = context;
        mValue = list;
    }

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onItemClick(int p);
        void onItemPlay(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
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

        animateView(holder.mView,position);

    }

    @Override
    public int getItemCount() {
        return mValue.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            mView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    itemClickCallback.onItemClick(getAdapterPosition());
                    return true;
                }
            });

            mIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.tunePlayIcon)
                itemClickCallback.onItemPlay(getAdapterPosition());
            else{
                Intent intent = new Intent(mContext, TuneDetails.class);
                MyTunes tune = mValue.get(getAdapterPosition());
                Bundle b = tune.toBundle();
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
//            Context ctx = v.getContext();
        }
    }

    //Animate view after its attached to window
    private void animateView(View view, int position){
        if(position < lastAnimatedPosition){
            lastAnimatedPosition = position;
            view.setTranslationY(-300);
            view.animate()
                    .translationY(0.0f)
                    .setInterpolator(new DecelerateInterpolator(3.0f))
                    .setDuration(700)
                    .start();
        }
    }

    public void filterList(ArrayList<MyTunes> filteredTunes) {
        this.mValue = filteredTunes;
        notifyDataSetChanged();
    }
}
