package ilya_fedin.ttlfixer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.util.Scanner;

import static java.lang.Runtime.getRuntime;

public class MainService extends Service {
    public MainService() {
    }

    void successNotification() {
        NewNotification newNotification = new NewNotification();
        newNotification.notify(getApplicationContext(), "result_notification", getApplicationContext().getResources().getString(R.string.ttl_success));
    }

    void errorNotification(String stdout, String stderr) {
        NewNotification newNotification = new NewNotification();
        newNotification.notify(getApplicationContext(), "result_notification", getApplicationContext().getResources().getString(R.string.ttl_error) + (stdout.length() > 0 || stderr.length() > 0 ? ":\n" : "") + (stdout.length() > 0 ? stdout : "") + (stdout.length() > 0 && stderr.length() > 0 ? "\n" : "") + (stderr.length() > 0 ? stderr : ""));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Process ttl_fix = null;
        try {
            ttl_fix = getRuntime().exec(new String[]{"su", "-c", "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64"});
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
        } catch (IOException e) {
            errorNotification(e.toString(), "");
        } catch (InterruptedException e) {
            errorNotification(e.toString(), "");
        }
        Intent stopServiceIntent = new Intent(getApplicationContext(), this.getClass());
        getApplicationContext().stopService(stopServiceIntent);
        System.exit(0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
