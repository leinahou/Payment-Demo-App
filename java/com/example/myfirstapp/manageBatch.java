package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.pax.poslink.BatchRequest;
import com.pax.poslink.BatchResponse;
import com.pax.poslink.CommSetting;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.poslink.POSLinkCreator;

public class manageBatch extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    PosLink poslink;
    String text, text2, refnum;
    String result, result1, msg, batchnum, creditcount, creditamt;
    ProcessTransResult TransResult;
    Intent intent1;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner4);
        text = spinner.getSelectedItem().toString();

        Spinner spinner2 = (Spinner)findViewById(R.id.spinner5);
        text2 = spinner2.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        text = "BATCHCLOSE";
        text2 = "ALL";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_batch);

        CommSetting commset = new CommSetting();
        commset.setType("AIDL");
        POSLinkAndroid.init(getApplicationContext());
        poslink = POSLinkCreator.createPoslink(getApplicationContext());
        poslink.SetCommSetting(commset);

        Spinner spinner = (Spinner) findViewById(R.id.spinner4);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.batch_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner5);
        spinner1.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.edc_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);
    }

    public void processtrans(View view) {
        intent1 = new Intent(this, DisplayBatchResult.class);
        EditText refNum = findViewById(R.id.editTextTextPersonName2);
        refnum = refNum.getText().toString();
        BatchRequest batchReq = new BatchRequest();
        batchReq.TransType = batchReq.ParseTransType(text);
        batchReq.EDCType = batchReq.ParseEDCType(text2);
        batchReq.RefNum = refnum;
        poslink.BatchRequest = batchReq;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TransResult = poslink.ProcessTrans();
                result = TransResult.Code + ":" + TransResult.Msg;
                if(TransResult.Code.equals(ProcessTransResult.ProcessTransResultCode.OK)) {
                    BatchResponse batchRes = poslink.BatchResponse;
                    result1 = batchRes.ResultCode + ":" + batchRes.ResultTxt;
                    msg = "Message: " + batchRes.Message;
                    batchnum = "Batch Num: " + batchRes.BatchNum;
                    creditcount = "Credit Count: " + batchRes.CreditCount;
                    creditamt = "Credit Amount: " + batchRes.CreditAmount;
                    if (!result.equals(" ")) {
                        intent1.putExtra("info", result);
                    }
                    if (!result1.equals(" ")) {
                        intent1.putExtra("info1", result1);
                    }
                    if (!msg.equals(" ")) {
                        intent1.putExtra("msg", msg);
                    }
                    if (!batchnum.equals(" ")) {
                        intent1.putExtra("batchnum", batchnum);
                    }
                    if (!creditcount.equals(" ")) {
                        intent1.putExtra("creditcount", creditcount);
                    }
                    if (!creditamt.equals(" ")) {
                        intent1.putExtra("creditamt", creditamt);
                    }
                    startActivity(intent1);
                }
            }
        });
        thread.start();
    }
}