package com.little.pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;


public class FacebookActivity extends LoginActivity {
    private CallbackManager mCallbackManager;
    private static final String TAG = "Autorizado";
    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        mFirebaseAuth = FirebaseAuth.getInstance();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSucces" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"CANCEL" );

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError" + error);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode,data);

    }
    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookToken"+token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "EXITO");
                    FirebaseUser usr = mFirebaseAuth.getCurrentUser();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()){
                        String idu = usr.getUid();
                        String correo = usr.getEmail();
                        String nombre = usr.getDisplayName();

                        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null){
                            photourl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                        }else{
                            photourl = "Aca va la foto";
                        }


                        HashMap<Object,String> DatosUusuario = new HashMap<>();
                        DatosUusuario.put("id",idu);
                        DatosUusuario.put("correo",correo);
                        DatosUusuario.put("nombres",nombre);
                        DatosUusuario.put("telefono","");
                        DatosUusuario.put("direccion","");
                        DatosUusuario.put("foto",photourl);
                        DatosUusuario.put("sexo","");
                        DatosUusuario.put("tipou","1");
                        DatosUusuario.put("apellidos","");
                        DatosUusuario.put("contraseña","");

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("usuarios");
                        reference.child(idu).setValue(DatosUusuario);
                    }
                    progressDialog = new ProgressDialog(FacebookActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progresdialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                    startActivity(new Intent(FacebookActivity.this, com.little.pet.MainActivity.class));
                }else{
                    Log.d(TAG, "Error", task.getException());
                    Toast.makeText(FacebookActivity.this, "Fallo", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }
    private void updateUI(FirebaseUser user){
        if (user != null){

        }
    }
}
