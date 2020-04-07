package kingim.service.impl;

import kingim.dao.GroupMapper;
import kingim.model.Group;
import kingim.model.User;
import kingim.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

/**
 * 群组Service
 * @author 1434262447@qq.com
 */
@Service
public class GroupServiceImpl extends BaseServiceImpl<Group> implements GroupService{

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public Mapper<Group> getMapper() {
        return groupMapper;
    }

    @Override
    public User getGroupMaster(int groupId) {
        return groupMapper.getGroupMaster(groupId);
    }

}
