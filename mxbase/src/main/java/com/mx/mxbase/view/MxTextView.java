package com.mx.mxbase.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.mx.mxbase.model.MxTextViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xj on 2018/1/2.
 */

public class MxTextView extends TextView {
    /**
     * 文字源
     */
    private String sourceText="";
    /**
     * 当前页数
     */
    private int page = 0;
    /**
     * 分页显示文字
     */
    private List<MxTextViewModel> textModels = new ArrayList<MxTextViewModel>();
    /**
     * 是否分页完成
     */
    private boolean pagingSucess=false;
    /**
     * 速度监听
     */
    private VelocityTracker mVelocityTracker;

    public MxTextView(Context context) {
        super(context);
        init(context);
    }

    public MxTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MxTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resize();
        mVelocityTracker=VelocityTracker.obtain();
    }

    public void setSourceText(String sourceText) {
        try {
            this.sourceText = sourceText;
            setText(sourceText);
            clearSource();
        }catch (Exception e){

        }

    }
    public void clearSource(){
        textModels.clear();
        pagingSucess=false;
        page=0;
        resize();
    }

    /**
     * 去除当前页无法显示的字
     *
     * @return 去掉的字数
     */
    public int resize() {
        if (textModels.size()!=0&&page>0) {
            setText(sourceText.substring(textModels.get(page-1).index));
        }
        CharSequence oldContent = getText();
        int index = getCharNum();
        CharSequence newContent = oldContent.subSequence(0, index);
        setText(newContent);

        if (textModels.size() <= page) {
            if (textModels.size()!=0) {
                index+=textModels.get(page-1).index;
            }
            if (index==sourceText.length()){
                pagingSucess=true;
            }
            if (index>0) {
                if (page==(textModels.size()-1))textModels.remove(page);
                textModels.add(new MxTextViewModel(newContent, page, index));
            }
        }
        return oldContent.length() - newContent.length();
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int downX;
    private int downY;
    private int mTouchSlop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        if (event.getAction() == MotionEvent.ACTION_UP||event.getAction() == MotionEvent.ACTION_CANCEL) {
            mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = mVelocityTracker.getXVelocity();
            mVelocityTracker.clear();
            if (Math.abs(xVelocity) <= 200) {//速度不够
                return false;
            }

            int upx = (int) event.getRawX();
            int upy = (int) event.getRawY();
            if (Math.abs(downX - upx) > mTouchSlop&& Math.abs(downY - upy) < mTouchSlop*3) {
                flipOver((downX - upx) > 0);
            } else if (Math.abs(downY - upy) > mTouchSlop&&Math.abs(downX - upx) > mTouchSlop*3) {
                flipOver((downY - upy) > 0);
            } else {
                return false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getRawX();
            downY = (int) event.getRawY();
        }
        return true;
    }

    /**
     * 是否是下一页
     *
     * @param next 下一页为true
     */
    public void flipOver(boolean next) {
        if (textModels.size()==0)return;
        if (next) {
            if (page>=(textModels.size()-1)&&!pagingSucess){
                if (page>=textModels.size())page=textModels.size()-1;
                page++;
                resize();
            }else if (page<(textModels.size()-1)){
                page++;
                setText(textModels.get(page).showTxt);
            }
        } else if (page > 0){
            page--;
            setText(textModels.get(page).showTxt);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mVelocityTracker!=null)
        mVelocityTracker.recycle();
    }
}
