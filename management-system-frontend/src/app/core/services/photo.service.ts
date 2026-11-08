import {Service, signal} from '@angular/core';
import {Photo} from "../models/photo";


export const PLACEHOLDER_IMAGE = '/images/placeholder.svg';

const DEMO_PHOTOS: Photo[] = [
    {
        id: 'alpine-dawn', title: 'Alpine Dawn', tag: 'Nature', date: '2025-03-14',
        src: '/user/alpine-dawn.jpeg', type: 'jpeg', alt: 'Layered mountains at sunrise'
    },
    {
        id: 'sunrise', title: 'Sunrise', tag: 'Nature', date: '2026-03-14',
        src: '/user/sunrise.webp', type: 'webp', alt: 'Sunrise'
    },
];

@Service()
export class PhotoService {
    /** Demo data in memory. Later: load from the backend. */
    readonly photos = signal<Photo[]>(DEMO_PHOTOS).asReadonly();
}
