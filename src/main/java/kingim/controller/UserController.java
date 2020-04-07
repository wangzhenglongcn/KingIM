package kingim.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import kingim.model.Friend;
import kingim.model.FriendType;
import kingim.model.Group;
import kingim.service.FriendTypeService;
import kingim.service.GroupUserService;
import kingim.utils.RedisUtils;
import kingim.vo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import kingim.model.User;
import kingim.service.UserService;

import java.util.*;

@Controller
@RequestMapping("/index")
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	private FriendTypeService friendTypeService;
	@Autowired
	private GroupUserService groupUserService;

	private static Logger logger = Logger.getLogger(UserController.class);

	@RequestMapping("login")
	public String login() {
		return "login";
	}

	//md5工具接口
	@RequestMapping("/md5")
	public @ResponseBody String md5(String str) {
		return SecureUtil.md5(str);
	}

	//注册接口
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public @ResponseBody String register(@RequestBody User user) {
		if(StringUtils.isEmpty(user.getUserName())){
			return "userName cannot be empty";
		}
		if(StringUtils.isEmpty(user.getPassword())){
			return "password cannot be empty";
		}
		if(StringUtils.isEmpty(user.getNickName())){
			return "nickName cannot be empty";
		}

		User user1 = new User();
		user1.setUserName(user.getUserName());
		if(null != userService.select(user1)){
			return "userName repeat,register error";
		}

		User user2 = new User();
		user2.setNickName(user.getNickName());
		if(null != userService.select(user2)){
			return "nickName repeat,register error";
		}

		if(StringUtils.isEmpty(user.getGender())){
			if(Math.random()*10 % 2 == 1){
				user.setGender(1);
			}else {
				user.setGender(0);
			}
		}
		if(StringUtils.isEmpty(user.getAvatar())){
			String rand = "0";
			int random = (int)(Math.random()*20+1);
			if(random < 10){
				rand += String.valueOf(random);
			}else{
				rand = String.valueOf(random);
			}
			if(user.getGender()==1){
				user.setAvatar("/images/girl-"+rand+".png");
			}else{
				user.setAvatar("/images/boy-"+rand+".png");
			}
		}
		try{
			user.setPassword(SecureUtil.md5(user.getPassword()));


			userService.save(user);
		}catch (Exception e){
			logger.error("register error",e);
			return "register error";
		}
		return "register success";
	}

	@RequestMapping("main")
	public String main() {
		return "main";
	}

	@RequestMapping("wap")
	public String wap() {
		return "wap";
	}

	@RequestMapping("myFriends")
	public String myFriends() {
		return "myFriends";
	}

	@RequestMapping("myGroups")
	public String myGroups() {
		return "myGroups";
	}

	@RequestMapping("searchFriends")
	public String searchFriends() {
		return "searchFriends";
	}

	@RequestMapping("/updateUserInfo")
	public @ResponseBody Map<String, Object> updateUserInfo(User user) {
		String code = "0";
		User u = new User();
		u.setId(user.getId());
		if(user.getSign()!=null){
			u.setSign(user.getSign());
		}
		if(user.getNickName()!=null){
			u.setNickName(user.getNickName());
		}
		if(user.getEmail()!=null){
			u.setEmail(user.getEmail());
		}
		if(user.getGender()!=null){
			u.setGender(user.getGender());
		}
		if(user.getPhone()!=null){
			u.setPhone(user.getPhone());
		}
		Map<String, Object> map = new HashMap<>();
		try {
			userService.update(u);
			code = "1";
		} catch (Exception e) {
			code = "0";
			e.printStackTrace();
		}
		map.put("code", code);
		return map;
	}

	/**
	 * 用户登录
	 * @author 1434262447@qq.com
	 * @param model
	 * @param userName   用户名
	 * @param password   密码
	 */
	@RequestMapping(value = "loginCheck", method = RequestMethod.POST)
	public String loginCheck(HttpSession session,HttpServletRequest request,Model model, String userName, String password) {


		// 是否存在用户
		User user = userService.matchUser(userName);
		if (user!=null) {
			session.setAttribute("user",user);
			boolean isMobile = isMobile(request);
			logger.info("用户:"+user.getNickName()+" userName:"+user.getUserName()+" isMobile="+isMobile);
			if(isMobile){
				return "wap";
			}
			return "myFriends";
		}
		model.addAttribute("msg", "用户名或密码错误，请重新输入!");
		logger.error("userName:"+userName+" password:"+password+"  登录失败!");
		return "login";
	}

	/**
	 * 退出登录
	 * @author 1434262447@qq.com
	 */
	@RequestMapping("logout")
	public String logout(SessionStatus sessionStatus, HttpSession session) {
		sessionStatus.setComplete();
		session.invalidate();
		return "redirect:/";
	}

	/**
	 * 给layim提供初始化数据服务，包括个人信息、好友列表信息、群组列表信息
	 * @param userId
	 * @author 1434262447@qq.com
	 */
	@RequestMapping(value = "getInitList", produces = "text/plain; charset=utf-8")
	public @ResponseBody String getInitList(int userId) {
		User u=userService.getUserById(userId);
		SNSUser mine=new SNSUser();
		mine.setId(u.getId());
		if(u.getAvatar()==null || u.getAvatar().equals("")){
			mine.setAvatar("images/avatar/default.png");
		}else{
			mine.setAvatar(u.getAvatar());
		}
		mine.setSign(u.getSign());
		mine.setUsername(u.getNickName());
		//获取redis中的用户在线状态
		String redisKey=userId+"_status";
		RedisUtils.set(redisKey, "online");
		mine.setStatus("online");
		SNSInit snsinit =new SNSInit();
		SNSdata data=new SNSdata();
		data.setMine(mine);
		snsinit.setData(data);
		List<SNSFriend> snsFriendList = new ArrayList<>();
		List<FriendType> list = friendTypeService.getFriendTypeByUserId(userId);
		if(list==null){
			int id=friendTypeService.getByUserId(userId);  //如果没有默认分组，则初始化分组
			SNSFriend snsFriend = new SNSFriend();
			snsFriend.setGroupname("好友");
			snsFriend.setOnline(0);
			snsFriend.setId(id);
			snsFriendList.add(snsFriend);
		}else{
			try{
				for(int i=0;i<list.size();i++){
					SNSFriend snsFriend = new SNSFriend();
					snsFriend.setGroupname(list.get(i).getTypeName());
					snsFriend.setId(list.get(i).getId());
					List<Friend> friendList=list.get(i).getFriends();
					List<SNSUser> snsUserList = new ArrayList<>();
					int onlineNum=0;
					for(int j=0;j<friendList.size();j++){
						SNSUser snsUser = new SNSUser();
						snsUser.setAvatar(friendList.get(j).getFriendInfo().getAvatar());
						snsUser.setSign(friendList.get(j).getFriendInfo().getSign());
						snsUser.setUsername(friendList.get(j).getFriendInfo().getNickName());
						snsUser.setId(friendList.get(j).getFriendId());
						onlineNum++;
						//获取redis中的用户在线状态
						redisKey=friendList.get(j).getFriendId()+"_status";
						if(RedisUtils.exists(redisKey)){
							snsUser.setStatus(RedisUtils.get(redisKey).toString());
						}else{
							snsUser.setStatus("offline");
						}
						snsUserList.add(snsUser);
					}
					snsFriend.setOnline(onlineNum);
					snsFriend.setList(snsUserList);
					snsFriendList.add(snsFriend);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		data.setFriend(snsFriendList);
		//获取我加入的群的列表
		List<Group> groupList = groupUserService.getByUserId(userId);
		List<SNSGroup> glist = new ArrayList<>();
		if(groupList!=null){
			for(int k=0;k<groupList.size();k++){
				SNSGroup sgroup = new SNSGroup();
				sgroup.setGroupname(groupList.get(k).getGroupName());
				sgroup.setId(groupList.get(k).getId());
				sgroup.setAvatar(groupList.get(k).getAvatar());
				glist.add(sgroup);
			}
			data.setGroup(glist);
		}
		snsinit.setData(data);

		return JSON.toJSONString(snsinit);
	}

	//从redis中获取离线消息
	@RequestMapping(value = "getOfflineMsgFromRedis")
	public @ResponseBody JSONArray getOfflineMsgFromRedis(int userId) {
		JSONArray jsonArray = new JSONArray();
		if (RedisUtils.exists(userId + "_msg"))
		{
			Long len = RedisUtils.llen(userId + "_msg");
			while (len > 0)
			{
				jsonArray.add(RedisUtils.rpop(userId + "_msg"));
				len--;
			}
		}
		return jsonArray;
	}

	public static boolean isMobile(HttpServletRequest request) {
		List<String> mobileAgents = Arrays.asList("ipad", "iphone os", "rv:1.2.3.4", "ucweb", "android", "windows ce", "windows mobile","micromessenger");
		String ua = request.getHeader("User-Agent").toLowerCase();
		for (String sua : mobileAgents) {
			if (ua.indexOf(sua) > -1) {
				return true;//手机端
			}
		}
		return false;//PC端
	}


}
