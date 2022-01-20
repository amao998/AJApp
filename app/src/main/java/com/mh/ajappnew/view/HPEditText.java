package com.mh.ajappnew.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class HPEditText extends EditText {
    private boolean isRun = false;
    private Boolean isNeedHeng = true;
    private String d = "";

    public HPEditText(Context context) {
        super(context);
        setBankCardTypeOn();
    }

    public HPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBankCardTypeOn();
    }

    public HPEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBankCardTypeOn();
    }

    public void SetStlye(boolean isneedheng) {
        isNeedHeng = isneedheng;
    }

    private void setBankCardTypeOn() {
        HPEditText.this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isNeedHeng==false)
                {
                    return;
                }

                if (isRun) {
                    isRun = false;
                    return;
                }
                isRun = true;
                d = "";
                String newStr = s.toString();

                if(newStr.length()>8)
                {
                    HPEditText.this.setText(newStr.substring(0,8));
                    HPEditText.this.setSelection(8);
                    return;
                }

                newStr = newStr.replace("-", "");
                int index = 0;
                while ((index + 2) < newStr.length()) {
                    d += (newStr.substring(index, index + 2) + "-");
                    index += 2;
                }
                d += (newStr.substring(index, newStr.length()));
                int i = getSelectionStart();
                HPEditText.this.setText(d);
                try {

                    if (i % 2 == 0 && before == 0) {
                        if (i + 1 <= d.length()) {
                            HPEditText.this.setSelection(i + 1);
                        } else {
                            HPEditText.this.setSelection(d.length());
                        }
                    } else if (before == 1 && i < d.length()) {
                        HPEditText.this.setSelection(i);
                    } else if (before == 0 && i + 1 < d.length()) {
                        HPEditText.this.setSelection(i);
                    } else
                        HPEditText.this.setSelection(d.length());


                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        HPEditText.this.setTransformationMethod(new ReplacementTransformationMethod() {
            @Override
            protected char[] getOriginal() {
                char[] originalCharArr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
                return originalCharArr;
            }

            @Override
            protected char[] getReplacement() {
                char[] replacementCharArr = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
                return replacementCharArr;
            }
        });

    }

    //对外提供暴漏的方法
    public void insertText(EditText editText, String mText) {
        editText.getText().insert(getSelectionStart(), mText);

    }


}
