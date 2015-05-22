/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.example.ezpay;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import library.UserFunctions;

public class SearchActivity extends Activity {
	Button btnSearch;
	Button btnLinkToRegister;
	Button btnLinkToDashboard;
	EditText inputName;
	EditText inputPassword;
	ListView productList;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_NUM = "product_num";
	private static String KEY_NAME = "name";
	private static String KEY_CASH = "cash";
	private static String KEY_ADDR = "addr";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		// Importing all assets like buttons, text fields
		inputName = (EditText) findViewById(R.id.SearchName);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnLinkToDashboard = (Button) findViewById(R.id.btnLinkToDashboard);
		productList = (ListView) findViewById(R.id.listView1);
		// Login button Click Event
		btnSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputName.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				
				int n = 0,times = 1,start;
				String search = name;
				///Chinese exist yes or not
				if(name.getBytes().length != name.length() ){
					n = name.length();
				}///end of if name.getBytes()
				else{//若不是中文，搜尋一次即可 n表是中文字數量，若是英文 n = 1
					n=1;
				}
				
				for(int i = 0 ; n - i > 0;i++ ){//將中文分成N段做搜尋，所搜尋的次數=times
					if( n != 0){
						if(n%(n - i) == 0){
							times = n/(n - i);
						}
						else{
							times = n/(n - i) + n%(n - i);
						}
					}//// n ! = 0 exist chineses
					/// time default is 1
					for(start = 0; times > 0;times-- , start ++){
						if(n == 1 )search =name;//search = 欲搜尋的關鍵字
						else search = name.substring(start, start+n-i);
						///call userFunction.loginUser 作搜尋的動作 
						JSONObject json = userFunction.search(search);//回傳JSON檔
						try {
							if (json.getString(KEY_SUCCESS) != null) {//JSON中是否有"success"的tag
								ArrayList<String> List = new ArrayList<String>();//宣告arraylist檔 搜尋結果
								ArrayList<String> COO =  new ArrayList<String>();//宣告arraylist檔 經緯度
								List.clear();//清空LIST中的資料
								COO.clear();//清空LIST COO中的資料
								String res = json.getString(KEY_SUCCESS); //將sucess tag中的檔案取出(STRING檔)
								if(Integer.parseInt(res) == 1){//若是1 代表正確，其他則是未搜尋到箱府的商品
									
									// user successfully logged in
									// Store user details in SQLite Database
									res = json.getString(KEY_NUM);//取出KEY_NUM TAG的質，表示符合條件的數量
									int product_num = Integer.parseInt(res);//轉換為INT (原為STRING)
									JSONObject temp ;
									String Msg = "";//清空MSG，MSG作為LIST每行要顯示的訊息
									while(product_num > 0){//將回傳的資料做拆解，執行product_num次
										
										temp  = json.getJSONObject(Integer.toString(product_num));
										temp  = temp.getJSONObject("product");
										//先做成字串
										Msg = Msg  + temp.getString(KEY_NAME) + "  $" +temp.getString(KEY_CASH) + "\n" + temp.getString(KEY_ADDR);
										product_num--;
										List.add(Msg);//加到LIST中 顯示用
										COO.add(temp.getString("coo"));//加到LIST中 存放座標用
										Log.d("aa", "bb");
										Msg = "";//清空MSG
									}
									
									// Clear all previous data in database
									userFunction.logoutUser(getApplicationContext());
									n = 0;
									// 將ARRAYLIST顯示LISTVIEW上
									ArrayAdapter<String> content  = new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1, List);
									productList.setAdapter(content);
									//將商品位置顯是在商品下方，以便檢測
									content.add( COO.toString() );
									content.notifyDataSetChanged();
								}else{
									// Error in login
									List.add(json.getString("error_msg"));///沒找到東西，就將SERVER回傳的訊息直接加到LIST中
									productList.setAdapter(new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1, List));//將ARRAYLIST顯示LISTVIEW上
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						
					}///End of for n times
					
				}
				// check for login response
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
