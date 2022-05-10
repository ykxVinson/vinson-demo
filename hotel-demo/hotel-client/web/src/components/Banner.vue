<template>
        <a-row class="banner" type="flex" align="middle">
            <a-col class="logo" flex="200px">
                <a-row type="flex">
                    <a-col flex="50px">
                        <img src="../assets/heima.png" alt="1"/>
                    </a-col>
                    <a-col flex="auto">
                         <span>
                             <div class="logo-ch">黑马旅游</div>
                             <div class="logo-en">www.itheima.com</div>
                        </span>
                    </a-col>
                </a-row>


            </a-col>
            <a-col flex="auto"></a-col>
            <a-col flex="100px">
                <a-switch v-model:checked="testChecked" checked-children="测试" un-checked-children="正式"/>
            </a-col>
        </a-row>

</template>

<script>
    import {defineComponent, ref} from "vue";

    export default defineComponent({
        name: "BannerView",
        setup() {
            const testChecked = ref(true);

            return {
                testChecked
            }
        }
    })
</script>

<style scoped lang="scss">
  .banner {
    height: 55px;
    .logo {
      img {
        width: 40px;
        height: 40px;
      }
      .logo-ch{
        line-height: 26px;
        font-size: 14px;
        color: #333;
        font-weight: bold;
        font-family: "Helvetica Neue", "Arial", "PingFang SC", "Microsoft Yahei", "SimSun", sans-serif;
      }
      .logo-en{
        line-height: 14px;
        font-size: 8px;
      }
    }
  }
</style>