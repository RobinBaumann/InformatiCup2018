import Vue from 'vue'
import {Component} from 'vue-typed'
import {CsvProcessor} from "../app/CsvProcessor";
import {AppError, Route} from "../app/DomainTypes";
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
            this.$emit('error', new AppError('Must choose one csv for upload'));
            return;
        }
        const reader = new FileReader();
        reader.onload = () => this.handleParseResult(new CsvProcessor().processCsv(reader.result));
        reader.readAsText(element.files[0])
    }

    handleParseResult(result: Route | AppError) {
        if (result instanceof AppError) {
            this.$emit('error', result)
        } else if (result instanceof Route) {
            Api.route(result)
                .then(response => console.log(response))
                .catch(reason => console.log(reason.response))
        }
    }
}