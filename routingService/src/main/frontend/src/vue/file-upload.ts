import Vue from 'vue'
import {Component} from 'vue-typed'
import {CsvProcessor} from "../app/CsvProcessor";
import {
    AppError, GasStrategy, Problem, Route, Events, PricePredictionRequests,
    PricePredictions
} from "../app/DomainTypes";
import {Api} from '../app/Api'

@Component({
    template: require('./file-upload.html')
})

export class FileUpload extends Vue {
    private fileType?: FileType = undefined;

    openFileDialogRoute() {
        this.fileType = FileType.Route;
        this.triggerUpload()
    }

    openFileDialogPrice() {
        this.fileType = FileType.Price;
        this.triggerUpload()
    }

    private triggerUpload() {
        const input = <HTMLInputElement>this.$refs.fileInput;
        input.value = '';
        input.click();
    }

    private getFunction() {
        if (this.fileType === FileType.Route) {
            return (result: any, processor: CsvProcessor) =>
                this.handleRouteParseResult(processor.processRouteCsv(result));
        } else if (this.fileType === FileType.Price) {
            return (result: any, processor: CsvProcessor) =>
                this.handlePredictionParseResult(processor.processPriceCsv(result));
        }
        throw new Error("This should never happen ;)")
    }

    processFile(event: Event) {
        const element = <HTMLInputElement>event.target;
        if (!element.files || element.files.length !== 1) {
            this.$emit(Events.Error, new AppError('Must choose one csv for upload'));
            return;
        }
        const reader = new FileReader();
        const processor = new CsvProcessor(element.files[0].name);
        reader.onload = () => this.getFunction()(reader.result, processor);
        reader.readAsText(element.files[0])
    }

    private handleRouteParseResult(result: Route | AppError) {
        if (result instanceof AppError) {
            this.$emit(Events.Error, result)
        } else if (result instanceof Route) {
            Api.route(result)
                .then(response => this.$emit(
                    Events.StrategyReceived, new GasStrategy(response.data.stops, result.name, result.capacity)))
                .catch(this.apiErrorHandler)
        }
    }

    private handlePredictionParseResult(result: PricePredictionRequests | AppError) {
        if (result instanceof AppError) {
            this.$emit(Events.Error, result)
        } else if (result instanceof PricePredictionRequests) {
            Api.predictions(result)
                .then(response => this.$emit(
                    Events.PredictionsReceived, new PricePredictions(result.name, response.data.predictions)
                ))
                .catch(this.apiErrorHandler)
        }
    }

    private apiErrorHandler(reason: any) {
        if (reason.response && reason.response.data && reason.response.data.type && reason.response.data.status && reason.response.data.title) {
            this.$emit(Events.Error, new Problem(
                reason.response.data.type,
                reason.response.data.title,
                reason.response.data.detail,
                reason.response.data.status));
        } else {
            this.$emit(Events.Error, new Problem(
                "https://github.com/RobinBaumann/InformatiCup2018/InternalError",
                "An internal error occurred.",
                "An internal error occurred, we are fixing it asap.",
                500
            ));
        }
    }
}

enum FileType {
    Route = 'Route',
    Price = 'Price'
}