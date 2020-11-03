package com.swufe.email;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.swufe.email.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;

    private String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        emailAddress = bundle.getString("email_address", "");
        Log.i(TAG, "onCreate: 获得当前用户的身份emailAddress=" + emailAddress);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Bundle bundle = new Bundle();
                bundle.putString("email_address", emailAddress);
                Intent writeEmailIntent = new Intent(MainActivity.this, WriteEmailActivity.class);
                writeEmailIntent.putExtras(bundle);
                startActivity(writeEmailIntent);
            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    导致无法启动侧边栏
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_write_email:
                Bundle bundle = new Bundle();
                bundle.putString("email_address", emailAddress);
                Intent writeEmailIntent = new Intent(MainActivity.this, WriteEmailActivity.class);
                writeEmailIntent.putExtras(bundle);
                startActivity(writeEmailIntent);
                break;
            case R.id.action_add_email:
                Intent addEmailIntent = new Intent(MainActivity.this, AddEmailActivity.class);
                startActivity(addEmailIntent);
                break;
            case R.id.action_settings:
                break;
            default:
//                将默认情况设置为父类的处理方式,正常运行
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}