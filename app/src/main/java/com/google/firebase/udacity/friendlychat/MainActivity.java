package com.google.firebase.udacity.friendlychat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    public int NOTIFICATION_ID = 2;
    public int INTENT_ID = 0;


    private UserAdapter mUserAdapter;
    private ProgressBar mProgressBar;
    private ListView mUserListView;
    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mUserChildEventListener;
    private ChildEventListener mMessageEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mMessagesDatabaseReference;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mUserListView = (ListView) findViewById(R.id.userListView);

        // Initialize message ListView and its adapter
        List<UserList> userLists = new ArrayList<>();
        mUserAdapter = new UserAdapter(this, R.layout.item_user, userLists);
        mUserListView.setAdapter(mUserAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getEmail().replace(".", ""));
                } else {
                    onSignedOutCleanUP();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChatRoom.class);
                intent.putExtra("receiver", mUserAdapter.getItem(position).getEmail());
                intent.putExtra("receiverName",mUserAdapter.getItem(position).getName());
                intent.putExtra("sender", mUsername);
                detachMessageDatabaseReadListener();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 9){
            if(grantResults[0] != -1) {
                attachDatabaseReadListener();
                attachMessageDatabaseReadListener();
            }
        }

    }

    public HashMap<String, String> getNameEmailDetails(){
        HashMap<String, String> names = new HashMap<String, String>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    if (email != null) {
                        names.put(email.replace(".", ""), name);
                    }
                }
                cur1.close();
            }
        }
        return names;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                final FirebaseUser user = mFirebaseAuth.getCurrentUser();
                Toast.makeText(MainActivity.this,user.getDisplayName() + " Signed in successfully",Toast.LENGTH_SHORT).show();
                mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean exist = false;
                        assert user.getEmail() != null;
                        String mail = user.getEmail();
                        mail = mail.replace(".","");
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            UserList users = data.getValue(UserList.class);
                            if(users.getEmail().contains(mail)){
                                exist = true;
                                break;
                            }
                        }
                        if(!exist){
                            UserList userList = new UserList(user.getDisplayName(),mail);
                            mUsersDatabaseReference.push().setValue(userList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this,"Sign in cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mUserAdapter.clear();
    }

    private void onSignedInInitialize(String name){
        mUsername = name;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS},9);
        }else {
            attachDatabaseReadListener();
            attachMessageDatabaseReadListener();
        }
    }

    private void attachDatabaseReadListener(){
        final HashMap<String,String> contactList = getNameEmailDetails();
        if(mUserChildEventListener == null) {
            mUserChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    UserList userList = dataSnapshot.getValue(UserList.class);
                    if(contactList.containsKey(userList.getEmail())) {
                        userList.setName(contactList.get(userList.getEmail()));
                        mUserAdapter.add(userList);
                    }
                    mUserAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUsersDatabaseReference.addChildEventListener(mUserChildEventListener);
        }
    }

    private void attachMessageDatabaseReadListener(){
        if(mMessageEventListener == null) {
            mMessageEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessageReceived friendlyMessageReceived = dataSnapshot.getValue(FriendlyMessageReceived.class);
                    if(friendlyMessageReceived.getStatus().contains("send") && friendlyMessageReceived.getReceiverName().contains(mUsername)) {
                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification.Builder builder = new Notification.Builder(MainActivity.this);
                        builder.setSmallIcon(R.mipmap.ic_launcher)
                                .setContentText(friendlyMessageReceived.getText())
                                .setContentTitle(friendlyMessageReceived.getName())
                                .setAutoCancel(true)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, INTENT_ID++,
                                new Intent(MainActivity.this,ChatRoom.class)
                                .putExtra("receiver", friendlyMessageReceived.getName())
                                .putExtra("receiverName",friendlyMessageReceived.getName())
                                .putExtra("sender", mUsername), 0);
                        builder.setContentIntent(contentIntent);

                        notificationManager.notify(NOTIFICATION_ID++, builder.build());
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mMessageEventListener);
        }
    }

    private void onSignedOutCleanUP(){
        mUsername = ANONYMOUS;
        mUserAdapter.clear();
        detachDatabaseReadListener();
        detachMessageDatabaseReadListener();
    }

    private void detachDatabaseReadListener(){
        if(mUserChildEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mUserChildEventListener);
            mUserChildEventListener = null;
        }
    }

    private void detachMessageDatabaseReadListener(){
        if(mMessageEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mMessageEventListener);
            mMessageEventListener = null;
        }
    }

}
