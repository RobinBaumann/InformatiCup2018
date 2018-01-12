import Vue from 'vue'
import {Component} from "vue-typed";
import {GasStrategy} from "../app/DomainTypes";
import {CsvProcessor} from "../app/CsvProcessor";

@Component({
    template: require('./route-details.html')
})
export class RouteDetails extends Vue {
    routes: RouteDetail[] = [];

    get count(): number {
        return this.routes.length
    }

    addRoute(strategy: GasStrategy, layer: ol.layer.Vector) {
        this.routes.push(new RouteDetail(strategy, layer))
    }

    get showRoutes(): boolean {
        return this.routes.length > 0
    }

}

class RouteDetail {
    strategy: GasStrategy;
    layer: ol.layer.Vector;

    constructor(strategy: GasStrategy, layer: ol.layer.Vector) {
        this.strategy = strategy;
        this.layer = layer;
    }

    get csv(): string {
        return `data:text/plain;charset=utf8,${encodeURI(CsvProcessor.toCsv(this.strategy))}`
    }
}
