package ilya_fedin.ttlfixer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;

import java.io.IOException;
import java.util.Scanner;

import static java.lang.Runtime.getRuntime;

public class MainService extends Service {
    Context context;
    Resources res;

    void successNotification() {
        NewNotification.notify(context, "result_notification", res.getString(R.string.ttl_success));
    }

    void errorNotification(String stdout, String stderr) {
        String notificationText = res.getString(R.string.ttl_error);
        if(stdout.length() > 0 || stderr.length() > 0) notificationText += ":";
        if(stdout.length() > 0) notificationText += "\n" + stdout;
        if(stderr.length() > 0) notificationText += "\n" + stderr;
        NewNotification.notify(context, "result_notification", notificationText);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        res = context.getResources();
        Process ttl_fix;
        try {
            ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64"});
            int ttl_fix_code = ttl_fix.waitFor();
            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(output.length() == 0 && error.length() == 0) {
                if (ttl_fix_code == 0) successNotification();
                else errorNotification(output, error);
            } else {
                errorNotification(output, error);
            }
        } catch (IOException | InterruptedException e) {
            errorNotification(e.toString(), "");
        }
        Intent stopServiceIntent = new Intent(context, this.getClass());
        context.stopService(stopServiceIntent);
        System.exit(0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
