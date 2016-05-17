package com.apptec.dtvchannelsearch;

/**
 * Created by Rodrigo on 09/04/2016.
 */

//Bibliotecas
/*Bibliotecas Android*/
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.os.RemoteException;

/*Bibliotecas cordova*/
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.apache.commons.lang3.ArrayUtils;

/*Bibliotecas Java*/
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.Exception.*;
import java.lang.*;//Thread.*;

/*Bibliotecas SDMV - AIDL - low level*/
import com.sdmc.aidl.ProgramParcel;
import com.sdmc.aidl.SearchParcel;

/*### Bibliotecas SDMV - ACPI - high level ###*/
import com.sdmc.dtv.acpi.DTVACPIManager;
import com.sdmc.dtv.acpi.ProgramInfo;
import com.sdmc.dtv.acpi.ProgramSearch;
import com.sdmc.dtv.acpi.TunerInfo;
import com.sdmc.dtv.acpi.NoPermissionsException;
//import com.sdmc.dtv.acpi.QuickIntegration;

/*### Fim Bibliotecas SDMV - ACPI - high level ###*/

public class searchListener extends DTVChannelSearch implements ProgramSearch.ProgramSearchListener{

    public void onBeginOneFreq (SearchParcel spSearchParcel){
        spReturn(spSearchParcel);
        PluginResult prResultListenerPS = new PluginResult(PluginResult.Status.NO_RESULT);
        CallbackContext callbackContextX = new CallbackContext("customID01", webView);
        try {
            prResultListenerPS = new PluginResult(PluginResult.Status.OK,
                    "<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
                            "<br>Retorno callback (1) ProgramSearchListener onBeginOneFreq --> " +
                            "Parcel: " + spSearchParcel);
            prResultListenerPS.setKeepCallback(true);
            callbackContextX.sendPluginResult(prResultListenerPS);
        } catch (Exception e) {
            prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,
                    "<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
                            "<br>Retorno callback ERROR (1) ProgramSearchListener onBeginOneFreq --> " +
                            "Exception: " + e);
            prResultListenerPS.setKeepCallback(true);
            callbackContextX.sendPluginResult(prResultListenerPS);
        }
    }


    public void onEndOneFreq(int iCurrFrequency, int iFrequencyCount, ProgramParcel[] ppaProgramParcel) {
        PluginResult prResultListenerPS = new PluginResult(PluginResult.Status.NO_RESULT);
        CallbackContext callbackContextY = new CallbackContext("customID02", webView);
        try {
            prResultListenerPS = new PluginResult(PluginResult.Status.OK,
                    "<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
                            "<br>Retorno callback (2) ProgramSearchListener onBeginOneFreq --> " +
                            "currentFreqIndex: " + iCurrFrequency + " - " +
                            "FreqCount: " + iFrequencyCount + " - " +
                            "parcels: " + ppaProgramParcel);
            prResultListenerPS.setKeepCallback(true);
            callbackContextY.sendPluginResult(prResultListenerPS);

        } catch (Exception e) {
            prResultListenerPS = new PluginResult(PluginResult.Status.ERROR,
                    "<br>##########PROGRAM SEARCH LISTENER#########<BR>" +
                            "Retorno callback ERROR (1) ProgramSearchListener onBeginOneFreq --> " +
                            "Exception: " + e);
            prResultListenerPS.setKeepCallback(true);
            callbackContextY.sendPluginResult(prResultListenerPS);
        }
    }
    public SearchParcel spReturn (SearchParcel spSearchParcelReturn){
        return spSearchParcelReturn;
    }
}
