package com.amiabalo.occupancycalculator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONFIGURATION = "Configuration";
    private static final String GPU_PHYSICAL_LIMITS = "GPU Physical Limits";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Configuration mConfiguration;
    private GPUPhysicalLimits mGpuPhysicalLimits;

    private int mWarpsPerBlock;
    private int mRegistersPerBlock;
    private int mSharedMemoryPerBlock;

    private int mWarpLimitPerSM;
    private int mRegisterLimitPerSM;
    private int mSharedMemoryLimitPerSM;

    private int allocatableBlocksByWarps;
    private int allocatableBlocksByRegisters;
    private int allocatableBlocksBySharedMemory;



    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gpuPhysicalLimits GPU Physical Limits.
     * @return A new instance of fragment DataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataFragment newInstance(Configuration configuration, GPUPhysicalLimits gpuPhysicalLimits) {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONFIGURATION, configuration);
        args.putSerializable(GPU_PHYSICAL_LIMITS, gpuPhysicalLimits);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mConfiguration = (Configuration) getArguments().getSerializable(CONFIGURATION);
            mGpuPhysicalLimits = (GPUPhysicalLimits) getArguments().getSerializable(GPU_PHYSICAL_LIMITS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data, container, false);
        calculate();
        displayData(v);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private int ceiling(double value, int multiple)
    {
        if (multiple == 1) return (int)Math.ceil(value);
        else
            return (int) Math.ceil(value / multiple) * multiple;

    }

    private int floor(double value, int multiple)
    {
        if (multiple == 1) return (int) Math.floor(value);
        else
            return (int) Math.floor(value / multiple) * multiple;
    }

    private void calculate()
    {
        mWarpsPerBlock = ceiling((double)mConfiguration.getThreadsPerBlock()/
                                mGpuPhysicalLimits.getThreadsPerWarp(), 1);
        mRegistersPerBlock = mWarpsPerBlock;
        mSharedMemoryPerBlock = ceiling(mConfiguration.getSharedMemoryPerBlock(),
                                        mGpuPhysicalLimits.getSharedMemoryAllocationUnitSize());

        mWarpLimitPerSM = mGpuPhysicalLimits.getMaxWarpsPerMultiprocessor();
        mRegisterLimitPerSM = floor(((double)mGpuPhysicalLimits.getMaxRegistersPerBlock()/
                                    (double)(ceiling(mConfiguration.getRegistersPerThread() *
                                            mGpuPhysicalLimits.getThreadsPerWarp(),
                                            mGpuPhysicalLimits.getRegisterAllocationUnitSize()))),
                                    mGpuPhysicalLimits.getWarpAllocationGranularity());
        mSharedMemoryLimitPerSM = mGpuPhysicalLimits.getMaxSharedMemoryPerBlock();

        allocatableBlocksByWarps = Math.min(mGpuPhysicalLimits.getMaxBlocksPerMultiprocessor(),
                (int)Math.floor((double)mWarpLimitPerSM / (double)mWarpsPerBlock));
        if (mConfiguration.getRegistersPerThread() > 0)
            allocatableBlocksByRegisters = floor((double)mRegisterLimitPerSM / (double)mRegistersPerBlock, 1)*
                                            floor((double)mGpuPhysicalLimits.getRegistersPerMultiprocessor() /
                                                    (double)mGpuPhysicalLimits.getMaxRegistersPerBlock(), 1);
        else
            allocatableBlocksByRegisters = mGpuPhysicalLimits.getMaxBlocksPerMultiprocessor();

        if (mConfiguration.getSharedMemoryPerBlock() > 0)
            allocatableBlocksBySharedMemory = mGpuPhysicalLimits.getSharedMemoryPerMultiprocessor() / mSharedMemoryPerBlock;
        else
            allocatableBlocksByRegisters = mGpuPhysicalLimits.getMaxBlocksPerMultiprocessor();
    }

    private void displayData(View v)
    {
        TextView textView = v.findViewById(R.id.threads_per_warp);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getThreadsPerWarp()));

        textView = v.findViewById(R.id.max_warps_per_multiprocessor);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxWarpsPerMultiprocessor()));

        textView = v.findViewById(R.id.max_blocks_per_multiprocessor);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxBlocksPerMultiprocessor()));

        textView= v.findViewById(R.id.max_threads_per_multiprocessor);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxThreadsPerMultiprocessor()));

        textView = v.findViewById(R.id.max_threads_per_block);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxThreadsPerBlock()));

        textView = v.findViewById(R.id.max_registers_per_multiprocessor);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getRegistersPerMultiprocessor()));

        textView = v.findViewById(R.id.max_registers_per_block);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxRegistersPerBlock()));

        textView = v.findViewById(R.id.max_registers_per_thread);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxRegistersPerThread()));

        textView = v.findViewById(R.id.shared_memory_per_multiprocessor);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getSharedMemoryPerMultiprocessor()));

        textView = v.findViewById(R.id.max_shared_memory_per_block);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getMaxSharedMemoryPerBlock()));

        textView = v.findViewById(R.id.register_allocation_unit_size);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getRegisterAllocationUnitSize()));

        textView = v.findViewById(R.id.register_allocation_granularity);
        textView.setText(mGpuPhysicalLimits.getRegisterAllocationGranularity());

        textView = v.findViewById(R.id.shared_memory_allocation_unit_size);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getSharedMemoryAllocationUnitSize()));

        textView = v.findViewById(R.id.warp_allocation_granularity);
        textView.setText(Integer.toString(mGpuPhysicalLimits.getWarpAllocationGranularity()));

        // Warp Data
        textView = v.findViewById(R.id.warps_per_block);
        textView.setText(Integer.toString(mWarpsPerBlock));

        textView = v.findViewById(R.id.warp_limit_per_sm);
        textView.setText(Integer.toString(mWarpLimitPerSM));

        textView = v.findViewById(R.id.allocatable_blocks_by_warps);
        textView.setText(Integer.toString(allocatableBlocksByWarps));

        // Register Data
        textView = v.findViewById(R.id.registers_per_block);
        textView.setText(Integer.toString(mRegistersPerBlock));

        textView = v.findViewById(R.id.register_limit_per_sm);
        textView.setText(Integer.toString(mRegisterLimitPerSM));

        textView = v.findViewById(R.id.allocatable_blocks_by_registers);
        textView.setText(Integer.toString(allocatableBlocksByRegisters));

        //Shared Memory Data
        textView = v.findViewById(R.id.shared_memory_per_block);
        textView.setText(Integer.toString(mSharedMemoryPerBlock));

        textView = v.findViewById(R.id.shared_memory_limit_per_sm);
        textView.setText(Integer.toString(mSharedMemoryLimitPerSM));

        textView = v.findViewById(R.id.allocatable_blocks_by_shared_memory);
        textView.setText(Integer.toString(allocatableBlocksBySharedMemory));


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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
