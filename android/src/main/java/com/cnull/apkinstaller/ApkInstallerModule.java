package com.cnull.apkinstaller;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import java.io.File;

public class ApkInstallerModule extends ReactContextBaseJavaModule {
  private ReactApplicationContext _context = null;

  // private static final String DURATION_SHORT_KEY = "SHORT";
  // private static final String DURATION_LONG_KEY = "LONG";

  public ApkInstallerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _context = reactContext;
  }

  @Override
  public String getName() {
    return "ApkInstaller";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    // constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    // constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  @ReactMethod
  // public void show(String message, int duration) {
  public void test(String message) {
    Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
  }

  @ReactMethod
  public void install(String path) {
      String cmd = "chmod 777 " +path;
      try {
          Runtime.getRuntime().exec(cmd);
      } catch (Exception e) {
          e.printStackTrace();
      }
//      Uri apkURI;
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//          apkURI = ApkInstallerFileProvider.getUriForFile(_context,
//                  _context.getPackageName() + ".provider",
//            new File(path));
//      } else {
//          apkURI = Uri.fromFile(new File(path));
//      }
//      Intent intent = new Intent(Intent.ACTION_VIEW);
//      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
//      _context.startActivity(intent);
      if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
          File file= new File(path);
          Uri apkUri = ApkInstallerFileProvider.getUriForFile(_context, _context.getPackageName() + ".provider", file);//在AndroidManifest中的android:authorities值
          if (BuildConfig.DEBUG) {
              Log.w(ApkInstallerModule.class.getSimpleName(), "packageName:" + _context.getPackageName() + " apkUri:"+apkUri.getPath() + " path:"+path);
          }
          Intent install = new Intent(Intent.ACTION_VIEW);
          install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
          install.setDataAndType(apkUri, "application/vnd.android.package-archive");
          _context.startActivity(install);
      } else{
          Intent install = new Intent(Intent.ACTION_VIEW);
          install.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
          install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          _context.startActivity(install);
      }
  }
}
