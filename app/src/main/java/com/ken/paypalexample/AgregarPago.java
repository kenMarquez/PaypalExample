package com.ken.paypalexample;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;

public class AgregarPago extends AppCompatActivity {

    private static final int MY_SCAN_REQUEST_CODE = 600;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.add_payment_et_card_number)
    EditText inputCardNumber;

    @BindView(R.id.add_payment_et_month)
    EditText inputMonth;

    @BindView(R.id.add_payment_et_year)
    EditText inputYear;

    @BindView(R.id.add_payment_et_cvv)
    EditText inputCvv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pago);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Agregar pago");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.add_payment_scan)
    public void scanCard() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

//                inputCardNumber.setText(scanResult.getRedactedCardNumber());
                inputCardNumber.setText(scanResult.getFormattedCardNumber());


                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    Log.d("myLog", resultDisplayStr);
                    String exMonth = (scanResult.expiryMonth < 10) ? "0" + scanResult.expiryMonth : scanResult.expiryMonth + "";
                    String exYaer = scanResult.expiryYear + "";
                    inputMonth.setText(exMonth);
                    inputYear.setText(exYaer.substring(exYaer.length() - 2, exYaer.length()));
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    inputCvv.setText(scanResult.cvv + "");
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            } else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    @OnClick(R.id.btn_add_payment)
    public void tokenize() {
        Conekta.setPublicKey("key_FzYpy3zwTbvc31T3r18yAqA"); //Set public key
        Conekta.setApiVersion("1.0.0"); //Set api version (optional)
        Conekta.collectDevice(AgregarPago.this); //Collect device

        Card card = new Card("name", inputCardNumber.getText().toString(),
                inputCvv.getText().toString(), inputMonth.getText().toString(), inputYear.getText().toString());
        Token token = new Token(AgregarPago.this);

        token.onCreateTokenListener(new Token.CreateToken() {
            @Override
            public void onCreateTokenReady(JSONObject data) {
                try {
                    //Use token
                    Log.d("Token::::", data.getString("id"));
                } catch (Exception err) {
                    //Show error to user
                    Log.d("Error: ", err.toString());
                }
            }
        });

        token.create(card);
    }
}
