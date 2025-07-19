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

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText etUserName,etEmail,etPhoneNumber,etPassword,etConfirmPassword;
    private CheckBox cbRememberUser,cbRememberLogin;
    private Button btnHaveAccount,btnSignUp;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp=this.getSharedPreferences("my_sp",MODE_PRIVATE);
        String email=sp.getString("USER_EMAIL","NOT_YET_CREATED");
        if (!email.equals("NOT_YET_CREATED")){
            Intent i= new Intent(this,LoginActivity.class);
            startActivity(i);
            finishAffinity();
        }


       setContentView(R.layout.sign_up_activity);
       etUserName=findViewById(R.id.etUserName);
       etEmail=findViewById(R.id.etEmail);
       etPhoneNumber=findViewById(R.id.etPhoneNumber);
       etPassword=findViewById(R.id.etPassword);
       etConfirmPassword=findViewById(R.id.etConfirmPassword);
       cbRememberUser=findViewById(R.id.cbRememberUser);
       cbRememberLogin=findViewById(R.id.cbRememberLogin);
       btnHaveAccount=findViewById(R.id.btnHaveAccount);
       btnSignUp=findViewById(R.id.btnSignUp);
       //
        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String userName=etUserName.getText().toString().trim();
                String email=etEmail.getText().toString().trim();
                String phone=etPhoneNumber.getText().toString().trim();
                String password=etPassword.getText().toString().trim();
                String confirmPassword=etConfirmPassword.getText().toString().trim();

                System.out.println(userName);
                System.out.println(email);
                System.out.println(phone);
                System.out.println(password);
                System.out.println(confirmPassword);

                if (userName.length()<4){
                    Toast.makeText(SignupActivity.this,"Username should be 4-8 letters",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Pattern.matches("[_a-zA-Z0-9]+(\\.[A-Za-z0-9]*)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]*)*", email)) {
                    Toast.makeText(SignupActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                    return;
                }
                if (phone.length()<8){
                    Toast.makeText(SignupActivity.this,"Phone should be 8-13 letters",Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length()<4){
                    Toast.makeText(SignupActivity.this,"Password should be 4 letters",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(confirmPassword)){
                    Toast.makeText(SignupActivity.this,"Not Match Password",Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences.Editor e= sp.edit();
                e.putString("USER_EMAIL",email);
                e.putString("USER_NAME",userName);
                e.putString("USER_PHONE",phone);
                e.putString("PASSWORD",password);
                e.putBoolean("REMEMBER_USER",cbRememberUser.isChecked());
                e.putBoolean("REMEMBER_LOGIN",cbRememberLogin.isChecked());
                e.apply();
                Intent i= new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
                finishAffinity();

            }
        });


        //


        btnHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}