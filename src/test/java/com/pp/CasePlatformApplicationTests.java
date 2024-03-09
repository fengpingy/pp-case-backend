package com.pp;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.pp.common.enums.system.RoleType;
import com.pp.dto.PlanCaseDTO;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;

import com.pp.jira.JiraAuth;

@SpringBootTest
public class CasePlatformApplicationTests {

    @Test
    public void test1(){
        PlanCaseDTO planCaseDTO = new PlanCaseDTO();
        planCaseDTO.setIsExecute(1);

        if (planCaseDTO.getIsExecute().equals(1)){
            System.out.println("哈哈哈哈哈");
        }


    }

    @Test
    public void test2(){
        String role = "TEST";
        if (role.equals(RoleType.nameOf(role).name())){
            System.out.println(RoleType.nameOf(role).name());
            System.out.println("hhh1");
        }else {
            System.out.println("xixiixi");
        }
        Date date = new Date();
        System.out.println(date);

    }

    @Test
    public void test3(){
        String s = "tc-p3：请假规则-限制规则-跨有效期使用选项-默认“不限制”";
        String[] a =  s.split("[:|：]{1}");
        String[] t = a[0].split("[-|_]{1}");

//        Arrays.stream(a).forEach(System.out::println);
        Arrays.stream(t).forEach(System.out::println);

    }
    @Test
    public void test4(){
       Object a = null;

       System.out.println(a.toString());

    }

    @Test
    public void test5() throws URISyntaxException {

        JiraRestClient jiraRestClient = JiraAuth.loginJira();




    }




}
