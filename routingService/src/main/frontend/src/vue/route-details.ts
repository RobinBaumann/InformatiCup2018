import Vue from 'vue'
import {Component, Prop} from "vue-typed";
import {GasStrategy, Route} from "../app/DomainTypes";

@Component({
    template: require('./route-details.html')
})
export class RouteDetails extends Vue {
    routes: RouteDetail[] = []

    get count(): number {
        return this.routes.length
    }

    addRoute(strategy: GasStrategy, layer: ol.layer.Vector) {
        this.routes.push(new RouteDetail(strategy, layer))
    }
}

class RouteDetail {
    strategy: GasStrategy;
    layer: ol.layer.Vector;

    constructor(strategy: GasStrategy, layer: ol.layer.Vector) {
        this.strategy = strategy;
        this.layer = layer;
    }
}
