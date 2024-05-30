
import { th } from 'element-plus/es/locales.mjs';
import {defineStore} from 'pinia'

export default defineStore('user',{
    state(){
        return{
            user:{
                admin: false,
                avatar: "",
                id: "",
                token: "",
                userName: "",
                email:"",
                phone:"",
                groupName:"",
                shareToken:"",
            }
        }
    },
    persist:{
        enabled:true,
        storage:sessionStorage,
    },
    actions:{
        setShareToken(t:string){
            this.user.shareToken=t;
        },
        setAvatar(avatar:string){
            this.user.avatar=avatar;
        },
        setEmail(email:string){
            this.user.email=email;
        },
        setPhone(phone:string){
            this.user.phone=phone;
        },
        setToken(token:string){
            this.user.token=token;
        },
        setUserInfo(r:object){
            this.user.avatar=r.avatar
            this.user.id=r.id
            this.user.userName=r.userName
            this.user.email=r.email
            this.user.phone=r.phone
            this.user.groupName=r.groupName
        },
        setUserLogin(user:object){
            this.user.admin=user.admin
            this.user.avatar=user.avatar
            this.user.id=user.id
            this.user.token=user.token
            this.user.userName=user.userName
        },
        clearUser(){
            this.user={
                admin: false,
                avatar: "",
                id: "",
                token: "",
                userName: "",
                email:"",
                phone:"",
                groupName:"",
            }
        },
    }
})