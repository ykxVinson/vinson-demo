import axiso from "@/axiso";

export function getHotelsAjax () {
    return axiso({
        method: 'get',
        url: 'hotel/list'
    })
}

/**
 * 根据筛选条件查询酒店信息
 * @param 传入对象
 * @returns {AxiosPromise}
 */
export function getHotelsByConditionsAjax(data) {
    return axiso({
        method: 'post',
        url: 'hotel/list',
        data
    })
}

/**
 * 根据筛选条件查获取过滤条件的列表
 * @param 传入对象
 * @returns {AxiosPromise}
 */
export function getFiltersByConditionsAjax(data) {
    return axiso({
        method: 'post',
        url: 'hotel/filters',
        data
    })
}

export function getSuggestByConditionsAjax(key){
    return axiso({
        method: 'get',
        url: 'hotel/suggestion?key='+key
    })
}