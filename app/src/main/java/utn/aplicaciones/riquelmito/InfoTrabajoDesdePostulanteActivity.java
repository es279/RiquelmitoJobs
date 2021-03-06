package utn.aplicaciones.riquelmito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import utn.aplicaciones.riquelmito.domain.AdministradorDeSesion;
import utn.aplicaciones.riquelmito.domain.DiasLaborales;
import utn.aplicaciones.riquelmito.domain.HorarioLaboral;
import utn.aplicaciones.riquelmito.domain.Rubro;
import utn.aplicaciones.riquelmito.domain.Suscripcion;
import utn.aplicaciones.riquelmito.domain.Trabajo;
import utn.aplicaciones.riquelmito.utilidades.AdministradorDeCargaDeInterfaces;

public class InfoTrabajoDesdePostulanteActivity extends AppCompatActivity {
    private ImageView ivInfoTrabPostulanteIconoTrabajo;
    private TextView tvInfoTrabPostulanteRubro;
    private TextView tvInfoTrabPostulanteCargo;
    private TextView tvInfoTrabPostulanteHorario;
    private TextView tvInfoTrabPostulanteSueldo;
    private TextView tvInfoTrabPostulanteDescripcion;
    private TextView tvInfoTrabPostulantePerfilEmpleado;
    private TextView tvInfoTrabPostulanteExperiencia;
    private Button btnInfoTrabPostulantePostular;

    private Trabajo trabajo;
    private Integer idProximaSuscripcion = -1;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_trabajo_desde_postulante);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarFirebase();

        //Cargar views
        ivInfoTrabPostulanteIconoTrabajo = findViewById(R.id.ivInfoTrabPostulanteIconoTrabajo);
        tvInfoTrabPostulanteRubro = findViewById(R.id.tvInfoTrabPostulanteRubro);
        tvInfoTrabPostulanteCargo = findViewById(R.id.tvInfoTrabPostulanteCargo);
        tvInfoTrabPostulanteHorario = findViewById(R.id.tvInfoTrabPostulanteHorario);
        tvInfoTrabPostulanteSueldo = findViewById(R.id.tvInfoTrabPostulanteSueldo);
        tvInfoTrabPostulanteDescripcion = findViewById(R.id.tvInfoTrabPostulanteDescripcion);
        tvInfoTrabPostulantePerfilEmpleado = findViewById(R.id.tvInfoTrabPostulantePerfilEmpleado);
        tvInfoTrabPostulanteExperiencia = findViewById(R.id.tvInfoTrabPostulanteExperiencia);
        btnInfoTrabPostulantePostular = findViewById(R.id.btnInfoTrabPostulantePostular);

        //Cargar detalles del trabajo
        Bundle bundleReseptor = getIntent().getExtras();
        if(bundleReseptor!=null)
            trabajo = (Trabajo) bundleReseptor.getSerializable("trabajo");

        if(trabajo!=null) {
            cargarDatosVistaDesdeTrabajo();

            databaseReference.child("Suscripcion").orderByChild("idTrabajo").equalTo(trabajo.getIdTrabajo()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Suscripcion suscripcion;

                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            suscripcion = ds.getValue(Suscripcion.class);

                            if(suscripcion.idPostulante.equals(AdministradorDeSesion.postulante.getIdPostulante())){
                                btnInfoTrabPostulantePostular.setVisibility(View.GONE);
                                return;
                            }
                        }

                        btnInfoTrabPostulantePostular.setVisibility(View.VISIBLE);
                    }
                    else{
                        btnInfoTrabPostulantePostular.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Obtiene el valor del ID de la suscripción que tendrá en caso de suscribirse a este Trabajo
            databaseReference.child("IdSuscripcion").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        idProximaSuscripcion = Integer.parseInt(dataSnapshot.child("valor").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void cargarDatosVistaDesdeTrabajo(){
        ivInfoTrabPostulanteIconoTrabajo.setImageResource(AdministradorDeCargaDeInterfaces.getIdIconoRubro(this,trabajo));
        tvInfoTrabPostulanteRubro.setText(AdministradorDeCargaDeInterfaces.getFilaRubro(this, trabajo));
        if(trabajo.getCargo()!=null)
            tvInfoTrabPostulanteCargo.setText(trabajo.getCargo());
        tvInfoTrabPostulanteHorario.setText(AdministradorDeCargaDeInterfaces.getFilaHorario(this,trabajo));
        tvInfoTrabPostulanteSueldo.setText(AdministradorDeCargaDeInterfaces.getFilaSalario(this,trabajo));
        if(trabajo.getDescripcionCargo()!=null)
            tvInfoTrabPostulanteDescripcion.setText(trabajo.getDescripcionCargo());
        if(trabajo.getPerfilEmpleado()!=null)
            tvInfoTrabPostulantePerfilEmpleado.setText(trabajo.getPerfilEmpleado());
        if(trabajo.getExperienciaEmpleado()!=null)
            tvInfoTrabPostulanteExperiencia.setText(trabajo.getExperienciaEmpleado());
    }


    public void goToSuscribirse(View view){
        if(idProximaSuscripcion==-1){
            Toast.makeText(this, "Espere hasta que la página inicie",Toast.LENGTH_LONG).show();
            return;
        }

        Suscripcion suscripcion = new Suscripcion( idProximaSuscripcion, trabajo.getIdTrabajo(), AdministradorDeSesion.postulante.getIdPostulante(), trabajo.getIdEmpleador() );
        registrarSuscripcion(suscripcion);
        databaseReference.child("IdSuscripcion").child("valor").setValue(idProximaSuscripcion+1);
    }

    private void registrarSuscripcion(Suscripcion suscripcion) {
        databaseReference.child("Suscripcion").child( suscripcion.getIdSuscripcion().toString() ).setValue(suscripcion);
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public  void goToConsultarUbicacionTrabDesdePostulante(View view){
        Intent intent = new Intent(this, ConsultarUbicacionActivity.class);

        Bundle bundle = new Bundle();
        bundle.putDouble("lat", trabajo.getLat());
        bundle.putDouble("lng", trabajo.getLng());

        intent.putExtras(bundle);

        startActivity(intent);
    }


    //Esta función permite que el botón de 'volver atrás' de la barra superior funcione
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
