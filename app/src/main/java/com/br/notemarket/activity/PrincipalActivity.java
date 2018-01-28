package com.br.notemarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.br.notemarket.R;
import com.br.notemarket.presenter.PrincipalPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrincipalActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView tvUserLogado;
    private TextView tvListaJaCdt;
    private FloatingActionButton floatActionButtonAdicLista;
    private ListView listViewListasJaCdt;
    private boolean abrindoTelaLogin;

    private PrincipalPresenter principalPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tvUserLogado = findViewById(R.id.tvUserLogado);
        tvListaJaCdt = findViewById(R.id.tvListasCdt);
        floatActionButtonAdicLista = findViewById(R.id.floatActionButtonAdicLista);
        listViewListasJaCdt = findViewById(R.id.listViewListasJaCdt);

        ocultarLabelBotaoListaJaCdt();

        principalPresenter = new PrincipalPresenter(this);

        listViewListasJaCdt.setAdapter(principalPresenter.adapter);

        listViewListasJaCdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                principalPresenter.onItemClickListaJaCdt(position);
            }
        });
   }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth != null) {
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                tvUserLogado.setText(mAuth.getCurrentUser().getEmail());

                mostrarLabelBotaoListaJaCdt();

                //carregar nome das listas ja cdt por esse usuario
                principalPresenter.obterNomeListasJaCadastradas();
            }
        } else {
            ocultarLabelBotaoListaJaCdt();
            tvUserLogado.setText("");
            principalPresenter.limparListas();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*se esta finalizando essa activity para abrir a tela de login manter o usuario conectado, caso contrario, se esta saindo
        da app deslogar o usuario*/
        if (!abrindoTelaLogin) {
            if (mAuth != null) {
                mAuth.signOut();
            }
        }
        abrindoTelaLogin = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_principal, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.login) {
            abrindoTelaLogin = true;
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return  true;
    }

    public void clickCriarLista(View v) {
        startActivity(new Intent(PrincipalActivity.this, CadastrarListaActivity.class));
    }

    public void ocultarLabelBotaoListaJaCdt() {
        floatActionButtonAdicLista.setVisibility(View.GONE);
        tvListaJaCdt.setVisibility(View.INVISIBLE);
    }

    public void mostrarLabelBotaoListaJaCdt() {
        floatActionButtonAdicLista.setVisibility(View.VISIBLE);
        tvListaJaCdt.setVisibility(View.VISIBLE);
    }


}
