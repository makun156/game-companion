package com.business.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import com.business.domain.vo.EsportsHotelVo;
import com.business.domain.bo.EsportsHotelBo;
import com.business.service.IEsportsHotelService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 商家表
 *
 * @author Mk
 * @date 2026-06-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/hotel")
public class EsportsHotelController extends BaseController {

    private final IEsportsHotelService esportsHotelService;

    /**
     * 查询商家表列表
     */
    @GetMapping("/list")
    public TableDataInfo<EsportsHotelVo> list(EsportsHotelBo bo, PageQuery pageQuery) {
        return esportsHotelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商家表列表
     */
    @SaCheckPermission("user:hotel:export")
    @Log(title = "商家表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EsportsHotelBo bo, HttpServletResponse response) {
        List<EsportsHotelVo> list = esportsHotelService.queryList(bo);
        ExcelUtil.exportExcel(list, "商家表", EsportsHotelVo.class, response);
    }

    /**
     * 获取商家表详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<EsportsHotelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(esportsHotelService.queryById(id));
    }

    /**
     * 新增商家表
     */
    @SaCheckPermission("user:hotel:add")
    @Log(title = "商家表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody EsportsHotelBo bo) {
        return toAjax(esportsHotelService.insertByBo(bo));
    }

    /**
     * 修改商家表
     */
    @SaCheckPermission("user:hotel:edit")
    @Log(title = "商家表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EsportsHotelBo bo) {
        return toAjax(esportsHotelService.updateByBo(bo));
    }

    /**
     * 删除商家表
     *
     * @param ids 主键串
     */
    @SaCheckPermission("user:hotel:remove")
    @Log(title = "商家表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(esportsHotelService.deleteWithValidByIds(List.of(ids), true));
    }
}
