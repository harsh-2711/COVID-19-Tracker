package com.developer.abhinavraj.covid19tracker.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.applivery.applvsdklib.Applivery;
import com.developer.abhinavraj.covid19tracker.R;
import com.developer.abhinavraj.covid19tracker.fragments.IndividualWorldFragment;
import com.developer.abhinavraj.covid19tracker.fragments.UpdatesFragment;
import com.developer.abhinavraj.covid19tracker.fragments.WorldCasesFragment;
import com.developer.abhinavraj.covid19tracker.others.ForceUpdateChecker;
import com.developer.abhinavraj.covid19tracker.others.SNSConfig;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements ForceUpdateChecker.OnUpdateNeededListener {

    BottomNavigationView bottomNavigation;
    ImageView imageView;
    private JSONObject jsonObject;
    private WorldCasesFragment worldCasesFragment;
    private IndividualWorldFragment individualWorldFragment;
    private UpdatesFragment updatesFragment;
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(worldCasesFragment);
                            return true;
                        case R.id.navigation_sms:
                            openFragment(individualWorldFragment);
                            return true;
                        case R.id.navigation_notifications:
                            openFragment(updatesFragment);
                            return true;
                    }
                    return false;
                }
            };
    private FrameLayout frameLayout;
    private boolean isExecuted = true;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private FirebaseAnalytics firebaseAnalytics;
    private BroadcastReceiver broadcastReceiver;
    private SharedPreferences preferences;
    private static final String NOTIFICATION_PREFERNCES = "Notification_Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().hide();
        } catch (Exception e) {
            // pass
        }

        setContentView(R.layout.activity_main);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        Applivery.setCheckForUpdatesBackground(true);

        installListener();
        preferences = getSharedPreferences(NOTIFICATION_PREFERNCES, MODE_PRIVATE);
        preferences.getBoolean("State", true);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        imageView = findViewById(R.id.notFound);
        frameLayout = findViewById(R.id.container);

        worldCasesFragment = new WorldCasesFragment();
        individualWorldFragment = new IndividualWorldFragment();
        updatesFragment = new UpdatesFragment();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        /*

        TODO: Implement Side Navigation

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.side_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.navigation_home:
                        Toast.makeText(MainActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_advisory:
                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_helpline:
                        Toast.makeText(MainActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_sources:
                        Toast.makeText(MainActivity.this, "Sources", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }


                return true;

            }
        });

        */


        if (isNetworkAvailable()) {
            try {
                new SubscribeSNS().execute();
            } catch (Exception e) {
                Log.e("Token", e.toString());
            }
            openFragment(individualWorldFragment);
        } else {
            setExecutionStatus(false);
            openFragment(worldCasesFragment);
        }

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        firebaseAnalytics.setCurrentScreen(this, fragment.getClass().getSimpleName(), fragment.getClass().getSimpleName());
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navigation_helpline:
                startActivity(new Intent(MainActivity.this, HelplineActivity.class));
                return true;
            case R.id.navigation_sources:
                startActivity(new Intent(MainActivity.this, SourceActivity.class));
                return true;
//            case R.id.navigation_notif_settings:
//                boolean currentState = preferences.getBoolean("State", true);
//                showDialog(currentState);
//                return true;
            case R.id.navigation_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog(final boolean state) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(state)
            builder.setMessage("Turn off notifications ");
        else
            builder.setMessage("Turn on notifications");
        builder.setTitle("Notifications");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(state) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("State", false);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("State", true);
                    editor.apply();
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (getSupportFragmentManager().getBackStackEntryCount() != 1)
                try {
                    if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("com.developer.abhinavraj.covid19tracker.fragments.IndividualFragment"))
                        return super.onKeyDown(keyCode, event);
                } catch (Exception e) {
                    // pass
                }
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
        dialog.show();
    }

    public boolean getExecutionStatus() {
        return isExecuted;
    }

    public void setExecutionStatus(boolean status) {
        this.isExecuted = status;
        if (status) {
            frameLayout.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            frameLayout.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("StaticFieldLeak")
    private class SubscribeSNS extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            new SNSConfig(getApplicationContext());
            return null;
        }
    }

    private void installListener() {

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();

                    try {
                        NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

                        NetworkInfo.State state = info.getState();
                        Log.d("BroadcastReceiver", info.toString() + " " + state.toString());

                        if (state == NetworkInfo.State.CONNECTED)
                            setExecutionStatus(true);
                        else
                            setExecutionStatus(false);
                    } catch (Exception e) {
                        // pass
                    }
                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

}
