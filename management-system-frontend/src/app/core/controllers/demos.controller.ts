import {computed, inject, Service, signal} from '@angular/core';
import {PhotoService, PLACEHOLDER_IMAGE} from "../services/photo.service";
import {LgGalleryItem} from "@lightgallery/angular";
import {Photo} from "../models/photo";


export type SortKey = 'date' | 'name' | 'tag' | 'type';
export type SortDirection = 'asc' | 'desc';

interface SortOption {
    key: SortKey;
    label: string;
    icon: string;
    defaultDirection: SortDirection;
}

@Service()
export class DemosController {

    private readonly photoService = inject(PhotoService);

    readonly sortOptions: SortOption[] = [
        {key: 'date', label: 'Date', icon: 'pi pi-calendar', defaultDirection: 'desc'},
        {key: 'name', label: 'Name', icon: 'pi pi-sort-alpha-down', defaultDirection: 'asc'},
        {key: 'tag', label: 'Tag', icon: 'pi pi-tag', defaultDirection: 'asc'},
        {key: 'type', label: 'Type', icon: 'pi pi-file', defaultDirection: 'asc'},
    ];

    readonly sortKey = signal<SortKey>('date');
    readonly direction = signal<SortDirection>('desc');

    /** Ids of pictures whose grid image failed to load */
    private readonly failedIds = signal<ReadonlySet<string>>(new Set());

    readonly photos = computed(() => {
        const dir = this.direction() === 'asc' ? 1 : -1;
        return [...this.photoService.photos()].sort((a, b) => {
            switch (this.sortKey()) {
                case 'date':
                    return dir * a.date.localeCompare(b.date);
                case 'name':
                    return dir * a.title.localeCompare(b.title);
                case 'tag':
                    return dir * a.tag.localeCompare(b.tag) || a.title.localeCompare(b.title);
                case 'type':
                    return dir * a.type.localeCompare(b.type) || a.title.localeCompare(b.title);
                default:
                    return 0;
            }
        });
    });

    /** Same order as the grid, so the lightbox always follows the current sort */
    readonly slides = computed<LgGalleryItem[]>(() =>
        this.photos().map((photo) => {
            // Without a separate thumb, the grid image IS the full image, so its failure means the full image is broken too
            const fullImageBroken = this.failedIds().has(photo.id) && !photo.thumb;
            return {
                src: fullImageBroken ? PLACEHOLDER_IMAGE : photo.src,
                thumb: this.thumbOf(photo),
                alt: photo.alt,
                caption: `${photo.title} · ${photo.tag}`,
            };
        }),
    );

    readonly sortSummary = computed(() => {
        const asc = this.direction() === 'asc';
        switch (this.sortKey()) {
            case 'date':
                return asc ? 'Sorted by date, oldest first' : 'Sorted by date, newest first';
            case 'name':
                return asc ? 'Sorted by name, A to Z' : 'Sorted by name, Z to A';
            case 'tag':
                return asc ? 'Sorted by tag, A to Z' : 'Sorted by tag, Z to A';
            default:
                return asc ? 'Sorted by file type, A to Z' : 'Sorted by file type, Z to A';
        }
    });

    /** The image shown in the grid: the thumb if there is one, the default picture if loading failed */
    thumbOf(photo: Photo): string {
        return this.failedIds().has(photo.id) ? PLACEHOLDER_IMAGE : (photo.thumb ?? photo.src);
    }

    markFailed(id: string): void {
        this.failedIds.update((ids) => (ids.has(id) ? ids : new Set(ids).add(id)));
    }

    /** Clicking the active button flips the direction; another button selects it with its default */
    sortBy(key: SortKey): void {
        if (this.sortKey() === key) {
            this.direction.update((d) => (d === 'asc' ? 'desc' : 'asc'));
            return;
        }
        const option = this.sortOptions.find((o) => o.key === key);
        this.sortKey.set(key);
        this.direction.set(option?.defaultDirection ?? 'asc');
    }

}
