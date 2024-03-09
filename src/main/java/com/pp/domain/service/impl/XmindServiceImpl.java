package com.pp.domain.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pp.common.UserHolder;
import com.pp.common.enums.business.XmindNodeType;
import com.pp.dao.CaseMapper;
import com.pp.dao.ModuleMapper;
import com.pp.domain.service.api.XmindService;
import com.pp.dto.*;
import com.pp.dto.response.ModuleTreeDTO;
import com.pp.entity.CaseEntity;
import com.pp.entity.ModuleEntity;
import com.pp.entity.UserEntity;
import com.pp.entity.other.CaseStepExpect;
import com.pp.expection.PpExpection;
import com.pp.service.CaseImageService;
import com.pp.service.CaseService;
import com.pp.service.ModuleService;
import com.pp.service.UnitService;
import com.pp.utils.BeanUtils;
import com.pp.xmind.XmindParse;
import com.pp.xmind.XmindPathHelper;
import com.pp.xmind.XmindConst;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.pp.common.constants.CommonError.FILE_IS_EMPTY;
import static com.pp.common.constants.CommonError.FILE_TYPE_ERROR;
import static com.pp.common.constants.SystemConst.XMIND_FILE_SUFFIX;


@Service
@Slf4j
public class XmindServiceImpl implements XmindService {

    @Resource
    private CaseMapper caseMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private UnitService unitService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private CaseService caseService;
    @Resource
    private CaseImageService caseImageService;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean parseCaseAndModule(MultipartFile file, Long unitId, boolean cover) {
        // 文件为空
        if (file.isEmpty()) {
            throw PpExpection.newsPpExpection(FILE_TYPE_ERROR);
        }
        String filename = file.getOriginalFilename();
        assert filename != null;
        //文件类型错误
        if (!filename.endsWith(XMIND_FILE_SUFFIX)) {
            throw new PpExpection(FILE_IS_EMPTY);
        }

        XmindPathHelper xmindPathHelper = new XmindPathHelper(file);
        XmindParse xmindParse = XmindParse.with(xmindPathHelper.saveAndSolutionFile());
        //用例和模块
        Map<String, List<CaseEntity>> moduleAndCase = xmindParse.analysisCase();
        //生成用例
        List<CaseEntity> caseList = new ArrayList<>();
        UserEntity userEntity = UserHolder.get();
        moduleAndCase.forEach((k, v) -> {
            Long lastModuleId = parseModulePathByName(k, unitId);
            Boolean aBoolean = caseService.checkCaseByModuleId(lastModuleId);
            ;
            if (cover && aBoolean) {
                log.info("该模块下已存在case，需要覆盖已存在cases");
                caseMapper.deleteCaseByModuleId(lastModuleId);
                caseImageService.deleteImageCaseByModuleId(lastModuleId);
            }

            //所有case
            List<CaseEntity> caseEntities = v.stream().peek(caseEntity -> {
                Date date = new Date();
                caseEntity.setId(IdWorker.getId());
                caseEntity.setCreateTime(date);
                caseEntity.setUpdateTime(date);
                caseEntity.setModuleId(lastModuleId);
                if (userEntity != null) {
                    caseEntity.setTesterId(userEntity.getId());
                }
            }).distinct().collect(Collectors.toList());
            caseList.addAll(caseEntities);
        });
        //插入用例
        caseMapper.batchInsert(caseList);
        return true;
    }

    @Override
    public XmindCaseDTO getXmindCase(Long id) {
        List<ModuleEntity> moduleEntityList = moduleService.selectModuleAndSubmodule(id, true);
        List<ModuleDTO> moduleDTOS = BeanUtils.listObjectCopyProperty(moduleEntityList, ModuleDTO.class);
        ModuleDTO module = moduleService.getModule(id);
        XmindCaseDTO xmindCaseDTO = new XmindCaseDTO();
        xmindCaseDTO.setId(id);
        xmindCaseDTO.setTitle(module.getName());
        xmindCaseDTO.setType(XmindNodeType.MODULE);
        xmindCaseDTO.setParentId(module.getParentId());
        assert moduleDTOS != null;
        // 组装模块树
        selectModuleAndSubmodule(xmindCaseDTO, moduleDTOS);
        List<CaseDTO> caseByModuleIds = caseService.getCaseByModuleIds(moduleEntityList);
        log.info("用例集："+caseByModuleIds.toString());
        // 组装case树
        selectCaseAndSubmodule(xmindCaseDTO, caseByModuleIds);
        return xmindCaseDTO;
    }

