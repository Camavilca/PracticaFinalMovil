package camavilca.orlando.finalexamen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.ViewHolder> {

    private static final String TAG = PostRVAdapter.class.getSimpleName();

    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public PostRVAdapter(){
        this.posts = new ArrayList<>();
    }
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView displaynameText;
        TextView locationText;
        Button btnMapear;

        ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_picture);
            displaynameText = itemView.findViewById(R.id.user_displayname);
            locationText = itemView.findViewById(R.id.location_text);
            btnMapear = itemView.findViewById(R.id.btn_mapear);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Post post = posts.get(position);

        //viewHolder.titleText.setText(post.getTitle());
        //viewHolder.bodyText.setText(post.getBody());

        viewHolder.locationText.setText("39 Km");
        viewHolder.userImage.setImageResource(R.drawable.ic_profile);
        // Obteniendo datos del usuario asociado al post (una vez, sin realtime)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(post.getUserid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange " + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);

                viewHolder.displaynameText.setText(user.getEmail());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        });

        viewHolder.btnMapear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.startActivity(new Intent(context, MapsActivity.class));
            }
        });

        // Get currentuser from FirebaseAuth
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }

}