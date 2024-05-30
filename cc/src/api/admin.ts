import {$post,$get} from '../utils/request.ts'

//获取管理员信息
//export const $adminGetInfo = ({email,password}) => $post('user/login',{email,password})

//获取全部用户
export const $adminGetAllUser = (params:object) => $get('user/page/listUsers',params) 
//更新用户信息
export const $adminUpdateUser = (params:object) => $post('user/updateUser',params)
//通过id删除用户
export const $adminDeleteById = (params:object) => $post('user/deleteById',params)
//批量删除用户信息
export const $adminDeleteUserlist = (params:object) => $post('user/deleteUserlist',params)
//跟据昵称、部门(可选)查用户
export const $getUsersByName = (params:object) => $get('/user/page/getUsersByName',params)
//修改用户状态
export const $modifyUserStatus = (params:object) => $post('/user/modifyUserStatusById',params)