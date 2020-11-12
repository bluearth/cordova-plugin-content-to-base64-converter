package id.co.reston.cordova.android;

import android.os.Environment;
import android.content.ContentResolver; 
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

import jxl.Workbook;
import jxl.Sheet;

public class ExcelImporter extends CordovaPlugin {

    private static final String LOG_TAG = "ExcelImporter";
    private static final String ACTION_IMPORT = "import";
    CallbackContext callback;

    @Override
    public boolean execute(String action, JSONArray params, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_IMPORT)) {
            Log.d(LOG_TAG, "Dapet file uri " + params.getString(0));
            try {
                loadWorkbook(params.getString(0));
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Succesfuly invoking native plugin");
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
                Log.d(LOG_TAG, "sendPluginResult OK");
                return true;    
            } catch (Exception e) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
                Log.d(LOG_TAG, "sendPluginResult ERROR");
                return false;    
            }
        }        
        return false;
    }

    private void loadWorkbook(String uriString) throws Exception {

        try {
            ContentResolver contentResolver = this.cordova.getActivity().getApplicationContext().getContentResolver();
            Uri uri = Uri.parse(uriString);
            InputStream in = null;
            if (uri.getScheme().equals("content")) {
                in = contentResolver.openInputStream(uri);
            } else if (uri.getScheme().equals("file")) {
                in = new FileInputStream(new File(uriString));
            } else {
                throw new Exception("URI scheme not recognized " + uri.getScheme());
            }
            Log.d(LOG_TAG, "Buka stream berhasil");
            Workbook wbk = Workbook.getWorkbook(in);
            Sheet [] wkss = wbk.getSheets();
            Log.d(LOG_TAG, "getSheets berhasil");
            for (int i = 0 ; i < wkss.length ; i++) {
                Log.d(LOG_TAG, "Sheet info " + wkss[i].getName());
                Log.d(LOG_TAG, "   isHidden() " + wkss[i].getSettings().isHidden());
                Log.d(LOG_TAG, "   isProtected() " + wkss[i].getSettings().isProtected());
            }
            wbk.close();
            in.close();
            Log.d(LOG_TAG, "Tutup stream berhasil");

        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Something bad happened." , e);
        }
    }

    private void listFileInfo(String uri) {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // final String n = System.getProperty("line.separator");
        // File f = new File(uri);
        // String s = "Info about uri " + uri + n +
        //     "exists() " + f.exists() + n + 
        //     "canRead() " + f.canRead() + n +  
        //     "getAbsolutePath() " + f.getAbsolutePath() + n +
        //     "getCanonicalPath() " + f.getCanonicalPath() + n +
        //     "getName() " + f.getName() + n +
        //     "isDirectory() " + f.isDirectory() + n;
        // Log.d(LOG_TAG, s);
        File [] children = downloadDir.listFiles();
        for (int i = 0; i < children.length ; i++) {
            Log.d(LOG_TAG, children[i].getAbsolutePath());
        }

    }

}
