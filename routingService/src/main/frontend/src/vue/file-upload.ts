import Vue from 'vue'
import {Component} from 'vue-typed'

@Component({
    template: require('./file-upload.html')
})

export class FileUpload extends Vue {
    openFileDialog() {
        (this.$refs.fileInput as HTMLElement).click();
    }
}