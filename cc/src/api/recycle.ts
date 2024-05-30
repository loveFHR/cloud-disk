import {$get,$post} from '../utils/request'
//显示回收站文件
export const  $queryRecycleFile=async (params:any)=>{
 let ret=await $get('userFiles/recycleBin/page',params)
 return ret;
 }
 //恢复文件
 export const  $queryRecycleOneFile=async (params:any)=>{
    let ret=await $post('userFiles/recycleBin/restore',params)
    return ret;
    }
//删除文件
export const $queryDelecteOneFile=async (params:any)=>{
    let ret=await $post('userFiles/recycleBin/completelyDelete',params)
    return ret;
    }
    
//清空回收站
export const $queryDelecteAllOneFile=async ()=>{
    let ret=await $post('userFiles/recycleBin/clearAll')
    return ret;
  }


