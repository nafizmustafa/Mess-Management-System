package edu.ewubd.cse489n2021260082;

import android.content.Context;
import android.media.metrics.Event;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomItemAdapter extends ArrayAdapter<Item> {


    private final ArrayList<Item> records;
    private LayoutInflater inflater;

    public CustomItemAdapter(@NonNull Context context, @NonNull ArrayList<Item> records) {
        super(context, -1, records);
        this.records = records;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View template = inflater.inflate(R.layout.row_item, parent, false);

        TextView tvSN = template.findViewById(R.id.tvSN);
        TextView tvItemName = template.findViewById(R.id.tvItemName);
        TextView tvDate = template.findViewById(R.id.tvDate);
        TextView tvCost = template.findViewById(R.id.tvCost);

        tvSN.setText(String.valueOf(position+1));
        tvItemName.setText(records.get(position).itemName);
        tvDate.setText(getFormattedDate(records.get(position).date));
        tvCost.setText(String.valueOf(records.get(position).cost));

        return template;
    }
    private String getFormattedDate(long milliseconds){
        // Create a Date object from milliseconds
        Date date = new Date(milliseconds);

        // Define the desired format (you can modify this as needed)
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // Format the date and return the formatted string
        return formatter.format(date);
    }
}
