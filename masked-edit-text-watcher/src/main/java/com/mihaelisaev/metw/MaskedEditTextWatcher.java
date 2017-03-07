package com.mihaelisaev.metw;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Mikhail Isaev on 05/03/2017.
 */

public class MaskedEditTextWatcher implements TextWatcher {

    private EditText mEditText;
    private String mMask = "";
    private String countryCode = "";
    private MaskedEditTextWatcherDelegate mDelegate;
    private boolean isUpdating;

    public MaskedEditTextWatcher(EditText editText, MaskedEditTextWatcherDelegate delegate) {
        mEditText = editText;
        mDelegate = delegate;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditText == null || mDelegate == null) return;
        if (!isUpdating && count == 0) {
            if (countryCode.length() > 0 && s.toString().equals("+") || s.toString().length() < countryCode.length()) {
                mMask = "";
                countryCode = "";
            }
            return;
        }
        String str = s.toString();
        if (!isUpdating && mMask.length() > 0 && countryCode.length() > 0 && (str.trim().equals(countryCode) || str.trim().length() < countryCode.length())) {
            mMask = "";
            countryCode = "";
            return;
        }

        if (!isUpdating && countryCode.equals("")) {
            String text = str.replace(" ", "").replace("+", "");
            String m = mDelegate.maskForCountryCode(text);
            if (m != null && m.length() > 0) {
                mMask = m;
                if (m.contains("+"+text)) {
                    str = "";
                    countryCode = "+"+text;
                } else {
                    countryCode = m.substring(0, m.indexOf(" "));
                    mEditText.setText(countryCode+str);
                }
            }
            return;
        }

        if (isUpdating) {
            isUpdating = false;
            return;
        }

        String masked = mask(mMask, unmask(str, countryCode));
        isUpdating = true;
        mEditText.setText(masked);
        mEditText.setSelection(masked.length());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    private String unmask(String s, String countryCode) {
        s = s.replace("+", "");
        s = s.substring(countryCode.replace("+", "").length());
        s = s.replaceAll("\\D", "");
        return s;
    }

    private String mask(String format, String text) {
        String maskedText = "";
        int i = 0;
        for (char character : format.toCharArray()) {
            if (character != '#') {
                maskedText += character;
                continue;
            }
            try {
                maskedText += text.charAt(i);
            } catch (Exception e) {
                break;
            }
            i++;
        }
        return maskedText;
    }
}
