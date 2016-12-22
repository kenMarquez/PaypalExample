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
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

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

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
    }

    private void initializeConfigurationPaypal() {
        mPayPalConfiguration =
                new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                        .clientId("AXWoLCb7viR9piqf5Eih0qogMjaAZn41izBQo4JXJMcNZEfrSkgepT8n0QFjMCFoGb06gwO3trw5rvrH")
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("myLog", "aqui");
//        if (requestCode == REQUEST_CODE_PAYPAL) {
//
//            if (resultCode == Activity.RESULT_OK) {
//
//                PayPalAuthorization auth =
//                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
//                if (auth != null) {
////                    sendAuthorizationCodePaypal(auth.getAuthorizationCode());
//                } else {
////                    errorAddPaypal();
//                }
//            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
////                errorAddPaypal();
//            }
//        }
//    }

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


        //Creating a paypalpayment and add the amount
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf("100")), "USD", "Simplified Coding Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;

    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AXWoLCb7viR9piqf5Eih0qogMjaAZn41izBQo4JXJMcNZEfrSkgepT8n0QFjMCFoGb06gwO3trw5rvrH");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);


                        Log.d("myLog", paymentDetails);


                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


}
