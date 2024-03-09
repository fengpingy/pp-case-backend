package com.pp.service;

import com.pp.dto.CaseDTO;
import com.pp.dto.DeleteCase;
import com.pp.dto.XmindCaseDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.CasePageQuery;
import com.pp.dto.response.page.CasePage;
import com.pp.dto.response.page.PageResponse;
import com.pp.entity.ModuleEntity;

import java.util.List;
import java.util.Map;

public interface CaseService {

    /**
     * 新增用例
     * @param caseDTO
     * @return
     */
    Long addCase(CaseDTO caseDTO);

    /**
     *批量新增用例
     * @param caseDTOS
     * @return
     */
    int batchAddCase(List<CaseDTO> caseDTOS);


    /**
     * 获取用例详情
     * @param id
     * @return
     */
    CaseDTO getCase(Long id);

    /**
     * 分页
     * @param pageQueryPageDTO
     */
    PageResponse<CasePage> pageList(PageDTO<CasePageQuery> pageQueryPageDTO);
    /***
     * 根据ModuleId查询case
     */
    List<CasePage> getCaseByModuleId(Long id);

    /**
     * 根据caseIdList 查询数据
     * @param caseIdList
     * @return
     */
    List<CaseDTO> getCaseByCaseIds(List<Long> caseIdList);

    /**
     * 根据ModuleIds 查询case
     * @param moduleEntities
     * @return
     */
    List<CaseDTO> getCaseByModuleIds(List<ModuleEntity> moduleEntities);

    /**
     * 修改case
     * @param caseDTO
     * @return
     */
    int editCase(CaseDTO caseDTO);

    /**
     * 删除case
     * @param id
     * @return
     */
    int deleteCase(Long id);


    /**
     * 批量删除case
     * @param caseIdS
     * @return
     */
    int batchDeleteCase(DeleteCase caseIdS);

    /**
     * 检查当前模块及子模块下是否有case,有返回true,没有返回false
     * @param moduleId
     * @return
     */
    Boolean checkCaseByModuleId(Long moduleId);


    /**
     * 批量更新case
     * @param caseDTOS
     * @return
     */
    int batchUpdateCases(List<CaseDTO> caseDTOS);

    /**
     * 删除模块下的cases
     * @param moduleId
     */
    int deleteCaseByModule(long moduleId);

    /**
     * 组装cases树
     */
    Map<Long, List<XmindCaseDTO>> spliceCaseTree(List<CaseDTO> caseDTOS);
}
