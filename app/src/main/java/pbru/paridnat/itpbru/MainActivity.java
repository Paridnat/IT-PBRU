package pbru.paridnat.itpbru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

// *** Technic shortcut key "command + 6" is hide dialog show debug
// *** Log.i("test", "test debug" + i); Debug values

public class MainActivity extends AppCompatActivity {

    //Explicit
    private MyManage myManage;
    private static final String urlJSON = "http://swiftcodingthai.com/pbru2/get_user_master.php";
    private EditText userEditText, passwordEditText;
    private String userString, passwordString; // get value form EditText
    private String[] loginStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        userEditText = (EditText) findViewById(R.id.editText5);
        passwordEditText = (EditText) findViewById(R.id.editText6);

        myManage = new MyManage(this);

        //Test Add New User
        //myManage.addNewUser("123", "name", "sur", "user", "pass"); test send data to database
        deleteAllSQLite();

        mySynJSON();

    } // Main Method


    public void clickSignIn(View view) {

        userString = userEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();

        //Check Space
        if (userString.equals("") || passwordString.equals("")) {
            MyAlert myAlert = new MyAlert();
            myAlert.myDialog(this, "Have Space", "Please fill all every blank");
        } else {
            checkUserAndPassword();
        }

    }

    private void checkUserAndPassword() {

        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                    MODE_PRIVATE, null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM userTABLE WHERE User = " + "'" + userString + "'", null); //ดึงทุกอย่างไปไว้ในแรมก่อน
            cursor.moveToFirst();

            loginStrings = new String[cursor.getColumnCount()];
            for (int i=0; i<cursor.getColumnCount(); i++) {
                loginStrings[i] = cursor.getString(i);
            }
            cursor.close(); // คืนหน่วยความจำ

            // Check Password
            if (passwordString.equals(loginStrings[4])) {
                Toast.makeText(this, "Welcome" + loginStrings[2] + " " + loginStrings[1],
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, CalendaActivity.class);
                intent.putExtra("Login", loginStrings);
                startActivity(intent);
                finish();

            } else {
                MyAlert myAlert = new MyAlert();
                myAlert.myDialog(this, "Password False", "Please try again password");
            }

        } catch (Exception e) {
            MyAlert myAlert = new MyAlert();
            myAlert.myDialog(this, "ไม่มี User นี้?", "ไม่มี "+userString+" ในฐานข้อมูลของเรา");
        }

    }// Check User


    private void mySynJSON() {

        ConnectedUserTABLE connectedUserTABLE = new ConnectedUserTABLE(this);
        connectedUserTABLE.execute();

    }


    //Create Inner Class การสร้างคลาสซ้อนคลาส
    private class ConnectedUserTABLE extends AsyncTask<Void, Void, String> {
        private Context context;
        private ProgressDialog progressDialog;

        public ConnectedUserTABLE(Context context) {
            this.context = context;
        }// Constructor

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context, "Synchronize Server",
                    "Please Wait ... Process Synchronize");

        } // onPre ตอนโหลดข้อมูล (ตัวโหลดข้อมูล หมุนๆ)

        @Override
        protected String doInBackground(Void... params) {

            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                Response response = okHttpClient.newCall(request).execute();
                return  response.body().string();

            } catch (Exception e) {
                Log.d("7June", "error DoIn ==> " + e.toString());
                return null;
            }// เช็คเน็ตติดๆหายๆ

        } // doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                progressDialog.dismiss();
                Log.d("7June", "JSON ==> " + s);

                JSONArray jsonArray = new JSONArray(s);

                // จองหน่วยความจำให้กับ array ทั้งสี่ตัว
                String[] idStings = new String[jsonArray.length()];
                String[] nameStrings = new String[jsonArray.length()];
                String[] surnameString = new String[jsonArray.length()];
                String[] userString = new String[jsonArray.length()];
                String[] passwordString = new String[jsonArray.length()];

                for (int i=0; i<jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    idStings[i] = jsonObject.getString("id");
                    nameStrings[i] = jsonObject.getString(MyManage.column_name);
                    surnameString[i] = jsonObject.getString(MyManage.column_surname);
                    userString[i] = jsonObject.getString(MyManage.column_user);
                    passwordString[i] = jsonObject.getString(MyManage.column_password);

                    myManage.addNewUser(idStings[i], nameStrings[i],
                            surnameString[i], userString[i], passwordString[i]);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }// Method หลังจากที่ทำงานเสร็จแล้ว

    }// Connected Class


    private void deleteAllSQLite() {

        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                MODE_PRIVATE, null);
        sqLiteDatabase.delete(MyManage.user_table, null, null);

    } // deleteAllSQLite

    public void clickSignUpMain(View view) {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }

} // Main Class
