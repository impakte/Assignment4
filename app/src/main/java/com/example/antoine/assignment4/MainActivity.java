package com.example.antoine.assignment4;

import android.os.Bundle;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.app.LoaderManager;

public class MainActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter dataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayListView();

        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // starts a new Intent to add a Country
                Intent countryEdit = new Intent(getBaseContext(), Editor.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                countryEdit.putExtras(bundle);
                startActivity(countryEdit);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

    private void displayListView() {


        // The desired columns to be bound
        String[] columns = new String[] {
                DataBase.KEY_CODE,
                DataBase.KEY_NAME,
                DataBase.KEY_CONTINENT
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.code,
                R.id.name,
                R.id.continent,
        };

        // create an adapter from the SimpleCursorAdapter
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.trainer_info,
                null,
                columns,
                to,
                0);

        // get reference to the ListView
        ListView listView = (ListView) findViewById(R.id.countryList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // display the selected country
                String countryCode =
                        cursor.getString(cursor.getColumnIndexOrThrow(DataBase.KEY_CODE));
                Toast.makeText(getApplicationContext(),
                        countryCode, Toast.LENGTH_SHORT).show();

                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(DataBase.KEY_ROWID));

                // starts a new Intent to update/delete a Country
                // pass in row Id to create the Content URI for a single row
                Intent countryEdit = new Intent(getBaseContext(), Editor.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", rowId);
                countryEdit.putExtras(bundle);
                startActivity(countryEdit);

            }
        });

    }

    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                DataBase.KEY_ROWID,
                DataBase.KEY_CODE,
                DataBase.KEY_NAME,
                DataBase.KEY_CONTINENT};
        CursorLoader cursorLoader = new CursorLoader(this,
                MyContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        dataAdapter.swapCursor(null);
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        //return true;
    //}
}