    @Override
    public Boolean editXmindCase(XmindCaseDTO xmindCaseDTO) {
        Long moduleId = xmindCaseDTO.getId();
        // 获取旧的模块信息
        List<ModuleEntity> oldModuleEntities = moduleService.selectModuleAndSubmodule(moduleId, true);
        List<Long> oldModuleIds = oldModuleEntities.stream().map(ModuleEntity::getId).collect(Collectors.toList());
        // 获取旧的用例信息
        List<CaseDTO> oldCases = caseService.getCaseByModuleIds(oldModuleEntities);
        // 收集新的module 变更信息，收集新的 case 变更信息
        List<ModuleDTO> moduleDTOS = new ArrayList<>();
        List<CaseDTO> caseDTOS = new ArrayList<>();
        analysisModuleTree(xmindCaseDTO, moduleDTOS);     // 收集新的module 变更信息
        analysisCaseTree(xmindCaseDTO, caseDTOS);        // 收集新的 case 变更信息
        List<Long> newModuleIds = moduleDTOS.stream().map(ModuleDTO::getId).collect(Collectors.toList());
        List<Long> newCaseIds = caseDTOS.stream().map(CaseDTO::getId).collect(Collectors.toList());
        // 用例相关变更
        if (oldCases == null) {                  // 老的用例为空，则全部做新增
            if (!caseDTOS.isEmpty()) {
                caseService.batchAddCase(caseDTOS);
            }
        } else if (caseDTOS.isEmpty()) {         // 新的用例为空，则全部做删除
            for (CaseDTO oldCase : oldCases) {
                caseService.deleteCase(oldCase.getId());
            }
        } else {                                 // 部分新增，部分删除
            List<Long> oldCaseIds = oldCases.stream().map(CaseDTO::getId).collect(Collectors.toList());
            // 需要删除的case
            List<Long> removeCaseIds = oldCaseIds.stream().filter(oldCaseId -> !newCaseIds.contains(oldCaseId)).collect(Collectors.toList());
            // 需要新增的case
            List<CaseDTO> addCases = caseDTOS.stream().filter(caseDTO -> caseDTO.getId() == null).collect(Collectors.toList());
            // 需要更新的case
            caseDTOS.removeAll(addCases);
            // 删除case
            for (Long removeCaseId : removeCaseIds) {
                caseService.deleteCase(removeCaseId);
            }
            // 新增case
            caseService.batchAddCase(addCases);
            // 更新case
            caseService.batchUpdateCases(caseDTOS);
        }
        // 模块相关变更，由于xmind结构至少有一个根节点，这里不做全部为空处理
        // 需要删除的模块
        List<Long> removeModuleIds = oldModuleIds.stream().filter(oldModuleId -> !newModuleIds.contains(oldModuleId)).collect(Collectors.toList());
        // 需要新增的模块
        List<ModuleDTO> addModules = moduleDTOS.stream().filter(newModule -> newModule.getId() == null).collect(Collectors.toList());
        // 需要更新的模块
        moduleDTOS.removeAll(addModules);
        // 删除模块
        if (!removeModuleIds.isEmpty()) {
            for (Long removeModuleId : removeModuleIds) {
                moduleService.deleteModule(removeModuleId);
            }
        }
        // 新增模块
        if (!addModules.isEmpty()) {
            for (ModuleDTO moduleDTO : addModules) {
                moduleService.addModule(moduleDTO);
            }
        }
        // 更新模块
        moduleService.batchUpdateModules(moduleDTOS);
        return true;
    }

    @Override
    public Boolean getXmindTemplate(String name, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = null;
        // 读取静态资源文件
        ClassPathResource classPathResource = new ClassPathResource(name + XMIND_FILE_SUFFIX);
        InputStream inputStream = classPathResource.getInputStream();
        try {
            outputStream = response.getOutputStream();
            // 设置下载文件名称
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(name, "utf-8") + XMIND_FILE_SUFFIX);
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }


    /**
     * 根据路径名称返回最后一层路径的id 并新建module
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long parseModulePathByName(String modulePath, Long unitId) {
        String[] moduleNames = splitModulePath(modulePath);
        //查询模块树
        ModuleTreeDTO moduleTreeDTO = unitService.selectModuleTree(unitId);
        //默认模块
        List<ModuleTreeDTO> children = moduleTreeDTO.getChildren();
        int startIndex = 0;
        //开始不同节点的parentId
        long startModuleId = moduleTreeDTO.getId();
        for (int i = 0; i < moduleNames.length; i++) {
            boolean exists = false;
            for (ModuleTreeDTO child : children) {
                if (child.getName().equals(moduleNames[i])) {
                    startModuleId = child.getId();
                    children = child.getChildren();
                    exists = true;
                    break;
                }
            }
            if (exists) {
                continue;
            } else {
                startIndex = i;
                break;
            }
        }
        if (startIndex != 0) {
            for (int i = startIndex; i < moduleNames.length; i++) {
                ModuleEntity moduleEntity = new ModuleEntity();
                moduleEntity.setParentId(startModuleId);
                moduleEntity.setName(moduleNames[i]);
                moduleEntity.setCode(moduleNames[i]);
                //暂时循环插入 todo
                moduleMapper.insert(moduleEntity);
                startModuleId = moduleEntity.getId();
            }
        }
        return startModuleId;
    }


    /**
     * 解析modulePath
     *
     * @param path
     * @return
     */
    public String[] splitModulePath(String path) {
        return path.split(XmindConst.MODULE_SEPARATOR);
    }


