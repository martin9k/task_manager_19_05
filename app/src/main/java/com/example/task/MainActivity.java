package com.example.task;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.task.R;
import com.example.task.RegistrationActivity;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


    private GoogleApiClient googleApiClient;
    TextView textView;
    private static final int RC_SIGN_IN = 101;
    private TextView singup;
    private TextView singin;
    private EditText log;
    private EditText pass;
    private Button btnLog;
    private Button btnGoogle;

    private LoginButton loginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private static final String TAG = "FacebookAuthentication";
    private GoogleApiClient mGoogleApiClient;
    private Button loginAnonymousbutton;
    private FirebaseUser currentUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mFirebaseAnalytics=FirebaseAnalytics.getInstance( this );
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mFirebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize( getApplicationContext() );
        loginButton = findViewById( R.id.login_button );
        loginButton.setReadPermissions( "email", "public_profile" );
        loginAnonymousbutton = findViewById( R.id.loginAnonymousbutton );
        loginAnonymousbutton.setOnClickListener( new View.OnClickListener() {        //insert a listener on button that checks whether the button is clicked
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    mAuth.signInAnonymously().
                            addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
                                    }
                                }
                            } )
                            .addOnFailureListener( new OnFailureListener() {         //if the signin failed
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e( "TAG", e.getMessage() );            //return error in logs
                                }
                            } );
                } else                                            //check if the user is not new
                {
                    Toast.makeText( getApplicationContext(), "you are already login anonymously!!!", Toast.LENGTH_LONG ).show();
                }
            }
        } );
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback( mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d( TAG, "onSuccess" + loginResult );
                handlerFacebookToken( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
                Log.d( TAG, "onCancel" );

            }

            @Override
            public void onError(FacebookException error) {
                Log.d( TAG, "onError" + error );
            }
        } );
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    updateUI( user );
                } else {
                    updateUI( null );
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mFirebaseAuth.signOut();
                }
            }
        };

        if (mAuth.getCurrentUser() != null) {
            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
        }
        mDialog = new ProgressDialog( this );
        singup = findViewById( R.id.singup );
        singin=findViewById( R.id.singin );
        log = findViewById( R.id.email_login );
        pass = findViewById( R.id.password );
        btnLog = findViewById( R.id.login_btn );

        btnLog.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = log.getText().toString().trim();
                String mPassword = pass.getText().toString().trim();
                if (TextUtils.isEmpty( mEmail )) {
                    log.setError( "Required field.." );
                    return;
                }
                if (TextUtils.isEmpty( mPassword )) {
                    pass.setError( "Required field.." );
                    return;
                }
                mDialog.setMessage( "Processing.." );
                mDialog.show();
                mAuth.signInWithEmailAndPassword( mEmail, mPassword ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText( getApplicationContext(), "Login Successful", Toast.LENGTH_LONG ).show();
                            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
                            mDialog.dismiss();
                        } else {
                            Toast.makeText( getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_LONG ).show();
                            mDialog.dismiss();
                        }
                    }
                } );
            }
        } );

        singin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(), Login_google.class ) );
            }
        } );

        singup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(), RegistrationActivity.class ) );
            }
        } );
    }



    private void gotoProfile(){
        Intent intent=new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
    }


    public void handlerFacebookToken(AccessToken token) {
        Log.d( TAG, "handleFacebookToken" );
        AuthCredential credential = FacebookAuthProvider.getCredential( token.getToken() );
        mFirebaseAuth.signInWithCredential( credential ).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d( TAG, "Loging successful" );
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI( user );
                } else {
                    Log.d( TAG, "Loging successful", task.getException() );
                    Toast.makeText( MainActivity.this, "Authentication failed", Toast.LENGTH_LONG ).show();
                    updateUI( null );
                }
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        mCallbackManager.onActivityResult( requestCode, resultCode, data );
        super.onActivityResult( requestCode, resultCode, data );

    }



    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent accountIntent = new Intent( MainActivity.this, HomeActivity.class );
            startActivity( accountIntent );
            finish();
        } else {
            Log.d( TAG, "Loging unsuccessful" );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener( authStateListener );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mFirebaseAuth.removeAuthStateListener( authStateListener );
        }
    }

}

