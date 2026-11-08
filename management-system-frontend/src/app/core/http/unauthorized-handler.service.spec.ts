import {TestBed} from '@angular/core/testing';
import {UnauthorizedHandlerService} from './unauthorized-handler.service';

describe('UnauthorizedHandlerService', () => {
    let service: UnauthorizedHandlerService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(UnauthorizedHandlerService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
