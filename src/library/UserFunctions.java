package library;
 
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;


public class UserFunctions {
     
    private JSONParser jsonParser;
    
     
    // Testing in localhost using wamp or xampp 
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "http://220.143.15.23/login_api/";
    private static String registerURL = "http://220.143.15.23/login_api/";
    private static String searchURL = "http://220.143.15.23/login_api/";
    
    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String search_tag = "search";
    private static String key_tag = "key";
    private static String mail_tag = "mail";
    private static String EMAIL;
    private static String checkno_type = "checkno";
    private static String withdraw_type = "withdraw";
   
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
        
    }
    //if the string is number
    public boolean isNumeric(String str){
    	  for (int i = str.length();--i>=0;){   
    	   if (!Character.isDigit(str.charAt(i))){
    	    return false;
    	   }
    	  }
    	  return true;
    	 }
   //get public key from server
    public JSONObject get_publicKey()
    {
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key", key_tag));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }
    
    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", RSA.encryptByPublic(email)));
        params.add(new BasicNameValuePair("password", RSA.encryptByPublic(password)));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        EMAIL = email;
        return json;
    }
     
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password, String new_password, String id, String post_id, byte[] pubkey){
    	
    	 // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", RSA.encryptByPublic(name)));
        params.add(new BasicNameValuePair("email", RSA.encryptByPublic(email)));
        params.add(new BasicNameValuePair("password", RSA.encryptByPublic(password)));
        params.add(new BasicNameValuePair("new_password", RSA.encryptByPublic(new_password)));
        params.add(new BasicNameValuePair("id", RSA.encryptByPublic(id)));
        params.add(new BasicNameValuePair("post_id", RSA.encryptByPublic(post_id)));
        params.add(new BasicNameValuePair("pubkey", RSA.encryptByPublic(Base64.encodeToString(pubkey, Base64.DEFAULT))));
       
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        // return json
        return json;
    }
     /**
      * Function send mail 
      **/
    public JSONObject send_mail(){
    	
   	 // Building Parameters
       List<NameValuePair> params = new ArrayList<NameValuePair>();
       params.add(new BasicNameValuePair("tag", mail_tag));
       params.add(new BasicNameValuePair("email", RSA.encryptByPublic(EMAIL)));
       params.add(new BasicNameValuePair("type", checkno_type));
       // getting JSON Object
       JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
       // return json
       return json;
   }
    /**
     * Withdraw Money 
     **/
   public JSONObject withdraw(String dollar , String checkno, String sign){
   	
  	 // Building Parameters
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("tag", mail_tag));
      params.add(new BasicNameValuePair("email",RSA.encryptByPublic(EMAIL)));
      params.add(new BasicNameValuePair("type", withdraw_type));
      params.add(new BasicNameValuePair("dollar", RSA.encryptByPublic(dollar)));
      params.add(new BasicNameValuePair("checkno", RSA.encryptByPublic(checkno)));
      params.add(new BasicNameValuePair("sign", RSA.encryptByPublic(sign)));
      // getting JSON Object
      JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
      // return json
      return json;
  }
   /*
    * *search goods
    */
   public JSONObject search(String name){
		// Building Parameters
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", search_tag));
		params.add(new BasicNameValuePair("name", name));
		//params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(searchURL, params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
     
}