package sg.edu.rp.webservices.c302_p09_mcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

public class AddMenuItemActivity extends AppCompatActivity {

    EditText et1, et2;
    Button b1;
    private AsyncHttpClient client;
    String catId, loginId, apikey;
    MenuCategory cat;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);
        et1 = findViewById(R.id.editText);
        et2 = findViewById(R.id.editText2);
        b1 = findViewById(R.id.button);

        Intent i = getIntent();
        cat = (MenuCategory) i.getSerializableExtra("cat");
        catId = cat.getCategoryId();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        loginId = pref.getString("loginID", "");
        apikey = pref.getString("apikey", "");

        client = new AsyncHttpClient();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.add("loginId", loginId);
                params.add("apikey", apikey);
                params.add("catID", catId);
                params.add("name", et1.getText().toString());
                params.add("price", et2.getText().toString());
                client.post("http://10.0.2.2:8012/C302_P09_mCafe/addMenuItem.php", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Toast.makeText(getBaseContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Intent i = new Intent(AddMenuItemActivity.this, MenuItemsByCategoryActivity.class);
                i.putExtra("thing", cat);
                startActivity(i);
            }
        });
    }
}