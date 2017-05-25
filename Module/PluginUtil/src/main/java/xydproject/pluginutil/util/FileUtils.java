package xydproject.pluginutil.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by xiaojun on 2017/5/19.
 */

public class FileUtils {

    /**
     * copy assets文件到SD卡
     *
     * @param fileName 文件名
     * @param context  上下文
     */
    public static boolean copyAssetsToSdcard(Context context, String fileName) {
        try {
            InputStream assetsInput = context.getAssets().open(fileName);
            String dest = context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName;
            if (copyFile(context, assetsInput, dest)) {
                return true;
            } else {
                assetsInput = context.getAssets().open(fileName);
                dest = context.getCacheDir().getAbsolutePath() + "/" + fileName;
                if (copyFile(context, assetsInput, dest)) {                    return true;
                } else {
                    Toast.makeText(context, "抽取assets中的Apk失败" + dest, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyFile(Context context, final InputStream inputStream, String dest) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dest.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                int permissionState = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionState != PackageManager.PERMISSION_GRANTED) {
                    //6.0的系统即使申请了读写sdcard的权限,仍然可以在设置中关闭, 则需要requestPermissons
                    Log.e("FileUtils", "6.0以上的系统, targetSDK>=23时, sdcard读写默认为未授权,需requestPermissons或者在设置中开启" + dest);
                    return false;
                }
            }
        }
        FileOutputStream oputStream = null;
        try {
            File destFile = new File(dest);
            File parentDir = destFile.getParentFile();
            if (!parentDir.isDirectory() || !parentDir.exists()) {
                destFile.getParentFile().mkdirs();
            }
            oputStream = new FileOutputStream(destFile);
            byte[] bb = new byte[48 * 1024];
            int len;
            while ((len = inputStream.read(bb)) != -1) {
                oputStream.write(bb, 0, len);
            }
            oputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oputStream != null) {
                try {
                    oputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
