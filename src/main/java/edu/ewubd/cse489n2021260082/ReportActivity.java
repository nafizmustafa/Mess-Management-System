package edu.ewubd.cse489n2021260082;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private ListView lvExpenditureList;
    private TextView tvTotalCost;
    private Button btnAddNew,btnBack,btnSearch;
    private ArrayList<Item> items=new ArrayList<>();
    private CustomItemAdapter adapter;
    private EditText search_Bar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        lvExpenditureList=findViewById(R.id.lvExpenditureList);
        tvTotalCost=findViewById(R.id.tvTotalCost);
        btnAddNew=findViewById(R.id.btnAddNew);
        btnBack=findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        search_Bar = findViewById(R.id.search_bar);

        adapter= new CustomItemAdapter(this,items);
        lvExpenditureList.setAdapter(adapter);

        //Delete
        lvExpenditureList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                final Item selectedItem = items.get(position);

                // Show a confirmation dialog
                new android.app.AlertDialog.Builder(ReportActivity.this)
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Delete item from local DB and remote server
                            deleteItem(selectedItem);
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;  // Return true to indicate the event is handled
            }
        });


        //Search Button On click listener
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = search_Bar.getText().toString().trim();
                loadLocalData(searchText);
            }
        });

        // Real Time searching
        search_Bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadLocalData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });





        //

        lvExpenditureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = items.get(position);
                Intent intent = new Intent(ReportActivity.this, AddItemActivity.class);
                intent.putExtra("ID", selectedItem.id);
                intent.putExtra("ITEM-NAME", selectedItem.itemName);
                intent.putExtra("DATE", selectedItem.date);
                intent.putExtra("COST", selectedItem.cost);
                startActivity(intent);
            }
        });

        //

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(ReportActivity.this,AddItemActivity.class);
                startActivity(i);
            }
        });
        //
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(ReportActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
    public  void onStart(){
        super.onStart();
        loadLocalData("");
        loadRemoteData();
    }


    private void loadLocalData(String searchBy) {
        items.clear();
        double totalCost = 0;
        ItemDB db = new ItemDB(this);
        String q = "SELECT * FROM items";
        if (!searchBy.isEmpty()) {
            q += " WHERE itemName LIKE '%" + searchBy + "%'";
        }

        Cursor c = db.selectItems(q);
        while (c.moveToNext()) {
            String id = c.getString(0);
            String itemName = c.getString(1);
            long date = c.getLong(2);
            double cost = c.getDouble(3);

            Item i = new Item(id, itemName, cost, date);
            items.add(i);
            totalCost += cost;
        }
        adapter.notifyDataSetChanged();
        tvTotalCost.setText(String.format("%.2f", totalCost));
    }


    private void loadRemoteData(){
        String keys[] = {"action", "sid", "semester"};
        String values[] = {"restore", "2021-2-60-082", "2024-3"};
        httpRequest(keys, values);

    }
    // delete items from local server
    private void deleteItem(Item item) {
        // Delete the item from the local database
        ItemDB db = new ItemDB(ReportActivity.this);
        db.deleteItem(item.id);  // Deletes item based on ID
        db.close();

        // Remove the item from the items list and refresh the ListView
        items.remove(item);
        adapter.notifyDataSetChanged();

        // Show a toast to notify the user
        Toast.makeText(ReportActivity.this, "Item deleted locally.", Toast.LENGTH_SHORT).show();

        // Also delete the item from the server
        deleteItemFromServer(item.id);
    }
    //This method sends a delete request to the server using an HTTP request.
    private void deleteItemFromServer(String itemId) {
        String keys[] = {"action", "sid", "semester", "id"};
        String values[] = {"remove", "2021-2-60-082", "2024-3", itemId};

        // Send the HTTP request to delete the item from the server
        httpRequest(keys, values);
    }


    private void httpRequest(final String keys[], final String values[]) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                ArrayList<Object> params = new ArrayList<>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
                String url = "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data = RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    // Handle the response from the server
                    handleServerResponse(data);
                    updateLocalDBByServerData(data);
                } else {
                    // If there was no response from the server, show an error
                    Toast.makeText(ReportActivity.this, "Error deleting item from server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
// this method to handle the server response after attempting to delete the item from the server.
private void handleServerResponse(String response) {
    try {
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
            // Successfully deleted from the server
            Toast.makeText(ReportActivity.this, "Item deleted from server.", Toast.LENGTH_SHORT).show();
        } else {
            // Handle failure case (item not deleted)
            Toast.makeText(ReportActivity.this, "Failed to delete item from server.", Toast.LENGTH_SHORT).show();
        }
    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(ReportActivity.this, "Error parsing server response.", Toast.LENGTH_SHORT).show();
    }


}



    private void updateLocalDBByServerData (String data){
        System.out.println("found");
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("classes")){
                items.clear();
                double totalCost=0;
                JSONArray ja = jo.getJSONArray("classes");
                ItemDB db=new ItemDB(ReportActivity.this);
                for(int i=0; i<ja.length(); i++){
                    JSONObject item = ja.getJSONObject(i);
                    String id = item.getString("id");
                    String itemName = item.getString("itemName");
                    double cost = item.getDouble("cost");
                    long date = item.getLong("date");
                    Item item1=new Item(id,itemName,cost,date);
                    items.add(item1);
                    System.out.println(item1);
                    totalCost+=cost;
                    db.updateItem(id,itemName,date,cost);


                }
            }
        }catch(Exception e){}
    }



                    private long getDateInMilliSecond(String date){
        long dateInMilliSecond = 0; // write  code to convert date value to millisecond here
        return dateInMilliSecond;
    }
}