<template>
    <a-row class="search">
        <a-col :span="24"  align="middle">
            <a-auto-complete
                    v-model:value="searchParams.key"
                    :dropdown-match-select-width="452"
                    :options="searchResults"
                    style="width: 500px"
                    @select="handleSelect"
                    @search="handleSearchTextChanged"
            >
                <template #option="item">
                    <div style="display: flex; justify-content: space-between">
                        {{item.value}}
                    </div>
                </template>
                <a-input-search size="large" placeholder="请输入酒店信息" enter-button @search="handleSearch"></a-input-search>
            </a-auto-complete>
        </a-col>
    </a-row>
    <a-row class="selects">
        <a-col :span="24">
            <div class="selected-ops">
                <div class="open">全部结果：</div>
                <div class="selected-op" v-for="(v,k) in filterObject" :key="k" @click="handleDeleteFilter(k)">
                    {{ filterNames[k]}}：<span>{{ v }} <span class='close'>×</span></span>
                </div>
            </div>
        </a-col>
    </a-row>
    <a-row class="filter">
        <a-col :span="24" >
            <div class="filter-list">
                <div v-for="(v,k) in remainFilter" :key="k">
                    <div class="filter-box">
                        <div class="f-key"><strong>{{ filterNames[k] }}</strong></div>
                        <div class="column-divider"></div>
                        <div class="f-items">
                            <div @click="handleClickFilter(k,o)" class="f-item" v-for="(o,j) in v" :key="j">
                                <a href="javascript:void(0)">{{ o }}</a>
                            </div>
                        </div>
                    </div>
                    <div class="row-divider"></div>
                </div>
                <div class="filter-box">
                    <div class="f-key"><strong>{{ filterNames["price"] }}</strong></div>
                    <div class="column-divider"></div>
                    <div class="f-items">
                        <div class="f-item" @click="handleClickFilter('price','0-100')"><a href="javascript:void(0)">100元以下</a></div>
                        <div class="f-item" @click="handleClickFilter('price','100-300')"><a href="javascript:void(0)">100-300元</a></div>
                        <div class="f-item" @click="handleClickFilter('price','300-600')"><a href="javascript:void(0)">300-600元</a></div>
                        <div class="f-item" @click="handleClickFilter('price','600-1500')"><a href="javascript:void(0)">600-1500元</a></div>
                        <div class="f-item" @click="handleClickFilter('price','1500-0')"><a href="javascript:void(0)">1500元以上</a></div>
                    </div>
                </div>
            </div>

        </a-col>
    </a-row>
    <a-row class="hotels">
        <a-col :span="24">
            <!-- 排序 -->
            <div class="top-ban">
                <!--排序条件-->
                <div class="sort-items">
                    <div class="sort-item">
                        <a href="javascript:void(0)">默认</a>|
                    </div>
                    <div class="sort-item">
                        <a href="javascript:void(0)">评价</a>|
                    </div>
                    <div class="sort-item">
                        <a href="javascript:void(0)">价格</a>|
                    </div>
                </div>
                <!--分页条-->
                <div class="top-pagination">
                    <span>共 <i style="color: #222;">{{ total }}</i> 家酒店</span>
                    <span><i style="color: red;">{{ searchParams.page }}</i>/{{ totalPage }}</span>
                    <a class="btn-arrow" href="#" style="display: inline-block" @click="handlePrePage">&lt;</a>
                    <a class="btn-arrow" href="#" style="display: inline-block" @click="handleNextPage">&gt;</a>
                </div>
            </div>
            <div class="row-divider" style="margin-bottom: 5px; width: 100%"></div>
            <!--酒店列表-->
            <div style="display: flex; justify-content: space-between;">

                <div class="hotel-list">
                    <div class="hotel-box" v-for="(hotel,k) in hotels" :key="k"
                         style="display: flex;justify-content: space-between;">
                        <div class="ad-mark" v-if="hotel.isAD">
                            <img src="../assets/ad.png" width="25"/>
                        </div>
                        <div style="width: 0;"></div>
                        <div class="picture" style="width: 25%">
                            <img :src="hotel.pic">
                        </div>
                        <div class="hotel-info">
                            <div class="hotel-name">
                                {{ hotel.name }}
                            </div>
                            <div class="star-name"> {{ hotel.starName }}</div>
                            <div class="address">
                                位于 <span style="color: #BC8516;">{{ hotel.business }}</span> 周边，{{ hotel.address }}
                            </div>
                            <div class="order"> 1分钟前有人预订了该酒店</div>
                            <div class="address">距离您 {{hotel.distance.toFixed(2)}} km</div>
                        </div>
                        <div style="text-align: left; width: 15%;">
                            <div>
                                <b style="color: #f60;">￥</b> <span id='hotel-price'>{{ hotel.price }}</span> <span
                                    style="font-size: 0.2em; color: #999;">起</span>
                            </div>
                            <div class='btn'>立即预定</div>
                            <div>
                                <span class="hotel-score">{{hotel.score / 10}}分</span> /<span>5分</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="map-box">
                    <div class="map-head">地图预览</div>
                    <div class="amap" id="container"></div>
                    <div class="map-geo" @click="handleGetLoc">
                        <img src="https://a.amap.com/jsapi/static/image/plugin/waite.png" v-show="loading">
                    </div>
                </div>
            </div>
        </a-col>
    </a-row>
