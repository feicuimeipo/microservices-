import authRequest from "@/utils/authRequest";

export class BlockPosition{
    blockX: number;
    blockY: number;
    constructor(x:number,y:number) {
        this.blockX = x;
        this.blockY = y;
    }
};

export enum VerifyType{
    default,
    accountLogin=1,
    mobileLogin,
    accountRegister,
    mobileRegister,
};

export class SuccessData{
    timestamp: number;
    left: number;
    accuracy: number;
    verifyType: VerifyType;
    constructor(timestamp: number,left: number,accuracy: number,verifyType: VerifyType) {
        this.timestamp = timestamp;
        this.left = left;
        this.accuracy = accuracy;
        this.verifyType = verifyType;
    }
};

const getDefaultValue = () => {
    return {
         l: <number>42,
         r: <number>10,
         w: <number>330,
         h:  <number>155,
         accuracy:<number>5
    }
}


const getRemoteBlockPosition = (l:number,r:number,w:number,h:number,verifyType: VerifyType|null):Promise<BlockPosition|null> => {
    const param = {L:l,r:r,w:w,h:h,verify:verifyType};
    return new Promise((resolve, reject)=>{
       authRequest.post("/auth/verifyInfo",param).then((res)=>{
            if (res.data){
                const data = res.data.data?res.data.data:res.data;
                const block = new BlockPosition(data.blockX,data.blockY);
                console.log("1.getRemoteBlockPosition：param:"+JSON.stringify(param)+",ret.data="+JSON.stringify(data)+",block="+block);
                resolve(block);
            }else{
                resolve(null);
            }
        }).catch((err)=>{
            console.log(err);
            resolve(null);
        })
    });
};


const getRandomNumberByRange = (start: number, end: number): number => {
    return Math.round(Math.random() * (end - start) + start);
}


const sendSMS = (verifyResult:SuccessData,mobile:string):Promise<boolean> => {
    const param = {verify:verifyResult,mobile:mobile};
    return new Promise((resolve, reject)=>{
        authRequest.post("/auth/sendSMS",param).then((res)=>{
            const ret = res.data;
            if ((ret.code===200 || ret.code===0)){
                resolve(true);
            }else{
                resolve(false);
            }
        }).catch((err)=>{
            console.log(err);
            resolve(false);
        })
    });
}

export const vertifyUtils = {
    sendSMS,getRandomNumberByRange,getRemoteBlockPosition,getDefaultValue
}
