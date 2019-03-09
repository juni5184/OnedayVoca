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

public class Score2Fragment extends Fragment {

    private ListView list2;
    private String myJSON2;
    private GraphView line_graph2;

    private SimpleAdapter adapter2;
    private JSONArray scores2 = null;
    private ArrayList<HashMap<String, String>> scoreList2;


    public Score2Fragment(){
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(Common.Server+"score.php?type=2"); //기본 테스트 type=1
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_score2, container, false);

        list2= (ListView)layout.findViewById(R.id.list2);
        line_graph2= (GraphView)layout.findViewById(R.id.line_graph2);
        scoreList2= new ArrayList<HashMap<String, String>>();

        return layout;
    }


    //기본 세팅
    //여기 안들어감
    protected void showList(final Context context) {
        try {

            JSONObject jsonObj2 = new JSONObject(myJSON2);
            scores2 = jsonObj2.getJSONArray("result2");

            Log.i("score2 result",myJSON2+"");

            DataPoint[] arr= new DataPoint[scores2.length()];
            String[] day= new String[scores2.length()];

            for (int i = 0; i < scores2.length(); i++) {
                JSONObject jsonObject2 = scores2.getJSONObject(i);
                String id= String.valueOf(jsonObject2.getInt("id"));
                String score = String.valueOf(jsonObject2.getInt("score"));
                String date = jsonObject2.getString("date");

                HashMap<String, String> scores2 = new HashMap<String, String>();
                scores2.put("id",id);
                scores2.put("score", score);
                scores2.put("date", date);
                scoreList2.add(scores2);

                DataPoint point= new DataPoint(Integer.parseInt(id),Integer.parseInt(score));
                arr[i]= point;

                day[i]=id;
            }

            LineGraphSeries<DataPoint> line_series= new LineGraphSeries<DataPoint>(arr);
            line_graph2.addSeries(line_series);

            line_graph2.getViewport().setScrollable(true);

            // set manual X bounds
            line_graph2.getViewport().setXAxisBoundsManual(true);
            line_graph2.getViewport().setMinX(1);
            line_graph2.getViewport().setMaxX(arr.length);

            // set manual Y bounds
            line_graph2.getViewport().setYAxisBoundsManual(true);
            line_graph2.getViewport().setMinY(0);
            line_graph2.getViewport().setMaxY(100);

            StaticLabelsFormatter staticLabelsFormatter= new StaticLabelsFormatter(line_graph2);
            staticLabelsFormatter.setVerticalLabels(new String[]{"0","10","20","30","40","50","60","70","80","90","100"});
            staticLabelsFormatter.setHorizontalLabels((day));
            line_graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);




            line_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPointInterface) {
                    Toast.makeText(context, "Series:On Dat",Toast.LENGTH_SHORT).show();
                }
            });

            adapter2 = new SimpleAdapter(
                    context, scoreList2, R.layout.item_score,
                    new String[]{"id","score","date"}, new int[]{R.id.txtId, R.id.txtScore, R.id.txtDate}
            );
            list2.setAdapter(adapter2);


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
                Log.i("result2",result+"");
                myJSON2 = result;
                    showList(getContext());
                    Log.i("getContext2",getContext()+"");
//                    Toast.makeText(getActivity(),"성적표 결과가 없습니다",Toast.LENGTH_SHORT).show();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}