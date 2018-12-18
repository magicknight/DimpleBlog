package com.dimple.controller;

import com.dimple.bean.Blog;
import com.dimple.service.FrontService;
import com.dimple.framework.message.Result;
import com.dimple.framework.message.ResultUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FrontController
 * @Description: 前端请求的Controller
 * @Auther: Owenb
 * @Date: 12/12/18 17:00
 * @Version: 1.0
 */
@Controller
public class FrontController {


    @Autowired
    FrontService frontService;

    /**
     * 跳转到首页
     *
     * @return
     */
    @RequestMapping("/")
    public String index(Model model) {
        Map<String, List<Blog>> map = frontService.getCategoryInfo();
        model.addAttribute("categories", frontService.getCategoryName());
        model.addAttribute("blogs", frontService.getBlogsInfo());
        model.addAttribute("peopleSee", frontService.getBlogsPeopleSee());
        return "front/index";
    }


    /**
     * 获取个人名片信息
     * todo 做一个网站资源管理
     *
     * @return
     */
    @GetMapping("/front/businessCard")
    @ResponseBody
    public Result getBusinessCard() {
        return null;
    }


    @GetMapping("/api/test")
    @ResponseBody
    public Object test() {
        List<Blog> blogsPeopleSee = frontService.getBlogsPeopleSee();
        return blogsPeopleSee;
    }


    @GetMapping("/api/front/newestBlog")
    @ResponseBody
    public Result getNewestBlog(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> blogs = frontService.getNewestBlog();
        return ResultUtil.success(blogs);
    }

}
