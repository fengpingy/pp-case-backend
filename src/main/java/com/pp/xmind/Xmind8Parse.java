package com.pp.xmind;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.pp.common.constants.SystemConst;
import com.pp.common.enums.business.CaseLevel;
import com.pp.entity.CaseEntity;
import com.pp.entity.other.CaseStepExpect;
import com.pp.expection.PpExpection;
import com.pp.utils.XMLUtils;
import com.pp.xmind.xmind8.Children;
import com.pp.xmind.xmind8.Topic;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pp.common.constants.CommonError.TEMPLATE_IS_ERROR;
import static com.pp.xmind.XmindConst.*;

@Slf4j
public class Xmind8Parse extends AbstractXmindParse {


    public Xmind8Parse(String contentFileDir) {
        super(contentFileDir);
    }


    @Override
    void parseSecondFloor() {
        org.json.JSONObject jsonObject = XMLUtils.xmlToJson(content);
        log.info("xml转json数据："+jsonObject);
        try {
            this.secondFloorContent = jsonObject
                    .getJSONObject(XML_MAP_STRING)
                    .getJSONObject(SHEET)
                    .getJSONObject(TOPIC)
                    .getJSONObject(CHILDREN)
                    .getJSONObject(TOPICS)
                    .getJSONObject(TOPIC).toString().trim();
        } catch (JSONException e ) {
            throw PpExpection.newsPpExpection(TEMPLATE_IS_ERROR);
        }
    }


    @Override
    public Map<String, List<CaseEntity>> analysisCase() {
        Topic topic = JSONObject.parseObject(secondFloorContent, Topic.class);
        log.info("json转Topic数据："+topic.toString());
        Children children = topic.getChildren();
        recursion(children, caseModuleMap, SystemConst.DEFAULT_MODULE_NAME + XmindConst.MODULE_SEPARATOR + topic.getTitle());
        return caseModuleMap;
    }


