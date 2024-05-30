import {$post,$get} from '../utils/request.ts'
export const $Login=async(params:object)=>{
    let msg="登录失败"
    let r=await $post('user/login',params).catch(e=>{
        msg=e.response.data.msg;
        console.log("msgg",msg);
        
        return {'msg':msg};
    })
    if(r.code===200){
        return r.data
    }
    return {'msg':msg};
}

export const $register=async(params:object)=>{
    let msg=""
    await $post('user/register',params).catch(e=>{
        msg=e.response.data.msg;
        return msg;
    })
    return msg;
}

export const $getInfo=async(params:object)=>{
    let r=await $get('user/currentUser/getUserById',params).catch(e=>{
        return null
    })
    if(r.code===200){
        return r.data
    }
    return null
}
export const $confirmPassword=async(params:object)=>{
    let msg;
    let r=await $post('user/currentUser/confirmPassword',{},{params:params}).catch(e=>{
        msg=e.response.data.msg;
        return msg;
    })
    if(r.code===200) return "";
    msg=r.msg;
    return msg;
}
export const $updateEmail=async(params:object)=>{
    let msg="1";
    let r=await $post('user/currentUser/updateEmail',params).catch(e=>{
        msg=e.response.data.msg;
        return msg;
    })
    if(r.code===200) return "";
    return msg;
}
export const $updatePhone=async(params:object)=>{
    let msg="1";
    let r=await $post('user/currentUser/updatePhone',params).catch(e=>{
        msg=e.response.data.msg;
        return msg;
    })
    if(r.code===200) return "";
    msg=r.msg;
    return msg;
}
export const $updatePassword=async(params:object)=>{
    let msg="1";
    let r=await $post('user/currentUser/updatePassword',params).catch(e=>{
        console.log(e);
        
        msg=e.response.data.msg;
        return msg;
    })
    if(r.code===200) return "";
    msg=r.msg;
    return msg;
}
export const $sendVerifycode=async(params:object)=>{
    let msg="1"
    let res=await $get(`user/reset/${params}`,{}).catch(e=>{
        msg = e.response?.msg || "邮箱错误";
        return msg
    })
    if(res.code===200){
        return ""
    }
    return msg
}
export const $reset=async(params:object)=>{
    let msg="1";
    let r=await $post('user/reset',params).catch(e=>{
        msg=e.response.data.msg;
        return msg;
    })
    if(r.code===200) return "";
    return msg;
}
export const $uploadAvatar=async(params:object)=>{
    let msg="1"
    let r=await $post('files/currentUser/uploadAvatar',params,{
        headers:{'Content-type':'multipart/form-data'}
    }).catch(e=>{
        msg = e.response?.msg || "头像上传失败";
    })
    if(r.code===200){
        return {'url':r.data,'msg':"success"};
    }
    return {'url':"",'msg':msg};
}
export const $updateAvatar=async(params:object)=>{
    let msg="1"
    let res=await $post('user/currentUser/updateAvatar',{},{params:params}).catch(e=>{
        console.log(e);
        msg = e.response.data.msg;
    })    
    if(res.code===200){
        msg="success";
    }
    return msg;
}

export const $getUserCapInfo=async(params:object)=>{
    let r=await $get('cap/getUserCapInfo',params).catch(e=>{
        console.log(e);
        return {"used_cap": 0,
        "all_cap": 2048000}
    })
    if(r.code===200){
        return r.data
    }
    return {"used_cap": 0,
    "all_cap": 2048000}
}