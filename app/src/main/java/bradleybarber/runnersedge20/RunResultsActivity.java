package bradleybarber.runnersedge20;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class RunResultsActivity extends ActionBarActivity {

    final static int milliPerMin = 60000;

    ArrayList<Integer> data;
    ArrayList<Integer> time;

    double runTimeMinutes;
    //double timeDouble;
    double sumImpact;
    double cadence;
    double avgImpact;
    int temp;
    int greatestImpact;
    double steps;
    int numImpact;
    int previousData;
    int currentData;

    TextView calcGreatestImpact;
    TextView calcAvgImpact;
    TextView calcCadence;

    Button graphButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_results);

        calcGreatestImpact = (TextView) findViewById(R.id.calcGreatestImpact);
        calcAvgImpact = (TextView) findViewById(R.id.calcAvgImpact);
        calcCadence = (TextView) findViewById(R.id.calcCadence);

        graphButton = (Button) findViewById(R.id.graphButton);

        data = getIntent().getExtras().getIntegerArrayList("data");
        time = getIntent().getExtras().getIntegerArrayList("time");

        avgImpact = 0;
        cadence = 0;
        greatestImpact = 0;
        steps = 0;
        sumImpact = 0;
        numImpact = 0;
        runTimeMinutes = 0;
        previousData = 0;
        currentData = 0;

        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphView();
            }
        });

        calcCadence();
        calcImpact();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run_results, menu);
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

    public void calcCadence(){
        runTimeMinutes = ((double)time.get(time.size()-1))/milliPerMin;
        for(int i = 0; i < data.size(); i++) {
            if (i > 0) {
                previousData = data.get(i-1);
                currentData = data.get(i);
                if (previousData == 0 && currentData > 0) {
                    steps++;
                }
            }
        }
        cadence = steps/runTimeMinutes;
        calcCadence.setText(Double.toString(cadence));
    }

    public void calcImpact(){
        for (int j = 0; j < data.size(); j ++){
            temp = data.get(j);
            if (temp > greatestImpact){
                greatestImpact = temp;
            }
            if (temp > 0){
                sumImpact = sumImpact + (double) temp;
                numImpact++;
            }
        }
        avgImpact = sumImpact/steps;

        calcGreatestImpact.setText(Integer.toString(greatestImpact));
        calcAvgImpact.setText(Double.toString(avgImpact));
    }

    public void graphView(){
        Intent graphView = new Intent(this, DataViewActivity.class);
        graphView.putIntegerArrayListExtra("data", data);
        graphView.putIntegerArrayListExtra("time", time);
        startActivity(graphView);
    }
}
