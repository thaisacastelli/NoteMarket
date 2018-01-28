package com.br.notemarket.presenter;


import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.br.notemarket.activity.CadastrarListaActivity;
import com.br.notemarket.activity.PrincipalActivity;
import com.br.notemarket.model.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CadastrarListaPresenter {

    private final CadastrarListaActivity cdtListaActivity;
    private DatabaseReference mDatabase;
    private String nomeLista = "";
    private List<String> listItensAdicionados;
    private List<Boolean> listCheckedItensAdicionados;
    private List<Item> listDeItens;
    private Item obItem;
    private ArrayAdapter<Item> adapter;

    public CadastrarListaPresenter(CadastrarListaActivity cdtListaActivity) {
        this.cdtListaActivity = cdtListaActivity;

        inicializar();
    }

    private void inicializar() {
        //database reference pointing to root of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listItensAdicionados = new ArrayList<>();
        listCheckedItensAdicionados = new ArrayList<>();
        listDeItens = new ArrayList<Item>();

        adapter = new ArrayAdapter<Item>(cdtListaActivity, android.R.layout.simple_list_item_multiple_choice, listDeItens) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                CheckedTextView text = (CheckedTextView) view.findViewById(android.R.id.text1); //nome do CheckedTextView no layout simple_list_item_multiple_choice

                //marcar e desabilitar as pos ja selecionadas, pois se ja salvas nao podem ser desmarcadas, so se clicar em Limpar
                if (listDeItens.get(position).getCheckedItem()) {
                    text.setChecked(true);
                } else {
                    text.setChecked(false);
                }

                return view;
            }

        };

    }

    public void carregarDadosListaJaCdt(String idLista) {

        //addListenerForSingleValueEvent executado apenas 1 vez ao ser criado o listener
        mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class); //tipo do retorno
                nomeLista = value;
                cdtListaActivity.alimentarNomeLista(nomeLista);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                nomeLista = "";
                cdtListaActivity.alimentarNomeLista("");
            }
        });


        mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).child("itens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItensAdicionados.clear();
                listCheckedItensAdicionados.clear();
                listDeItens.clear();
                for (DataSnapshot child_item: dataSnapshot.getChildren()) { //percorrer todos os nos
                    if (child_item.getKey().equals("descricao")) {
                        for (DataSnapshot child : child_item.getChildren()) { //percorre valores dentro desse no 'descricao'
                            listItensAdicionados.add(child.getValue().toString());
                        }
                    } else if (child_item.getKey().equals("checked")) {
                        for (DataSnapshot child : child_item.getChildren()) {//percorre valores dentro desse no 'checked'
                            listCheckedItensAdicionados.add(child.getValue().toString().equals("T")?true:false);
                        }
                    }
                }

                for (int x= 0; x < listItensAdicionados.size(); x++) {
                    obItem = new Item();
                    obItem.setDescItem(listItensAdicionados.get(x));
                    if (x < listCheckedItensAdicionados.size()) {
                        obItem.setCheckedItem(listCheckedItensAdicionados.get(x));
                    } else {
                        obItem.setCheckedItem(false);
                    }
                    listDeItens.add(obItem);
                }

                adapter.notifyDataSetChanged();

                cdtListaActivity.marcarDesmarcarItensCheckedNaListView(-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void excluirItemLista(int posItemSelec) {
        if (posItemSelec >= 0) {
            listDeItens.get(posItemSelec).setCheckedItem(false);
            cdtListaActivity.marcarDesmarcarItensCheckedNaListView(posItemSelec);
            listDeItens.remove(posItemSelec);
            adapter.notifyDataSetChanged();
        }
    }

    public boolean adicionarItemLista(String vlItem) {
        if (!vlItem.equals("")) {
            obItem = new Item();
            obItem.setDescItem(vlItem);
            obItem.setCheckedItem(false);
            listDeItens.add(obItem);
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean criarLista(String idLista, boolean listaJaCdt) {
        if ((listDeItens != null) && (listDeItens.size() > 0)) {
            mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).child("nome").setValue(nomeLista);
            //se a lista ja estava cdt e esta editando, remover itens antes de adic novamente
            if (listaJaCdt) {
                mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).child("itens").removeValue();
            }
            //gravar itens na lista
            for (int x = 0; x < listDeItens.size(); x++) {
                mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).child("itens").child("descricao").push().setValue(listDeItens.get(x).getDescItem());
                mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).child("itens").child("checked").push().setValue(listDeItens.get(x).getCheckedItem()?"T":"F");
            }
            return true;
        } else {
            return false;
        }
    }

    public void excluirLista(String idLista) {
        mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).child(idLista).removeValue();
    }

    public void onItemClickList(int position, SparseBooleanArray sparseBooleanArray) {
        if (listDeItens != null && listDeItens.size() > 0) {
            if (sparseBooleanArray.get(position)) { //true
                listDeItens.get(position).setCheckedItem(true);
            } else {
                listDeItens.get(position).setCheckedItem(false);
            }
        }
    }

    public String getNomeLista(){
        return nomeLista;
    }

    public void setNomeLista(String vlNome) {
        nomeLista = vlNome;
    }

    public List<Item> getListDeItens() {return  listDeItens; }

    public ArrayAdapter<Item> getAdapter() {return adapter; }

}
