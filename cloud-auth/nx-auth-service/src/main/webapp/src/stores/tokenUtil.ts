import Cookies from 'js-cookie'
import {ElMessage} from "element-plus/es";
import type {AuthUser} from "@/stores/auth-store";
import authRequest from "@/utils/authRequest";

const TOKEN_KEY = 'authToken';
const Days = 7;


const getToken = ():string|null =>{
    const token = Cookies.get(TOKEN_KEY)
    if (token) return token
    return null;
}

const setToken = (value:string,expiration:number) =>{
    if (expiration>0){
        const exp = new Date(new Date().getTime() + expiration);
        Cookies.set(TOKEN_KEY,value,{ expires: exp})
    }else{
        Cookies.set(TOKEN_KEY,value);
    };
}

const remoteToken = () =>{
  Cookies.remove(TOKEN_KEY)
}

const getAuthUser = ():AuthUser|null =>{
    const token = tokenUtils.getToken()
    let url = "";
    if (token != undefined && token!=null && token.length>0){
        //得到AuthToken
        url = "/auth/fetchAuthInfo?token="+token;
    }else{
        url = "/auth/checkLoginStatus";
    }
    authRequest.get(url).then((res) => {
        if (res){
            const data = res.data.data.code?res.data.data:res.data;
            if (data.code===0 || data==200){
                setToken(data.authToken,data.expires);
                return data;
            }else{
                ElMessage.error(data.msg);
                return null;
            }
        }
        return null;
    }).catch((err)=>{
        console.error(err)
        return null;
    });
    return null;
}



export const tokenUtils = {
    getToken,setToken,remoteToken,getAuthUser
}

