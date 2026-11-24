package com.example.mylife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mylife.R;
import com.example.mylife.db.Sesion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * Pantalla principal: barra superior con el menú lateral a la DERECHA
 * (botón ☰) y barra de navegación inferior. Los dos menús están sincronizados.
 */
public class MenuPrincipalActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNav;
    private Sesion sesion;

    // Título de la pestaña abierta (para restaurarlo al cerrar "Acerca de")
    private String tituloActual = "MyLife";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sesion = new Sesion(this);

        // Sin sesión no se puede estar aquí
        if (!sesion.activa()) {
            irAlInicio();
            return;
        }

        setContentView(R.layout.activity_menuprincipal);

        // Que la barra superior no quede debajo de la barra de estado
        View contenido = findViewById(R.id.contenido);
        ViewCompat.setOnApplyWindowInsetsListener(contenido, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, barras.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNav = findViewById(R.id.bottom_navigation);

        actualizarEncabezado();

        // Navegación inferior
        bottomNav.setOnItemSelectedListener(item -> {
            mostrarSeccion(item.getItemId());
            return true;
        });

        // Tocar de nuevo la pestaña actual cierra lo que esté encima ("Acerca de")
        bottomNav.setOnItemReselectedListener(item -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                mostrarSeccion(item.getItemId());
            }
        });

        // Menú lateral (derecha)
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.END);
            return alElegirEnMenuLateral(item.getItemId());
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                ponerTitulo(tituloActual);
            }
        });

        // El botón atrás primero cierra el menú lateral, luego "Acerca de"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Mostrar Inicio al abrir
        if (savedInstanceState == null) {
            mostrarSeccion(R.id.nav_inicio);
        }
    }

    // ====================== BOTÓN ☰ (DERECHA) ======================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_menu) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ====================== SECCIONES ======================

    private void mostrarSeccion(int id) {

        Fragment fragment;
        String titulo;

        if (id == R.id.nav_inicio) {
            fragment = new InicioFragment();
            titulo = "MyLife";
        } else if (id == R.id.nav_tareas) {
            fragment = new TareasFragment();
            titulo = "Tareas";
        } else if (id == R.id.nav_habitos) {
            fragment = new HabitosFragment();
            titulo = "Hábitos";
        } else if (id == R.id.nav_metas) {
            fragment = new MetasFragment();
            titulo = "Metas";
        } else if (id == R.id.nav_perfil) {
            fragment = new PerfilFragment();
            titulo = "Mi perfil";
        } else {
            return;
        }

        // Al cambiar de pestaña se cierra lo que estuviera encima
        getSupportFragmentManager().popBackStack(
                null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        mostrarFragment(fragment, titulo, false);
        navigationView.setCheckedItem(id);
    }

    private boolean alElegirEnMenuLateral(int id) {

        if (id == R.id.nav_inicio || id == R.id.nav_tareas
                || id == R.id.nav_habitos || id == R.id.nav_metas
                || id == R.id.nav_perfil) {

            if (bottomNav.getSelectedItemId() == id) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    mostrarSeccion(id);
                }
            } else {
                bottomNav.setSelectedItemId(id);
            }
            return true;
        }

        if (id == R.id.nav_acerca) {
            abrirAcerca();
            return false;
        }

        if (id == R.id.nav_cerrar_sesion) {
            cerrarSesion();
            return false;
        }

        if (id == R.id.nav_salir) {
            finishAffinity();
            return false;
        }

        return false;
    }

    private void mostrarFragment(Fragment fragment, String titulo,
                                 boolean agregarAPila) {

        FragmentTransaction t = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (agregarAPila) {
            t.addToBackStack(null);
        } else {
            tituloActual = titulo;
        }

        t.commit();
        ponerTitulo(titulo);
    }

    private void ponerTitulo(String titulo) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }
    }


    public void abrirAcerca() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mostrarFragment(new AcercaFragment(), "Acerca de", true);
        }
    }

    // Actualiza el nombre y el correo que se ven en el menú lateral
    public void actualizarEncabezado() {

        View cabecera = navigationView.getHeaderView(0);
        TextView nombre = cabecera.findViewById(R.id.txt_nav_nombre);
        TextView correo = cabecera.findViewById(R.id.txt_nav_correo);

        String n = sesion.getNombre();
        nombre.setText(n.isEmpty() ? "Tu nombre" : n);
        correo.setText(sesion.getCorreo());
    }

    public void cerrarSesion() {

        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar tu sesión?")
                .setPositiveButton("Sí, cerrar", (dialog, which) -> {
                    sesion.cerrar();
                    irAlInicio();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void irAlInicio() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
