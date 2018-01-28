package com.br.notemarket.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.br.notemarket.R;
import com.br.notemarket.presenter.CadastrarListaPresenter;

import java.util.Calendar;

public class CadastrarListaActivity extends AppCompatActivity {

    private TextView tvVlLista;
    private EditText editVlItem;
    private EditText editVlLista;
    private ListView listViewItensAdic;
    private ImageButton bAdicItem;
    private ImageButton bCancelarItem;
    private Button bOkNomeLista;
    private ConstraintLayout layoutTela;
    private FloatingActionButton floatActionButtonExcluirItemLista;
    private String idLista = "";
    private int vlPosItemSelecionado = -1;
    private boolean carregarDadosListaJaCdt;
    private CadastrarListaPresenter cdtListaPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastrar_lista);

        tvVlLista = findViewById(R.id.tvVlLista);
        editVlLista = findViewById(R.id.editVlLista);
        listViewItensAdic = findViewById(R.id.listViewItensAdicionados);
        bOkNomeLista = findViewById(R.id.bOkLista);
        layoutTela = findViewById(R.id.layoutTela);
        floatActionButtonExcluirItemLista = findViewById(R.id.floatActionButtonExcluirItemLista);

        floatActionButtonExcluirItemLista.setVisibility(View.GONE);

        cdtListaPresenter = new CadastrarListaPresenter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idLista = extras.getString("id_lista", "");
        }

        if (idLista.equals("")) {
            idLista = "id_" + Calendar.getInstance().getTimeInMillis();
            carregarDadosListaJaCdt = false;
        } else {
            carregarDadosListaJaCdt = true;
        }

        listViewItensAdic.setAdapter(cdtListaPresenter.getAdapter());

        listViewItensAdic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cdtListaPresenter.onItemClickList(position, listViewItensAdic.getCheckedItemPositions());
                if ((vlPosItemSelecionado >= 0) && (vlPosItemSelecionado == position)) {
                    ocultarBotaoExcluirItemElimparSelecaoLista();
                } else {
                    listViewItensAdic.setSelector(R.color.azul_transp_70_perc);
                    vlPosItemSelecionado = position;
                    floatActionButtonExcluirItemLista.setVisibility(View.VISIBLE);
                }
            }
        });

        editVlLista.requestFocus();

        layoutTela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarBotaoExcluirItemElimparSelecaoLista();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editVlLista.getWindowToken(), 0);
            }
        });

        editVlLista.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    layoutTela.performClick();
                }
            }
        });

        bOkNomeLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdtListaPresenter.setNomeLista(editVlLista.getText().toString());
                tvVlLista.setText(cdtListaPresenter.getNomeLista());
                permitirAlterarNomeLista(false);
                layoutTela.performClick();
            }
        });

        if (carregarDadosListaJaCdt) {
            cdtListaPresenter.carregarDadosListaJaCdt(idLista);
        }
    }

    private void ocultarBotaoExcluirItemElimparSelecaoLista() {
        floatActionButtonExcluirItemLista.setVisibility(View.GONE);
        vlPosItemSelecionado = -1;
        listViewItensAdic.setSelector(android.R.color.transparent);
    }

    public void marcarDesmarcarItensCheckedNaListView(int pos) {
        if (cdtListaPresenter.getListDeItens() != null) {
            if (pos > -1) { //se quer de uma pos especifica
                listViewItensAdic.setItemChecked(pos, cdtListaPresenter.getListDeItens().get(pos).getCheckedItem());
            } else {
                for (int x = 0; x < cdtListaPresenter.getListDeItens().size(); x++) {
                    listViewItensAdic.setItemChecked(x, cdtListaPresenter.getListDeItens().get(x).getCheckedItem());
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void irTelaPrincipal() {
        finish(); //a tela de CadastrarLista foi chamada a partir da tela princ, entao a tela princ ja esta aberta por baixo dela no stack de atividades
    }

    public void alimentarNomeLista(String nome) {
        editVlLista.setText(nome);
        tvVlLista.setText(nome);
    }

    public void clickExcluirItemLista(View v) {
        try {
            cdtListaPresenter.excluirItemLista(vlPosItemSelecionado);
        } catch (Exception e) {
            Snackbar.make(layoutTela, getString(R.string.lbl_msgErrorRemoveItemList), Snackbar.LENGTH_LONG).show();
        }
        ocultarBotaoExcluirItemElimparSelecaoLista();
    }


    public void criarLista(View v) {
        ocultarBotaoExcluirItemElimparSelecaoLista();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editVlLista.getWindowToken(), 0);
        if (editVlLista.getText().toString().equals("")) {
            Snackbar.make(layoutTela, getString(R.string.lbl_msgEnterNameList), Snackbar.LENGTH_LONG).show();
        } else {
            try {
                if (cdtListaPresenter.criarLista(idLista, carregarDadosListaJaCdt)) {
                    Snackbar.make(layoutTela, getString(R.string.lbl_msgSucessCreateList), Snackbar.LENGTH_LONG).show();
                    irTelaPrincipal();
                } else {
                    Snackbar.make(layoutTela, getString(R.string.lbl_msgEnterItemList), Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Snackbar.make(layoutTela, getString(R.string.lbl_msgErrorSaveList), Snackbar.LENGTH_LONG).show();
            }

        }
    }

    private void excluirLista() {
        ocultarBotaoExcluirItemElimparSelecaoLista();
        if (carregarDadosListaJaCdt) { //se ja esta salvo no banco remover de la
            try {
                cdtListaPresenter.excluirLista(idLista);
                Snackbar.make(layoutTela, getString(R.string.lbl_msgSucessRemoveList), Snackbar.LENGTH_LONG).show();
                irTelaPrincipal();
            } catch (Exception e) {
                Snackbar.make(layoutTela, getString(R.string.lbl_msgErrorRemoveList), Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(layoutTela, getString(R.string.lbl_msgSucessRemoveList), Snackbar.LENGTH_LONG).show();
            irTelaPrincipal();
        }
    }

    private void permitirAlterarNomeLista(boolean opcao) {
        if (opcao) {
            tvVlLista.setVisibility(View.GONE);
            editVlLista.setVisibility(View.VISIBLE);
            bOkNomeLista.setVisibility(View.VISIBLE);
        } else {
            tvVlLista.setVisibility(View.VISIBLE);
            editVlLista.setVisibility(View.GONE);
            bOkNomeLista.setVisibility(View.GONE);
        }
    }

    public void mostrarDialogAdicItem(View v) {
        ocultarBotaoExcluirItemElimparSelecaoLista();

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View alertDialogView = li.inflate(R.layout.layout_adic_item, null);
        final AlertDialog alertDialogBuilder = new AlertDialog.Builder(CadastrarListaActivity.this).create();
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(alertDialogView);

        editVlItem      =  alertDialogView.findViewById(R.id.editVlItem);
        bAdicItem       =  alertDialogView.findViewById(R.id.bAdicionar);
        bCancelarItem   =  alertDialogView.findViewById(R.id.bCancelar);

        bAdicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editVlItem.getWindowToken(), 0);
                if (cdtListaPresenter.adicionarItemLista(editVlItem.getText().toString())) {
                    editVlItem.setText("");
                    alertDialogBuilder.dismiss();
                }
            }
        });

        bCancelarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.dismiss();
            }
        });


        alertDialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cadastrar_lista, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.criarLista) {
            criarLista(null);
        } else if (item.getItemId() == R.id.excluirLista) {
            excluirLista();
        } else if (item.getItemId() == R.id.alterarNomeLista) {
            permitirAlterarNomeLista(true);
        }
        return  true;
    }
}
