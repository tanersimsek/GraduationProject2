package cmpe.sjsu.socialawesome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import cmpe.sjsu.socialawesome.Utils.UserAuth;
import cmpe.sjsu.socialawesome.models.User;

public class CreatePostActivity extends AppCompatActivity {
    private EditText contentText;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private Uri filePath;
    private ImageView contentPicView;
    private ProgressDialog pd;

    private static int UPLOAD_REQUEST = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        contentText = (EditText) findViewById(R.id.post_content);
        contentPicView = (ImageView) findViewById(R.id.post_pic);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Uploading image...");
        User user = UserAuth.getInstance().getCurrentUser();
        if (user.profilePhotoURL == null) {
            user.profilePhotoURL = getString(R.string.default_profile_pic);
        }
        Picasso.with(getApplicationContext()).load(user.profilePhotoURL).into(((ImageView)findViewById(R.id.timeline_pic)));
        ((TextView)findViewById(R.id.timeline_name)).setText(user.first_name + " " + user.last_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.post) {
            final Intent data = new Intent();
            data.putExtra(TimeLineFragment.POST_CONTENT_KEY, contentText.getText().toString());
            if (filePath == null) {
                setResult(TimeLineFragment.RESULT_OK, data);
                finish();
            } else {
                pd.show();
                String fileName = UUID.randomUUID().toString() + ".jpg";
                StorageReference newRef = storageRef.child(fileName);
                UploadTask uploadTask = newRef.putFile(filePath);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        data.putExtra(TimeLineFragment.POST_CONTENT_URL_KEY, taskSnapshot.getDownloadUrl().toString());
                        setResult(TimeLineFragment.RESULT_OK, data);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG);
                    }
                });
            }
        } else if (id == R.id.upload) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), UPLOAD_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                contentPicView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
