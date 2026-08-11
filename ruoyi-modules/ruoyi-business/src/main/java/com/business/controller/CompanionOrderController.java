package com.business.controller;

import com.business.domain.bo.CompanionOrderBo;
import com.business.domain.vo.CompanionOrderVo;
import com.business.service.ICompanionOrderService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 陪玩订单管理端 Controller.
 * <p>
 * 管理端仅提供查询和查看详情功能，订单的创建/修改/删除由小程序端或业务层处理.
 *
 * @author companion
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/business/companion/order")
public class CompanionOrderController extends BaseController {

    private final ICompanionOrderService companionOrderService;

    /**
     * 分页查询陪玩订单列表
     */
    @GetMapping("/list")
    public TableDataInfo<CompanionOrderVo> list(CompanionOrderBo bo, PageQuery pageQuery) {
        return companionOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取陪玩订单详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<CompanionOrderVo> getInfo(@NotNull(message = "主键不能为空")
                                       @PathVariable Long id) {
        return R.ok(companionOrderService.queryById(id));
    }

}