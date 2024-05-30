import {$get} from '../utils/request.ts'
//返回全部文件列表
export const $queryAllfile=async(params:object)=>{
    let ret =await $get('/userFiles/files',params)
    return ret
}

//返回图片文件列表
export const $queryPicturefile=async(params:object)=>{
    let ret =await $get('',params)
    return ret
}

//返回文档文件列表
export const $queryDocumentfile=async(params:object)=>{
    let ret =await $get('',params)
    return ret
}

//返回音乐文件列表
export const $queryMusicfile=async(params:object)=>{
    let ret =await $get('',params)
    return ret
}

//返回视频文件列表
export const $queryVideofile=async(params:object)=>{
    let ret =await $get('',params)
    return ret
}

//返回其他文件列表
export const $queryLesefile=async(params:object)=>{
    let ret =await $get('',params)
    return ret
}

//根据类型查询文件
export const $queryTypefile=async(params:object)=>{
    let ret =await $get('/userFiles/types',params)
    return ret
}

//预览文件
export const $queryPreviewfile=async(params:any)=>{
    let ret =await $get('/files/preview/'+params)

    return ret
}

//部门文件
export const $queryAllDepartmentfile=async(params:object)=>{
    let ret =await $get('/userFiles/groups',params)
    return ret
}

//按指定路径查询部门文件
export const $queryDepartmentfile=async(params:object)=>{
    let ret =await $get('/userFiles/groups/path',params)
    return ret
}
