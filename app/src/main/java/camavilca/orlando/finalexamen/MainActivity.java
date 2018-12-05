package camavilca.orlando.finalexamen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);
        User user = new User();
        user.setUid(currentUser.getUid());
        user.setEmail(currentUser.getEmail());
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(user.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onSuccess");
                        }else{
                            Log.e(TAG, "onFailure", task.getException());
                        }
                    }
                });
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange " + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                setTitle(user.getEmail());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final PostRVAdapter postRVAdapter = new PostRVAdapter();
        recyclerView.setAdapter(postRVAdapter);
        final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded " + dataSnapshot.getKey());
                String postKey = dataSnapshot.getKey();
                final Post addedPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "addedPost " + addedPost);
                List<Post> posts = postRVAdapter.getPosts();
                posts.add(0, addedPost);
                postRVAdapter.notifyDataSetChanged();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString("fullname", "Danilo Lopez");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
                mFirebaseAnalytics.setUserProperty("username", "dlopez");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "currentUser: " + currentUser);
                User user = new User();
                user.setUid(currentUser.getUid());
                user.setEmail(currentUser.getEmail());
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                usersRef.child(user.getUid()).setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onSuccess");
                                } else {
                                    Log.e(TAG, "onFailure", task.getException());
                                }
                            }
                        });
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "user: " + user1);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved " + dataSnapshot.getKey());
                String postKey = dataSnapshot.getKey();
                Post removedPost = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "removedPost " + removedPost);
                List<Post> posts = postRVAdapter.getPosts();
                posts.remove(removedPost);
                postRVAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildMoved " + dataSnapshot.getKey());
                Post movedPost = dataSnapshot.getValue(Post.class);
                String postKey = dataSnapshot.getKey();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        };
        postsRef.addChildEventListener(childEventListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                callLogout(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void callLogout(View view){
        Log.d(TAG, "Ssign out user");
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
