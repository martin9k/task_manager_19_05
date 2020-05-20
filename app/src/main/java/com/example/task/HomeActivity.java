package com.example.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
private Toolbar toolbar;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
private FloatingActionButton floatingActionButton;
private RecyclerView recyclerView;
private  EditText uptitle;
private EditText upnote;
private Button btnDelete;
private Button btnUpdate;
private String title,note,post_key;
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
                final AlertDialog dialog=myDialog.create();
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
                        dialog.dismiss();
                    }
                } );
                dialog.show();
            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(Data.class,R.layout.item_data,MyViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(MyViewHolder viewholder, final Data model, final int position){
    viewholder.setTitle( model.getTitle() );
    viewholder.setNote( model.getNote() );
    viewholder.setDate( model.getDate() );
    viewholder.myview.setOnClickListener( new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            post_key=getRef( position ).getKey();
            title=model.getTitle();
            note=model.getNote();
            updateData();
        }
    } );
        }
        };
        recyclerView.setAdapter(adapter);
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
public void updateData(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder( HomeActivity.this );
        LayoutInflater inflater=LayoutInflater.from( HomeActivity.this );
        View myview=inflater.inflate( R.layout.updateinputfield,null );
        mydialog.setView( myview );
        final AlertDialog dialog=mydialog.create();
uptitle=myview.findViewById( R.id.edt_titleupd);
upnote=myview.findViewById( R.id.edt_noteupd );
uptitle.setText( title );
uptitle.setSelection( title.length() );
upnote.setText( note );
upnote.setSelection( note.length() );
btnUpdate=myview.findViewById( R.id.btn_upd );
btnDelete=myview.findViewById( R.id.btn_del );
btnUpdate.setOnClickListener( new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        title=uptitle.getText().toString().trim();
        note=upnote.getText().toString().trim();
        String mDate=DateFormat.getDateInstance().format( new Date(  ));
        Data data=new Data( title,note,mDate,post_key );
        mDatabase.child( post_key ).setValue( data );
        dialog.dismiss();
    }
} );
btnDelete.setOnClickListener( new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        mDatabase.child( post_key ).removeValue();
        dialog.dismiss();
    }
} );
        dialog.show();
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate( R.menu.mainmenu,menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
            mAuth.signOut();
            startActivity( new Intent( getApplicationContext(),MainActivity.class ) );
            break;
        }
        return super.onOptionsItemSelected( item );
    }
}
