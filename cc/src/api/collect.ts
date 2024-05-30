import {$get} from '../utils/request.ts'
//加载收藏文件列表
export const $QueryCollectfile =async(params:object)=>{
    let ret =await $get('userFiles/page/collectFile',params)
    return ret
}
//取消收藏
export const  $cancelCollectfile =async(params:object)=>{
    let ret =await $get('userFiles/cancelCollection',params)
    return ret;
}
  