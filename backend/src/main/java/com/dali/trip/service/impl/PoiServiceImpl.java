package com.dali.trip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dali.trip.common.BusinessException;
import com.dali.trip.common.PageResult;
import com.dali.trip.entity.Poi;
import com.dali.trip.mapper.PoiMapper;
import com.dali.trip.service.PoiService;
import com.dali.trip.vo.PoiVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * POI 服务实现
 */
@Service
@RequiredArgsConstructor
public class PoiServiceImpl implements PoiService {

    private final PoiMapper poiMapper;

    @Override
    public PageResult<PoiVO> getPoiList(String city, String area, String tag, String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<Poi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Poi::getStatus, 1);

        if (StringUtils.hasText(city)) {
            wrapper.eq(Poi::getCity, city);
        }
        if (StringUtils.hasText(area)) {
            wrapper.eq(Poi::getArea, area);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Poi::getName, keyword);
        }
        if (StringUtils.hasText(tag)) {
            wrapper.apply("JSON_CONTAINS(tags, {0})", "\"" + tag + "\"");
        }

        wrapper.orderByDesc(Poi::getId);

        Page<Poi> pageResult = poiMapper.selectPage(new Page<>(page, size), wrapper);

        List<PoiVO> voList = pageResult.getRecords().stream()
                .map(this::toPoiVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, pageResult.getTotal(), page, size);
    }

    @Override
    public PoiVO getPoiDetail(Long id) {
        Poi poi = poiMapper.selectById(id);
        if (poi == null || poi.getStatus() == 0) {
            throw BusinessException.notFound("景点不存在");
        }
        return toPoiVO(poi);
    }

    @Override
    public List<String> getAreas(String city) {
        return poiMapper.selectAreasByCity(city);
    }

    @Override
    public List<String> getTags() {
        return poiMapper.selectAllTags();
    }

    @Override
    public List<Poi> getPoisByCondition(String city, String theme, Integer limit) {
        LambdaQueryWrapper<Poi> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Poi::getStatus, 1)
               .eq(Poi::getCity, city);

        if (StringUtils.hasText(theme)) {
            wrapper.apply("JSON_CONTAINS(tags, {0})", "\"" + theme + "\"");
        }

        wrapper.last("LIMIT " + limit);

        return poiMapper.selectList(wrapper);
    }

    private PoiVO toPoiVO(Poi poi) {
        PoiVO vo = new PoiVO();
        BeanUtils.copyProperties(poi, vo);
        return vo;
    }
}
