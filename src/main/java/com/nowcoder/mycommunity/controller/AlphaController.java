package com.nowcoder.mycommunity.controller;

//import ch.qos.logback.core.model.Model;
import com.nowcoder.mycommunity.service.AlphaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @ResponseBody
    @RequestMapping("/hello")
    public String sayHello(){
        return "hello world";
    }

    @ResponseBody
    @RequestMapping("/data")
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        // 获取参数
        System.out.println(request.getParameter("code"));

        // return response data
        response.setContentType("text/html;charset=utf-8");
        try (
            PrintWriter printWriter = response.getWriter();
        ){
            printWriter.write("<h1>NeoCoder</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // GET request example:
    // students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false, defaultValue = "20") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "all students";
    }

    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    // POST request
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name+" "+age);
        return "success";
    }

    // html response
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "tom");
        modelAndView.addObject("age", "15");

        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "king");
        model.addAttribute("age", "15");
        return "/demo/view";
    }

    // json response; commonly used in async request
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "tom");
        map.put("age", 32);
        map.put("salary", 8000);
        return map;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("name", "tom");
        map.put("age", 32);
        map.put("salary", 8000);
        list.add(map);

        map = new HashMap<>();
        map.put("name", "t0om");
        map.put("age", 320);
        map.put("salary", 80000);
        list.add(map);

        map = new HashMap<>();
        map.put("name", "to00m");
        map.put("age", 3200);
        map.put("salary", 800000);
        list.add(map);

        return list;
    }
}
