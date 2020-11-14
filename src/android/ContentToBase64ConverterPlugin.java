package id.co.reston.cordova.plugins;

import android.app.Activity;
import android.content.Intent;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import android.util.Base64;
import android.os.ParcelFileDescriptor;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// import org.apache.commons.codec.*;
// import org.apache.commons.codec.binary.*;
// import org.apache.commons.codec.digest.*;

import java.io.*;
import java.util.*;

public class ContentToBase64ConverterPlugin extends CordovaPlugin {

    private static final String TAG = "ContentToBase64Converter";
    private static final String ACTION_CONVERT = "convert";
    private static final int PICK_FILE_REQUEST = 1;
    private static final int MAX_CONTENT_SIZE = 20000000; // maximum content size allowed in bytes

    public static final String MIME = "mime";

    CallbackContext callback;

    @Override
    public boolean execute(String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_CONVERT)) {
            JSONObject filters = inputs.optJSONObject(0);
            chooseContent(filters, callbackContext);
            return true;
        }
        return false;
    }

    public void chooseFile(JSONObject filter, CallbackContext callbackContext) {
        String uri_filter = filter.has(MIME) ? filter.optString(MIME) : "*/*";

        // type and title should be configurable
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(uri_filter);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && callback != null) {

            if (resultCode == Activity.RESULT_OK) {

                Uri uri = data.getData();

                if (uri != null) {
                    try {
                        ContentResolver contentResolver = this.cordova
                            .getActivity()
                            .getApplicationContext()
                            .getContentResolver();
                        ParcelFileDescriptor pd = contentResolver.openFileDescriptor(uri, "r");
                        if (pd.getStatSize() > MAX_CONTENT_SIZE)
                            throw new Exception("Content size too large. Maximum size is " + MAX_CONTENT_SIZE + " bytes");
                        
                        Log.w(TAG, "Uri: "+ uri.toString());
                        Log.w(TAG, "Content size: " + pd.getStatSize());
                        byte[] raw = new byte[(int) pd.getStatSize()];
                        InputStream fis = contentResolver.openInputStream(uri);
                        fis.read(raw, 0, raw.length);
                        // String b64EncodedStr = Base64.encodeBase64String(raw); //ini yg di return
                        String b64EncodedStr = Base64.encodeToString(raw, Base64.DEFAULT);

                        // Verfiry import result by comparing digest
                        // 
                        // byte [] b64DecodedBytes = Base64.decode(b64EncodedStr, Base64.DEFAULT);
                        // DigestUtils digester = new DigestUtils(MessageDigestAlgorithms.MD5);
                        // byte [] dig1 = digester.digest(raw);
                        // byte [] dig2 = digester.digest(b64DecodedBytes);
                        // Log.w(TAG, "Hasi digest sama? " + Arrays.equals(dig1, dig2));
                        // Log.w(TAG, "-----------------------------");
                        callback.success(b64EncodedStr);                        
                    } catch(Exception e) {
                        //e.printStackTrace();
                        callback.error(e.getMessage());
                    }
                } else {
                    callback.error("File uri was empty");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // keep this string the same as in iOS document picker plugin
                // https://github.com/iampossible/Cordova-DocPicker
                callback.error("User canceled.");
            } else {
                callback.error(resultCode);
            }
        }
    }
}
