
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

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre, txtCorreo;
    private EditText txtContrasena, txtConfirmarContrasena;

    private Button btnCrearCuenta;
    private TextView txtIniciarSesion;

    private Base baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        baseDatos = new Base(this);

        // Relacionar elementos del XML
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        txtConfirmarContrasena =
                findViewById(R.id.txtConfirmarContrasena);

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        txtIniciarSesion = findViewById(R.id.txtIniciarSesion);

        // Crear cuenta
        btnCrearCuenta.setOnClickListener(v -> registrarUsuario());

        // Regresar al login
        txtIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegistroActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
            finish();
        });
    }

    private void registrarUsuario() {

        String nombre = txtNombre.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString();
        String confirmar = txtConfirmarContrasena.getText().toString();

        // Validar campos vacíos
        if (nombre.isEmpty() || correo.isEmpty()
                || contrasena.isEmpty() || confirmar.isEmpty()) {

            Toast.makeText(this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato de correo
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(correo).matches()) {

            txtCorreo.setError("Ingresa un correo válido");
            return;
        }

        // Contraseña mínima
        if (contrasena.length() < 6) {

            txtContrasena.setError("Usa al menos 6 caracteres");
            return;
        }

        // Comprobar que las contraseñas coincidan
        if (!contrasena.equals(confirmar)) {

            txtConfirmarContrasena.setError(
                    "Las contraseñas no coinciden"
            );
            return;
        }

        // Guardar usuario en SQLite
        long resultado = baseDatos.registrarUsuario(
                nombre,
                correo,
                contrasena
        );

        if (resultado != -1) {

            Toast.makeText(this,
                    "¡Cuenta creada correctamente!",
                    Toast.LENGTH_LONG).show();

            // Ir al login
            Intent intent = new Intent(
                    RegistroActivity.this,
                    LoginActivity.class
            );

            intent.putExtra("correo", correo);

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(this,
                    "No se pudo crear la cuenta. Verifica si el correo ya está registrado.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
