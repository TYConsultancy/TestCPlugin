package com.cordovatest.plugins;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.System.out;

/**
 * This class echoes a string called from JavaScript.
 */
public class TestCPlugin extends CordovaPlugin {
    private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 0;
    private final int PICK_FROM_FILE = 1;
    private final int PICK_FROM_CAMERA = 2;
    private final int CROP_FROM_FILE = 3;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    protected final static String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE };

    private int imageWidth;
    private int imageHeight;

    private DisplayMetrics dm;

    private Uri uri = null;
    private Bitmap bitmap = null;
    CallbackContext callbackContext = null;
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        cordova.setActivityResultCallback(this);
        this.callbackContext = callbackContext;


            //String name = data.getString(0);
            //String message = "Hello, " + name;



            //callbackContext.success(message);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (action.equals("gallery")) {
                        checkPermission(PICK_FROM_FILE);
                    }else {
                        checkPermission(PICK_FROM_CAMERA);
                    }
                }
            });

            return true;

        } else {

            return false;

        }
    }
    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.cordova.startActivityForResult(this,intent, PICK_FROM_FILE);

    }
    public void performFileSearch2() {
        Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }
    //@Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        //StorageManager imm = (StorageManager)getSystemService(Context.STORAGE_SERVICE);
        out.println("*********************************************am i called****"+resultCode+"***req***"+requestCode);
        if (resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

            switch (requestCode) {
                case PICK_FROM_FILE:
                    uri = resultData.getData();
                    //Allow cropping for the selected image.
                    doCrop();
                    break;

                case CROP_FROM_FILE:

                    Bundle extras = resultData.getExtras();

                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        //Toast.makeText(this.getApplicationContext(),"Looks like we got the image",Toast.LENGTH_LONG).show();
                        out.println("*************************************************************Looks like we got the image****");

                        //callbackContext.success("success");
                    }

                    String filePath= Environment.getExternalStorageDirectory() + "/"+TEMP_PHOTO_FILE;
                    bitmap =  BitmapFactory.decodeFile(filePath);


                    File f = new File(filePath);
                    if (f.exists())
                        // f.delete();
                        if(bitmap!=null){
                            callbackContext.success(filePath);
                            out.println("***********************************************Looks like image is saved****");
                            //Toast.makeText(this.getApplicationContext(),"Looks like image is saved",Toast.LENGTH_LONG).show();


                        }else{
                            //callbackContext.error();
                            out.println("***********************************************Looks like image not saved****");
                            // Toast.makeText(this.getApplicationContext(),"Looks like image not saved",Toast.LENGTH_LONG).show();
                        }
                    break;
            }
        }
    }
    private void doCrop(){
        out.println("******************************************************"+uri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());

        //intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) this.cordova.startActivityForResult(this,intent, CROP_FROM_FILE);



    }
    private Uri getTempUri() {
        if(getTempFile() != null)
            return Uri.fromFile(getTempFile());
        else
            return null;
    }

    private File getTempFile() {
        if (isSDCARDMounted()) {
            File f = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
            try {
                f.createNewFile();
            } catch (IOException e) {
                return null;
            }
            return f;
        } else {
            return null;
        }
    }

    private boolean isSDCARDMounted(){
        String status = Environment.getExternalStorageState();
        return (status.equals(Environment.MEDIA_MOUNTED));
    }
    public void checkPermission(int intVal) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
           /* if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }else{
                performFileSearch();
            }*/
            boolean saveAlbumPermission = PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(!saveAlbumPermission){
                PermissionHelper.requestPermission(this, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
            }else{
                if(intVal==PICK_FROM_FILE)
                    performFileSearch();
                else
                    performFileSearch2();
            }
        }else{
            if(intVal==PICK_FROM_FILE)
                performFileSearch();
            else
                performFileSearch2();
        }
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        out.println("***************requestcode***********"+requestCode);
        switch (requestCode) {
            case(MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE):

                performFileSearch();
                break;
            default:
                PermissionHelper.requestPermission(this, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
                //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
