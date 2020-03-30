package com.example.youtubenativeplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements videoDisplayer.OnFragmentInteractionListener {
        Button button ;
        ListView listView ;
        ArrayList<VideoData> arrayList ;
        ListViewAdapter listViewAdapter ;
        final  String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=";
        final  String queryPrefs = "&key=";
        private  final String API_KEY = "AIzaSyB43vfEpUD3NFH5ZV3l1UXcJOZ5aZ7SIZQ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           // listView = findViewById(R.id.listview);
            arrayList = new ArrayList<>();
           // listViewAdapter = new ListViewAdapter(MainActivity.this , arrayList,L);
       identifyViews();
        }

        private  void  identifyViews(){
            ImageButton button = findViewById(R.id.imageButton2);
            ImageButton imageButton2 = findViewById(R.id.imageButton3) ;
            ImageButton imageButton3 = findViewById(R.id.imageButton);
            ImageButton imageButton4 = findViewById(R.id.imageButton4);
            ImageButton imageButton5 = findViewById(R.id.imageButton5);
            ImageButton imageButton6 = findViewById(R.id.imageButton6);
            TextView textView = findViewById(R.id.textView);
            TextView textView2 = findViewById(R.id.textView2);
            TextView textView3= findViewById(R.id.textView3);
            TextView textView4 = findViewById(R.id.textView4);
            TextView textView5 = findViewById(R.id.textView5);
            TextView textView6 = findViewById(R.id.textView6);
            textView.setOnClickListener(oncl);
            textView2.setOnClickListener(oncl);
            textView3.setOnClickListener(oncl);
            textView4.setOnClickListener(oncl);
            textView5.setOnClickListener(oncl);
            textView6.setOnClickListener(oncl);
            button.setOnClickListener(oncl);
            imageButton2.setOnClickListener(oncl);
            imageButton3.setOnClickListener(oncl);
            imageButton4.setOnClickListener(oncl);
            imageButton5.setOnClickListener(oncl);
            imageButton6.setOnClickListener(oncl);
        }
    View.OnClickListener oncl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.textView :
                case R.id.imageButton2: startFrag("UCk5OZdnlhjc0DsZrnRUlIeQ");
            break;
                case R.id.textView2 :
                case R.id.imageButton3: startFrag("UCe0TLA0EsQbE-MjuHXevj2A ");
                    break;
                case R.id.textView3 :
                case R.id.imageButton: startFrag("UC07e1ZK4KWoDWnCN-z6VUiA");
                    break;
                case R.id.textView4 :
                case R.id.imageButton4: startFrag("UCY1kMZp36IQSyNx_9h4mpCg");
                    break;
                case R.id.textView5 :
                case R.id.imageButton5: startFrag("UCoxcjq-8xIDTYp3uz647V5A");
                    break;
                case R.id.textView6 :
                case R.id.imageButton6: startFrag("UC_Fh8kvtkVPkeihBs42jGcA");
                    break;


        }}
    };
    View.OnClickListener athleanxListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),Player.class) ;
        intent.putExtra("url", "k6nLfCbAzgo");
        startActivity(intent);


    }
};
    void startFrag(String channelId){
        Bundle bundle = new Bundle();
        bundle.putString("edttext", channelId);
        videoDisplayer frag = new videoDisplayer() ;
        frag.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment,frag).addToBackStack(null);  setContentView(R.layout.emptylayout);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
       identifyViews();
        super.onBackPressed();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
