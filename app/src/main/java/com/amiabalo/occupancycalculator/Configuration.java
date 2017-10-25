package com.amiabalo.occupancycalculator;

import java.io.Serializable;

/**
 * Created by jacquelineabalo on 8/18/17.
 */

public class Configuration implements Serializable{
    private double computeCapability;
    private int sharedMemorySize;
    private String cachingMode;
    private int threadsPerBlock;
    private int registersPerThread;
    private int sharedMemoryPerBlock;

    public Configuration(double computeCapability,
                         int sharedMemorySize,
                         String cachingMode,
                         int threadsPerBlock,
                         int registersPerThread,
                         int sharedMemoryPerBlock)
    {
        this.computeCapability = computeCapability;
        this.sharedMemorySize = sharedMemorySize;
        this.cachingMode = cachingMode;
        this.threadsPerBlock = threadsPerBlock;
        this.registersPerThread = registersPerThread;
        this.sharedMemoryPerBlock = sharedMemoryPerBlock;
    }

    public double getComputeCapability(){return computeCapability;}
    public int getSharedMemorySize(){return sharedMemorySize;}
    public String getCachingMode(){return cachingMode;}
    public int getThreadsPerBlock(){return threadsPerBlock;}
    public int getRegistersPerThread(){return registersPerThread;}
    public int getSharedMemoryPerBlock(){return sharedMemoryPerBlock;}
}
