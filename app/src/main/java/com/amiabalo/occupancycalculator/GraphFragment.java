package com.amiabalo.occupancycalculator;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONFIGURATION = "Configuration";
    private static final String GPU_PHYSICAL_LIMITS = "GPU Physical Limits";

    private Configuration configuration;
    private GPUPhysicalLimits gpuPhysicalLimits;

    private int allocatableBlocksByWarps;
    private int allocatableBlocksByRegisters;
    private int allocatableBlocksBySharedMemory;

    private int mUserOccupancy;

    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;

    private PointsGraphSeries<DataPoint> mUserSeries1;
    private PointsGraphSeries<DataPoint> mUserSeries2;
    private PointsGraphSeries<DataPoint> mUserSeries3;

    private OnFragmentInteractionListener mListener;

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param configuration Parameter 1.
     * @param gpuPhysicalLimits Parameter 2.
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(Configuration configuration, GPUPhysicalLimits gpuPhysicalLimits) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONFIGURATION, configuration);
        args.putSerializable(GPU_PHYSICAL_LIMITS, gpuPhysicalLimits);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("lool", "not null");
            configuration = (Configuration) getArguments().getSerializable(CONFIGURATION);
            gpuPhysicalLimits = (GPUPhysicalLimits) getArguments().getSerializable(GPU_PHYSICAL_LIMITS);
        }
        else
            Log.d("lool", "null");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        mUserOccupancy = calculateOccupancy(configuration.getThreadsPerBlock(),
                            configuration.getRegistersPerThread(),
                            configuration.getSharedMemoryPerBlock());
        graphOccupancyByBlockSize(v);
        graphOccupancyBySharedMemory(v);
        graphOccupancyByRegisterUsage(v);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private int calculateOccupancy(int threadsPerBlock, int registersPerThread, int memoryPerBlock)
    {
        int warpsPerBlock = (int)Math.ceil((double)threadsPerBlock/ gpuPhysicalLimits.getThreadsPerWarp());
        int registersPerBlock = warpsPerBlock;
        int sharedMemoryPerBlock = (int) Math.ceil((double) memoryPerBlock /
                gpuPhysicalLimits.getSharedMemoryAllocationUnitSize()) *
                gpuPhysicalLimits.getSharedMemoryAllocationUnitSize();
        int warpsLimitPerSM = gpuPhysicalLimits.getMaxWarpsPerMultiprocessor();
        int registersLimitPerSM = (int)Math.floor((gpuPhysicalLimits.getMaxRegistersPerBlock()/
                                        (Math.ceil(registersPerThread *
                                                gpuPhysicalLimits.getThreadsPerWarp() /
                                                (double)gpuPhysicalLimits.getRegisterAllocationUnitSize()) *
                                                gpuPhysicalLimits.getRegisterAllocationUnitSize())) /
                                                (double)gpuPhysicalLimits.getWarpAllocationGranularity()) *
                                                gpuPhysicalLimits.getWarpAllocationGranularity();
        int sharedMemoryLimitPerSM = gpuPhysicalLimits.getMaxSharedMemoryPerBlock();

        allocatableBlocksByWarps = Math.min(gpuPhysicalLimits.getMaxBlocksPerMultiprocessor(),
                                            (int)Math.floor((double)warpsLimitPerSM / (double)warpsPerBlock));
        if (registersPerThread > 0)
            allocatableBlocksByRegisters = registersLimitPerSM / registersPerBlock;
        else
            allocatableBlocksByRegisters = gpuPhysicalLimits.getMaxBlocksPerMultiprocessor();

        if (memoryPerBlock > 0)
            allocatableBlocksBySharedMemory = gpuPhysicalLimits.getSharedMemoryPerMultiprocessor() / memoryPerBlock;
        else
            allocatableBlocksByRegisters = gpuPhysicalLimits.getMaxBlocksPerMultiprocessor();

        return Math.min(Math.min(allocatableBlocksByWarps, allocatableBlocksByRegisters),
                            allocatableBlocksBySharedMemory) * warpsPerBlock;
    }

    private void graphOccupancyByBlockSize(View v)
    {
        GraphView graphView = (GraphView) v.findViewById(R.id.occupancy_by_block_size);
        int count = gpuPhysicalLimits.getMaxThreadsPerBlock();
        int interval = 32;
        DataPoint[] points = new DataPoint[count / interval];
        int threadsPerBlock;
        int warpsPerSM;
        for (int i = 0; i < count / interval; i++)
        {
            threadsPerBlock = (i + 1) * interval;
            warpsPerSM = calculateOccupancy(threadsPerBlock,
                                            configuration.getRegistersPerThread(),
                                            configuration.getSharedMemoryPerBlock());
            Log.d("DATA", Integer.toString(threadsPerBlock) + ", " + Integer.toString(warpsPerSM));
            points[i] = new DataPoint((double)threadsPerBlock, (double) warpsPerSM);
        }
        mSeries1 = new LineGraphSeries<>(points);
        graphView.addSeries(mSeries1);

        mUserSeries1 = new PointsGraphSeries<>(new DataPoint[]{new DataPoint((double)configuration.getThreadsPerBlock(), (double)mUserOccupancy)});
        graphView.addSeries(mUserSeries1);
        mUserSeries1.setShape(PointsGraphSeries.Shape.TRIANGLE);
        mUserSeries1.setColor(Color.RED);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(gpuPhysicalLimits.getMaxWarpsPerMultiprocessor());
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(count / 2);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        int xLabelInterval = 100;
        graphView.getGridLabelRenderer().setNumHorizontalLabels(count / xLabelInterval);
        int yLabelInterval = 5;
        graphView.getGridLabelRenderer().setNumVerticalLabels(gpuPhysicalLimits.getMaxWarpsPerMultiprocessor() / yLabelInterval);
        graphView.getGridLabelRenderer().setTextSize(25);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.block_size_x_axis_label));
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.occupancy_y_axis_label));
        //graphView.getGridLabelRenderer().setLabelsSpace(0);
        graphView.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);
        graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(45);
        graphView.setTitle(getString(R.string.block_size_title));
        graphView.getViewport().setBackgroundColor(Color.LTGRAY);
    }

    private void graphOccupancyBySharedMemory(View v)
    {
        GraphView graphView = (GraphView) v.findViewById(R.id.occupancy_by_shared_memory);
        int count = gpuPhysicalLimits.getMaxSharedMemoryPerBlock();
        int interval = 512;
        DataPoint[] points = new DataPoint[(count / interval) + 1];
        int sharedMemoryPerBlock;
        int warpsPerSM;
        for (int i = 0; i <= count / interval; i++)
        {
            sharedMemoryPerBlock = i * interval;
            warpsPerSM = calculateOccupancy(configuration.getThreadsPerBlock(),
                    configuration.getRegistersPerThread(),
                    sharedMemoryPerBlock);
            Log.d("DATA", Integer.toString(sharedMemoryPerBlock) + ", " + Integer.toString(warpsPerSM));
            points[i] = new DataPoint((double)sharedMemoryPerBlock, (double) warpsPerSM);
        }
        mSeries2 = new LineGraphSeries<>(points);
        graphView.addSeries(mSeries2);

        mUserSeries2 = new PointsGraphSeries<>(new DataPoint[]{new DataPoint((double)configuration.getSharedMemoryPerBlock(), (double)mUserOccupancy)});
        graphView.addSeries(mUserSeries2);
        mUserSeries2.setShape(PointsGraphSeries.Shape.TRIANGLE);
        mUserSeries2.setColor(Color.RED);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(gpuPhysicalLimits.getMaxWarpsPerMultiprocessor());
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(count / 2);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        int labelInterval = 10000;
        //graphView.getGridLabelRenderer().setNumHorizontalLabels(count / labelInterval);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(10);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(45);
        int yLabelInterval = 5;
        graphView.getGridLabelRenderer().setNumVerticalLabels(gpuPhysicalLimits.getMaxWarpsPerMultiprocessor() / yLabelInterval);
        graphView.getGridLabelRenderer().setTextSize(25);
        graphView.setTitle(getString(R.string.shared_memory_title));
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.shared_memory_x_axis_label));
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.occupancy_y_axis_label));
        //graphView.getGridLabelRenderer().setLabelsSpace(0);
        graphView.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);
        graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        graphView.getViewport().setBackgroundColor(Color.LTGRAY);

    }

    private void graphOccupancyByRegisterUsage(View v)
    {
        GraphView graphView = (GraphView) v.findViewById(R.id.occupancy_by_registers);
        int count = gpuPhysicalLimits.getMaxRegistersPerThread();
        int interval = 1;
        DataPoint[] points = new DataPoint[count/interval + 1];
        int registersPerThread;
        int warpsPerSM;
        for (int i = 0; i < count/interval; i++)
        {
            registersPerThread = (i * interval) + 1;
            warpsPerSM = calculateOccupancy(configuration.getThreadsPerBlock(),
                                            registersPerThread,
                                            configuration.getSharedMemoryPerBlock());
            Log.d("DATA", Integer.toString(registersPerThread) + ", " + Integer.toString(warpsPerSM));
            points[i] = new DataPoint((double)registersPerThread, (double) warpsPerSM);
        }
        points[count/interval] = new DataPoint((double) count + 1, 0);
        mSeries3 = new LineGraphSeries<>(points);
        mUserSeries3 = new PointsGraphSeries<>(new DataPoint[]{new DataPoint((double)configuration.getRegistersPerThread(), (double)mUserOccupancy)});



        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(gpuPhysicalLimits.getMaxWarpsPerMultiprocessor());
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(count / 2);


        int labelInterval = 5;
        graphView.getGridLabelRenderer().setNumHorizontalLabels(count / labelInterval);
        int yLabelInterval = 5;
        graphView.getGridLabelRenderer().setNumVerticalLabels(gpuPhysicalLimits.getMaxWarpsPerMultiprocessor() / yLabelInterval);
        graphView.getGridLabelRenderer().setTextSize(25);
        graphView.setTitle(getString(R.string.register_usage_title));
        graphView.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.register_usage_x_axis_label));
        graphView.getGridLabelRenderer().setVerticalAxisTitle(getString(R.string.occupancy_y_axis_label));
        graphView.getGridLabelRenderer().setLabelsSpace(0);
        graphView.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);
        graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        graphView.getViewport().setBackgroundColor(Color.LTGRAY);


        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        graphView.addSeries(mSeries3);

        graphView.addSeries(mUserSeries3);
        mUserSeries3.setShape(PointsGraphSeries.Shape.TRIANGLE);
        mUserSeries3.setColor(Color.RED);

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
