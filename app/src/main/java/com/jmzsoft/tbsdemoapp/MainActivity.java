package com.jmzsoft.tbsdemoapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "employee_name";
    private static final String TAG_SALARY = "employee_salary";
    private static final String TAG_AGE = "employee_age";
    private static final String TAG_IMAGE = "profile_image";

    private SearchView searchView;
    private EmployeeAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Employees> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView  = findViewById(R.id.listings_view);
        BackgroundTask task = new BackgroundTask(MainActivity.this);
        task.execute();

    }

    private ArrayList<Employees> getData() {
        ArrayList<Employees> employees = new ArrayList<>();

        HttpHandler sh = new HttpHandler();

        String jsonStr = sh.makeServiceCall();

        if (jsonStr != null) {
            if (!jsonStr.trim().equals("null")) {
                try {
                    JSONArray jsonObject = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonObject.length(); i++) {
                        try {
                            JSONObject object = jsonObject.getJSONObject(i);
                            Employees employee = new Employees();
                            employee.setId(object.getInt(TAG_ID));
                            employee.setEmployeeName(object.getString(TAG_NAME));
                            employee.setEmployeeSalary(object.getInt(TAG_SALARY));
                            employee.setEmployeeAge(object.getInt(TAG_AGE));
                            employee.setProfileImage(object.getString(TAG_IMAGE));
                            employees.add(employee);
                        } catch (JSONException ignored) {
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return employees;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_search || super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        BackgroundTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Downloading records...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter = new EmployeeAdapter(MainActivity.this, employees);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            employees = getData();
            return null;
        }

    }
}
