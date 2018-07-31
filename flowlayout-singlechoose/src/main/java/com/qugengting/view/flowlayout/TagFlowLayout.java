package com.qugengting.view.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {

    private TagAdapter mTagAdapter;
    private boolean mAttachLabel = true;
    private boolean attachInput = true;
    private Set<Integer> mSelectedItemPosSet = new HashSet<>();

    private EditText editText;

    /**
     * 设置是否显示标签，默认显示
     * @param attachLabel 是否显示标签
     */
    public void setAttachLabel(boolean attachLabel) {
        this.mAttachLabel = attachLabel;
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        ta.recycle();
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            TagView tagView = (TagView) getChildAt(i);
            if (tagView.getVisibility() == View.GONE) {
                continue;
            }
            if (tagView.getTagView().getVisibility() == View.GONE) {
                tagView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setAdapter(TagAdapter adapter) {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        mSelectedItemPosSet.clear();
        changeAdapter();
    }

    private void removeItem(int position) {
        mTagAdapter.remove(mAttachLabel ? position - 1 : position);
        mSelectedItemPosSet.clear();
        changeAdapter();
    }

    private void addItem(String s) {
        mTagAdapter.add(s);
        mSelectedItemPosSet.clear();
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        TagAdapter adapter = mTagAdapter;
        TagView tagViewContainer;
        if (mAttachLabel) {
            View labelView = adapter.getLabelView(this);
            tagViewContainer = new TagView(getContext());
            labelView.setDuplicateParentStateEnabled(true);
            if (labelView.getLayoutParams() != null) {
                tagViewContainer.setLayoutParams(labelView.getLayoutParams());
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                lp.setMargins(dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5));
                tagViewContainer.setLayoutParams(lp);
            }
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            labelView.setLayoutParams(lp);
            tagViewContainer.addView(labelView);
            addView(tagViewContainer);
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));

            tagViewContainer = new TagView(getContext());
            tagView.setDuplicateParentStateEnabled(true);
            if (tagView.getLayoutParams() != null) {
                tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                lp.setMargins(dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5));
                tagViewContainer.setLayoutParams(lp);
            }
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            tagView.setLayoutParams(lp);
            tagViewContainer.addView(tagView);
            addView(tagViewContainer);

            tagView.setClickable(false);
            final TagView finalTagViewContainer = tagViewContainer;
            final int position = mAttachLabel ? i + 1 : i;
            tagViewContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelect(finalTagViewContainer, position);
                    editText.setCursorVisible(false);
                }
            });
        }
        if (attachInput) {
            editText = (EditText) adapter.getInputView(this);
            tagViewContainer = new TagView(getContext());
            editText.setDuplicateParentStateEnabled(true);
            if (editText.getLayoutParams() != null) {
                tagViewContainer.setLayoutParams(editText.getLayoutParams());
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.setMargins(dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5),
                        dip2px(getContext(), 5));
                tagViewContainer.setLayoutParams(lp);
            }
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            editText.setLayoutParams(lp);
            tagViewContainer.addView(editText);
            addView(tagViewContainer);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().contains(",") || s.toString().contains("，") && s.length() > 1) {
                        String ss = s.toString().substring(0, s.length() - 1);
                        addItem(ss);
                        editText.setText("");
                        s.clear();
                        editText.requestFocus();
                    }
                }
            });
            editText.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //执行了两次因为onkey事件包含了down和up事件，所以只需要加入其中一个即可
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                        if (mAttachLabel && getChildCount() <= 2) {
                            return false;
                        }
                        if (!mAttachLabel && getChildCount() <= 1) {
                            return false;
                        }
                        if (isSelect) {
                            removeItem(selectedIndex);
                            isSelect = false;
                            editText.requestFocus();
                            editText.setCursorVisible(false);
                        } else {
                            if (editText.length() == 0) {
                                doSelect((TagView) getChildAt(getChildCount() - 2), getChildCount() - 2);
                            }
                        }
                    }
                    return false;
                }
            });
            editText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setCursorVisible(true);
                }
            });
        }
    }

    private void showInput(final EditText et) {
        et.requestFocus();
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void setChildChecked(TagView view) {
        view.setChecked(true);
    }

    private void setChildUnChecked(TagView view) {
        view.setChecked(false);
    }

    private int selectedIndex = -1;
    private boolean isSelect = false;

    private void doSelect(TagView child, int position) {
        if (!child.isChecked()) {
            if (mSelectedItemPosSet.size() == 1) {
                Iterator<Integer> iterator = mSelectedItemPosSet.iterator();
                Integer preIndex = iterator.next();
                TagView pre = (TagView) getChildAt(preIndex);
                setChildUnChecked(pre);
                setChildChecked(child);

                mSelectedItemPosSet.remove(preIndex);
                mSelectedItemPosSet.add(position);
            } else {
                if (mSelectedItemPosSet.size() >= 1) {
                    return;
                }
                setChildChecked(child);
                mSelectedItemPosSet.add(position);
            }
            if (attachInput) {
                showInput(editText);
            }
            selectedIndex = position;
            isSelect = true;
        } else {
            setChildUnChecked(child);
            mSelectedItemPosSet.remove(position);
            isSelect = false;
        }
    }

    @Override
    public void onChanged() {
        mSelectedItemPosSet.clear();
        changeAdapter();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
