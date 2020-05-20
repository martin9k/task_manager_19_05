package com.example.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login_google extends AppCompatActivity {
private SignInButton signInButton;
private GoogleSignInClient mGoogleSignInClient;
private String TAG="MainActivity";
        private FirebaseAuth mAuth;
        private int RC_SIGN_IN=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login_google );
        signInButton=findViewById( R.id.sign_in_button );
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso =new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken( getString( R.string.default_web_client_id ) )
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient( this,gso );
        signInButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singIn();
            }
        } );
    }
    private void singIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult( signInIntent,RC_SIGN_IN );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent( data );
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
try{
GoogleSignInAccount acc=completedTask.getResult(ApiException.class);
    Toast.makeText( Login_google.this,"Singed in Successfully",Toast.LENGTH_SHORT ).show();
    FirebaseGoogleAuth(acc);
}
catch (ApiException e){

    Toast.makeText( Login_google.this,"Singed in Failed",Toast.LENGTH_SHORT ).show();
    FirebaseGoogleAuth(null);
}
    }
    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential= GoogleAuthProvider.getCredential( acct.getIdToken(),null );
        mAuth.signInWithCredential( authCredential ).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText( Login_google.this,"Successful",Toast.LENGTH_SHORT ).show();
                    FirebaseUser user=mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Toast.makeText( Login_google.this,"Unuccessful",Toast.LENGTH_SHORT ).show();
                    updateUI(null);
                }
            }
        } );
    }
    private  void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount( getApplicationContext() );

            if (account != null) {
                Intent accountIntent = new Intent( Login_google.this, HomeActivity.class );
                startActivity( accountIntent );
                finish();
            }

    }
}
