package utn.aplicaciones.riquelmito.domain;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.NonNull;

import utn.aplicaciones.riquelmito.R;

public enum Rubro {
    ATENCION_AL_PUBLICO,COMUNICACIONES,CONSTRUCCION,ELECTRICIDAD,INFORMATICA,RRHH,SALUD,TRANSPORTE;

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case ATENCION_AL_PUBLICO:
                return AdministradorDeSesion.context.getString(R.string.rubro_atencion_al_publico);
            case COMUNICACIONES:
                return AdministradorDeSesion.context.getString(R.string.rubro_comunicaciones);
            case CONSTRUCCION:
                return AdministradorDeSesion.context.getString(R.string.rubro_construccion);
            case ELECTRICIDAD:
                return AdministradorDeSesion.context.getString(R.string.rubro_electricidad);
            case INFORMATICA:
                return AdministradorDeSesion.context.getString(R.string.rubro_informatica);
            case RRHH:
                return AdministradorDeSesion.context.getString(R.string.rubro_rrhh);
            case SALUD:
                return AdministradorDeSesion.context.getString(R.string.rubro_salud);
            case TRANSPORTE:
                return AdministradorDeSesion.context.getString(R.string.rubro_transporte);
            default:
                return AdministradorDeSesion.context.getString(R.string.desconocido);
        }

    }
}
