package com.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.business.domain.bo.GameBo;
import com.business.domain.vo.GameVo;
import com.business.service.IGamesService;
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
 * 游戏列表
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/games")
public class GameController extends BaseController {

    private final IGamesService tGamesService;

    /**
     * 查询游戏列表列表
     */
    @GetMapping("/list")
    public TableDataInfo<GameVo> list(GameBo bo, PageQuery pageQuery) {
        return tGamesService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出游戏列表列表
     */
    @SaCheckPermission("games:info:export")
    @Log(title = "游戏列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GameBo bo, HttpServletResponse response) {
        List<GameVo> list = tGamesService.queryList(bo);
        ExcelUtil.exportExcel(list, "游戏列表", GameVo.class, response);
    }

    /**
     * 获取游戏列表详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<GameVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(tGamesService.queryById(id));
    }

    /**
     * 新增游戏列表
     */
    @SaCheckPermission("games:info:add")
    @Log(title = "游戏列表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody GameBo bo) {
        return toAjax(tGamesService.insertByBo(bo));
    }

    /**
     * 修改游戏列表
     */
    @SaCheckPermission("games:info:edit")
    @Log(title = "游戏列表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody GameBo bo) {
        return toAjax(tGamesService.updateByBo(bo));
    }

    /**
     * 删除游戏列表
     *
     * @param ids 主键串
     */
    @SaCheckPermission("games:info:delete")
    @Log(title = "游戏列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(tGamesService.deleteWithValidByIds(List.of(ids), true));
    }
}
