package com.mihaelisaev.metw_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mihaelisaev.metw.MaskedEditTextWatcher;
import com.mihaelisaev.metw.MaskedEditTextWatcherDelegate;

/**
 * Created by Mikhail Isaev on 05/03/2017.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SIMPLE
        EditText phoneTextField1 = (EditText) findViewById(R.id.phoneTextField1);
        MaskedEditTextWatcher simpleListener = new MaskedEditTextWatcher(phoneTextField1, new MaskedEditTextWatcherDelegate() {
            @Override
            public String maskForCountryCode(String text) {
                if (text.equals("1")) {
                    return "+1 ###-###-####";
                } else if (text.equals("7")) {
                    return "+7 (###) ###-##-##";
                } else if (text.equals("44")) {
                    return "+44 (##) ###-####";
                } else if (text.equals("64")) {
                    return "+64 ## # (###) ##-##";
                }
                return null;
            }
        });
        phoneTextField1.addTextChangedListener(simpleListener);

        //GLIB
        EditText phoneTextField2 = (EditText) findViewById(R.id.phoneTextField2);
        MaskedEditTextWatcher glibListener = new MaskedEditTextWatcher(phoneTextField2, new MaskedEditTextWatcherDelegate() {
            @Override
            public String maskForCountryCode(String text) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    Double.parseDouble(text);
                    String regionCode = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(text));
                    Phonenumber.PhoneNumber exampleNumber = phoneUtil.getExampleNumber(regionCode);
                    if (exampleNumber == null) return null;
                    int countryCodeForRegion = phoneUtil.getCountryCodeForRegion(regionCode);
                    String detectedCountryCode = "+" + countryCodeForRegion;
                    String example = phoneUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                    example = example.replace("8 ", "");
                    example = example.replace(detectedCountryCode, "");
                    return detectedCountryCode+" "+example.replaceAll("\\d", "#");
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
        phoneTextField2.addTextChangedListener(glibListener);
    }
}
