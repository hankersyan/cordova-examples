/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.engine;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the WebChromeClient that implements callbacks for our web view.
 * The kind of callbacks that happen here are on the chrome outside the document,
 * such as onCreateWindow(), onConsoleMessage(), onProgressChanged(), etc. Related
 * to but different than CordovaWebViewClient.
 */
public class SystemWebChromeClient extends WebChromeClient {

    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String LOG_TAG = "SystemWebChromeClient";
    private long MAX_QUOTA = 100 * 1024 * 1024;
    protected final SystemWebViewEngine parentEngine;

    // the video progress view
    private View mVideoProgressView;

    private CordovaDialogsHelper dialogsHelper;
    private Context appContext;

    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mCustomView;

    public SystemWebChromeClient(SystemWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        appContext = parentEngine.webView.getContext();
        dialogsHelper = new CordovaDialogsHelper(appContext);

        requestPermissions();
    }

    /**
     * Tell the client to display a javascript alert dialog.
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        dialogsHelper.showAlert(message, new CordovaDialogsHelper.Result() {
            @Override public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    /**
     * Tell the client to display a confirm dialog to the user.
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        dialogsHelper.showConfirm(message, new CordovaDialogsHelper.Result() {
            @Override
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    /**
     * Tell the client to display a prompt dialog to the user.
     * If the client returns true, WebView will assume that the client will
     * handle the prompt dialog and call the appropriate JsPromptResult method.
     *
     * Since we are hacking prompts for our own purposes, we should not be using them for
     * this purpose, perhaps we should hack console.log to do this instead!
     */
    @Override
    public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
        // Unlike the @JavascriptInterface bridge, this method is always called on the UI thread.
        String handledRet = parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (handledRet != null) {
            result.confirm(handledRet);
        } else {
            dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() {
                @Override
                public void gotResult(boolean success, String value) {
                    if (success) {
                        result.confirm(value);
                    } else {
                        result.cancel();
                    }
                }
            });
        }
        return true;
    }

    /**
     * Handle database quota exceeded notification.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
                                        long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
    {
        LOG.d(LOG_TAG, "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", estimatedSize, currentQuota, totalUsedQuota);
        quotaUpdater.updateQuota(MAX_QUOTA);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage)
    {
        if (consoleMessage.message() != null)
            LOG.d(LOG_TAG, "%s: Line %d : %s" , consoleMessage.sourceId() , consoleMessage.lineNumber(), consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    /**
     * Instructs the client to show a prompt to ask the user to set the Geolocation permission state for the specified origin.
     *
     * This also checks for the Geolocation Plugin and requests permission from the application  to use Geolocation.
     *
     * @param origin
     * @param callback
     */
    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
        //Get the plugin, it should be loaded
        CordovaPlugin geolocation = parentEngine.pluginManager.getPlugin("Geolocation");
        if(geolocation != null && !geolocation.hasPermisssion())
        {
            geolocation.requestPermissions(0);
        }

    }

    // API level 7 is required for this, see if we could lower this using something else
    @Override
    @SuppressWarnings("deprecation")
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        parentEngine.getCordovaWebView().showCustomView(view, callback);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onHideCustomView() {
        parentEngine.getCordovaWebView().hideCustomView();
    }

    @Override
    /**
     * Ask the host application for a custom progress view to show while
     * a <video> is loading.
     * @return View The progress view.
     */
    public View getVideoLoadingProgressView() {

        if (mVideoProgressView == null) {
            // Create a new Loading view programmatically.

            // create the linear layout
            LinearLayout layout = new LinearLayout(parentEngine.getView().getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.setLayoutParams(layoutParams);
            // the proress bar
            ProgressBar bar = new ProgressBar(parentEngine.getView().getContext());
            LinearLayout.LayoutParams barLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            barLayoutParams.gravity = Gravity.CENTER;
            bar.setLayoutParams(barLayoutParams);
            layout.addView(bar);

            mVideoProgressView = layout;
        }
        return mVideoProgressView;
    }

    // <input type=file> support:
    // openFileChooser() is for pre KitKat and in KitKat mr1 (it's known broken in KitKat).
    // For Lollipop, we use onShowFileChooser().
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        this.openFileChooser(uploadMsg, "*/*");
    }

    public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType ) {
        this.openFileChooser(uploadMsg, acceptType, null);
    }

    public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                LOG.d(LOG_TAG, "Receive file chooser URL: " + result);
                uploadMsg.onReceiveValue(result);
            }
        }, intent, FILECHOOSER_RESULTCODE);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
        // Check if multiple-select is specified
        Boolean selectMultiple = false;
        if (fileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) {
            selectMultiple = true;
        }
        Intent fileIntent = fileChooserParams.createIntent();
        fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, selectMultiple);


        // Image from camera intent
        Uri tempUri = null;
        Intent captureIntent = null;
        if (true || fileChooserParams.isCaptureEnabled()) {
            captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Context context = parentEngine.getView().getContext();
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) &&
                    captureIntent.resolveActivity(context.getPackageManager()) != null) {
                try {
                    File tempFile = createTempFile(context);
                    LOG.d(LOG_TAG, "Temporary photo capture file: " + tempFile);
                    tempUri = createUriForFile(context, tempFile);
                    LOG.d(LOG_TAG, "Temporary photo capture URI: " + tempUri);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } catch (Exception e) {
                    LOG.e(LOG_TAG, "Unable to create temporary file for photo capture", e);
                    captureIntent = null;
                }
            } else {
                LOG.w(LOG_TAG, "Device does not support photo capture");
                captureIntent = null;
            }
        }
        final Uri captureUri = tempUri;

        // Chooser intent
        Intent chooserIntent = Intent.createChooser(fileIntent, null);
        if (captureIntent != null) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { captureIntent });
        }

        try {
            LOG.i(LOG_TAG, "Starting intent for file chooser");
            parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    // Handle result
                    Uri[] result = null;
                    if (resultCode == Activity.RESULT_OK) {
                        List<Uri> uris = new ArrayList<Uri>();
                        if (intent == null && captureUri != null) { // camera
                            LOG.v(LOG_TAG, "Adding camera capture: " + captureUri);
                            uris.add(captureUri);

                        } else if (intent.getClipData() != null) { // multiple files
                            ClipData clipData = intent.getClipData();
                            int count = clipData.getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri uri = clipData.getItemAt(i).getUri();
                                LOG.v(LOG_TAG, "Adding file (multiple): " + uri);
                                if (uri != null) {
                                    uris.add(uri);
                                }
                            }

                        } else if (intent.getData() != null) { // single file
                            LOG.v(LOG_TAG, "Adding file (single): " + intent.getData());
                            uris.add(intent.getData());
                        }

                        if (!uris.isEmpty()) {
                            LOG.d(LOG_TAG, "Receive file chooser URL: " + uris.toString());
                            result = uris.toArray(new Uri[uris.size()]);
                        }
                    }
                    filePathsCallback.onReceiveValue(result);
                }
            }, chooserIntent, FILECHOOSER_RESULTCODE);
        } catch (ActivityNotFoundException e) {
            LOG.w("No activity found to handle file chooser intent.", e);
            filePathsCallback.onReceiveValue(null);
        }
        return true;
    }

    private File createTempFile(Context context) {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        try {
            file.createNewFile();
        } catch (Exception e) {
            LOG.w("createTempFile", e);
        }
        return file;
    }

    private Uri createUriForFile(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void requestPermissions() {
        Application app = (Application) appContext.getApplicationContext();
        if (app.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) appContext).requestPermissions(new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                }, 1);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        LOG.d(LOG_TAG, "onPermissionRequest: " + Arrays.toString(request.getResources()));
        request.grant(request.getResources());
    }

    public void destroyLastDialog(){
        dialogsHelper.destroyLastDialog();
    }
}
