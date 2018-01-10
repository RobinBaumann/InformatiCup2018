import Vue from 'vue'
import {Component} from 'vue-typed'
import {Map} from './vue/map'
import 'material-design-lite/dist/material.amber-blue.min.css'
import 'material-design-lite/material.min.js'
import './css/app.css'
import 'vue-material-design-icons/styles.css'
import './icons.ts'
import {CsvModal} from "./vue/csvmodal";

Vue.component('my-map', Map);
Vue.component('modal', CsvModal);

@Component({
    template: `
        <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
            <div class="mdl-layout__content">
                <div class="page-content">
                    <my-map></my-map>
                   <modal></modal>
                </div>
            </div>
        </div>
    `
})

class App extends Vue {
}

new App().$mount('#app');