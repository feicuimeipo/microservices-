import Vue from "vue";
import Router from "vue-router";
import store from "@/store";
const HtTabsComponent = () => import("@/components/common/HtTabsComponent.vue");
const TemplateComponent = () =>
  import("@/components/common/TemplateComponent.vue");

Vue.use(Router);

var router = new Router({
  base: process.env.BASE_URL,
  mode: "history",
  routes: [
    {
      path: "/login/:tenant",
      name: "tenantLogin",
      props: true,
      component: () => import("@/views/Login.vue"),
      meta: {
        single: true,
        anonymous: true
      }
    },
    {
      path: "/login",
      name: "login",
      props: true,
      component: () => import("@/views/Login.vue"),
      meta: {
        single: true,
        anonymous: true
      }
    },
    {
      path: "/resetPwdView",
      name: "resetPwdView",
      props: true,
      component: () =>
        import("@/views/ResetPwdView.vue"),
      meta: {
        single: true,
        anonymous: true
      }
    },
    {
      path: "/",
      redirect: {
        name: "home"
      }
    },
    {
      path: "/href/template/:alias",
      name: "href",
      component: () => import("@/components/common/HtTabsComponentHref.vue")
    },
    {
      path: "/home",
      name: "home",
      component: () => import("@/views/common/Home.vue"),
      meta: {
        isHome: true
      }
    },
    {
      path: "/form",
      name: "form",
      component: HtTabsComponent
    },
    {
      path: "/report",
      name: "report",
      component: HtTabsComponent
    },
    {
      path: "/meta",
      name: "meta",
      component: HtTabsComponent
    },
    {
      path: "/i18n",
      name: "i18n",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/reportLine",
      name: "reportLine",
      component: HtTabsComponent
    },
    {
      path: "/roleManager",
      name: "roleManager",
      component: HtTabsComponent
    },
    {
      path: "/menuManagerRouter",
      name: "menuManagerRouter",
      component: HtTabsComponent
    },
    {
      path: "/ucManager",
      name: "ucManager",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/tenantType",
      name: "tenantType",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/tenant",
      name: "tenant",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/tenantMailServer",
      name: "tenantMailServer",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/tenantManager",
      name: "tenantManager",
      component: HtTabsComponent
    },
    {
      path: "/tenantInfoManager",
      name: "tenantInfoManager",
      component: HtTabsComponent
    },
    {
      path: "/instance",
      name: "instance",
      component: HtTabsComponent
    },
    {
      path: "/customDialogList",
      name: "customDialogList",
      component: HtTabsComponent
    },
    {
      path: "/customQueryList",
      name: "customQueryList",
      component: HtTabsComponent
    },
    {
      path: "/fileList",
      name: "fileList",
      component: HtTabsComponent
    },
    {
      path: "/flowDesign",
      name: "flowDesign",
      component: HtTabsComponent
    },
    {
      path: "/organization",
      name: "organization",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/businessObj",
      name: "businessObj",
      component: HtTabsComponent
    },
    {
      path: "/sysTypeManager",
      name: "sysTypeManager",
      component: HtTabsComponent,
      meta: {
        isReloadPage: true
      }
    },
    {
      path: "/schedulerManager",
      name: "schedulerManager",
      component: HtTabsComponent
    },
    {
      path: "/customChartManager",
      name: "customChartManager",
      component: HtTabsComponent
    },
    {
      path: "/reportManager",
      name: "reportManager",
      component: HtTabsComponent
    },
    {
      path: "/heiOrganization",
      name: "heiOrganization",
      component: HtTabsComponent
    },
    {
      path: "/reportManager/viewList",
      name: "viewList",
      component: () => import("@/components/form/customView/ViewList.vue"),
      meta: {
        isHome: true
      }
    },
    {
      path: "/reportManager/flowChartEdit",
      name: "flowChartEdit",
      component: () => import("@/components/form/chart/FlowChartEdit.vue"),
      meta: {
        isHome: true
      }
    },
    {
      path: "/agent",
      name: "agent",
      component: HtTabsComponent
    },
    {
      path: "/demo",
      name: "demo",
      component: HtTabsComponent,
      meta: {
        anonymous: true
      }
    },
    {
      path: "/formTemplateList",
      name: "formTemplateList",
      component: HtTabsComponent
    },
    {
      path: "/dataTemplateList",
      name: "dataTemplateList",
      component: HtTabsComponent
    },
    {
      path: "/executeJobList/:jobName",
      name: "executeJobList",
      component: HtTabsComponent,
      meta: {
        keepAlive: true //页面跳转重新渲染 默认不渲染
      }
    },
    {
      path: "/schedulerTriggerList/:jobName",
      name: "schedulerTriggerList",
      component: HtTabsComponent,
      meta: {
        keepAlive: true
      }
    },
    {
      path: "/portal",
      name: "portal",
      component: HtTabsComponent
    },
    {
      path: "/sysPropertiesManager",
      name: "sysPropertiesManager",
      component: HtTabsComponent
    },
    {
      path: "/sysSetting",
      name: "sysSetting",
      component: HtTabsComponent
    },
    {
      path: "/sysExternalUniteList",
      name: "sysExternalUniteList",
      component: () =>
        import("@/views/system/sysExternalUnite/SysExternalUnite.vue")
    },
    {
      path: "/sysModuleList",
      name: "sysModuleList",
      component: () => import("@/views/system/generator/sysModuleList.vue")
    },

    {
      path: "/help",
      name: "help",
      component: HtTabsComponent
    },
    {
      path: "/systemLog",
      name: "systemLog",
      component: HtTabsComponent
    },
    {
      path: "/templateMenu/:alias",
      name: "templateMenu",
      component: TemplateComponent,
      meta: {
        keepAlive: true //页面跳转重新渲染 默认不渲染
      }
    },
    {
      path: "/pwdStrategy",
      name: "pwdStrategy",
      component: HtTabsComponent
    },
    {
      path: "/codeGenerator",
      name: "codeGenerator",
      component: HtTabsComponent
    }
  ]
});

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => !record.meta.anonymous)) {
    let ticket = to.query.token;
    if (window.ssoConfig.mode == "cas") {
      ticket = to.query.ticket;
    }

    store
      .dispatch("login/validAndCompletedCurrent", ticket)
      .then(() => {
        // 初始化菜单
        store.dispatch("menu/actionMenus").then(() => {});
        next();
      })
      .catch(() => {
        store.dispatch("login/logoutAndCleanUp").then(() => {
          if (window.ssoConfig.mode == "cas" && !ticket) {
            let service = window.location.href;
            localStorage.setItem("service", service);
            window.location.href = window.ssoConfig.url + "?service=" + service;
          }

          if (!window.ssoConfig.mode || window.ssoConfig.mode == "jwt") {
            let account = "";
            if (store.state.login.currentUser) {
              account = store.state.login.currentUser.account;
            }
            next({
              path: localStorage.getItem(account + "loginRoutePath") || "/login",
              query: {
                redirect: to.fullPath
              }
            });
          }
        });
      });
  } else {
    next();
  }
});

export default router;
