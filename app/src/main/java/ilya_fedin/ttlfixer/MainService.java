package ilya_fedin.ttlfixer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.Runtime.getRuntime;

public class MainService extends Service {
    private Context context;
    private Resources res;
    private static final String DEFAULT_TTL = "64";
    private String ttl = DEFAULT_TTL;

    private void successNotification() {
        NewNotification.notify(context, "result_notification", res.getString(R.string.ttl_success) + ": " + ttl);
    }

    private void errorNotification(String stdout, String stderr) {
        String notificationText = res.getString(R.string.ttl_error);
        if(!stdout.isEmpty() || !stderr.isEmpty()) notificationText += ":";
        if(!stdout.isEmpty()) notificationText += "\n" + stdout;
        if(!stderr.isEmpty()) notificationText += "\n" + stderr;
        NewNotification.notify(context, "result_notification", notificationText);
    }

    private void createChain() {
        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -N TTLFixer"});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(!(output.isEmpty() && error.isEmpty() && ttl_fix_code == 0)) {
                if(Integer.parseInt(ttl) != 0) errorNotification(output, error);
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            if(Integer.parseInt(ttl) != 0) errorNotification(e.toString(), "");
            System.exit(0);
        }

        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -A POSTROUTING -j TTLFixer"});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(!(output.isEmpty() && error.isEmpty() && ttl_fix_code == 0)) {
                if(Integer.parseInt(ttl) != 0) errorNotification(output, error);
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            if(Integer.parseInt(ttl) != 0) errorNotification(e.toString(), "");
            System.exit(0);
        }
    }

    private void flushChain() {
        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -F TTLFixer"});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(!(output.isEmpty() && error.isEmpty() && ttl_fix_code == 0)) {
                if(Integer.parseInt(ttl) != 0) errorNotification(output, error);
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            if(Integer.parseInt(ttl) != 0) errorNotification(e.toString(), "");
            System.exit(0);
        }
    }

    private void removeChain() {
        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -D POSTROUTING -j TTLFixer"});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(!(output.isEmpty() && error.isEmpty() && ttl_fix_code == 0)) {
                if(Integer.parseInt(ttl) != 0) errorNotification(output, error);
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            if(Integer.parseInt(ttl) != 0) errorNotification(e.toString(), "");
            System.exit(0);
        }

        flushChain();

        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -X TTLFixer"});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(!(output.isEmpty() && error.isEmpty() && ttl_fix_code == 0)) {
                if(Integer.parseInt(ttl) != 0) errorNotification(output, error);
                System.exit(0);
            }
        } catch (IOException | InterruptedException e) {
            if(Integer.parseInt(ttl) != 0) errorNotification(e.toString(), "");
            System.exit(0);
        }
    }

    private boolean isChainExists() {
        boolean exist = false;
        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -S"});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";

            if(!(error.isEmpty() && ttl_fix_code == 0)) {
                if(Integer.parseInt(ttl) != 0) errorNotification(output, error);
                System.exit(0);
            }

            exist = output.contains("-N TTLFixer");
        } catch (IOException | InterruptedException e) {
            if(Integer.parseInt(ttl) != 0) errorNotification(e.toString(), "");
            System.exit(0);
        }
        return exist;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        res = context.getResources();

        File sdcard = Environment.getExternalStorageDirectory();
        File ttl_file = new File(sdcard, ".fix_ttl");
        try {
            BufferedReader br = new BufferedReader(new FileReader(ttl_file));
            ttl = br.readLine();
            br.close();
        } catch (IOException e) {
            ttl = DEFAULT_TTL;
        }

        if(ttl == null || ttl.trim().isEmpty()) ttl = DEFAULT_TTL;
        ttl = ttl.trim();
        if(!ttl.matches("\\d+") || Integer.parseInt(ttl) < 0 || Integer.parseInt(ttl) > 255) {
            NewNotification.notify(context, "result_notification", res.getString(R.string.ttl_incorrect_value));
            System.exit(0);
        }

        if(!RootUtil.isDeviceRooted()) {
            if(Integer.parseInt(ttl) != 0) NewNotification.notify(context, "result_notification", res.getString(R.string.root_error));
            System.exit(0);
        }

        if (isChainExists()) {
            if(Integer.parseInt(ttl) == 0) {
                removeChain();
                System.exit(0);
            }
            else flushChain();
        } else {
            if(Integer.parseInt(ttl) == 0) System.exit(0);
            else createChain();
        }

        try {
            Process ttl_fix = getRuntime().exec(new String[] {"su", "-c", "iptables -t mangle -A TTLFixer -j TTL --ttl-set " + ttl});
            int ttl_fix_code = ttl_fix.waitFor();

            Scanner outputScanner = new Scanner(ttl_fix.getInputStream()).useDelimiter("\\A");
            Scanner errorScanner = new Scanner(ttl_fix.getErrorStream()).useDelimiter("\\A");
            String output = outputScanner.hasNext() ? outputScanner.next() : "";
            String error = errorScanner.hasNext() ? errorScanner.next() : "";
            if(output.isEmpty() && error.isEmpty() && ttl_fix_code == 0)
                successNotification();
            else
                errorNotification(output, error);
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
