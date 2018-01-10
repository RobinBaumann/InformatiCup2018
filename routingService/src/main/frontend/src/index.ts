import Vue from 'vue'
import {Component} from 'vue-typed'
import {Map} from './vue/map'
import 'material-design-lite/dist/material.amber-blue.min.css'
import 'material-design-lite/material.min.js'
import './css/app.css'
import 'vue-material-design-icons/styles.css'
import './icons.ts'
import {FileUpload} from "./vue/file-upload";
import {Toastr} from "./vue/toastr";
import {AppError} from "./app/DomainTypes";

Vue.component('my-map', Map);
Vue.component('upload-button', FileUpload);
Vue.component('toastr', Toastr);

//TODO add top level tabs to switch from prediction to routing
@Component({
    template: `
        <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
            <div class="mdl-layout__content">
                <div class="page-content">
                    <my-map></my-map>
                    <upload-button class="floating-bottom-right" @error="showError"></upload-button>
                    <toastr :error="error"></toastr>
                </div>
            </div>
        </div>
    `
})

class App extends Vue {
    error?: AppError = undefined

    showError(error: AppError) {
        this.error = error
    }
}

new App().$mount('#app');