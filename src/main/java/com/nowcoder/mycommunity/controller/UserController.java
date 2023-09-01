package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.FollowService;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

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

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${qiniu.key.accessKey}")
    private String accessKey;

    @Value("${qiniu.key.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // set the name of the uploaded file
        String fileName = CommunityUtil.generateUUID();

        // set the information of the response
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));

        // generate the certificate of uploading
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    // update the path of profile picture
    @PostMapping(path = "/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(1, "The file name cane is required.");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJSONString(0);
    }

    // Deprecated
    @Deprecated
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "please choose a image");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
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

    @Deprecated
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // server storage path
        fileName = uploadPath + "/" + fileName;
        // parsing the file suffix
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // response image
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                OutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("failed to read profile picture" + e.getMessage());
            throw new RuntimeException("failed to read profile picture");
        }
    }

    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(String confirmPassword, String oldPassword, String newPassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user, oldPassword, newPassword, confirmPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/logout";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "/site/setting";
        }
    }

    // personnel homepage
    @GetMapping(path = "/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("this user doesn't exist!");
        }

        // user
        model.addAttribute("user", user);

        // number of received likes
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // the number of follow
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        // the number of subscriptions
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
//            System.out.println(hasFollowed);
        }
        model.addAttribute("hasFollowed", hasFollowed);
//        System.out.println(hostHolder.getUser() != null? true: false);

        return "/site/profile";
    }
}
