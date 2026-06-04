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
import com.business.domain.vo.CityVo;
import com.business.domain.bo.CityBo;
import com.business.service.ICityService;

/**
 * 区域
 *
 * @author Mk
 * @date 2026-06-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/city")
public class CityController extends BaseController {

    private final ICityService cityService;

    /**
     * 查询区域列表
     */
    @SaCheckPermission("city:info:list")
    @GetMapping("/list")
    public R<List<CityVo>> list(CityBo bo) {
        List<CityVo> list = cityService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 导出区域列表
     */
    @SaCheckPermission("city:info:export")
    @Log(title = "区域", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CityBo bo, HttpServletResponse response) {
        List<CityVo> list = cityService.queryList(bo);
        ExcelUtil.exportExcel(list, "区域", CityVo.class, response);
    }

    /**
     * 获取区域详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("city:info:query")
    @GetMapping("/{id}")
    public R<CityVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(cityService.queryById(id));
    }

    /**
     * 新增区域
     */
    @SaCheckPermission("city:info:add")
    @Log(title = "区域", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CityBo bo) {
        return toAjax(cityService.insertByBo(bo));
    }

    /**
     * 修改区域
     */
    @SaCheckPermission("city:info:edit")
    @Log(title = "区域", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CityBo bo) {
        return toAjax(cityService.updateByBo(bo));
    }

    /**
     * 删除区域
     *
     * @param ids 主键串
     */
    @SaCheckPermission("city:info:remove")
    @Log(title = "区域", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(cityService.deleteWithValidByIds(List.of(ids), true));
    }
}