    /**
     * 组装module树
     *
     * @param xmindCaseDTO
     * @param allModule
     */
    public void selectModuleAndSubmodule(XmindCaseDTO xmindCaseDTO, List<ModuleDTO> allModule) {
        for (ModuleDTO dto : allModule) {
            if (dto.getParentId().equals(xmindCaseDTO.getId())) {
                XmindCaseDTO sonXmindCaseDTO = new XmindCaseDTO();
                BeanUtils.copyProperty(dto, sonXmindCaseDTO);
                sonXmindCaseDTO.setTitle(dto.getName());
                sonXmindCaseDTO.setType(XmindNodeType.MODULE);
                xmindCaseDTO.getChildren().add(sonXmindCaseDTO);
                selectModuleAndSubmodule(sonXmindCaseDTO, allModule);
            } else {
                continue;
            }
        }
    }

    /**
     * 组装xmind用例
     *
     * @param xmindCaseDTO
     * @param caseDTOS
     */
//    public void selectCaseAndSubmodule(XmindCaseDTO xmindCaseDTO, List<CaseDTO> caseDTOS) {
//        // 获取当前节点的ID
//        Long id = xmindCaseDTO.getId();
//        // 获取当前节点的子节点
//        List<XmindCaseDTO> children = xmindCaseDTO.getChildren();
//        // 获取子节点ID
//        List<Long> childrenIds = children.stream().map(XmindCaseDTO::getId).collect(Collectors.toList());
//        String[] nullString = {""};
//
//        // 判断case的模块ID是否等于当前节点ID
//        for (CaseDTO caseDTO : caseDTOS) {
//            if (Objects.equals(caseDTO.getModuleId(), id) && !childrenIds.contains(caseDTO.getId())) {  // 必须判断是否已经加过同样的节点了，不然一堆重复节点
//
//                // 用例
//                XmindCaseDTO sonXmindCaseDTO = new XmindCaseDTO();
//                sonXmindCaseDTO.setTitle(caseDTO.getTitle());
//                sonXmindCaseDTO.setType(XmindNodeType.CASE);
//                sonXmindCaseDTO.setId(caseDTO.getId());
//                sonXmindCaseDTO.setParentId(caseDTO.getModuleId());
//                children.add(sonXmindCaseDTO);
//
//                // 前置条件
//                String precondition = caseDTO.getPrecondition();
//
//                XmindCaseDTO xmindPrecondition = new XmindCaseDTO();
//                xmindPrecondition.setId(IdWorker.getId());
//                xmindPrecondition.setTitle(precondition);
//                xmindPrecondition.setType(XmindNodeType.CASE_PRECONDITION);
//                xmindPrecondition.setParentId(caseDTO.getId());
//                sonXmindCaseDTO.getChildren().add(xmindPrecondition);
//
//                // 备注
//                String remark = caseDTO.getRemark();
//
//                XmindCaseDTO xmindRemark = new XmindCaseDTO();
//                xmindRemark.setId(IdWorker.getId());
//                xmindRemark.setTitle(remark);
//                xmindRemark.setType(XmindNodeType.CASE_REMARK);
//                xmindRemark.setParentId(caseDTO.getId());
//                sonXmindCaseDTO.getChildren().add(xmindRemark);
//
//
//                List<CaseStepExpect> stepExpect = caseDTO.getStepExpect();
//                String s1 = JSON.toJSONString(stepExpect);
//                List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);
//                for (CaseStepExpect caseStepExpect : caseStepExpects) {
//                    XmindCaseDTO xmindStep = new XmindCaseDTO();
//                    // 步骤
//                    String step = caseStepExpect.getStep();
//                    xmindStep.setId(IdWorker.getId());
//                    xmindStep.setType(XmindNodeType.CASE_STEP);
//                    xmindStep.setTitle(step);
//                    xmindStep.setParentId(caseDTO.getId());
//                    sonXmindCaseDTO.getChildren().add(xmindStep);
//
//                    XmindCaseDTO xmindExpect = new XmindCaseDTO();
//
//                    // 预期结果
//
//                    String expect = caseStepExpect.getExpect();
//                    String[] split = expect != null ? expect.split("\\r\\n") : nullString;
//                    for (String s : split) {
//                        xmindExpect.setId(IdWorker.getId());
//                        xmindExpect.setTitle(s);
//                        xmindExpect.setType(XmindNodeType.CASE_EXPECT);
//                        xmindExpect.setParentId(xmindStep.getId());
//                        xmindStep.getChildren().add(xmindExpect);
//                    }
//                }
//            }
//            // 递归组装xmindCase
//            for (XmindCaseDTO child : children) {
//                if (child.getType().equals(XmindNodeType.MODULE)) {
//                    selectCaseAndSubmodule(child, caseDTOS);
//                }
//            }
//        }
//    }
    public void selectCaseAndSubmodule(XmindCaseDTO xmindCaseDTO, List<CaseDTO> caseDTOS) {

        Stack<XmindCaseDTO> stack = new Stack<>();
        stack.push(xmindCaseDTO);
        Map<Long, List<XmindCaseDTO>> XmindCaseData = caseService.spliceCaseTree(caseDTOS);
        Set<Long> allModule = XmindCaseData.keySet();

        while (!stack.isEmpty()) {
            XmindCaseDTO current = stack.pop();
            Long id = current.getId();
            List<XmindCaseDTO> children = current.getChildren();
            if (allModule.contains(id)) {
                children.addAll(XmindCaseData.get(id));
            }

//            添加子节点到栈中
            for (XmindCaseDTO child : children) {
                if (child.getType().equals(XmindNodeType.MODULE)) {
                    stack.push(child);
                }
            }
        }
    }

