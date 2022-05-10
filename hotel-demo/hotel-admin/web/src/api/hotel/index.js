import axiso from "@/axiso";

export function getHotelsByConditionsAjax(data) {
    return axiso({
        method: 'get',
        url: 'hotel/list?page='+data.page+'&size='+data.size
    })
}

export function updateHotelByIdAjax(data) {
    return axiso({
        method: 'put',
        url: 'hotel',
        data: data
    })
}

export function addHotelByIdAjax(data) {
    return axiso({
        method: 'post',
        url: 'hotel',
        data: data
    })
}

export function deleteHotelByIdAjax(id) {
    return axiso({
        method: 'delete',
        url: 'hotel/'+id
    })
}