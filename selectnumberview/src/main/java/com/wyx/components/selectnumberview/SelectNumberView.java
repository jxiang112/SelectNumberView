package com.wyx.components.selectnumberview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * @author: yongxiang.wei
 * @version: 1.2.0, 2019/4/13 20:23
 * @since: 1.2.0
 */
public class SelectNumberView extends LinearLayout {
    ImageView mBtnDecrease;
    ImageView mBtnIncrease;
    EditText mEtValue;
    View mLine1;
    View mLine2;

    Drawable mDecreaseEnableImg;
    Drawable mDecreaseDisableImg;

    Drawable mIncreaseEnableImg;
    Drawable mIncreaseDisableImg;

    private final int DEFAULT_STEP = 1;
    private final int DEFAULT_MIN_VALUE = 1;
    private final int DEFAULT_MAX_VALUE = 100;
    int mMinValue = DEFAULT_MIN_VALUE;
    int mMaxValue = DEFAULT_MAX_VALUE;
    int mValue = mMinValue;
    int mStep = DEFAULT_STEP;

    private final int VIEW_VISIBLE = 0;
    private final int VIEW_HIDDEN = 1;

    OnValueChangedListener mValueChangeListener;

    public SelectNumberView(Context context) {
        this(context, null);
    }

    public SelectNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SelectNumberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_select_number, this);
        mBtnDecrease = view.findViewById(R.id.select_number_decrease_btn);
        mBtnIncrease = view.findViewById(R.id.select_number_increase_btn);
        mEtValue = view.findViewById(R.id.select_number_value);
        mLine1 = view.findViewById(R.id.select_number_line_1);
        mLine2 = view.findViewById(R.id.select_number_line_2);

        mEtValue.setCursorVisible(false);

        requestFocus();
        requestFocusFromTouch();

        if(attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectNumberView);
            //descrease btn
            if(a.hasValue(R.styleable.SelectNumberView_decrease_enable_img)){
                mDecreaseEnableImg = a.getDrawable(R.styleable.SelectNumberView_decrease_enable_img);
            }else{
                mDecreaseEnableImg = getResources().getDrawable(R.drawable.ic_descrease);
            }
            if(a.hasValue(R.styleable.SelectNumberView_decrease_disable_img)){
                mDecreaseDisableImg = a.getDrawable(R.styleable.SelectNumberView_decrease_disable_img);
            }else{
                mDecreaseDisableImg = getResources().getDrawable(R.drawable.ic_descrease_disable);
            }

            //increase btn
            if(a.hasValue(R.styleable.SelectNumberView_increase_enable_img)){
                mIncreaseEnableImg = a.getDrawable(R.styleable.SelectNumberView_increase_enable_img);
            }else{
                mIncreaseEnableImg = getResources().getDrawable(R.drawable.ic_increase);
            }
            if(a.hasValue(R.styleable.SelectNumberView_increase_disable_img)){
                mIncreaseDisableImg = a.getDrawable(R.styleable.SelectNumberView_increase_disable_img);
            }else{
                mIncreaseDisableImg = getResources().getDrawable(R.drawable.ic_increase_disable);
            }

            //value
            if(a.hasValue(R.styleable.SelectNumberView_min_value)){
                mMinValue = a.getInt(R.styleable.SelectNumberView_min_value, DEFAULT_MIN_VALUE);
            }

            if(a.hasValue(R.styleable.SelectNumberView_max_value)){
                mMaxValue = a.getInt(R.styleable.SelectNumberView_max_value, DEFAULT_MAX_VALUE);
            }
            if(a.hasValue(R.styleable.SelectNumberView_value_step)){
                mStep = a.getInt(R.styleable.SelectNumberView_value_step, DEFAULT_STEP);
            }
            if(mMinValue > mMaxValue){
                int temp = mMinValue;
                mMaxValue = mMinValue;
                mMinValue = temp;
            }
            if(a.hasValue(R.styleable.SelectNumberView_value)){
                mValue = a.getInt(R.styleable.SelectNumberView_value, mMinValue);
            }
            if(mValue < mMinValue){
                mValue = mMinValue;
            }
            if(mValue > mMaxValue){
                mValue = mMaxValue;
            }
            if(mStep > mMaxValue){
                mStep = mMaxValue;
            }
            float textSize = 14f;
            if(a.hasValue(R.styleable.SelectNumberView_value_text_size)){
                textSize = a.getDimension(R.styleable.SelectNumberView_value_text_size, 14);
                mEtValue.setTextSize(textSize);
            }
            int textColor = Color.BLACK;
            if(a.hasValue(R.styleable.SelectNumberView_value_text_color)){
                textColor = a.getColor(R.styleable.SelectNumberView_value_text_color, textColor);
                mEtValue.setTextColor(textColor);
            }

            boolean isSplitLineShow = true;
            if(a.hasValue(R.styleable.SelectNumberView_split_line_show)){
                isSplitLineShow = a.getInt(R.styleable.SelectNumberView_split_line_show, VIEW_VISIBLE) == VIEW_VISIBLE;
            }
            if(isSplitLineShow){
                mLine1.setVisibility(View.VISIBLE);
                mLine2.setVisibility(View.VISIBLE);
            }else{
                mLine1.setVisibility(View.GONE);
                mLine2.setVisibility(View.GONE);
            }
            if(a.hasValue(R.styleable.SelectNumberView_split_line_width)){
                int width = a.getDimensionPixelSize(R.styleable.SelectNumberView_split_line_width, DisplayUtil.dip2px(context, 0.5f));
                ViewGroup.LayoutParams vlp = mLine1.getLayoutParams();
                vlp.width = width;
                mLine1.setLayoutParams(vlp);

                vlp = mLine2.getLayoutParams();
                vlp.width = width;
                mLine2.setLayoutParams(vlp);
            }
            if(a.hasValue(R.styleable.SelectNumberView_split_line_color)){
                textColor = a.getColor(R.styleable.SelectNumberView_split_line_color, Color.parseColor("#EDEDED"));
                mLine1.setBackgroundColor(textColor);
                mLine2.setBackgroundColor(textColor);
            }
        }
        mEtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int value = mValue;
                if(TextUtils.isEmpty(s)){
                    setValue(mMinValue, false);
                    return;
                }
                String valueStr = s.toString().trim();
                try{
                    value = Integer.parseInt(valueStr, 10);
                }catch (Exception e){
                    e.printStackTrace();
                }
                setValue(value, true);
            }
        });
        updateDescreaseImg(mValue > mMinValue);
        updateIncreaseImg(mValue < mMaxValue);
        setValueAndCursorEnd(mValue);
        setValue(mValue);
        OnClickListener clickListener = new OnClickListener(){

            @Override
            public void onClick(View v) {
                int vid = v.getId();
                if(vid == R.id.select_number_decrease_btn){
                    descrease();
                }else if(vid == R.id.select_number_increase_btn){
                    increase();
                }else if(vid == R.id.select_number_value){
                    mEtValue.setCursorVisible(true);
                    cursorEnd();
                }
            }
        };
        mBtnDecrease.setOnClickListener(clickListener);
        mBtnIncrease.setOnClickListener(clickListener);
        mEtValue.setOnClickListener(clickListener);
    }

    public TextView getValueView(){
        return mEtValue;
    }

    public void setMinValue(int pMinValue){
        mMinValue = pMinValue;
        if(mMinValue > mMaxValue){
            int temp = mMinValue;
            mMaxValue = mMinValue;
            mMinValue = temp;
        }
        if(mValue < mMinValue){
            setValue(mMinValue);
        }
        updateDescreaseImg(mValue > mMinValue);
        updateIncreaseImg(mValue < mMaxValue);
    }

    public void setMaxValue(int pMaxValue){
        mMaxValue = pMaxValue;
        if(mMinValue > mMaxValue){
            int temp = mMinValue;
            mMaxValue = mMinValue;
            mMinValue = temp;
        }
        if(mValue > mMaxValue){
            setValue(mMaxValue);
        }
        updateDescreaseImg(mValue > mMinValue);
        updateIncreaseImg(mValue < mMaxValue);
    }

    public int getValue(){
        return mValue;
    }

    public void setValue(int pValue){
        setValue(pValue, false);
    }
    public void setValue(int pValue, boolean pFromTextChg){
        if(mValue == pValue){
            return;
        }

        if(pValue < mMinValue){
            pValue = mMinValue;
            if(pFromTextChg){
                setValueAndCursorEnd(pValue);
                return;
            }
        }

        if(pValue > mMaxValue){
            pValue = mMaxValue;
            if(pFromTextChg){
                setValueAndCursorEnd(pValue);
                return;
            }
        }

        if(mValue == pValue){
            return;
        }

        if(pValue == mMinValue){
            updateDescreaseImg(false);
        }else{
            updateDescreaseImg(true);
        }

        if(pValue == mMaxValue){
            updateIncreaseImg(false);
        }else{
            updateIncreaseImg(true);
        }

        mValue = pValue;
        if(!pFromTextChg) {
            setValueAndCursorEnd(mValue);
        }
        if(mValueChangeListener != null){
            mValueChangeListener.onValueChanged(mValue);
        }
    }

    private void setValueAndCursorEnd(int pValue){
        mEtValue.setText(pValue + "");
        cursorEnd();
    }

    private void cursorEnd(){
        Editable editable = mEtValue.getText();
        int len = editable == null ? 0 : editable.length();
        mEtValue.setSelection(len);
    }

    public void increase(){
        if(mValue >= mMaxValue){
            return;
        }
        setValue(mValue + mStep);
    }

    public void descrease(){
        if(mValue <= mMinValue){
            return;
        }
        setValue(mValue - mStep);
    }

    private void updateDescreaseImg(boolean pEnable){
        if(pEnable){
            mBtnDecrease.setImageDrawable(mDecreaseEnableImg);
        }else{
            mBtnDecrease.setImageDrawable(mDecreaseDisableImg);
        }
    }

    private void updateIncreaseImg(boolean pEnable){
        if(pEnable){
            mBtnIncrease.setImageDrawable(mIncreaseEnableImg);
        }else{
            mBtnIncrease.setImageDrawable(mIncreaseDisableImg);
        }
    }

    public void setOnValueChangedListener(OnValueChangedListener pListener){
        mValueChangeListener = pListener;
    }

    public interface OnValueChangedListener{
        void onValueChanged(int pValue);
    }
}
