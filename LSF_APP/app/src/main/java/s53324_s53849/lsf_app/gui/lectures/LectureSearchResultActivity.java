package s53324_s53849.lsf_app.gui.lectures;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.database.query.LectureQueryBuilder;
import s53324_s53849.lsf_app.database.query.LectureQueryData;
import s53324_s53849.lsf_app.gui.AutoHideNavigationBarActivity;
import s53324_s53849.lsf_app.provider.ContentConsumer;


public class LectureSearchResultActivity extends AutoHideNavigationBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private LectureQueryData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_search_result);

        final Bundle extras = getIntent().getExtras();
        LectureQueryData data = (LectureQueryData) extras.getSerializable("data");
        this.data = data;

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(1, null, this);
        //TODO layout display
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ContentConsumer.getLecturesLoader(this, data);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        if (data.getCount() == 0) {
            Toast.makeText(this,"Keine Veranstaltungen gefunden.",Toast.LENGTH_LONG).show();
        } else {
            //create an adapter to wrap the content of the cursor
            CursorAdapter adapter = new LectureAdapter(this, data, true);

            //sets the adapter whose content to display in the listView
            ListView listView = (ListView) findViewById(R.id.lecture_list_view);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
