package com.example.androidlottieapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlottieapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.LayerViewHolder> {

    private final Context context;
    private final List<String> mLayersList;
    private final LayerAdapterListener listener;

    public LayerAdapter(List<String> mLayersList, Context context, LayerAdapterListener listener) {
        this.mLayersList = mLayersList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layer_item, parent, false);
        return new LayerAdapter.LayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LayerViewHolder holder, int position) {
        LayerAdapter.LayerViewHolder holder1 = holder;
        holder1.bind(mLayersList.get(position));
    }

    @Override
    public int getItemCount() {
        return mLayersList.size();
    }

    public interface LayerAdapterListener {
        void onClick(String name);
    }

    public class LayerViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView tvLayer_info;
        private final MaterialCardView cardItem;

        public LayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLayer_info = itemView.findViewById(R.id.tvLayer_info);
            cardItem = itemView.findViewById(R.id.cardItem);
        }

        public void bind(String item) {
            tvLayer_info.setText(item);
            cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(item);
                }
            });
        }
    }
}
