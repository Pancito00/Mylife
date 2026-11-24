package com.example.mylife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylife.R;
import com.example.mylife.db.Base;
import com.example.mylife.db.Sesion;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText txtCorreo, txtContrasena;
    private Button btnIniciarSesion;
    private TextView txtRegistrarse;

    private Base baseDatos;
    private Sesion sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sesion = new Sesion(this);

        // Si ya existe una sesión, se entra directo a la app
        if (sesion.activa()) {
            abrirMenuPrincipal();
            return;
        }

        setContentView(R.layout.activity_login);

        // Conectar con SQLite
        baseDatos = new Base(this);

        // Relacionar elementos del XML
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        txtRegistrarse = findViewById(R.id.txtRegistrarse);

        // Si venimos del registro, el correo ya aparece escrito
        String correoNuevo = getIntent().getStringExtra("correo");
        if (correoNuevo != null) {
            txtCorreo.setText(correoNuevo);
            txtContrasena.requestFocus();
        }

        // Iniciar sesión
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        // Ir al registro
        txtRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            finish();
        });
    }

    private void iniciarSesion() {

        String correo = txtCorreo.getText().toString().trim()
                .toLowerCase(Locale.ROOT);
        String contrasena = txtContrasena.getText().toString();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar los datos en SQLite
        if (!baseDatos.validarUsuario(correo, contrasena)) {
            Toast.makeText(this, "Correo o contraseña incorrectos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = baseDatos.obtenerIdUsuario(correo);
        String nombre = baseDatos.obtenerNombreUsuario(correo);

        // Guardar sesión
        sesion.iniciar(idUsuario, nombre == null ? "" : nombre, correo);

        Toast.makeText(this, "¡Bienvenido a MyLife!",
                Toast.LENGTH_SHORT).show();

        abrirMenuPrincipal();
    }

    private void abrirMenuPrincipal() {

        Intent intent = new Intent(this, MenuPrincipalActivity.class);

        // Limpia las pantallas anteriores (bienvenida, login, registro)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
