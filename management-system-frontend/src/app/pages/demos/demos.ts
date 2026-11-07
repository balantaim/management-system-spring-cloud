import {Component, inject} from '@angular/core';
import {DemosController} from "../../core/controllers/demos.controller";
import {withThumbnail} from "@lightgallery/angular/plugins/thumbnail";
import {withZoom} from "@lightgallery/angular/plugins/zoom";
import {DatePipe} from "@angular/common";
import {LgGalleryComponent} from "@lightgallery/angular";

@Component({
    imports: [DatePipe, LgGalleryComponent],
    providers: [DemosController],
    selector: 'app-demo',
    styleUrl: './demos.css',
    templateUrl: './demos.html',
})
export class Demos {
    protected readonly ctrl = inject(DemosController);
    protected readonly features = [withThumbnail({thumbWidth: 100}), withZoom()];
}
