package com.athome.practice.PayIt.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;

import com.athome.practice.PayIt.Constants;
import com.athome.practice.PayIt.R;
import com.athome.practice.PayIt.activities.ScanReceiverActivity_2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Anand Singh on 19/11/2017.
 */
public class AmountDialog extends DialogFragment {

    public static final Pattern AMOUNT_REGEX = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_amount, null);
        final AppCompatEditText et = (AppCompatEditText) view.findViewById(R.id.payable_amount);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setTitle(Constants.ENTER_AMOUNT)
                .setCancelable(true)
                .setPositiveButton(Constants.PAY, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String amount = et.getText().toString();
                        if(isValidAmount(amount)) {
//                            Intent intent = new Intent(getActivity(), ScanReceiverActivity.class);
                            Intent intent = new Intent(getActivity(), ScanReceiverActivity_2.class);
                            intent.putExtra(Constants.AMOUNT, amount);
                            startActivity(intent);
                        } else {
                            et.setError(Constants.INVALID_AMOUNT_MSG);
                            return;
                        }
                    }
                })
                .setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        return;
                }
            });
        return builder.create();
    }

    private boolean isValidAmount(String amount) {
        Matcher matcher = AMOUNT_REGEX.matcher(amount);
        return matcher.find();
    }
}
