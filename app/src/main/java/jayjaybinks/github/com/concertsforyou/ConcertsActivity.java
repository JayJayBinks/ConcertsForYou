package jayjaybinks.github.com.concertsforyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.github.jjbinks.bandsintown.dto.ArtistEvent;
import com.github.jjbinks.bandsintown.exception.BITException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.User;

import static jayjaybinks.github.com.concertsforyou.BandsInTownUtility.bitAPI;

public class ConcertsActivity extends AppCompatActivity {



    private String lastFMUsername;
    private ListView lvArtistConcerts;

    //private Collection<>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_concerts);

        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        lastFMUsername = sharedPref.getString(getString(R.string.preference_lastfm_username), null);

        lvArtistConcerts= findViewById(R.id.artist_concerts);

        if(lastFMUsername == null){
            new AlertDialog.Builder(this)
                    .setTitle("No last.fm username")
                    .setMessage("Info can not be fetched without a last.fm username set.")
                    //TODO navigate to set username
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    })
                    .setNegativeButton("Okay", null).show();
        }else{
            fetchArtistEventsTask.execute(lastFMUsername, getString(R.string.api_key));
        }



//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private AsyncTask<String, Void, Integer> fetchArtistEventsTask = new AsyncTask<String, Void, Integer>()
    {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        @Override
        protected Integer doInBackground(String... params)
        {
            fetchArtistEvents(params[0], params[1]);
            return 0;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        @Override
        protected void onPostExecute(Integer result)
        {
            //displayAd();
        }
    };

    private void fetchArtistEvents(String lastFMUsername, String apiKey) {
        Collection<Artist> topArtists = User.getTopArtists(lastFMUsername, apiKey);

        int i = 0;
        for(Artist artist : topArtists){
            //TODO make this a setting
            //only top ten artists

            if(i < 10){
                break;
            }
            try {
                File file = getBaseContext().getFileStreamPath(artist.getName() + ".png");
                if(file.exists() == false){
                    new DownloadImage(artist.getImageURL(ImageSize.SMALL), artist.getName() + ".png").execute();
                }
                int b = 0;
                for(ArtistEvent artistEvent : bitAPI.getArtistEvents(artist.getName())){
                    if(b < 10){
                        break;
                    }
                    Concert concert = new Concert(artist.getName(), artistEvent.getDatetime()
                            ,artist.getName() + ".png"
                            ,artistEvent.getVenue().getName()
                            ,artistEvent.getVenue().getCountry()
                            ,artistEvent.getVenue().getCity());


                    lvArtistConcerts.add(ConcertFragment.newInstance(concert));

                }

            } catch (BITException e) {
                e.printStackTrace();
            }
            i++;
        }

    }
    private void saveImage(Context context, Bitmap b, String imageName)
    {
        FileOutputStream foStream;
        try
        {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        }
        catch (Exception e)
        {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private String sUrl;
        private String fileName;

        public DownloadImage(String sUrl, String fileName) {
            this.sUrl = sUrl;
            this.fileName = fileName;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, fileName);
        }
    }
}
