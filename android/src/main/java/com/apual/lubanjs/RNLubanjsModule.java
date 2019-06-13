
package com.apual.lubanjs;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.zero.smallvideorecord.LocalMediaCompress;
import com.zero.smallvideorecord.model.AutoVBRMode;
import com.zero.smallvideorecord.model.LocalMediaConfig;
import com.zero.smallvideorecord.model.OnlyCompressOverBean;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

  private int nfileCount;

  private String eventName;

  private WritableArray nResultList;

  public RNLubanjsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    nfileCount = 0;
    eventName = "lubanjs-event";

  }

  @Override
  public String getName() {
    return "RNLubanjs";
  }

  public String Uri2FilePath(String struri) {

    String result = struri;
    int index = struri.indexOf(new String("file:///"));

    if (index >= 0) {

      return result;
    }

    // int index1 = struri.indexOf(new String("content://"));

    if (struri.indexOf(new String("content://")) >= 0) {

      result = GetPathFromUri4kitkat.getPath(this.reactContext, Uri.parse(struri));

      result = "file://" + result;

    }

    return result;
  }

  /**
   *
   * @param eventName 事件的名称
   * @param obj       对应的Value
   */
  public void sendEventToJs(String eventName, Object obj) {
    getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(this.eventName,
        obj);
  }

  @ReactMethod
  public void Compress(ReadableMap options, Callback processCallback) {

    this.notifyCB = processCallback;

    final String fileuri = options.getString("filepath");
    final String targetdir = options.getString("targetdir");
    // final String position = options.getString("position");

    final String filepath = this.Uri2FilePath((fileuri));

    final List<Uri> uris = new ArrayList<>();

    uris.add(Uri.parse(filepath));

    Luban.with(reactContext).load(uris).ignoreBy(100).setTargetDir(getPath(targetdir))
        .filter(new CompressionPredicate() {
          @Override
          public boolean apply(String path) {
            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
          }
        }).setCompressListener(myCompressLisenser).launch();

  }

  @ReactMethod
  public void RemoveFile(ReadableMap options) {

    try {

      final String targetdir = options.getString("targetdir");

      final String imagedir = this.getPath(targetdir);
      
      File filedir = new File(imagedir);

      deleteDirWihtFile(filedir);
      
    } catch (Exception e) {
      //TODO: handle exception
    }

  }
  @ReactMethod
  public void CompressVideo(ReadableMap options) {

    try {

      final String eName = options.getString("event");
      final String targetdir = options.getString("targetdir");
      final String videopath = options.getString("videopath");
      this.eventName = eName;
      new Thread(new Runnable() {
        @Override
        public void run() {
          //视频压缩
          LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
          final LocalMediaConfig config = buidler
                  .setVideoPath(videopath)
                  .captureThumbnailsTime(1)
                  .doH264Compress(new AutoVBRMode())
                  .setFramerate(15)
                  .build();
          OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();

          if(onlyCompressOverBean.isSucceed()){

            String video_result = onlyCompressOverBean.getVideoPath();

            String image_result = onlyCompressOverBean.getVideoPath();

            System.out.println("压缩视频地址:"+onlyCompressOverBean.getVideoPath());
            System.out.println("压缩视频预览图:"+onlyCompressOverBean.getPicPath());

            WritableMap msg = Arguments.createMap();
            msg.putString("status", "finished");
            msg.putString("video", video_result);
            msg.putString("image", image_result);
            sendEventToJs(eName, msg);

          }
          else{

            WritableMap msg = Arguments.createMap();
            msg.putString("status", "failed");
            msg.putString("video", "failed");
            msg.putString("image", "failed");
            sendEventToJs(eName, msg);
          }




          //TODO 执行上传操作
        }
      }).start();

    } catch (Exception e) {
      //TODO: handle exception
      WritableMap msg = Arguments.createMap();
      msg.putString("status", "failed");
      msg.putString("state", "CompressVieo");
      msg.putString("content", e.getMessage());
      sendEventToJs("lubanjs-event", msg);
    }

  }

  @ReactMethod
  public void CompressWithNotify(ReadableMap options) {

    try {

      final String eName = options.getString("event");
      final String targetdir = options.getString("targetdir");
      final ReadableArray filelist = options.getArray("filelist");
      nfileCount = filelist.size();

      this.nResultList = Arguments.createArray();
      this.eventName = eName;

      final String imagedir = this.getPath(targetdir);
      
      // File filedir = new File(imagedir);

      // deleteDirWihtFile(filedir);

      final List<Uri> uris = new ArrayList<>();

      // WritableArray notifystring= Arguments.createArray();

      for (int index = 0; index < filelist.size(); index++) {

        String filename = filelist.getString(index);
        final String filepath = this.Uri2FilePath(filename);
        uris.add(Uri.parse(filepath));
      }

      Luban.with(reactContext).load(uris).ignoreBy(100).setTargetDir(this.getPath(targetdir))
          .filter(new CompressionPredicate() {
            @Override
            public boolean apply(String path) {
              return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
            }
          }).setCompressListener(myPromistLisenser).launch();

    } catch (Exception e) {

      WritableMap msg = Arguments.createMap();
      msg.putString("status", "failed");
      msg.putString("state", "CompressWithNotify");
      msg.putString("content", e.getMessage());
      sendEventToJs("lubanjs-event", msg);
    }
  }

  public static void deleteDirWihtFile(File dir) {

    try {
      if (dir == null || !dir.exists() || !dir.isDirectory())
        return;
      for (File file : dir.listFiles()) {
        if (file.isFile())
          file.delete(); // 删除所有文件
        else if (file.isDirectory())
          deleteDirWihtFile(file); // 递规的方式删除文件夹
      }

    } catch (Exception e) {
      // TODO: handle exception
    }

    // dir.delete();// 删除目录本身
  }

  private String getPath(String filepath) {

    if (filepath == "") {
      filepath = "/com.apual.lubanjs/image/";
    }
    String path = Environment.getExternalStorageDirectory() + filepath;
    File file = new File(path);
    if (file.mkdirs()) {
      return path;
    }
    return path;
  }

  public void OnProcessSucessed(File file) {

    try {

      String result = "OK";
      String content = file.getAbsolutePath();
      String filepath = Uri2FilePath(content);
      Uri furi = Uri.fromFile(file);

      String fieluri = furi.toString();

      Integer fc = nfileCount;

      WritableMap msg = Arguments.createMap();
      msg.putString("status", "finished");
      msg.putString("uri", fieluri);
      msg.putString("count", fc.toString());
      msg.putString("fieluri", filepath);
      sendEventToJs("lubanjs-event", msg);

    } catch (Exception e) {
      // TODO: handle exception

      WritableMap msg = Arguments.createMap();
      msg.putString("status", "failed");
      msg.putString("state", "OnProcessSucessed");
      msg.putString("content", e.getMessage());
      sendEventToJs("lubanjs-event", msg);
    }
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

      Uri furi = Uri.fromFile(file);

      content = furi.toString();

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

  public OnCompressListener myPromistLisenser = new OnCompressListener() {
    @Override
    public void onStart() {

      WritableMap msg = Arguments.createMap();
      msg.putString("status", "begin");
      sendEventToJs("lubanjs-event", msg);

    }

    @Override
    public void onSuccess(File file) {

      OnProcessSucessed(file);
    }

    @Override
    public void onError(Throwable e) {

      String result = "failed";
      String content = e.getMessage();

      WritableMap msg = Arguments.createMap();
      msg.putString("status", "failed");
      msg.putString("content", content);
      sendEventToJs("lubanjs-event", msg);

    }
  };

}