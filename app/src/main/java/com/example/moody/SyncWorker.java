package com.example.moody;

import android.content.Context;
import android.database.Cursor;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncWorker extends Worker {
    //Userspecialsituation usermeeting survey
    private static String URL_POST_SURVEY = "http://192.168.0.16/api/survey";
    private static String URL_POST_USERMEETING = "http://192.168.0.16/api/usermeeting";
    private static String URL_POST_Userspecialsituation = "http://192.168.0.16/api/userspecialsituation";
    String USER_ID, db_survey_id, api_survey_id;

    JSONArray jsonArray;

    public SyncWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @Override
    public Result doWork() {
        sync_data();
        System.out.println("sync strart");

        return Result.success();
    }

    private void sync_data() {
        System.out.println("sync strart");
        DBHelper DB = new DBHelper(getApplicationContext());

        Cursor cursor_survey = DB.getSurvey();
        cursor_survey.moveToFirst();

        while (cursor_survey.isAfterLast() == false) {

            System.out.println( cursor_survey.getString(cursor_survey.getColumnIndex("user_id")));
            db_survey_id = cursor_survey.getString(cursor_survey.getColumnIndex("id"));

            System.out.println(db_survey_id);
            DB.setSyncSurvey(db_survey_id);
            System.out.println("database sync");

            Map<String, String> params = new HashMap<>();
            params.put("user_id", cursor_survey.getString(cursor_survey.getColumnIndex("user_id")));
            params.put("mood_level", cursor_survey.getString(cursor_survey.getColumnIndex("mood_level")));
            params.put("relaxed_level", cursor_survey.getString(cursor_survey.getColumnIndex("relaxed_level")));
            params.put("sync", "1");

            StringRequest stringRequestSurvey = new StringRequest(Request.Method.POST, URL_POST_SURVEY,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonSurvey = new JSONObject(response);
                                JSONObject jsonSurveyData = jsonSurvey.getJSONObject("data");
                                System.out.println(jsonSurvey);
                                System.out.println(jsonSurveyData);
                                api_survey_id = jsonSurveyData.getString("id");
                                System.out.println(api_survey_id);
                                System.out.println("api geldi");
                                DB.setSyncSurvey(db_survey_id);

                                Cursor cursor_user_special_situation = DB.getUserSpecialSituation(db_survey_id);
                                cursor_user_special_situation.moveToFirst();

                                while (cursor_user_special_situation.isAfterLast() == false) {
                                    System.out.println(api_survey_id);
                                    System.out.println("api_survey_id");

                                    Map<String, String> user_special_params = new HashMap<>();
                                    user_special_params.put("survey_id", api_survey_id);
                                    user_special_params.put("special_situation", cursor_user_special_situation.getString(cursor_user_special_situation.getColumnIndex("special_situation")));
                                    user_special_params.put("special_situation_type", cursor_user_special_situation.getString(cursor_user_special_situation.getColumnIndex("special_situation_type")));

                                    StringRequest stringRequestPostUserspecialsituation = new StringRequest(Request.Method.POST, URL_POST_Userspecialsituation,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        System.out.println("special stituation saved");
                                                        jsonArray = new JSONArray(response);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                }
                                            }) {
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            return user_special_params;
                                        }
                                    };
                                    RequestQueue requestQueueUserspecialsituation = Volley.newRequestQueue(getApplicationContext());
                                    requestQueueUserspecialsituation.add(stringRequestPostUserspecialsituation);

                                    cursor_user_special_situation.moveToNext();
                                }



                                Cursor cursor_user_meeting = DB.getUserMeeting(db_survey_id);
                                cursor_user_meeting.moveToFirst();

                                while (cursor_user_meeting.isAfterLast() == false) {
                                    Map<String, String> user_meeting_params = new HashMap<>();
                                    user_meeting_params.put("survey_id", api_survey_id);
                                    user_meeting_params.put("contacted_user_id", cursor_user_meeting.getString(cursor_user_meeting.getColumnIndex("contacted_user_id")));
                                    user_meeting_params.put("meeting_type", cursor_user_meeting.getString(cursor_user_meeting.getColumnIndex("meeting_type")));


                                    System.out.println(cursor_user_meeting);
                                    StringRequest stringRequestPostUserMeeting = new StringRequest(Request.Method.POST, URL_POST_USERMEETING,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        jsonArray = new JSONArray(response);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                }
                                            }) {
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            return user_meeting_params;
                                        }
                                    };

                                    RequestQueue requestQueueUserMeeting = Volley.newRequestQueue(getApplicationContext());
                                    requestQueueUserMeeting.add(stringRequestPostUserMeeting);

                                    cursor_user_meeting.moveToNext();
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
                    }) {
                protected Map<String, String> getParams() throws AuthFailureError {

                    return params;
                }
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };

            RequestQueue requestQueueSurvey = Volley.newRequestQueue(getApplicationContext());
            requestQueueSurvey.add(stringRequestSurvey);

            cursor_survey.moveToNext();
        }
    }
}
