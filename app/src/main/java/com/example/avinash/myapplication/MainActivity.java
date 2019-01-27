package com.example.avinash.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MainActivity extends AppCompatActivity {
    private int RC_SIGN_IN = 123;
    private ConstraintLayout constraintLayout;
    private static String TAG = "Avinash";
    private EditText groupNameET = null;
    private String currentUserEmailId = null;

    private ArrayList<Group> chatList = new ArrayList<Group>();
    private Button signoutButton = null;
    private ChatListAdapter adapter = null;
    ListView chatListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the variables
        constraintLayout = findViewById(R.id.constraint_layout);
        chatListView = findViewById(R.id.chat_list);
        signoutButton = findViewById(R.id.button);
        groupNameET = findViewById(R.id.editText);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
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
        }else{
            currentUserEmailId = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","");
            Snackbar.make(constraintLayout,"Welcome, "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Snackbar.LENGTH_LONG).show();
            // Adding Value event listener to fetch groups associated with current user.
//            if(currentUserEmailId != null) {
//                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + currentUserEmailId + "/groups");
//                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d(TAG, "groups count = "+ dataSnapshot.getChildrenCount());
//
//                        for(DataSnapshot groupSnapshot: dataSnapshot.getChildren()){
//                            addChat(groupSnapshot);
//                        }
//                        populateChats();
//                        initializeChatChildEventListener(databaseReference);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }

        }

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });

        FloatingActionButton addNewGroup = findViewById(R.id.send_message_btn);
        addNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Floating button clicked");
                String groupName = String.valueOf(groupNameET.getText());
                String groupKey = createNewGroup(groupName);
                if(groupKey != null) {
                    if (addGroupToCurrentUser(groupKey))
                        Log.i(TAG, "New group has been added to user");
                    else
                        Log.e(TAG, "Failed to add group to current user");
                }else{
                    Log.e(TAG,"Failed to create New Group");
                }
            }
        });
    }

    private void addChat(DataSnapshot groupSnapshot) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups/"+groupSnapshot.getKey());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                chatList.add(group);
                Log.d(TAG, "Added group to list : "+ group.getTitle());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateChat(DataSnapshot groupSnapshot) {
        // Todo:
    }

    private void initializeChatChildEventListener(DatabaseReference databaseReference) {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addChat(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateChat(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateChats() {
        Log.d(TAG, "Populating chats");
        Log.d(TAG, "Chat List count = " + chatList.size());
        if(adapter == null)
        {
            adapter = new ChatListAdapter(this,chatList);
            chatListView.setAdapter(adapter);
        }
        else
        {
            adapter.notifyDataSetChanged();
            adapter = new ChatListAdapter(this,chatList);
            chatListView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(getApplicationContext(), "Failed to sign in", Toast.LENGTH_SHORT).show();
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
    }

    private String createNewGroup(String groupName){
        if(groupName != null && !groupName.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference newGroupRef = databaseReference.child("groups").push();
            newGroupRef.setValue(new Group(groupName, ""));
            return newGroupRef.getKey();
        }
        return null;
    }

    private boolean addGroupToCurrentUser(String groupKey) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null && groupKey != null && !groupKey.isEmpty()) {
            String email_id = user.getEmail();
            FirebaseDatabase.getInstance().getReference().child("users").child(email_id.replace(".", ""))
                    .child("groups").child(groupKey).setValue("");
            return true;
        }
        return false;
    }
}
