package com.example.drishya_vani;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class NearbyPlaces extends AppCompatActivity {

    RecyclerView recyclerView;
    NearbyAdapter adapter;
    ArrayList<NearbyPlaceModel> placeList;
    FusedLocationProviderClient fusedLocationClient;

    // ⭐ MULTIPLE OVERPASS SERVERS (ANTI SERVER BUSY FIX)
    private final String[] OVERPASS_SERVERS = {
            "https://overpass-api.de/api/interpreter",
            "https://lz4.overpass-api.de/api/interpreter",
            "https://z.overpass-api.de/api/interpreter"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        recyclerView = findViewById(R.id.recyclerNearby);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeList = new ArrayList<>();
        adapter = new NearbyAdapter(placeList, this);
        recyclerView.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null)
                fetchNearbyPlaces(location.getLatitude(), location.getLongitude());
            else
                Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101 && grantResults.length>0 &&
                grantResults[0]==PackageManager.PERMISSION_GRANTED)
            getLocation();
    }

    // ⭐ MAIN FUNCTION (UPDATED)
    private void fetchNearbyPlaces(double userLat, double userLon) {

        new Thread(() -> {
            try {

                // ⭐ FAST 5KM QUERY
                String query =
                        "[out:json][timeout:25];(" +
                                "node(around:5000,"+userLat+","+userLon+")[\"tourism\"][\"name\"];"+
                                "node(around:5000,"+userLat+","+userLon+")[\"amenity\"][\"name\"];"+
                                "node(around:5000,"+userLat+","+userLon+")[\"historic\"][\"name\"];"+
                                "way(around:5000,"+userLat+","+userLon+")[\"tourism\"][\"name\"];"+
                                "way(around:5000,"+userLat+","+userLon+")[\"amenity\"][\"name\"];"+
                                "way(around:5000,"+userLat+","+userLon+")[\"historic\"][\"name\"];"+
                                ");out center;";

                String response = "";

                // ⭐ TRY MULTIPLE SERVERS
                for(String server : OVERPASS_SERVERS){
                    try{
                        String urlString = server+"?data="+URLEncoder.encode(query,"UTF-8");
                        HttpURLConnection conn = (HttpURLConnection)new URL(urlString).openConnection();
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(20000);

                        if(conn.getResponseCode()==200){
                            InputStream is = conn.getInputStream();
                            Scanner sc = new Scanner(is).useDelimiter("\\A");
                            response = sc.hasNext()?sc.next():"";
                            sc.close();
                            break;
                        }
                    }catch(Exception ignored){}
                }

                if(response.isEmpty())
                    throw new Exception("All servers busy");

                JSONObject jsonObject = new JSONObject(response);
                JSONArray elements = jsonObject.getJSONArray("elements");

                ArrayList<NearbyPlaceModel> fetchedList = new ArrayList<>();
                Set<String> seen = new HashSet<>();

                for(int i=0;i<elements.length();i++){
                    JSONObject obj = elements.getJSONObject(i);
                    JSONObject tags = obj.optJSONObject("tags");
                    if(tags==null || !tags.has("name")) continue;

                    String name = tags.getString("name");
                    double lat = obj.optDouble("lat",Double.NaN);
                    double lon = obj.optDouble("lon",Double.NaN);

                    if(Double.isNaN(lat)){
                        JSONObject center = obj.optJSONObject("center");
                        if(center!=null){
                            lat=center.optDouble("lat",Double.NaN);
                            lon=center.optDouble("lon",Double.NaN);
                        }
                    }
                    if(Double.isNaN(lat)||Double.isNaN(lon)) continue;

                    String id = obj.optString("id");
                    if(seen.contains(id)) continue;
                    seen.add(id);

                    String type="place";
                    if(tags.has("tourism")) type=tags.getString("tourism");
                    else if(tags.has("amenity")) type=tags.getString("amenity");
                    else if(tags.has("historic")) type=tags.getString("historic");

                    float[] result=new float[1];
                    Location.distanceBetween(userLat,userLon,lat,lon,result);

                    fetchedList.add(new NearbyPlaceModel(name,lat,lon,type,result[0]));
                }

                Collections.sort(fetchedList,(a,b)->Double.compare(a.getDistance(),b.getDistance()));

                runOnUiThread(()->{
                    placeList.clear();
                    placeList.addAll(fetchedList);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this,fetchedList.size()+" places found within 5km",
                            Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Servers busy. Please try again.",
                                Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}