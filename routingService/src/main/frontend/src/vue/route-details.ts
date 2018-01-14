import Vue from 'vue'
import {Component} from "vue-typed";
import {Detail, Events, GasStrategy, Route} from "../app/DomainTypes";
import {CsvProcessor} from "../app/CsvProcessor";

@Component({
    template: require('./route-details.html')
})
export class RouteDetails extends Vue {
    routes: RouteDetail[] = [];

    get count(): number {
        return this.routes.length;
    }

    addRoute(strategy: GasStrategy, layer: ol.layer.Vector) {
        this.routes.push(new RouteDetail(strategy, layer, this.remove));
    }

    get showRoutes(): boolean {
        return this.routes.length > 0;
    }

    remove(detail: Detail<GasStrategy>) {
        for (let i = 0; i < this.routes.length; i++) {
            if (this.routes[i] === detail) {
                this.routes.splice(i, 1);
                this.$emit(Events.RouteDetailsRemoved, detail);
                return;
            }
        }
    }
}

class RouteDetail extends Detail<GasStrategy>{
    constructor(strategy: GasStrategy, layer: ol.layer.Vector, removeHandler: (strategy: Detail<GasStrategy>) => void) {
        super(strategy, layer, removeHandler);
    }

    toCsv(): string {
        return this.createDataUri(CsvProcessor.strategyToCsv(this.data));
    }

    toName(): string {
        return `${this.data.name.split('.')[0]}_strategy.csv`;
    }
}
