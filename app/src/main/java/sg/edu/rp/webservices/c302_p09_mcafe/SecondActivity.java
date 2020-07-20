package sg.edu.rp.webservices.c302_p09_mcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SecondActivity extends AppCompatActivity {

    EditText et1, et2;
    Button b1, b2;
    MenuCategoryItem item;
    MenuCategory sendback;
    String loginId, apikey;
    AsyncHttpClient client;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        et1 = findViewById(R.id.editText3);
        et2 = findViewById(R.id.editText4);

        Intent i = getIntent();
        item = (MenuCategoryItem) i.getSerializableExtra("thing");
        sendback = (MenuCategory) i.getSerializableExtra("return");
        et1.setText(item.getDescription());
        et2.setText(item.getUnitPrice() + "");
        b1 = findViewById(R.id.button2);
        b2 = findViewById(R.id.button3);

        client = new AsyncHttpClient();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        loginId = pref.getString("loginID", "");
        apikey = pref.getString("apikey", "");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.add("itemId", item.getId());
                params.add("loginId", loginId);
                params.add("apikey", apikey);
                params.add("desc", et1.getText().toString());
                params.add("price", et2.getText().toString());
                client.post("http://10.0.2.2:8012/C302_P09_mCafe/updateMenuItemById.php", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Toast.makeText(getBaseContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Intent i = new Intent(SecondActivity.this, MenuItemsByCategoryActivity.class);
                i.putExtra("thing", sendback);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.add("id", item.getId());
                params.add("loginId", loginId);
                params.add("apikey", apikey);
                client.post("http://10.0.2.2:8012/C302_P09_mCafe/deleteMenuItemById.php", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Toast.makeText(getBaseContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Intent i = new Intent(SecondActivity.this, MenuItemsByCategoryActivity.class);
                i.putExtra("thing", sendback);
                startActivity(i);
            }
        });
    }
}