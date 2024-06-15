package com.shiqu.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.schedule.pojos.TaskinfoLogs;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shiqu
 */
@Mapper
@Repository
public interface TaskinfoLogsMapper extends BaseMapper<TaskinfoLogs> {

}
