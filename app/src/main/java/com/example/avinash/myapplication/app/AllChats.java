package com.example.avinash.myapplication.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.avinash.myapplication.GlobalChat;
import com.example.avinash.myapplication.GlobalChatAdapter;
import com.example.avinash.myapplication.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllChats extends AppCompatActivity {
    private String TAG;
    private ListView allChatsListView;
    AllChatsListAdapter allChatsListAdapter;
    ArrayList<ChatItem> chatItems = new ArrayList<>();
    private FirebaseUser firebaseUser;
    ConstraintLayout constraintLayout;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_chats_layout);

        allChatsListView = findViewById(R.id.all_chats_list_view);
        constraintLayout = findViewById(R.id.all_chats_constriant_layout);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        TAG = Constants.TAG;
        if(firebaseUser == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }else {
            fetchChats();
        }
    }

    // Fetch all the chats related to the logged in user.
    private void fetchChats() {
         Snackbar.make(constraintLayout, "Welcome, " + firebaseUser.getDisplayName(), Snackbar.LENGTH_LONG).show();

         // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // String url = Constants.ALL_CHATS_URL + firebaseUser.getEmail();
        String url = "http://scce.ac.in/erp/api/get_all_groups.php?email=palapatirahul@gmail.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        // mTextView.setText("Response is: "+ response.substring(0,500));
                        Log.d(TAG, "response is " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int iter = 0;iter<jsonArray.length();iter++) {
                                chatItems.add(new ChatItem(jsonArray.getString(iter), "", 0));
                            }
                            Log.d(TAG, "" + jsonArray.length());
                            populateChat();
                            initializeChatListClickListener();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void initializeChatListClickListener() {
        allChatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllChats.this, GlobalChat.class);
                intent.putExtra("chatName", chatItems.get(position).getChatName());
                startActivity(intent);
            }
        });
    }

    // Populates the chats data to the listview
    private void populateChat() {
        if(allChatsListAdapter == null) {
            allChatsListAdapter = new AllChatsListAdapter(this,chatItems);
            allChatsListView.setAdapter(allChatsListAdapter);
        }
        else {
            allChatsListAdapter.notifyDataSetChanged();
            allChatsListAdapter = new AllChatsListAdapter(this,chatItems);
            allChatsListView.setAdapter(allChatsListAdapter);
        }
        allChatsListView.setSelection(allChatsListAdapter.getCount() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_SIGN_IN:
                    IdpResponse response = IdpResponse.fromResultIntent(data);
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                    fetchChats();
                    break;
            }
        }
    }

    private void signout()
    {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_signout:
                signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
