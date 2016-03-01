package com.quaere.deepak.quaereshinecity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quaere.deepak.quaereshinecity.R;

/**
 * Created by deepak sachan on 8/26/2015.
 */
public class StatementAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] itemtext ;
    private final Integer[] imageid;
    public StatementAdapter(Context context, int resource, String[] itemtext, Integer[] imageid) {
        super(context,R.layout.mystatementadapter,itemtext);
        this.context =context;
        this.itemtext = itemtext;
        this.imageid = imageid;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.mystatementadapter, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_statement);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_statement);


        txtTitle.setText(itemtext[position]);
        imageView.setImageResource(imageid[position]);
        return rowView;
    }
}