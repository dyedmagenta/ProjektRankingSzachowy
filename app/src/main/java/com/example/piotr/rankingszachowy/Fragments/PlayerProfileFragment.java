package com.example.piotr.rankingszachowy.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piotr.rankingszachowy.DBHelpers.UserDBHelper;
import com.example.piotr.rankingszachowy.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Piotr on 02.04.2017.
 */

public class PlayerProfileFragment extends Fragment {


    private Boolean isEditMode = false;
    private EditText etUsername;
    private EditText etAge;
    private EditText etRank;
    private EditText etLastPlayed;
    private EditText etPlayingSince;
    private Cursor result;
    private Spinner spinnerPlayer;
    UserDBHelper userDB;
    ImageButton btnEdit;
    private Integer RESULT_LOAD_IMG = 1;
    private ImageButton bttnProfilePic;
    private Uri picURI;

    ArrayList<String> nicks;

    //private LinearLayout linEditImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playerprofile, container, false);

        userDB = new UserDBHelper(getActivity());
        etUsername = (EditText) view.findViewById(R.id.etUsername);
        etAge = (EditText) view.findViewById(R.id.etAge);
        etRank = (EditText) view.findViewById(R.id.etRank);
        etLastPlayed = (EditText) view.findViewById(R.id.etLastPlayed);
        etPlayingSince = (EditText) view.findViewById(R.id.etPlayingSince);

        spinnerPlayer = (Spinner) view.findViewById(R.id.spinnerPlayer);


        nicks = new ArrayList<>();
        getNicks();

        final ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, nicks);
        spinnerPlayer.setAdapter(adapter2);

        btnEdit = (ImageButton) view.findViewById(R.id.btnEditProfile);
        bttnProfilePic = (ImageButton) view.findViewById(R.id.bttnProfilePicture);
        bttnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                isStoragePermissionGranted();
                profileOnClick();
            }
        });

        spinnerPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    clearEditText();
                }
                setUser((String) spinnerPlayer.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getAll();

        btnEdit = (ImageButton) view.findViewById(R.id.btnEditProfile);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEditMode(!isEditMode);
                if (isEditMode) {
                    boolean isInserted = userDB.insertData(etUsername.getText().toString(),
                            etRank.getText().toString(),
                            etLastPlayed.getText().toString(),
                            etPlayingSince.getText().toString(),
                            etAge.getText().toString(),
                            picURI.toString());
                    if (isInserted) {
                        Toast.makeText(getActivity(), "Added to database", Toast.LENGTH_SHORT).show();
                        getNicks();
                        adapter2.notifyDataSetChanged();
                        spinnerPlayer.setSelection(nicks.size());
                    }
                    else
                        Toast.makeText(getActivity(), "Derp to database", Toast.LENGTH_SHORT).show();
                }
                isEditMode = !isEditMode;
            }
        });


        CreateTables(view);


        return view;
    }

    private void clearEditText() {
        etUsername.setText("");
        etAge.setText("");
        etRank.setText("");
        etLastPlayed.setText("");
        etPlayingSince.setText("");
    }

    private void getNicks() {
        result = userDB.getAllData();
        nicks.clear();
        nicks.add("+ Add Player +");
        if (result.getCount() == 0) {
            Toast.makeText(getActivity(), "No records of users in database", Toast.LENGTH_SHORT).show();
        } else {
            while (result.moveToNext()) {
                nicks.add(result.getString(1));
            }
        }
    }

    private void setUser(String username) {
        result = userDB.getSpecificData(username);
        if (result.getCount() == 0) {
            Toast.makeText(getActivity(), "No records in database", Toast.LENGTH_SHORT).show();
        } else {
            result.moveToNext();
            etUsername.setText(result.getString(1));
            etRank.setText(result.getString(2));
            etLastPlayed.setText(result.getString(3));
            etPlayingSince.setText(result.getString(4));
            etAge.setText(result.getString(5));
            final InputStream imageStream;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(Uri.parse(result.getString(6)));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                bttnProfilePic.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),"No Avatar",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            profileOnClick();
        }
    }




    public void profileOnClick(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                bttnProfilePic.setImageBitmap(selectedImage);
                picURI = imageUri;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    private void getAll() {
        Cursor res = userDB.getAllData();
        if (res.getCount() == 0) {
            //Toast.makeText(getActivity(), "No records in database", Toast.LENGTH_SHORT).show();
        } else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                etUsername.setText(res.getString(1));
                etAge.setText(res.getString(5));
                etRank.setText(res.getString(2));
                etLastPlayed.setText(res.getString(3));
                etPlayingSince.setText(res.getString(4));

            }
        }
    }

    private void CreateTables(View view) {

//        TableLayout tblAchievements = (TableLayout) view.findViewById(R.id.tblAchivementsPlayerProfile);
//        TableLayout tblHistory = (TableLayout) view.findViewById(R.id.tblGamesHistoryPlayerProfile);

        TableRow tbrHeader = new TableRow(getActivity());

        TextView tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.tournamentHeader_fragment));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);

        tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.placementHeader_playerProfile_fragment));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);

//        tblAchievements.addView(tbrHeader);
//        tblAchievements.setStretchAllColumns(true);

        tbrHeader = new TableRow(getActivity());

        tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.playersHeader_fragment));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);

        tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.tournamentHeader_fragment));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);


        tv = new TextView(getActivity());
        tv.setText(view.getResources().getString(R.string.dateHeader_fragment));
        tv.setBackgroundColor(Color.LTGRAY);
        tbrHeader.addView(tv);

//        tblAchievements.addView(tbrHeader);
//        tblAchievements.setStretchAllColumns(true);


//        AddContent(tblAchievements, tblHistory);
    }

    public void setEditMode(Boolean editMode) {

//        etUsername.setFocusable(editMode);
//        etUsername.setClickable(editMode);
//
//        etRank.setFocusable(editMode);
//        etRank.setClickable(editMode);
//
//        etPlayingSince.setFocusable(editMode);
//        etPlayingSince.setClickable(editMode);
//
//        etLastPlayed.setFocusable(editMode);
//        etLastPlayed.setClickable(editMode);
//
//        etAge.setFocusable(editMode);
//        etAge.setClickable(editMode);
        if(editMode)
            btnEdit.setImageResource(R.drawable.ic_save);
        else
            btnEdit.setImageResource(R.drawable.ic_create);

    }
}
