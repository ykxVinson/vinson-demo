import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

//import antComponents from './ant'

import Antd from 'ant-design-vue/es';
import 'ant-design-vue/dist/antd.css';


// import './style/index.css'

const app = createApp(App);

// antComponents.forEach(component => {
//     app.component(component.name, component);
// })

app.use(store);
app.use(router);
app.use(Antd);
app.mount('#app');
