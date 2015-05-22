package com.example.ezpay;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import library.JSONParser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


public class CheckActivity extends Activity {
	
	Button btnMatch;
	Button btnLinkToDashboard;
	//變數宣告
	ListView prizeList;
	static String number[] = new String[6];///中獎號碼ARRAY
	ArrayList<String> ListCheck = new ArrayList<String>();
	private static String CheckURL = "http://10.0.2.2/receipt/";
	private static JSONParser jsonParser;
	static String receipt_num[];
	
	public CheckActivity(){//CONSTRUCTOR
		jsonParser = new JSONParser();
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match);

		btnMatch = (Button) findViewById(R.id.btnMatch);
		btnLinkToDashboard = (Button) findViewById(R.id.btnLinkToDashboard);
		prizeList = (ListView) findViewById(R.id.listView2);
		///SHOW PRIZE NUMBER
		try {
			this.shownumber();//中獎號碼顯示function
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d("yy", "xx");
		// Login button Click Event
		btnMatch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String number = "";//表發票號碼
				String Msg = "";//訊息STRING
				int receipt_num = 0 , prize_num = 0;//receipt_num表示發票數量，prize_num中獎次數
				Log.d("Button", "Match");
				try {
					JSONObject json = Getreceipt();
					String res = json.getString("success");//取出success tag的質 
					if(Integer.parseInt(res) == 1){//1表正確
						receipt_num = Integer.parseInt(json.getString("receipt_num"));//取出發票的數量
						if(receipt_num > 0){
							ListCheck.clear();//清空LIST
							while(receipt_num > 0){
								number = json.getJSONObject(Integer.toString(receipt_num)).getString("receipt");//取發票號碼
								Log.d("debug",Integer.toString(receipt_num));//DEBUG用
								Msg = CheckActivity.matchNumber(number);//用MATCH function判定結果，回傳結果訊回一String
								if(Msg.compareTo("Don't match any prize") != 0){//中獎處理
									prize_num ++;//中獎次數加一
									ListCheck.add(Msg + "\n" + number);//加入LIST中
								}
								receipt_num--;
							}//N times for matching
						}//have receipt
					}///end of get number
					if(prize_num == 0){///全部發票都沒中獎處理
						ListCheck.add("沒有中獎,請再接再厲");
					}
					//條列式顯示結果
					prizeList.setAdapter(new ArrayAdapter<String>(CheckActivity.this,android.R.layout.simple_list_item_1, ListCheck));
					//Msg = CheckActivity.matchNumber(num);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("MSG", Msg);
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
	/*
	 * SHOW PRIZE INFO in the view
	 * */
		private void shownumber() throws IOException {
			/*
			 * 讀取網頁資料，取出各種獎項的號碼*/
			String url = "http://invoice.etax.nat.gov.tw/";
			Document doc = Jsoup.connect(url).get();
			Element table = doc.select("table").first();
			Iterator<Element> ite = table.select("td").iterator();
			
			int cnt = 0;
		    //String number[] = new String[6];
		    while(ite.hasNext())
		      {
		       cnt++;
		       String text = ite.next().text();
		       String temp = "";
		            switch( cnt ){
		            case 2:
		          	  temp = text.substring(0 , 8);
		          	  number[0] = temp;
		          	ListCheck.add("特別獎(1000萬) : " + "\n" + temp);
		          	  break;
		            case 4:
		          	  temp = text.substring(0 , 8);
		          	  number[1] = temp;
		          	ListCheck.add("特獎(200萬) : " + "\n" + temp);
		          	  break;
		            case 6:
		          	  temp = text.substring(0 , 8);
		          	  number[2] = temp;
		          	  temp = text.substring(9 , 17);
		          	  number[3] = temp;
		          	  temp = text.substring(18 , 26);
		          	  number[4] = temp;
		          	ListCheck.add("頭獎(20,0000~200) : " + "\n" + number[2] + "," + number[3] + "," + number[4]);
		          	  break;
		            case 18:
		          	  temp = text.substring(0 , 3);
		          	  number[5] = temp;
		          	ListCheck.add("增開六獎(200) : " + "\n" + temp);
		          	  break;
		            }//end of switch 
		        }
			
		    prizeList.setAdapter(new ArrayAdapter<String>(CheckActivity.this,android.R.layout.simple_list_item_1, ListCheck));
			
		// TODO Auto-generated method stub
		
	}
		/**
		 * Function to match receipt
		 * 比對號碼是否中獎
		 * num 欲比對的號碼
		 * */
	public static String matchNumber(String num) throws Exception{
	        /////CHECK
		String msg = "";
	        int i = 0 , prize = 0 ,r;
	      //有六組號碼，比對致中獎或6組都沒中
	        for( i = 0 ; i < 6 ; i ++){
	      	  if( i == 5){
	      		  if(num.substring(5, 8).equals(number[5])){
	      			  prize = 9;
	      			  break;
	      		  }
	      	  }//end of adjunction
	      	  if(i == 0){
	   			 if(num.equals(number[i])){
	   				 prize = 1;
	   			 	 break;
	   			 	}
	   		 	}//end of 10 million
	      	  if( i == 1 ){
	      		  if(num.equals(number[i])){
	      			  prize = 2;
	      			  break;
	      		  }
	      	  }//end of 2 million
	      	  if( i >= 2 && i <= 4 ){
	      		  for(r = 5 ; r >= 0 ; r --){
	      			  if(num.substring(r, 8).equals(number[i].substring(r, 8)) ){
	      				  if(r == 5) prize = 8; 
	      				  else prize--;
	      			  }//end of GET
	      			  else{
	      				  break;
	      			  }//end of else get
	      			  
	      		  }//end of for
	      		  
	      	  }//end of at most two hundred thousand
	      	  
	        	}//end of for
	        switch(prize){
	        case 0:
	        	msg = "Don't match any prize";
	      	  break;
	        case 1:
	        	msg = "特別獎 : 1000萬";
	      	  break;
	        case 2:
	        	msg = "特獎 : 200萬";
	      	  break;
	        case 3:
	        	msg = "頭獎 : 20萬";
	      	  break;
	        case 4:
	        	msg = "二獎 : 4萬";
	      	  break;
	        case 5:
	        	msg = "三獎 : 1萬";
	      	  break;
	        case 6:
	        	msg = "四獎 : 4000";
	      	  break;
	        case 7:
	        	msg = "五獎 : 1000";
	      	  break;
	        case 8:
	        	msg = "六獎 : 200";
	      	  break;
	        case 9:
	        	msg = "增開六獎 : 200";
	      	  break;
	        }//end of switch prize
		    
			return msg;//回傳比對結果
		}////end of match
/*
 * GET RECEIPT ONLINE
 * */
	public static JSONObject Getreceipt(){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "receipt"));
		JSONObject json = jsonParser.getJSONFromUrl(CheckURL, params);//連線SERVER用
		//Log.e("JSON", json.toString());
		return json;
	}
}

