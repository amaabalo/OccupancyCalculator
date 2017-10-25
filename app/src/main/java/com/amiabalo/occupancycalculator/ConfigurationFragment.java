package com.amiabalo.occupancycalculator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfigurationFragment.OnSubmissionListener} interface
 * to handle interaction events.
 * Use the {@link ConfigurationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigurationFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONFIGURATION = "Configuration";
    private static final String ARG_PARAM2 = "param2";
    private static final int[] COMPUTE_CAPABILITY_IDS = new int[12];

    private static final int THREADS_PER_WARP_INDEX = 1;
    private static final int MAX_WARPS_PER_MULTIPROCESSOR_INDEX = 2;
    private static final int MAX_THREADS_PER_MULTIPROCESSOR_INDEX = 3;
    private static final int MAX_BLOCKS_PER_MULTIPROCESSOR_INDEX = 4;
    private static final int SHARED_MEMORY_PER_MULTIPROCESSOR_INDEX = 5;
    private static final int MAX_SHARED_MEMORY_PER_BLOCK_INDEX = 6;
    private static final int REGISTERS_PER_MULTIPROCESSOR_INDEX = 7;
    private static final int MAX_REGISTERS_PER_BLOCK_INDEX = 8;
    private static final int REGISTER_ALLOCATION_UNIT_SIZE_INDEX = 9;
    private static final int REGISTER_ALLOCATION_GRANULARITY_INDEX = 10;
    private static final int MAX_REGISTERS_PER_THREAD_INDEX = 11;
    private static final int SHARED_MEMORY_ALLOCATION_UNIT_SIZE_INDEX = 12;
    private static final int WARP_ALLOCATION_GRANULARITY_INDEX = 13;
    private static final int MAX_THREAD_BLOCK_SIZE_INDEX = 14;
    private static final int NO_SHARED_MEMORY_OPTIONS_INDEX = 15;




    private static final int CC_20_ID = R.array.cc_20_array;
    private static final int CC_21_ID = R.array.cc_21_array;
    private static final int CC_30_ID = R.array.cc_30_array;
    private static final int CC_32_ID = R.array.cc_32_array;
    private static final int CC_35_ID = R.array.cc_35_array;
    private static final int CC_37_ID = R.array.cc_37_array;
    private static final int CC_50_ID = R.array.cc_50_array;
    private static final int CC_52_ID = R.array.cc_52_array;
    private static final int CC_53_ID = R.array.cc_53_array;
    private static final int CC_60_ID = R.array.cc_60_array;
    private static final int CC_61_ID = R.array.cc_61_array;
    private static final int CC_62_ID = R.array.cc_62_array;

    String[] current_compute_capability_data;


    private String sharedMemorySize;
    private String cachingMode;
    private String computeCapability;
    private String threadsPerBlock;
    private String registersPerThread;
    private String sharedMemoryPerBlock;

    private int maxSharedMemoryPerBlock;
    private int maxRegistersPerThread;
    private int maxThreadsPerBlock;
    private int maxRegistersPerBlock;
    private int warpAllocationGranularity;
    private double currentComputeCapability;
    private int mSharedMemoryPerMultiprocessor;

    private Configuration mConfiguration;

    private OnSubmissionListener submissionListener;

    public ConfigurationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param configuration Configuration.
     * @return A new instance of fragment ConfigurationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigurationFragment newInstance(Configuration configuration)
    {
        ConfigurationFragment fragment = new ConfigurationFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONFIGURATION, configuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mConfiguration = (Configuration) getArguments().getSerializable(CONFIGURATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        COMPUTE_CAPABILITY_IDS[0] = CC_20_ID;
        COMPUTE_CAPABILITY_IDS[1] = CC_21_ID;
        COMPUTE_CAPABILITY_IDS[2] = CC_30_ID;
        COMPUTE_CAPABILITY_IDS[3] = CC_32_ID;
        COMPUTE_CAPABILITY_IDS[4] = CC_35_ID;
        COMPUTE_CAPABILITY_IDS[5] = CC_37_ID;
        COMPUTE_CAPABILITY_IDS[6] = CC_50_ID;
        COMPUTE_CAPABILITY_IDS[7] = CC_52_ID;
        COMPUTE_CAPABILITY_IDS[8] = CC_53_ID;
        COMPUTE_CAPABILITY_IDS[9] = CC_60_ID;
        COMPUTE_CAPABILITY_IDS[10] = CC_61_ID;
        COMPUTE_CAPABILITY_IDS[11] = CC_62_ID;

        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_configuration, container, false);
        Button button = v.findViewById(R.id.button);
        button.setOnClickListener(this);
        initialiseResourceUsage(v);
        initialiseComputeCapabilitySpinner(v);
        initialiseCacheModeSpinner(v);
        initialiseSharedMemorySpinner(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmissionListener) {
            submissionListener = (OnSubmissionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSubmissionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        submissionListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSubmissionListener {
        // TODO: Update argument type and name
        void onSubmission(String[] current_compute_capability_data, Configuration configuration,
                          GPUPhysicalLimits gpuPhysicalLimits);
    }

    private boolean isValidInput(View v)
    {
        EditText editText = v.findViewById(R.id.editText);
        String input = editText.getText().toString();
        if(input.isEmpty())
        {
            Toast.makeText(v.getContext(), "\"" + getString(R.string.threads_per_block_label)
                    + "\" is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (Integer.parseInt(input) > maxThreadsPerBlock)
        {
            Toast.makeText(v.getContext(), "\"" + getString(R.string.threads_per_block_label)
                    + "\" cannot be greater than " + maxThreadsPerBlock + ".", Toast.LENGTH_LONG).show();
            return false;

        }
        threadsPerBlock = input;

        editText = v.findViewById(R.id.editText2);
        input = editText.getText().toString();
        if(input.isEmpty())
        {
            Toast.makeText(v.getContext(), "\"" + getString(R.string.registers_per_thread_label)
                    + "\" is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(Integer.parseInt(input) > maxRegistersPerThread)
        {
            Toast.makeText(v.getContext(), "\"" + getString(R.string.registers_per_thread_label)
                    + "\" cannot be greater than " + maxRegistersPerThread + ".", Toast.LENGTH_SHORT).show();
            return false;
        }
        registersPerThread = input;

        editText = v.findViewById(R.id.editText3);
        input = editText.getText().toString();
        if(input.isEmpty())
        {
            Toast.makeText(v.getContext(), "\"" + getString(R.string.shared_memory_per_block_label)
                    + "\" is empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Integer.parseInt(input) > maxSharedMemoryPerBlock)
        {
            Toast.makeText(v.getContext(), "\"" + getString(R.string.shared_memory_per_block_label)
                    + "\" cannot be greater than " + maxSharedMemoryPerBlock, Toast.LENGTH_LONG).show();
            return false;
        }
        sharedMemoryPerBlock = input;

        return true;
    }

    private void hideEntry(View v, int textViewId, int spinnerId)
    {
        v.findViewById(textViewId).setVisibility(View.GONE);
        v.findViewById(spinnerId).setVisibility(View.GONE);
    }

    private void showEntry(View v, int textViewId, int spinnerId)
    {
        v.findViewById(textViewId).setVisibility(View.VISIBLE);
        v.findViewById(spinnerId).setVisibility(View.VISIBLE);
    }

    private void initialiseComputeCapabilitySpinner(final View v)
    {
        Spinner compute_capability_spinner = v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> compute_capability_adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.compute_capabilities_array, android.R.layout.simple_spinner_item);
        compute_capability_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        compute_capability_spinner.setAdapter(compute_capability_adapter);
        int i = compute_capability_adapter.getPosition(Double.toString(mConfiguration.getComputeCapability()));
        compute_capability_spinner.setSelection(i);
        compute_capability_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                computeCapability = (String)adapterView.getItemAtPosition((int)l);
                currentComputeCapability = Double.parseDouble(computeCapability);
                current_compute_capability_data = getResources().getStringArray(COMPUTE_CAPABILITY_IDS[(int)l]);
                maxRegistersPerThread = Integer.parseInt(current_compute_capability_data[MAX_REGISTERS_PER_THREAD_INDEX]);
                maxThreadsPerBlock = Integer.parseInt(current_compute_capability_data[MAX_THREAD_BLOCK_SIZE_INDEX]);
                maxRegistersPerBlock = Integer.parseInt(current_compute_capability_data[MAX_REGISTERS_PER_BLOCK_INDEX]);
                warpAllocationGranularity = Integer.parseInt(current_compute_capability_data[WARP_ALLOCATION_GRANULARITY_INDEX]);
                inspectCachingMode(currentComputeCapability);
                updateSharedMemorySpinner(v);
                if ( currentComputeCapability <= 5.0)
                    hideEntry(v, R.id.textView4, R.id.spinner3);
                else
                    showEntry(v, R.id.textView4, R.id.spinner3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
    }

    private void initialiseCacheModeSpinner(final View v)
    {
        Spinner caching_mode_spinner = (Spinner) v.findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> caching_mode_adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.caching_mode_array, android.R.layout.simple_spinner_item);
        caching_mode_spinner.setAdapter(caching_mode_adapter);

        if (mConfiguration.getCachingMode() != null)
        {
            int i = caching_mode_adapter.getPosition(mConfiguration.getCachingMode());
            caching_mode_spinner.setSelection(i);
        }

        caching_mode_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cachingMode = (String)adapterView.getItemAtPosition((int)l);
                inspectCachingMode(currentComputeCapability);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initialiseSharedMemorySpinner(View v)
    {
        Spinner shared_memory_spinner = v.findViewById(R.id.spinner2);
        shared_memory_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                sharedMemorySize = (String)adapterView.getItemAtPosition((int)l);
                mSharedMemoryPerMultiprocessor = Math.min(Integer.parseInt(sharedMemorySize),
                        Integer.parseInt(current_compute_capability_data[SHARED_MEMORY_PER_MULTIPROCESSOR_INDEX]));
                maxSharedMemoryPerBlock = Math.min(Integer.parseInt(sharedMemorySize),
                        Integer.parseInt(current_compute_capability_data[MAX_SHARED_MEMORY_PER_BLOCK_INDEX]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateSharedMemorySpinner(View v)
    {
        Spinner shared_memory_spinner = v.findViewById(R.id.spinner2);
        int no_shared_memory_options = Integer.parseInt(current_compute_capability_data[NO_SHARED_MEMORY_OPTIONS_INDEX]);
        String[] shared_memory_array = new String[no_shared_memory_options];
        for (int j = 0; j < no_shared_memory_options; j++)
        {
            shared_memory_array[j] = current_compute_capability_data[NO_SHARED_MEMORY_OPTIONS_INDEX + j + 1];
        }
        ArrayAdapter<CharSequence> shared_memory_adapter = new ArrayAdapter<CharSequence>(v.getContext(),
                android.R.layout.simple_spinner_item, shared_memory_array);
        shared_memory_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shared_memory_spinner.setAdapter(shared_memory_adapter);
        int i = shared_memory_adapter.getPosition(Integer.toString(mConfiguration.getSharedMemorySize()));
        shared_memory_spinner.setSelection(i);

    }

    @Override
    public void onClick(View v)
    {
        if(isValidInput(v.getRootView()))
        {
            if (submissionListener != null) {
                submissionListener.onSubmission(current_compute_capability_data, getConfiguration(), getGPUPhysicalLimits());
            }
        }
    }

    private Configuration getConfiguration()
    {
        return new Configuration(
                currentComputeCapability,
                Integer.parseInt(sharedMemorySize),
                cachingMode,
                Integer.parseInt(threadsPerBlock),
                Integer.parseInt(registersPerThread),
                Integer.parseInt(sharedMemoryPerBlock)
        );
    }

    private GPUPhysicalLimits getGPUPhysicalLimits()
    {
        return new GPUPhysicalLimits(
                Integer.parseInt(current_compute_capability_data[THREADS_PER_WARP_INDEX]),
                Integer.parseInt(current_compute_capability_data[MAX_WARPS_PER_MULTIPROCESSOR_INDEX]),
                Integer.parseInt(current_compute_capability_data[MAX_BLOCKS_PER_MULTIPROCESSOR_INDEX]),
                Integer.parseInt(current_compute_capability_data[MAX_THREADS_PER_MULTIPROCESSOR_INDEX]),
                maxThreadsPerBlock,
                Integer.parseInt(current_compute_capability_data[REGISTERS_PER_MULTIPROCESSOR_INDEX]),
                maxRegistersPerBlock,
                maxRegistersPerThread,
                mSharedMemoryPerMultiprocessor,
                maxSharedMemoryPerBlock,
                Integer.parseInt(current_compute_capability_data[REGISTER_ALLOCATION_UNIT_SIZE_INDEX]),
                current_compute_capability_data[REGISTER_ALLOCATION_GRANULARITY_INDEX],
                Integer.parseInt(current_compute_capability_data[SHARED_MEMORY_ALLOCATION_UNIT_SIZE_INDEX]),
                warpAllocationGranularity
        );
    }

    private void inspectCachingMode(double computeCapability)
    {
        if (computeCapability == 5.2)
        {
            if(cachingMode == getString(R.string.l1plusl2))
            {
                maxRegistersPerBlock = Integer.parseInt(current_compute_capability_data[REGISTERS_PER_MULTIPROCESSOR_INDEX]) / 2;
            }
            else
            {
                maxRegistersPerBlock = Integer.parseInt(current_compute_capability_data[REGISTERS_PER_MULTIPROCESSOR_INDEX]);
            }
        }
        if (computeCapability == 5.2 || computeCapability == 5.3)
        {
            if (cachingMode == getString(R.string.l1plusl2)) {
                warpAllocationGranularity = 2;
            } else {
                warpAllocationGranularity = 4;
            }
        }
    }

    private void initialiseResourceUsage(View v)
    {
        EditText editText = v.findViewById(R.id.editText);
        editText.setText(Integer.toString(mConfiguration.getThreadsPerBlock()));
        editText = v.findViewById(R.id.editText2);
        editText.setText(Integer.toString(mConfiguration.getRegistersPerThread()));
        editText = v.findViewById(R.id.editText3);
        editText.setText(Integer.toString(mConfiguration.getSharedMemoryPerBlock()));
    }

}