    public void recursion(Children children, Map<String, List<CaseEntity>> map, String parentName) {
        System.out.println("Xmind8解析");
        if (children == null) return;
        if (children.getTopics() == null) return;
        List<Topic> topics = jsonObjToList(children.getTopics().getTopic());
        if (topics == null || topics.size() < 1) return;
        for (Topic topic : topics) {
            //节点名称可能不是一个String 可能是一个title对象
            Object moduleTitle = topic.getTitle();
            if (!(moduleTitle instanceof String)) {
                // todo Integer不支持转JSON
                moduleTitle = ((JSONObject) moduleTitle).get("content");
            }
            String currentTitle = ((String) moduleTitle).replaceAll("\n","").trim();
            //用例节点
            if (currentTitle.matches(TITLE_PREFIX)) {
                String newModuleTitle = currentTitle;
                String s = newModuleTitle.split("：|:")[0].split("-")[1];
                String title = newModuleTitle.split("：|:",2)[1];
                CaseLevel caseLevel = CaseLevel.nameOf(s);
                CaseEntity caseEntity = CaseEntity.builder().build();
                caseEntity.setTitle(title);
                caseEntity.setLevel(caseLevel);
                List<CaseStepExpect> caseStepList = new ArrayList<>();
                if(topic.getChildren()!=null&&topic.getChildren().getTopics()!=null&&topic.getChildren().getTopics().getTopic()!=null){
                    List<Topic> topic1 = jsonObjToList(topic.getChildren().getTopics().getTopic());

                    for (Topic caseTopic : topic1) {
                        //用例节点的子节点
                        Object caseDescription = caseTopic.getTitle();
                        if (!(caseDescription instanceof String)) {
                            caseDescription = ((JSONObject) caseDescription).get("content");
                        }
                        //前置条件
                        if (((String) caseDescription).trim().matches(PRECONDITION_PREFIX)) {
                            caseEntity.setPrecondition((String) caseDescription);
                            //备注
                        } else if (((String) caseDescription).trim().matches(REMARK_PREFIX)) {
                            caseEntity.setRemark((String) caseDescription);
                            //步骤
                        } else {
                            CaseStepExpect caseStepExpect = new CaseStepExpect();
                            caseStepExpect.setStep((String) caseDescription);
                            StringBuilder expects = new StringBuilder();
                            if(caseTopic.getChildren()!=null&&caseTopic.getChildren().getTopics()!=null&&caseTopic.getChildren().getTopics().getTopic()!=null){
                                List<Topic> expectTopics = jsonObjToList(caseTopic.getChildren().getTopics().getTopic());
                                for (Topic expectTopic : expectTopics) {
                                    Object expectTopicTitle = expectTopic.getTitle();

                                    if (!(expectTopicTitle instanceof String) && !(expectTopicTitle instanceof  Integer)) {
                                        expectTopicTitle = expectTopicTitle != null?((JSONObject) expectTopicTitle).get("content"):"";
                                    }
                                    if (expectTopicTitle instanceof Integer) {
                                        expectTopicTitle = expectTopicTitle.toString();
                                    }
                                    expects.append((String) expectTopicTitle);
                                    expects.append(SystemConst.LINE_BREAK);
                                }
                            }
                            caseStepExpect.setExpect(expects.toString());
                            caseStepList.add(caseStepExpect);
                        }
                    }
                    if(caseStepList.size()==0){//当只有前置条件或者备注时，为了兼容测试计划的展示样式，赋步骤和结果为空
                        CaseStepExpect caseStepExpect = new CaseStepExpect();
                        caseStepExpect.setStep("");
                        caseStepExpect.setExpect("");
                        caseStepList.add(caseStepExpect);
                    }
                    caseEntity.setStepExpect(caseStepList);
//                map.get(initModuleName.toString()).add(caseEntity);
                    map.get(parentName).add(caseEntity);
                }else{
                    CaseStepExpect caseStepExpect = new CaseStepExpect();
                    caseStepExpect.setStep("");
                    caseStepExpect.setExpect("");
                    caseStepList.add(caseStepExpect);
                    caseEntity.setStepExpect(caseStepList);
                    map.get(parentName).add(caseEntity);
                }

            }else if(((String) moduleTitle).trim().matches(PRECONDITION_PREFIX)){//前置条件
                if(topic.getChildren()!=null&&topic.getChildren().getTopics()!=null&&topic.getChildren().getTopics().getTopic()!=null){
                    List<Topic> topic1 = jsonObjToList(topic.getChildren().getTopics().getTopic());
                    for (Topic caseTopic : topic1) {
                        //用例节点的子节点
                        Object caseDescription = caseTopic.getTitle();
                        if (!(caseDescription instanceof String)) {
                            caseDescription = ((JSONObject) caseDescription).get("content");
                        }
                        //是case
                        if (((String) caseDescription).trim().matches(TITLE_PREFIX)) {
                            String s = ((String) caseDescription).split("：")[0].split("-")[1];
                            String title = ((String) caseDescription).split("：",2)[1];
                            CaseLevel caseLevel = CaseLevel.nameOf(s);
                            CaseEntity caseEntity1 = CaseEntity.builder().build();
                            caseEntity1.setTitle(title);
                            caseEntity1.setLevel(caseLevel);
                            caseEntity1.setPrecondition((String) moduleTitle);
                            List<CaseStepExpect> caseStepList = new ArrayList<>();
                            //case下有子节点
                            if(caseTopic.getChildren()!=null&&caseTopic.getChildren().getTopics()!=null&&caseTopic.getChildren().getTopics().getTopic()!=null) {
                                List<Topic> topic2 = jsonObjToList(caseTopic.getChildren().getTopics().getTopic());

                                for (Topic child : topic2) {

                                    //用例节点的子节点
                                    Object childDescription = child.getTitle();
                                    if (!(childDescription instanceof String)) {
                                        childDescription = ((JSONObject) childDescription).get("content");
                                    }
                                    //备注
                                    if (((String) childDescription).trim().matches(REMARK_PREFIX)) {
                                        caseEntity1.setRemark((String) childDescription);
                                        //步骤
                                    } else {
                                        CaseStepExpect caseStepExpect = new CaseStepExpect();
                                        caseStepExpect.setStep((String) childDescription);
                                        StringBuilder expects = new StringBuilder();
                                        if(child.getChildren()!=null&&child.getChildren().getTopics()!=null&&child.getChildren().getTopics().getTopic()!=null){
                                            List<Topic> expectTopics = jsonObjToList(child.getChildren().getTopics().getTopic());
                                            for (Topic expectTopic : expectTopics) {
                                                Object expectTopicTitle = expectTopic.getTitle();

                                                if (!(expectTopicTitle instanceof String) && !(expectTopicTitle instanceof  Integer)) {
                                                    expectTopicTitle = ((JSONObject) expectTopicTitle).get("content");
                                                }
                                                if (expectTopicTitle instanceof Integer) {
                                                    expectTopicTitle = expectTopicTitle.toString();
                                                }
                                                expects.append((String) expectTopicTitle);
                                                expects.append(SystemConst.LINE_BREAK);
                                            }
                                        }
                                        caseStepExpect.setExpect(expects.toString());
                                        caseStepList.add(caseStepExpect);
                                    }


                                }
                                if(caseStepList.size()==0){//当只有前置条件或者备注时，为了兼容测试计划的展示样式，赋步骤和结果为空
                                    CaseStepExpect caseStepExpect = new CaseStepExpect();
                                    caseStepExpect.setStep("");
                                    caseStepExpect.setExpect("");
                                    caseStepList.add(caseStepExpect);
                                }
                                caseEntity1.setStepExpect(caseStepList);
                                map.get(parentName).add(caseEntity1);
                            }else{
                                CaseStepExpect caseStepExpect = new CaseStepExpect();
                                caseStepExpect.setStep("");
                                caseStepExpect.setExpect("");
                                caseStepList.add(caseStepExpect);
                                caseEntity1.setStepExpect(caseStepList);
                                map.get(parentName).add(caseEntity1);
                            }
                        }
                    }
                }

            } else {
                //  todo
//                initModuleName.append(topic.getTitle());
//                initModuleName.append(XmindConst.MODULE_SEPARATOR);
                map.put(parentName + XmindConst.MODULE_SEPARATOR + topic.getTitle(), new ArrayList<>());
                recursion(topic.getChildren(), map, parentName + XmindConst.MODULE_SEPARATOR + topic.getTitle());
            }
        }
    }

    /**
     * 对象转数组
     *
     */
    public List<Topic> jsonObjToList(Object o) {
        List<Topic> list = new ArrayList<>();
        if (o instanceof JSONObject) {
            String s = JSONObject.toJSONString(o);
            list.add(JSONObject.parseObject(s, Topic.class));
        } else {
            JSONArray jsonArray = (JSONArray) o;
            for (Object o1 : jsonArray) {
                String s = JSONObject.toJSONString(o1);
                list.add(JSONObject.parseObject(s, Topic.class));
            }
        }
        return list;
    }
}
