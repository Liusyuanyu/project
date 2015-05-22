package com.example.ezpay;
 
import org.json.JSONException;
import org.json.JSONObject;


//import java.io.IOException;
import 	java.security.*;

import library.DatabaseHandler;
import library.UserFunctions;
import library.RSA;
import library.certify;
 
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class RegisterActivity extends Activity {
    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputNewPassword;
    EditText inputId;
    EditText inputPostId;
    TextView registerErrorMsg;
     
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    //private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_KEY = "key";
    private SharedPreferences settings;
    private static final String database = "user_info";
    private static final String keyField = "priv_key";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
 
        // Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        inputNewPassword = (EditText) findViewById(R.id.registerNewPassword);
        inputId = (EditText) findViewById(R.id.registerID);
        inputPostId = (EditText) findViewById(R.id.registerPOST);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
         
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String new_password = inputNewPassword.getText().toString();
                String id = inputId.getText().toString();
                String post_id = inputPostId.getText().toString();
                UserFunctions userFunction = new UserFunctions();
                //get public key from server
                JSONObject srv_pubkey = userFunction.get_publicKey(); 
                try {
                	RSA.RSA_PUBLICE = srv_pubkey.getString(KEY_KEY);
                } 
                catch (JSONException e) 
                { 
                	e.printStackTrace();
                }
                //generate own RSA key pairs
                certify cert = new certify();
                KeyPair keyPair = cert.genKeyPair();
                // extract the encoded private key & public key , there are  unencrypted PKCS#8  keys
    			byte[] encodedprivkey = keyPair.getPrivate().getEncoded();
    			String privkey =String.valueOf(encodedprivkey);  
    		    settings = getSharedPreferences(database,0);
    		    settings.edit().putString(keyField, privkey);
    		    settings.edit().commit();   		 
    			byte[] encodedpubkey = keyPair.getPublic().getEncoded();
    			try
        		{
        			/*Signature signature = Signature.getInstance("SHA1withRSA");
                    signature.initSign(keyPair.getPrivate(), SecureRandom.getInstance("SHA1PRNG"));
                    signature.update(name.getBytes());
                    byte[] sigBytes = signature.sign();*/
                    JSONObject json = userFunction.registerUser(name, email, password,new_password,id,post_id,encodedpubkey);                          		                                                           
                     // check for login response              
                    if (json.getString(KEY_SUCCESS) != null) {
                        registerErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            // user successfully registred
                            // Store user details in SQLite Database
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                           
                            JSONObject json_user = json.getJSONObject("user");
                        
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
     
                            db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT)); 
                            //db.addUserPrivkey( encodedprivkey);
                            //start login activity
                            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// Close all views before launching Dashboard
                            startActivity(login);
                          
                            // Close Registration Screen
                            finish();
                        }else{
                            // Error in registration
                        	String error_msg = json.getString(KEY_ERROR_MSG);
                            registerErrorMsg.setText(error_msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }
}