import authRequest from "@/utils/authRequest";
import {getUrlParam} from "@/utils";

export interface AccountRegister{
    account:string,
    password:string,
    rePassword:string,
    mobileNo: string,
    smsVerifyCode:string,
    agreePrivacyPolicy: boolean
}

export interface MobileRegister{
    mobileNo: string,
    smsVerifyCode:string,
    agreePrivacyPolicy: boolean
}

export const authApi = {
    accountRegister(data: AccountRegister):Promise<boolean|null>{
        //todo:
       return  new Promise((resolve,reject) => {
            resolve(true);
        })
    },
    mobileRegister(data: MobileRegister):Promise<boolean|null>{
        //todo:
        return  new Promise((resolve,reject) => {
            resolve(true);
        })
    },
    getCallbackUrl (domain:string|null):string|null {
        if (domain==null){
            return null;
        }
        authRequest.get("/sso/client/callbackUrl?domain="+domain).then(res => {
            const data = res.data;
            const callback = "http://pi.domain/sso/login";
            return callback;
        }).catch(err =>{
            return null;
        });
        return null;
    },

    getReturnUr():string|null {
        return getUrlParam("returnUrl");
    },

    getOriginDomain():string|null {
        const url = getUrlParam("returnUrl");
        if (url!=null) {
            const urls = url.split("/");
            if (urls[2]) {
                const domain = urls[2];
                return domain;
            }
        }
        return null;
    }

}
