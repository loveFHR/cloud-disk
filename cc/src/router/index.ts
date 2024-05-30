import {createRouter,createWebHistory} from 'vue-router'
import useUser from '../store/user.ts'
import {$getInfo} from '../api/user'
const router=createRouter({
    history:createWebHistory(),
    routes:[
        {name:"login",path:"/login",component:()=>import("../views/user/login.vue")},
        {name:"register",path:"/register",component:()=>import("../views/user/register.vue")},
        {name:"reset",path:"/reset",component:()=>import("../views/user/reset.vue")},
        {name:"secret",path:"/secret",component:()=>import("../views/user/secret.vue")},
        {
            name:'home',
            path:'/',
            component:()=>import("../views/file/home.vue"),
        },
        {name:"shareLook",path:"/share",component:()=>import("../views/file/shareLook.vue")},
        {name:"account",path:"/account",component:()=>import("../views/user/account.vue")},
        {
            name:'usermanage',
            path:'/admin/user/manage',
            component:()=>import('../views/admin/UserManage.vue')
        },
        {
            name:'departmentmanage',
            path:'/admin/department/manage',
            component:()=>import('../views/admin/DepartmentManage.vue')
        },
        {name:"picture",path:"/picture",component:()=>import("../views/file/picture.vue")},
        {name:"recycle",path:"/recycle",component:()=>import("../views/file/recycle.vue")},
        {name:"collect",path:"/collect",component:()=>import("../views/file/collect.vue")},
        { name:"video",path:"/video",component:()=>import("../views/file/video.vue")},
        { name:"music",path:"/music",component:()=>import("../views/file/music.vue")},
        { name:"document",path:"/document",component:()=>import("../views/file/document.vue")}, 
        { name:"department",path:"/department",component:()=>import("../views/file/department.vue")}
    ]
})
router.beforeEach(async (to, from, next) => {
    const userStore = useUser();
    if(userStore.user.id){
        next();
    }else{
        let t = localStorage.getItem("token");
        if (!t) {
            if (to.name === "login" || to.name === "register" || to.name === "reset"||to.name === "secret") {
              next();
            } else {
              next({ name: "login" });
            }
          } else {
            userStore.setToken(t);
            let r = await $getInfo({});
            if(r){
              userStore.setUserInfo(r);
            }
            if (to.meta.requestAuth && !userStore.user.id) {
              next({ name: "login" });
            } else {
              next();
            }
          }
    }
  });
export default router