</template>

<script type="text/ecmascript-6" lang="js">
    import {ref, computed, watch, onMounted, reactive} from "vue";
    import { getHotelsByConditionsAjax, getSuggestByConditionsAjax, getFiltersByConditionsAjax } from "@/api/hotel";
    import AMap from 'AMap' // 引入高德地图

    export default {
        name: "HotelView",
        setup(){
            const filterNames =ref({
                brand: '品牌',
                city: '城市',
                starName: '星级',
                price: '价格'
            });
            const testFilterData=ref({
                city: [],
                starName: [],
                brand: []
            })
            const filterObject = ref({});
            const hotels =  ref([]);
            const total = ref(0);
            const totalPage = ref(0);
            const loading = ref(false);
            const searchResults = ref([])
            const searchParams = reactive({
                key: '',
                page: 1,
                size: 10,
                sortBy: 'default',
                location: 32.0+","+120.0
            });
            let map = null;
            let geolocation = null;

            const remainFilter = computed(() => {
                let obj = {};
                Object.keys(testFilterData.value).forEach(key =>{
                    if(!Object.keys(filterObject.value).includes(key)){
                        obj[key] = testFilterData.value[key];
                    }
                })
                return obj;
            })

            watch(() => searchParams.page, () => {
                handleSearch();
            })
            watch(() => filterObject, () => {
                handleSearch();
            },{
                deep: true
            })

            const handleClickFilter= (key, option) => {
                filterObject.value[key] = option;
                if(key == "price"){
                    const pArr = option.split('-');
                    const pMin = parseInt(pArr[0]);
                    const pMax = parseInt(pArr[1]);
                    searchParams['minPrice'] = pMin;
                    searchParams['maxPrice'] = pMax===0?99999:pMax;
                }else{
                    searchParams[key] = option;
                }
                console.log(searchParams)
            }

            const handleDeleteFilter = (key) => {
                console.log(key)
                delete filterObject.value[key];
                if(key == "price"){
                    delete searchParams['minPrice'];
                    delete searchParams['maxPrice'];
                }else{
                    delete searchParams[key];
                }
                console.log(searchParams)
            }

            const handleSearch = () => {
                hotels.value = [];
                getHotelsByConditionsAjax(searchParams).then(res => {
                   console.log(res);
                   hotels.value = res.data.hotels;
                   total.value = res.data.total;
                   totalPage.value = Math.floor((total.value + searchParams.size - 1) / searchParams.size);
                   console.log(searchParams.size);
                });
                getFiltersByConditionsAjax(searchParams).then(res => {
                    testFilterData.value.brand = res.data["品牌"];
                    testFilterData.value.city = res.data["城市"];
                    testFilterData.value.starName = res.data["星级"];
                })
            }

            const handlePrePage = () => {
                if (searchParams.page > 1) {
                    searchParams.page--;
                }
            }
            const handleNextPage =() => {
                if(searchParams.page < totalPage.value){
                    searchParams.page ++;
                }
            }

            const handleGetLoc = () => {
                loading.value = true;
                geolocation.getCurrentPosition((status, result) => {
                    loading.value = false;
                    console.log(status, result);
                    if(status === 'complete'){
                        addMarker('//a.amap.com/jsapi/static/image/plugin/point.png', result.position.lng,result.position.lat);
                        const loc = result.position.lat+','+ result.position.lng;
                        setMapCenter(loc);
                        searchParams['location'] = loc;
                        handleSearch();
                    }else{
                        console.log("error", status);
                    }
                });
            }

            const handleSearchTextChanged =() =>{
                getSuggestByConditionsAjax(searchParams.key).then(res => {
                    searchResults.value = res.data;
                });
            }

            const handleSelect =value=>{
                console.log(value)
            }

            const addMarker = (iconUrl, lng, lat) => {
                // 创建 AMap.Icon 实例：
                console.log(iconUrl)
                let icon = new AMap.Icon({
                    size: new AMap.Size(25, 25),    // 图标尺寸
                    image: iconUrl,  // Icon的图像
                    imageOffset: new AMap.Pixel(0, 0),  // 图像相对展示区域的偏移量，适于雪碧图等
                    imageSize: new AMap.Size(25, 25)   // 根据所设置的大小拉伸或压缩图片
                });

                // 将 Icon 实例添加到 marker 上:
                let marker = new AMap.Marker({
                    position: new AMap.LngLat(lng, lat),
                    offset: new AMap.Pixel(0, 0),
                    icon: icon, // 添加 Icon 实例
                    title: '北京',
                    zoom: 13,
                    anchor:"center"
                });
                map.add(marker);
            }
            const location = (loc) => {
                console.log(loc);
                let arr = loc.split(",");
                let json = '[' + arr[1] + ', ' + arr[0] + ']'
                console.log(json);
                return JSON.parse(json);
            }
            const setMapCenter = (loc) => {
                map.setCenter(location(loc));
            }

            const initMap = () => {
                map = new AMap.Map('container', {
                    resizeEnable: true,
                    zoom: 11, //初始地图级别
                    center: [120.0, 32.0], //初始地图中心点
                    viewMode:'3D'//使用3D视图
                });
                console.log(map);
                // 初始化定位系统
                AMap.plugin('AMap.Geolocation', () => {
                    geolocation = new AMap.Geolocation({
                        position: 'RT',    //定位按钮的停靠位置
                        timeout: 10000,
                        buttonOffset: new AMap.Pixel(10, 20),//定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
                        zoomToAccuracy: true,   //定位成功后是否自动调整地图视野到定位点
                    });
                    console.log(geolocation);
                });
            }

            onMounted(() => {
                handleSearch();
                initMap();
            });

            return{
                searchResults,
                loading,
                total,
                totalPage,
                searchParams,
                hotels,
                filterNames,
                testFilterData,
                remainFilter,
                filterObject,
                handleClickFilter,
                handleDeleteFilter,
                handleSearch,
                handlePrePage,
                handleNextPage,
                handleGetLoc,
                handleSearchTextChanged,
                handleSelect
            }
        }
    }
