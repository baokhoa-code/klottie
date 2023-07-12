package com.example.androidlottieapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlottieapp.KLottieDrawable;
import com.example.androidlottieapp.R;
import com.example.androidlottieapp.model.EditOption;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class EditOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditOptionAdapter";
    private final Context context;
    private ArrayList<EditOption> mList;
    private final KLottieDrawable drawable;

    public EditOptionAdapter(Context context, ArrayList<EditOption> mList, KLottieDrawable drawable) {
        this.context = context;
        this.mList = mList;
        this.drawable = drawable;
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }

    public void removeItem(EditOption item) {
        if (mList.contains(item)) {
            int position = mList.indexOf(item);
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size());
        }
    }

    public void addItemAtLast(EditOption item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
        notifyItemRangeInserted(mList.size() - 1, mList.size());
    }

    public void addItemAtSpecific(EditOption item, int position) {
        mList.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeInserted(position, mList.size());
    }

    public void setDataset(ArrayList<EditOption> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public boolean checkExist(EditOption item) {
        return mList.contains(item);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case EditOption.SPEED:
                view = LayoutInflater.from(context).inflate(R.layout.speed_item, parent, false);
                return new SpeedViewHolder(view, this);
            case EditOption.BACK_GROUND:
                view = LayoutInflater.from(context).inflate(R.layout.background_item, parent, false);
                return new BackgroundViewHolder(view, this);
            case EditOption.TRIM:
                view = LayoutInflater.from(context).inflate(R.layout.trim_item, parent, false);
                return new TrimViewHolder(view, this);
            case EditOption.OPTION:
                view = LayoutInflater.from(context).inflate(R.layout.option_item, parent, false);
                return new OptionViewHolder(view, this);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int typeView = this.getItemViewType(position);

        switch (typeView) {
            case EditOption.SPEED:
                SpeedViewHolder holder1 = (SpeedViewHolder) holder;
                holder1.bind(mList.get(position), position);
                break;
            case EditOption.BACK_GROUND:
                BackgroundViewHolder holder2 = (BackgroundViewHolder) holder;
                holder2.bind(mList.get(position), position);
                break;
            case EditOption.TRIM:
                TrimViewHolder holder3 = (TrimViewHolder) holder;
                holder3.bind(mList.get(position), position);
                break;
            case EditOption.OPTION:
                OptionViewHolder holder4 = (OptionViewHolder) holder;
                holder4.bind(mList.get(position), position);
                break;
            default:
        }

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : -1;
    }

    @Override
    public int getItemViewType(int position) {
        return mList != null ? mList.get(position).getType() : -1;
    }


    public class SpeedViewHolder extends RecyclerView.ViewHolder {

        EditOptionAdapter adapter;

        private final Button clear;
        private final Chip chip_speed1;
        private final Chip chip_speed2;
        private final Chip chip_speed3;
        private final Chip chip_speed4;
        private final Chip chip_speed5;

        public SpeedViewHolder(@NonNull View itemView, EditOptionAdapter adapter) {
            super(itemView);
            clear = itemView.findViewById(R.id.btn_speed_clear);
            chip_speed1 = itemView.findViewById(R.id.chip_speed1);
            chip_speed2 = itemView.findViewById(R.id.chip_speed2);
            chip_speed3 = itemView.findViewById(R.id.chip_speed3);
            chip_speed4 = itemView.findViewById(R.id.chip_speed4);
            chip_speed5 = itemView.findViewById(R.id.chip_speed5);
            this.adapter = adapter;
        }

        public void bind(EditOption item, int position) {
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawable.setSpeed(1f);
                    drawable.setReverseing(false);
                    item.listener.onClose(item);
                }
            });
            chip_speed1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onReverse(true);
                    drawable.setSpeed(1f);
                    drawable.setReverseing(true);
                }
            });
            chip_speed2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onReverse(true);
                    drawable.setSpeed(0.5f);
                    drawable.setReverseing(true);
                }
            });
            chip_speed3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawable.setSpeed(0.5f);
                    drawable.setReverseing(false);
                }
            });
            chip_speed4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawable.setSpeed(1f);
                    drawable.setReverseing(false);
                }
            });
            chip_speed5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawable.setSpeed(2f);
                    drawable.setReverseing(false);
                }
            });
        }
    }

    public class BackgroundViewHolder extends RecyclerView.ViewHolder {

        EditOptionAdapter adapter;

        Button btn_bg_1;
        Button btn_bg_2;
        Button btn_bg_3;
        Button btn_bg_4;
        Button btn_bg_5;
        Button btn_bg_6;

        private final Button clear;

        public BackgroundViewHolder(@NonNull View itemView, EditOptionAdapter adapter) {
            super(itemView);
            clear = itemView.findViewById(R.id.btn_bg_clear);
            btn_bg_1 = itemView.findViewById(R.id.btn_bg_1);
            btn_bg_2 = itemView.findViewById(R.id.btn_bg_2);
            btn_bg_3 = itemView.findViewById(R.id.btn_bg_3);
            btn_bg_4 = itemView.findViewById(R.id.btn_bg_4);
            btn_bg_5 = itemView.findViewById(R.id.btn_bg_5);
            btn_bg_6 = itemView.findViewById(R.id.btn_bg_6);
            this.adapter = adapter;
        }

        public void bind(EditOption item, int position) {
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg1));
                    item.listener.onClose(item);
                }
            });
            btn_bg_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg1));
                }
            });
            btn_bg_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg2));
                }
            });
            btn_bg_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg3));
                }
            });
            btn_bg_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg4));
                }
            });
            btn_bg_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg5));
                }
            });
            btn_bg_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onChangeColor(context.getColor(R.color.colorBg6));
                }
            });
        }
    }

    public class TrimViewHolder extends RecyclerView.ViewHolder {

        EditOptionAdapter adapter;
        Button btn_trim_start;
        Button btn_trim_end;

        private final Button clear;

        public TrimViewHolder(@NonNull View itemView, EditOptionAdapter adapter) {
            super(itemView);
            clear = itemView.findViewById(R.id.btn_trim_clear);
            btn_trim_start = itemView.findViewById(R.id.btn_trim_start);
            btn_trim_end = itemView.findViewById(R.id.btn_trim_end);
            this.adapter = adapter;
        }

        public void bind(EditOption item, int position) {

            btn_trim_start.setText("Start Frame: " + drawable.getStartFrame());
            btn_trim_end.setText("End Frame: " + drawable.getEndFrame());

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawable.setCustomStartFrame(-1);
                    drawable.setCustomEndFrame(-1);

                    item.listener.onClose(item);
                }
            });
            btn_trim_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Enter start frame")
                            .setView(R.layout.edit_text)
                            .setPositiveButton(
                                    "Load",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            TextView input = ((AlertDialog) dialog).findViewById(android.R.id.text1);
                                            drawable.setCustomStartFrame(Integer.parseInt(input.getText().toString()));
                                            btn_trim_start.setText("Start Frame: " + drawable.getStartFrame());
                                        }
                                    })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
            btn_trim_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Enter End frame")
                            .setView(R.layout.edit_text)
                            .setPositiveButton(
                                    "Load",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            TextView input = ((AlertDialog) dialog).findViewById(android.R.id.text1);
                                            drawable.setCustomEndFrame(Integer.parseInt(input.getText().toString()));
                                            btn_trim_end.setText("End Frame: " + drawable.getEndFrame());
                                        }
                                    })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder {

        EditOptionAdapter adapter;

        private final Button clear;
        private final TextView tv_option_layer_name;
        private final TextView tv_option_layer_property;
        private final TextView tv_option_layer_value;
        private final TextView tv_option_layer_color;

        public OptionViewHolder(@NonNull View itemView, EditOptionAdapter adapter) {
            super(itemView);
            clear = itemView.findViewById(R.id.btn_option_clear);
            tv_option_layer_name = itemView.findViewById(R.id.tv_option_layer_name);
            tv_option_layer_property = itemView.findViewById(R.id.tv_option_layer_property);
            tv_option_layer_value = itemView.findViewById(R.id.tv_option_layer_value);
            tv_option_layer_color = itemView.findViewById(R.id.tv_option_layer_color);
            this.adapter = adapter;
        }

        public void bind(EditOption item, int position) {
            tv_option_layer_name.setText(item.getChangedLayer());
            tv_option_layer_property.setText(item.getChangedProperty());
            String type = item.getChangedProperty();
            switch (type) {
                case "FillColor":
                    tv_option_layer_value.setVisibility(View.INVISIBLE);
                    tv_option_layer_color.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setText("");
                    tv_option_layer_color.setBackgroundColor(item.getNewValueInt());
                    break;
                case "FillOpacity":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText(item.getNewValueFloat1() + "");
                    break;
                case "StrokeColor":
                    tv_option_layer_value.setVisibility(View.INVISIBLE);
                    tv_option_layer_color.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setText("");
                    tv_option_layer_color.setBackgroundColor(item.getNewValueInt());
                    break;
                case "StrokeOpacity":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText(item.getNewValueFloat1() + "");
                    break;
                case "StrokeWidth":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText(item.getNewValueFloat1() + "");
                    break;
                case "TrOpacity":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText(item.getNewValueFloat1() + "");
                    break;
                case "TrPosition":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText("(x,y): " + item.getNewValueFloat1() + " , " + item.getNewValueFloat2());
                    break;
                case "TrRotation":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText(item.getNewValueFloat1() + "o");
                    break;
                case "TrScale":
                    tv_option_layer_value.setVisibility(View.VISIBLE);
                    tv_option_layer_color.setVisibility(View.INVISIBLE);
                    tv_option_layer_value.setText("(x,y): " + item.getNewValueFloat1() + " , " + item.getNewValueFloat2());
                    break;
            }
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.listener.onClose(item);
                }
            });
        }
    }
}
