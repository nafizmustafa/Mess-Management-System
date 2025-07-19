package edu.ewubd.cse489n2021260082;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {
    private EditText etItemName, etCost,etDate;
    private Button btnSave;
    private String id = "";
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_item);


        etItemName=findViewById(R.id.etItemName);
        etCost=findViewById(R.id.etCost);
        etDate=findViewById(R.id.etDate);
        btnSave=findViewById(R.id.btnSave);

        // Set up date picker for the date field
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(selectedDate.getTime());
                etDate.setText(formattedDate);
            },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });


        Intent i = getIntent();
        if(i != null && i.hasExtra("ID")){
            id = i.getStringExtra("ID");
            String itemName = i.getStringExtra("ITEM-NAME");
            long dateInMilliSeconds = i.getLongExtra("DATE", 0);
            double cost = i.getDoubleExtra("COST", 0);
            String date = dateInMilliSeconds+"";
            etItemName.setText(itemName);
            etCost.setText(String.valueOf(cost));
            etDate.setText(date);
        }
        //

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String itemName=etItemName.getText().toString().trim();
                String cost=etCost.getText().toString().trim();
                String date=etDate.getText().toString().trim();

                // validate date
                //write code

                if ( itemName.length() < 2 || itemName.length() > 20) {
                    Toast.makeText(AddItemActivity.this,"Item name must be between 2 and 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cost.isEmpty()) {
                    Toast.makeText(AddItemActivity.this, "Cost cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                double parsedCost = Double.parseDouble(cost);
                if (parsedCost <= 0) {
                    Toast.makeText(AddItemActivity.this, "Cost must be a positive value", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (date.isEmpty()) {
                    Toast.makeText(AddItemActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if all data are valid, then store
                int VALUEOFYEAR=2024;
                int VALUEOFMONTH=12;
                int VALUEOFDATE=3;

                Calendar currentCal= Calendar.getInstance();
                long currentTime=currentCal.getTimeInMillis();



                currentCal.setTimeInMillis(0);
                currentCal.set(Calendar.YEAR,VALUEOFYEAR);
                currentCal.set(Calendar.MONTH,VALUEOFMONTH);
                currentCal.set(Calendar.DATE,VALUEOFDATE);



                double costValue=Double.parseDouble(cost);
                long dateValue=currentTime;
                //



                ItemDB db = new ItemDB(AddItemActivity.this);
                if(id.isEmpty()){
                    id = itemName+":"+currentTime;
                    db.insertItem(id, itemName, dateValue, costValue);
                } else{
                    db.updateItem(id, itemName, dateValue, costValue);
                }
                db.close();



                //store data record to remote data base
                String keys[] = {"action", "sid", "semester", "id", "itemName", "cost", "date"};
                String values[] = {"backup", "2021-2-60-082", "2024-3", id, itemName, String.valueOf(costValue), String.valueOf(dateValue)};

                httpRequest(keys, values);

                finish();

            }
        });
    }

    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data= RemoteAccess.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }



}