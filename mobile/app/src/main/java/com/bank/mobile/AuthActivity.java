package com.bank.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.mobile.api.ApiClient;
import com.bank.mobile.api.ApiService;
import com.bank.mobile.api.models.AccountResponse;
import com.bank.mobile.api.models.AuthRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnLogin.setOnClickListener(v -> performAuth(true));
        btnRegister.setOnClickListener(v -> performAuth(false));
    }

    private void performAuth(boolean isLogin) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRequest request = new AuthRequest(username, password);
        Call<AccountResponse> call = isLogin ? apiService.login(request) : apiService.register(request);

        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AccountResponse account = response.body();
                    Toast.makeText(AuthActivity.this, "Welcome " + account.getUsername(), Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(AuthActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", account.getUsername());
                    intent.putExtra("BALANCE", account.getBalance());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AuthActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(AuthActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
