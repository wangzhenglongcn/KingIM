package kingim.service;

import com.github.pagehelper.PageInfo;
import kingim.model.FriendMessage;

public interface FriendMessageService extends BaseService<FriendMessage> {
    //获取用户消息记录
    PageInfo<FriendMessage> getHistoryMessage(Integer fromUserId, Integer toUserId, Integer pageNum, Integer pageSize);
}