    /**
     * 收集对模块树的修改，新增、编辑、删除 todo:变更模块父节点暂无实现
     *
     * @param xmindCaseDTO
     * @param moduleDTOS
     */
    private void analysisModuleTree(XmindCaseDTO xmindCaseDTO, List<ModuleDTO> moduleDTOS) {
        Long parentId = xmindCaseDTO.getParentId();
        List<XmindCaseDTO> children = xmindCaseDTO.getChildren();
        if (xmindCaseDTO.getType() == XmindNodeType.MODULE) {
            ModuleDTO moduleDTO = new ModuleDTO();
            moduleDTO.setId(xmindCaseDTO.getId());
            moduleDTO.setName(xmindCaseDTO.getTitle());
            moduleDTO.setParentId(parentId);
            moduleDTOS.add(moduleDTO);
        }
        for (XmindCaseDTO child : children) {
            if (child.getType().equals(XmindNodeType.MODULE)) {
                analysisModuleTree(child, moduleDTOS);
            }
        }
    }

    /**
     * 收集对用例的修改，新增、编辑、删除 todo: 变更case的所属模块暂无实现
     *
     * @param xmindCaseDTO
     * @param caseDTOS
     */
    private void analysisCaseTree(XmindCaseDTO xmindCaseDTO, List<CaseDTO> caseDTOS) {
        List<XmindCaseDTO> children = xmindCaseDTO.getChildren();
        if (xmindCaseDTO.getType() == XmindNodeType.CASE) {
            CaseDTO caseDTO = new CaseDTO();
            caseDTO.setId(xmindCaseDTO.getId()); // 用例ID
            caseDTO.setTitle(xmindCaseDTO.getTitle()); // 用例标题
            List<XmindCaseDTO> caseChildren = xmindCaseDTO.getChildren();
            List<CaseStepExpect> caseStepExpects = new ArrayList<>(); // 提前定义步骤和预期结果
            for (XmindCaseDTO caseChild : caseChildren) {
                if (caseChild.getType().equals(XmindNodeType.CASE_PRECONDITION)) {
                    caseDTO.setPrecondition(caseChild.getTitle()); // 前置条件
                } else if (caseChild.getType().equals(XmindNodeType.CASE_REMARK)) {
                    caseDTO.setRemark(caseChild.getTitle()); // 备注
                } else if (caseChild.getType().equals(XmindNodeType.CASE_STEP)) {
                    CaseStepExpect caseStepExpect = new CaseStepExpect();
                    caseStepExpect.setStep(caseChild.getTitle()); // 步骤
                    List<XmindCaseDTO> expectList = caseChild.getChildren();
                    for (XmindCaseDTO dto : expectList) {
                        caseStepExpect.setExpect(dto.getTitle()); // 预期结果
                    }
                    caseStepExpects.add(caseStepExpect);
                    caseDTO.setStepExpect(caseStepExpects);
                }
            }
            caseDTOS.add(caseDTO);
        }
        for (XmindCaseDTO child : children) {
            if (child.getType().equals(XmindNodeType.MODULE) || child.getType().equals(XmindNodeType.CASE)) {
                analysisCaseTree(child, caseDTOS);
            }
        }
    }

}
