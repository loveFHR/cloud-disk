import { defineStore } from 'pinia';

export default defineStore('movefile', {
  state() {
    return {
      movefile: {
        files:[],
        is_share:false,
      }
    };
  },
  persist: {
    enabled: true,
    storage: sessionStorage,
  },
  actions: {
    setIsShare(t:Boolean){
      this.movefile.is_share=t;
    },
    setMovefile(fileid) {
      this.movefile.files.push(fileid);
    },
    clearMovefile() {
      this.movefile.files = [];
      this.movefile.is_share=false;
    },
  },
});