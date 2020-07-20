package sg.edu.rp.webservices.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<MenuCategory> adapter;
    private ArrayList<MenuCategory> list;
    private AsyncHttpClient client;
    String loginId, apikey;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewMenuCategories);
        list = new ArrayList<MenuCategory>();

        client = new AsyncHttpClient();

        //TODO: read loginId and apiKey from SharedPreferences
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        loginId = pref.getString("loginID", "");
        apikey = pref.getString("apikey", "");
        adapter = new ArrayAdapter<MenuCategory>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // TODO: if loginId and apikey is empty, go back to LoginActivity
        if (loginId.isEmpty() || apikey.isEmpty()) {
            finish();
        }
        //TODO: Point X - call getMenuCategories.php to populate the list view
        RequestParams params = new RequestParams();
        params.add("loginId", loginId);
        params.add("apikey", apikey);
        client.post("http://10.0.2.2:8012/C302_P09_mCafe/getMenuCategories.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.i("JSON Results: ", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = (JSONObject) response.get(i);
                        int id = jsonObj.getInt("menu_item_category_id");
                        String desc = jsonObj.getString("menu_item_category_description");
                        MenuCategory cat = new MenuCategory("" + id, desc);
                        list.add(cat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuCategory selected = list.get(position);
                //TODO: make Intent to DisplayMenuItemsActivity passing the categoryId
                Intent i = new Intent(MainActivity.this, MenuItemsByCategoryActivity.class);
                i.putExtra("thing", selected);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            // TODO: Clear SharedPreferences
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            // TODO: Redirect back to login screen
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
