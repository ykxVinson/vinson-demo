<template>
    <a-row>
        <a-col :span="24">
            <banner/>
        </a-col>
    </a-row>
    <a-row>
        <a-col :span="24">
            <hotel />
        </a-col>
    </a-row>
</template>

<script>
import Banner from "@/components/Banner";
import Hotel from "@/components/Hotel";

export default {
    name: "HomeView",
    components: {
        Banner,
        Hotel
    },
    setup(){

    }
}
</script>
<style lang="scss" scoped>
  .ant-layout{
    a-layout-content{
      background: white;
      height: auto;
    }
    a-layout-footer{
      background: lightblue;
      height: 100px;
    }

  }
</style>
