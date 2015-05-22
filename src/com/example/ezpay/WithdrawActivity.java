package com.example.ezpay;
 
import org.json.JSONException;
import org.json.JSONObject;
 






//import library.DatabaseHandler;
import android.content.SharedPreferences;
import library.UserFunctions;
//import library.RSA;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import library.certify; 
 
public class WithdrawActivity extends Activity {
    Button btnWithdraw;
    Button btnAuthenticated;
    Button btnLinkToDashboard;
    EditText inputWithdrawMoney;
    EditText inputAuthenticatedNum;
    TextView withdrawMsg;
    private SharedPreferences settings;
    private static final String database = "user_info";
    private static final String keyField = "priv_key";
     
    // JSON Response node names
    private static String KEY_WITHDRAW_MSG = "withdraw_msg";
   // private static String KEY_AUTHEN_NUM = "authen_num";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw);
 
        // Importing all assets like buttons, text fields
        inputWithdrawMoney = (EditText) findViewById(R.id.withdrawMoney);
        inputAuthenticatedNum = (EditText) findViewById(R.id.authenticatedNum);
       
        btnWithdraw = (Button) findViewById(R.id.btnWithdraw);
        btnAuthenticated = (Button) findViewById(R.id.btnAuthenticated);
        btnLinkToDashboard = (Button) findViewById(R.id.btnLinkToDashboard);
        withdrawMsg = (TextView) findViewById(R.id.withdrawMsg);
         
        // Authenticated Num Button Click event
        btnAuthenticated.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
                
               
                UserFunctions userFunction = new UserFunctions(); 
               
                	 
                JSONObject json = userFunction.send_mail();
                try {
                	 String withdraw_msg = json.getString(KEY_WITHDRAW_MSG);
                     withdrawMsg.setText(withdraw_msg); 
                } 
                catch (JSONException e) 
                { 
                	e.printStackTrace();
                }
            }
        });
        // Withdraw Money Button Click event
        btnWithdraw.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
                String dollar = inputWithdrawMoney.getText().toString();
                String authen_num = inputAuthenticatedNum.getText().toString();
              
                UserFunctions userFunction = new UserFunctions(); 
                if(!userFunction.isNumeric(dollar))
                {
                	withdrawMsg.setText("請輸入正確金額"); 
                }
                else
                {
                	certify cert = new certify();
                
                	settings = getSharedPreferences(database,0);
                	String privkey = settings.getString(keyField, "");
                	byte [] privkeyEncode=privkey.getBytes();
                	String sign = cert.signData(dollar,privkeyEncode);
                    
                	 
                	JSONObject json = userFunction.withdraw(dollar , authen_num, sign);
                	try {
                			String withdraw_msg = json.getString(KEY_WITHDRAW_MSG);
                			withdrawMsg.setText(withdraw_msg); 
                		} 
                		catch (JSONException e) 
                		{ 
                			e.printStackTrace();
                		}
                }
            }
        });
 
        // Link to Dashboard Screen
        btnLinkToDashboard.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                       DashboardActivity.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }
}
