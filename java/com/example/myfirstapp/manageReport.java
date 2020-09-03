package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.pax.poslink.CommSetting;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.ReportRequest;
import com.pax.poslink.ReportResponse;
import com.pax.poslink.poslink.POSLinkCreator;

public class manageReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    PosLink poslink;
    String text, text2;
    String result, result1, edctype, transtotal;
    ProcessTransResult TransResult;
    Intent intent1;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner6);
        text = spinner.getSelectedItem().toString();

        Spinner spinner2 = (Spinner)findViewById(R.id.spinner7);
        text2 = spinner2.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        text = "LOCALTOTALREPORT";
        text2 = "ALL";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_report);

        CommSetting commset = new CommSetting();
        commset.setType("AIDL");
        POSLinkAndroid.init(getApplicationContext());
        poslink = POSLinkCreator.createPoslink(getApplicationContext());
        poslink.SetCommSetting(commset);

        Spinner spinner = (Spinner) findViewById(R.id.spinner6);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner7);
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
        intent1 = new Intent(this, DisplayReportResult.class);
        ReportRequest reportReq = new ReportRequest();
        reportReq.TransType = reportReq.ParseTransType(text);
        reportReq.EDCType = reportReq.ParseEDCType(text2);
        poslink.ReportRequest = reportReq;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TransResult = poslink.ProcessTrans();
                result = TransResult.Code + ":" + TransResult.Msg;
                if(TransResult.Code.equals(ProcessTransResult.ProcessTransResultCode.OK)) {
                    ReportResponse reportRes = poslink.ReportResponse;
                    result1 = reportRes.ResultCode + ":" + reportRes.ResultTxt;
                    edctype = "EDC Type: " + reportRes.EDCType;
                    transtotal = "Trans Total: " + reportRes.TransTotal;
                    if (!result.equals(" ")) {
                        intent1.putExtra("info", result);
                    }
                    if (!result1.equals(" ")) {
                        intent1.putExtra("info1", result1);
                    }
                    if (!edctype.equals(" ")) {
                        intent1.putExtra("edctype", edctype);
                    }
                    if (!transtotal.equals(" ")) {
                        intent1.putExtra("transtotal", transtotal);
                    }
                    startActivity(intent1);
                }
            }
        });
        thread.start();
    }
}