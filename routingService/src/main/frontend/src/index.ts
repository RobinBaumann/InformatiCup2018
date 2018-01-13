import Vue from 'vue'
import {Map} from './vue/map'
import 'material-design-icons/iconfont/material-icons.css'
import {FileUpload} from "./vue/file-upload";
import {Toastr} from "./vue/toastr";
import {DescribableError, GasStrategy} from "./app/DomainTypes";
import {Component} from "vue-typed";
import {toGeoJson} from "./app/GeoJsonConverter";
import 'vue-material/dist/vue-material.css'
import 'vue-material/dist/theme/default.css'
import './css/app.css'
import {RouteDetails} from "./vue/route-details";
import {
    MdApp,
    MdList,
    MdToolbar,
    MdButton,
    MdIcon,
    MdContent,
    MdSpeedDial,
    MdSnackbar,
    MdDrawer,
    MdTable,
    MdCard
} from 'vue-material/dist/components';
require('babel-core/register');
require('babel-polyfill');

Vue.use(MdApp);
Vue.use(MdList);
Vue.use(MdToolbar);
Vue.use(MdButton);
Vue.use(MdIcon);
Vue.use(MdContent);
Vue.use(MdSpeedDial);
Vue.use(MdSnackbar);
Vue.use(MdDrawer);
Vue.use(MdTable);
Vue.use(MdCard);

Vue.component('my-map', Map);
Vue.component('upload-button', FileUpload);
Vue.component('toastr', Toastr);
Vue.component('route-details', RouteDetails);

@Component({
    template: require('./index-template.html'),
})
class App extends Vue {
    error?: DescribableError = undefined; //TODO use refs for direct invocation
    menuVisible: boolean = false;

    showError(error: DescribableError) {
        this.error = error
    }

    strategyReceived(strategy: GasStrategy) {
        const geojson = toGeoJson(strategy);
        const layer = (<Map>this.$refs.map).addRoute(geojson);
        (<RouteDetails>this.$refs.routedetails).addRoute(strategy, layer)
    }

    toggleMenu() {
        this.menuVisible = !this.menuVisible
    }
}

new App().$mount('#app');