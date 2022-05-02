package com.little.pet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText reset;
    Button btnreset;
    String email="";
    FirebaseAuth mauth;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mauth = FirebaseAuth.getInstance();

        reset = findViewById(R.id.resetpass);
        btnreset = findViewById(R.id.btnreset);

        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = reset.getText().toString();
                if (!email.isEmpty()) {
                    resetPass();
                }
                else {
                    Toast.makeText(ResetPasswordActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void resetPass(){
        mauth.setLanguageCode("es");
        mauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this, "Se envio al correo", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                }else {
                    Toast.makeText(ResetPasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}