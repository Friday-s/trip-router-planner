package com.dali.trip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.trip.entity.Poi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * POI Mapper
 */
@Mapper
public interface PoiMapper extends BaseMapper<Poi> {

    /**
     * 获取所有区域
     */
    @Select("SELECT DISTINCT area FROM pois WHERE city = #{city} AND status = 1 AND area IS NOT NULL ORDER BY area")
    List<String> selectAreasByCity(String city);

    /**
     * 获取所有标签
     */
    @Select("SELECT DISTINCT JSON_UNQUOTE(JSON_EXTRACT(tags, CONCAT('$[', n.n, ']'))) as tag " +
            "FROM pois, (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
            "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) n " +
            "WHERE JSON_EXTRACT(tags, CONCAT('$[', n.n, ']')) IS NOT NULL AND status = 1 " +
            "ORDER BY tag")
    List<String> selectAllTags();
}
