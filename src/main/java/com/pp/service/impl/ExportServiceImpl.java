package com.pp.service.impl;

import com.alibaba.fastjson.JSON;
import com.pp.dao.CaseMapper;
import com.pp.dao.ModuleMapper;
import com.pp.dto.ExportDTO;
import com.pp.entity.CaseEntity;
import com.pp.entity.ModuleEntity;
import com.pp.entity.other.CaseStepExpect;
import com.pp.service.ExportService;
import com.pp.utils.CSVUtils;
import org.springframework.stereotype.Service;
import org.xmind.core.*;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ExportServiceImpl implements ExportService {
    @Resource
    private CaseMapper caseMapper;

    @Resource
    private ModuleMapper moduleMapper;
    @Override
    public  String getCase(ExportDTO dto) throws IOException, CoreException {
        if(dto.getType()==0){
            if(dto.getModuleId()!=null&&dto.getModuleId()>0){
                return xmindByModuleId(dto.getModuleId());
            }else if(dto.getCaseIds()!=null||dto.getCaseIds().size()>0)
                return xmindByCaseIds(dto.getCaseIds());
        }else if (dto.getType()==1){
            return saveCsv(dto);
        }
        return null;
    }
    private Map<String,List<CaseEntity>> getDataByModule(Long id ){
        List<ModuleEntity> list1 = new ArrayList<>();
        ModuleEntity rootEntity = moduleMapper.selectById(id);
        if (rootEntity != null) {
            list1.add(rootEntity);
        }
        List<ModuleEntity> allModule = moduleMapper.selectAllModule();
        selectModuleAndSubmodule(id, list1, allModule,rootEntity.getName());
        List<Long> moduleIds = list1.stream().map(ModuleEntity::getId).collect(Collectors.toList());
        List<CaseEntity> caseEntities=new ArrayList<>();
        for (Long ids:
                moduleIds) {
            List<CaseEntity> entities=caseMapper.selectByModuleId(ids);
            caseEntities.addAll(entities);
        }
        Map<String,List<CaseEntity>> data=new HashMap<>();
        for(int i=0;i<list1.size();i++){
            for (CaseEntity e:caseEntities) {
                if(list1.get(i).getId().equals(e.getModuleId())){
                    if(data.containsKey(list1.get(i).getName())){
                        List<CaseEntity> cc=data.get(list1.get(i).getName());
                        cc.add(e);
                        data.put(list1.get(i).getName(),cc);
                    }else{
                        List<CaseEntity> cc=new ArrayList<>();
                        cc.add(e);
                        data.put(list1.get(i).getName(),cc);
                    }
                }
            }
        }
        return data;
    }
    private  String xmindByModuleId(Long id) throws IOException, CoreException {//创建一个空白界面

        String url=selectParentModule(id);
        String[] sp=url.toString().split("/");
        if(sp.length<3){
            return null;
        }
        IWorkbookBuilder workbookBuilder = (IWorkbookBuilder) Core.getWorkbookBuilder();
        //创建工作溥
        IWorkbook iWorkbook = workbookBuilder.createWorkbook("data.fileName");
        ISheet primarySheet = iWorkbook.getPrimarySheet();
        //创建一个流程图的主题
        ITopic rootTopic = primarySheet.getRootTopic();
        //设置成为正确的逻辑图
        rootTopic.setStructureClass("org.xmind.ui.logic.right");
        rootTopic.setTitleText("根目录");
        ITopic lastTopic = rootTopic;
        for(int i=2;i<sp.length-1;i++){
            ITopic topic1 = iWorkbook.createTopic();
            topic1.setTitleText(sp[i]);
            lastTopic.add(topic1);
            lastTopic=topic1;
        }

        return saveXmind(getDataByModule(id),lastTopic,iWorkbook);
    }
    private String saveXmind(Map<String,List<CaseEntity>> data,ITopic rootTopic,IWorkbook iWorkbook) throws IOException, CoreException {
        //忽略表头
        for (String key:data.keySet()) {
            ITopic lastTopic = rootTopic;
            String[] sp=key.split("/");
            for(int p=0;p<sp.length;p++){
                List<ITopic> cc=lastTopic.getAllChildren();
                if(cc==null||cc.size()==0){
                    ITopic topic1 = iWorkbook.createTopic();
                    topic1.setTitleText(sp[p]);
                    lastTopic.add(topic1);
                    lastTopic = topic1;
                }else{
                    int num=0;
                    for(int r=0;r<cc.size();r++){
                        if(cc.get(r).getTitleText().equals(sp[p])){
                            lastTopic=cc.get(r);
                            num++;
                            break;
                        }
                    }
                    if(num==0){
                        ITopic topic1 = iWorkbook.createTopic();
                        topic1.setTitleText(sp[p]);
                        lastTopic.add(topic1);
                        lastTopic = topic1;
                    }

                }
            }
            ITopic ccTopic=lastTopic;
            for (int j=0;j<data.get(key).size();j++) {
                //标记是否存在相同结点
                //就当前数据创建一个新结点
                ITopic topic = iWorkbook.createTopic();
                CaseEntity n=data.get(key).get(j);
                topic.setTitleText("tc-"+n.getLevel().getName()+"："+n.getTitle());
                lastTopic.add(topic);
                lastTopic = topic;
                if(n.getRemark()!=null&&n.getRemark().trim().length()>0){
                    ITopic remark = iWorkbook.createTopic();
                    remark.setTitleText(n.getRemark().trim());
                    lastTopic.add(remark);
                }
                if(n.getPrecondition()!=null&&n.getPrecondition().trim().length()>0){
                    ITopic precondition = iWorkbook.createTopic();
                    precondition.setTitleText(n.getPrecondition().trim());
                    lastTopic.add(precondition);
                }
                if(n.getStepExpect()!=null){
                    String s1 = JSON.toJSONString(n.getStepExpect());
                    List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);
                    if(caseStepExpects!=null||caseStepExpects.size()>0){
                        for (CaseStepExpect caseStepExpect : caseStepExpects) {
                            ITopic step = iWorkbook.createTopic();
                            step.setTitleText(caseStepExpect.getStep().trim());
                            lastTopic.add(step);
                            if(caseStepExpect.getExpect()!=null&&caseStepExpect.getExpect().trim().length()>0){
                                lastTopic=step;
                                ITopic expect = iWorkbook.createTopic();
                                expect.setTitleText(caseStepExpect.getExpect().trim());
                                lastTopic.add(expect);
                                lastTopic=topic;
                            }
                        }
                    }
                }
                lastTopic=ccTopic;
            }

        }
        String file=System.getProperty("user.dir")+"/src/main/resources/case.xmind";
        File file1 = new File(file);
        //判断该路径下是否存在该文件，如果存在那么删除，如果不存在 直接新建
        if (file1.exists()) {
            file1.delete();
        }
        iWorkbook.save(file);
        return file;
    }
    private Map<String,List<CaseEntity>> getDataByCase(List<Long> ids){
        Map<String,List<CaseEntity>> data=new HashMap<>();
        for (Long id:ids) {
            List<CaseEntity> caseEntities=caseMapper.selectByCaseId(id);
            if(caseEntities==null||caseEntities.size()==0){
                continue;
            }
            String url=selectAllUrl(caseEntities.get(0).getModuleId());
            if(data.containsKey(url)){
                List<CaseEntity> c=data.get(url);
                c.add(caseEntities.get(0));
                data.put(url,c);
            }else{
                data.put(url,caseEntities);
            }
        }
        return data;
    }
    private  String xmindByCaseIds(List<Long> ids) throws IOException, CoreException {//创建一个空白界面

        IWorkbookBuilder workbookBuilder = (IWorkbookBuilder) Core.getWorkbookBuilder();
        //创建工作溥
        IWorkbook iWorkbook = workbookBuilder.createWorkbook("data.fileName");
        ISheet primarySheet = iWorkbook.getPrimarySheet();
        //创建一个流程图的主题
        ITopic rootTopic = primarySheet.getRootTopic();
        //设置成为正确的逻辑图
        rootTopic.setStructureClass("org.xmind.ui.logic.right");
        rootTopic.setTitleText("根目录");
        return saveXmind(getDataByCase(ids),rootTopic,iWorkbook);
    }



    /**
     * 查找该case的所有路径
     * @param id
     * @return
     */
    private String selectAllUrl(Long id){
        boolean flag=true;
        Long currentId=id;
        StringBuilder sb=new StringBuilder();
        while (flag){
            List<ModuleEntity> ls=moduleMapper.selectOneParentId(currentId);
            if(ls==null||ls.size()==0){//没有父目录
                flag=false;
            }else{
                sb.insert(0,"/"+ls.get(0).getName());
                currentId=ls.get(0).getParentId();
            }
        }
        return sb.toString().replace("/默认模块/","");
    }

    /**
     * 查找模块的父节点
     * @param topic
     * @param workbook
     * @param id
     * @return
     */
    private String selectParentModule(Long id){
        boolean flag=true;
        Long currentId=id;
        StringBuilder sb=new StringBuilder();
        while (flag){
            List<ModuleEntity> ls=moduleMapper.selectOneParentId(currentId);
            if(ls==null||ls.size()==0){//没有父目录
                flag=false;
            }else{
                sb.insert(0,"/"+ls.get(0).getName());
                currentId=ls.get(0).getParentId();
            }
        }

        return sb.toString();
    }

    /**
     * 查找所有子模块
     *
     * @param rootModuleId
     * @param list
     * @param allModule
     */
    public void selectModuleAndSubmodule(Long rootModuleId, List<ModuleEntity> list, List<ModuleEntity> allModule,String name) {
        for (ModuleEntity moduleEntity : allModule) {
            if (moduleEntity.getParentId().equals(rootModuleId)) {
                moduleEntity.setName(name+"/"+moduleEntity.getName());
                list.add(moduleEntity);
                selectModuleAndSubmodule(moduleEntity.getId(), list, allModule,moduleEntity.getName());
            } else {
                continue;
            }
        }
    }




    private String saveCsv(ExportDTO dto) throws IOException {
        Map<String,List<CaseEntity>> data=new HashMap<>();
        String url = "";
        if(dto.getModuleId()!=null&&dto.getModuleId()>0){
            data=getDataByModule(dto.getModuleId());
            url=selectParentModule(dto.getModuleId());
            String[] sp=url.toString().split("/");
            if(sp.length<3){
                return null;
            }
            url=url.replace("/默认模块/","");
        }else if(dto.getCaseIds()!=null||dto.getCaseIds().size()>0){
            data=getDataByCase(dto.getCaseIds());
        }
        if(data==null||data.isEmpty()){
            return null;
        }
        String file=System.getProperty("user.dir")+"/src/main/resources/case.csv";
        File csvFile = new File(file);
        BufferedWriter csvWtriter = null;
        File parent = csvFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        csvFile.createNewFile();
        csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8), 1024);
        String[] title = {"用例编号","所属产品","所属模块","相关需求","用例标题","前置条件","步骤","预期","实际情况","关键词","优先级","用例类型","适用阶段","用例状态","B","R","S","结果",
                "由谁创建","创建日期","最后修改者","修改日期","用例版本","相关用例"};
        //使用工具类
        CSVUtils.writeRow(title,csvWtriter);
        int num=1;
        for (Map.Entry < String, List<CaseEntity> > entry: data.entrySet()) {
            for (CaseEntity e:entry.getValue()) {
                StringBuilder step=new StringBuilder();
                StringBuilder excep=new StringBuilder();
                if(e.getStepExpect()!=null){
                    String s1 = JSON.toJSONString(e.getStepExpect());
                    List<CaseStepExpect> caseStepExpects = JSON.parseArray(s1, CaseStepExpect.class);

                    if(caseStepExpects!=null||caseStepExpects.size()>0){
                        for (CaseStepExpect caseStepExpect : caseStepExpects) {
                                step.append(caseStepExpect.getStep().trim()+"\n");
                                excep.append(caseStepExpect.getExpect().trim()+"\n");
                        }
                    }
                }
                String[] title1={String.valueOf(num),"",dto.getModuleId()!=null&&dto.getModuleId()>0?url+entry.getKey():entry.getKey(),"",e.getTitle(),e.getPrecondition(),step.toString(),excep.toString(),"","",e.getLevel().getName().replace("p","P"),"","","","","","","",
                        "","","","","",""};
                num++;
                CSVUtils.writeRow(title1,csvWtriter);
            }
        }
        csvWtriter.flush();
        csvWtriter.close();
//        return fileUrlToBytes(file);
        return  file;
    }
}
