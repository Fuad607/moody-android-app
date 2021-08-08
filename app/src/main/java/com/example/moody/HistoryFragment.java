package com.example.moody;

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

public class HistoryFragment extends Fragment {

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
    String[] date_list = new String[7];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override

    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        LocalDate current = LocalDate.now();
        LocalDate startdate = current.minusDays(6);

        for (int i = 0; i < 7; i++) {
            // date_list += "'" + current + "',";
            int date = startdate.getDayOfMonth();
            date_list[i] = String.valueOf(date);

            startdate = startdate.plusDays(1);
        }

        sharedPreferences = this.getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID", "");

        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View v, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + "/survey/" + USER_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject user_relationship = jsonArray.getJSONObject(i);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


        super.onViewCreated(v, savedInstanceState);

        point_graph = (GraphView) v.findViewById(R.id.scatterPlot);

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

        //api call get all users whcih user have relationship

        PointsGraphSeries<DataPoint> point_series = new PointsGraphSeries<DataPoint>(new DataPoint[]{

                new DataPoint(0, 20),
                new DataPoint(1, 25),

        });


        /*
        * {
"result": {
"3": {
"nickname": "John",
"mood_data": "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
"relaxed_data": "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0"
},
"4": {
"nickname": "ddd",
"mood_data": "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
"relaxed_data": "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0"
}
},
"label_date": " '20.Nov.2021',  '21.Nov.2021',  '22.Nov.2021',  '23.Nov.2021',  '24.Nov.2021',  '25.Nov.2021',  '26.Nov.2021',  '27.Nov.2021',  '28.Nov.2021',  '29.Nov.2021',  '30.Nov.2021',  '01.Dec.2021',  '02.Dec.2021',  '03.Dec.2021',  '04.Dec.2021',  '05.Dec.2021',  '06.Dec.2021',  '07.Dec.2021',  '08.Dec.2021',  '09.Dec.2021',  '10.Dec.2021',  '11.Dec.2021',  '12.Dec.2021' "
* */




        /*
        * {
"result": {
"3": {
"nickname": "John",
"mood_data": "0, 0, 0, 0, 0",
"relaxed_data": "0, 0, 0, 0, 0"
},
"4": {
"nickname": "ddd",
"mood_data": "0, 0, 0, 0, 0",
"relaxed_data": "0, 0, 0, 0, 0"
}
},
"label_date": " '20.Dec.2021',  '21.Dec.2021',  '22.Dec.2021',  '23.Dec.2021',  '24.Dec.2021'"
}*/

//
//        for (int i = 0; i < 10; i++) {
//
//        }
//        point_series.appendData();


        point_graph.addSeries(point_series);
        // point_series.setShape(PointsGraphSeries.Shape.RECTANGLE);
        point_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, 40, paint);
                canvas.drawText(String.valueOf(dataPoint.getY()), x, y, paint);

               /* canvas.drawLine(x, y, x+40, y , paint);
                canvas.drawLine(x+40, y, x + 40, y+40, paint);
                canvas.drawLine(x + 40, y+40, x, y + 40, paint);
                canvas.drawLine(x, y+40, x, y, paint);*/
            }
        });
        point_series.setColor(Color.GREEN);
        point_series.setSize(18);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(point_graph);

        staticLabelsFormatter.setHorizontalLabels(date_list);

        point_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
  /*      point_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
            }
        });*/


        point_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg = "X:" + dataPoint.getX() + "\nY:" + dataPoint.getX();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                System.out.println("asdadas");

            }
        });


/*        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alert = builder.create();

        alert.setTitle("Create the program");
        alert.setView(v);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            } });
        alert.show();*/


     /*   point_graph.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                     float screenX = event.getX();
                    float screenY = event.getY();
                    float width_x = v.getWidth();
                    float viewX = screenX - v.getLeft();
                    float viewY = screenY - v.getTop();
                    float percent_x = (viewX/width_x);


                    System.out.println("X: " + viewX + " Y: " + viewY +" Percent = " +percent_x);

                    return true;
                }
                return false;
            }

        });*/

        EditText fromDate = v.findViewById(R.id.fromDate);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

      /*  btnGet=(Button)findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvw.setText("Selected Date: "+ eText.getText());
            }
        });*/


        recyclerView = (RecyclerView) v.findViewById(R.id.user_name_history_recyclerview);
        //  searchText= (EditText) v.findViewById(R.id.search_text);

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

        final HelperAdapterUserList[] helperAdapter = {new HelperAdapterUserList(getContext(), array_list, array_list_contacted_user_id)};
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter[0]);

     /*   searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                array_list_filtered.clear();
                array_list_contacted_user_id_filtered.clear();

                //  if (s.toString().isEmpty())
                //   helperAdapter[0] = new HelperAdapter(getContext(), array_list, array_list_contacted_user_id);

                for (int i = 0; i < array_list.size(); i++) {
                    if (array_list.get(i).contains(s.toString())) {
                        array_list_filtered.add(array_list.get(i));
                        array_list_contacted_user_id_filtered.add(array_list_contacted_user_id.get(i));
                    }
                }
                helperAdapter[0] = new HelperAdapterUserList(getContext(), array_list_filtered, array_list_contacted_user_id_filtered);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(helperAdapter[0]);
            }
        });

*/
    }
}
