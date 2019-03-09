package com.example.onedayvoca.ScoreFragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class Score3Fragment extends Fragment {

    private ListView list3;
    private String myJSON3;
    private GraphView line_graph3;

    private SimpleAdapter adapter3;
    private JSONArray scores3 = null;
    private ArrayList<HashMap<String, String>> scoreList3;


    public Score3Fragment(){
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(Common.Server+"score.php?type=3"); //기본 테스트 type=1
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_score3, container, false);

        list3= (ListView)layout.findViewById(R.id.list3);
        line_graph3= (GraphView)layout.findViewById(R.id.line_graph3);
        scoreList3= new ArrayList<HashMap<String, String>>();

        return layout;
    }


    //기본 세팅
    //여기 안들어감
    protected void showList(final Context context) {
        try {

            JSONObject jsonObj3 = new JSONObject(myJSON3);
            scores3 = jsonObj3.getJSONArray("result3");

            Log.i("score3 result",myJSON3+"");

            DataPoint[] arr= new DataPoint[scores3.length()];
            String[] day= new String[scores3.length()];

            for (int i = 0; i < scores3.length(); i++) {
                JSONObject jsonObject3 = scores3.getJSONObject(i);
                String id= String.valueOf(jsonObject3.getInt("id"));
                String score = String.valueOf(jsonObject3.getInt("score"));
                String date = jsonObject3.getString("date");

                HashMap<String, String> scores3 = new HashMap<String, String>();
                scores3.put("id",id);
                scores3.put("score", score);
                scores3.put("date", date);
                scoreList3.add(scores3);

                DataPoint point= new DataPoint(Integer.parseInt(id),Integer.parseInt(score));
                arr[i]= point;

                day[i]=id;
            }

            LineGraphSeries<DataPoint> line_series= new LineGraphSeries<DataPoint>(arr);
            line_graph3.addSeries(line_series);

            line_graph3.getViewport().setScrollable(true);

            // set manual X bounds
            line_graph3.getViewport().setXAxisBoundsManual(true);
            line_graph3.getViewport().setMinX(1);
            line_graph3.getViewport().setMaxX(arr.length);

            // set manual Y bounds
            line_graph3.getViewport().setYAxisBoundsManual(true);
            line_graph3.getViewport().setMinY(0);
            line_graph3.getViewport().setMaxY(100);

            StaticLabelsFormatter staticLabelsFormatter= new StaticLabelsFormatter(line_graph3);
            staticLabelsFormatter.setVerticalLabels(new String[]{"0","10","20","30","40","50","60","70","80","90","100"});
            staticLabelsFormatter.setHorizontalLabels((day));
            line_graph3.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);




            line_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPointInterface) {
                    Toast.makeText(context, "Series:On Dat",Toast.LENGTH_SHORT).show();
                }
            });

            adapter3 = new SimpleAdapter(
                    context, scoreList3, R.layout.item_score,
                    new String[]{"id","score","date"}, new int[]{R.id.txtId, R.id.txtScore, R.id.txtDate}
            );
            list3.setAdapter(adapter3);


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
                Log.i("result3",result+"");
                myJSON3 = result;
                    showList(getContext());
                    Log.i("getContext3",getContext()+"");
//                    Toast.makeText(getActivity(),"성적표 결과가 없습니다",Toast.LENGTH_SHORT).show();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}