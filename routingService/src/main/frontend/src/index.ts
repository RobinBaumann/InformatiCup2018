import Vue from 'vue'
import {Map} from './vue/map'
import 'material-design-lite/dist/material.amber-blue.min.css'
import 'material-design-lite/material.min.js'
import './css/app.css'
import 'vue-material-design-icons/styles.css'
import './icons.ts'
import {FileUpload} from "./vue/file-upload";
import {Toastr} from "./vue/toastr";
import {AppError} from "./app/DomainTypes";
import {CsvModal} from "./vue/csvmodal";
import {Component} from "vue-typed";

Vue.component('my-map', Map);
Vue.component('upload-button', FileUpload);
Vue.component('toastr', Toastr);
Vue.component('modal', CsvModal);


@Component()
class App extends Vue {
    error?: AppError = undefined

    showError(error: AppError) {
        this.error = error
    }
}

new App().$mount('#app');