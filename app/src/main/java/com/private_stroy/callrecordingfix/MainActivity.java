package com.private_stroy.callrecordingfix;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private LinearLayout statusBox;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40,40,40,40);
        scroll.addView(root);

        TextView title = text("Восстановление записи звонков", 24);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title);
        root.addView(text("\nПроверка разрешений, которые могут сбрасываться после обновления HyperOS.\n",16));

        statusBox = new LinearLayout(this);
        statusBox.setOrientation(LinearLayout.VERTICAL);
        root.addView(statusBox);

        Button refresh = button("Проверить снова");
        refresh.setOnClickListener(v -> refresh());
        root.addView(refresh);

        Button calls = button("Разрешения приложения звонков");
        calls.setOnClickListener(v -> openApp("com.android.incallui"));
        root.addView(calls);

        Button recorder = button("Разрешения Диктофона Xiaomi");
        recorder.setOnClickListener(v -> openApp("com.android.soundrecorder"));
        root.addView(recorder);

        root.addView(text("\nЕсли есть красные пункты, откройте соответствующее приложение → «Разрешения» и разрешите нужные пункты. Для Диктофона Xiaomi особенно важен «Микрофон».\n\nПриложение ничего не меняет само: Android не позволяет обычному APK выдавать системным приложениям runtime-разрешения без root/системной подписи.",15));
        setContentView(scroll);
        refresh();
    }

    private TextView text(String s, int sp) {
        TextView v=new TextView(this); v.setText(s); v.setTextSize(sp); v.setPadding(8,10,8,10); return v;
    }
    private Button button(String s) {
        Button b=new Button(this); b.setText(s);
        b.setAllCaps(false);
        b.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return b;
    }
    private void line(String label,String pkg,String perm) {
        boolean ok=getPackageManager().checkPermission(perm,pkg)==PackageManager.PERMISSION_GRANTED;
        TextView v=text((ok?"✓ ":"✗ ")+label,17);
        statusBox.addView(v);
    }
    private void refresh() {
        statusBox.removeAllViews();
        line("Звонки: Телефон","com.android.incallui",Manifest.permission.READ_PHONE_STATE);
        line("Звонки: Микрофон","com.android.incallui",Manifest.permission.RECORD_AUDIO);
        line("Звонки: Журнал вызовов","com.android.incallui",Manifest.permission.READ_CALL_LOG);
        line("Звонки: Музыка и аудио","com.android.incallui",Manifest.permission.READ_MEDIA_AUDIO);
        line("Диктофон Xiaomi: Микрофон","com.android.soundrecorder",Manifest.permission.RECORD_AUDIO);
    }
    private void openApp(String pkg) {
        try {
            Intent i=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+pkg));
            startActivity(i);
        } catch(Exception e) {
            Intent i=new Intent(Settings.ACTION_SETTINGS); startActivity(i);
        }
    }
    @Override protected void onResume(){ super.onResume(); if(statusBox!=null) refresh(); }
}
