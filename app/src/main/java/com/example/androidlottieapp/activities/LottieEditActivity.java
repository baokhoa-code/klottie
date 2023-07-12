package com.example.androidlottieapp.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlottieapp.KLottieDrawable;
import com.example.androidlottieapp.KLottieImageView;
import com.example.androidlottieapp.KLottieItem;
import com.example.androidlottieapp.KLottieLayer;
import com.example.androidlottieapp.KLottieProperty;
import com.example.androidlottieapp.R;
import com.example.androidlottieapp.adapter.EditOptionAdapter;
import com.example.androidlottieapp.adapter.LayerAdapter;
import com.example.androidlottieapp.model.EditOption;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.nvt.color.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LottieEditActivity extends AppCompatActivity {

    private static final String TAG = LottieEditActivity.class.getSimpleName();

    KLottieItem item = null;
    EditOption handleItem = new EditOption();
    KLottieDrawable drawable = null;
    Random rnd = new Random();
    Boolean isPlaying = true;
    BottomSheetDialog bottomSheetDialog;

    ArrayList<EditOption> list = new ArrayList<>();

    RecyclerView rclv_option;
    RecyclerView rclv_layer;
    EditOptionAdapter adapter;
    TextView tv_currentFrame;
    TextView tv_endFrame;
    KLottieImageView lottieView;
    Button btn_close;
    MaterialButton btn_show_full;
    MaterialButton btn_play_stop;
    MaterialButton btn_repeat;
    Chip chip_layer;
    Chip chip_speed;
    Chip chip_bg;
    Chip chip_trim;
    Slider sldProgress;

    List<String> mLayersList = new ArrayList<>();
    LayerAdapter mLayersAdaper;
    AlertDialog optionDialog;

    TextView tvInfo;
    TextView tvInt;
    TextView tvFloat1;
    TextView tvFloat2;

    Button btn_pick_color;

    AutoCompleteTextView tieProperty;

    LinearLayout llColor;
    LinearLayout llInt;
    LinearLayout llFloat1;
    LinearLayout llFloat2;

    TextInputEditText tieInt;
    TextInputEditText tieFloat1;
    TextInputEditText tieFloat2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_edit);

        initUI();

        getData();

        setData();

        initListeners();

    }

    @SuppressLint("WrongViewCast")
    private void initUI() {
        LayoutInflater inflater = this.getLayoutInflater();
        View optionView = inflater.inflate(R.layout.option_dialog, null);
        optionDialog = new MaterialAlertDialogBuilder(LottieEditActivity.this,
                R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons)
                .setTitle("Property change")
                .setView(optionView)
                .setPositiveButton("Load", (dialog, which) -> {
                    String type = handleItem.getChangedProperty();
                    switch (type) {
                        case "FillColor":
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.fillColor(handleItem.getNewValueInt()));
                            break;
                        case "FillOpacity":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.fillOpacity(handleItem.getNewValueFloat1()));
                            break;
                        case "StrokeColor":
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.strokeColor(handleItem.getNewValueInt()));
                            break;
                        case "StrokeOpacity":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.strokeOpacity(handleItem.getNewValueFloat1()));
                            break;
                        case "StrokeWidth":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.strokeWidth(handleItem.getNewValueFloat1()));
                            break;
                        case "TrOpacity":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.trOpacity(handleItem.getNewValueFloat1()));
                            break;
                        case "TrPosition":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            handleItem.setNewValueFloat2(Float.parseFloat(tieFloat2.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.trPosition(handleItem.getNewValueFloat1(), handleItem.getNewValueFloat2()));
                            break;
                        case "TrRotation":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.trRotation(handleItem.getNewValueFloat1()));
                            break;
                        case "TrScale":
                            handleItem.setNewValueFloat1(Float.parseFloat(tieFloat1.getText().toString()));
                            handleItem.setNewValueFloat2(Float.parseFloat(tieFloat2.getText().toString()));
                            lottieView.setLayerProperty(handleItem.getChangedLayer() + ".**", KLottieProperty.trScale(handleItem.getNewValueFloat1(), handleItem.getNewValueFloat2()));
                            break;
                    }
                    adapter.addItemAtLast(handleItem);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        btn_pick_color = optionView.findViewById(R.id.btn_pick_color);
        tvInfo = optionView.findViewById(R.id.tvInfo);
        tvInt = optionView.findViewById(R.id.tvInt);
        tvFloat1 = optionView.findViewById(R.id.tvFloat1);
        tvFloat2 = optionView.findViewById(R.id.tvFloat2);
        tieProperty = optionView.findViewById(R.id.tieProperty);
        tieInt = optionView.findViewById(R.id.tieInt);
        tieFloat1 = optionView.findViewById(R.id.tieFloat1);
        tieFloat2 = optionView.findViewById(R.id.tieFloat2);
        llColor = optionView.findViewById(R.id.llColor);
        llInt = optionView.findViewById(R.id.llInt);
        llFloat1 = optionView.findViewById(R.id.llFloat1);
        llFloat2 = optionView.findViewById(R.id.llFloat2);

        lottieView = findViewById(R.id.lottie_view);
        btn_close = findViewById(R.id.btn_close);
        btn_show_full = findViewById(R.id.btn_show_full);
        btn_play_stop = findViewById(R.id.btn_play_stop);
        btn_repeat = findViewById(R.id.btn_repeat);
        sldProgress = findViewById(R.id.sldProgress);
        tv_currentFrame = findViewById(R.id.tv_currentFrame);
        tv_endFrame = findViewById(R.id.tv_endFrame);
        rclv_option = findViewById(R.id.rclv_option);
        chip_layer = findViewById(R.id.chip_layer);
        chip_speed = findViewById(R.id.chip_speed);
        chip_bg = findViewById(R.id.chip_bg);
        chip_trim = findViewById(R.id.chip_trim);

        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bs, null);
        rclv_layer = bottomSheetView.findViewById(R.id.rclv_layer);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setDismissWithAnimation(true);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                chip_layer.setChecked(false);
            }
        });


    }

    private void getData() {
        item = (KLottieItem) getIntent().getSerializableExtra("item");
        drawable = KLottieDrawable.fromAssets(this, item.getPath(), item.getName())
                .setSize(512, 512)
                .setSpeed(1f)
                .setRepeat(true)
                .build(new KLottieDrawable.KLottieDrawableListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = () -> {
                            btn_play_stop.setIcon(getDrawable(R.drawable.ic_play));
                            isPlaying = drawable.isRepeating();
                        };
                        mainHandler.post(myRunnable);
                    }

                    @Override
                    public void onProgress(int frame) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = () -> {
                            if (frame >= 0)
                                sldProgress.setValue(frame);
                            tv_currentFrame.setText("" + (frame + 1));


                        };
                        mainHandler.post(myRunnable);

                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rclv_option.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rclv_option.addItemDecoration(dividerItemDecoration);

        adapter = new EditOptionAdapter(this, list, drawable);

        rclv_option.setAdapter(adapter);
    }

    private void setData() {
        if (item == null)
            Toast.makeText(this, "Selected item is getting error!", Toast.LENGTH_LONG).show();
        else {

            lottieView.setLottieDrawable(drawable);

            lottieView.startLottie();

            for (KLottieLayer layerInfo : lottieView.getLottieDrawable().getLayers()) {
                Log.i("Layers", layerInfo.toString());
                if (!layerInfo.getName().isEmpty())
                    mLayersList.add(layerInfo.getName());
            }
            mLayersAdaper = new LayerAdapter(mLayersList, this, new LayerAdapter.LayerAdapterListener() {
                @Override
                public void onClick(String name) {

                    handleItem = new EditOption(EditOption.OPTION, new EditOption.EditOptionListener() {
                        @Override
                        public void onClose(EditOption temp) {
                            adapter.removeItem(temp);
                        }

                        @Override
                        public void onChangeColor(int color) {

                        }

                        @Override
                        public void onReverse(boolean isReverse) {

                        }
                    });

                    handleItem.setChangedLayer(name);

                    handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[0]);

                    tvInfo.setText("Layer (Path): " + name);
                    tieProperty.setText(getResources().getStringArray(R.array.property_items)[0], false);
                    tieInt.setText("");
                    tieFloat1.setText("");
                    tieFloat2.setText("");
                    btn_pick_color.setBackgroundColor(Color.WHITE);
                    llColor.setVisibility(View.VISIBLE);
                    llInt.setVisibility(View.GONE);
                    llFloat1.setVisibility(View.GONE);
                    llFloat2.setVisibility(View.GONE);
                    optionDialog.show();
                }
            });
            rclv_layer.setLayoutManager(new LinearLayoutManager(this));
            rclv_layer.setAdapter(mLayersAdaper);
        }

        if (drawable != null) {
            sldProgress.setValueTo((float) drawable.getRealEnd());
            tv_endFrame.setText("/ " + drawable.getRealEnd());
        }
    }

    private void initListeners() {
        tieProperty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                llColor.setVisibility(View.GONE);
                llInt.setVisibility(View.GONE);
                llFloat1.setVisibility(View.GONE);
                llFloat2.setVisibility(View.GONE);

                switch (position) {
                    case 0:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[0]);
                        llColor.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[1]);
                        tvFloat1.setText("Opacity");
                        llFloat1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[2]);
                        llColor.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[3]);
                        tvFloat1.setText("Opacity");
                        llFloat1.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[4]);
                        tvFloat1.setText("Width");
                        llFloat1.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[5]);
                        tvFloat1.setText("Opacity");
                        llFloat1.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[6]);
                        tvFloat1.setText("X");
                        tvFloat2.setText("Y");
                        llFloat1.setVisibility(View.VISIBLE);
                        llFloat2.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[7]);
                        tvFloat1.setText("Rotation");
                        llFloat1.setVisibility(View.VISIBLE);
                        break;
                    case 8:
                        handleItem.setChangedProperty(getResources().getStringArray(R.array.property_items)[8]);
                        tvFloat1.setText("Width");
                        tvFloat2.setText("Height");
                        llFloat1.setVisibility(View.VISIBLE);
                        llFloat2.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;

                }
            }
        });
        btn_pick_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(LottieEditActivity.this, Color.BLACK, true, new ColorPickerDialog.OnColorPickerListener() {
                    @Override
                    public void onCancel(ColorPickerDialog dialog) {

                    }

                    @Override
                    public void onOk(ColorPickerDialog dialog, int color) {
                        btn_pick_color.setBackgroundColor(color);
                        handleItem.setNewValueInt(color);
                    }
                });
                colorPickerDialog.show();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_show_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.btn_list).getVisibility() == View.VISIBLE){
                    findViewById(R.id.btn_list).setVisibility(View.GONE);;
                    findViewById(R.id.constrainLayout_bars2).setVisibility(View.GONE);;
                    findViewById(R.id.divider2).setVisibility(View.GONE);;
                }else{
                    findViewById(R.id.btn_list).setVisibility(View.VISIBLE);;
                    findViewById(R.id.constrainLayout_bars2).setVisibility(View.VISIBLE);;
                    findViewById(R.id.divider2).setVisibility(View.VISIBLE);;
                }
            }
        });
        btn_play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawable != null) {
                    if (isPlaying) {
                        drawable.stop();
                        btn_play_stop.setIcon(getDrawable(R.drawable.ic_play));
                        isPlaying = false;
                    } else {
                        if (!drawable.isRepeating() && (drawable.getCurrentFrame() == drawable.getEndFrame() - 1 || drawable.getCurrentFrame() == drawable.getEndFrame() + 1))
                            drawable.rePlay();
                        drawable.start();
                        btn_play_stop.setIcon(getDrawable(R.drawable.ic_pause));
                        isPlaying = true;
                    }
                }
            }
        });
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawable != null) {
                    if (drawable.isRepeating()) {
                        drawable.setRepeat(false);
                        btn_repeat.setIconTint(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.black, null)));
                    } else {
                        drawable.setRepeat(true);
                        btn_repeat.setIconTint(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null)));
                    }
                }
            }
        });
        sldProgress.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                drawable.setUserDrag(true);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                drawable.setUserDrag(false);

                if (isPlaying)
                    drawable.start();
                if (!isPlaying)
                    drawable.stop();

            }
        });

        sldProgress.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    drawable.setCurrentFrame((int) value);
                    drawable.start();
                }
            }
        });

        chip_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();

            }
        });

        chip_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditOption option = new EditOption(EditOption.SPEED, new EditOption.EditOptionListener() {
                    @Override
                    public void onClose(EditOption temp) {
                        chip_speed.callOnClick();
                        chip_speed.setChecked(false);
                    }

                    @Override
                    public void onChangeColor(int color) {
                    }

                    @Override
                    public void onReverse(boolean isReverse) {
                        if (isReverse) {
                            sldProgress.setValue((float) drawable.getRealEnd());
                        }
                    }
                });
                if (!adapter.checkExist(option)) {
                    adapter.addItemAtLast(option);
                } else {
                    adapter.removeItem(option);
                }
            }
        });

        chip_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditOption option = new EditOption(EditOption.BACK_GROUND, new EditOption.EditOptionListener() {
                    @Override
                    public void onClose(EditOption temp) {
                        chip_bg.callOnClick();
                        chip_bg.setChecked(false);
                    }

                    @Override
                    public void onChangeColor(int color) {
                        lottieView.setBackgroundColor(color);
                    }

                    @Override
                    public void onReverse(boolean isReverse) {

                    }
                });
                if (!adapter.checkExist(option)) {
                    adapter.addItemAtLast(option);
                } else {
                    adapter.removeItem(option);
                }
            }
        });

        chip_trim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditOption option = new EditOption(EditOption.TRIM, new EditOption.EditOptionListener() {
                    @Override
                    public void onClose(EditOption temp) {
                        chip_trim.callOnClick();
                        chip_trim.setChecked(false);
                    }

                    @Override
                    public void onChangeColor(int color) {
                    }

                    @Override
                    public void onReverse(boolean isReverse) {

                    }
                });
                if (!adapter.checkExist(option)) {
                    adapter.addItemAtLast(option);
                } else {
                    adapter.removeItem(option);
                }
            }
        });
    }

}