</script>

<style scoped lang="scss">
  a {
    text-decoration: none;
    color: #999;
  }
.search{
  .search-button{
    width: 250px;
  }
}
.selects{
  margin-top: 30px;
  .selected-ops {
    display: flex;
    align-items: center;
    .open {
      font-size: 12px;
      margin-left: 10px;
      line-height: 24px;
      margin-bottom: 3px;
    }
    .selected-op {
      border: 1px solid #eee;
      border-radius: 3px;
      font-size: 12px;
      margin-left: 10px;
      line-height: 16px;
      background: #fff;
      padding: 0px 5px 1px;
      span {
        color: red;
        cursor: pointer;
      }
      .close {
        margin-left: 8px;
        font-size: 16px;
        font-weight: 800;
      }
      &:hover{
        box-shadow: 1px 1px 2px 1px rgba(0, 0, 0, 0.1);
      }
    }
  }
}
.filter{
  .filter-list{
    padding: 5px 0;
    background: #fff;
    border-radius: 3px;
    box-shadow: 0 1px 3px 1px rgba(0, 0, 0, 0.2);
    .filter-box {
      display: flex;
      align-content: center;
      position: relative;
      line-height: 24px;
      .f-key {
        font-size: 12px;
        color: #666;
        width: 10%;
        text-align: center;
        margin: auto;
        line-height: 100%;
      }
      .column-divider{
        width: 2px;
        border-radius: 1px;
        box-shadow: 1px 0 0 rgba(0,0,0,.2) inset,-1px 0 0 rgba(255,255,255,.2) inset;
      }
      .f-items {
        width: 85%;
        display: flex;
        flex-wrap: wrap;
        align-content: center;
        .f-item {
          width: 80px;
          line-height: 30px;
          font-size: 12px;
          display: flex;
          justify-content: center;
        }
      }
    }
    .row-divider{
      margin: auto;
      width: 98%;
      border-radius: 1px;
      height: 3px;
      box-shadow:0 1px 0 rgba(0,0,0,.2) inset,0 -1px 0 rgba(255,255,255,.2) inset;
    }
  }
}
.hotels{
  margin-top: 30px;
  .top-ban{
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    .sort-items{
      .sort-item {
        display: inline;
        width: 50px;
        float: left;
        font-size: 13px;
        a{
          margin-right: 3px;
        }
      }
    }
    .top-pagination{
      padding: 3px 15px;
      font-size: 11px;
      font-weight: 700;
      line-height: 18px;
      color: #999;
      text-shadow: 0 1px 0 rgba(255, 255, 255, .5);
      text-transform: uppercase;
      span {
        margin-right: 10px;
      }
      .btn-arrow{
        border-radius: 3px;
        width: 46px;
        height: 23px;
        border: 1px solid #DDD;
        background: #FFF;
        line-height: 23px;
        font-family: "\5b8b\4f53";
        text-align: center;
        font-size: 16px;
        color: #AAA;
        text-decoration: none;
        out-line: none;
        &:hover{
          background-color: #FF8800;
          color: whitesmoke;
        }
      }
    }
  }
  .row-divider{
    margin: auto;
    width: 98%;
    border-radius: 1px;
    height: 3px;
    box-shadow:0 1px 0 rgba(0,0,0,.2) inset,0 -1px 0 rgba(255,255,255,.2) inset;
  }
  .hotel-list {
    display: flex;
    flex-direction: column;
    width: 640px;
    height: 100%;
    .hotel-box {
      padding: 20px;
      margin-bottom: 10px;
      position: relative;
      background: #fff;
      border-radius: 5px;
      box-shadow: 0 1px 3px 1px rgba(0, 0, 0, 0.2);
      .ad-mark {
        position: absolute;
        left: 5px;
        top: 0;
        img{
          filter: drop-shadow(1px 2px 1px rgba(0,0,0,.3))
        }
      }
      .picture{
        img{
          width: 100%;
          height: 100%;
        }
      }
      .hotel-info{
        text-align: left;
        width: 50%;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        .hotel-name {
          font-size: 18px;
          color: #333;
          font-weight: bold;
          font-family: "Helvetica Neue", "Arial", "PingFang SC", "Microsoft Yahei", "SimSun", sans-serif;
          line-height: 26px;
        }
        .star-name {
          width: 50px;
          text-align: center;
          font: 12px/1.5 tahoma, arial, 'pingfang sc', 'Hiragino Sans GB', \5b8b\4f53, sans-serif;
          background-color: #fe7a6b;
          color: #fff;
          margin-bottom: 10px;
          border-radius: 3px;
          padding: 2px 5px;
          zoom: 1;
        }
        .order {
          color: #2bba9e; cursor: default; font: 12px/1.5 tahoma,arial,'pingfang sc','Hiragino Sans GB',\5b8b\4f53,sans-serif;
        }
        .address {
          margin-bottom: 10px;font: 12px/1.5 tahoma,arial,'pingfang sc','Hiragino Sans GB',\5b8b\4f53,sans-serif;
        }
      }
      #hotel-price {
        font-weight: bold;
        font-size: 24px;
        color: #f60;
        padding-right: 2px;
        font-family: 'Helvetica Neue', 'Arial', 'PingFang SC', 'Microsoft Yahei', 'SimSun', sans-serif;
      }
      .btn {
        height: 34px;
        line-height: 34px;
        padding: 0 12px;
        font-size: 16px;
        font-family: "Arial", "PingFang SC", "Microsoft Yahei", "SimSun", sans-serif;
        color: #FFF;
        background: #F80;
        border-color: #F80;
        vertical-align: top;
        text-align: center;
        display: inline-block;
        box-sizing: content-box;
        cursor: pointer;
        border-radius: 3px;
      }
      .hotel-score {
        font-size: 14px;
        color: #365873;
        font-weight: 700;
      }
    }
    .map-box {
      background-color: #fff;
      color: #666;
      width: 360px;
      height: 400px;
      padding: 5px;
      border-radius: 5px;
      box-shadow: 0 1px 3px 1px rgba(0, 0, 0, 0.2);
      .map-head {
        line-height: 35px;
      }
      .amap {
        width: 100%;
        height: 350px;
      }
      .map-geo{
        position: relative;
        bottom: 35px;
        left: 310px;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        cursor: pointer;
        background-image: url(https://a.amap.com/jsapi/static/image/plugin/locate.png);
        background-size: 24px;
        background-repeat: no-repeat;
        background-position: 50%;
        background-color: #fff;
        box-shadow: 0 0 5px silver;
        img {
          height: 24px;
          width: 24px;
          background-color: #fff;
          margin: 4px;
          border-radius: 50%;
          -webkit-animation: rotate 2s linear infinite;
        }
      }

    }
  }
  .map-box{
    background-color: #fff;
    color: #666;
    width: 500px;
    height: 400px;
    padding: 5px;
    border-radius: 5px;
    box-shadow: 0 1px 3px 1px rgba(0, 0, 0, 0.2);
    .map-head{
      line-height: 35px;
    }
    .amap{
      width: 100%;
      height: 350px;
    }
    .map-geo{
      position: relative;
      bottom: 35px;
      left: 450px;
      width: 32px;
      height: 32px;
      border-radius: 50%;
      cursor: pointer;
      background-image: url(https://a.amap.com/jsapi/static/image/plugin/locate.png);
      background-size: 24px;
      background-repeat: no-repeat;
      background-position: 50%;
      background-color: #fff;
      box-shadow: 0 0 5px silver;
      img{
        height: 24px;
        width: 24px;
        background-color: #fff;
        margin: 4px;
        border-radius: 50%;
        -webkit-animation: rotate 2s linear infinite;
      }
    }
  }
}
</style>