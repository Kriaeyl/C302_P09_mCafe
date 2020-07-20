package sg.edu.rp.webservices.c302_p09_mcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MenuItemsByCategoryActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<MenuCategoryItem> adapter;
    private ArrayList<MenuCategoryItem> list;
    private AsyncHttpClient client;
    MenuCategory cat;
    String loginId, apikey;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_items_by_category);

        listView = (ListView) findViewById(R.id.lv);
        list = new ArrayList<MenuCategoryItem>();

        client = new AsyncHttpClient();

        Intent i = getIntent();
        cat = (MenuCategory) i.getSerializableExtra("thing");

        //TODO: read loginId and apiKey from SharedPreferences
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        loginId = pref.getString("loginID", "");
        apikey = pref.getString("apikey", "");
        adapter = new ArrayAdapter<MenuCategoryItem>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // TODO: if loginId and apikey is empty, go back to LoginActivity
        if (loginId.isEmpty() || apikey.isEmpty()) {
            finish();
        }
        //TODO: Point X - call getMenuCategories.php to populate the list view

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuCategoryItem selected = list.get(position);
                Intent i = new Intent(MenuItemsByCategoryActivity.this, SecondActivity.class);
                i.putExtra("thing", selected);
                i.putExtra("return", cat);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestParams params = new RequestParams();
        params.add("loginId", loginId);
        params.add("apikey", apikey);
        params.add("categoryId", cat.getCategoryId());
        client.post("http://10.0.2.2:8012/C302_P09_mCafe/getMenuItemsByCategory.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.i("JSON Results: ", response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = (JSONObject) response.get(i);
                        int catid = jsonObj.getInt("menu_item_category_id");
                        int id = jsonObj.getInt("menu_item_id");
                        String desc = jsonObj.getString("menu_item_description");
                        double price = jsonObj.getDouble("menu_item_unit_price");
                        MenuCategoryItem item = new MenuCategoryItem("" + id, "" + catid, desc, price);
                        list.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submain, menu);
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
            Intent i = new Intent(MenuItemsByCategoryActivity.this, LoginActivity.class);
            startActivity(i);

            return true;
        }
        if (id == R.id.menu_addmenuitem) {
            Intent i = new Intent(MenuItemsByCategoryActivity.this, AddMenuItemActivity.class);
            i.putExtra("cat", cat);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}