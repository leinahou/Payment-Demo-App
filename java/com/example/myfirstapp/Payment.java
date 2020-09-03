package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.pax.poslink.CommSetting;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.poslink.POSLinkCreator;
import com.pax.poslink.PaymentRequest;
import com.pax.poslink.PaymentResponse;

public class Payment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    PosLink poslink;
    String text,text2;
    String amt, origrefnum;
    String result, result1, resultamt, resultrefnum, resultextdata;
    ProcessTransResult TransResult;
    Intent intent1;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
         text = spinner.getSelectedItem().toString();

        Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);
         text2 = spinner2.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
          text = "CREDIT";
          text2 = "SALE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        CommSetting commset = new CommSetting();
        commset.setType("AIDL");
        POSLinkAndroid.init(getApplicationContext());
        poslink = POSLinkCreator.createPoslink(getApplicationContext());
        poslink.SetCommSetting(commset);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tender_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner2);
        spinner1.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.trans_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);
    }

    public void processtrans(View view){
        intent1 = new Intent(this, DisplayTransResult.class);
        EditText amount = findViewById(R.id.editTextTextPersonName);
        amt = amount.getText().toString();
        EditText origrefNum = findViewById(R.id.editTextTextPersonName4);
        origrefnum = origrefNum.getText().toString();
        PaymentRequest paymentReq = new PaymentRequest();
        paymentReq.TenderType = paymentReq.ParseTenderType(text);
        paymentReq.TransType = paymentReq.ParseTransType(text2);
        paymentReq.Amount = amt;
        paymentReq.OrigRefNum = origrefnum;
        paymentReq.ECRRefNum = "1";
        poslink.PaymentRequest = paymentReq;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TransResult = poslink.ProcessTrans();
                result = TransResult.Code + ":" + TransResult.Msg;
                if(TransResult.Code.equals(ProcessTransResult.ProcessTransResultCode.OK)) {
                    PaymentResponse paymentRes = poslink.PaymentResponse;
                    result1 = paymentRes.ResultCode + ":" + paymentRes.ResultTxt;
                    resultamt = "Approved amount: " + paymentRes.ApprovedAmount;
                    resultrefnum = "RefNum: " + paymentRes.RefNum;
                    resultextdata = "ExtData: " + paymentRes.ExtData;
                    if (!result.equals(" ")) {
                        intent1.putExtra("info", result);
                    }
                    if (!result1.equals(" ")) {
                        intent1.putExtra("info1", result1);
                    }
                    if (!resultamt.equals(" ")) {
                        intent1.putExtra("infoamt", resultamt);
                    }
                    if (!resultrefnum.equals(" ")) {
                        intent1.putExtra("inforefnum", resultrefnum);
                    }
                    if (!resultextdata.equals(" ")) {
                        intent1.putExtra("infoextdata", resultextdata);
                    }
                    startActivity(intent1);
                }
            }
        });
        thread.start();
    }
}
