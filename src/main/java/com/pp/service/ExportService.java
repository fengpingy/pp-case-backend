package com.pp.service;

import com.pp.dto.ExportDTO;
import org.xmind.core.CoreException;

import java.io.IOException;

public interface ExportService {
    /**
     * 导出数据
     * @return
     */
    String getCase(ExportDTO dto) throws IOException, CoreException;

}
