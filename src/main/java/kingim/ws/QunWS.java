package kingim.ws;

import java.util.ArrayList;
import java.util.List;
import kingim.model.*;
import kingim.service.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import kingim.service.GroupService;
import kingim.service.GroupUserService;
import kingim.vo.SNSMData;
import kingim.vo.SNSMember;
import kingim.vo.SNSUser;

@Controller
@RequestMapping("/api/qun")
public class QunWS {
 
  @Autowired
  private GroupUserService groupUserService;
  @Autowired
  private GroupService groupService;
  @Autowired
  private GroupMessageService groupMessageService;

	//获取群成员列表(只含用户名)
 	@RequestMapping(value = "getSimpleMemberByGroupId", produces = "text/plain; charset=utf-8")
	public @ResponseBody String getSimpleMemberByGroupId(int id){
		return JSON.toJSONString(groupUserService.getSimpleMemberByGroupId(id));
	}
	 
	//获取我的群组列表(加入的)
	@RequestMapping(value = "getGroupByUserId", produces = "text/plain; charset=utf-8")
	public @ResponseBody String getGroupByUserId(int userId){
		List<Group> list = groupUserService.getByUserId(userId);
		return JSON.toJSONString(list);
	}

	//记录发送的消息
	@RequestMapping(value = "saveMessage", produces = "text/plain; charset=utf-8")
	public @ResponseBody String saveMessage( Integer userId, Integer groupId, String content) {
 		GroupMessage msg = new GroupMessage();
 		msg.setGroupId(groupId);
 		msg.setUserId(userId);
 		msg.setContent(content);
		return groupMessageService.save(msg) + "";
	}
	
	//获取群成员信息 for sns
	@RequestMapping(value = "getByGroupId", produces = "text/plain; charset=utf-8")
	public @ResponseBody String getByGroupId(int id){
		List<GroupUser> list = groupUserService.getByGroupId(id);
		User groupMaster = groupService.getGroupMaster(id);
		SNSMember member = new SNSMember();
		List<SNSUser> userList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			SNSUser snsUser =new SNSUser();
			snsUser.setId(list.get(i).getId());
			snsUser.setAvatar(list.get(i).getUser().getAvatar());
			snsUser.setSign("无个性不签名");
			snsUser.setUsername(list.get(i).getUser().getNickName());
			userList.add(snsUser);
		}
		SNSUser snsMaster=new SNSUser();
		snsMaster.setAvatar(groupMaster.getAvatar());
		snsMaster.setId(groupMaster.getId());
		snsMaster.setSign(groupMaster.getSign());
		snsMaster.setUsername(groupMaster.getNickName());
		SNSMData data = new SNSMData();
		data.setList(userList);
		data.setOwner(snsMaster);
		member.setCode(0);
		member.setData(data);
		return JSON.toJSONString(member);
	}
}
