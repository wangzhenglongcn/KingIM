package kingim.ws;

import javax.servlet.http.HttpSession;
import com.github.pagehelper.PageInfo;
import kingim.model.FriendMessage;
import kingim.model.GroupMessage;
import kingim.service.FriendMessageService;
import kingim.service.GroupMessageService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import kingim.model.User;
import kingim.service.FriendService;
import kingim.utils.RedisUtils;

@Controller
@RequestMapping("/api/friend")
public class FriendWS {

	@Autowired
	private FriendMessageService friendMessageService;
	@Autowired
	private GroupMessageService groupMessageService;


	//记录发送的消息
	@RequestMapping(value = "saveMessage", produces = "text/plain; charset=utf-8")
	public @ResponseBody String saveMessage(Integer fromUserId,Integer toUserId,String content) {
		FriendMessage msg = new FriendMessage();
		msg.setContent(content);
		msg.setFromUserId(fromUserId);
		msg.setToUserId(toUserId);
		return friendMessageService.save(msg) + "";
	}


	//查询历史消息页面
	@RequestMapping(value = "getHistoryMessagePage")
	public String getHistoryMessagePage(HttpSession session,Integer id,String type,Integer pageNum,Integer pageSize,Integer userId, Model model) {
		User user = (User) session.getAttribute("user");
		if(pageNum==null){
			pageNum=1;
		}
		if(pageSize==null){
			pageSize=100000;
		}
		int fromUserId = userId;
		String str = getHistoryMsg(type,id,fromUserId,pageNum,pageSize);
		model.addAttribute("jsonStr","["+str+"]");
		model.addAttribute("toId",id);
		model.addAttribute("type",type);
		model.addAttribute("userId",userId);
		return "chatLog";
	}

	//查询历史消息接口
	@RequestMapping(value = "getHistoryMessage",produces="text/html;charset=UTF-8")
	public @ResponseBody String getHistoryMessage(HttpSession session,Integer id,String type,Integer pageNum,Integer pageSize,Integer userId) {
		User user = (User) session.getAttribute("user");
		if(pageNum==null){
			pageNum=1;
		}
		if(pageSize==null){
			pageSize=100;
		}
		int fromUserId = userId;
		String str = getHistoryMsg(type,id,fromUserId,pageNum,pageSize);
		return "["+str+"]";
	}

	private String getHistoryMsg(String type, Integer id, Integer fromUserId, Integer pageNum, Integer pageSize){
		if(type.equals("friend")){
			PageInfo<FriendMessage> list = friendMessageService.getHistoryMessage(fromUserId,id,pageNum,pageSize);
			JSONArray jsonArray= JSONArray.fromObject(list);
			return jsonArray.get(0).toString();
		}else if(type.equals("group")){
			PageInfo<GroupMessage> list = groupMessageService.getHistoryMessage(id,pageNum,pageSize);
			JSONArray jsonArray= JSONArray.fromObject(list);
			return jsonArray.get(0).toString();
		}
		return "";
	}

	@RequestMapping(value = "updateOnLineStatus", produces = "text/plain; charset=utf-8")
	public @ResponseBody String updateOnLineStatus(int userId,String status) {
        try{
            RedisUtils.set(userId + "_status", status);
            return "1";
        }catch(Exception e){
            e.printStackTrace();
        }
    	return "0";
	}


	@RequestMapping(value = "msgBoxPage", produces = "text/plain; charset=utf-8")
	public String msgBoxPage(HttpSession session,Model model,int userId) {
		//User user = (User) session.getAttribute("user");
		//int userId = user.getId();
		model.addAttribute("userId",userId);
		return "msgbox";
	}
	
}
