package com.example.moody;

import android.app.AlertDialog;
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

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment {

    PointsGraphSeries<DataPoint> xyValues;
    GraphView point_graph;
    SharedPreferences sharedPreferences;
    DBHelper DB;
    String USER_ID;

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

        while(cursor_surve.isAfterLast() == false){

            cursor_surve.moveToNext();
        }

        PointsGraphSeries<DataPoint> point_series = new PointsGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 2000),
                new DataPoint(1, 2500),
                new DataPoint(2, 2700),
                new DataPoint(3, 3000),
                new DataPoint(4, 300),
                new DataPoint(5, 2800),
                new DataPoint(3, 3700),
                new DataPoint(4, 3800),
                new DataPoint(5, 3500),
        });
        point_graph.addSeries(point_series);
       // point_series.setShape(PointsGraphSeries.Shape.RECTANGLE);
        point_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, 40, paint);
               /* canvas.drawLine(x, y, x+40, y , paint);
                canvas.drawLine(x+40, y, x + 40, y+40, paint);
                canvas.drawLine(x + 40, y+40, x, y + 40, paint);
                canvas.drawLine(x, y+40, x, y, paint);*/
            }
        });
        point_series.setColor(Color.GREEN);
        point_series.setSize(18);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(point_graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Jan", "Feb", "March", "Apr", "May", "june", "Aug", "Sept", "OCt", "Nov", "Dec"});
        point_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        point_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
              }
        });



        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alert = builder.create();

        alert.setTitle("Create the program");
        alert.setView(v);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            } });
        alert.show();


        point_graph.setOnTouchListener(new View.OnTouchListener(){
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

        });
        return v;
    }
}
