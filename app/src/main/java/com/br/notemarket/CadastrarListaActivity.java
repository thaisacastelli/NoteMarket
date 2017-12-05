package com.br.notemarket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CadastrarListaActivity extends AppCompatActivity {

    private EditText editVlItem;
    private EditText editVlItemJaExistente;
    private Button bAddItem;
    private Button bRecuperarItem;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastrar_lista);

        editVlItem = findViewById(R.id.editVlItem);
        editVlItemJaExistente = findViewById(R.id.editVlItemJaExistente);
        bAddItem   = findViewById(R.id.bAddItem);
        bRecuperarItem = findViewById(R.id.bRecuperItem);

        //database reference pointing to root of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        bAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //push creates a unique id in database
                //appReference.push().setValue(editVlItem.getText().toString());
                mDatabase.child("app_notemarket").setValue(editVlItem.getText().toString());
            }
        });

        bRecuperarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("app_notemarket").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class); //tipo do retorno, se fosse um outro obj passaria sua classe ex User.class
                        editVlItemJaExistente.setText(value);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //SnackBar databaseError.toException()
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
