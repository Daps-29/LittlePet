package com.little.pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginTabFragment extends Fragment {

    EditText email;
    EditText contra;
    TextView olvidaste;
    private String em = "";
    private String pa = "";

    Button login;
    float v=0;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private LoginButton loginButton;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    ProgressDialog progressDialog;
    private static final String TAG = "Autorizado";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        email = root.findViewById(R.id.email);
        contra = root.findViewById(R.id.password);
        olvidaste = root.findViewById(R.id.olvi);
        login = root.findViewById(R.id.login);

        olvidaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginTabFragment.this.getContext(), com.little.pet.ResetPasswordActivity.class));
            }
        });

        email.setTranslationX(800);
        contra.setTranslationX(800);
        olvidaste.setTranslationX(800);
        login.setTranslationX(800);

        email.setAlpha(v);
        contra.setAlpha(v);
        olvidaste.setAlpha(v);
        login.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        contra.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        olvidaste.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = email.getText().toString();
                pa = contra.getText().toString();
                if (!em.isEmpty() && !pa.isEmpty()) {
                    loginUser();
                }else {
                    Toast.makeText(LoginTabFragment.this.getContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
    private void loginUser(){
        mFirebaseAuth.signInWithEmailAndPassword(em,pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog = new ProgressDialog(LoginTabFragment.this.getContext());
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progresdialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                    startActivity(new Intent(LoginTabFragment.this.getContext(), com.little.pet.MainActivity.class));

                }else{
                    Toast.makeText(LoginTabFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

