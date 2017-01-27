package com.example.a47276138y.todoapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> adapter;
    private static final int RC_SIGN_IN = 123;
    private TextView textView;
    private DatabaseReference myRef;
    private EditText et_input;



    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> todos = new ArrayList<>();

        ListView lvTodos = (ListView) view.findViewById(R.id.lv_todos);
        Button btAdd = (Button) view.findViewById(R.id.button);
        textView = (TextView) view.findViewById(R.id.textView);
        et_input = (EditText) view.findViewById(R.id.et_input);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("todos");

        setupAuth();

        adapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.lv_todo_row,
                R.id.textView,
                todos);

        lvTodos.setAdapter(adapter);
        btAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String txt = et_input.getText().toString();
                Log.d("XXXXXXXX", txt);

                if(!txt.equals("")){
                    DatabaseReference child = myRef.push();
                    child.setValue(txt);
                }
            }
        });


        myRef.setValue("Hello, World!");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // String value = dataSnapshot.getValue(String.class);
                // Log.w("OnDataChanged", "Value is: " + value);
                adapter.clear();

                for (DataSnapshot values : dataSnapshot.getChildren()) {
                    adapter.add(values.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Failed to read value", databaseError.toException());
            }
        });


        return view;

    }


    private void setupAuth() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            Log.d("Current user", String.valueOf(auth.getCurrentUser()));

        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                            )
                            .build(),
                    RC_SIGN_IN);}
    }
}
