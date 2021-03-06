package utn.aplicaciones.riquelmito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import utn.aplicaciones.riquelmito.DTO.DatosTrabajosCercanosAUsuarioDTO;
import utn.aplicaciones.riquelmito.domain.AdministradorDeSesion;
import utn.aplicaciones.riquelmito.domain.Rubro;
import utn.aplicaciones.riquelmito.domain.Trabajo;
import utn.aplicaciones.riquelmito.utilidades.ConexionSQLiteHelper;

public class BuscarTrabajoActivity extends AppCompatActivity {

    private Spinner spnBuscTrabRubro;
    private SeekBar sbBuscTrabDistancia;
    private TextView tvBuscTrabDistancia;
    private Spinner spnBuscTrabSalario;

    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_trabajo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spnBuscTrabRubro = findViewById(R.id.spnBuscTrabRubro);
        spnBuscTrabRubro.setAdapter(new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, Rubro.values() ));
        sbBuscTrabDistancia = findViewById(R.id.sbBuscTrabDistancia);
        tvBuscTrabDistancia = findViewById(R.id.tvBuscTrabDistancia);
        spnBuscTrabSalario = findViewById(R.id.spnBuscTrabSalario);

        sbBuscTrabDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                tvBuscTrabDistancia.setText(progress + " Km");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //Carga últimos valores buscados
        conn = new ConexionSQLiteHelper(getApplicationContext(), "bd_usuarios", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();

        int longitud=0;
        Cursor cursor = db.rawQuery("SELECT longitud FROM DISTANCIA", null);
        if(cursor.moveToFirst()){
            longitud= cursor.getInt(0);
            sbBuscTrabDistancia.setProgress(cursor.getInt(0));
        }
        else{
            Toast.makeText(getApplicationContext(),"Perdedor!!!", Toast.LENGTH_LONG).show();
        }

        db.close();

    }

    public void actualizarDistancia(){
        int longitud = sbBuscTrabDistancia.getProgress();
        SQLiteDatabase dbb = conn.getWritableDatabase();
        dbb.execSQL("UPDATE DISTANCIA SET longitud="+longitud+" WHERE id=123");
        dbb.close();
    }

    public void buscarTrabajo(View view){

        actualizarDistancia();

        Intent intent = new Intent(this, TrabajosCercanosAUsuarioActivity.class);

        DatosTrabajosCercanosAUsuarioDTO parametros = new DatosTrabajosCercanosAUsuarioDTO();
        Double longitud = Double.valueOf(sbBuscTrabDistancia.getProgress());
        parametros.setRadioDeBusqueda(longitud);
        parametros.setRubro((Rubro) spnBuscTrabRubro.getSelectedItem());
        parametros.setSalario(Integer.valueOf(spnBuscTrabSalario.getSelectedItem().toString()));

        //Pasa los parámetros de búsuqeda a siguiente actividad
        Bundle bundle = new Bundle();
        bundle.putSerializable("parametrosBusqueda",parametros);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    //Esta función permite que el botón de 'volver atrás' de la barra superior funcione
    public boolean onOptionsItemSelected(MenuItem item) {

        actualizarDistancia();

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
