package com.stomas.appfirebase;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtId, txtNombre, txtNombreComprador, txtPrecio;
    private ListView Lista;
    private Spinner spBicicleta;
    private FirebaseFirestore db;
    String[] tipobicicleta = {"Montaña", "Híbrida", "Eléctrica", "Ruta"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos Firestore antes de cualquier llamada a métodos que dependan de esta instancia
        db = FirebaseFirestore.getInstance();

        // Enlazar variables a los componentes del XML
        txtId = findViewById(R.id.txtId);
        txtNombre = findViewById(R.id.txtNombre);
        txtNombreComprador = findViewById(R.id.txtNombreComprador);
        txtPrecio = findViewById(R.id.txtPrecio);
        spBicicleta = findViewById(R.id.spBicicleta);
        Lista = findViewById(R.id.lista);

        // Configurar Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipobicicleta);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBicicleta.setAdapter(adapter);

        // Cargar la lista desde Firestore
        CargarListaFireStore();
    }

    public void enviarDatosFirestore(View view) {
        String id = txtId.getText().toString();
        String nombre = txtNombre.getText().toString();
        String nombreComprador = txtNombreComprador.getText().toString();
        String precio = txtPrecio.getText().toString();
        String tipoBicicleta = spBicicleta.getSelectedItem().toString();

        Map<String, Object> bicicleta = new HashMap<>();
        bicicleta.put("id", id);
        bicicleta.put("nombre", nombre);
        bicicleta.put("nombrecomprador", nombreComprador);
        bicicleta.put("precio", precio);
        bicicleta.put("tipobicicleta", tipoBicicleta);

        db.collection("bicicletas")
                .document(id)
                .set(bicicleta)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Datos enviados", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show());
    }

    public void CargarListaFireStore() {
        db.collection("bicicletas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listaBicicletas = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String linea = "ID: " + document.getString("id") +
                                        " | Nombre: " + document.getString("nombre") +
                                        " | Comprador: " + document.getString("nombrecomprador") +
                                        " | Precio: " + document.getString("precio") +
                                        " | Tipo: " + document.getString("tipobicicleta");
                                listaBicicletas.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listaBicicletas);
                            Lista.setAdapter(adaptador);
                        } else {
                            Toast.makeText(MainActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void CargarLista(View view) {
        CargarListaFireStore();
    }
}
