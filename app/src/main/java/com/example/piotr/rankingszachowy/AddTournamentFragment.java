package com.example.piotr.rankingszachowy;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Piotr on 02.04.2017.
 */

public class AddTournamentFragment extends Fragment {

    TournamentDBHelper tournamentDBHelper;
    EditText etName;
    EditText etDesc;
    EditText etStarts;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addtournament, container, false);

        tournamentDBHelper = new TournamentDBHelper(getActivity());
        etName = (EditText) view.findViewById(R.id.editText4);
        etDesc = (EditText) view.findViewById(R.id.editText5);
        etStarts = (EditText) view.findViewById(R.id.editText7);
        CreateTables(view);

        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirmTournament);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isInserted = tournamentDBHelper.insertData(etName.getText().toString(),
                        etDesc.getText().toString(),
                        etStarts.getText().toString());
                if (isInserted) {
                    Toast.makeText(getActivity(), "Added to database", Toast.LENGTH_SHORT).show();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    Fragment tournamentsFragment = new CurrentTournamentsFragment();

                    transaction.replace(R.id.fragmentContainer, tournamentsFragment);

                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }
                else
                    Toast.makeText(getActivity(),"Derp to database",Toast.LENGTH_SHORT).show();


            }
        });
        return view;
    }

    private void CreateTables(View view){

        TableLayout tblInvites = (TableLayout) view.findViewById(R.id.tblPlayerInvitesCreateTournament);

        //Setting up Open Tournaments Rows
        TableRow tbrHeader = new TableRow(getActivity());

        TextView tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.playerInvited_header));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);

        tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.playerConfirmed_header_createTournament));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);

        tblInvites.addView(tbrHeader);
        tblInvites.setStretchAllColumns(true);

        AddContent(tblInvites);
    }

    //Used for filling tables with crap
    private void AddContent(TableLayout table){

        TableRow row;
        TextView tv;
        CheckBox cb;
        Random rng = new Random();

        for (int i = 0; i < 15; i++) {
            row = new TableRow(getActivity());

            tv = new TextView(getActivity());
            tv.setText("Row " + i + ": " + rng.nextInt(300));
            cb = new CheckBox(getActivity());
            cb.setText("");
            row.addView(tv);
            row.addView(cb);

            table.addView(row);
        }
    }
}
