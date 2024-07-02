<template>
  <el-avatar :src="userPhoto">
    <img :src="userPhoto" />
  </el-avatar>
</template>

<script>
export default {
  props: ["user"],
  data() {
    return {
      userPhoto: `${require("@/assets/svg/defaultPhoto.svg")}`,
      userPhotoUrl: null,
      defaultUserPhoto:  `${require("@/assets/svg/defaultPhoto.svg")}`,
    };
  },
  watch: {
    user: {
      handler: function(val, oldVal) {
        if (val && val.user && val.user.photo && val !== oldVal && val.user.photo.indexOf('/defaultPhoto.')>-1) {
          this.$store.dispatch("menu/downloadImg",val.user.photo).then(res => {
            if (res != "") {
              this.userPhoto =  res;
            }else{
              this.userPhoto = this.defaultUserPhoto;
            }
          });
        } else {
          this.userPhoto = this.defaultUserPhoto;
        };
        console.log("userPhoto=",this.userPhoto)
      },
      deep: true
    }
  }
};
</script>
