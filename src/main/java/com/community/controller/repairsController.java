package com.community.controller;

import com.community.bean.Message;
import com.community.bean.Repairs;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class repairsController {

    @Autowired
    com.community.service.repairsService repairsService;


    @ResponseBody
    @RequestMapping("/repairPage")
    public void toPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/views/list_repair.jsp").forward(req, resp);
    }

    /**
     * 删除数据
     * @param ids
     * @return
     */
    @RequestMapping(value = "/repair/{repairsId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Message deleteRepair(@PathVariable("ids")String ids){
        if (ids.contains("-")){
            List<Integer> del_list = new ArrayList<>();
            String[] str_ids = ids.split("-");
            for(String string : str_ids){
                del_list.add(Integer.parseInt(string));
            }
            repairsService.deleteBatch(del_list);
        }
        else {
            Integer id = Integer.parseInt(ids);
            repairsService.deleteRepairs(id);
        }
        return Message.success();
    }


    /**
     * 直接发送Ajax请求
     * 信息�?houseHolder
     * {houseId=4, name='null', gender='null', age=null, members=null, telephone='null', email='null'}
     *
     * 问题：
     * 请求中有数据
     * 但是Employee对象封装不上
     * update rq_householder where houseId = 3;报错
     *
     * 原因：
     *   Tomcat
     *     1.将请求体中的数据，封装一个map
     *     2.SpringMVC封装POJO对象的时候。
     *                会把POJO中的每个属性值，request.getParamter("email")
     *
     *
     *     ajax返送PUT请求的问题
     *         PUT请求中的数据request.getParamter()那不到
     *         Tomcat服务器对于PUT请求，不会封装其中的请求数据为map
     *               只有POST形式的请求，Tomcat才会封装
     *               org.
     *
     *     配置上HttpPutFormContentFilter
     * 更新数据
     * @param repairs
     * @return
     */
    @RequestMapping(value = "/repair/{ids}", method = RequestMethod.PUT)
    @ResponseBody
    public Message updateRepair(Repairs repairs, HttpServletRequest request){
//        System.out.println("信息：" + holder);
////        System.out.println("将要显示的员工数据：" + "房屋编号"+ houseHolder.getHouseId() +"姓名："+ houseHolder.getName()
////                +"年龄："+ houseHolder.getAge() +"性别："+ houseHolder.getGender() +"家庭成员："+ houseHolder.getMembers()
////                +"电话："+ houseHolder.getTelephone() +"邮箱："+ houseHolder.getEmail());
        repairsService.updateRepairs(repairs);

        return Message.success().add("msg","修改成功");
    }

    /**
     * 查询账单显示
     * @param id
     * @return
     */

    @RequestMapping(value = "/repiar/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Message getRepair(@PathVariable("id")Integer id){
        Repairs repair = repairsService.getRepairs(id);
        return Message.success().add("repair", repair);
    }


    /**
     * 检查costId是否可用
     *
     */
//    @ResponseBody
//    @RequestMapping("/checkRepId")
//    public Message checkId(@RequestParam("id")Integer id){
//        //先判断用户名是否合法
//
//        boolean flag = repairsService.c
//        if (flag){
//            return Message.success();
//        }else{
//            return Message.fail();
//        }
//
//    }


    /**
     * 保存新信息
     *
     */
    @RequestMapping(value="/repair",method=RequestMethod.POST)
    @ResponseBody
    public Message saveRepair(@Valid Repairs repairs, BindingResult result){

        if (result.hasErrors()){
            Map<String, Object> map = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError : errors){
                System.out.println("错误的字段名:" + fieldError.getField());
                System.out.println("错误信息:" + fieldError.getDefaultMessage());
                map.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
            return Message.fail().add("errorsFields", map);
        }else{
            repairsService.updateRepairs(repairs);
            return Message.success();
        }


    }



    /**
     *  查询所有信息
     */
    @RequestMapping("/repair")
    @ResponseBody
    public Message getRepair(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                             Model model) {
        // 这不是一个分页查询
        // 引入PageHelper分页插件
        // 在查询之前只需要调用，传入页码，以及每页的大小
        PageHelper.startPage(pn, 10);
        // startPage后面紧跟的这个查询就是一个分页查询
        List<Repairs> houses = repairsService.getAll();
        // 使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了。
        // 封装了详细的分页信息,包括有我们查询出来的数据，传入连续显示的页数
        PageInfo page = new PageInfo(houses, 5);
        return Message.success().add("pageInfo", page);

    }

}
