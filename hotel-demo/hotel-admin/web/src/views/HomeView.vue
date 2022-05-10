<template>
    <div class="home">
        <div class="title">
            <span>酒店数据管理</span>
        </div>
        <div class="operation">
            <a-button type="primary" @click="handleAdd">新增酒店</a-button>
        </div>
        <div class="content">
            <a-table bordered
                     :data-source="dataSource"
                     :columns="columns"
                     :pagination="pagination"
                     @change="handleTableChange">
                <template #bodyCell="{ column, text, record }">
                    <template v-if="column.dataIndex">
                        <div class="editable-cell">
                            {{ text }}
                        </div>
                    </template>
                    <template v-if="column.dataIndex === 'operation'">
                        <a @click="handleEdit(record)">Edit</a>
                        <a style="margin-left: 20px" @click="handleDelete(record.id)">Delete</a>
                    </template>
                </template>
            </a-table>
            <a-modal v-model:visible="visible" title="酒店信息" @ok="handleOK(hotel)" @cancel="handleCancel">
                <a-input v-model:value="hotel.name" placeholder="酒店名称" />
                <a-input v-model:value="hotel.address" placeholder="酒店地址" />
                <a-input v-model:value="hotel.price" placeholder="酒店价格" />
                <a-input v-model:value="hotel.score" placeholder="酒店评分" />
                <a-input v-model:value="hotel.brand" placeholder="酒店品牌" />
                <a-input v-model:value="hotel.city" placeholder="所在城市" />
                <a-input v-model:value="hotel.business" placeholder="所在商圈" />
                <a-input v-model:value="hotel.pic" placeholder="酒店图片" />
                <a-input v-model:value="hotel.latitude" placeholder="酒店纬度" />
                <a-input v-model:value="hotel.longitude" placeholder="酒店经度" />
            </a-modal>
        </div>
    </div>
</template>

<script>
    import {onMounted, reactive, ref, computed} from 'vue';
    import { getHotelsByConditionsAjax, updateHotelByIdAjax,addHotelByIdAjax, deleteHotelByIdAjax } from "@/api/hotel";
    import { cloneDeep } from 'lodash-es';
    import { message } from 'ant-design-vue';

    export default {
        name: 'HomeView',
        setup() {
            const columns = [{
                title: 'ID',
                dataIndex: 'id',
                width: '100px',
            }, {
                title: '酒店名称',
                dataIndex: 'name',
            }, {
                title: '酒店品牌',
                dataIndex: 'brand',
            }, {
                title: '酒店价格',
                dataIndex: 'price',
                width: '150px'
            }, {
                title: '所在商圈',
                dataIndex: 'address',
            }, {
                title: '操作',
                dataIndex: 'operation',
                width: '150px'
            }];
            const dataSource = ref([]);
            const params = reactive({
                page: 1,
                size: 10,
                total: 1
            })
            const visible = ref(false);
            const hotel = ref({});

            const getHotels = () => {
                getHotelsByConditionsAjax(params).then(res=>{
                    if(res.data!=null){
                        dataSource.value = res.data.data;
                        params.total = res.data.total;
                    }
                    console.log(res);
                })
            }

            const pagination = computed(() => ({
                total: params.total,
                current: params.page,
                pageSize: params.size
            }));

            onMounted(()=>{
                getHotels();
            })

            const handleTableChange = (page) =>{
                params.page = page.current;
                params.size= page.pageSize;
                getHotels();
            }

            const handleEdit = (val) => {
                console.log(val)
                hotel.value = cloneDeep(val);
                visible.value = true;
            }

            const handleAdd = () => {
                visible.value = true;
            }

            const handleOK = (val) => {
                console.log(val);
                visible.value = false;
                if(val.id){
                    console.log("修改")
                    updateHotelByIdAjax(val).then(res => {
                        console.log(res);
                        message.success(res.msg);
                        hotel.value = {};
                        getHotels();
                    })
                }else{
                    console.log("新增")
                    addHotelByIdAjax(val).then(res=>{
                        console.log(res);
                        message.success(res.msg);
                        hotel.value = {};
                        getHotels();
                    })
                }
            }
            const handleCancel = () => {
                hotel.value = {};
            }
            const handleDelete = (id) => {
                deleteHotelByIdAjax(id).then(res => {
                    console.log(res);
                    message.success(res.msg);
                    getHotels();
                })
            }

            return {
                hotel,
                visible,
                columns,
                dataSource,
                pagination,
                handleTableChange,
                handleEdit,
                handleOK,
                handleCancel,
                handleAdd,
                handleDelete
            };
        }
    }
</script>
<Style lang="scss">
  .home {
    margin-top: 40px;

    .title {
      font-size: 20px;
      display: flex;

      span {
        font-weight: bold;
        margin: 0 auto;
      }
    }

    .operation {
      margin-top: 30px;
    }

    .content {
      margin-top: 10px;
    }
  }
</Style>
