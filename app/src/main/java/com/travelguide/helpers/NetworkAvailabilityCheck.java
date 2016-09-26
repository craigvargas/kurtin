package com.travelguide.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * @author kprav
 *
 * History:
 *   10/18/2015     kprav       Initial Version
 */
public class NetworkAvailabilityCheck {

    // Check if network is available
    public static boolean networkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting())
            return true;
        // showNetworkUnavailableDialog(activity);
        return false;
    }

    // Show a dialog that network is not available
    private static void showNetworkUnavailableDialog(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("No Internet Access!");
        alertDialog.setMessage("Please check your connection and try again.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void showToast(Activity activity) {
        Toast.makeText(activity, "No Internet! Please check your connection.", Toast.LENGTH_SHORT).show();
    }

}

