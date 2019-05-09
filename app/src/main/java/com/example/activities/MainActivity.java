package com.example.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

//import com.example.api.DonationApi;
import com.example.api.DonationApi;
import com.example.donation10.R;
import com.example.models.Donation;

import java.util.List;

public class MainActivity extends Base {
    private Button donateButton;
    private RadioGroup paymentMethod;
    private ProgressBar progressBar;
    private NumberPicker amountPicker;
    private EditText amountText;
    private TextView amountTotal;
//    private int totalDonated=0;

//    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        onResume();
        donateButton = (Button) findViewById(R.id.donateButton);
        if(donateButton!=null){
            Log.v("Donate","Really got the donate button");
        }
        paymentMethod = (RadioGroup) findViewById(R.id.paymentMethod);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        amountPicker=(NumberPicker) findViewById(R.id.amountPicker);
        amountText = (EditText) findViewById(R.id.paymentAmount);
        amountTotal= (TextView) findViewById(R.id.totalSoFar);

        amountPicker.setMaxValue(1000);
        amountPicker.setMinValue(0);
        progressBar.setMax(10000);
        amountTotal.setText("$0");
//        app.dbManager.setTotalDonated(this);
        String totalDonatedStr = "$" + app.totalDonated;
        amountTotal.setText(totalDonatedStr);
        progressBar.setProgress(app.totalDonated);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.menuReport:
                Intent intent= new Intent(this,Report.class);
                this.startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void donateButtonPressed(View view){
        int donateAmount=amountPicker.getValue();
        int radioId=paymentMethod.getCheckedRadioButtonId();
        String method=radioId==R.id.PayPal?"PayPal":"Direct";
        if(donateAmount==0){
            String text=amountText.getText().toString();
            if(!text.equals("")){
                donateAmount=Integer.parseInt(text);

            }
        }
        if(donateAmount>0){
            app.newDonation(new Donation(donateAmount,method,0));
            progressBar.setProgress(app.totalDonated);
            String totalDonatedStr = "$" + app.totalDonated;
            amountTotal.setText(totalDonatedStr);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("onResume","Runing");
        new GetAllTask(this).execute("/donations");
    }

    @Override
    public void reset(MenuItem item) {
        super.reset(item);
//        app.dbManager.reset();
//        app.totalDonated=0;
//        progressBar.setProgress(app.totalDonated);
//        String totalDonatedStr = "$" + app.totalDonated;
//        amountTotal.setText(totalDonatedStr);
    }



    public class GetAllTask extends AsyncTask<String, Void, List<Donation>> {
        protected ProgressDialog dialog;
        protected Context context;
        public GetAllTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog=new ProgressDialog(context,1);
            this.dialog.setMessage("Retrieving donations List");
            this.dialog.show();
        }

        @Override
        protected List<Donation> doInBackground(String... params) {
            try {
                Log.v("donate", "Donation App Getting All Donations");
                List<Donation> x=(List<Donation>) DonationApi.getAll((String) params[0]);
                Log.v("donation","doInBackground "+x.toString());
                return x;
            }
            catch (Exception e){
                Log.v("donate", "ERROR: "+e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Donation> result) {
            super.onPostExecute(result);
//            Log.v("donate","RESUILT: "+result.toString());
            for(Donation d:result){
                app.totalDonated+=d.amount;
            }
            progressBar.setProgress(app.totalDonated);
            amountTotal.setText("$"+app.totalDonated);
            if(dialog.isShowing()) dialog.dismiss();
        }
    }

}
