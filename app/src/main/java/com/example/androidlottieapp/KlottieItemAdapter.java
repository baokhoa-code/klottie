package com.example.androidlottieapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlottieapp.activities.LottieEditActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class KlottieItemAdapter extends RecyclerView.Adapter<KlottieItemAdapter.KlottieItemViewHolder> {

    private final List<KLottieItem> mItem;

    public KlottieItemAdapter(List<KLottieItem> mItem) {
        this.mItem = mItem;
    }

    @NonNull
    @Override
    public KlottieItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.klottie_item, parent, false);
        return new KlottieItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KlottieItemViewHolder holder, int position) {
        KLottieItem item = mItem.get(position);
        if (item == null) {
            return;
        }
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        if (mItem != null) {
            return mItem.size();
        }
        return 0;
    }

    public class KlottieItemViewHolder extends RecyclerView.ViewHolder {

        private final KLottieImageView lottieview_klottie_item;
        private final TextView titleView_klottie_item;
        private final MaterialCardView cardview_klottie_item;

        public KlottieItemViewHolder(View itemView) {
            super(itemView);
            lottieview_klottie_item = itemView.findViewById(R.id.lottieview_klottie_item);
            titleView_klottie_item = itemView.findViewById(R.id.titleView_klottie_item);
            cardview_klottie_item = itemView.findViewById(R.id.cardview_klottie_item);
        }

        public void bind(KLottieItem item) {

            lottieview_klottie_item.setLottieDrawable(item.getDrawable());

            lottieview_klottie_item.startLottie();

            titleView_klottie_item.setText(item.getName());

            cardview_klottie_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    item.setDrawable(null);

                    Context context = itemView.getContext().getApplicationContext();
                    context.startActivity(new Intent(context, LottieEditActivity.class)
                            .putExtra("item", item)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }

    }
}
