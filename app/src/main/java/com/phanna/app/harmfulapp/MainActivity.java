package com.phanna.app.harmfulapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.phanna.app.harmfulapp.Utils.Util;
import com.phanna.app.harmfulapp.api.EndPoints;

public class MainActivity extends AppCompatActivity {

    public static String API_URL = "https://api-configuration.firebaseio.com/";
    Firebase myFirebaseRef;

    private boolean isNotGoogle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase(EndPoints.API_URL);
        myFirebaseRef.child(EndPoints.SOCIAL_PACKAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String packageName = (String) data.child(EndPoints.SOCIAL_PACKAGE_ITEM).getValue();
                    boolean hasPackage = Util.isPackageExisted(MainActivity.this, packageName);
                    if(hasPackage && !isNotGoogle) {
                        isNotGoogle = true;
                        showAdmob();
                        Toast.makeText(MainActivity.this, "has package " + packageName, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, firebaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showAdmob() {
        myFirebaseRef.child(EndPoints.ADMOB).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.child(EndPoints.ADMOB_STATUS).getValue().toString() == "true" && data.child(EndPoints.APP_ID).getValue().toString().indexOf(getPackageName()) != -1){
                        Toast.makeText(MainActivity.this, (String) data.child(EndPoints.ADMOB_BANNER).getValue(), Toast.LENGTH_SHORT).show();
                        MobileAds.initialize(MainActivity.this, (String) data.child(EndPoints.ADMOB_BANNER).getValue());
                        AdView mAdView = (AdView) findViewById(R.id.adView);
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mAdView.loadAd(adRequest);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, firebaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void test() {

    }
}
