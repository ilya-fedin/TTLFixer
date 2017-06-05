package ilya_fedin.ttlfixer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startServiceIntent = new Intent(getApplicationContext(), MainService.class);
        getApplicationContext().startService(startServiceIntent);
        finish();
    }
}
