package ilya_fedin.ttlfixer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        Intent startServiceIntent = new Intent(context, MainService.class);
        context.startService(startServiceIntent);
        finish();
    }
}
