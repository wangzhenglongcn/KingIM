package kingim.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import kingim.dao.GroupMessageMapper;
import kingim.model.FriendMessage;
import kingim.model.GroupMessage;
import kingim.service.GroupMessageService;
import kingim.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class GroupMessageServiceImpl extends BaseServiceImpl<GroupMessage> implements GroupMessageService {
    @Autowired
    private GroupMessageMapper groupMessageMapper;

    public Mapper<GroupMessage> getMapper() {
        return groupMessageMapper;
    }

    @Override
    public PageInfo<GroupMessage> getHistoryMessage(Integer groupId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GroupMessage> list = groupMessageMapper.getHistoryMessage(groupId);
        PageInfo<GroupMessage> page = new PageInfo<>(list);
        return page;
    }


}
