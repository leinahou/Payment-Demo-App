package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.pax.poslink.CommSetting;
import com.pax.poslink.ManageRequest;
import com.pax.poslink.ManageResponse;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.entity.ScanResult;
import com.pax.poslink.peripheries.POSLinkPrinter;
import com.pax.poslink.peripheries.POSLinkScanner;
import com.pax.poslink.peripheries.ProcessResult;
import com.pax.poslink.poslink.POSLinkCreator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Printer extends AppCompatActivity implements POSLinkPrinter.PrintListener, POSLinkScanner.IScanListener {
    PosLink poslink;
    POSLinkPrinter printer;
    POSLinkScanner scanner;
    ProcessTransResult TransResult;
    String data="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        CommSetting commset = new CommSetting();
        commset.setType("AIDL");
        POSLinkAndroid.init(getApplicationContext());
        poslink = POSLinkCreator.createPoslink(getApplicationContext());
        poslink.SetCommSetting(commset);

        printer = POSLinkPrinter.getInstance(getApplicationContext());
        printer.setGray(4);
        printer.setPrintWidth(404);
        printer.cutPaper(-1);

        scanner = POSLinkScanner.getPOSLinkScanner(getApplicationContext(),"REAR");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            printer.print(b, this);
        }
        return true;
    }

    public void print_bitmap(View view){
        Bitmap b1 = POSLinkPrinter.convertSignDataToBitmap("0,65535^47,14^47,17^0,65535^105,10^104,13^104,17^103,20^103,23^102,26^101,29^101,33^101,36^100,39^100,42^99,46^99,49^98,52^97,55^97,59^96,62^95,65^94,68^93,72^90,75^88,78^85,79^82,79^78,79^75,78^72,76^69,75^66,73^62,71^59,69^56,67^53,63^50,61^47,58^0,65535^89,38^92,38^0,65535^35,15^36,18^37,21^38,25^39,28^41,31^44,33^47,34^50,36^54,36^57,37^60,37^63,37^66,37^70,37^73,38^76,38^79,38^82,40^86,41^89,42^92,42^95,42^~");
        printer.print(b1, this);
    }

    public void do_sign(View view){
        ManageRequest manageReq = new ManageRequest();
        manageReq.TransType = manageReq.ParseTransType("DOSIGNATURE");
        poslink.ManageRequest = manageReq;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                poslink.ProcessTrans();
            }
        });
        thread.start();
    }

    public void print_sign(View view) throws InterruptedException {
        getsign();
        Thread.sleep(3000);
        Bitmap b1 = POSLinkPrinter.convertSignDataToBitmap(data);
        printer.print(b1, this);
    }

    public void getsign(){
        ManageRequest manageReq1 = new ManageRequest();
        manageReq1.TransType = manageReq1.ParseTransType("GETSIGNATURE");
        poslink.ManageRequest = manageReq1;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TransResult = poslink.ProcessTrans();
                if(TransResult.Code.equals(ProcessTransResult.ProcessTransResultCode.OK)) {
                    ManageResponse manageRes1 = poslink.ManageResponse;
                    String path = manageRes1.SigFileName;
                    try {
                        data = getStringFromFile(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    public static String getStringFromFile (String filePath) throws Exception {
        File f = new File(filePath);
        FileInputStream fs = new FileInputStream(f);
        String res = convertStreamToString(fs);
        fs.close();
        return res;
    }
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public void print_string(View view){
        POSLinkPrinter.PrintDataFormatter formatter = new POSLinkPrinter.PrintDataFormatter();
        formatter.addLeftAlign().addContent("i love printing left");
        formatter.addCenterAlign().addContent("centerrrrrrr");
        formatter.addRightAlign().addContent("rightttt");
        String text = formatter.build();
        printer.print(text,this);
    }

    @Override
    public void onSuccess() {
        Log.i("result","print successfully!!!");
    }

    @Override
    public void onError(ProcessResult processResult) {
        Log.i("result","print error: " + processResult.getMessage() + processResult.getCode());
    }

    public void open_scanner(View view){
        scanner.open();
    }

    public void start_scanner(View view){
        scanner.start(this);
    }

    public void close_scanner(View view){
        scanner.close();
    }

    @Override
    public void onRead(ScanResult scanResult) {
        String result = "Scan Result: " + scanResult.getContent();
        TextView textView = findViewById(R.id.textView7);
        textView.setText(result);
    }

    @Override
    public void onFinish() {
        Log.i("result","scan finished!!!");
    }
}