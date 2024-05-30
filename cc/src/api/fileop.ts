import {$post,$get,instance} from '../utils/request.ts'
import { baseURL_dev } from '../config/baseURL';
export const $upload=async(params:object)=>{
    console.log("upload",params);
    let r=await $post('files/upload/commonFile',params,{
        headers:{'Content-type':'multipart/form-data'}
    }).catch(e=>{
        console.log("upload",e);
        return false
    })
    console.log("upload_r",r);
    if(r.code===200){
        return true;
    }
    return false;
}
export const $checkfile=async(params:object)=>{
    let r=await $post('files/upload/checkfile',params,{
        headers:{'Content-type':'multipart/form-data'}
    }).catch(e=>{
        console.log("checkfile",e);
        return false;
    })
    if(r.code===200){
        return true;
    }
    return false;
}
export const $checkchunk=async(params:object)=>{
    let r=await $post('files/upload/checkchunk',{},{params:params}).catch(e=>{
        console.log("checkchunk_e",e);
        return false;
    })
    if(r.code===200){
        return r.data;
    }
    return false;
}
export const $uploadchunk=async(params:object)=>{
    let r=await $post('files/upload/uploadchunk',params,{
        headers:{'Content-type':'multipart/form-data'}
    }).catch(e=>{
        console.log("uploadchunk_e",e);
        return false;
    })
    if(r.code===200){
        return r.data;
    }
    return false;
}
export const $mergechunks=async(params:object)=>{
    let r=await $post('files/upload/mergechunks',{},{params:params}).catch(e=>{
        console.log("mergechunks_e",e);
        return false;
    })
    if(r.code===200){
        return true;
    }
    return false;
}
export const $collect = async (ids: number[]) => {
    let msg="收藏失败"
    let r = await $post('userFiles/collect',ids).catch(e => {
        console.log("collect", e);
        msg = e.response?.msg || "收藏失败";
        return msg;
    });
    if (r.code === 200) {
        msg="success"
    }
    return msg;
}
export const $delete = async (ids: number[]) => {
    console.log("ids",ids);
    
    let msg="删除失败"
    let r = await $post('userFiles/delete',ids).catch(e => {
        console.log("delete", e);
        msg = e.response?.msg || "删除失败";
        return msg;
    });
    if (r.code === 200) {
        msg="success"
    }
    return msg;
}
export const $rename = async (params: object) => {
    console.log("params",params);
    
    let msg="重命名失败"
    let r = await $post('userFiles/rename',{},{params:params}).catch(e => {
        console.log("rename", e);
        msg = e.response?.msg || "重命名失败";
        return msg;
    });
    console.log("r",r);
    
    if (r.code === 200) {
        msg="success"
    }else{
        msg=r.msg;
    }
    return msg;
}
export const $createDir = async (params:object) => {
    let msg="新建文件夹失败"
    let r = await $post('userFiles/createDir',params).catch(e => {
        console.log("mkdir_e", e);
        msg = e.response.data?.msg || "新建文件夹失败";
        return msg;
    });
    if (r.code === 200) {
        return "新建文件夹成功";
    }
    return r+",新建文件夹失败";
}
export const $share = async (params:object) => {
    console.log("share_p",params);
    
    let msg="分享失败"
    let r = await $post('userFiles/share',params).catch(e => {
        console.log("share_e", e);
        msg = e.response.data?.msg || "X分享失败";
        return msg;
    });
    if (r.code === 200) {
        return {token:r.data,msg:'success'};
    }
    return {msg:msg};
}
export const $getshare = async (params:object) => {
    let r = await $get('userFiles/share',params).catch(e => {
        console.log("getshare_e", e);
        return null;
    });
    if (r.code === 200) {
        return r.data;
    }
    return null;
}
export const $download = async (params:object) => {
    console.log("download_p",params);
    
    let r = await instance.get(baseURL_dev+`files/download/${params}`,{responseType: 'blob'}).catch(e => {
        console.log("download_e", e);
        return null;
    });
    if(r===null) return null;
    return r.data;
}

export const $move = async (params:object) => {
    let r = await $post('userFiles/move',{},{params:params}).catch(e => {
        console.log("move_e", e);
        return e.response.data.msg;
    });
    if (r.code === 200) {
        return "";
    }
    return r
}
export const $share_status = async (params:object) => {
    let r = await $post('userFiles/share_status',{},{params:params}).catch(e => {
        console.log("share_status_e", e);
        return false;
    });
    if (r.code === 200) {
        return true;
    }
    return false
}
export const $share_store = async (params:object) => {
    let r = await $post('userFiles/store',params).catch(e => {
        console.log("share_store_e", e);
        return false;
    });
    if (r.code === 200) {
        return true;
    }
    return false
}
export const $search = async (params:object) => {
    console.log("search_p",params);
    let r = await $get('userFiles/search',params).catch(e => {
        console.log("getshare_e", e);
        return null;
    });
    console.log("saarch_r",r)
    return r;
}