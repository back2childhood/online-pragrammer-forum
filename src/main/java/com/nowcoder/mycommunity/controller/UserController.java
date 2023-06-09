package com.nowcoder.mycommunity.controller;

import com.google.protobuf.compiler.PluginProtos;
import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${mucommunity.path.upload}")
    private String uploadPath;

    @Value("${mycommunity.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error", "please choose a image");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix =  filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "the file format is incorrect");
            return "/site/setting";
        }

        // maybe deffierent user will upload the same name file, we should generate random file name.
        filename = CommunityUtil.generateUUID() + suffix;
        // confirm the file path
        File dest = new File(uploadPath + "/" + filename);
        try {
            // store file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("file upload failed" + e.getMessage());
            throw new RuntimeException("the file fails to be uploaded, and an exception occurs on the server.");
        }

        // update the path of the current user's profile picture
        // eg:http://localhost:8080/community/user/header/xxx.img
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // server storage path
        fileName = uploadPath + "/" + fileName;
        // parsing the file suffix
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // response image
        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                OutputStream outputStream = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fileInputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("failed to read profile picture" + e.getMessage());
            throw new RuntimeException("failed to read profile picture");
        }
    }
}
