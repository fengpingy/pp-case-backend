package com.pp.domain.service.api;

import com.pp.dto.XmindCaseDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface XmindService {
    /**
     * 文件解析
     * @param file
     * @param unitId
     * @return
     */
    Boolean parseCaseAndModule(MultipartFile file, Long unitId, boolean cover);

    /**
     * 根据模块ID查询所属的xmindCase
     * @param id
     * @return
     */
    XmindCaseDTO getXmindCase(Long id);

    /**
     * 编辑xmindCase
     * @param xmindCaseDTO
     * @return
     */
    Boolean editXmindCase(XmindCaseDTO xmindCaseDTO);


    /**
     * 获取xmind模板
     * @param name
     * @param response
     * @return
     * @throws IOException
     */
    Boolean getXmindTemplate(String name, HttpServletResponse response) throws IOException;
}
