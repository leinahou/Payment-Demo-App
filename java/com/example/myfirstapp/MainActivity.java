package com.example.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String type;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner3);
        type = spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
       type="AIDL";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTimeout(1);

        Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.comm_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void setTimeout(int screenOffTimeout) {
        int time;
        switch (screenOffTimeout) {
            case 0:
                time = 15000;
                break;
            case 1:
                time = 30000;
                break;
            case 2:
                time = 60000;
                break;
            case 3:
                time = 120000;
                break;
            case 4:
                time = 600000;
                break;
            case 5:
                time = 1800000;
                break;
            default:
                time = -1;
        }
        android.provider.Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, time);
    }

    public void setComm(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ManageInitActivity.class);

        EditText destIP = (EditText) findViewById(R.id.editTextTextPersonName3);
        String dest_ip = destIP.getText().toString();
        EditText destPORT = (EditText) findViewById(R.id.editTextTextPersonName5);
        String dest_port = destPORT.getText().toString();
        EditText timeout = (EditText) findViewById(R.id.editTextTextPersonName6);
        String time_out = timeout.getText().toString();

        intent.putExtra("destipextra", dest_ip);
        intent.putExtra("destportextra", dest_port);
        intent.putExtra("timeoutextra", time_out);
        intent.putExtra("commType", type);
        startActivity(intent);
    }
}