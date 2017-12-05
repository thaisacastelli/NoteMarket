package com.br.notemarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity implements
        View.OnClickListener {

    private TextView tvStatus;
    private EditText editVlEmail;
    private EditText editVlSenha;
    private Button   bEntrar;
    private Button   bSair;
    private Button   bCriarConta;
    private Button   bVerificarEmail;
    private ConstraintLayout layoutTela;

    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "PrincipalAuth";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvStatus = findViewById(R.id.tvStatus);
        layoutTela = findViewById(R.id.layoutTela);
        editVlEmail = findViewById(R.id.editVlEmail);
        editVlSenha = findViewById(R.id.editVlSenha);

        bEntrar = findViewById(R.id.buttonEntrar);
        bCriarConta = findViewById(R.id.buttonCriarConta);
        bSair = findViewById(R.id.buttonSairSessao);
        bVerificarEmail = findViewById(R.id.buttonVerificarEmail);

        layoutTela.setOnClickListener(this);
        bEntrar.setOnClickListener(this);
        bCriarConta.setOnClickListener(this);
        bSair.setOnClickListener(this);
        bVerificarEmail.setOnClickListener(this);

        PrincipalActivity.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        FirebaseUser currentUser = PrincipalActivity.mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void  createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        PrincipalActivity.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            tvStatus.setText(getString(R.string.user_create));
                            Snackbar.make(layoutTela, getString(R.string.user_create), Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                tvStatus.setText(getString(R.string.verification_email_account));
                                Snackbar.make(layoutTela, getString(R.string.verification_email_account), Snackbar.LENGTH_SHORT).show();
                            } else {
                                tvStatus.setText(getString(R.string.auth_failed));
                                Snackbar.make(layoutTela, getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        hideProgressDialog();
                    }
                });

    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        PrincipalActivity.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            tvStatus.setText("");
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = PrincipalActivity.mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                updateUI(user);
                            } else {
                                tvStatus.setText(getString(R.string.sending_verification_email));
                                sendEmailVerification();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(layoutTela, getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                            tvStatus.setText(R.string.auth_failed);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        PrincipalActivity.mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        bVerificarEmail.setEnabled(false);

        final FirebaseUser user = PrincipalActivity.mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        bVerificarEmail.setEnabled(true);

                        if (task.isSuccessful()) {
                            Snackbar.make(layoutTela, getString(R.string.sent_verification_to)  + user.getEmail(), Snackbar.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            Snackbar.make(layoutTela, getString(R.string.sent_verification_failed), Snackbar.LENGTH_SHORT).show();
                            updateUI(user);
                            tvStatus.setText(getString(R.string.sent_verification_failed));
                        }

                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editVlEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editVlEmail.setError(getString(R.string.required_field));
            valid = false;
        } else {
            editVlEmail.setError(null);
        }

        String password = editVlSenha.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editVlSenha.setError(getString(R.string.required_field));
            valid = false;
        } else {
            editVlSenha.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            if (!user.isEmailVerified()) {
                tvStatus.setText(getString(R.string.msg_verification_email_account_1) + user.getEmail() + "\n"
                        + getString(R.string.msg_verification_email_account_2)  + "\n"
                        + getString(R.string.msg_verification_email_account_3));

                bCriarConta.setVisibility(View.GONE);
                bEntrar.setVisibility(View.GONE);
                editVlEmail.setVisibility(View.INVISIBLE);
                editVlSenha.setVisibility(View.INVISIBLE);
                bSair.setVisibility(View.VISIBLE);
                bVerificarEmail.setVisibility(View.VISIBLE);
            } else {
               //Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
               //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
               //startActivity(i);
               finish();
            }
        } else {
            tvStatus.setText(R.string.signed_out);

            bCriarConta.setVisibility(View.VISIBLE);
            bEntrar.setVisibility(View.VISIBLE);
            editVlEmail.setVisibility(View.VISIBLE);
            editVlSenha.setVisibility(View.VISIBLE);
            bSair.setVisibility(View.GONE);
            bVerificarEmail.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonCriarConta) {
            createAccount(editVlEmail.getText().toString(), editVlSenha.getText().toString());
        } else if (i == R.id.buttonEntrar) {
            signIn(editVlEmail.getText().toString(), editVlSenha.getText().toString());
        } else if (i == R.id.buttonSairSessao) {
            signOut();
        } else if (i == R.id.buttonVerificarEmail) {
            sendEmailVerification();
        } else if (i == R.id.layoutTela) {
            //ocultar teclado aberto qdo se clicou em algum dos edits
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editVlEmail.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}