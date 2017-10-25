package com.amiabalo.occupancycalculator;

import java.io.Serializable;

/**
 * Created by jacquelineabalo on 8/21/17.
 */

public class GPUPhysicalLimits implements Serializable{
    private int threadsPerWarp;
    private int maxWarpsPerMultiprocessor;
    private int maxBlocksPerMultiprocessor;
    private int maxThreadsPerMultiprocessor;
    private int maxThreadsPerBlock;
    private int registersPerMultiprocessor;
    private int maxRegistersPerBlock;
    private int maxRegistersPerThread;
    private int sharedMemoryPerMultiprocessor;
    private int maxSharedMemoryPerBlock;
    private int registerAllocationUnitSize;
    private String registerAllocationGranularity;
    private int sharedMemoryAllocationUnitSize;
    private int warpAllocationGranularity;

    public GPUPhysicalLimits(
            int threadsPerWarp,
            int maxWarpsPerMultiprocessor,
            int maxBlocksPerMultiprocessor,
            int maxThreadsPerMultiprocessor,
            int maxThreadsPerBlock,
            int registersPerMultiprocessor,
            int maxRegistersPerBlock,
            int maxRegistersPerThread,
            int sharedMemoryPerMultiprocessor,
            int maxSharedMemoryPerBlock,
            int registerAllocationUnitSize,
            String registerAllocationGranularity,
            int sharedMemoryAllocationUnitSize,
            int warpAllocationGranularity
    )
    {
        this.threadsPerWarp = threadsPerWarp;
        this.maxWarpsPerMultiprocessor = maxWarpsPerMultiprocessor;
        this.maxBlocksPerMultiprocessor = maxBlocksPerMultiprocessor;
        this.maxThreadsPerMultiprocessor = maxThreadsPerMultiprocessor;
        this.maxThreadsPerBlock = maxThreadsPerBlock;
        this.registersPerMultiprocessor = registersPerMultiprocessor;
        this.maxRegistersPerBlock = maxRegistersPerBlock;
        this.maxRegistersPerThread = maxRegistersPerThread;
        this.sharedMemoryPerMultiprocessor = sharedMemoryPerMultiprocessor;
        this.maxSharedMemoryPerBlock = maxSharedMemoryPerBlock;
        this.registerAllocationUnitSize = registerAllocationUnitSize;
        this.registerAllocationGranularity = registerAllocationGranularity;
        this.sharedMemoryAllocationUnitSize = sharedMemoryAllocationUnitSize;
        this.warpAllocationGranularity = warpAllocationGranularity;

    }

    public int getThreadsPerWarp(){return threadsPerWarp;}
    public int getMaxWarpsPerMultiprocessor(){return maxWarpsPerMultiprocessor;}
    public int getMaxBlocksPerMultiprocessor(){return maxBlocksPerMultiprocessor;}
    public int getMaxThreadsPerMultiprocessor(){return maxThreadsPerMultiprocessor;}
    public int getMaxThreadsPerBlock(){return maxThreadsPerBlock;}
    public int getRegistersPerMultiprocessor(){return registersPerMultiprocessor;}
    public int getMaxRegistersPerBlock(){return maxRegistersPerBlock;}
    public int getMaxRegistersPerThread(){return maxRegistersPerThread;}
    public int getSharedMemoryPerMultiprocessor(){return sharedMemoryPerMultiprocessor;}
    public int getMaxSharedMemoryPerBlock(){return maxSharedMemoryPerBlock;}
    public int getRegisterAllocationUnitSize(){return registerAllocationUnitSize;}
    public String getRegisterAllocationGranularity(){return registerAllocationGranularity;}
    public int getSharedMemoryAllocationUnitSize(){return sharedMemoryAllocationUnitSize;}
    public int getWarpAllocationGranularity(){return warpAllocationGranularity;}
}
