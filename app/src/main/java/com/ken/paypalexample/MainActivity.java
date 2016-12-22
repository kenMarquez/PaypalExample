package com.ken.paypalexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private PayPalConfiguration mPayPalConfiguration;
    private static final int REQUEST_CODE_PAYPAL = 12;

    private static final String NAME_COMPANY_PAYPAL = "OhanaHome";
    private static final String URL_PRIVACY_POLICY_PAYPAL = "https://www.ohanahome.mx/privacy";
    private static final String URL_USER_AGREEMENT_PAYPAL = "https://www.ohanahome.mx/legal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Pago");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeConfigurationPaypal() {
        mPayPalConfiguration =
                new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                        .clientId("AQBfiZMtnqUlzEQAc4GPPbz7k5DUkJPt27IZwvL___hezwgzEZpe40S276fr_B6ok3jt_VfnpXn9_Hay")
                        .merchantName(NAME_COMPANY_PAYPAL)
                        .merchantPrivacyPolicyUri(Uri.parse(URL_PRIVACY_POLICY_PAYPAL))
                        .merchantUserAgreementUri(Uri.parse(URL_USER_AGREEMENT_PAYPAL));
    }

    private void initializeServicePaypal() {
        Intent intent = new Intent(MainActivity.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
        startService(intent);
    }

    private void finishPaypalService() {
        stopService(new Intent(MainActivity.this, PayPalService.class));
    }


    public void showAddPaypalScreen() {
        Intent intent = new Intent(MainActivity.this, PayPalFuturePaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
        startActivityForResult(intent, REQUEST_CODE_PAYPAL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("myLog", "aqui");
        if (requestCode == REQUEST_CODE_PAYPAL) {

            if (resultCode == Activity.RESULT_OK) {

                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
//                    sendAuthorizationCodePaypal(auth.getAuthorizationCode());
                } else {
//                    errorAddPaypal();
                }
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
//                errorAddPaypal();
            }
        }
    }

    public void showErrorAddPaypal() {
//        showDefaultMessageSnackBar(R.string.message_error_add_paypal);
    }

    /**
     * @param idString Id del texto a mostrar.
     */
    protected void showDefaultMessageSnackBar(@StringRes int idString) {


    }

    @OnClick(R.id.layout_credit_card)
    public void addCreditCard() {
        startActivity(new Intent(this, AgregarPago.class));
    }

    @OnClick(R.id.layout_paypal)
    public void addPaypal() {
        initializeConfigurationPaypal();
        initializeServicePaypal();
        showAddPaypalScreen();
    }




}
