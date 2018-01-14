import Vue from 'vue';
import {Component} from "vue-typed";
import {Detail, PricePredictions} from "../app/DomainTypes";
import {CsvProcessor} from "../app/CsvProcessor";

@Component({
    template: require('./prediction-details.html')
})
export class PredictionDetails extends Vue {
    predictions: PredictionDetail[] = [];

    get count(): number {
        return this.predictions.length;
    }

    addPrediction(prediction: PricePredictions, layer: ol.layer.Vector) {
        this.predictions.push(new PredictionDetail(prediction, layer));
    }

    get showPredictions(): boolean {
        return this.predictions.length > 0;
    }
}

class PredictionDetail extends Detail<PricePredictions>{
    constructor(predictions: PricePredictions, layer: ol.layer.Vector) {
        super(predictions, layer);
    }

    toCsv(): string {
        return this.createDataUri(CsvProcessor.predictionsToCsv(this.data));
    }

    toName(): string {
        return `${this.data.name.split('.')[0]}_predicted.csv`;
    }
}