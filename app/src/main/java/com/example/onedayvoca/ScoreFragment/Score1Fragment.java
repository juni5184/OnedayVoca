package com.example.onedayvoca.ScoreFragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.onedayvoca.Common;
import com.example.onedayvoca.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Score1Fragment extends Fragment {

    private ListView list1;
    private String myJSON;
    private GraphView line_graph;

    private SimpleAdapter adapter;
    private JSONArray scores = null;
    private ArrayList<HashMap<String, String>> scoreList;


    public Score1Fragment(){
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(Common.Server+"score.php?type=1"); //기본 테스트 type=1
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_score1, container, false);

        list1= (ListView)layout.findViewById(R.id.list1);
        line_graph= (GraphView)layout.findViewById(R.id.line_graph);
        scoreList= new ArrayList<HashMap<String, String>>();


        return layout;
    }


    //기본 세팅
    protected void showList(final Context context) {
        try {

            JSONObject jsonObj = new JSONObject(myJSON);
            scores = jsonObj.getJSONArray("result1");

            Log.i("score1 result",myJSON+"");

            DataPoint[] arr= new DataPoint[scores.length()];

            String[] day= new String[scores.length()];

            for (int i = 0; i < scores.length(); i++) {
                JSONObject jsonObject = scores.getJSONObject(i);
                String id= String.valueOf(jsonObject.getInt("id"));
                String score = String.valueOf(jsonObject.getInt("score"));
                String date = jsonObject.getString("date");

                HashMap<String, String> scores = new HashMap<String, String>();
                scores.put("id",id);
                scores.put("score", score);
                scores.put("date", date);
                scoreList.add(scores);

                DataPoint point= new DataPoint(Integer.parseInt(id),Integer.parseInt(score));
                arr[i]= point;

                day[i]=id;
            }

            LineGraphSeries<DataPoint> line_series= new LineGraphSeries<DataPoint>(arr);
            line_graph.addSeries(line_series);

            line_graph.getViewport().setScrollable(true);

            // set manual X bounds
            line_graph.getViewport().setXAxisBoundsManual(true);
            line_graph.getViewport().setMinX(1);
            line_graph.getViewport().setMaxX(arr.length);

            // set manual Y bounds
            line_graph.getViewport().setYAxisBoundsManual(true);
            line_graph.getViewport().setMinY(0);
            line_graph.getViewport().setMaxY(100);

            StaticLabelsFormatter staticLabelsFormatter= new StaticLabelsFormatter(line_graph);
            staticLabelsFormatter.setVerticalLabels(new String[]{"0","10","20","30","40","50","60","70","80","90","100"});
            staticLabelsFormatter.setHorizontalLabels((day));
            line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);




            line_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPointInterface) {
                    Toast.makeText(context, "Series:On Dat",Toast.LENGTH_SHORT).show();
                }
            });

            adapter = new SimpleAdapter(
                    context, scoreList, R.layout.item_score,
                    new String[]{"id","score","date"}, new int[]{R.id.txtId, R.id.txtScore, R.id.txtDate}
            );
            list1.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Log.i("result1",result+"");
                myJSON = result;
                showList(getContext());
                Log.i("getContext1",getContext()+"");

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}