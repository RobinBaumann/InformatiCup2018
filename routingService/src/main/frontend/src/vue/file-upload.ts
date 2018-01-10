import Vue from 'vue'
import {Component} from 'vue-typed'
import {CsvProcessor} from "../app/CsvProcessor";
import {AppError, Route} from "../app/DomainTypes";

@Component({
    template: require('./file-upload.html')
})

export class FileUpload extends Vue {
    openFileDialog() {
        (this.$refs.fileInput as HTMLElement).click();
    }

    processFile(event:Event) {
        const element = <HTMLInputElement>event.target;
        if (!element.files || element.files.length !== 1) {
            //TODO handle error
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
            //TODO handle happy path
        }
    }
}