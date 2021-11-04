package com.jtkurniawan.verticalscrollpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by <b>Dicky Kurniawan</b> at 09 September 2021
 */
public class VerticalScrollPicker extends ConstraintLayout {

    private static final String TAG = "VerticalScrollPicker";

    // View
    private RelativeLayout markerLayout;
    private ImageView markerTopImage;
    private ImageView markerBottomImage;
    private RecyclerView scrollList;

    // Data Compound
    private Context context;
    private ArrayList<Data> spacer = new ArrayList<>();
    private ArrayList<Data> listData = new ArrayList<>();
    private Data selectedData = null;
    private LinearLayoutManager layoutManager = null;
    private LinearSnapHelper snapHelper = null;
    private Adapter adapter = null;

    // Sample Text
    private String sampleTextIdle = "Idle";
    private String sampleTextFocus = "Focus";

    // Custom parameter
    private int customSpacer = 2;
    private int customMarkerVisibility = 1;
    private int customMarkerBackgroundColor = 0;
    private int customMarkerLineTopColor = 0;
    private int customMarkerLineBottomColor = 0;
    private int customTextIdleColor = 0;
    private int customTextIdleSize = 14;
    private int customTextIdleBackgroundColor = 0;
    private int customTextIdleBackgroundResource = 0;
    private int customTextIdleBackgroundResourceTint = 0;
    private int customTextFocusColor = 0;
    private int customTextFocusSize = 17;
    private int customTextFocusBackgroundColor = 0;
    private int customTextFocusBackgroundResource = 0;
    private int customTextFocusBackgroundResourceTint = 0;

    public VerticalScrollPicker(@NonNull Context context) {
        super(context);
    }

