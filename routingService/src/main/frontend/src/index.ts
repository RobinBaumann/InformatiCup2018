import Vue from 'vue'
import {Component} from 'vue-typed'
import {Map} from './vue/map'
import 'material-design-lite/dist/material.amber-blue.min.css'
import './css/app.css'

Vue.component('my-map', Map);

@Component({
    template: `
        <div>
            <my-map></my-map>
        </div>
    `
})

class App extends Vue {
}

new App().$mount('#app');