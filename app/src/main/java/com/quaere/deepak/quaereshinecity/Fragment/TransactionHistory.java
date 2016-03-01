package com.quaere.deepak.quaereshinecity.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.quaere.deepak.quaereshinecity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TransactionHistory extends Fragment implements View.OnClickListener {

    private EditText fromDateEtxt;
    private EditText toDateEtxt;
    private Button search_button;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private int mYear, mMonth, mDay;


    public TransactionHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_transaction_history, container, false);
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        fromDateEtxt = (EditText)view.findViewById(R.id.edt_trans_hist_fromdate);
        // fromDateEtxt.setInputType(InputType.TYPE_NULL);
        toDateEtxt = (EditText)view. findViewById(R.id.edttrans_hist_todate);
        // toDateEtxt.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        setDateTimeField();
        fromDateEtxt.setText(dateFormatter.format(c.getTime()));
        toDateEtxt.setText(dateFormatter.format(c.getTime()));
        search_button= (Button)view.findViewById(R.id.btn_trans_hist_datewise_seach);



           search_button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String fromdate = fromDateEtxt.getText().toString();
                   String todate = toDateEtxt.getText().toString();

                   TransactionHistoryData ldf = new TransactionHistoryData ();
                   Bundle args = new Bundle();
                   args.putString("FROMDATE", fromdate);
                   args.putString("TODATE", todate);
                   ldf.setArguments(args);

//Inflate the fragment
                   getFragmentManager().beginTransaction().replace(R.id.frame_container, ldf).commit();
               }
           });

        return view;
    }
    @Override
    public void onClick(View v) {
        if(v == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if(v == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }
    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


}
