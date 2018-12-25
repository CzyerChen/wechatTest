package com.github.clairechen.controller;

import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpGetSelfMenuInfoResult;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

/**
 * @author Claire.Chen
 */
@RestController
@RequestMapping("/wechat/menu")
public class WxMenuController implements WxMpMenuService {

  @Autowired
  private WxMpService wxService;


  @Override
  @PostMapping("/create")
  public String menuCreate(@RequestBody WxMenu menu) throws WxErrorException {
    return this.wxService.getMenuService().menuCreate(menu);
  }

  @GetMapping("/create")
  public String menuCreateSample() throws WxErrorException {
    WxMenu menu = new WxMenu();
    WxMenuButton button1 = new WxMenuButton();
    button1.setType(MenuButtonType.CLICK);
    button1.setName("今日歌曲");
    button1.setKey("V1001_TODAY_MUSIC");

    WxMenuButton button3 = new WxMenuButton();
    button3.setName("菜单");

    menu.getButtons().add(button1);
//        menu.getButtons().add(button2);
    menu.getButtons().add(button3);

    WxMenuButton button31 = new WxMenuButton();
    button31.setType(MenuButtonType.VIEW);
    button31.setName("搜索");
    button31.setUrl("http://www.soso.com/");

    WxMenuButton button32 = new WxMenuButton();
    button32.setType(MenuButtonType.VIEW);
    button32.setName("视频");
    button32.setUrl("http://v.qq.com/");

    WxMenuButton button33 = new WxMenuButton();
    button33.setType(MenuButtonType.CLICK);
    button33.setName("赞一下我们");
    button33.setKey("V1001_GOOD");

    button3.getSubButtons().add(button31);
    button3.getSubButtons().add(button32);
    button3.getSubButtons().add(button33);

    return this.wxService.getMenuService().menuCreate(menu);
  }


  @Override
  @GetMapping("/create/{json}")
  public String menuCreate(@PathVariable String json) throws WxErrorException {
    return this.wxService.getMenuService().menuCreate(json);
  }

  @Override
  @GetMapping("/delete")
  public void menuDelete() throws WxErrorException {
    this.wxService.getMenuService().menuDelete();
  }

  @Override
  @GetMapping("/delete/{menuId}")
  public void menuDelete(@PathVariable String menuId) throws WxErrorException {
    this.wxService.getMenuService().menuDelete(menuId);
  }

  @Override
  @GetMapping("/get")
  public WxMpMenu menuGet() throws WxErrorException {
    return this.wxService.getMenuService().menuGet();
  }

  @Override
  @GetMapping("/menuTryMatch/{userid}")
  public WxMenu menuTryMatch(@PathVariable String userid) throws WxErrorException {
    return this.wxService.getMenuService().menuTryMatch(userid);
  }

  @Override
  @GetMapping("/getSelfMenuInfo")
  public WxMpGetSelfMenuInfoResult getSelfMenuInfo() throws WxErrorException {
    return this.wxService.getMenuService().getSelfMenuInfo();
  }
}
