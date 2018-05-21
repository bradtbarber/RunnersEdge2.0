package bradleybarber.runnersedge20;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class StartActivity extends ActionBarActivity {

    ViewGroup rootContainer;
    Scene preStartScene;
    Scene startScene;
    Transition transitionMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity_scene_transition);

        rootContainer = (ViewGroup) findViewById(R.id.rootContainer);
        transitionMgr = TransitionInflater.from(this).inflateTransition(R.transition.transition);

        preStartScene = Scene.getSceneForLayout(rootContainer, R.layout.prestart_scene, this);
        startScene = Scene.getSceneForLayout(rootContainer, R.layout.activity_start, this);

        preStartScene.enter();

        TransitionManager.go(startScene, transitionMgr);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void userProfile(View view) {
        Intent userProfileActivity = new Intent(this, UserProfileActivity.class);
        startActivity(userProfileActivity);
    }
}
