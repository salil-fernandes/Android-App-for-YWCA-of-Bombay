package com.sevatech.ywca.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sevatech.ywca.Connection_Detector;
import com.sevatech.ywca.InternetConnection;
import com.sevatech.ywca.MainActivity;
import com.sevatech.ywca.PreNMProfileActivity;
import com.sevatech.ywca.PreProfileActivity;
import com.sevatech.ywca.R;
import com.sevatech.ywca.navigation.aboutUs.AboutUsFragment;
import com.sevatech.ywca.navigation.adminApproval.AdminApprovalFragment;
import com.sevatech.ywca.navigation.analytics.AnalyticsFragment;
import com.sevatech.ywca.navigation.contactUs.ContactUsFragment;
import com.sevatech.ywca.navigation.events.AdminEventsFragment;
import com.sevatech.ywca.navigation.events.EventsFragment;
import com.sevatech.ywca.navigation.events.MemberEventsFragment;
import com.sevatech.ywca.navigation.initiatives.InitiativesFragment;
import com.sevatech.ywca.navigation.successStories.SuccessStoriesFragment;

public class FragmentBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUserName;
    SharedPreferences sp;

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    FloatingActionButton fab;
    Button logout;
    FirebaseAuth firebaseAuth;
    ActionBarDrawerToggle toggle;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    String name, status;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_gradient));
        }


        int o = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);

        logout = findViewById(R.id.logout);
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mFragmentManager = getSupportFragmentManager();
        fab.setVisibility(View.GONE);

        View headerView = navigationView.getHeaderView(0);
        tvUserName = headerView.findViewById(R.id.user_name);

        firebaseAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("f1", MODE_PRIVATE);

        // name = sp.getString("name", "");                       //Rectify 1

        status = sp.getString("status", "");
        switch (status) {
            case "Admin":
                tvUserName.setText("Admin");
                break;
            case "Member":
                String memberName = sp.getString("memberName", "");
                tvUserName.setText(memberName);
                break;
            case "Staff":
                String staffName = sp.getString("staffName", "");
                tvUserName.setText(staffName);
                break;
            default:
                String name = sp.getString("name", "");
                tvUserName.setText(name);
                break;
        }


        if (!status.equals("Admin")) {
            tvUserName.setOnClickListener(new View.OnClickListener() {             //Rectify 2
                @Override
                public void onClick(View v) {
                    InternetConnection intconn = new InternetConnection(FragmentBaseActivity.this);
                    if (!intconn.isNetworkAvailable()) {
                        intconn.Dialog(FragmentBaseActivity.this);
                    } else {
                        if (status.equals("Member") || status.equals("Staff")) {
                            //Toast.makeText(getApplicationContext(), "Redirect to Member/Staff profile", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(FragmentBaseActivity.this, PreProfileActivity.class);
                            startActivity(i);
//                        finish();
                        } else {
                            // Toast.makeText(getApplicationContext(), "Redirect to NonMember profile", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(FragmentBaseActivity.this, PreNMProfileActivity.class);
                            startActivity(i);
//                        finish();
                        }
                    }

                }
            });
        }

        if (!status.equals("Admin")) {
            Menu drawerMenu = navigationView.getMenu();
            drawerMenu.findItem(R.id.nav_analytics).setVisible(false);
            drawerMenu.findItem(R.id.nav_admin_approval).setVisible(false);
        }

        String firebaseAuth = FirebaseAuth.getInstance().getUid();
        System.out.println("Uid "+firebaseAuth);

        if (savedInstanceState == null) {
            checkItemEvents();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);

                Fragment backFragment = mFragmentManager.findFragmentById(R.id.fragment_container);

                assert backFragment != null;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(backFragment.getContext());
                alertDialogBuilder.setMessage("Do you want to Logout?");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (status.equals("Member") || status.equals("Staff"))
                            logoutMember();
                        else if (status.equals("Admin"))
                            logoutAdmin();
                        else
                            logoutNonMember();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#417eca"));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                InternetConnection intconn = new InternetConnection(FragmentBaseActivity.this);
                if (!intconn.isNetworkAvailable()) {
                    intconn.Dialog(FragmentBaseActivity.this);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), Admin_add_event.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void logoutNonMember() {                           //rectify 3

        SharedPreferences.Editor editor = sp.edit();
        String status1 = "";

        editor.putString("status", status1);
        editor.putString("name", status1);
        editor.putString("phone", status1);
        editor.putString("nmKey", status1);
        editor.putBoolean("firstStart", false);

        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        firebaseAuth.signOut();
        this.finish();
    }

    private void logoutAdmin() {                   //Rectify  4

        firebaseAuth.signOut();
        SharedPreferences.Editor editor = sp.edit();
        String status1 = "";
        editor.putString("status", status1);
        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }

    private void logoutMember() {                      //Rectify  5
        firebaseAuth.signOut();
        SharedPreferences.Editor editor = sp.edit();
        String status1 = "";
        editor.putString("status", status1);
        editor.putString("memberName", status1);
        editor.putString("memberPhone", status1);
        editor.putString("staffName", status1);
        editor.putString("staffPhone", status1);
        editor.putString("memKey", status1);
        editor.putString("staffKey", status1);

        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }


    @SuppressLint("WrongConstant")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_events: {
                InternetConnection intconn = new InternetConnection(FragmentBaseActivity.this);
                if (!intconn.isNetworkAvailable()) {
                    intconn.Dialog(FragmentBaseActivity.this);
                }
                else {
                    Fragment eventsFragment;
                    if (status.equals("Member") || status.equals("Staff"))
                        eventsFragment = new MemberEventsFragment();
                    else if (status.equals("Admin"))
                        eventsFragment = new AdminEventsFragment();
                    else
                        eventsFragment = new EventsFragment();
                    changeFragment(eventsFragment);
                    //                checkItemEvents();
                    break;
                }
            }

            case R.id.nav_about_us: {
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                changeFragment(aboutUsFragment);
                navigationView.setCheckedItem(R.id.nav_about_us);
                break;
            }

            case R.id.nav_initiatives: {
                InitiativesFragment initiativesFragment = new InitiativesFragment();
                changeFragment(initiativesFragment);
                navigationView.setCheckedItem(R.id.nav_initiatives);
                break;
            }

            case R.id.nav_success_stories: {
                SuccessStoriesFragment successStoriesFragment = new SuccessStoriesFragment();
                changeFragment(successStoriesFragment);
                navigationView.setCheckedItem(R.id.nav_success_stories);
                break;
            }

            case R.id.nav_contact_us: {
                ContactUsFragment contactUsFragment = new ContactUsFragment();
                changeFragment(contactUsFragment);
                navigationView.setCheckedItem(R.id.nav_contact_us);
                break;
            }

            case R.id.nav_analytics: {
                InternetConnection intconn = new InternetConnection(FragmentBaseActivity.this);
                if (!intconn.isNetworkAvailable()) {
                    intconn.Dialog(FragmentBaseActivity.this);
                }
                else {
                    AnalyticsFragment analyticsFragment = new AnalyticsFragment();
                    changeFragment(analyticsFragment);
                    navigationView.setCheckedItem(R.id.nav_analytics);
                    break;
                }
            }

            case R.id.nav_admin_approval: {
                InternetConnection intconn = new InternetConnection(FragmentBaseActivity.this);
                if (!intconn.isNetworkAvailable()) {
                    intconn.Dialog(FragmentBaseActivity.this);
                }
                else {
                    AdminApprovalFragment approvalFragment = new AdminApprovalFragment();
                    changeFragment(approvalFragment);
                    navigationView.setCheckedItem(R.id.nav_admin_approval);
                    break;
                }
            }

        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void changeFragment(Fragment fragment) {
        String backFragment = mFragmentManager.findFragmentById(R.id.fragment_container).getClass().getSimpleName();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_container, fragment);
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (backFragment.contains("EventsFragment") && mFragmentManager.getBackStackEntryCount() < 1) {
            mFragmentTransaction.addToBackStack(null);
        }
        mFragmentTransaction.commit();
    }

    public void checkItemEvents() {
        if (status.equals("Member") || status.equals("Staff")) {                  //rectify 6
            final MemberEventsFragment eventsFragment = new MemberEventsFragment();
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, eventsFragment).commit();

            if (status.equals("Member")) {
                String memberName = sp.getString("memberName", "");
                tvUserName.setText(memberName);
            } else {
                String staffName = sp.getString("staffName", "");
                tvUserName.setText(staffName);
            }

            fab.setVisibility(View.GONE);

        } else if (status.equals("Admin")) {                                //rectify 7
            final AdminEventsFragment eventsFragment = new AdminEventsFragment();
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, eventsFragment).commit();

            tvUserName.setText(status);
            fab.setVisibility(View.VISIBLE);

        } else {                                                                //rectify 8
            final EventsFragment eventsFragment = new EventsFragment();
            mFragmentManager.beginTransaction().replace(R.id.fragment_container, eventsFragment).commit();

            fab.setVisibility(View.GONE);
            name = sp.getString("name", "");
            tvUserName.setText(name);

        }

        navigationView.setCheckedItem(R.id.nav_events);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mFragmentManager.getBackStackEntryCount() > 0) {

            mFragmentManager.popBackStack();
        } else {
            Fragment backFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            assert backFragment != null;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(backFragment.getContext());
            alertDialogBuilder.setMessage("Do you want to Exit?");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FragmentBaseActivity.super.onBackPressed();
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#417eca"));

        }
    }

}
