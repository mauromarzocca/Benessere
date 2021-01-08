package com.interfacciabili.benessere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.interfacciabili.benessere.control.DatabaseService;
import com.interfacciabili.benessere.control.DietDBHelper;

import static android.content.Context.BIND_AUTO_CREATE;

public class EliminaClienteDialog extends AppCompatDialogFragment {

    private String utente;
    private TextView tvMessaggioElimina;

    public DatabaseService databaseService;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DatabaseService.LocalBinder localBinder = (DatabaseService.LocalBinder) service;
            databaseService = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_elimina_cliente, null);
        tvMessaggioElimina = view.findViewById(R.id.tvMessaggioEliminaCliente);
        if(!utente.isEmpty()){tvMessaggioElimina.append(utente);}


        if(savedInstanceState!=null){
            utente = savedInstanceState.getString("USERNAME");
            if(!utente.isEmpty()){tvMessaggioElimina.append(utente);}
        }

        builder.setView(view)
                .setTitle("Elimina cliente")
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseService.eliminaCliente(utente);

                        ((HomeDietologo) getActivity()).clienteCliccato = null;

                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intentDatabaseService = new Intent(getActivity(), DatabaseService.class);
        getActivity().startService(intentDatabaseService);
        getActivity().bindService(intentDatabaseService, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!utente.isEmpty()){outState.putString("USERNAME", utente);}
    }

    public void setUtente(String valore){
        utente = valore;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //((HomeDietologo) getActivity()).updateMasterFragment();
        //((HomeDietologo) getActivity()).updateClientDetailFragment("", R.layout.dettagli_cliente_blank);
        getActivity().unbindService(serviceConnection);
    }

}