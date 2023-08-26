package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    // the page of the statistics
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    // calculates the UV of the site
//    @PostMapping(path = "/data/uv")
    @RequestMapping(path = "/data/uv", method = {RequestMethod.GET/*, RequestMethod.POST*/})
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");

        long uv = dataService.calculateUV(start, end);

        System.out.println("------------------------------------------------" + uv);

        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);

        // `forward` means this function just disposal a half of the request,
        // and the rest of request need to be executed by other functions
        // post request still be post after forwarding
        return "forward:/data";
    }

    // calculates the DAU of the site
//    @PostMapping(path = "/data/dau")
    @RequestMapping(path = "/data/dau", method = {RequestMethod.GET/*, RequestMethod.POST*/})
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);

        // `forward` means this function just disposal a half of the request,
        // and the rest of request need to be executed by other functions
        // post request still be post after forwarding
        return "forward:/data";
    }
}
