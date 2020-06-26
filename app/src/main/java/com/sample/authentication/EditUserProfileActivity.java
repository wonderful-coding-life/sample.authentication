package com.sample.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.sample.authentication.databinding.ActivityEditUserProfileBinding;

public class EditUserProfileActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    private static final String TAG = EditUserProfileActivity.class.getSimpleName();
    private ActivityEditUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth.getInstance().addAuthStateListener(this);
        binding.cancel.setOnClickListener(this);
        binding.save.setOnClickListener(this);
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
        }
    }
}