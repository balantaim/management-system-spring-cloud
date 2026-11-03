import {Type} from '@angular/core';

export interface UiConfig {

}

export type UiSize = 'small' | 'large' | undefined;

/** Describes a component to load dynamically, plus its inputs */
export interface UiItem {
    id: string;
    component: Type<unknown>;
    inputs: Record<string, unknown>;
}
