package com.br.notemarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrincipalActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView tvUserLogado;
    private FloatingActionButton floatActionButtonAdicLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tvUserLogado = findViewById(R.id.tvUserLogado);
        floatActionButtonAdicLista = findViewById(R.id.floatActionButtonAdicLista);

        floatActionButtonAdicLista.setVisibility(View.GONE);
   }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth != null) {
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                tvUserLogado.setText(mAuth.getCurrentUser().getEmail());

                floatActionButtonAdicLista.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAuth != null) {
            mAuth.signOut();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_principal, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.login) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        return  true;
    }

    public void clickCriarLista(View v) {
        startActivity(new Intent(PrincipalActivity.this, CadastrarListaActivity.class));
    }


}
