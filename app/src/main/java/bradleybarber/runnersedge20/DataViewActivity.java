package bradleybarber.runnersedge20;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.*;

import java.util.ArrayList;

public class DataViewActivity extends AppCompatActivity {

    private static final String TAG = DataViewActivity.class.getSimpleName();
    static final int secPerMilli = 1000;

    GraphView dataView;
    ArrayList<Integer> data;
    ArrayList<Integer> time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        data = getIntent().getExtras().getIntegerArrayList("data");
        time = getIntent().getExtras().getIntegerArrayList("time");

        Log.d(TAG, "retrieving data");

        dataView  = (GraphView) findViewById(R.id.dataView);
        LineGraphSeries<DataPoint> dataSet = new LineGraphSeries<>();
        for(int i = 0; i < data.size(); i++) {
            dataSet.appendData(new DataPoint(time.get(i) / secPerMilli, data.get(i)), true, data.size());
        }

        dataView.addSeries(dataSet);
        Log.d(TAG, "adding points to graph");

        dataSet.setColor(Color.BLUE);

        dataView.setTitle("Force on Knee over Run Time");
        dataView.setTitleColor(Color.rgb(0, 190, 114));
        dataView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        dataView.getGridLabelRenderer().setHorizontalAxisTitle("Time (Seconds)");
        dataView.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(0, 190, 114));
        dataView.getGridLabelRenderer().setVerticalAxisTitle("Force (Newtons)");
        dataView.getGridLabelRenderer().setVerticalAxisTitleColor(Color.rgb(0, 190, 114));
        dataView.getGridLabelRenderer().setGridColor(Color.BLACK);
        dataView.getViewport().setBackgroundColor(Color.TRANSPARENT);
        dataView.setTitleTextSize(35);
        dataView.setBackgroundColor(Color.TRANSPARENT);
        dataView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        dataView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_view, menu);
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
}
