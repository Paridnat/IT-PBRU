package pbru.paridnat.itpbru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MainActivity extends AppCompatActivity {

    //Explicit
    private MyManage myManage;
    private static final String urlJSON = "http://swiftcodingthai.com/pbru2/get_user_master.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myManage = new MyManage(this);

        //Test Add New User
        //myManage.addNewUser("123", "name", "sur", "user", "pass"); test send data to database
        deleteAllSQLite();

        mySynJSON();

    } // Main Method

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
