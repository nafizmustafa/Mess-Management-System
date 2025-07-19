package edu.ewubd.cse489n2021260082;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail,etPassword;
    private CheckBox cbRememberUser,cbRememberLogin;
    private Button btnSignIn;

    private SharedPreferences sp;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        cbRememberUser=findViewById(R.id.cbRememberUser);
        cbRememberLogin=findViewById(R.id.cbRememberLogin);
        btnSignIn=findViewById(R.id.btnSignIn);

        //
        sp=this.getSharedPreferences("my_sp",MODE_PRIVATE);
         email=sp.getString("USER_EMAIL","");
        password=sp.getString("PASSWORD","");
        boolean rememberUser=sp.getBoolean("REMEMBER_USER",false);
        boolean rememberLogin=sp.getBoolean("REMEMBER_LOGIN",false);
        if (rememberUser){
            etEmail.setText((email));
            cbRememberUser.setChecked(true);
        }
        if (rememberLogin){
            etEmail.setText((email));
            cbRememberUser.setChecked(true);
        }
        //

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String e=etEmail.getText().toString().trim();
                String p=etPassword.getText().toString().trim();
                if (!e.equals(email)){
                    Toast.makeText(LoginActivity.this,"Not Match email",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!p.equals(password)){
                    Toast.makeText(LoginActivity.this,"Not Match Password",Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences.Editor ed= sp.edit();
                ed.putBoolean("REMEMBER_USER",cbRememberUser.isChecked());
                ed.putBoolean("REMEMBER_LOGIN",cbRememberLogin.isChecked());
                ed.apply();
                Intent i= new Intent(LoginActivity.this,ReportActivity.class);
                startActivity(i);


            }
        });

    }
}