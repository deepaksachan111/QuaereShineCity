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
public class AccountAdapter extends ArrayAdapter<String> {
    Context context;
    private String[] data;
    private Integer[] imgid;

    public AccountAdapter(Context context, int resource, String[] data, Integer[] imgid) {
        super(context, resource, data);
        this.context = context;
        this.data = data;
        this.imgid = imgid;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.myaccountadapter, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_account);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_account);


        txtTitle.setText(data[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;
    }
}