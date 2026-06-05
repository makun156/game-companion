package com.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.business.domain.bo.GameCategoryBo;
import com.business.domain.vo.GameCategoryVo;
import com.business.service.IGameCategoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏类别
 *
 * @author Mk
 * @date 2026-06-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/gameCategory")
public class GameCategoryController extends BaseController {

    private final IGameCategoryService gameCategoryService;

    /**
     * 查询游戏类别列表
     */
    @SaCheckPermission("gameCategory:info:list")
    @GetMapping("/list")
    public TableDataInfo<GameCategoryVo> list(GameCategoryBo bo, PageQuery pageQuery) {
        return gameCategoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出游戏类别列表
     */
    @SaCheckPermission("gameCategory:info:export")
    @Log(title = "游戏类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GameCategoryBo bo, HttpServletResponse response) {
        List<GameCategoryVo> list = gameCategoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "游戏类别", GameCategoryVo.class, response);
    }

    /**
     * 获取游戏类别详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("gameCategory:info:query")
    @GetMapping("/{id}")
    public R<GameCategoryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(gameCategoryService.queryById(id));
    }

    /**
     * 新增游戏类别
     */
    @SaCheckPermission("gameCategory:info:add")
    @Log(title = "游戏类别", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody GameCategoryBo bo) {
        return toAjax(gameCategoryService.insertByBo(bo));
    }

    /**
     * 修改游戏类别
     */
    @SaCheckPermission("gameCategory:info:edit")
    @Log(title = "游戏类别", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody GameCategoryBo bo) {
        return toAjax(gameCategoryService.updateByBo(bo));
    }

    /**
     * 删除游戏类别
     *
     * @param ids 主键串
     */
    @SaCheckPermission("gameCategory:info:remove")
    @Log(title = "游戏类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(gameCategoryService.deleteWithValidByIds(List.of(ids), true));
    }
}
