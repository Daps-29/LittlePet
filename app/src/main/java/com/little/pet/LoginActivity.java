package com.little.pet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.little.pet.adapter.LoginAdapter;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton google,face;
    private Button loginb;
    private GoogleSignInClient mgoogleSignInClient;
    float v=0;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private LoginButton loginButton;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private static final String TAG = "Autorizado";
    private static final int RC_SIGN_IN = 123;
    //login
    private String em = "";
    private String pa = "";
    private EditText coreo;
    private EditText pass;
    String photourl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        google = findViewById(R.id.fab_google);
        face = findViewById(R.id.face);
        //login
        coreo = findViewById(R.id.correo);
        pass = findViewById(R.id.contra);
        loginButton = findViewById(R.id.fab_facebook);
        loginButton.setReadPermissions("email","public_profile");
        tabLayout.addTab(tabLayout.newTab().setText("Ingresar"));
        tabLayout.addTab(tabLayout.newTab().setText("Registrar"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        loginb = findViewById(R.id.login);
        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(),this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        google.setTranslationY(300);
        tabLayout.setTranslationY(300);


        google.setAlpha(v);
        tabLayout.setAlpha(v);

        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

        mCallbackManager = CallbackManager.Factory.create();

        crearsolicitud();


        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, com.little.pet.FacebookActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });



        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sigIn();
            }
        });
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    updateUI(user);
                }else{
                    updateUI(null);
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    mFirebaseAuth.signOut();
                }
            }
        };



    }
    private void crearsolicitud(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("417998320479-4lod7c6bdrtoc05vauop80ngp0pfbbc7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mgoogleSignInClient = GoogleSignIn.getClient(this,gso);


    }
    private void sigIn(){
        Intent sigIntent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(sigIntent,RC_SIGN_IN);
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
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progresdialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                    startActivity(new Intent(LoginActivity.this, com.little.pet.MainActivity.class));
                }else{
                    Log.d(TAG, "Error", task.getException());
                    Toast.makeText(LoginActivity.this, "Fallo", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AutenticacionFirebase(account);
            }catch (ApiException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void AutenticacionFirebase(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    int tipo = 1;

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
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progresdialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

                    startActivity(new Intent(LoginActivity.this, com.little.pet.MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user){
        if (user != null){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
        if (mFirebaseAuth.getCurrentUser() !=null){
            startActivity(new Intent(LoginActivity.this, com.little.pet.MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            mFirebaseAuth.removeAuthStateListener(authStateListener);

        }
    }


}