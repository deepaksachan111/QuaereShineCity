package com.quaere.deepak.quaereshinecity.Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quaere.deepak.quaereshinecity.Db.DbHandler;
import com.quaere.deepak.quaereshinecity.R;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbHandler.startIfNotStarted(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView cardno = (TextView) view.findViewById(R.id.txt_pri_cardno);
        TextView displayname = (TextView) view.findViewById(R.id.txt_displayname);
        TextView totalamount = (TextView) view.findViewById(R.id.txt_totalamount);
        TextView redeemac = (TextView) view.findViewById(R.id.txt_redeemac);
        TextView balance = (TextView) view.findViewById(R.id.txt_balaance_card);


        String cardno2 = DbHandler.dbHandler.getcardProfileList().getCardno();
        String displayname2 = DbHandler.dbHandler.getcardProfileList().getDisplyname();
        String totalamount2 = DbHandler.dbHandler.getcardProfileList().getTotalamount();
        String redeemac2 = DbHandler.dbHandler.getcardProfileList().getRedeemaccount();
        String balance2 = DbHandler.dbHandler.getcardProfileList().getBalance();

        cardno.setText(cardno2);
        displayname.setText(displayname2);
        totalamount.setText(totalamount2);
        redeemac.setText(redeemac2);
        balance.setText(balance2);


        return view;
    }


}
