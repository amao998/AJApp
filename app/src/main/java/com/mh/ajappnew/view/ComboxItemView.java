package com.mh.ajappnew.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mh.ajappnew.R;

/**
 * 自定义个人中心选项控件
 */
public class ComboxItemView extends RelativeLayout {
    private TextView text;
    private TextView value;


    public ComboxItemView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_combox_menu, this);
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ComboxltemView);

        ImageView more = findViewById(R.id.item_combox_more);
        ImageView line = findViewById(R.id.item_combox_line);
        value = findViewById(R.id.item_combox_value);
        TextView caption = findViewById(R.id.item_combox_caption);

        text = findViewById(R.id.item_combox_text);

        value.setText(typedArray.getText(R.styleable.ComboxltemView_comboxvalue));
        caption.setText(typedArray.getText(R.styleable.ComboxltemView_comboxcaption));

        if (typedArray.getBoolean(R.styleable.ComboxltemView_comboxshow_more, false)) {
            more.setVisibility(VISIBLE);
        }
        if (typedArray.getBoolean(R.styleable.ComboxltemView_comboxshow_line, false)) {
            line.setVisibility(VISIBLE);
        }
        typedArray.recycle();
    }

    // 提供设置控件的描述数据
    public void setText(String value) {
        this.text.setText(value);
    }

    public void setValue(String value) {
        this.value.setText(value);
    }

    public String getText() {
        return this.text.getText().toString();
    }

    public String getValue() {
        return this.value.getText().toString();
    }

    public void setFocusAA() {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
    }


}
