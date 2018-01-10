import Vue from 'vue'
import {Component} from 'vue-typed'

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
        reader.onload = () => console.log(reader.result); //TODO handle file
        reader.readAsText(element.files[0])
    }
}