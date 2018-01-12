import Vue from 'vue'
import {Component} from 'vue-typed'
import {CsvProcessor} from "../app/CsvProcessor";
import {AppError, GasStrategy, Problem, Route, Events} from "../app/DomainTypes";
import {Api} from '../app/Api'

@Component({
    template: require('./file-upload.html')
})

export class FileUpload extends Vue {
    openFileDialog() {
        const input = <HTMLInputElement>this.$refs.fileInput;
        input.value = '';
        input.click();
    }

    processFile(event:Event) {
        const element = <HTMLInputElement>event.target;
        if (!element.files || element.files.length !== 1) {
            this.$emit(Events.Error, new AppError('Must choose one csv for upload'));
            return;
        }
        const reader = new FileReader();
        const processor = new CsvProcessor(element.files[0].name)
        reader.onload = () => this.handleParseResult(processor.processCsv(reader.result));
        reader.readAsText(element.files[0])
    }

    handleParseResult(result: Route | AppError) {
        if (result instanceof AppError) {
            this.$emit(Events.Error, result)
        } else if (result instanceof Route) {
            Api.route(result)
                .then(response => this.$emit(
                    Events.StrategyReceived, new GasStrategy(response.data.stops, result.name, result.capacity)))
                .catch(reason => this.$emit(Events.Error, <Problem>reason.response.data))
        }
    }
}