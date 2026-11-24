package com.example.mylife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylife.R;
import com.example.mylife.db.Sesion;

/**
 * Pantalla de bienvenida. Si ya hay una sesión iniciada,
 * pasa directo a la app.
 */
public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sesión ya iniciada: no hace falta volver a pedir el login
        if (new Sesion(this).activa()) {
            startActivity(new Intent(this, MenuPrincipalActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);

        // Ir al inicio de sesión
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        // Ir al registro
        btnRegistro.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegistroActivity.class)));
    }
}
