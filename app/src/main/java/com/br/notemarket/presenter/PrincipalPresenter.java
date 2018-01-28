package com.br.notemarket.presenter;


import android.content.Intent;
import android.widget.ArrayAdapter;

import com.br.notemarket.activity.CadastrarListaActivity;
import com.br.notemarket.activity.PrincipalActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrincipalPresenter {

    private final PrincipalActivity principalActivity;
    private DatabaseReference mDatabase;
    private List<String> listListasJaCdt;
    private List<String> idlistListasJaCdt;
    public  ArrayAdapter<String> adapter;

    public PrincipalPresenter(PrincipalActivity princActivity) {
        this.principalActivity = princActivity;

        inicializar();
    }

    private void inicializar() {
        //database reference pointing to root of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listListasJaCdt   = new ArrayList<>();
        idlistListasJaCdt = new ArrayList<>();
        adapter = new ArrayAdapter<>(principalActivity, android.R.layout.simple_list_item_1, listListasJaCdt);
    }

    public void onItemClickListaJaCdt(int position) {
        if (principalActivity != null) {
            Intent c = new Intent(principalActivity, CadastrarListaActivity.class);
            c.putExtra("id_lista", idlistListasJaCdt.get(position));
            principalActivity.startActivity(c);
        }
    }

    public void obterNomeListasJaCadastradas() {
        if (principalActivity != null) {
            mDatabase.child("app_notemarket").child("idListasCdt").child(PrincipalActivity.mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listListasJaCdt.clear();
                    idlistListasJaCdt.clear();
                    //para cada usuario pode ter n listas, cada lista tem um id e dentro de cada lista tem o nome e os itens
                    for (DataSnapshot child_id : dataSnapshot.getChildren()) {
                        idlistListasJaCdt.add(child_id.getKey());
                        for (DataSnapshot child : child_id.getChildren()) {
                            if (child.getKey().equals("nome")) {
                                listListasJaCdt.add(child.getValue().toString());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void limparListas() {
        listListasJaCdt.clear();
        adapter.notifyDataSetChanged();
    }

    public List<String> getListListasJaCdt() {
        return listListasJaCdt;
    }

    /*public List<String> getIdlistListasJaCdt() {
        return idlistListasJaCdt;
    }*/

}
