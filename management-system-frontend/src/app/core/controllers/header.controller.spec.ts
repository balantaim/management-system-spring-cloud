import {TestBed} from '@angular/core/testing';
import {HeaderController} from './header.controller';

describe('HeaderController', () => {
    let service: HeaderController;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(HeaderController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
