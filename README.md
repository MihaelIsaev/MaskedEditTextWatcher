# MaskedEditTextWatcher
Custom TextWatcher for EditText to mask phone number on-the-fly even with automatic country mask detection

![Demonstration GIF](https://github.com/MihaelIsaev/MaskedEditTextWatcher/raw/master/stuff/example.gif)

## How to use

### Manual list of masks
```java
//Instantiate your EditText
EditText phoneTextField = (EditText) findViewById(R.id.phoneTextField);
//Create text watcher
MaskedEditTextWatcher simpleListener = new MaskedEditTextWatcher(phoneTextField, new MaskedEditTextWatcherDelegate() {
    @Override
    public String maskForCountryCode(String text) {
        //Here you receive just entered text
        //and you should return the mask or null
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
//Add the textWatcher to your text field
phoneTextField.addTextChangedListener(simpleListener);
```

### Get mask from google phone library

First of all add the following line to your app module gradle file to install the google phone library
```
compile 'com.googlecode.libphonenumber:libphonenumber:7.1.1'
```

Then just implement MaskedEditTextWatcherDelegate using google phone library
```java
//Instantiate your EditText
EditText phoneTextField = (EditText) findViewById(R.id.phoneTextField);
//Create text watcher
MaskedEditTextWatcher glibListener = new MaskedEditTextWatcher(phoneTextField, new MaskedEditTextWatcherDelegate() {
    @Override
    public String maskForCountryCode(String text) {
        //Here you receive just entered text
        //and you should return the mask or null
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            //We just should check if entered text is numeric
            Double.parseDouble(text);
            //Then we should get the region code based on first entered digits
            String regionCode = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(text));
            //Then we get an example number for this region
            Phonenumber.PhoneNumber exampleNumber = phoneUtil.getExampleNumber(regionCode);
            //We should check the number and if it's null then we alse return null
            if (exampleNumber == null) return null;
            //Here we get country code as digits
            int countryCodeForRegion = phoneUtil.getCountryCodeForRegion(regionCode);
            //Here we create the string with country code with + symbol
            String detectedCountryCode = "+" + countryCodeForRegion;
            //Here we create example number but wothout country code
            String example = phoneUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            example = example.replace("8 ", ""); //needed for Russian example number
            example = example.replace(detectedCountryCode, "");
            //And finally we create full mask with country code
            return detectedCountryCode+" "+example.replaceAll("\\d", "#");
        } catch (NumberFormatException e) {
            //If entered text is not numeric then we shoul return null
            return null;
        }
    }
});
//Add the textWatcher to your text field
phoneTextField.addTextChangedListener(glibListener);
```

*This lib is under Apache 2.0 license.*
