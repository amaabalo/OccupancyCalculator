package com.amiabalo.occupancycalculator;



import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements ConfigurationFragment.OnSubmissionListener {



    private FragmentManager mFragmentManager;
    private Configuration configuration;
    private GPUPhysicalLimits gpuPhysicalLimits;
    TabLayout tabLayout;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cc20, 49152 memory initial default settings
        configuration = new Configuration(2.0, 49152, null, 128, 48, 4096);

        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            Fragment fragment = ConfigurationFragment.newInstance(configuration);
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction = transaction.add(R.id.fragment_container, fragment);
            transaction.commit();
        }

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition())
                {
                    case 0:
                        fragment = ConfigurationFragment.newInstance(configuration);
                        break;
                    case 1:
                        fragment = GraphFragment.newInstance(configuration, gpuPhysicalLimits);
                        break;
                    case 2:
                        fragment = DataFragment.newInstance(configuration, gpuPhysicalLimits);
                        break;
                }
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        }
        );
        /*
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.compute_capabilities_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        */

    }

    @Override
    public void onSubmission(String[] array, Configuration configuration, GPUPhysicalLimits gpuPhysicalLimits) {
        this.configuration = configuration;
        this.gpuPhysicalLimits = gpuPhysicalLimits;
        tabLayout.getTabAt(2).select();
    }
}
