package com.example.crimelocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpAct extends AppCompatActivity {
    Button registerBtn;
    TextView signInBtn;
    Boolean usernameDone=Boolean.FALSE, emailDone=Boolean.FALSE, numberDone=Boolean.FALSE,co, passwordDone=Boolean.FALSE;
    ProgressBar signupProgBar;

    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String Email = "don@gmail.com", Password = "don@19", Username="tariq";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();

        TextInputLayout usernameLayout =findViewById(R.id.usernameLayout);
        TextInputLayout emailLayout=findViewById(R.id.emailLayout);
        TextInputLayout passwordLayout=findViewById(R.id.passwordLayout);
        TextInputLayout confirmPasswordLayout =findViewById(R.id.confirmPasswordLayout);

        TextInputEditText usernameField =findViewById(R.id.username); // usernameField
        TextInputEditText emailField = findViewById(R.id.email);    // Email
        TextInputEditText passwordField=findViewById(R.id.password);   //passwordField
        TextInputEditText confirmPasswordField =findViewById(R.id.confirmPassword);  //confirm passwordField



        registerBtn =findViewById(R.id.register);
        signInBtn =findViewById(R.id.textsignin);
        signupProgBar=findViewById(R.id.signupProgBar);


        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailChecker;
                emailChecker = String.valueOf(emailField.getText());
                if( Patterns.EMAIL_ADDRESS.matcher(emailChecker).matches()) {
                    Email = emailChecker;
                    emailLayout.setHelperText("Valid");
                    if (Email!=" ") {
                        firebaseEmailCheck(Email, emailLayout);
                    }
                }
                else{
                    Email = "";
                    emailLayout.setError("Not Valid");
                    emailDone=Boolean.FALSE;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Changes Before the TextField has been changed
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwordFieldText =charSequence.toString();
                if(passwordFieldText.length() >=8 && passwordFieldText.length()<=10){
                    Pattern pattern=Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher=pattern.matcher(passwordFieldText);
                    boolean passwordOkay=matcher.find();
                    if(passwordOkay){
                        passwordLayout.setHelperText("Strong Password");
                        passwordLayout.setError("");
                        passwordDone=Boolean.TRUE;
                    }
                    else {
                        passwordLayout.setHelperText("");
                        passwordLayout.setError("Weak Password.Include 1 special character(eg:#@*)");
                        passwordDone=Boolean.FALSE;
                    }
                }
                else if(passwordFieldText.length() < 8){
                    passwordLayout.setHelperText("");
                    passwordLayout.setError("Minimum 8 to 10 characters");
                    passwordDone=Boolean.FALSE;
                }
                else if(passwordFieldText.length()>10){
                    passwordField.setText("");//mustRemove This
                }
            }
            @Override
            public void afterTextChanged(Editable editable){
                //Changes after TextField has been changed
            }
        });
        confirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwordFieldText = passwordField.getText().toString();
                String confirmPasswordFieldText = confirmPasswordField.getText().toString();
                if(confirmPasswordFieldText.equals(passwordFieldText) && passwordDone){
                    confirmPasswordLayout.setHelperText("Password Matches");
                    Password = confirmPasswordFieldText;
                }
                else if(!passwordDone){
                    Password = "";
                    confirmPasswordLayout.setError("Enter password according to the given standards");
                }
                else {
                    Password = "";
                    confirmPasswordLayout.setError("Confirm Password not Match with Password");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameFieldText = usernameField.getText().toString();
                if (usernameFieldText.matches("[a-zA-z0-9]+")&& usernameFieldText.length() > 6) {
                    Username = usernameFieldText;
                    usernameLayout.setHelperText("Matched");
                    usernameDone=Boolean.TRUE;
                }
                else if (usernameFieldText.length() <= 6) {
                    Username = "";
                    usernameDone=Boolean.FALSE;
                    usernameLayout.setError("enter more than 6 char");
                }
                else if(!usernameFieldText.matches("[a-zA-z0-9]+") && usernameFieldText.length() >6){
                    Username = "";
                    usernameLayout.setError("include only alphabetical char");}}
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupProgBar.setVisibility(View.VISIBLE);

                if(Email == "" || Password == "" || Username == ""){
                    Toast.makeText(SignUpAct.this, "Fill All Fields Properly", Toast.LENGTH_SHORT).show();
                    signupProgBar.setVisibility(View.GONE);
                }

                else if(!usernameDone || !emailDone || !passwordDone)
                {
                    Toast.makeText(SignUpAct.this, "Fill All Fields Properly", Toast.LENGTH_SHORT).show();
                    signupProgBar.setVisibility(View.GONE);
                }
                else {
                    firebaseRegister(Email, Password);
                }

//
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpAct.this,MainActivity.class));
                finish();
            }
        });
    }

    public void firebaseEmailCheck(String email, TextInputLayout emailLayout){
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check= !task.getResult().getSignInMethods().isEmpty();
                        if(check){
                            emailDone=Boolean.FALSE;
                            emailLayout.setError("Email Already Exists");
                            Toast.makeText(SignUpAct.this, "Email Already Exists", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            emailDone=Boolean.TRUE;
                        }
                    }
                });
    }

    public void firebaseRegister(String Email, String Password){
      //  Toast.makeText(this, "Please Wait while we are Registering you...", Toast.LENGTH_SHORT).show();
        
        auth = FirebaseAuth.getInstance(); //Instantiate the firebaseAuth

        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseStoreUserDetails(Username, Email);
                }
                else{
                    signupProgBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpAct.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void firebaseStoreUserDetails(String username, String email){
        Map<String, Object> m = new HashMap<>();
        m.put("username", username);
        m.put("email", email);
        m.put("isAdmin", false);
        db.collection("Users/")
                .document(email)
                .set(m)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        LoggedInUserDetails ud = new LoggedInUserDetails(username, email);
                        Toast.makeText(SignUpAct.this, "Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpAct.this, NewsFeed.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpAct.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}



