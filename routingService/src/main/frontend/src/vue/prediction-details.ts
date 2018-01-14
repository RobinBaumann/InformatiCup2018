import Vue from 'vue';
import {Component} from "vue-typed";
import {Detail, Events, PricePredictions} from "../app/DomainTypes";
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
        this.predictions.push(new PredictionDetail(prediction, layer, this.remove));
    }

    get showPredictions(): boolean {
        return this.predictions.length > 0;
    }

    remove(predictions: Detail<PricePredictions>) {
        for (let i = 0; i < this.predictions.length; i++) {
            if (this.predictions[i] === predictions) {
                this.predictions.splice(i, 1);
                this.$emit(Events.PredictionDetailsRemoved, predictions)
                return;
            }
        }
    }
}

class PredictionDetail extends Detail<PricePredictions>{
    constructor(predictions: PricePredictions, layer: ol.layer.Vector, removeHandler: (predictions: Detail<PricePredictions>) => void) {
        super(predictions, layer, removeHandler);
    }

    toCsv(): string {
        return this.createDataUri(CsvProcessor.predictionsToCsv(this.data));
    }

    toName(): string {
        return `${this.data.name.split('.')[0]}_predicted.csv`;
    }
}