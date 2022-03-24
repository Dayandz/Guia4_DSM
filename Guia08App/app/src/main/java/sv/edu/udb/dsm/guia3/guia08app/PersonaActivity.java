package sv.edu.udb.dsm.guia3.guia08app;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import sv.edu.udb.dsm.guia3.guia08app.Persona;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PersonaActivity extends AppCompatActivity {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference refPersonas = database.getReference("personas");


    Query consultaOrdenada = refPersonas.orderByChild("nombre");

    List<Persona> personas;
    ListView listaPersonas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona);
        inicializar();
    }
    private void inicializar() {
        FloatingActionButton fab_agregar= findViewById(R.id.fab_agregar);
        listaPersonas = findViewById(R.id.ListaPersonas);


        listaPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), AddPersonaActivity.class);

                intent.putExtra("accion","e"); // Editar
                intent.putExtra("key", personas.get(i).getKey());
                intent.putExtra("nombre",personas.get(i).getNombre());
                intent.putExtra("dui",personas.get(i).getDui());
                startActivity(intent);
            }
        });
        listaPersonas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                // Preparando cuadro de dialogo para preguntar al usuario
                // Si esta seguro de eliminar o no el registro
                AlertDialog.Builder ad = new AlertDialog.Builder(PersonaActivity.this);
                ad.setMessage("Está seguro de eliminar registro?")
                        .setTitle("Confirmación");

                ad.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PersonaActivity.refPersonas
                                .child(personas.get(position).getKey()).removeValue();

                        Toast.makeText(PersonaActivity.this,
                                "Registro borrado!",Toast.LENGTH_SHORT).show();
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(PersonaActivity.this,
                                "Operación de borrado cancelada!",Toast.LENGTH_SHORT).show();
                    }
                });

                ad.show();
                return true;
            }
        });

        fab_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getBaseContext(), AddPersonaActivity.class);
                i.putExtra("accion","a"); // Agregar
                i.putExtra("key","");
                i.putExtra("nombre","");
                i.putExtra("dui","");
                startActivity(i);
            }
        });
        personas = new ArrayList<>();

        consultaOrdenada.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Procedimiento que se ejecuta cuando hubo algun cambio
                // en la base de datos
                // Se actualiza la coleccion de personas
                personas.removeAll(personas);
                for (DataSnapshot dato : dataSnapshot.getChildren()) {
                    Persona persona = dato.getValue(Persona.class);
                    persona.setKey(dato.getKey());
                    personas.add(persona);
                }

                AdaptadorPersona adapter = new AdaptadorPersona(PersonaActivity.this, personas );
                listaPersonas.setAdapter(adapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}