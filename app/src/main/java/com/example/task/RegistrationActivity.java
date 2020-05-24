package com.example.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class RegistrationActivity extends AppCompatActivity {
    private EditText reg;
    private EditText pass;
    private Button btnReg;
    private TextView login_txt;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );
        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog( this );
        reg=findViewById( R.id.email_register );
        pass=findViewById( R.id.password1 );
        btnReg=findViewById( R.id.register_btn );
        login_txt=findViewById( R.id.singin );
        login_txt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(),MainActivity.class ) );            }
        } );
        btnReg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail=reg.getText().toString().trim();
                String mPassword=pass.getText().toString().trim();
                if(TextUtils.isEmpty(mEmail)){
                    reg.setError( "Required field.." );
                    return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    pass.setError( "Required field.." );
                    return;
                }
                mDialog.setMessage( "Processing.." );
                mDialog.show();
                mAuth.createUserWithEmailAndPassword( mEmail,mPassword ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText( getApplicationContext(),"Successful",Toast.LENGTH_LONG ).show();
                            startActivity( new Intent( getApplicationContext(),HomeActivity.class ) );
                            mDialog.dismiss();
                        }else{
                            Toast.makeText( getApplicationContext(),"Problem",Toast.LENGTH_LONG ).show();
                            mDialog.dismiss();
                        }
                    }
                } );
            }
        } );
    }
}