    public VerticalScrollPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VerticalScrollPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.VerticalScrollPicker,0,0);

        try {
            // Spacer
            customSpacer = a.getInteger(R.styleable.VerticalScrollPicker_setSpacer,2);
            if (customSpacer<0) customSpacer=0;

            // Marker
            customMarkerVisibility = a.getInteger(R.styleable.VerticalScrollPicker_markerVisibility,1);
            customMarkerBackgroundColor = a.getColor(R.styleable.VerticalScrollPicker_markerBackgroundColor,Color.GRAY);
            customMarkerLineTopColor = a.getColor(R.styleable.VerticalScrollPicker_markerLineTopColor,Color.BLACK);
            customMarkerLineBottomColor = a.getColor(R.styleable.VerticalScrollPicker_markerLineBottomColor,Color.BLACK);

            // Text Idle
            sampleTextIdle = a.getString(R.styleable.VerticalScrollPicker_textIdleSample);
            sampleTextIdle = sampleTextIdle==null?"Idle":sampleTextIdle;
            customTextIdleSize = a.getInteger(R.styleable.VerticalScrollPicker_textIdleSize,14);
            customTextIdleColor = a.getColor(R.styleable.VerticalScrollPicker_textIdleColor,Color.BLACK);
            customTextIdleBackgroundColor = a.getColor(R.styleable.VerticalScrollPicker_textIdleBackgroundColor,Color.TRANSPARENT);
            customTextIdleBackgroundResource = a.getResourceId(R.styleable.VerticalScrollPicker_textIdleBackgroundResource,0);
            customTextIdleBackgroundResourceTint = a.getColor(R.styleable.VerticalScrollPicker_textIdleBackgroundResourceTint,Color.TRANSPARENT);

            // Text Focus
            sampleTextFocus = a.getString(R.styleable.VerticalScrollPicker_textFocusSample);
            sampleTextFocus = sampleTextFocus==null?"Focus":sampleTextFocus;
            customTextFocusSize = a.getInteger(R.styleable.VerticalScrollPicker_textFocusSize,17);
            customTextFocusColor = a.getColor(R.styleable.VerticalScrollPicker_textFocusColor,Color.BLACK);
            customTextFocusBackgroundColor = a.getColor(R.styleable.VerticalScrollPicker_textFocusBackgroundColor,Color.TRANSPARENT);
            customTextFocusBackgroundResource = a.getResourceId(R.styleable.VerticalScrollPicker_textFocusBackgroundResource,0);
            customTextFocusBackgroundResourceTint = a.getColor(R.styleable.VerticalScrollPicker_textFocusBackgroundResourceTint,Color.TRANSPARENT);

        } finally {
            a.recycle();
        }
        setMinWidth(50);

        GenerateView();

        if (isInEditMode()){
            GeneratePreviewView();
        }
    }

    private void GenerateView(){
        //=== MARKER
        // PARENT
        markerLayout = new RelativeLayout(context);
        LayoutParams markerLayoutParams = new LayoutParams(LayoutParams.MATCH_CONSTRAINT,DPtoPX(context,50));
        markerLayoutParams.startToStart = LayoutParams.PARENT_ID;
        markerLayoutParams.endToEnd = LayoutParams.PARENT_ID;
        markerLayoutParams.topToTop = LayoutParams.PARENT_ID;
        markerLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        markerLayout.setBackgroundColor(customMarkerBackgroundColor);
        markerLayout.setVisibility(customMarkerVisibility==1?VISIBLE:GONE);
        markerLayout.setLayoutParams(markerLayoutParams);

        // MARKER TOP
        markerTopImage = new ImageView(context);
        RelativeLayout.LayoutParams markerTopImageParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,DPtoPX(context,2));
        markerTopImage.setLayoutParams(markerTopImageParam);
        markerTopImage.setBackgroundColor(customMarkerLineTopColor);
        markerLayout.addView(markerTopImage);

        // MARKER BOTTOM
        markerBottomImage = new ImageView(context);
        RelativeLayout.LayoutParams markerBottomImageParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,DPtoPX(context,2));
        markerBottomImageParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        markerBottomImage.setLayoutParams(markerBottomImageParam);
        markerBottomImage.setBackgroundColor(customMarkerLineBottomColor);
        markerLayout.addView(markerBottomImage);

        //=== LIST
        scrollList = new RecyclerView(context);
        LayoutParams scrollListParams = new LayoutParams(LayoutParams.WRAP_CONTENT,DPtoPX(context,250));
        scrollListParams.startToStart = LayoutParams.PARENT_ID;
        scrollListParams.endToEnd = LayoutParams.PARENT_ID;
        scrollListParams.topToTop = LayoutParams.PARENT_ID;
        scrollListParams.bottomToBottom = LayoutParams.PARENT_ID;
        scrollList.setLayoutParams(scrollListParams);

        //=== ADD CHILD
        addView(markerLayout);
        addView(scrollList);

        adapter = new Adapter(context,new ArrayList<Data>());

        // Set layout manager
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(scrollList);
        scrollList.setLayoutManager(layoutManager);

        // set default spacer
        setSpacer(customSpacer);
    }

    //==========================================================================================
    //                                       PREVIEW ONLY
    //==========================================================================================
    private void GeneratePreviewView(){
        int height = 50 + (50*(customSpacer*2));
        LinearLayout showParent = new LinearLayout(context);
        LayoutParams showParentParams = new LayoutParams(LayoutParams.WRAP_CONTENT,DPtoPX(context,height));
        showParentParams.startToStart = LayoutParams.PARENT_ID;
        showParentParams.endToEnd = LayoutParams.PARENT_ID;
        showParentParams.topToTop = LayoutParams.PARENT_ID;
        showParentParams.bottomToBottom = LayoutParams.PARENT_ID;
        showParent.setLayoutParams(showParentParams);
        showParent.setOrientation(LinearLayout.VERTICAL);
        showParent.setGravity(Gravity.CENTER_HORIZONTAL);

        for (int i=0;i<customSpacer;i++){
            ConstraintLayout layout = new ConstraintLayout(context);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,DPtoPX(context,50));
            layout.setLayoutParams(layoutParams);

            // Text View
            TextView idle = new TextView(context);
            LinearLayout.LayoutParams idleParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,DPtoPX(context,50));
            idle.setLayoutParams(idleParam);
            idle.setText(sampleTextIdle);
            idle.setTextColor(customTextIdleColor);
            idle.setPadding(DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10));
            idle.setTextSize(TypedValue.COMPLEX_UNIT_SP,customTextIdleSize);
            idle.setGravity(Gravity.CENTER);
            idle.setId(generateViewId());

            Rect idleBounds = new Rect();
            Paint idleTextPaint = idle.getPaint();
            idleTextPaint.getTextBounds(sampleTextIdle,0,sampleTextIdle.length(),idleBounds);
            int idleWidth = idleBounds.width()+DPtoPX(context,40);

            LayoutParams textParams = new LayoutParams(idleWidth, LayoutParams.MATCH_PARENT);
            textParams.startToStart = LayoutParams.PARENT_ID;
            textParams.endToEnd = LayoutParams.PARENT_ID;
            idle.setLayoutParams(textParams);

            // Background
            ImageView background = new ImageView(context);
            LayoutParams backgroundParams = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_PARENT);
            backgroundParams.startToStart = idle.getId();
            backgroundParams.endToEnd = idle.getId();
            background.setLayoutParams(backgroundParams);
            if (customTextIdleBackgroundResource!=0){
                background.setImageResource(customTextIdleBackgroundResource);
                background.setColorFilter(customTextIdleBackgroundResourceTint);
            }

            // Attach to parent
            layout.addView(background);
            layout.addView(idle);

            showParent.addView(layout);
        }

        ConstraintLayout focusLayout = new ConstraintLayout(context);
        LayoutParams focusLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,DPtoPX(context,50));
        focusLayout.setLayoutParams(focusLayoutParams);

        // Text View
        TextView focus = new TextView(context);
        LinearLayout.LayoutParams focusParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,DPtoPX(context,50));
        focus.setLayoutParams(focusParam);
        focus.setText(sampleTextFocus);
        focus.setTextColor(customTextFocusColor);
        focus.setPadding(DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10));
        focus.setTextSize(TypedValue.COMPLEX_UNIT_SP,customTextFocusSize);
        focus.setGravity(Gravity.CENTER);
        focus.setTypeface(Typeface.DEFAULT_BOLD);
        focus.setId(generateViewId());

        Rect focusBounds = new Rect();
        Paint focusTextPaint = focus.getPaint();
        focusTextPaint.getTextBounds(sampleTextFocus,0,sampleTextFocus.length(),focusBounds);
        int focusWidth = focusBounds.width()+DPtoPX(context,40);

        LayoutParams focusTextParams = new LayoutParams(focusWidth, LayoutParams.MATCH_PARENT);
        focusTextParams.startToStart = LayoutParams.PARENT_ID;
        focusTextParams.endToEnd = LayoutParams.PARENT_ID;
        focus.setLayoutParams(focusTextParams);

        // Background
        ImageView focusBackground = new ImageView(context);
        LayoutParams focusBackgroundParams = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_PARENT);
        focusBackgroundParams.startToStart = focus.getId();
        focusBackgroundParams.endToEnd = focus.getId();
        focusBackground.setLayoutParams(focusBackgroundParams);
        if (customTextFocusBackgroundResource!=0){
            focusBackground.setImageResource(customTextFocusBackgroundResource);
            focusBackground.setColorFilter(customTextFocusBackgroundResourceTint);
        }

        // Attach to parent
        focusLayout.addView(focusBackground);
        focusLayout.addView(focus);

        showParent.addView(focusLayout);

        for (int i=0;i<customSpacer;i++){
            ConstraintLayout layout = new ConstraintLayout(context);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,DPtoPX(context,50));
            layout.setLayoutParams(layoutParams);

            // Text View
            TextView idle = new TextView(context);
            LinearLayout.LayoutParams idleParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,DPtoPX(context,50));
            idle.setLayoutParams(idleParam);
            idle.setText(sampleTextIdle);
            idle.setTextColor(customTextIdleColor);
            idle.setPadding(DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10));
            idle.setTextSize(TypedValue.COMPLEX_UNIT_SP,customTextIdleSize);
            idle.setGravity(Gravity.CENTER);
            idle.setId(generateViewId());

            Rect idleBounds = new Rect();
            Paint idleTextPaint = idle.getPaint();
            idleTextPaint.getTextBounds(sampleTextIdle,0,sampleTextIdle.length(),idleBounds);
            int idleWidth = idleBounds.width()+DPtoPX(context,40);

            LayoutParams textParams = new LayoutParams(idleWidth, LayoutParams.MATCH_PARENT);
            textParams.startToStart = LayoutParams.PARENT_ID;
            textParams.endToEnd = LayoutParams.PARENT_ID;
            idle.setLayoutParams(textParams);

            // Background
            ImageView background = new ImageView(context);
            LayoutParams backgroundParams = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_PARENT);
            backgroundParams.startToStart = idle.getId();
            backgroundParams.endToEnd = idle.getId();
            background.setLayoutParams(backgroundParams);
            if (customTextIdleBackgroundResource!=0){
                background.setImageResource(customTextIdleBackgroundResource);
                background.setColorFilter(customTextIdleBackgroundResourceTint);
            }

            // Attach to parent
            layout.addView(background);
            layout.addView(idle);

            showParent.addView(layout);
        }

        addView(showParent);
    }

    //============================================ INIT ============================================
    /**
     * Set a new adapter to provide child views on demand.
     * When adapter is changed, all existing views are recycled back to the pool. If the pool has only one adapter, it will be cleared.
     * @param data VerticalScrollPicker Data
     * @param listener OnSelected Listener
     */
    public void setData(@NonNull ArrayList<Data> data, @NonNull final OnSelectedListener listener){
        // Set list data
        listData = new ArrayList<>();
        listData.addAll(spacer);
        listData.addAll(data);
        listData.addAll(spacer);

        // Check if selected item is set
        int selectedPos = 0;
        boolean isSelectedExist = false;
        for (int i=0;i<listData.size();i++){
            listData.get(i).setId(i);
            if (listData.get(i).isSelected()){
                selectedData = listData.get(i);
                selectedPos = i;
                isSelectedExist = true;
            }
        }
        if (!isSelectedExist){
            listData.get(spacer.size()).setSelected(true);
            selectedData = listData.get(spacer.size());
        }
        listener.OnSlotSelected(selectedData);

        // Set adapter
        adapter = new Adapter(context,listData);
        adapter.setCustomsIdle(customTextIdleSize,customTextIdleColor,customTextIdleBackgroundColor,customTextIdleBackgroundResource,customTextIdleBackgroundResourceTint);
        adapter.setCustomFocus(customTextFocusSize,customTextFocusColor,customTextFocusBackgroundColor,customTextFocusBackgroundResource,customTextFocusBackgroundResourceTint);
        scrollList.setAdapter(adapter);

        // Scroll to selected item is exist
        if (selectedPos!=0)
            layoutManager.scrollToPositionWithOffset(selectedPos,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics())*spacer.size());

        // Add scroll listener to get focused data
        scrollList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollList.post(new Runnable() {
                    @Override
                    public void run() {
                        View view = snapHelper.findSnapView(layoutManager);
                        if (view!=null){
                            int pos = scrollList.getChildAdapterPosition(view);
                            if (pos>=0 && pos<listData.size()){
                                Data selected = listData.get(pos);
                                if (selectedData.getId() != selected.getId()){
                                    for (Data get : listData){
                                        get.setSelected(false);
                                    }
                                    selectedData = selected;
                                    listData.get(pos).setSelected(true);
                                    adapter.notifyDataSetChanged();

                                    listener.OnSlotSelected(selectedData);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    //========================================== CUSTOMS ===========================================
    /**
     * Change height spinner by amount item top and bottom
     * @param amountSpacer
     */
    public void setSpacer(int amountSpacer){
        spacer = new ArrayList<>();
        for (int i=0;i<amountSpacer;i++){
            Data set = new Data(0,"","");
            set.setPadding(true);
            spacer.add(set);
        }

        int height = (100*amountSpacer) + 50;
        Log.e(TAG, "SetSpacer: "+height);

        ViewGroup.LayoutParams params_slot_1 = scrollList.getLayoutParams();
        params_slot_1.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,height,context.getResources().getDisplayMetrics());
        scrollList.setLayoutParams(params_slot_1);
    }

    /**
     * Scroll to the specified adapter position
     * @param index position
     */
    public void setSelection(int index){
        if (layoutManager==null)
            return;

        for (int i=0;i<listData.size();i++){
            listData.get(i).setSelected(false);
        }

        int pos = index+2;

        listData.get(pos).setSelected(true);
        adapter.notifyDataSetChanged();

        layoutManager.scrollToPositionWithOffset(pos,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics())*spacer.size());
    }

    /**
     * Show/Hide line marker
     * @param visibility
     */
    public void setMarkerVisibility(int visibility){
        markerLayout.setVisibility(VISIBLE);
    }

    /**
     * Change marker background color
     * @param color
     */
    public void setMarkerBackgroundColor(int color){
        markerLayout.setBackgroundColor(color);
    }

    /**
     * Change Top Line marker color
     * @param color
     */
    public void setMarkerLineTopColor(int color){
        markerTopImage.setBackgroundColor(color);
    }

    /**
     * Change Bottom line marker color
     * @param color
     */
    public void setMarkerLineBottomColor(int color){
        markerBottomImage.setBackgroundColor(color);
    }

    /**
     * Change text color when not focused
     * @param resourceColor
     */
    public void setTextIdleColor(int resourceColor){
        customTextIdleColor = resourceColor;
        adapter.setCustomsIdle(customTextIdleSize,customTextIdleColor,customTextIdleBackgroundColor,customTextIdleBackgroundResource,customTextIdleBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change size text when not focused
     * @param size
     */
    public void setTextIdleSize(int size){
        customTextIdleSize = size;
        adapter.setCustomsIdle(customTextIdleSize,customTextIdleColor,customTextIdleBackgroundColor,customTextIdleBackgroundResource,customTextIdleBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change background text color when not focused
     * @param resourceColor
     */
    public void setTextIdleBackgroundColor(int resourceColor){
        customTextIdleBackgroundColor = resourceColor;
        adapter.setCustomsIdle(customTextIdleSize,customTextIdleColor,customTextIdleBackgroundColor,customTextIdleBackgroundResource,customTextIdleBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change background text with image drawable resource when no focused
     * @param drawable
     */
    public void setTextIdleBackgroundResource(int drawable){
        customTextIdleBackgroundResource = drawable;
        adapter.setCustomsIdle(customTextIdleSize,customTextIdleColor,customTextIdleBackgroundColor,customTextIdleBackgroundResource,customTextIdleBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change color filter background when background image is applied.
     * @param color
     */
    public void setTextIdleBackgroundResourceTint(int color){
        customTextIdleBackgroundResourceTint = color;
        adapter.setCustomsIdle(customTextIdleSize,customTextIdleColor,customTextIdleBackgroundColor,customTextIdleBackgroundResource,customTextIdleBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change text color when focused
     * @param resourceColor
     */
    public void setTextFocusColor(int resourceColor){
        customTextFocusColor = resourceColor;
        adapter.setCustomFocus(customTextFocusSize,customTextFocusColor,customTextFocusBackgroundColor,customTextFocusBackgroundResource,customTextFocusBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change size text when focused
     * @param size
     */
    public void setTextFocusSize(int size){
        customTextFocusSize = size;
        adapter.setCustomFocus(customTextFocusSize,customTextFocusColor,customTextFocusBackgroundColor,customTextFocusBackgroundResource,customTextFocusBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change background text color when focused
     * @param resourceColor
     */
    public void setTextFocusBackgroundColor(int resourceColor){
        customTextFocusBackgroundColor = resourceColor;
        adapter.setCustomFocus(customTextFocusSize,customTextFocusColor,customTextFocusBackgroundColor,customTextFocusBackgroundResource,customTextFocusBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change background text with image drawable resource when no focused
     * @param drawable
     */
    public void setTextFocusBackgroundResource(int drawable){
        customTextFocusBackgroundResource = drawable;
        adapter.setCustomFocus(customTextFocusSize,customTextFocusColor,customTextFocusBackgroundColor,customTextFocusBackgroundResource,customTextFocusBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    /**
     * Change color filter background when background image is applied.
     * @param color
     */
    public void setTextFocusBackgroundResourceTint(int color){
        customTextFocusBackgroundResourceTint = color;
        adapter.setCustomFocus(customTextFocusSize,customTextFocusColor,customTextFocusBackgroundColor,customTextFocusBackgroundResource,customTextFocusBackgroundResourceTint);
        adapter.notifyDataSetChanged();
    }

    //======================================== ADAPTER =============================================
    private static class Adapter extends RecyclerView.Adapter<ViewHolder>{

        private Context context;
        private ArrayList<Data> data;
        private String longestWord = "";
        private int textIdleSize = 14;
        private int textIdleColor;
        private int textIdleBackgroundColor;
        private int textIdleBackgroundResource = 0;
        private int textIdleBackgroundResourceTint = 0;
        private int textFocusSize = 17;
        private int textFocusColor;
        private int textFocusBackgroundColor;
        private int textFocusBackgroundResource = 0;
        private int textFocusBackgroundResourceTint = 0;

        public Adapter(Context context, ArrayList<Data> data) {
            this.context = context;
            this.data = data;

            for (int i=0;i<data.size();i++){
                if (data.get(i).getShowData().length() > longestWord.length()){
                    longestWord = data.get(i).getShowData();
                }
            }

            textIdleColor = Color.BLACK;
            textIdleBackgroundColor = Color.TRANSPARENT;
            textFocusColor = Color.BLACK;
            textFocusBackgroundColor = Color.TRANSPARENT;
        }

        public void setCustomsIdle(int textIdleSize, int textIdleColor, int textIdleBackgroundColor, int textIdleBackgroundResource, int textIdleBackgroundResourceTint){
            this.textIdleSize = textIdleSize;
            this.textIdleColor = textIdleColor==0? Color.BLACK:textIdleColor;
            this.textIdleBackgroundColor = textIdleBackgroundColor==0? Color.TRANSPARENT:textIdleBackgroundColor;
            this.textIdleBackgroundResource = textIdleBackgroundResource;
            this.textIdleBackgroundResourceTint = textIdleBackgroundResourceTint;
        }

        public void setCustomFocus(int textFocusSize, int textFocusColor, int textFocusBackgroundColor, int textFocusBackgroundResource, int textFocusBackgroundResourceTint){
            this.textFocusSize = textFocusSize;
            this.textFocusColor = textFocusColor==0? Color.BLACK:textFocusColor;
            this.textFocusBackgroundColor = textFocusBackgroundColor==0? Color.TRANSPARENT:textFocusBackgroundColor;
            this.textFocusBackgroundResource = textFocusBackgroundResource;
            this.textFocusBackgroundResourceTint = textFocusBackgroundResourceTint;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout layout = new ConstraintLayout(context);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,DPtoPX(context,50));
            layout.setLayoutParams(layoutParams);

            // Text View
            TextView text = new TextView(context);
            text.setText(longestWord);
            text.setTextColor(Color.parseColor("#000000"));
            text.setGravity(Gravity.CENTER);
            text.setPadding(DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10),DPtoPX(context,10));
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            text.setMaxLines(1);
            text.setId(generateViewId());

            Rect bounds = new Rect();
            Paint textPaint = text.getPaint();
            textPaint.getTextBounds(longestWord,0,longestWord.length(),bounds);
            int width = bounds.width()+DPtoPX(context,40);

            LayoutParams textParams = new LayoutParams(width, LayoutParams.MATCH_PARENT);
            textParams.startToStart = LayoutParams.PARENT_ID;
            textParams.endToEnd = LayoutParams.PARENT_ID;
            text.setLayoutParams(textParams);

            // Background
            ImageView background = new ImageView(context);
            LayoutParams backgroundParams = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_PARENT);
            backgroundParams.startToStart = text.getId();
            backgroundParams.endToEnd = text.getId();
            background.setLayoutParams(backgroundParams);

            // Attach to parent
            layout.addView(background);
            layout.addView(text);

            return new ViewHolder(layout,background,text);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Data get = data.get(position);

            if (get.isPadding()){
                holder.background.setImageBitmap(null);
                holder.text.setBackgroundColor(Color.TRANSPARENT);
            } else {
                if (get.isSelected()){
                    if (textFocusBackgroundResource!=0) {
                        holder.background.setImageResource(textFocusBackgroundResource);
                        holder.text.setBackgroundColor(Color.TRANSPARENT);
                        if (textFocusBackgroundResourceTint!=0)
                            holder.background.setColorFilter(textFocusBackgroundResourceTint);
                        else
                            holder.background.setColorFilter(Color.TRANSPARENT);
                    } else if (textFocusBackgroundColor!=0){
                        holder.background.setImageBitmap(null);
                        holder.text.setBackgroundColor(textFocusBackgroundColor);
                    } else {
                        holder.background.setImageBitmap(null);
                        holder.text.setBackgroundColor(Color.TRANSPARENT);
                    }

                    holder.text.setTextColor(textFocusColor);
                    holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP,textFocusSize);
                    holder.text.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    if (textIdleBackgroundResource!=0) {
                        holder.background.setImageResource(textIdleBackgroundResource);
                        holder.text.setBackgroundColor(Color.TRANSPARENT);
                        if (textIdleBackgroundResourceTint!=0)
                            holder.background.setColorFilter(textIdleBackgroundResourceTint);
                        else
                            holder.background.setColorFilter(Color.TRANSPARENT);
                    } else if (textIdleBackgroundColor!=0){
                        holder.background.setImageBitmap(null);
                        holder.text.setBackgroundColor(textIdleBackgroundColor);
                    } else {
                        holder.background.setImageBitmap(null);
                        holder.text.setBackgroundColor(Color.TRANSPARENT);
                    }

                    holder.text.setTextColor(textIdleColor);
                    holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP,textIdleSize);
                    holder.text.setTypeface(Typeface.DEFAULT);
                }
            }
            holder.text.setText(get.getShowData());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView background;
        TextView text;

        public ViewHolder(@NonNull View itemView, ImageView background, TextView text) {
            super(itemView);
            this.background = background;
            this.text = text;
        }
    }

    //======================================= MODEL DATA ===========================================
    public static class Data implements Parcelable {

        private int id = -1;
        private String showData = "";
        private String realData = "";
        private boolean selected = false;
        private boolean padding = false;

        public Data() {}

        public Data(int id, String showData, String realData) {
            this.id = id;
            this.showData = showData;
            this.realData = realData;
        }

        protected Data(Parcel in){
            id = in.readInt();
            showData = in.readString();
            realData = in.readString();
        }

        public final static Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel source) {
                return new Data(source);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(showData);
            dest.writeString(realData);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getShowData() {
            return showData;
        }

        public void setShowData(String showData) {
            this.showData = showData;
        }

        public String getRealData() {
            return realData;
        }

        public void setRealData(String realData) {
            this.realData = realData;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isPadding() {
            return padding;
        }

        public void setPadding(boolean padding) {
            this.padding = padding;
        }
    }

    //======================================== LISTENER ============================================
    public interface OnSelectedListener{
        void OnSlotSelected(Data data);
    }

    public interface OnCompileResultDate{
        void OnResultDate(String date, Data day, Data month, Data year);
    }

    public interface OnCompileResultTime{
        void OnResultTime(String time, Data hour, Data minute, @Nullable Data second);
    }

    //========================================== TOOLS =============================================
    private static int DPtoPX(Context context, int amount){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,amount,context.getResources().getDisplayMetrics());
    }

    /**
     * Combine 3 VerticalScrollPicker to create DatePicker
     * Just create 3 view and inserted here.
     * @param selectCurrentDate if true, current date will automatically focused
     * @param selectedDate if not null, selected date will automatically focused
     * @param patternMonth if null, default pattern is MMM
     * @param maxLimitYear insert oldest year, e.g 1990,1980... if range with current year is > 100, then max limit will set to current year - 100.
     * @param day View VerticalScrollPicker
     * @param month View VerticalScrollPicker
     * @param year View VerticalScrollPicker
     * @param listener Result Listener
     */
    public static void BuildDatePicker(
            boolean selectCurrentDate,
            @Nullable String selectedDate,
            @Nullable String patternMonth,
            int maxLimitYear,
            @NonNull VerticalScrollPicker day,
            @NonNull VerticalScrollPicker month,
            @NonNull VerticalScrollPicker year,
            @NonNull final OnCompileResultDate listener){

        final Data[] selectedDay = new Data[1];
        final Data[] selectedMonth = new Data[1];
        final Data[] selectedYear = new Data[1];

        day.setData(TEMPLATE_DATE_DAY(selectCurrentDate, selectedDate), new OnSelectedListener() {
            @Override
            public void OnSlotSelected(Data data) {
                selectedDay[0] = data;
                listener.OnResultDate(CompileDateData(selectedDay[0],selectedMonth[0],selectedYear[0]),selectedDay[0],selectedMonth[0],selectedYear[0]);
            }
        });
        month.setData(TEMPLATE_DATE_MONTH(selectCurrentDate, selectedDate, patternMonth), new OnSelectedListener() {
            @Override
            public void OnSlotSelected(Data data) {
                selectedMonth[0] = data;
                listener.OnResultDate(CompileDateData(selectedDay[0],selectedMonth[0],selectedYear[0]),selectedDay[0],selectedMonth[0],selectedYear[0]);
            }
        });
        year.setData(TEMPLATE_DATE_YEAR(selectedDate, maxLimitYear), new OnSelectedListener() {
            @Override
            public void OnSlotSelected(Data data) {
                selectedYear[0] = data;
                listener.OnResultDate(CompileDateData(selectedDay[0],selectedMonth[0],selectedYear[0]),selectedDay[0],selectedMonth[0],selectedYear[0]);
            }
        });
    }

    /**
     * Combine 2-3 VerticalScrollPicker to create TimePicker
     * Just create 3 view or 2 view (if not using second) and insert here.
     * @param selectCurrentTime if true, current time will automatically focused
     * @param selectedTime if not null, selected time will automatically focused
     * @param hour View VerticalScrollPicker
     * @param minute View VerticalScrollPicker
     * @param second if not using Second, then set this parameter to null
     * @param listener Result time listener
     */
    public static void BuildTimePicker(
            boolean selectCurrentTime,
            @Nullable String selectedTime,
            @NonNull VerticalScrollPicker hour,
            @NonNull VerticalScrollPicker minute,
            @Nullable VerticalScrollPicker second,
            @NonNull final OnCompileResultTime listener){

        final Data[] selectedHour = new Data[1];
        final Data[] selectedMinute = new Data[1];
        final Data[] selectedSecond = new Data[1];

        final boolean isUseSecond = second!=null;

        hour.setData(TEMPLATE_TIME_HOUR(selectCurrentTime, selectedTime, isUseSecond), new OnSelectedListener() {
            @Override
            public void OnSlotSelected(Data data) {
                selectedHour[0] = data;
                if (isUseSecond){
                    listener.OnResultTime(CompileTimeData(selectedHour[0],selectedMinute[0],selectedSecond[0]),selectedHour[0],selectedMinute[0],selectedSecond[0]);
                } else {
                    listener.OnResultTime(CompileTimeData(selectedHour[0],selectedMinute[0],null),selectedHour[0],selectedMinute[0],null);
                }
            }
        });
        minute.setData(TEMPLATE_TIME_MINUTE(selectCurrentTime, selectedTime, isUseSecond), new OnSelectedListener() {
            @Override
            public void OnSlotSelected(Data data) {
                selectedMinute[0] = data;
                if (isUseSecond){
                    listener.OnResultTime(CompileTimeData(selectedHour[0],selectedMinute[0],selectedSecond[0]),selectedHour[0],selectedMinute[0],selectedSecond[0]);
                } else {
                    listener.OnResultTime(CompileTimeData(selectedHour[0],selectedMinute[0],null),selectedHour[0],selectedMinute[0],null);
                }
            }
        });
        if (isUseSecond){
            second.setData(TEMPLATE_TIME_SECOND(), new OnSelectedListener() {
                @Override
                public void OnSlotSelected(Data data) {
                    selectedSecond[0] = data;
                    listener.OnResultTime(CompileTimeData(selectedHour[0],selectedMinute[0],selectedSecond[0]),selectedHour[0],selectedMinute[0],selectedSecond[0]);
                }
            });
        }
    }

    //========================================== TEMPLATE ==========================================
    public static ArrayList<Data> TEMPLATE_DATE_DAY(boolean selectCurrentDate, @Nullable String selectedDate){
        Calendar cal = Calendar.getInstance();
        if (selectedDate!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date setDate = sdf.parse(selectedDate);
                if (setDate!=null)
                    cal.setTime(setDate);
            } catch (ParseException e) {
                Log.e(TAG, "SetAsDatePicker: "+e.getMessage(), e);
            }
        }

        int current_day = selectCurrentDate?cal.get(Calendar.DAY_OF_MONTH):1;

        // Day
        ArrayList<Data> day = new ArrayList<>();
        for (int i=1;i<32;i++){
            String value = "";
            if (i<10){
                value = "0" + String.valueOf(i);
            } else {
                value = String.valueOf(i);
            }

            Data set = new Data(i,value,value);
            if (i==current_day) {
                set.setSelected(true);
            }
            day.add(set);
        }

        return day;
    }

    public static ArrayList<Data> TEMPLATE_DATE_MONTH(boolean selectCurrentDate, @Nullable String selectedDate, @Nullable String patternMonth){
        Calendar cal = Calendar.getInstance();
        if (selectedDate!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date setDate = sdf.parse(selectedDate);
                if (setDate!=null)
                    cal.setTime(setDate);
            } catch (ParseException e) {
                Log.e(TAG, "SetAsDatePicker: "+e.getMessage(), e);
            }
        }

        int current_month = selectCurrentDate?cal.get(Calendar.MONTH):0;

        // Month
        if (patternMonth==null) patternMonth = "MMM";
        if (patternMonth.isEmpty()) patternMonth = "MMM";
        ArrayList<Data> month = new ArrayList<>();
        for (int i=0;i<12;i++){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(patternMonth, Locale.ENGLISH);
            c.set(Calendar.MONTH,i);
            String name = format.format(c.getTime());

            Data set = new Data(i,name, String.valueOf(i+1));
            if (i==current_month) {
                set.setSelected(true);
            }
            month.add(set);
        }

        return month;
    }

    public static ArrayList<Data> TEMPLATE_DATE_YEAR(@Nullable String selectedDate, int maxLimitYear){
        Calendar cal = Calendar.getInstance();
        if (selectedDate!=null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date setDate = sdf.parse(selectedDate);
                if (setDate!=null)
                    cal.setTime(setDate);
            } catch (ParseException e) {
                Log.e(TAG, "SetAsDatePicker: "+e.getMessage(), e);
            }
        }

        int current_year = cal.get(Calendar.YEAR);

        if (current_year - maxLimitYear > 100)
            maxLimitYear = current_year - 100;

        // Year
        ArrayList<Data> year = new ArrayList<>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i>maxLimitYear; i--){
            Data set = new Data(0, String.valueOf(i), String.valueOf(i));
            if (current_year==i) {
                set.setSelected(true);
            }
            year.add(set);
        }

        return year;
    }

    public static ArrayList<Data> TEMPLATE_TIME_HOUR(boolean selectCurrentTime, @Nullable String selectedTime, boolean isUseSecond){
        Calendar cal = Calendar.getInstance();
        if (selectedTime!=null){
            try {
                String pattern = isUseSecond?"HH:mm:ss":"HH:mm";
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
                Date setDate = format.parse(selectedTime);
                if (setDate!=null)
                    cal.setTime(setDate);
            } catch (ParseException e){
                Log.e(TAG, "SetAsTimePicker: "+e.getMessage(), e);
            }
        }

        int current_hour = selectCurrentTime?cal.get(Calendar.HOUR_OF_DAY):0;

        // Hour
        ArrayList<Data> hour = new ArrayList<>();
        for (int i=0;i<24;i++){
            String value;
            if (i<10){
                value = "0" + String.valueOf(i);
            } else {
                value = String.valueOf(i);
            }

            Data set = new Data(i,value,value);
            if (i==current_hour) {
                set.setSelected(true);
            }
            hour.add(set);
        }
        return hour;
    }

    public static ArrayList<Data> TEMPLATE_TIME_MINUTE(boolean selectCurrentTime, @Nullable String selectedTime, boolean useSecond){
        Calendar cal = Calendar.getInstance();
        if (selectedTime!=null){
            try {
                String pattern = useSecond?"HH:mm:ss":"HH:mm";
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
                Date setDate = format.parse(selectedTime);
                if (setDate!=null)
                    cal.setTime(setDate);
            } catch (ParseException e){
                Log.e(TAG, "SetAsTimePicker: "+e.getMessage(), e);
            }
        }

        int current_minute = selectCurrentTime?cal.get(Calendar.MINUTE):0;

        // Minute
        ArrayList<Data> minute = new ArrayList<>();
        for (int i=0;i<60;i++){
            String value;
            if (i<10){
                value = "0" + String.valueOf(i);
            } else {
                value = String.valueOf(i);
            }

            Data set = new Data(i,value,value);
            if (i==current_minute) {
                set.setSelected(true);
            }
            minute.add(set);
        }

        return minute;
    }

    public static ArrayList<Data> TEMPLATE_TIME_SECOND(){
        ArrayList<Data> second = new ArrayList<>();
        for (int i=0;i<60;i++){
            String value;
            if (i<10){
                value = "0" + String.valueOf(i);
            } else {
                value = String.valueOf(i);
            }

            Data set = new Data(i,value,value);
            if (i==0) {
                set.setSelected(true);
            }
            second.add(set);
        }
        return second;
    }

    //=========================================== TOOLS ============================================
    public static String CompileDateData(Data day, Data month, Data year){
        String value = "";
        if (year!=null){
            value += year.getRealData();
        } else {
            value += "1990";
        }

        value+="-";
        if (month!=null){
            value += month.getRealData();
        } else {
            value += "01";
        }

        value+="-";
        if (day!=null){
            value += day.getRealData();
        } else {
            value += "01";
        }

        return value;
    }

    public static String CompileDateShowData(Data day, Data month, Data year){
        String value = "";

        if (day!=null){
            value += day.getShowData();
        } else {
            value += "01";
        }

        value+=" ";
        if (month!=null){
            value += month.getShowData();
        } else {
            value += "01";
        }

        value+=" ";
        if (year!=null){
            value += year.getShowData();
        } else {
            value += "1990";
        }

        return value;
    }

    public static String CompileTimeData(Data hour, Data minute, Data second){
        String value = "";
        if (hour!=null){
            value += hour.getRealData();
        } else {
            value += "00";
        }

        value += ":";
        if (minute!=null){
            value += minute.getRealData();
        } else {
            value += "00";
        }

        if (second!=null){
            value += ":" + second.getRealData();
        }

        return value;
    }
}
