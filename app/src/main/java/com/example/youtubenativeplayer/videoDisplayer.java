package com.example.youtubenativeplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.xml.datatype.Duration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link videoDisplayer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link videoDisplayer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class videoDisplayer extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
            private static final String ARG_PARAM1 = "param1";
            private static final String ARG_PARAM2 = "param2";
            private String mParam1;
            private String mParam2;
    private static final String TAG = "videoDisplayer";
            private OnFragmentInteractionListener mListener;
            ListView listView ;
            LayoutInflater layoutInflater;
            ListViewAdapter listViewAdapter ;
            ArrayList<VideoData> arrayList  ;
            final  String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=";
            String channelId ;
            final  String queryPrefs = "&key=";
            private  final String API_KEY = "AIzaSyBYde8Dlb_44unqzJvWjvEZRQftB0bYurM";
            private final String requestPart1 = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=";
            String token ;
            private final  String requestSecPart = "&key=AIzaSyAgZHDVtxhqEjBWpuHTWFeB354JYUrb2b0";

        public videoDisplayer() {

        }
    public static videoDisplayer newInstance() {
        return new videoDisplayer();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: on view created was called");
        listView = view.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               VideoData video =  arrayList.get(position);
                Intent intent = new Intent(getContext() , Player.class) ;
                intent.putExtra("url", video.getId());
                intent.putExtra("channelId",channelId);
                StorageUtil storageUtil = new StorageUtil(getContext());
                storageUtil.storeSong(arrayList);
                storageUtil.storeVideoIndex(position);
                getContext().startActivity(intent);
            }
        });
        listView.setOnScrollListener(new EndlessScrollListener(5));
        arrayList = new ArrayList<>();
        listViewAdapter = new ListViewAdapter(getContext(),arrayList);
        displayVids( url +channelId+queryPrefs+ API_KEY);
    }
    void displayVids(String request){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET
                ,request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response) ;
                    token = jsonObject.getString("nextPageToken");
                    JSONArray items = jsonObject.getJSONArray("items");
                    Log.d(TAG, "onResponse: "+items.length());
                    for (int i =0;i<items.length();i++){
                            JSONObject jsonObject1 = items.getJSONObject(i) ;
                            JSONObject jsonid = jsonObject1.getJSONObject("id");
                            JSONObject jsonSnippet = jsonObject1.getJSONObject("snippet");
                            JSONObject jsonImage = jsonSnippet.getJSONObject("thumbnails").getJSONObject("medium") ;
                            arrayList.add(new VideoData(jsonSnippet.getString("title")
                                        ,jsonSnippet.getString("description") , jsonImage.getString("url"),
                                        jsonid.getString("videoId") ));
                            Log.d(TAG, "onResponse: "+items.length());
                    }
                        listView.setAdapter(listViewAdapter);
                        listViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Faild" ,Toast.LENGTH_SHORT).show();

            }
        });

            requestQueue.add(stringRequest);
        }


    @Override
        public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: the fragment was created"); 
            super.onCreate(savedInstanceState);
            channelId = getArguments().getString("edttext");
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_video_displayer, container, false);
        }

        // TODO: Rename method, update argument and hook method into UI event
        public void onButtonPressed(Uri uri) {
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            void onFragmentInteraction(Uri uri);
        }

class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 20;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    public EndlessScrollListener() {
    }
    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

            displayVids(requestPart1+channelId+"&pageToken="+token+requestSecPart);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
}