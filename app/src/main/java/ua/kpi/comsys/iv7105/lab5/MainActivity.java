package ua.kpi.comsys.iv7105.lab5;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.json.*;

import ua.kpi.comsys.iv7105.lab5.model.Movie;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1234;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSIONS_COUNT = 2;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Lab3");

        TabHost tabHost = findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.linearLayout);
        tabSpec.setIndicator("Movies");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.linearLayout2);
        tabSpec.setIndicator("Pictures");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.linearLayout3);
        tabSpec.setIndicator("Weapons");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        // End tab

        String jsonString = "";
        try (InputStream is = this.getResources().openRawResource(R.raw.movies_list)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            jsonString = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Movie> movies = new LinkedList<>();
        try {
            JSONObject obj = new JSONObject(jsonString);

            int size = obj.getJSONArray("Search").length();
            for (int i = 0; i < size; i++) {
                JSONObject movieObj = obj.getJSONArray("Search").getJSONObject(i);

                movies.add(new Movie(
                        movieObj.getString("Title"),
                        movieObj.getString("Year"),
                        movieObj.getString("imdbID"),
                        movieObj.getString("Type"),
                        movieObj.getString("Poster")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<String> moviesRepr = new LinkedList<>();
        for (Movie m : movies) {
            moviesRepr.add(m.shortInfo());
        }

        final ListView listView = findViewById(R.id.list_view_id);
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, moviesRepr);
        listView.setAdapter(adapter);
        final Context c= getApplicationContext();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(c, MovieDetail.class);
                Bundle b = new Bundle();
                b.putString("text", ((TextView)view).getText().toString());
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    private boolean arePermissionsDenied() {
        for (int i = 0; i< PERMISSIONS_COUNT; i++) {
            if(checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSIONS && grantResults.length>0) {
            if (arePermissionsDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE)))
                        .clearApplicationUserData();
                recreate();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);

        if(!isGalleryInitialized) {
            final ListView listViewLeft = findViewById(R.id.listViewLeft);
            final ListView listViewCenter = findViewById(R.id.listViewCenter);
            final ListView listViewRight = findViewById(R.id.listViewRight);

            final GalleryAdapter galleryAdapterLeft = new GalleryAdapter();
            final GalleryAdapter galleryAdapterCenter = new GalleryAdapter();
            final GalleryAdapter galleryAdapterRight = new GalleryAdapter();

            final File imageDir = new File(String.valueOf(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            final File[] files = imageDir.listFiles();
            final int filesCount = files.length;
            final List<String> filesList = new ArrayList<>();

            for(int i=0;i<filesCount;i++) {
//                final String path = files[i].getAbsolutePath();
                Arrays.stream(files[i].listFiles())
                        .map(File::getAbsolutePath)
                        .filter(path -> path.endsWith(".jpg") || path.endsWith(".png"))
                        .forEach(filesList::add);
//                if (path.endsWith(".jpg") || path.endsWith(".png")) {
//                    filesList.add(path);
//                }
            }

            galleryAdapterLeft.setData(filesList.subList(0, 3));
            galleryAdapterCenter.setData(filesList.subList(3, 4));
            galleryAdapterRight.setData(filesList.subList(4, 7));

            listViewLeft.setAdapter(galleryAdapterLeft);
            listViewCenter.setAdapter(galleryAdapterCenter);
            listViewRight.setAdapter(galleryAdapterRight);

            isGalleryInitialized = true;
        }
    }

    private boolean isGalleryInitialized;

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    final class GalleryAdapter extends BaseAdapter {

        private List<String> data = new ArrayList<>();

        void setData(List<String> data) {
            if (this.data.size() > 0)
                data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if(convertView == null) {
                imageView = (ImageView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageView);
            return imageView;
        }
    }
}