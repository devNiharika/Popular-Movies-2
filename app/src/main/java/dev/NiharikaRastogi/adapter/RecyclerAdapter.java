package dev.NiharikaRastogi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.NiharikaRastogi.MovieDetailActivity;
import dev.NiharikaRastogi.MovieDetailFragment;
import dev.NiharikaRastogi.MovieListActivity;
import dev.NiharikaRastogi.R;
import dev.NiharikaRastogi.models.Structure;
import dev.NiharikaRastogi.utils.AppController;

/**
 * Created by Niharika Rastogi on 29-12-2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    MovieListActivity mContext;
    ArrayList<Structure> arrayList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public RecyclerAdapter(MovieListActivity context, ArrayList<Structure> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_format, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String pop = arrayList.get(position).popularity;
        String l = pop.substring(0, 2) + "%";
        holder.title.setText(arrayList.get(position).title);
        holder.imageView.setImageUrl("http://image.tmdb.org/t/p/w780/" + arrayList.get(position).posterPath, imageLoader);
        holder.rating.setText(arrayList.get(position).rating);
        holder.popularity.setText(l);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MovieListActivity.mTwoPane) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movie", arrayList.get(position));
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(bundle);
                    mContext.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra("movie", arrayList.get(position));
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image)
        NetworkImageView imageView;
        @Bind(R.id.text)
        TextView title;
        @Bind(R.id.popularity)
        TextView popularity;
        @Bind(R.id.rating)
        TextView rating;
        @Bind(R.id.cardView)
        CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


