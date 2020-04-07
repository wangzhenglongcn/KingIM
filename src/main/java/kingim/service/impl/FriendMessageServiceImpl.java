package kingim.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kingim.dao.FriendMessageMapper;
import kingim.model.FriendMessage;
import kingim.service.FriendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class FriendMessageServiceImpl extends BaseServiceImpl<FriendMessage> implements FriendMessageService {
    @Autowired
    private FriendMessageMapper friendMessageMapper;

    public Mapper<FriendMessage> getMapper() {
        return friendMessageMapper;
    }

    public PageInfo<FriendMessage> getHistoryMessage(Integer fromUserId, Integer toUserId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<FriendMessage> friendMsgList = friendMessageMapper.getHistoryMessage(fromUserId, toUserId);
        PageInfo<FriendMessage> page = new PageInfo<>(friendMsgList);
        return page;
    }

}
