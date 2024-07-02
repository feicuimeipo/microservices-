import {defineStore} from "pinia";

import {tokenUtils} from "@/stores/tokenUtil";
import type {SuccessData} from "@/packages/slide-verify/vertify-utils";
import authRequest from "@/utils/authRequest";

export enum LoginType{
    Account, //帐号和密码方式
    Mobile, //手机验证码方式
    QR //二维码
}

export interface AuthUser{
    userId: string;
    username:string;
    loginAccount:string,
    displayName:string;
    tenantId:string;
    authToken:string;
    loginTime:number;
    loginType: LoginType;
    expiration:number;
    rememberMe: boolean;
}

export interface AccountLogin{
    account:string;
    password:string;
    rememberMe: boolean;
    sliderVerityResult: SuccessData|null;
}

export interface MobileLogin{
    mobileNo:string|null;
    smsVerifyCode:string;
    agreePrivacyPolicy:boolean;
}



export const useAuthStore = defineStore("authStore",{
    state: () => {
        const currentUser= tokenUtils.getAuthUser();
        return {
            ...currentUser
        }
    },

    persist: {
        enabled: true,
        strategies: [
            { storage: sessionStorage, paths: ["userId","loginType","loginAccount","displayName","tenantId",]},
            { storage: localStorage, paths: ["authToken","loginTime","expiration"] },
        ],
    },

    getters: {
        isLogin: (state):boolean =>{
            const isCheckLogin =  (state.authToken!=null && state.authToken.length>0);
            return isCheckLogin;
        },
    },

    actions: {
        accountLogin (reqInfo:AccountLogin):Promise<AuthUser|null> {
            const url = "/auth/login";
            return new Promise((resolve, reject) => {
                authRequest.post(url, reqInfo).then((res)=>{
                    const ret = res.data ? res.data : res;
                    if (ret.code === 0 || ret.code === 200) {
                        this.$state = {...ret.data};
                        resolve(ret.data());
                    }else{
                        resolve(ret.msg);
                    }
                }).catch((err)=>{
                    console.log("登录失败："+err.message);
                    reject(err.message);
                })
            });
        },
        async mobileLogin (reqInfo:MobileLogin):Promise<boolean|null> {
            const url = "/auth/mobileLogin";

            return new Promise((resolve, reject) => {
                authRequest.post(url, reqInfo).then((res)=>{
                    const ret = res.data ? res.data : res;
                    if (ret.code === 0 || ret.code === 200) {
                        this.$state = {...ret.data};
                        resolve(ret.data());
                    }else{
                        reject(ret.msg);
                    }
                }).catch((err)=>{
                    console.log("登录失败："+err.message);
                    reject(err.message);
                })
            });
        },
        refreshToken():Promise<AuthUser|null> {
            const token = this.$state.authToken;
            const url = "/auth/refresh?token="+token;

            return new Promise((resolve, reject) => {
                authRequest.get(url).then((res) =>{
                    const ret = res.data ? res.data : res;
                    if (ret.code === 0 || ret.code === 200) {
                        this.$state = {...ret.data};
                        resolve(ret.data());
                    }else{
                        console.log("刷新token失败："+ret.msg);
                        resolve(null)
                        //reject(ret.msg);
                    }
                }).catch((err)=>{
                    console.log("刷新token失败："+err.message);
                    resolve(null)
                    //reject(err.message);
                })
            });
        },
    },
 });
