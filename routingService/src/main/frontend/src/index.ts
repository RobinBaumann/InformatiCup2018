import  Vue from 'vue'
import { Component } from 'vue-typed'
import { Counter } from './vue/counter'
import 'material-design-lite/dist/material.amber-blue.min.css'
import './css/app.css'

Vue.component('counter', Counter);

@Component({
    template: `
    <counter></counter>
    `
})

class App extends Vue {
}

new App().$mount('#app');