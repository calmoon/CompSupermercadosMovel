package com.dispmoveis.compsupermercadosmovel.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Locale;

public class MoneyInputWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private final Locale locale;
    private final DecimalFormat formatter;

    public MoneyInputWatcher(EditText editText, Locale locale, Boolean ignoreSymbol) {
        this.editTextWeakReference = new WeakReference<>(editText);
        this.locale = locale != null ? locale : Locale.getDefault();
        if (ignoreSymbol) {
            this.formatter = (DecimalFormat) NumberFormat.getNumberInstance(locale);
            formatter.setMinimumFractionDigits(2);
        } else {
            this.formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;
        editText.removeTextChangedListener(this);

        BigDecimal parsed = Util.currencyToBigDecimal(editable.toString(), locale);

        String formatted =  formatter.format(parsed);

        editText.setText(formatted);
        editText.setSelection(formatted.length());

        editText.addTextChangedListener(this);
    }

}