package ru.bda.imagedribapp.view.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.bda.imagedribapp.R;
import ru.bda.imagedribapp.db.DBController;
import ru.bda.imagedribapp.entity.Shot;

/**
 * Created by User on 10.02.2017.
 */

public class ShotRecyclerAdapter extends RecyclerView.Adapter<ShotRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Shot> mShotList;
    private DBController mDBController;
    private List<String> mTitleList = new ArrayList<>();

    public ShotRecyclerAdapter(Context context, List<Shot> shots) {
        this.mContext = context;
        this.mShotList = shots;
        mDBController = new DBController(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shot_adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Shot shot = mShotList.get(position);
        holder.mTitleView.setText(shot.getTitle());
        Log.d("adapter_log", "shot = " + shot.getDescription());
        holder.mDescriptionView.setText(shot.getDescription());
        holder.mDescriptionView.setMaxLines(2);

        Picasso.with(mContext)
                .load(shot.getImagePath())
                .error(android.R.drawable.ic_menu_gallery)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.mShotView, new Callback() {
                    @Override
                    public void onSuccess() {
                        new InsertDBTask().execute(shot);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mShotList.size();
    }

    public void setShotList(List<Shot> shots) {
        this.mShotList = shots;
    }

    private class InsertDBTask extends AsyncTask<Shot, Void, Void> {

        @Override
        protected Void doInBackground(Shot... shots) {
            synchronized (DBController.class) {
                mDBController.insertShot(shots[0]);
            }
            return null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mShotView;
        TextView mTitleView;
        TextView mDescriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            mShotView = (ImageView) itemView.findViewById(R.id.shot_image_view);
            mTitleView = (TextView) itemView.findViewById(R.id.title_view);
            mDescriptionView = (TextView) itemView.findViewById(R.id.description_view);
        }
    }
}
