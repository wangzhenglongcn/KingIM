package kingim.service;

import com.github.pagehelper.PageInfo;
import kingim.model.GroupMessage;

public interface GroupMessageService extends BaseService<GroupMessage> {
    //获取群消息记录
    PageInfo<GroupMessage> getHistoryMessage(Integer groupId, Integer pageNum, Integer pageSize);
}