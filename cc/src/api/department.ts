import {$post,$get} from '../utils/request.ts'

//分页查询部门
export const $getGroupsByPage = (pageInfo:object) => $get('groups/page/getGroupsByPage',pageInfo)
//查询所有部门
export const $getAllGroups = () => $get('groups/getAllGroups')
//新添部门
export const $addGroup = (params:object) => $post('groups/addGroup',params)
//根据部门查用户
export const $getUsersByGroup = (params:object) => $post('groups/page/getUsersByGroup',params)
//删除某个部门
export const $deleteGroupByName = (params:object) => $post('groups/deleteGroupByName',params)
//批量删除部门
export const $deleteUserlist = (params:object) => $post('groups/deleteGrouplist',params)
