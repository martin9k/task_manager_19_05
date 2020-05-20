package com.example.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
private Toolbar toolbar;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
private FloatingActionButton floatingActionButton;
private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uId=mUser.getUid();
        mDatabase=FirebaseDatabase.getInstance().getReference().child( "TaskNote" ).child( uId );
        recyclerView=findViewById( R.id.recycler );
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager( this );
        linearLayoutManager.setReverseLayout( true );
        linearLayoutManager.setStackFromEnd( true );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( linearLayoutManager );
        toolbar=findViewById( R.id.toolbar);
        floatingActionButton=findViewById( R.id.fab_btn );
        floatingActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myDialog=new AlertDialog.Builder( HomeActivity.this );
                LayoutInflater inflater=LayoutInflater.from( HomeActivity.this);
                View myview=inflater.inflate(R.layout.custominputfield,null);
                myDialog.setView( myview );
                AlertDialog dialog=myDialog.create();
                final EditText title=myview.findViewById( R.id.edt_title );
                final EditText note=myview.findViewById( R.id.edt_note );
                Button btnSave=myview.findViewById( R.id.btn_save);
                btnSave.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mTitle=title.getText().toString().trim();
                        String mNote=note.getText().toString().trim();
                        if(TextUtils.isEmpty( mTitle )){
                            title.setError( "Required Field.." );
                            return;
                        }
                        if(TextUtils.isEmpty( mNote )){
                            note.setError( "Required Field.." );
                            return;
                        }
                        String id=mDatabase.push().getKey();
                        String datee= DateFormat.getDateInstance().format( new Date(  ) );
                        Data data=new Data(mTitle,mNote,datee,id);
                        mDatabase.child( id ).setValue( data );
                        Toast.makeText(getApplicationContext(),"Data Insert",Toast.LENGTH_SHORT).show();

                    }
                } );
                dialog.show();
            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View myview;
        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            myview=itemView;
        }
public void setTitle(String title){
    TextView mTitle=myview.findViewById( R.id.title);
    mTitle.setText( title );
}
        public void setNote(String note){
            TextView mNote=myview.findViewById( R.id.note);
            mNote.setText( note );
        }
        public void setDate(String date){
            TextView mDate=myview.findViewById( R.id.date);
            mDate.setText( date );
        }
    }
}
