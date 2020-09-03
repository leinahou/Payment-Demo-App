package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.pax.poslink.CommSetting;
import com.pax.poslink.ManageRequest;
import com.pax.poslink.ManageResponse;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.poslink.POSLinkCreator;

public class ManageInitActivity extends AppCompatActivity {
    PosLink poslink;
    String result, result1, sn, model, os, extdata;
    ProcessTransResult TransResult;
    Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_init);

        Intent intent = getIntent();
        String type = intent.getStringExtra("commType");
        String destIP = intent.getStringExtra("destipextra");
        String destPORT = intent.getStringExtra("destportextra");
        String timeOUT = intent.getStringExtra("timeoutextra");
        CommSetting commset = new CommSetting();
        commset.setType(type);
        commset.setDestIP(destIP);
        commset.setDestPort(destPORT);
        commset.setTimeOut(timeOUT);
        POSLinkAndroid.init(getApplicationContext());
        poslink = POSLinkCreator.createPoslink(getApplicationContext());
        poslink.SetCommSetting(commset);
    }

    public void manageInit(View view) {
        intent1 = new Intent(this, DisplayMessageActivity.class);
        ManageRequest manageReq = new ManageRequest();
        manageReq.TransType = manageReq.ParseTransType("INIT");
        poslink.ManageRequest = manageReq;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TransResult = poslink.ProcessTrans();
                result = TransResult.Code + ":" + TransResult.Msg;
                if(TransResult.Code.equals(ProcessTransResult.ProcessTransResultCode.OK)) {
                    ManageResponse manageRes = poslink.ManageResponse;
                    result1 = manageRes.ResultCode + ":" + manageRes.ResultTxt;
                    sn = "SN: " + manageRes.SN;
                    model = "Model Name: " + manageRes.ModelName;
                    os = "OS Version: " + manageRes.PrimaryFirmVersion;
                    extdata = "ExtData: " + manageRes.ExtData;
                    if (!result.equals(" ")) {
                        intent1.putExtra("info", result);
                    }
                    if (!result1.equals(" ")) {
                        intent1.putExtra("info1", result1);
                    }
                    if (!sn.equals(" ")) {
                        intent1.putExtra("infosn", sn);
                    }
                    if (!model.equals(" ")) {
                        intent1.putExtra("infomodel", model);
                    }
                    if (!os.equals(" ")) {
                        intent1.putExtra("infoos", os);
                    }
                    if (!extdata.equals(" ")) {
                        intent1.putExtra("infoextdata", extdata);
                    }
                    startActivity(intent1);
                }
            }
        });
        thread.start();
    }

    public void managePayment(View view) {
        intent1 = new Intent(this, Payment.class);
        startActivity(intent1);
    }

    public void manageBatch(View view) {
        intent1 = new Intent(this, manageBatch.class);
        startActivity(intent1);
    }

    public void manageReport(View view) {
        intent1 = new Intent(this, manageReport.class);
        startActivity(intent1);
    }

    public void managePeri(View view) {
        intent1 = new Intent(this, Printer.class);
        startActivity(intent1);
    }
}