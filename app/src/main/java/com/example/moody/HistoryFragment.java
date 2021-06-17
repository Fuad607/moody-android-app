package com.example.moody;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.jetbrains.annotations.NotNull;
import android.content.SharedPreferences;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

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
    EditText searchText;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_history,container,false);
        point_graph=(GraphView)v.findViewById(R.id.scatterPlot);

        sharedPreferences =this.getActivity().getSharedPreferences("USER_DATA", MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("USER_ID","");

        DB=new DBHelper(getContext());

        Cursor cursor_surve = DB.getSurvey();
        cursor_surve.moveToFirst();
        point_graph.getViewport().setScrollable(true);
        point_graph.getViewport().setScrollableY(true);
        point_graph.getViewport().setScalable(true);
        point_graph.getViewport().setScalableY(true);

        while(cursor_surve.isAfterLast() == false){

            cursor_surve.moveToNext();
        }

        PointsGraphSeries<DataPoint> point_series = new PointsGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 20),
                new DataPoint(1, 25),
                new DataPoint(2, 27),
                new DataPoint(3, 30),
                new DataPoint(4, 3),
                new DataPoint(5, 28),
                new DataPoint(3, 37),
                new DataPoint(4, 38),
                new DataPoint(5, 35),
        });
        point_series.setTitle("Air");


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
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mon", "Tue", "Wed", "Thu", "Fr", "Sat", "Sun"});
        point_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
  /*      point_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
            }
        });*/


        point_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg="X:"+dataPoint.getX()+"\nY:"+dataPoint.getX();
                Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
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

        EditText fromDate=v.findViewById(R.id.fromDate);

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

        DB=new DBHelper(getContext());

        Cursor cursor_user_relationship = DB.getUserRelationshipData(USER_ID);
        cursor_user_relationship.moveToFirst();

        while(cursor_user_relationship.isAfterLast() == false){
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
        return v;
    }
}
