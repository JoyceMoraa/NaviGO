package com.example.navigo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText loginUsername, loginPass;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private TextView txtStatus;
    private TextView txtDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // inflate the tool bar
        //Toolbar toolbar = findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);
        //setToolbarTitle("");
        //Initialize the views

        Button loginBtn = findViewById(R.id.btnRegister);

        loginUsername = (EditText) findViewById(R.id.inputUsername) ;

        //loginEmail =findViewById(R.id.login_email);
        loginPass = findViewById(R.id.inputPassword);

        //Initialize the Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        //Initialize the database reference where you have the child node Users
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        //if user is not registered , register him/her



        //Set on click listener on the login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, R.string.processing, Toast.LENGTH_LONG).show();
                // get the email and password entered by the user
                String username = loginUsername.getText().toString().trim();
                String password = loginPass.getText().toString().trim();

                if (!TextUtils.isEmpty(username)&& !TextUtils.isEmpty(password)){
                    // use firebase authentication instance you create and call the method signInWithEmailAndPassword method passing the email and password you got from the views
                    //Further call the addOnCompleteListener() method to handle the Authentication result
                    mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //create a method that will check if the user exists in our database reference
                                checkUserExistence();
                            }else {
                                //if the user does not exit in the database reference throw a toast
                                Toast.makeText(LoginActivity.this, R.string.could_not_login, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    //if the fields for email and password where not completed show a toast
                    Toast.makeText(LoginActivity.this, R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //check if the user exists
    public void checkUserExistence(){
        //check the user existence of the user using the user id in users database reference
        final String user_id = mAuth.getCurrentUser().getUid();
        //call the method addValueEventListener on the database reference of the user to determine if the current userID supplied exists in our database reference
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get a dataSnapshot of the users database reference to determine if current user exists


                if (dataSnapshot.hasChild(user_id)){
                    //if the users exists direct the user to the <Main> Activity

                    mDatabaseUsers.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            Intent mainPage = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainPage);
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }else {
                    //if the user id does not exist show a toast
                    Toast.makeText(LoginActivity.this, R.string.not_registered, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent myIntent=new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(myIntent);
        finish();
        super.onBackPressed();  // optional

    }
    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }
}