package com.example.youtubenativeplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.SubscriptionSnippet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Player extends YouTubeBaseActivity implements videoDisplayer.OnFragmentInteractionListener, View.OnClickListener {
    private static final int CODE_OK = 9010;
    private static Collection<String> scopes;
    YouTubePlayer.OnInitializedListener mOninitializedListener ;
    YouTubePlayerView playerView ;
    ArrayList<String> videoList;
    ListViewAdapter listViewAdapter ;
    YouTubePlayer youtubePlayer ;
    ArrayList<VideoData> videos;
    ListView listView ;
    String channelId;
    GoogleSignInOptions gso ;
    GoogleSignInClient mGoogleSignInClient ;
    SignInButton signInButton ;
    static Button subscribeButton ;
    static Context context;
    final  int RC_SIGN_IN = 9001 ;
    private final  String API_KEY = " AIzaSyAgZHDVtxhqEjBWpuHTWFeB354JYUrb2b0";
    private static   final String ACCESS_KEY = "";
    private static final String REFRESH_TOKEN = "";
    private  final  String request = "https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&key="+API_KEY;
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private  final Scope  youTubeScope = new Scope("https://www.googleapis.com/auth/youtube");
    private static final String APPLICATION_NAME = "Youtube Native Player";
    private final static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TAG = "Player";
    static  Boolean subscribed ;
    Button signOutButton ;
    BroadcastReceiver broadcastReceiver ;
    private final int RC_REQUEST_PERMISSION_SUCCESS_Subscribtion = 9002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        context = getApplicationContext();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent intent = getIntent();
        channelId = intent.getStringExtra("channelId");
        defineViews();
        checkIfSignedIn();
        Log.d(TAG, "onCreate: "+subscribed);
        isSubscribed();
        Log.d(TAG, "onCreate: "+subscribed);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        StorageUtil storageUtil = new StorageUtil(getApplicationContext());
        videos = storageUtil.loadVideo();
        int position = storageUtil.loadVideoIndex() ;
        VideoData videoData = videos.get(0);
        videos.set(0,videos.get(position));
        videos.set(position,videoData);
        videoList = new ArrayList<>();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI();
            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter("com.nativeyoutubeplayer.UPDATEUI")) ;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            signOutButton.setVisibility(View.VISIBLE);
        }

        for (int i=0;i<videos.size();i++) {videoList.add(videos.get(i).getId()) ;}

        mOninitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                        youtubePlayer = youTubePlayer ;
                        youTubePlayer.loadVideos(videoList);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        playerView.initialize(API_KEY,mOninitializedListener);
      //  setSubscribeButtonText();

      }
      void defineViews(){
          signInButton = findViewById(R.id.sign_in_button);
          subscribeButton = findViewById(R.id.subscribe_button);
          playerView = findViewById(R.id.player);
          signInButton.setOnClickListener(this);
          subscribeButton.setOnClickListener(this);
          StorageUtil storageUtil = new StorageUtil(getApplicationContext()) ;
          videos = storageUtil.loadVideo();
          listViewAdapter = new ListViewAdapter(getApplicationContext(),videos);
          listView = findViewById(R.id.listview2);
          listView.setAdapter(listViewAdapter);
          listView.setOnItemClickListener(this.onItemClickListener);
          signOutButton = findViewById(R.id.sign_out);
          signOutButton.setOnClickListener(this);
      }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
      switch (v.getId()){
          case R.id.subscribe_button:     if (account==null){
              Log.d(TAG, "onClick: no accoutnt was logged in");
              signInButton.setVisibility(View.VISIBLE);
            subscribeButton.setVisibility(View.INVISIBLE);
            
          }
            else {try {
              if(subscribed){unsubscribe(channelId);}
               else subscribtion(channelId);
          }catch (GeneralSecurityException e1){
              Log.d(TAG, "onClick: general Security exception"+e1.getStackTrace());
              Log.d(TAG, "onClick: general Security exception"+e1.getMessage());
              Log.d(TAG, "onClick: general Security exception"+e1.toString());
              Log.d(TAG, "onClick: general Security exception"+e1.getCause());

          }catch (IOException e2){
              Log.d(TAG, "onClick: IoException");
          }}
            break;
          case R.id.sign_in_button:
            checkIfSignedIn();
            signOutButton.setVisibility(View.VISIBLE);
            subscribeButton.setVisibility(View.INVISIBLE);
            break;
          case R.id.sign_out: signOut();
            signOutButton.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE
            );
          break;

      }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Signed out Succesfully",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signin() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);
    }
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          String keep= videoList.get(0);
          videoList.set(0,videoList.get(position));
          videoList.set(position,keep) ;
        youtubePlayer.loadVideos(videoList);
        }
    } ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == RC_SIGN_IN){
             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
             handleSignInResult(task);
             if (RC_REQUEST_PERMISSION_SUCCESS_Subscribtion == requestCode) {
                 try {
                     subscribtion(channelId);
                 } catch (GeneralSecurityException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }

    }
        public void updateUI(){
            if (subscribed== true){
                subscribeButton.setText("UNSUBSCRIBE")
                ;
            }
            else {
                subscribeButton.setText("SUBSCRIBE");
            }
        }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInButton.setVisibility(View.GONE);
            subscribeButton.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"SIGN IN WAS SUCCESSFUL",Toast.LENGTH_SHORT).show();
        }catch (ApiException e){
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(),"LOG IN FAILED",Toast.LENGTH_SHORT).show();
        }
        }

            public static GoogleCredential authorize(final NetHttpTransport httpTransport, String token , String refresh) throws IOException {
              /*  scopes = new ArrayList<>();
                scopes.add(Scopes.PROFILE);
                scopes.add(Scopes.EMAIL); */
                Log.d(TAG, "authorize: i was called ");
                Gson gson = new Gson();
                InputStream in = context.getAssets().open("client_secerts.json");
                if (in==null){
                    Log.d(TAG, "authorize: inputSTREA == NuL");
                }
                GoogleClientSecrets clientSecrets =
                        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
                GoogleAuthorizationCodeFlow flow =
                        new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                                .build();
                GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).
                        setJsonFactory(JSON_FACTORY).
                        setClientSecrets(clientSecrets).build().
                        setAccessToken(token).setRefreshToken(refresh) ;

                return credential;
            }

            public static YouTube getService(String token , String refresh) throws GeneralSecurityException, IOException {
                final NetHttpTransport httpTransport =  new com.google.api.client.http.javanet.NetHttpTransport();
                GoogleCredential credential = authorize(httpTransport,token,refresh);
                Log.d(TAG, "getService: i was called");
              //  credential.setSelectedAccountName(GoogleSignIn.getLastSignedInAccount(context).getDisplayName());
                //credential.getSelectedAccount()
                YouTube.Builder builder =  new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                      .setApplicationName(APPLICATION_NAME) ;
                return  builder.build();
            }

             void subscribtion(final  String channelId)
                    throws GeneralSecurityException, IOException, GoogleJsonResponseException {
                    final HttpHeaders httpHeaders = new HttpHeaders();
                 List<String> keys = new ArrayList<>();
                 keys.add(ACCESS_KEY);
                    httpHeaders.setAuthorization(keys);
                 Thread thread = new Thread(new Runnable() {
                     @Override
                     public void run() {
                       try {

                                checkIfSignedIn();
                               YouTube youtubeService = getService(ACCESS_KEY,REFRESH_TOKEN);
                               Subscription subscription = new Subscription();
                               Log.d(TAG, "subscribtion: " + channelId);
                               SubscriptionSnippet snippet = new SubscriptionSnippet();
                               ResourceId resourceId = new ResourceId();
                               resourceId.setChannelId(channelId);
                               resourceId.setKind("youtube#channel");
                               snippet.setResourceId(resourceId);
                               subscription.setSnippet(snippet);

                               YouTube.Subscriptions.Insert request = youtubeService.subscriptions()
                                       .insert("snippet", subscription);
                               request.setKey(API_KEY);
                               request.setOauthToken(ACCESS_KEY);
                               request.setRequestHeaders(httpHeaders);
                               Subscription response = request.execute();
                               setSubscribedToTrue();
                               Log.d(TAG, "subscribtion: " + response);


                       }catch (GeneralSecurityException e){
                           Log.d(TAG, "run: "+e.getMessage());

                       }
                       catch (IOException e){
                           Log.d(TAG, "run: "+e.getMessage());

                       }

                     }

                 });
                    thread.start();
            }

            void unsubscribe(final String channelId){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            checkIfSignedIn();
                            YouTube youTubeService = getService(ACCESS_KEY,REFRESH_TOKEN);
                            YouTube.Subscriptions.List response = youTubeService.subscriptions().list("snippet,contentDetails");
                            SubscriptionListResponse res = response.setForChannelId(channelId).setMine(true).execute();
                            Log.d(TAG, "run: "+res);
                            JSONTokener tokener = new JSONTokener(res.toString());
                            JSONObject finalResult = new JSONObject(tokener);
                            JSONArray items = finalResult.getJSONArray("items");
                            JSONObject object = items.getJSONObject(0);
                            String id = object.getString("id");
                            YouTube.Subscriptions.Delete request = youTubeService.subscriptions().delete(id);
                            request.execute();
                            setSubscribedToFalse();
                        }catch (GeneralSecurityException e){
                            Log.d(TAG, "unsubscribe: "+e.getMessage());
                        }
                        catch (IOException e){
                            Log.d(TAG, "unsubscribe: "+e.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                    thread.start();
            }
               void setSubscribedToTrue(){
                subscribed = true ;
                setSubscribeButtonText();
               }
               void setSubscribedToFalse(){
                subscribed = false ;
                setSubscribeButtonText();
                   Log.d(TAG, "setSubscribedToFalse: form is false "+subscribed);
               }
                void setSubscribeButtonText(){
                    if (subscribed==true){
                        Intent intent = new Intent();
                        intent.setAction("com.nativeyoutubeplayer.UPDATEUI");
                        sendBroadcast(intent);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setAction("com.nativeyoutubeplayer.UPDATEUI");
                        sendBroadcast(intent);
                    }
                }
                void checkIfSignedIn(){
                        if (GoogleSignIn.getLastSignedInAccount(context)!= null){
                            subscribeButton.setVisibility(View.VISIBLE);
                            signOutButton.setVisibility(View.VISIBLE);
                            signInButton.setVisibility(View.INVISIBLE);
                            if (!GoogleSignIn.hasPermissions(
                                    GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                                    youTubeScope )) {
                                GoogleSignIn.requestPermissions(
                                        Player.this,
                                        RC_REQUEST_PERMISSION_SUCCESS_Subscribtion,
                                        GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                                        youTubeScope);
                            }if (!GoogleSignIn.hasPermissions(
                                    GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                                    new Scope( Scopes.PROFILE))){
                                GoogleSignIn.requestPermissions(
                                        Player.this,
                                        RC_REQUEST_PERMISSION_SUCCESS_Subscribtion,
                                        GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                                        new Scope(Scopes.PROFILE));
                            } if (!GoogleSignIn.hasPermissions(
                                    GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                                    new Scope( Scopes.EMAIL))){
                                GoogleSignIn.requestPermissions(
                                        Player.this,
                                        RC_REQUEST_PERMISSION_SUCCESS_Subscribtion,
                                        GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                                        new Scope(Scopes.EMAIL));
                            }
                        }else{
                           signin();
                        }
                }
            void isSubscribed(){
           Thread thread = new Thread(new Runnable() {
               @Override
               public void run() {
               try {
                   checkIfSignedIn();
                   YouTube youTubeService = getService(ACCESS_KEY, REFRESH_TOKEN);
                    YouTube.Subscriptions.List request = youTubeService.subscriptions().list("snippet,contentDetails");
                    SubscriptionListResponse response = request.setForChannelId(channelId).setMine(true).execute();
                    JSONTokener jsonTokener = new JSONTokener(response.toString());
                    JSONObject result = new JSONObject(jsonTokener) ;
                    JSONObject pageInfo = result.getJSONObject("pageInfo");
                    int numOfResults = pageInfo.getInt("totalResults");
                   Log.d(TAG, "run: "+numOfResults);
                    if (numOfResults == 0){
                        Log.d(TAG, "run: Form the If Statement");
                        setSubscribedToFalse();
                    }
                    else setSubscribedToTrue();
               }catch (GeneralSecurityException e){
                   Log.d(TAG, "isSubscribed: "+e.getMessage());
               }catch (IOException e){
                   Log.d(TAG, "isSubscribed: "+e.getMessage());
               } catch (JSONException e) {
                   e.printStackTrace();
               }}
                 });
                thread.start();
            }
        }



