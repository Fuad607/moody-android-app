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

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment implements RvClickListener {

    PointsGraphSeries<DataPoint> xyValues;
    GraphView point_graph;
    SharedPreferences sharedPreferences;
    DBHelper DB;
    String USER_ID;
    DatePickerDialog picker;
    RecyclerView recyclerView;
    CheckBox checkbox_mood;
    CheckBox checkbox_relax;
    ArrayList name;
    JSONArray jsonArray;
    EditText searchText;
    private static String URL = "https://collectivemoodtracker.herokuapp.com/api/";

    ArrayList<String> array_list_contacted_user_id = new ArrayList<String>();

    private boolean isFromDateSet = false;
    private boolean isToDateSet = false;
    EditText fromDate, toDate;
    private final MutableLiveData<SurveyResponse> responseLiveData = new MutableLiveData<>();

    private HashMap<DataPoint, Integer> moodDataMap = new HashMap<>();
    private HashMap<DataPoint, Integer> relaxedDataMap = new HashMap<>();
    private List<Integer> colorList = new ArrayList<>(
            Arrays.asList(
                    Color.GREEN,
                    Color.BLUE,
                    Color.RED,
                    Color.MAGENTA,
                    Color.YELLOW,
                    Color.CYAN,
                    Color.GRAY
            )
    );
    private HashMap<Integer, Integer> colors = new HashMap<>();

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        point_graph = v.findViewById(R.id.scatterPlot);
        checkbox_mood = v.findViewById(R.id.checkbox_select_mood);
        checkbox_relax = v.findViewById(R.id.checkbox_select_relax);
        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");
        DB = new DBHelper(getContext());

        point_graph.getViewport().setScrollable(true);
        point_graph.getViewport().setScrollableY(true);
        point_graph.getViewport().setScalable(true);
        point_graph.getViewport().setScalableY(true);

        LocalDate current = LocalDate.now();
        LocalDate startdate = current.minusDays(6);

        fromDate = v.findViewById(R.id.fromDate);
        toDate = v.findViewById(R.id.toDate);

        fromDate.setText(startdate.getDayOfMonth() + "." + startdate.getMonthValue() + "." + startdate.getYear());
        isFromDateSet = true;

        toDate.setText(current.getDayOfMonth() + "." + current.getMonthValue() + "." + current.getYear());
        isToDateSet = true;

        fetchData(fromDate.getText().toString(), toDate.getText().toString(), "&quot;&quot;");

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
                        toDate.setText("");
                    }, year, month, day);
            picker.updateDate(year, month, day);
            picker.show();
        });

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
            picker.updateDate(year, month, day);
            picker.show();
        });

        checkbox_mood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFromDateSet = true;
                isToDateSet = true;

                fetchData(fromDate.getText().toString(), toDate.getText().toString(), "&quot;&quot;");
            }
        });
        checkbox_relax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFromDateSet = true;
                isToDateSet = true;

                fetchData(fromDate.getText().toString(), toDate.getText().toString(), "&quot;&quot;");
            }
        });


        recyclerView = v.findViewById(R.id.user_name_history_recyclerview);

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        Integer date_time=Integer.valueOf(ts);
        System.out.println(MenuActivity.experiment_end_time);

        if (MenuActivity.experiment_end_time <= date_time) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
        }

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");

        ArrayList<String> array_list = new ArrayList<String>();
        ArrayList<String> array_list_filtered = new ArrayList<String>();
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
        moodDataMap.clear();
        relaxedDataMap.clear();
        colors.clear();

        for (int i = 0; i < response.getResult().size(); i++) {
            for (int j = 0; j < response.getResult().get(i).getMoodData().size(); j++) {
                moodDataMap.put(new DataPoint(j, response.getResult().get(i).getMoodData().get(j)), response.getResult().get(i).getId());
                relaxedDataMap.put(new DataPoint(j, response.getResult().get(i).getRelaxedData().get(j)), response.getResult().get(i).getId());
            }
        }
        generateColors(array_list_contacted_user_id);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(point_graph);
        staticLabelsFormatter.setHorizontalLabels(response.getLabelDate().toArray(new String[0]));
        point_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        PointsGraphSeries<DataPoint> dataSeries = new PointsGraphSeries<>(listToArray(moodDataMap, relaxedDataMap));

        point_graph.addSeries(dataSeries);
        dataSeries.setCustomShape((canvas, paint, x, y, dataPoint) -> {
            if (moodDataMap.containsKey(dataPoint) && checkbox_mood.isChecked()) {
                paint.setColor(colors.get(moodDataMap.get(dataPoint)));
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, 25, paint);
                //canvas.drawText(String.valueOf(dataPoint.getY()), x, y, paint);
            } else if (relaxedDataMap.containsKey(dataPoint) && checkbox_relax.isChecked()) {
                paint.setColor(colors.get(relaxedDataMap.get(dataPoint)));
                canvas.drawLine(x, y, x + 40, y, paint);
                canvas.drawLine(x + 40, y, x + 40, y + 40, paint);
                canvas.drawLine(x + 40, y + 40, x, y + 40, paint);
                canvas.drawLine(x, y + 40, x, y, paint);
            }
        });

        dataSeries.setOnDataPointTapListener((series, dataPoint) -> {
            String msg = "X:" + dataPoint.getX() + "\nY:" + dataPoint.getX();
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
         });
    }

    private void generateColors(List<String> friends) {
        colors.put(Integer.valueOf(USER_ID), colorList.get(0));
        for (int i = 0; i < friends.size(); i++) {
            colors.put(Integer.valueOf(friends.get(i)), colorList.get(i + 1));
        }
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

    private DataPoint[] listToArray(HashMap<DataPoint, Integer> mood, HashMap<DataPoint, Integer> relaxed) {

        Set<DataPoint> all = new HashSet<>();
        all.addAll(mood.keySet());
        all.addAll(relaxed.keySet());

        return all.toArray(new DataPoint[all.size()]);
    }
}
