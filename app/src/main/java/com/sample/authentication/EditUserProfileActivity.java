package com.sample.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.sample.authentication.databinding.ActivityEditUserProfileBinding;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.CAMERA;

public class EditUserProfileActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    private static final String TAG = EditUserProfileActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CAMER_FOR_PROFILE = 100;
    private static final int REQUEST_CODE_CAMERA_PERMISSION_FOR_PROFILE = 101;
    private ActivityEditUserProfileBinding binding;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth.getInstance().addAuthStateListener(this);
        binding.photo.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);
        binding.save.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() == null) {
            binding.photo.setImageResource(R.drawable.ic_baseline_account_circle_24);
        } else {
            Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(binding.photo);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            binding.name.setText(user.getDisplayName());
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.save:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if (!TextUtils.isEmpty(binding.name.getText().toString())) {
                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                        builder.setDisplayName(binding.name.getText().toString());
                        UserProfileChangeRequest profileChangeRequest = builder.build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(TAG, "completed changes of user profile");
                                Toast.makeText(EditUserProfileActivity.this, "수정되었습니다", Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.photo:
                pickFromCamera();
                break;
        }
    }


    private void pickFromCamera() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                try {
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    photoFile = File.createTempFile("picture_" + timeStamp, ".jpg", storageDir);
                    Uri photoURI = FileProvider.getUriForFile(this, "com.sample.authentication.provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, REQUEST_CODE_CAMER_FOR_PROFILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CODE_CAMERA_PERMISSION_FOR_PROFILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMER_FOR_PROFILE:
                    Glide.with(this).load(photoFile).apply(RequestOptions.circleCropTransform()).into(binding.photo);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION_FOR_PROFILE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromCamera();
                }
                break;
        }
    }
}