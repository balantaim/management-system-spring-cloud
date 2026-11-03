import {TestBed} from '@angular/core/testing';
import {DemoController} from './demo.controller';

describe('DemoController', () => {
    let service: DemoController;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(DemoController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
