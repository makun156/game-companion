package com.business.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import com.business.domain.vo.GameCompanionUserVo;
import com.business.domain.bo.GameCompanionUserBo;
import com.business.service.IGameCompanionUserService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 陪玩表
 *
 * @author Mk
 * @date 2026-06-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/companion")
public class GameCompanionUserController extends BaseController {

    private final IGameCompanionUserService gameCompanionUserService;

    /**
     * 查询陪玩表列表
     */
    @GetMapping("/list")
    public TableDataInfo<GameCompanionUserVo> list(GameCompanionUserBo bo, PageQuery pageQuery) {
        return gameCompanionUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出陪玩表列表
     */
    @SaCheckPermission("user:companion:export")
    @Log(title = "陪玩表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GameCompanionUserBo bo, HttpServletResponse response) {
        List<GameCompanionUserVo> list = gameCompanionUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "陪玩表", GameCompanionUserVo.class, response);
    }

    /**
     * 获取当前登录陪玩信息
     */
    @GetMapping("/info")
    public R<GameCompanionUserVo> getInfo() {
        Long userId = LoginHelper.getUserId();
        GameCompanionUserVo companionUserVo = gameCompanionUserService.queryById(userId);
        if (companionUserVo == null) {
            throw new ServiceException("陪玩信息不存在");
        }
        return R.ok(companionUserVo);
    }

    /**
     * 获取陪玩表详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<GameCompanionUserVo> getInfoById(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(gameCompanionUserService.queryById(id));
    }

    /**
     * 新增陪玩表
     */
    @SaCheckPermission("user:companion:add")
    @Log(title = "陪玩表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody GameCompanionUserBo bo) {
        return toAjax(gameCompanionUserService.insertByBo(bo));
    }

    /**
     * 修改陪玩表
     */
    @SaCheckPermission("user:companion:edit")
    @Log(title = "陪玩表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody GameCompanionUserBo bo) {
        return toAjax(gameCompanionUserService.updateByBo(bo));
    }

    /**
     * 删除陪玩表
     *
     * @param ids 主键串
     */
    @SaCheckPermission("user:companion:remove")
    @Log(title = "陪玩表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(gameCompanionUserService.deleteWithValidByIds(List.of(ids), true));
    }
}
