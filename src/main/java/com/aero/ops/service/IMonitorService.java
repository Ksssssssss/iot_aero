package com.aero.ops.service;

import java.util.List;

public interface IMonitorService {
    List<String> getAllIndices();
    String getSystemMemory(String indices);
    String getSystemCPU(String indices);
    String getSystemTCP(String indices);
}
