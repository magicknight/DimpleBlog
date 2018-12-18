package com.dimple.service.impl;

import com.dimple.bean.Links;
import com.dimple.bean.LinksDetails;
import com.dimple.bean.LinksExample;
import com.dimple.framework.enums.LinksSearchCode;
import com.dimple.dao.CustomMapper;
import com.dimple.dao.LinksMapper;
import com.dimple.service.LinksService;
import com.dimple.framework.message.Result;
import com.dimple.framework.message.ResultEnum;
import com.dimple.framework.message.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: LinksServiceImpl
 * @Description:
 * @Auther: Owenb
 * @Date: 11/21/18 17:31
 * @Version: 1.0
 */
@Service
@Transactional
public class LinksServiceImpl implements LinksService {
    @Autowired
    LinksMapper linksMapper;
    @Autowired
    CustomMapper customMapper;

    @Override
    public List<Links> getAllLinksHandled(String title, Date startTime, Date endTime, Boolean display) {

        LinksExample linksExample = new LinksExample();
        LinksExample.Criteria criteria = linksExample.createCriteria();
        //获取已经处理的友链
        criteria.andStatusEqualTo(true);
        if (startTime != null && endTime != null) {
            criteria.andCreateTimeBetween(startTime, endTime);
        } else if (startTime != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startTime);
        } else if (endTime != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endTime);
        }

        if (StringUtils.isNotBlank(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (display != null) {
            criteria.andDisplayEqualTo(display);
        }
        List<Links> links = linksMapper.selectByExample(linksExample);
        return links;
    }

    @Override
    public Result switchLinkStatus(Integer linkId, Boolean status) {
        if (linkId == null || status == null) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        Links link = linksMapper.selectByPrimaryKey(linkId);
        if (link == null) {
            return ResultUtil.error(ResultEnum.LINKS_NOT_FOUND.getCode(), ResultEnum.LINKS_NOT_FOUND.getMsg());
        }
        link.setDisplay(!status);
        linksMapper.updateByPrimaryKey(link);
        return ResultUtil.success();
    }

    @Override
    public Result deleteLinks(Integer[] ids) {
        //参数合法性校验
        if (ids == null || ids.length == 0) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        //todo 事务的设置，需要设置单个事务失败不影响整个事物
        //记录操作的数
        Integer count = 0;
        for (Integer id : ids) {
            Links link = linksMapper.selectByPrimaryKey(id);
            if (link == null) {
                return ResultUtil.error(ResultEnum.LINKS_NOT_FOUND.getCode(), ResultEnum.LINKS_NOT_FOUND.getMsg());
            }
            count += linksMapper.deleteByPrimaryKey(id);
        }
        return ResultUtil.success(count);
    }

    @Override
    public Result getLinkInfo(Integer linkId) {
        if (linkId == null) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        Links link = linksMapper.selectByPrimaryKey(linkId);
        return ResultUtil.success(link);
    }

    @Override
    public Result addLink(Links links) {
        if (links == null || StringUtils.isBlank(links.getTitle()) || StringUtils.isBlank(links.getUrl())) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        links.setCreateTime(new Date());
        //设置是否已经处理，默认未处理
        links.setStatus(false);
        linksMapper.insert(links);
        return ResultUtil.success();
    }

    @Override
    public Result applyLink(Links links) {
        if (links == null || StringUtils.isBlank(links.getTitle()) || StringUtils.isBlank(links.getUrl())) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        links.setCreateTime(new Date());
        //设置是否显示，默认是不显示
        links.setDisplay(false);
        //设置是否已经处理，默认未处理
        links.setStatus(false);
        linksMapper.insert(links);
        return ResultUtil.success();
    }

    @Override
    public Result updateLinkInfo(Links links) {
        if (links == null || links.getLinkId() == null) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        int i = linksMapper.updateByPrimaryKeySelective(links);
        return ResultUtil.success(i);
    }

    @Override
    public LinksDetails getDetails() {
        LinksDetails linksDetails = customMapper.selectLinksDetails();
        return linksDetails;
    }

    @Override
    public List<Links> getAllLinksUnHandled(Date startTime, Date endTime, String title) {
        LinksExample linksExample = new LinksExample();
        LinksExample.Criteria criteria = linksExample.createCriteria();
        //设置查询条件为未处理的
        criteria.andStatusEqualTo(false);
        if (startTime != null && endTime != null) {
            criteria.andCreateTimeBetween(startTime, endTime);
        } else if (startTime != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startTime);
        } else if (endTime != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endTime);
        }
        if (StringUtils.isNotBlank(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        List<Links> links = linksMapper.selectByExample(linksExample);
        return links;
    }

    @Override
    public Result passLinksApply(Integer linkId) {
        if (linkId == null) {
            return ResultUtil.error(ResultEnum.LINKS_PARAM_ERROR.getCode(), ResultEnum.LINKS_PARAM_ERROR.getMsg());
        }
        Links links = linksMapper.selectByPrimaryKey(linkId);
        if (links == null) {
            return ResultUtil.error(ResultEnum.LINKS_NOT_FOUND.getCode(), ResultEnum.LINKS_NOT_FOUND.getMsg());
        }
        //设置通过友链申请
        links.setStatus(true);
        int i = linksMapper.updateByPrimaryKey(links);
        return ResultUtil.success(i);
    }

    @Override
    public Integer getUnHandledLinksCount() {
        Integer count = customMapper.selectUnHandledLinksCount();
        return count;
    }

    @Override
    public List<Links> getLinksCondition(String title, Date startTime, Date endTime, Boolean display, LinksSearchCode searchCode) {
        if (searchCode == null) {
            return getAllLinksHandled(title, startTime, endTime, display);
        }
        List<Links> links = null;
        switch (searchCode) {
            case SEARCH_CODE_ALL:
                links = getLinksAll();
                break;
            case SEARCH_CODE_DIE:
                links = getLinksDie();
                break;
            case SEARCH_CODE_UNHANDLED:
                links = getAllLinksUnHandled(null, null, null);
                break;
            case SEARCH_CODE_HIDE:
                links = getLinksHide();
                break;
            case SEARCH_CODE_DISPLAY:
                links = getLinksDisplay();
                break;
            default:
                break;
        }
        return links;
    }

    @Override
    public List<Links> getLinksAll() {
        return linksMapper.selectByExample(null);
    }

    @Override
    public List<Links> getLinksDie() {
        LinksExample linksExample = new LinksExample();
        LinksExample.Criteria criteria = linksExample.createCriteria();
        criteria.andAvailableEqualTo(false);
        return linksMapper.selectByExample(linksExample);
    }

    @Override
    public List<Links> getLinksHide() {
        LinksExample linksExample = new LinksExample();
        LinksExample.Criteria criteria = linksExample.createCriteria();
        criteria.andDisplayEqualTo(false);
        return linksMapper.selectByExample(linksExample);
    }

    @Override
    public List<Links> getLinksDisplay() {
        LinksExample linksExample = new LinksExample();
        LinksExample.Criteria criteria = linksExample.createCriteria();
        criteria.andDisplayEqualTo(true);
        return linksMapper.selectByExample(linksExample);
    }
}
