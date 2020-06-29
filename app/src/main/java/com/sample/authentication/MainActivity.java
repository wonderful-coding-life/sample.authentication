package com.sample.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sample.authentication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FIREBASE_AUTH_SIGN_IN_REQUEST_CODE = 100;
    private static final int EDIT_USER_PROFILE_REQUEST_CODE = 101;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUserProfile();
        invalidateOptionsMenu();
    }

    private void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.displayName.setText("이름: " + user.getDisplayName());
            binding.email.setText("메일: " + user.getEmail());
            binding.phone.setText("전화번호: " + user.getPhoneNumber());
            if (user.getPhotoUrl() == null) {
                binding.photo.setImageResource(R.drawable.ic_baseline_account_circle_24);
            } else {
                Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(binding.photo);
            }
        } else {
            binding.displayName.setText("이름: ");
            binding.email.setText("메일: ");
            binding.phone.setText("전화번호: ");
            binding.photo.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.mainuser, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.mainguest, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    AuthUI.SignInIntentBuilder builder = AuthUI.getInstance().createSignInIntentBuilder();
                    List<AuthUI.IdpConfig> idpConfigList = new ArrayList<>();
                    idpConfigList.add(new AuthUI.IdpConfig.PhoneBuilder().build());
                    idpConfigList.add(new AuthUI.IdpConfig.EmailBuilder().build());
                    idpConfigList.add(new AuthUI.IdpConfig.GoogleBuilder().build());
                    Intent intent = builder.setAvailableProviders(idpConfigList).build();
                    startActivityForResult(intent, FIREBASE_AUTH_SIGN_IN_REQUEST_CODE);
                }
                return true;
            case R.id.logout:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.edit_user_profile:
                Intent intent = new Intent(this, EditUserProfileActivity.class);
                startActivityForResult(intent, EDIT_USER_PROFILE_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FIREBASE_AUTH_SIGN_IN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "logged in");
            } else {
                if (response == null) {
                    Log.i(TAG, "login cancelled");
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.i(TAG, "login failed due to no internet connection");
                } else {
                    Log.i(TAG, "login failed due to " + response.getError());
                }
            }
        } else if (requestCode == EDIT_USER_PROFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                updateUserProfile();
            }
        }
    }
}