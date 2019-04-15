
package com.dfzn.lubanjs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.text.TextUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class RNLubanjsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private Callback notifyCB;

  public RNLubanjsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNLubanjs";
  }

  @ReactMethod
  public void Compress(ReadableMap options, Callback processCallback) {

    this.notifyCB = processCallback;

    final String filepath = options.getString("filepath");
    final String targetdir = options.getString("targetdir");
    // final String position = options.getString("position");

    File file = new File(filepath);

    final List<Uri> uris = new ArrayList<>();

    uris.add(Uri.parse(filepath));

    Luban.with(reactContext).load(uris).ignoreBy(100).setTargetDir(getPath()).filter(new CompressionPredicate() {
      @Override
      public boolean apply(String path) {
        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
      }
    }).setCompressListener(myCompressLisenser).launch();

  }

  private String getPath() {
    String path = Environment.getExternalStorageDirectory() + "/com.dfzn.lubanjs/image/";
    File file = new File(path);
    if (file.mkdirs()) {
      return path;
    }
    return path;
  }

  public OnCompressListener myCompressLisenser = new OnCompressListener() {
    @Override
    public void onStart() {
      // TODO 压缩开始前调用，可以在方法内启动 loading UI

      String result = "Start";
      String content = "";

      // processCallback.invoke(result, content);
    }

    @Override
    public void onSuccess(File file) {
      // TODO 压缩成功后调用，返回压缩后的图片文件

      String result = "OK";
      String content = file.getAbsolutePath();

      Uri  furi = Uri.fromFile(file);

      content= furi.toString();

      notifyCB.invoke(result, content);
    }

    @Override
    public void onError(Throwable e) {
      // TODO 当压缩过程出现问题时调用

      String result = "Failed";
      String content = e.getMessage();

      notifyCB.invoke(result, content);
    };


  };

}