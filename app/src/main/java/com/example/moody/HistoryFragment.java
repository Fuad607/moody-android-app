package com.example.moody;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moody.model.Result;
import com.example.moody.model.SurveyResponse;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment implements RvClickListener {

    PointsGraphSeries<DataPoint> xyValues;
    GraphView point_graph;
    SharedPreferences sharedPreferences;
    DBHelper DB;
    String USER_ID;
    DatePickerDialog picker;
    RecyclerView recyclerView;
    ArrayList name;
    JSONArray jsonArray;
    EditText searchText;
    private static String URL = "https://collectivemoodtracker.herokuapp.com/api/";

    private boolean isFromDateSet = false;
    private boolean isToDateSet = false;
    EditText fromDate, toDate;
    private MutableLiveData<SurveyResponse> responseLiveData = new MutableLiveData<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override

    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");

        return v;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        point_graph = v.findViewById(R.id.scatterPlot);
        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");
        DB = new DBHelper(getContext());

        //if no internet
        Cursor cursor_survey = DB.getSurvey();
        cursor_survey.moveToFirst();
        point_graph.getViewport().setScrollable(true);
        point_graph.getViewport().setScrollableY(true);
        point_graph.getViewport().setScalable(true);
        point_graph.getViewport().setScalableY(true);

        while (cursor_survey.isAfterLast() == false) {
            cursor_survey.moveToNext();
        }

        fromDate = v.findViewById(R.id.fromDate);
        fromDate.setOnClickListener(v1 -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        fromDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year1);
                        isFromDateSet = true;
                    }, year, month, day);
            picker.show();
        });

        toDate = v.findViewById(R.id.toDate);
        toDate.setOnClickListener(v1 -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        toDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year1);
                        isToDateSet = true;
                        fetchData(fromDate.getText().toString(), toDate.getText().toString(), "&quot;&quot;");
                    }, year, month, day);
            picker.show();
        });
        recyclerView = v.findViewById(R.id.user_name_history_recyclerview);

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");

        ArrayList<String> array_list = new ArrayList<String>();
        ArrayList<String> array_list_filtered = new ArrayList<String>();
        ArrayList<String> array_list_contacted_user_id = new ArrayList<String>();
        ArrayList<String> array_list_contacted_user_id_filtered = new ArrayList<String>();

        DB = new DBHelper(getContext());

        Cursor cursor_user_relationship = DB.getUserRelationshipData(USER_ID);
        cursor_user_relationship.moveToFirst();

        while (cursor_user_relationship.isAfterLast() == false) {
            array_list.add(cursor_user_relationship.getString(cursor_user_relationship.getColumnIndex("nickname")));
            array_list_contacted_user_id.add(cursor_user_relationship.getString(cursor_user_relationship.getColumnIndex("contacted_user_id")));
            cursor_user_relationship.moveToNext();
        }

        HelperAdapterUserList helperAdapter = new HelperAdapterUserList(getContext(), array_list, array_list_contacted_user_id);
        helperAdapter.setClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter);
        responseLiveData.observe(getViewLifecycleOwner(), this::drawGraph);

    }

    private void drawGraph(SurveyResponse response) {
        point_graph.removeAllSeries();
        //api call get all users which user have relationship

        // ----- burda arrayOfDataPoints e DataPointleri elave ele ozu,
        // datapointin x, y i ne olmalidi onu yaz,
        // mood data, relaxed data hansini gostermey isteyirsen onlari filterle add ele ancaq
        List<DataPoint> arrayOfDataPoints = new ArrayList<>();
        for (int i = 0; i < response.getResult().size(); i++) {
            for (int j = 0; j < response.getResult().get(i).getMoodData().size(); j++) {
                arrayOfDataPoints.add(new DataPoint(i, response.getResult().get(i).getMoodData().get(j)));
            }
        }
        // -----

        DataPoint[] arr = new DataPoint[arrayOfDataPoints.size()];
        for (int i = 0; i < arrayOfDataPoints.size(); i++) {
            arr[i] = arrayOfDataPoints.get(i);
        }
        PointsGraphSeries<DataPoint> point_series = new PointsGraphSeries<>(arr);

        point_graph.addSeries(point_series);
        // point_series.setShape(PointsGraphSeries.Shape.RECTANGLE);
        point_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                int index = arrayOfDataPoints.indexOf(dataPoint);
                if (index != -1) {
                    if (index % 2 == 0) {
                        paint.setStrokeWidth(10);
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawCircle(x, y, 40, paint);
                        canvas.drawText(String.valueOf(dataPoint.getY()), x, y, paint);
                    } else {
                        paint.setColor(Color.BLUE);
                        canvas.drawLine(x, y, x + 40, y, paint);
                        canvas.drawLine(x + 40, y, x + 40, y + 40, paint);
                        canvas.drawLine(x + 40, y + 40, x, y + 40, paint);
                        canvas.drawLine(x, y + 40, x, y, paint);
                    }
                }
            }
        });

        point_series.setColor(Color.GREEN);
        point_series.setSize(18);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(point_graph);


        staticLabelsFormatter.setHorizontalLabels(response.getLabelDate().toArray(new String[0]));

        point_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        point_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg = "X:" + dataPoint.getX() + "\nY:" + dataPoint.getX();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                System.out.println("asdadas");

            }
        });
    }

    private void fetchData(String fromDate, String toDate, String friends) {
        if (friends.isEmpty()) friends = "&quot;&quot;";
        if (isFromDateSet && isToDateSet) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + "survey/" + USER_ID + "/" + fromDate + "/" + toDate + "/" + friends,
                    response -> {
                        try {
                            SurveyResponse res = new SurveyResponse();
                            List<Result> resultList = new ArrayList<>();
                            List<String> labelDataList = new ArrayList<>();

                            JSONObject obj = new JSONObject(response);
                            JSONArray result = obj.getJSONArray("result");

                            for (int i = 0; i < result.length(); i++) {
                                Result r = new Result();
                                r.setId(result.getJSONObject(i).getInt("id"));
                                r.setNickname(result.getJSONObject(i).getString("nickname"));

                                List<Integer> moodDataList = new ArrayList<>();
                                List<Integer> relaxedDataList = new ArrayList<>();

                                JSONArray moodDataJsonArray = result.getJSONObject(i).getJSONArray("mood_data");
                                for (int j = 0; j < moodDataJsonArray.length(); j++) {
                                    moodDataList.add(moodDataJsonArray.getInt(j) + j);
                                }

                                JSONArray relaxedDataJsonArray = result.getJSONObject(i).getJSONArray("relaxed_data");
                                for (int j = 0; j < relaxedDataJsonArray.length(); j++) {
                                    relaxedDataList.add(relaxedDataJsonArray.getInt(j));
                                }

                                r.setMoodData(moodDataList);
                                r.setRelaxedData(relaxedDataList);
                                resultList.add(r);
                            }

                            res.setResult(resultList);

                            JSONArray label_date = obj.getJSONArray("label_date");
                            for (int i = 0; i < label_date.length(); i++) {
                                labelDataList.add(label_date.getString(i));
                            }

                            res.setLabelDate(labelDataList);
                            responseLiveData.postValue(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public void onClick(String ids) {
        if (isFromDateSet && isToDateSet)
            fetchData(fromDate.getText().toString(), toDate.getText().toString(), ids);
    }
}
