package com.pp.xmind;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.pp.common.constants.SystemConst;
import com.pp.common.enums.business.CaseLevel;
import com.pp.entity.CaseEntity;
import com.pp.entity.other.CaseStepExpect;
import com.pp.xmind.xmind2021.Attached;
import com.pp.xmind.xmind2021.AttachedNode;
import com.pp.xmind.xmind2021.RootTopic;

import java.util.*;

import static com.pp.xmind.XmindConst.XMIND2021_ROOT;


public class Xmind2021Parse extends AbstractXmindParse {
    public Xmind2021Parse(String contentFileDir) {
        super(contentFileDir);
    }


    @Override
    void parseSecondFloor() {
        JSONArray objects = JSON.parseArray(content);
        Object rootTopic = objects.getJSONObject(0).get(XMIND2021_ROOT);
        this.secondFloorContent = JSON.toJSONString(rootTopic);
    }


    @Override
    public Map<String, List<CaseEntity>> analysisCase() {
        RootTopic rootTopic = JSONObject.parseObject(secondFloorContent, RootTopic.class);
        Attached children = rootTopic.getChildren();
        recursion(children, caseModuleMap, SystemConst.DEFAULT_MODULE_NAME);
        return caseModuleMap;
    }


    public void recursion(Attached children, Map<String, List<CaseEntity>> map, String parentName) {
        if (children == null) return;
        if (children.getAttached() == null) return;
        List<AttachedNode> attached1 = children.getAttached();
        for (AttachedNode attachedNode : attached1) {
            String currentTitle = attachedNode.getTitle().replaceAll("\n","").trim();
            //如果是一个用例
            if (currentTitle.matches(XmindConst.TITLE_PREFIX)) {
                try {
                    CaseEntity caseEntity = CaseEntity.builder().build();
                    CaseLevel caseLevel = CaseLevel.nameOf(currentTitle.split(XmindConst.SPLIT)[0].split(XmindConst.TITLE_SEPARATOR)[1]);
                    String title = currentTitle.split(XmindConst.SPLIT,2)[1];
                    caseEntity.setLevel(caseLevel);
                    caseEntity.setTitle(title);
                    caseEntity.setType(XmindConst.DEFAULT_CASE_TYPE);
                    List<CaseStepExpect> stepExpectList = new ArrayList<>();
                    if(attachedNode.getChildren()!=null&&attachedNode.getChildren().getAttached()!=null){//用例名称下有子节点
                        List<AttachedNode> attached = attachedNode.getChildren().getAttached();
                        if (attached != null && attached.size() > 0) {
                            for (AttachedNode node : attached) {
                                //如果是前置条件
                                if (node.getTitle().trim().matches(XmindConst.PRECONDITION_PREFIX)) {
                                    caseEntity.setPrecondition(node.getTitle().trim());
                                    //如果是备注
                                } else if (node.getTitle().trim().matches(XmindConst.REMARK_PREFIX)) {
                                    caseEntity.setRemark(node.getTitle().trim());
                                    //否则就是用例步骤
                                } else {
                                    CaseStepExpect caseStepExpect = new CaseStepExpect();
                                    caseStepExpect.setStep(node.getTitle());
                                    if(node.getChildren()!=null){//有步骤有预期结果
                                        List<AttachedNode> steps = node.getChildren().getAttached();
                                        StringBuilder stepString = null;
                                        if (steps != null && steps.size() > 0) {
                                            stepString = new StringBuilder();
                                            for (AttachedNode step : steps) {
                                                stepString.append(step.getTitle());
                                                stepString.append(SystemConst.LINE_BREAK);
                                            }
                                            caseStepExpect.setExpect(stepString.toString());
                                        }else{
                                            caseStepExpect.setExpect("");
                                        }
                                    }else{//只有步骤没有预期结果
                                        caseStepExpect.setExpect("");
                                    }

                                    stepExpectList.add(caseStepExpect);
                                }
                            }
                        }
                        if(stepExpectList.size()==0){//如果用例只有前置条件或者备注，为了兼容测试计划的详情展示，需赋值步骤和结果为空
                            CaseStepExpect caseStepExpect = new CaseStepExpect();
                            caseStepExpect.setStep("");
                            caseStepExpect.setExpect("");
                            stepExpectList.add(caseStepExpect);
                        }
                        caseEntity.setStepExpect(stepExpectList);
//                    map.get(initModuleName.toString()).add(caseEntity);
                        map.get(parentName).add(caseEntity);
                    }else{//用例名称下没有子节点,默认赋值一个步骤和结果

                        CaseStepExpect caseStepExpect = new CaseStepExpect();
                        caseStepExpect.setStep("");
                        caseStepExpect.setExpect("");
                        stepExpectList.add(caseStepExpect);
                        caseEntity.setStepExpect(stepExpectList);
                        caseEntity.setPrecondition("");
                        map.get(parentName).add(caseEntity);
                    }


                } catch (Exception e) {
                    continue;
                }
            }else if (currentTitle.matches(XmindConst.PRECONDITION_PREFIX)) {//如果是前置条件
                try {
                    if(attachedNode.getChildren()!=null&&attachedNode.getChildren().getAttached()!=null) {
                        List<AttachedNode> attached = attachedNode.getChildren().getAttached();
                        if (attached != null && attached.size() > 0) {
                            for (AttachedNode node : attached) {
                                //如果是case
                                if (node.getTitle().trim().matches(XmindConst.TITLE_PREFIX)) {
                                    CaseEntity caseEntity = CaseEntity.builder().build();
                                    CaseLevel caseLevel = CaseLevel.nameOf(node.getTitle().trim().split(XmindConst.SPLIT)[0].split(XmindConst.TITLE_SEPARATOR)[1]);
                                    caseEntity.setLevel(caseLevel);
                                    String title = node.getTitle().trim().split(XmindConst.SPLIT,2)[1];
                                    caseEntity.setTitle(title);
                                    caseEntity.setType(XmindConst.DEFAULT_CASE_TYPE);
                                    caseEntity.setPrecondition(currentTitle);
                                    List<CaseStepExpect> stepExpectList = new ArrayList<>();
                                    if (node.getChildren() != null && node.getChildren().getAttached() != null) {
                                        List<AttachedNode> attachedChild = node.getChildren().getAttached();
                                        if (attachedChild != null && attached.size() > 0) {

                                            for (AttachedNode nodeChild : attachedChild) {
                                                if (nodeChild.getTitle().trim().matches(XmindConst.REMARK_PREFIX)) {
                                                    caseEntity.setRemark(nodeChild.getTitle());
                                                    //否则就是用例步骤
                                                } else {
                                                    CaseStepExpect caseStepExpect = new CaseStepExpect();
                                                    caseStepExpect.setStep(nodeChild.getTitle());
                                                    if (nodeChild.getChildren() != null) {//有步骤有预期结果
                                                        List<AttachedNode> steps = nodeChild.getChildren().getAttached();
                                                        StringBuilder stepString = null;
                                                        if (steps != null && steps.size() > 0) {
                                                            stepString = new StringBuilder();
                                                            for (AttachedNode step : steps) {
                                                                stepString.append(step.getTitle());
                                                                stepString.append(SystemConst.LINE_BREAK);
                                                            }
                                                            caseStepExpect.setExpect(stepString.toString());
                                                        } else {
                                                            caseStepExpect.setExpect("");
                                                        }
                                                    } else {//只有步骤没有预期结果
                                                        caseStepExpect.setExpect("");
                                                    }
                                                    stepExpectList.add(caseStepExpect);
                                                }
                                            }
                                        }
                                        if (stepExpectList.size() == 0) {//如果用例只有前置条件或者备注，为了兼容测试计划的详情展示，需赋值步骤和结果为空
                                            CaseStepExpect caseStepExpect = new CaseStepExpect();
                                            caseStepExpect.setStep("");
                                            caseStepExpect.setExpect("");
                                            stepExpectList.add(caseStepExpect);
                                        }
                                        caseEntity.setStepExpect(stepExpectList);
                                        map.get(parentName).add(caseEntity);
                                    }else{
                                        CaseStepExpect caseStepExpect = new CaseStepExpect();
                                        caseStepExpect.setStep("");
                                        caseStepExpect.setExpect("");
                                        stepExpectList.add(caseStepExpect);
                                        caseEntity.setStepExpect(stepExpectList);
                                        map.get(parentName).add(caseEntity);
                                    }
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            else {
                //不是用例
                // TODO: 2022/6/14 这有个bug
                map.put(parentName + XmindConst.MODULE_SEPARATOR + currentTitle, new ArrayList<>());
                recursion(attachedNode.getChildren(), map, parentName + XmindConst.MODULE_SEPARATOR + currentTitle);
            }
        }
    }
}
