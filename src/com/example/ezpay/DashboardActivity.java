package com.example.ezpay;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
 
import library.UserFunctions;
 
public class DashboardActivity extends Activity {
    UserFunctions userFunctions;
    Button btnLogout;
    Button btnWithdraw;
    Button btnScan;
    Button btnSearch;
    Button btnCheck;
    String contents;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        /**
         * Dashboard Screen for the application
         * */       
        // Check login status in database
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
       // user already logged in show databoard
            setContentView(R.layout.dashboard);
            //logout event
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new View.OnClickListener() {
                 
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    // Closing dashboard screen
                    finish();
                }
            });
            //withdraw event
            btnWithdraw = (Button) findViewById(R.id.btnWithdraw);
            btnWithdraw.setOnClickListener(new View.OnClickListener() {
            	 
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),
                            WithdrawActivity.class);
                    startActivity(i);
                    finish();
                }
            });
          //scan event
            btnScan = (Button) findViewById(R.id.btnScan);
            
          //search event
            btnSearch = (Button) findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new View.OnClickListener() {
            	 
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),
                            SearchActivity.class);
                    startActivity(i);
                    finish();
                }
            });
          //check event
            btnCheck = (Button) findViewById(R.id.btnCheck);
            btnCheck.setOnClickListener(new View.OnClickListener() {
            	 
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),
                            CheckActivity.class);
                    startActivity(i);
                    finish();
                }
            });
             
        }else{
            // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        }    
        
    }
    private Button.OnClickListener btnScanOnClick = new Button.OnClickListener () {
		public void onClick(View v){
			//按下按鈕所執行的程式碼
			
			//連結ZXING的API
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");	//開啟條碼掃描器
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");	//設定QR Code參數
			startActivityForResult(intent, 1);	//要求回傳1
			
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {	//startActivityForResult回傳值
			if (resultCode == RESULT_OK) {
				contents = data.getStringExtra("SCAN_RESULT");	//取得QR Code內容,照預定的格式(擷取)
				AlertDialog.Builder altDlg = new AlertDialog.Builder(DashboardActivity.this);
				altDlg.setTitle("確認購買的資訊");
				altDlg.setMessage(contents);
				altDlg.setIcon(android.R.drawable.ic_dialog_info);
				altDlg.setCancelable(false);
				altDlg.setPositiveButton("確定", 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//上傳扣款資訊,記帳
								int first = 0;
								int sum = 0;
								int tax = 0;
								String GPS;
								
								int times = Integer.parseInt(contents.substring(1, 2));
								String[] item = new String[times];
								int[] price = new int [times];
								
								for ( int i=1 ; i<=times ; i++ ){
									first = (i-1)*17 +3;
									item[i-1] = contents.substring(first, first+10);
									price[i-1] = Integer.parseInt(contents.substring(first+11, first+16));
									sum = sum + price[i-1];
								}
								tax = Integer.parseInt(contents.substring(first+17,first+24));
								GPS = contents.substring(first+26);
							}
						});
				altDlg.setNegativeButton("錯誤", 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 重新掃描
							}
						});
				altDlg.show();
			}
		}
	}
}