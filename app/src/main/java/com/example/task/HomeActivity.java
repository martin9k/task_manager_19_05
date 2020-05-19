package com.example.task;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
private Toolbar toolbar;
private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
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
                dialog.show();
            }
        } );
    }
}
