package org.peach.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.peach.auth.entity.User;
import org.peach.common.mybatis.mapper.BaseMapper;


/**
 * 见类名。
 *
 * @author leiyangjun
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
