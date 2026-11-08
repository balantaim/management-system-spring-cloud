export type PhotoType = 'jpeg' | 'jpg' | 'png' | 'webp' | 'avif' | 'gif' | 'svg';

export interface Photo {
    id: string;
    title: string;
    tag: string;
    // ISO yyyy-mm-dd
    date: string;
    src: string;
    thumb?: string;
    type: PhotoType;
    alt: string;
}
