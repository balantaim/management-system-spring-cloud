import {TestBed} from '@angular/core/testing';
import {DemosController} from './demos.controller';

describe('DemoController', () => {
    let service: DemosController;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(DemosController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
